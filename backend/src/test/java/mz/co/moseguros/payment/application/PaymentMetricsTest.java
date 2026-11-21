package mz.co.moseguros.payment.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class PaymentMetricsTest {

    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    private final PaymentMetrics metrics = new PaymentMetrics(provider(registry));

    @AfterEach
    void clearSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void countsFinalStatesAndUnknownByMethodAndStatus() {
        metrics.transitioned(PaymentMethod.MPESA, PaymentStatus.SUCCEEDED);
        metrics.transitioned(PaymentMethod.MPESA, PaymentStatus.SUCCEEDED);
        metrics.transitioned(PaymentMethod.BANK_REFERENCE, PaymentStatus.CANCELLED);
        metrics.transitioned(PaymentMethod.MPESA, PaymentStatus.UNKNOWN);
        metrics.transitioned(PaymentMethod.MPESA, PaymentStatus.FAILED);
        metrics.transitioned(PaymentMethod.BANK_REFERENCE, PaymentStatus.EXPIRED);

        assertThat(count("MPESA", "SUCCEEDED")).isEqualTo(2.0);
        assertThat(count("BANK_REFERENCE", "CANCELLED")).isEqualTo(1.0);
        assertThat(count("MPESA", "UNKNOWN")).isEqualTo(1.0);
        assertThat(count("MPESA", "FAILED")).isEqualTo(1.0);
        assertThat(count("BANK_REFERENCE", "EXPIRED")).isEqualTo(1.0);
    }

    @Test
    void ignoresIntermediateAndRefundedStates() {
        metrics.transitioned(PaymentMethod.MPESA, PaymentStatus.PENDING);
        metrics.transitioned(PaymentMethod.MPESA, PaymentStatus.CREATED);
        metrics.transitioned(PaymentMethod.MPESA, PaymentStatus.REFUNDED);

        assertThat(registry.find(PaymentMetrics.INTENTS).counters()).isEmpty();
    }

    @Test
    void withATransactionCountsOnlyAfterCommit() {
        TransactionSynchronizationManager.initSynchronization();
        metrics.transitioned(PaymentMethod.BANK_REFERENCE, PaymentStatus.SUCCEEDED);
        assertThat(registry.find(PaymentMetrics.INTENTS).counters()).isEmpty();

        for (TransactionSynchronization s : TransactionSynchronizationManager.getSynchronizations()) {
            s.afterCommit();
        }
        assertThat(count("BANK_REFERENCE", "SUCCEEDED")).isEqualTo(1.0);
    }

    @Test
    void rolledBackTransitionIsNotCounted() {
        TransactionSynchronizationManager.initSynchronization();
        metrics.transitioned(PaymentMethod.MPESA, PaymentStatus.FAILED);
        for (TransactionSynchronization s : TransactionSynchronizationManager.getSynchronizations()) {
            s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        }
        assertThat(registry.find(PaymentMetrics.INTENTS).counters()).isEmpty();
    }

    private double count(String method, String status) {
        return registry.get(PaymentMetrics.INTENTS).tag("method", method).tag("status", status).counter().count();
    }

    private static ObjectProvider<MeterRegistry> provider(MeterRegistry registry) {
        StaticListableBeanFactory factory = new StaticListableBeanFactory();
        factory.addBean("registry", registry);
        return factory.getBeanProvider(MeterRegistry.class);
    }
}
