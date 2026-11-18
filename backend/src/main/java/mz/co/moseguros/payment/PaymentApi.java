package mz.co.moseguros.payment;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.payment.application.PaymentExceptionService;
import mz.co.moseguros.payment.application.PaymentIntentService;
import mz.co.moseguros.payment.application.PaymentQueryService;
import mz.co.moseguros.payment.application.PaymentSystemLookups;
import mz.co.moseguros.payment.application.gateway.GatewayRegistry;
import mz.co.moseguros.shared.domain.Money;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Pagamentos (S5-02 §1; modelo transversal §3). Os módulos donos dos alvos
 * (proposal no S5, policy no S7, integration no S12-02) chamam-na nos seus endpoints. O dinheiro vai
 * sempre directamente para a conta da seguradora: a plataforma e os mediadores nunca recebem fundos.
 *
 * <p>As consultas correm no âmbito de quem chama (RLS), salvo {@link #availableMethods} (configuração
 * lida em {@code SYSTEM}).
 */
@Service
public class PaymentApi {

    private final PaymentIntentService intents;
    private final PaymentQueryService queries;
    private final PaymentExceptionService exceptions;
    private final PaymentSystemLookups lookups;
    private final GatewayRegistry gateways;

    public PaymentApi(PaymentIntentService intents, PaymentQueryService queries, PaymentExceptionService exceptions,
                      PaymentSystemLookups lookups, GatewayRegistry gateways) {
        this.intents = intents;
        this.queries = queries;
        this.exceptions = exceptions;
        this.lookups = lookups;
        this.gateways = gateways;
    }

    /**
     * Cria (ou devolve, pela {@code Idempotency-Key}) uma intenção de pagamento (S5-02 §4 e §5).
     *
     * @throws mz.co.moseguros.shared.error.DomainException os códigos do S5-02 §4 e §13
     */
    public IntentView createIntent(CreateIntentCommand command) {
        return intents.create(command);
    }

    /** A intenção no âmbito de quem chama. @throws mz.co.moseguros.shared.error.DomainException 404 {@code PAYMENT_INTENT_NOT_FOUND} */
    public IntentView intent(UUID intentId) {
        return queries.intent(intentId);
    }

    /** Métodos activos da seguradora, com adaptador disponível e dentro dos limites, por {@code sortOrder}. */
    public List<PaymentMethodOption> availableMethods(UUID insurerId, Money amount) {
        return lookups.enabledConfigs(insurerId).stream()
                .filter(c -> gateways.isAvailable(c.method()))
                .filter(c -> (c.minAmount() == null || amount.compareTo(c.minAmount()) >= 0)
                        && (c.maxAmount() == null || amount.compareTo(c.maxAmount()) <= 0))
                .sorted(Comparator.comparingInt(PaymentSystemLookups.MethodConfig::sortOrder)
                        .thenComparing(c -> c.method().name()))
                .map(c -> new PaymentMethodOption(c.method(), c.displayName(), c.sortOrder()))
                .toList();
    }

    /** A intenção mais recente do alvo em {@code CREATED}, {@code PENDING} ou {@code UNKNOWN}. */
    public Optional<IntentView> activeIntent(PaymentTargetType targetType, UUID targetId) {
        return queries.activeIntent(targetType, targetId);
    }

    /** Há uma intenção M-Pesa em {@code CREATED}, {@code PENDING} ou {@code UNKNOWN} (o dinheiro pode estar a caminho). */
    public boolean hasBlockingIntent(PaymentTargetType targetType, UUID targetId) {
        return queries.hasBlockingIntent(targetType, targetId);
    }

    /** Há uma intenção {@code SUCCEEDED} do alvo. */
    public boolean isPaid(PaymentTargetType targetType, UUID targetId) {
        return queries.isPaid(targetType, targetId);
    }

    /**
     * O dono do alvo recebeu um pagamento que já não pode aceitar: cria {@code payment_exception}
     * {@code TARGET_NOT_PAYABLE} (uma por intenção). Corre em {@code SYSTEM} (listeners).
     *
     * @param detail código curto (ex.: {@code PROPOSAL_EXPIRED}, {@code PROPOSAL_ALREADY_PAID})
     */
    public void raiseTargetNotPayable(UUID intentId, String detail) {
        exceptions.raiseTargetNotPayable(intentId, detail);
    }
}
