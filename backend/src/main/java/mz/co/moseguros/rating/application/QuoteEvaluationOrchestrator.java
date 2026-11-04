package mz.co.moseguros.rating.application;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.rating.QuoteResult;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.application.RatingExecutorConfig.RatingExecutor;
import mz.co.moseguros.rating.application.provider.ProviderResult;
import mz.co.moseguros.rating.application.provider.RatingProvider;
import mz.co.moseguros.rating.application.provider.RatingRequest;
import mz.co.moseguros.rating.domain.RatingProviderKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;

/**
 * Fase 2 das cotações (S3-02 §4): avalia todas as versões em paralelo no {@code ratingExecutor},
 * sem transacção, sem {@code TenantContext} e sem acesso à BD. Espera por todas até ao prazo
 * global ({@code início + global-timeout}); as que não acabaram ficam {@code INSURER_TIMEOUT} e são
 * canceladas. Tarefa recusada pelo executor (fila cheia) → {@code INSURER_UNAVAILABLE}; excepção
 * inesperada num provider → {@code EVALUATION_ERROR} (log {@code ERROR} só com o {@code quoteId}).
 */
@Component
public class QuoteEvaluationOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(QuoteEvaluationOrchestrator.class);

    private final List<RatingProvider> providers;
    private final RatingExecutor executor;
    private final RatingProperties properties;

    public QuoteEvaluationOrchestrator(List<RatingProvider> providers, RatingExecutor executor,
                                       RatingProperties properties) {
        this.providers = List.copyOf(providers);
        this.executor = executor;
        this.properties = properties;
    }

    /**
     * Resultado da fase 2.
     *
     * @param results      um por pedido ({@code quoteId} → resultado), pela ordem dos pedidos
     * @param evaluationMs duração da fase
     */
    public record Evaluation(Map<UUID, ProviderResult> results, int evaluationMs) {

        public ProviderResult of(RatingRequest request) {
            return results.get(request.quoteId());
        }
    }

    public Evaluation evaluate(List<RatingRequest> requests) {
        long start = System.nanoTime();
        long deadline = start + properties.getGlobalTimeout().toNanos();
        Map<RatingRequest, Future<ProviderResult>> futures = new IdentityHashMap<>();
        Map<UUID, ProviderResult> results = new HashMap<>();
        for (RatingRequest request : requests) {
            RatingProvider provider = provider(request.insurer().ratingMode());
            try {
                futures.put(request, executor.submit(() -> provider.rate(request)));
            } catch (TaskRejectedException e) {
                log.warn("Quote {}: rating task rejected (executor queue full)", request.quoteId());
                results.put(request.quoteId(), notAvailable(request, UnavailableReason.INSURER_UNAVAILABLE, start));
            }
        }
        for (RatingRequest request : requests) {
            Future<ProviderResult> future = futures.get(request);
            if (future == null) {
                continue;
            }
            long remaining = deadline - System.nanoTime();
            try {
                results.put(request.quoteId(), future.get(Math.max(0, remaining), TimeUnit.NANOSECONDS));
            } catch (TimeoutException ex) {
                future.cancel(true);
                log.info("Quote {}: insurer {} did not answer within the global timeout", request.quoteId(),
                        request.insurer().insurerId());
                results.put(request.quoteId(), notAvailable(request, UnavailableReason.INSURER_TIMEOUT, start));
            } catch (ExecutionException ex) {
                log.error("Quote {}: unexpected error while rating", request.quoteId(), ex.getCause());
                results.put(request.quoteId(), notAvailable(request, UnavailableReason.EVALUATION_ERROR, start));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                future.cancel(true);
                results.put(request.quoteId(), notAvailable(request, UnavailableReason.EVALUATION_ERROR, start));
            }
        }
        Map<UUID, ProviderResult> ordered = new LinkedHashMap<>();
        for (RatingRequest request : requests) {
            ordered.put(request.quoteId(), results.get(request.quoteId()));
        }
        return new Evaluation(ordered, elapsedMs(start));
    }

    private RatingProvider provider(RatingMode mode) {
        RatingMode effective = mode == null ? RatingMode.INTERNAL : mode;
        return providers.stream().filter(p -> p.supports(effective)).findFirst()
                .orElseThrow(() -> new IllegalStateException("No rating provider for " + effective));
    }

    private static ProviderResult notAvailable(RatingRequest request, UnavailableReason reason, long start) {
        String provider = RatingProviderKind.of(request.insurer().ratingMode()).name();
        return new ProviderResult(QuoteResult.notAvailable(reason, Map.of(), provider), null, elapsedMs(start));
    }

    private static int elapsedMs(long startNanos) {
        return (int) Math.min(Integer.MAX_VALUE, (System.nanoTime() - startNanos) / 1_000_000);
    }
}
