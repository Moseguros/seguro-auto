package mz.co.moseguros.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.FileSourceHealth;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Alerta de fonte parada (S5-10 §5) com relógio fixo e {@code stale-after} de 15 min: mede-se a ligação ao SFTP
 * (última leitura com sucesso, ou a activação), não a chegada de ficheiros.
 */
class PaymentSourceHealthJobTest {

    private static final Instant NOW = Instant.parse("2027-01-15T10:00:00Z");

    private InsurerPaymentMethodRepository methods;
    private MeterRegistry registry;
    private PaymentSourceHealthJob job;
    private final UUID sourceId = UUID.randomUUID();
    private final UUID insurerId = UUID.randomUUID();

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        methods = mock(InsurerPaymentMethodRepository.class);
        registry = new SimpleMeterRegistry();
        ObjectProvider<MeterRegistry> meters = mock(ObjectProvider.class);
        when(meters.getIfAvailable()).thenReturn(registry);
        PaymentProperties properties = new PaymentProperties();
        properties.getFileSource().setStaleAfter(Duration.ofMinutes(15));
        job = new PaymentSourceHealthJob(methods, new PaymentAlerts(meters), properties,
                mock(PlatformTransactionManager.class), Clock.fixed(NOW, ZoneOffset.UTC));
        when(methods.markStale(any(), any())).thenReturn(1);
        when(methods.clearStale(any())).thenReturn(1);
    }

    private void sources(FileSourceHealth... rows) {
        when(methods.enabledSourcesHealth(PaymentMethod.BANK_REFERENCE)).thenReturn(List.of(rows));
    }

    private double alerts() {
        var counter = registry.find(PaymentAlerts.COUNTER).tag("code", "PAYMENT_SOURCE_STALE").counter();
        return counter == null ? 0 : counter.count();
    }

    @Test
    void noSuccessFor16MinutesMarksTheSourceStaleAndAlertsOnce() {
        sources(new FileSourceHealth(sourceId, insurerId, NOW.minus(Duration.ofDays(10)),
                NOW.minus(Duration.ofMinutes(16)), null));

        PaymentSourceHealthJob.Result first = job.check();

        assertThat(first.becameStale()).isEqualTo(1);
        verify(methods).markStale(sourceId, NOW);
        assertThat(alerts()).isEqualTo(1);

        // A execução seguinte já vê stale_since preenchido: não alerta outra vez.
        sources(new FileSourceHealth(sourceId, insurerId, NOW.minus(Duration.ofDays(10)),
                NOW.minus(Duration.ofMinutes(21)), NOW.minus(Duration.ofMinutes(5))));
        PaymentSourceHealthJob.Result second = job.check();

        assertThat(second.becameStale()).isZero();
        assertThat(alerts()).isEqualTo(1);
    }

    @Test
    void aSuccessfulPollAgainClearsStaleSince() {
        sources(new FileSourceHealth(sourceId, insurerId, NOW.minus(Duration.ofDays(10)),
                NOW.minus(Duration.ofMinutes(1)), NOW.minus(Duration.ofMinutes(30))));

        PaymentSourceHealthJob.Result result = job.check();

        assertThat(result.recovered()).isEqualTo(1);
        verify(methods).clearStale(sourceId);
        verify(methods, never()).markStale(any(), any());
        assertThat(alerts()).isZero();
    }

    @Test
    void neverPolledAndEnabled9MinutesAgoDoesNotAlert() {
        sources(new FileSourceHealth(sourceId, insurerId, NOW.minus(Duration.ofMinutes(9)), null, null));

        PaymentSourceHealthJob.Result result = job.check();

        assertThat(result.becameStale()).isZero();
        verify(methods, never()).markStale(any(), any());
        assertThat(alerts()).isZero();
    }

    @Test
    void neverPolledAndEnabled20MinutesAgoAlerts() {
        sources(new FileSourceHealth(sourceId, insurerId, NOW.minus(Duration.ofMinutes(20)), null, null));

        job.check();

        verify(methods).markStale(eq(sourceId), eq(NOW));
        assertThat(alerts()).isEqualTo(1);
    }

    @Test
    void okPollsWithoutFilesForThreeDaysDoNotAlert() {
        // A última leitura correu bem há 2 minutos; o último ficheiro chegou há 3 dias (não conta).
        sources(new FileSourceHealth(sourceId, insurerId, NOW.minus(Duration.ofDays(30)),
                NOW.minus(Duration.ofMinutes(2)), null));

        PaymentSourceHealthJob.Result result = job.check();

        assertThat(result.becameStale()).isZero();
        verify(methods, never()).markStale(any(), any());
        assertThat(alerts()).isZero();
    }
}
