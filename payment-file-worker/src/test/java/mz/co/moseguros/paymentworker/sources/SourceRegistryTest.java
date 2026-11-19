package mz.co.moseguros.paymentworker.sources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentcontracts.FileSourceList;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import mz.co.moseguros.paymentworker.processing.SourcePoller;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.ResourceAccessException;

/**
 * Agendamento das fontes (S5-09 §4): versão nova reagenda, fonte retirada cancela, backend em baixo mantém
 * os agendamentos, e cada fonte é lida de {@code poll-interval} em {@code poll-interval}.
 */
class SourceRegistryTest {

    static final Duration POLL = Duration.ofSeconds(60);

    final BackendSourceClient backend = mock(BackendSourceClient.class);
    final SourcePoller poller = mock(SourcePoller.class);
    final TaskScheduler scheduler = mock(TaskScheduler.class);
    final TaskScheduler refresh = mock(TaskScheduler.class);

    static WorkerProperties properties(Duration poll) {
        return new WorkerProperties("http://backend", "payment-file-worker", "s", Duration.ofMinutes(1), poll,
                Path.of("target"), DataSize.ofMegabytes(20), 50, Duration.ofMinutes(30), Duration.ofSeconds(0),
                Duration.ofSeconds(10), true);
    }

    static FileSourceDescriptor source(UUID id, long version) {
        return new FileSourceDescriptor(id, UUID.randomUUID(), version, true, "90002", "MEPS", null, null,
                new FileSourceDescriptor.Sftp("sftp", 22, "demo", "PASSWORD", "inbound", true, "processed", "error",
                        "*", null),
                Map.of("sftp.password", "secret/insurers/x/payments/BANK_REFERENCE/sftp.password"));
    }

    @SuppressWarnings("unchecked")
    ScheduledFuture<Object> scheduleReturns() {
        ScheduledFuture<Object> future = mock(ScheduledFuture.class);
        when(scheduler.scheduleWithFixedDelay(any(Runnable.class), any(Instant.class), eq(POLL)))
                .thenReturn((ScheduledFuture) future);
        return future;
    }

    @Test
    void newSourceIsScheduledEveryPollInterval() {
        SourceRegistry registry = new SourceRegistry(backend, poller, scheduler, refresh, properties(POLL),
                Clock.systemUTC());
        UUID id = UUID.randomUUID();
        when(backend.fetch()).thenReturn(new FileSourceList(List.of(source(id, 1))));
        scheduleReturns();

        assertThat(registry.refresh()).isTrue();

        verify(scheduler, times(1)).scheduleWithFixedDelay(any(Runnable.class), any(Instant.class), eq(POLL));
        assertThat(registry.size()).isEqualTo(1);

        // a mesma versão: nada muda
        registry.refresh();
        verify(scheduler, times(1)).scheduleWithFixedDelay(any(Runnable.class), any(Instant.class), eq(POLL));
    }

    @Test
    void newVersionReschedules() {
        SourceRegistry registry = new SourceRegistry(backend, poller, scheduler, refresh, properties(POLL),
                Clock.systemUTC());
        UUID id = UUID.randomUUID();
        ScheduledFuture<Object> first = scheduleReturns();
        when(backend.fetch()).thenReturn(new FileSourceList(List.of(source(id, 1))));
        registry.refresh();

        when(backend.fetch()).thenReturn(new FileSourceList(List.of(source(id, 2))));
        registry.refresh();

        verify(first).cancel(false);
        verify(scheduler, times(2)).scheduleWithFixedDelay(any(Runnable.class), any(Instant.class), eq(POLL));
        assertThat(registry.descriptor(id)).map(FileSourceDescriptor::version).contains(2L);
    }

    @Test
    void removedSourceIsCancelled() {
        SourceRegistry registry = new SourceRegistry(backend, poller, scheduler, refresh, properties(POLL),
                Clock.systemUTC());
        UUID id = UUID.randomUUID();
        ScheduledFuture<Object> future = scheduleReturns();
        when(backend.fetch()).thenReturn(new FileSourceList(List.of(source(id, 1))));
        registry.refresh();

        when(backend.fetch()).thenReturn(new FileSourceList(List.of()));
        registry.refresh();

        verify(future).cancel(false);
        assertThat(registry.size()).isZero();
    }

    @Test
    void backendDownKeepsTheSchedules() {
        SourceRegistry registry = new SourceRegistry(backend, poller, scheduler, refresh, properties(POLL),
                Clock.systemUTC());
        UUID id = UUID.randomUUID();
        ScheduledFuture<Object> future = scheduleReturns();
        when(backend.fetch()).thenReturn(new FileSourceList(List.of(source(id, 1))));
        registry.refresh();

        when(backend.fetch()).thenThrow(new ResourceAccessException("connection refused"));
        assertThat(registry.refresh()).isFalse();

        verify(future, never()).cancel(any(Boolean.class));
        assertThat(registry.size()).isEqualTo(1);
        assertThat(registry.health().getStatus()).isEqualTo(Status.UP);
    }

    @Test
    void healthIsDownWhenTheBackendHasNotAnsweredForFifteenMinutes() {
        Instant start = Instant.parse("2026-11-18T08:00:00Z");
        Clock[] clock = {Clock.fixed(start, java.time.ZoneOffset.UTC)};
        Clock moving = new Clock() {
            @Override
            public java.time.ZoneId getZone() {
                return java.time.ZoneOffset.UTC;
            }

            @Override
            public Clock withZone(java.time.ZoneId zone) {
                return this;
            }

            @Override
            public Instant instant() {
                return clock[0].instant();
            }
        };
        SourceRegistry registry = new SourceRegistry(backend, poller, scheduler, refresh, properties(POLL), moving);
        when(backend.fetch()).thenReturn(new FileSourceList(List.of()));
        registry.refresh();
        clock[0] = Clock.fixed(start.plus(Duration.ofMinutes(14)), java.time.ZoneOffset.UTC);
        assertThat(registry.health().getStatus()).isEqualTo(Status.UP);
        clock[0] = Clock.fixed(start.plus(Duration.ofMinutes(16)), java.time.ZoneOffset.UTC);
        assertThat(registry.health().getStatus()).isEqualTo(Status.DOWN);
    }

    @Test
    void eachSourceIsReadPollIntervalAfterTheEndOfThePreviousRead() {
        Duration poll = Duration.ofMillis(300);
        ThreadPoolTaskScheduler real = new ThreadPoolTaskScheduler();
        real.setPoolSize(4);
        real.initialize();
        try {
            List<Instant> starts = new CopyOnWriteArrayList<>();
            List<Instant> ends = new CopyOnWriteArrayList<>();
            UUID id = UUID.randomUUID();
            when(poller.pollWithLock(any())).thenAnswer(inv -> {
                starts.add(Instant.now());
                Thread.sleep(100);
                ends.add(Instant.now());
                return Optional.empty();
            });
            when(backend.fetch()).thenReturn(new FileSourceList(List.of(source(id, 1))));
            SourceRegistry registry = new SourceRegistry(backend, poller, real, refresh, properties(poll),
                    Clock.systemUTC());
            Instant scheduledAt = Instant.now();
            registry.refresh();

            await().atMost(Duration.ofSeconds(10)).until(() -> starts.size() >= 4);
            registry.stop();

            // a primeira logo a seguir ao agendamento; as seguintes poll-interval depois do fim da anterior
            assertThat(Duration.between(scheduledAt, starts.get(0))).isLessThan(Duration.ofMillis(250));
            for (int i = 1; i < 4; i++) {
                Duration gap = Duration.between(ends.get(i - 1), starts.get(i));
                assertThat(gap).isGreaterThanOrEqualTo(poll.minusMillis(20)).isLessThan(poll.plusMillis(500));
            }
        } finally {
            real.shutdown();
        }
    }
}
