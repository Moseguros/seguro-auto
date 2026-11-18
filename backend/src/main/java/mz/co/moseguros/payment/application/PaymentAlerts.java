package mz.co.moseguros.payment.application;

import java.time.Clock;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Alertas dos pagamentos (S5-04 §5). {@code PAYMENT_CONFIG_ERROR} (credenciais, código de serviço ou
 * autorização recusados pelo fornecedor, ou configuração incompleta): grava
 * {@code last_error_code = PROVIDER_CONFIG_ERROR} e {@code last_error_at} na forma de pagamento (o ecrã
 * de configuração mostra-o, S5-12), regista um {@code WARN} estruturado e conta em
 * {@code moseguros.payment.alerts{code, method}}. O envio por e-mail ou outro canal fica para o S12-08.
 *
 * <p>Corre no âmbito de quem chama (jobs em {@code SYSTEM}), numa transacção própria: um erro aqui nunca
 * desfaz o resultado do pagamento.
 */
@Component
public class PaymentAlerts {

    public static final String PAYMENT_CONFIG_ERROR = "PAYMENT_CONFIG_ERROR";
    public static final String LAST_ERROR_CODE = "PROVIDER_CONFIG_ERROR";
    public static final String METRIC = "moseguros.payment.alerts";

    private static final Logger log = LoggerFactory.getLogger(PaymentAlerts.class);

    private final InsurerPaymentMethodRepository methods;
    private final MeterRegistry meters;
    private final TransactionTemplate tx;
    private final Clock clock;

    public PaymentAlerts(InsurerPaymentMethodRepository methods, MeterRegistry meters,
                         PlatformTransactionManager transactionManager, Clock clock) {
        this.methods = methods;
        this.meters = meters;
        this.tx = new TransactionTemplate(transactionManager);
        this.tx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.clock = clock;
    }

    /** @param providerCode código do fornecedor ({@code INS-2}…) ou o motivo local ({@code SECRET_MISSING}…) */
    public void configError(MethodConfig config, String providerCode) {
        log.warn("alert={} insurerId={} method={} code={}", PAYMENT_CONFIG_ERROR, config.insurerId(), config.method(),
                providerCode);
        Counter.builder(METRIC).tag("code", PAYMENT_CONFIG_ERROR).tag("method", config.method().name())
                .register(meters).increment();
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
