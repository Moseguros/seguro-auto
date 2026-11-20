package mz.co.moseguros.payment.application;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import io.micrometer.core.instrument.MeterRegistry;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Contador das intenções de pagamento por forma e estado final (S6-08 §1):
 * {@code moseguros.payment.intents{method, status}} (Prometheus {@code moseguros_payment_intents_total}).
 *
 * <p>O {@link IntentStateService} chama {@link #transitioned} depois de cada transição; só contam
 * {@code SUCCEEDED}, {@code FAILED}, {@code UNKNOWN}, {@code EXPIRED} e {@code CANCELLED}
 * ({@code REFUNDED} fica para quem implementar a devolução, S7). Com uma transacção activa o contador só
 * sobe depois do commit: uma transição desfeita não conta. As etiquetas nunca levam dados pessoais.
 */
@Component
public class PaymentMetrics {

    public static final String INTENTS = "moseguros.payment.intents";

    static final Set<PaymentStatus> COUNTED = EnumSet.of(PaymentStatus.SUCCEEDED, PaymentStatus.FAILED,
            PaymentStatus.UNKNOWN, PaymentStatus.EXPIRED, PaymentStatus.CANCELLED);

    private final ObjectProvider<MeterRegistry> meters;

    public PaymentMetrics(ObjectProvider<MeterRegistry> meters) {
        this.meters = meters;
    }

    /** Uma intenção passou a {@code status}; os estados fora de {@link #COUNTED} são ignorados. */
    public void transitioned(PaymentMethod method, PaymentStatus status) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(status, "status");
        if (!COUNTED.contains(status)) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    increment(method, status);
                }
            });
        } else {
            increment(method, status);
        }
    }

    private void increment(PaymentMethod method, PaymentStatus status) {
        MeterRegistry registry = meters.getIfAvailable();
        if (registry != null) {
            registry.counter(INTENTS, "method", method.name(), "status", status.name()).increment();
        }
    }
}
