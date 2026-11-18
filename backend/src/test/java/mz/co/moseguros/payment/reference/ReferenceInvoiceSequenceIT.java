package mz.co.moseguros.payment.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.payment.PaymentTestSupport;
import mz.co.moseguros.payment.application.reference.GeneratedReference;
import mz.co.moseguros.payment.application.reference.ReferenceGenerator;
import mz.co.moseguros.payment.application.reference.ReferenceInvoiceSequence;
import mz.co.moseguros.payment.application.reference.ReferenceRequest;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Numeração das facturas por entidade (S5-07 §3): começa em {@code 0000001}, sequências independentes
 * por entidade, 20 gerações em paralelo, rollback sem buracos nem repetições e esgotamento → 503.
 *
 * <p>Cada teste usa uma entidade aleatória e apaga antes o contador dela (como dono).
 */
@SpringBootTest
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class ReferenceInvoiceSequenceIT {

    @Autowired
    ReferenceInvoiceSequence sequence;

    @Autowired
    ReferenceGenerator generator;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    IdentityFixtures fixtures;

    String freshEntity() {
        String entity = PaymentTestSupport.randomEntity();
        fixtures.owner().update("delete from payment.reference_invoice_sequence where entity = ?", entity);
        return entity;
    }

    int next(String entity) {
        Integer value = tx.execute(s -> sequence.next(entity));
        return value == null ? -1 : value;
    }

    GeneratedReference generate(String entity, long amount) {
        return tx.execute(s -> generator.generate(new ReferenceRequest(UUID.randomUUID(), UUID.randomUUID(), entity,
                Money.ofCentavos(amount), Instant.parse("2027-03-10T10:00:00Z"))));
    }

    Integer lastValue(String entity) {
        return fixtures.owner().queryForObject(
                "select last_value from payment.reference_invoice_sequence where entity = ?", Integer.class, entity);
    }

    @Test
    void theFirstReferenceOfAnEntityHasInvoice0000001AndTheNext0000002() {
        String entity = freshEntity();
        GeneratedReference first = generate(entity, 299_900);
        assertThat(first.invoiceNumber()).isEqualTo(1);
        assertThat(first.reference()).startsWith("000000103");
        assertThat(generator.isValid(entity, first.reference(), 299_900)).isTrue();
        GeneratedReference second = generate(entity, 299_900);
        assertThat(second.invoiceNumber()).isEqualTo(2);
        assertThat(second.reference()).startsWith("000000203");
        assertThat(generator.isValid(entity, second.reference(), 299_900)).isTrue();
    }

    @Test
    void twoEntitiesHaveIndependentSequences() {
        String a = freshEntity();
        String b;
        do {
            b = freshEntity();
        } while (b.equals(a));
        assertThat(next(a)).isEqualTo(1);
        assertThat(next(a)).isEqualTo(2);
        assertThat(next(b)).isEqualTo(1);
        assertThat(next(a)).isEqualTo(3);
        assertThat(next(b)).isEqualTo(2);
    }

    @Test
    void twentyParallelGenerationsGetTwentyDistinctConsecutiveNumbers() throws Exception {
        String entity = freshEntity();
        ExecutorService pool = Executors.newFixedThreadPool(20);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<GeneratedReference>> futures = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                futures.add(pool.submit(() -> {
                    start.await();
                    return generate(entity, 150_000);
                }));
            }
            start.countDown();
            Set<Integer> numbers = new TreeSet<>();
            Set<String> references = new TreeSet<>();
            for (Future<GeneratedReference> f : futures) {
                GeneratedReference r = f.get(30, TimeUnit.SECONDS);
                numbers.add(r.invoiceNumber());
                references.add(r.reference());
            }
            Set<Integer> expected = new TreeSet<>();
            for (int i = 1; i <= 20; i++) {
                expected.add(i);
            }
            assertThat(numbers).isEqualTo(expected);
            assertThat(references).hasSize(20);
            assertThat(lastValue(entity)).isEqualTo(20);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void aRolledBackTransactionLeavesNoGapAndNeverRepeatsASavedNumber() {
        String entity = freshEntity();
        assertThat(next(entity)).isEqualTo(1);
        Integer rolledBack = tx.execute(s -> {
            int n = sequence.next(entity);
            s.setRollbackOnly();
            return n;
        });
        assertThat(rolledBack).isEqualTo(2);
        assertThat(lastValue(entity)).isEqualTo(1);
        // sem buraco (o 2 nunca foi mostrado) e sem repetir o 1, já gravado
        assertThat(next(entity)).isEqualTo(2);
        assertThat(next(entity)).isEqualTo(3);
    }

    @Test
    void anExhaustedEntityGives503AndAnAlert(CapturedOutput output) {
        String entity = freshEntity();
        fixtures.owner().update("insert into payment.reference_invoice_sequence (entity, last_value) values (?, ?)",
                entity, 9_999_998);
        assertThat(next(entity)).isEqualTo(9_999_999);
        assertThat(output).contains("alert=REFERENCE_SEQUENCE_NEAR_LIMIT entity=" + entity);

        assertThatThrownBy(() -> generate(entity, 100_000)).isInstanceOfSatisfying(DomainException.class, e -> {
            assertThat(e.getCode()).isEqualTo("REFERENCE_GENERATION_FAILED");
            assertThat(e.getHttpStatus()).isEqualTo(503);
        });
        assertThat(output).contains("alert=REFERENCE_SEQUENCE_EXHAUSTED entity=" + entity);
        assertThat(lastValue(entity)).isEqualTo(9_999_999);
    }

    @Test
    void requiresATransaction() {
        assertThatThrownBy(() -> sequence.next("12345")).isInstanceOf(IllegalTransactionStateException.class);
        assertThatThrownBy(() -> generator.generate(new ReferenceRequest(UUID.randomUUID(), UUID.randomUUID(), "12345",
                Money.ofCentavos(1), Instant.now()))).isInstanceOf(IllegalTransactionStateException.class);
        assertThatThrownBy(() -> next("1234")).isInstanceOf(IllegalArgumentException.class);
    }
}
