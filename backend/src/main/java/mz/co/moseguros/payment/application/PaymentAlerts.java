package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Alertas operacionais dos pagamentos: uma linha {@code WARN} com {@code alert=<code>} e o contador
 * {@code moseguros.payment.alerts{code, method}} ({@code method} = {@code NONE} quando o alerta não é de
 * uma forma de pagamento). O envio por e-mail ou outro canal fica para o S12-08. As etiquetas nunca levam
 * dados pessoais nem segredos.
 *
 * <ul>
 *   <li>{@link #raise} — genérico (S5-10 §5: {@code PAYMENT_SOURCE_STALE}, {@code PAYMENT_DLQ_NOT_EMPTY},
 *       {@code PAYMENT_FILE_REJECTED}).</li>
 *   <li>{@link #configError} — {@code PAYMENT_CONFIG_ERROR} (S5-04 §5: credenciais, código de serviço ou
 *       autorização recusados pelo fornecedor, ou configuração incompleta): grava também
 *       {@code last_error_code = PROVIDER_CONFIG_ERROR} e {@code last_error_at} na forma de pagamento (o
 *       ecrã de configuração mostra-o, S5-12), numa transacção própria: um erro aqui nunca desfaz o
 *       resultado do pagamento.</li>
 * </ul>
 */
@Component
public class PaymentAlerts {

    public static final String COUNTER = "moseguros.payment.alerts";
    /** Igual a {@link #COUNTER} (nome usado pelo S5-04). */
    public static final String METRIC = COUNTER;
    public static final String PAYMENT_CONFIG_ERROR = "PAYMENT_CONFIG_ERROR";
    public static final String LAST_ERROR_CODE = "PROVIDER_CONFIG_ERROR";
    /** Etiqueta {@code method} da métrica quando o alerta não é de uma forma de pagamento. */
    static final String NO_METHOD = "NONE";

    private static final Logger log = LoggerFactory.getLogger(PaymentAlerts.class);

    private final InsurerPaymentMethodRepository methods;
    private final ObjectProvider<MeterRegistry> meters;
    private final TransactionTemplate tx;
    private final Clock clock;

    public PaymentAlerts(InsurerPaymentMethodRepository methods, ObjectProvider<MeterRegistry> meters,
                         PlatformTransactionManager transactionManager, Clock clock) {
        this.methods = methods;
        this.meters = meters;
        this.tx = new TransactionTemplate(transactionManager);
        this.tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.clock = clock;
    }

    /**
     * @param insurerId seguradora afectada, ou {@code null}
     * @param tags      etiquetas do log (ex.: {@code sourceId}, {@code queue}); só {@code method} vai também
     *                  para a métrica
     */
    public void raise(String code, UUID insurerId, Map<String, String> tags) {
        Objects.requireNonNull(code, "code");
        String extra = tags == null || tags.isEmpty() ? "" : " " + new TreeMap<>(tags).entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(" "));
        log.warn("Payment alert alert={} insurerId={}{}", code, insurerId, extra);
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            String method = tags == null ? null : tags.get("method");
            registry.counter(COUNTER, "code", code, "method", method == null ? NO_METHOD : method).increment();
        }
    }

    /** @param providerCode código do fornecedor ({@code INS-2}…) ou o motivo local ({@code SECRET_MISSING}…) */
    public void configError(MethodConfig config, String providerCode) {
        raise(PAYMENT_CONFIG_ERROR, config.insurerId(),
                Map.of("method", config.method().name(), "code", String.valueOf(providerCode)));
        try {
            tx.executeWithoutResult(status -> methods.recordLastError(config.id(), config.insurerId(),
                    LAST_ERROR_CODE, clock.instant()));
        } catch (RuntimeException e) {
            log.warn("Could not record the payment method error insurerId={} method={} ({})", config.insurerId(),
                    config.method(), e.getClass().getSimpleName());
        }
    }

    /** Primeiro {@code INS-0} depois de um erro: limpa {@code last_error_code}. */
    public void clearConfigError(MethodConfig config) {
        try {
            tx.executeWithoutResult(status -> methods.clearLastError(config.id(), config.insurerId()));
        } catch (RuntimeException e) {
            log.warn("Could not clear the payment method error insurerId={} method={} ({})", config.insurerId(),
                    config.method(), e.getClass().getSimpleName());
        }
    }
}
