package mz.co.moseguros.payment.application;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import mz.co.moseguros.payment.IntentView;
import mz.co.moseguros.payment.NextAction;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.ReferencePayment;
import mz.co.moseguros.payment.domain.ReferencePaymentRepository;
import org.springframework.stereotype.Component;

/**
 * Converte intenções em {@link IntentView} (S5-02 §1): seguradora pelo nome, referência e próxima
 * acção ({@code nextAction}). Corre na transacção de quem chama.
 */
@Component
public class IntentViews {

    /** Tempo para confirmar no telemóvel quando a configuração não o tem (schema M-Pesa: defeito 90 s). */
    static final int DEFAULT_C2B_TIMEOUT_SECONDS = 90;

    private final ReferencePaymentRepository references;
    private final PaymentSystemLookups lookups;

    public IntentViews(ReferencePaymentRepository references, PaymentSystemLookups lookups) {
        this.references = references;
        this.lookups = lookups;
    }

    public IntentView of(PaymentIntent intent) {
        return of(List.of(intent)).get(0);
    }

    public List<IntentView> of(Collection<PaymentIntent> intents) {
        if (intents.isEmpty()) {
            return List.of();
        }
        Set<UUID> referenceIntents = intents.stream().filter(i -> i.getMethod() == PaymentMethod.BANK_REFERENCE)
                .map(PaymentIntent::getId).collect(Collectors.toSet());
        Map<UUID, ReferencePayment> refs = referenceIntents.isEmpty() ? Map.of()
                : references.findByIntentIdIn(referenceIntents).stream()
                .collect(Collectors.toMap(ReferencePayment::getIntentId, r -> r));
        Map<UUID, String> names = lookups.insurerNames(intents.stream().map(PaymentIntent::getInsurerId)
                .collect(Collectors.toSet()));
        Map<UUID, Integer> timeouts = new HashMap<>();
        return intents.stream().map(i -> view(i, refs.get(i.getId()), names.get(i.getInsurerId()), timeouts))
                .toList();
    }

    private IntentView view(PaymentIntent i, ReferencePayment ref, String insurerName,
                            Map<UUID, Integer> timeouts) {
        return new IntentView(i.getId(), i.getReference(), i.getStatus(), i.getMethod(), i.getPurpose(),
                new IntentView.TargetRef(i.getTargetType(), i.getTargetId(), i.getTargetLabel()),
                new IntentView.InsurerRef(i.getInsurerId(), insurerName), i.getPayerUserId(), i.amount(),
                i.getPayerMsisdn() == null ? null : i.getPayerMsisdn().masked(), i.getFailureCode(),
                i.getExpiresAt(), i.getCreatedAt(), i.getSucceededAt(), nextAction(i, ref, timeouts));
    }

    private NextAction nextAction(PaymentIntent i, ReferencePayment ref, Map<UUID, Integer> timeouts) {
        PaymentStatus status = i.getStatus();
        if (i.getMethod() == PaymentMethod.MPESA) {
            if (status == PaymentStatus.CREATED || status == PaymentStatus.PENDING) {
                int seconds = timeouts.computeIfAbsent(i.getMethodConfigId(), this::c2bTimeoutSeconds);
                Instant start = i.getDispatchedAt() != null ? i.getDispatchedAt() : i.getCreatedAt();
                return NextAction.awaitConfirmation(start == null ? null : start.plusSeconds(seconds));
            }
        }
        if (status == PaymentStatus.UNKNOWN) {
            return NextAction.checkingStatus(i.getNextQueryAt());
        }
        if (i.getMethod() == PaymentMethod.BANK_REFERENCE && status == PaymentStatus.PENDING && ref != null) {
            return NextAction.payByReference(ref.getEntity(), ref.getReference(), ref.amount(), ref.getValidUntil());
        }
        return NextAction.none();
    }

    private int c2bTimeoutSeconds(UUID configId) {
        return lookups.configById(configId).map(MethodConfig::settings)
                .map(s -> s.path("c2bTimeoutSeconds"))
                .filter(n -> n.canConvertToInt() && n.asInt() > 0)
                .map(n -> n.asInt())
                .orElse(DEFAULT_C2B_TIMEOUT_SECONDS);
    }
}
