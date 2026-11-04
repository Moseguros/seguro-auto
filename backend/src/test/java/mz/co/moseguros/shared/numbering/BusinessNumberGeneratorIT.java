package mz.co.moseguros.shared.numbering;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Numeração de negócio (S4-02 §1) com o relógio controlado: formato e dígitos por prefixo, mudança
 * de ano em Maputo, 20 transacções em paralelo e rollback sem números perdidos.
 *
 * <p>Usa um contexto próprio (propriedade {@code moseguros.test.context}), logo uma BD só sua: os
 * contadores começam do zero sem afectar os números das propostas dos outros testes.
 */
@SpringBootTest(properties = "moseguros.test.context=business-number")
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class BusinessNumberGeneratorIT {

    @Autowired
    BusinessNumberGenerator generator;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    MutableClock clock;

    @Autowired
    IdentityFixtures fixtures;

    @BeforeEach
    void resetCounters() {
        fixtures.owner().update("delete from shared.business_sequence");
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    void at(String instant) {
        clock.reset();
        clock.advance(Duration.between(Instant.now(), Instant.parse(instant)));
    }

    String next(String prefix) {
        return tx.execute(s -> generator.next(prefix));
    }

    @Test
    void firstAndSecondProposalNumbersOfTheYear() {
        at("2027-03-10T10:00:00Z");
        assertThat(next("PR")).isEqualTo("PR-2027-000001");
        assertThat(next("PR")).isEqualTo("PR-2027-000002");
    }

    @Test
    void paymentNumbersHaveSevenDigits() {
        at("2027-03-10T10:00:00Z");
        assertThat(next("PG")).isEqualTo("PG-2027-0000001");
        // outro prefixo não usa o contador de PR
        assertThat(next("PR")).isEqualTo("PR-2027-000001");
        // prefixo fora da tabela: 6 dígitos
        assertThat(next("SIMO")).isEqualTo("SIMO-2027-000001");
    }

    @Test
    void theYearIsTheOneInMaputo() {
        at("2027-12-31T22:30:00Z");
        assertThat(next("PR")).isEqualTo("PR-2028-000001");
        at("2027-12-31T21:59:00Z");
        assertThat(next("PR")).isEqualTo("PR-2027-000001");
    }

    @Test
    void twentyParallelTransactionsGetDistinctNumbers() throws Exception {
        at("2027-03-10T10:00:00Z");
        ExecutorService pool = Executors.newFixedThreadPool(20);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                futures.add(pool.submit(() -> {
                    start.await();
                    return tx.execute(s -> generator.next("PR"));
                }));
            }
            start.countDown();
            Set<String> numbers = new TreeSet<>();
            for (Future<String> f : futures) {
                numbers.add(f.get(30, TimeUnit.SECONDS));
            }
            Set<String> expected = new TreeSet<>();
            for (int i = 1; i <= 20; i++) {
                expected.add(String.format("PR-2027-%06d", i));
            }
            assertThat(numbers).isEqualTo(expected);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void aRolledBackTransactionDoesNotConsumeTheNumber() {
        at("2027-03-10T10:00:00Z");
        assertThat(next("PR")).isEqualTo("PR-2027-000001");
        String rolledBack = tx.execute(s -> {
            String n = generator.next("PR");
            s.setRollbackOnly();
            return n;
        });
        assertThat(rolledBack).isEqualTo("PR-2027-000002");
        assertThat(next("PR")).isEqualTo("PR-2027-000002");
    }

    @Test
    void requiresATransactionAndAValidPrefix() {
        assertThatThrownBy(() -> generator.next("PR")).isInstanceOf(IllegalTransactionStateException.class);
        assertThatThrownBy(() -> next("pr")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> next("P")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> next("PROPO")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> next(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void numbersAboveTheMaximumGrowWithoutCutting() {
        at("2027-03-10T10:00:00Z");
        fixtures.owner().update("insert into shared.business_sequence (prefix, year, last_value) values ('PR', 2027, 999999)");
        assertThat(next("PR")).isEqualTo("PR-2027-1000000");
        assertThat(BusinessNumberGenerator.format("EX", 2027, 123)).isEqualTo("EX-2027-000123");
        assertThat(BusinessNumberGenerator.format("CT", 2027, 1234)).isEqualTo("CT-2027-0001234");
    }
}
