package mz.co.moseguros.payment.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import mz.co.moseguros.payment.infrastructure.PaymentExecutorConfig.MpesaC2bExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/** Executor cheio → {@code submit} devolve {@code false} e nada corre (o job de recuperação volta a tentar). */
class ExecutorAsyncDispatcherTest {

    @Test
    void aFullExecutorRejectsWithoutRunningAndAFreeOneRuns() throws Exception {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(1);
        pool.setMaxPoolSize(1);
        pool.setQueueCapacity(0);
        pool.initialize();
        MpesaC2bExecutor executor = new MpesaC2bExecutor(pool);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch ran = new CountDownLatch(2);
        List<UUID> dispatched = new CopyOnWriteArrayList<>();
        ExecutorAsyncDispatcher dispatcher = new ExecutorAsyncDispatcher(executor, id -> {
            dispatched.add(id);
            try {
                release.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            ran.countDown();
        });
        try {
            UUID busy = UUID.randomUUID();
            UUID rejected = UUID.randomUUID();
            assertThat(dispatcher.submit(busy)).isTrue();
            assertThat(dispatcher.submit(rejected)).isFalse();
            release.countDown();

            UUID later = UUID.randomUUID();
            // a thread fica livre: o pedido seguinte é aceite
            for (int i = 0; i < 50 && !dispatcher.submit(later); i++) {
                Thread.sleep(20);
            }
            assertThat(ran.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(dispatched).containsExactly(busy, later);
        } finally {
            executor.destroy();
        }
    }
}
