package mz.co.moseguros.rating.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.rating.QuoteResult;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.application.QuoteEvaluationOrchestrator.Evaluation;
import mz.co.moseguros.rating.application.RatingExecutorConfig.RatingExecutor;
import mz.co.moseguros.rating.application.provider.ProviderResult;
import mz.co.moseguros.rating.application.provider.RatingProvider;
import mz.co.moseguros.rating.application.provider.RatingRequest;
import mz.co.moseguros.rating.RatedDriver;
import mz.co.moseguros.rating.RatedVehicle;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

/**
 * Fase 2 das cotações (S3-02 §4): paralelo com prazo global, tarefas recusadas pelo executor,
 * excepções nos providers e o MDC copiado para as threads {@code rating-*}.
 */
class QuoteEvaluationOrchestratorTest {

    final List<RatingExecutor> executors = new ArrayList<>();

    @AfterEach
    void shutdown() {
        executors.forEach(RatingExecutor::destroy);
        MDC.clear();
    }

    RatingProperties properties(Duration globalTimeout, int core, int max, int queue) {
        RatingProperties p = new RatingProperties();
        p.setGlobalTimeout(globalTimeout);
        p.getExecutor().setCoreSize(core);
        p.getExecutor().setMaxSize(max);
        p.getExecutor().setQueueCapacity(queue);
        return p;
    }

    QuoteEvaluationOrchestrator orchestrator(RatingProperties p, RatingProvider... providers) {
        RatingExecutor executor = new RatingExecutorConfig().ratingExecutor(p);
        executors.add(executor);
        return new QuoteEvaluationOrchestrator(List.of(providers), executor, p);
    }

    static RatingRequest request(RatingMode mode) {
        ProductVersionView version = new ProductVersionView(Ids.newId(), Ids.newId(), "P", "Produto",
                ProductType.MTPL_ONLY, Ids.newId(), 1, "PUBLISHED", LocalDate.of(2027, 1, 1), null, List.of(),
                List.of(), List.of(), List.of(), null, null, null, null, 0);
        InsurerProfileView insurer = new InsurerProfileView(version.insurerId(), "Seguradora", "SEG", null,
                OrganizationStatus.ACTIVE, mode, null, null, List.of());
        RatedVehicle vehicle = new RatedVehicle(null, null, null, null, 2020, null, VehicleCategory.LIGHT_PASSENGER,
                VehicleUsage.PRIVATE, 5, null, Province.GAZA, null, false);
        return new RatingRequest(Ids.newId(), version, insurer, vehicle,
                new RatedDriver(null, LocalDate.of(1990, 1, 1), null, List.of()), List.of(),
                LocalDate.of(2027, 1, 15), LocalDate.of(2028, 1, 14), 365);
    }

    static ProviderResult offered() {
        return new ProviderResult(new QuoteResult(mz.co.moseguros.rating.QuoteStatus.OFFERED, null, null, List.of(),
                100L, List.of(), 0L, 100L, "INTERNAL", null),
                com.fasterxml.jackson.databind.node.JsonNodeFactory.instance.objectNode(), 1);
    }

    /** Provider de teste: devolve, espera ou falha conforme o {@code behaviour}. */
    record FakeProvider(RatingMode mode, java.util.function.Function<RatingRequest, ProviderResult> behaviour)
            implements RatingProvider {

        @Override
        public boolean supports(RatingMode m) {
            return m == mode;
        }

        @Override
        public ProviderResult rate(RatingRequest request) {
            return behaviour.apply(request);
        }
    }

    @Test
    void evaluatesAllInParallelAndKeepsTheRequestOrder() {
        RatingRequest a = request(RatingMode.INTERNAL);
        RatingRequest b = request(RatingMode.EXTERNAL);
        Evaluation e = orchestrator(properties(Duration.ofSeconds(3), 4, 4, 10),
                new FakeProvider(RatingMode.INTERNAL, r -> offered()),
                new FakeProvider(RatingMode.EXTERNAL, r -> new ProviderResult(
                        QuoteResult.notAvailable(UnavailableReason.INSURER_DECLINED, null, "EXTERNAL"), null, 2)))
                .evaluate(List.of(a, b));
        assertThat(e.results().keySet()).containsExactly(a.quoteId(), b.quoteId());
        assertThat(e.of(a).result().isOffered()).isTrue();
        assertThat(e.of(b).result().unavailableReason()).isEqualTo(UnavailableReason.INSURER_DECLINED);
        assertThat(e.evaluationMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void whatDoesNotFinishBeforeTheDeadlineTimesOutAndIsCancelled() throws Exception {
        CountDownLatch interrupted = new CountDownLatch(1);
        RatingRequest slow = request(RatingMode.EXTERNAL);
        RatingRequest fast = request(RatingMode.INTERNAL);
        long started = System.nanoTime();
        Evaluation e = orchestrator(properties(Duration.ofMillis(300), 4, 4, 10),
                new FakeProvider(RatingMode.INTERNAL, r -> offered()),
                new FakeProvider(RatingMode.EXTERNAL, r -> {
                    try {
                        Thread.sleep(10_000);
                    } catch (InterruptedException ex) {
                        interrupted.countDown();
                        Thread.currentThread().interrupt();
                    }
                    return offered();
                })).evaluate(List.of(slow, fast));
        long elapsedMs = (System.nanoTime() - started) / 1_000_000;
        assertThat(elapsedMs).isLessThan(2_000);
        assertThat(e.of(slow).result().unavailableReason()).isEqualTo(UnavailableReason.INSURER_TIMEOUT);
        assertThat(e.of(slow).result().provider()).isEqualTo("EXTERNAL");
        assertThat(e.of(fast).result().isOffered()).isTrue();
        assertThat(interrupted.await(2, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void aRejectedTaskIsInsurerUnavailable() {
        CountDownLatch release = new CountDownLatch(1);
        RatingProvider blocking = new FakeProvider(RatingMode.INTERNAL, r -> {
            try {
                release.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return offered();
        });
        RatingRequest first = request(RatingMode.INTERNAL);
        RatingRequest queued = request(RatingMode.INTERNAL);
        RatingRequest rejected = request(RatingMode.INTERNAL);
        QuoteEvaluationOrchestrator orchestrator = orchestrator(properties(Duration.ofMillis(500), 1, 1, 1), blocking);
        Thread releaser = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            release.countDown();
        });
        releaser.start();
        Evaluation e = orchestrator.evaluate(List.of(first, queued, rejected));
        assertThat(e.of(rejected).result().unavailableReason()).isEqualTo(UnavailableReason.INSURER_UNAVAILABLE);
        assertThat(e.of(first).result().isOffered()).isTrue();
    }

    @Test
    void anUnexpectedExceptionIsAnEvaluationError() {
        RatingRequest request = request(RatingMode.INTERNAL);
        Evaluation e = orchestrator(properties(Duration.ofSeconds(1), 2, 2, 10),
                new FakeProvider(RatingMode.INTERNAL, r -> {
                    throw new IllegalStateException("boom");
                })).evaluate(List.of(request));
        assertThat(e.of(request).result().unavailableReason()).isEqualTo(UnavailableReason.EVALUATION_ERROR);
        assertThat(e.of(request).breakdown()).isNull();
    }

    @Test
    void withoutRequestsThereIsNothingToWaitFor() {
        Evaluation e = orchestrator(properties(Duration.ofSeconds(1), 1, 1, 1)).evaluate(List.of());
        assertThat(e.results()).isEmpty();
    }

    @Test
    void theMdcOfTheCallerIsCopiedToTheRatingThread() {
        MDC.put("traceId", "abc123");
        List<String> seen = new java.util.concurrent.CopyOnWriteArrayList<>();
        RatingRequest request = request(RatingMode.INTERNAL);
        orchestrator(properties(Duration.ofSeconds(1), 1, 1, 1), new FakeProvider(RatingMode.INTERNAL, r -> {
            seen.add(MDC.get("traceId") + "@" + Thread.currentThread().getName());
            return offered();
        })).evaluate(List.of(request));
        assertThat(seen).hasSize(1);
        assertThat(seen.get(0)).startsWith("abc123@rating-");
    }

    @Test
    void unknownModeFallsBackToInternal() {
        RatingRequest request = request(null);
        Evaluation e = orchestrator(properties(Duration.ofSeconds(1), 1, 1, 1),
                new FakeProvider(RatingMode.INTERNAL, r -> offered())).evaluate(List.of(request));
        assertThat(e.of(request).result().isOffered()).isTrue();
    }
}
