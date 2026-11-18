package mz.co.moseguros.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.MpesaMockContainer;
import mz.co.moseguros.payment.MpesaTestSupport;
import mz.co.moseguros.payment.MpesaTestSupport.Row;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Calendário das consultas de estado (S5-04 §4, propriedades de teste: 1 s, 2 s, 3 s, 4 s, depois a cada
 * 5 s, desistência aos 20 s): uma intenção {@code UNKNOWN} que o simulador não conhece (estado {@code N/A})
 * passa pelas quatro consultas, depois a cada {@code repeat-interval}; passado {@code give-up-after} abre
 * {@code UNRESOLVED_STATUS} e deixa de ser consultada. Nenhuma consulta gera um C2B.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentStatusQueryJobIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    PaymentStatusQueryJob job;

    PaymentFlowSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        MpesaMockContainer.start();
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        insurer = support.insurer("Seguradora Consultas");
        MpesaTestSupport.configure(insurer, MpesaMockContainer.settings(), MpesaMockContainer.secrets());
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        clock.reset();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    Map<String, Object> row(UUID id) {
        return support.intentRow(id);
    }

    static Instant instant(Object value) {
        return value == null ? null : ((Timestamp) value).toInstant();
    }

    /** Põe o relógio logo depois da próxima consulta da intenção. */
    void moveClockTo(Instant at) {
        Duration ahead = Duration.between(clock.instant(), at.plusMillis(5));
        if (!ahead.isNegative()) {
            clock.advance(ahead);
        }
    }

    @Test
    void unknownIntentIsQueriedOnTheScheduleThenGivesUpWithAnUnresolvedStatusException() {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        Instant since = clock.instant().truncatedTo(ChronoUnit.MILLIS);
        String thirdPartyReference = "NUNCAENVIADA";
        UUID intentId = MpesaTestSupport.insertIntent(support.owner(), insurer, c.id(), proposal, "841000999",
                new Row("UNKNOWN", since.minusSeconds(5), since.minusSeconds(5), thirdPartyReference, since,
                        since.plusSeconds(1)));
        long c2bBefore = MpesaMockContainer.c2bCount();

        // antes da hora, não consulta
        job.query();
        assertThat(row(intentId).get("query_attempts")).isEqualTo(0);

        List<Instant> planned = new ArrayList<>();
        for (int guard = 0; guard < 20; guard++) {
            Instant next = instant(row(intentId).get("next_query_at"));
            if (next == null) {
                break;
            }
            planned.add(next);
            moveClockTo(next);
            job.query();
        }

        // 1 s, 2 s, 3 s, 4 s desde unknown_since; depois de 5 em 5 s; a última aos 20 s
        assertThat(planned).hasSizeGreaterThanOrEqualTo(7);
        assertThat(Duration.between(since, planned.get(0)).toMillis()).isEqualTo(1000);
        assertThat(Duration.between(since, planned.get(1)).toMillis()).isEqualTo(2000);
        assertThat(Duration.between(since, planned.get(2)).toMillis()).isEqualTo(3000);
        assertThat(Duration.between(since, planned.get(3)).toMillis()).isEqualTo(4000);
        for (int i = 4; i < planned.size() - 1; i++) {
            assertThat(Duration.between(planned.get(i - 1), planned.get(i)).toMillis())
                    .isCloseTo(5000L, within(300L));
        }
        assertThat(planned.get(planned.size() - 1)).isEqualTo(since.plusSeconds(20));

        Map<String, Object> after = row(intentId);
        assertThat(after).containsEntry("status", "UNKNOWN");
        assertThat(after.get("next_query_at")).isNull();
        assertThat(((Number) after.get("query_attempts")).intValue()).isEqualTo(planned.size());
        assertThat(support.owner().queryForList("""
                select type || ':' || status || ':' || amount from payment.payment_exception where intent_id = ?""",
                String.class, intentId)).containsExactly("UNRESOLVED_STATUS:OPEN:" + PaymentFlowSupport.AMOUNT);
        assertThat(MpesaTestSupport.events(support.owner(), intentId)).filteredOn("STATUS_QUERY"::equals)
                .hasSize(planned.size());
        assertThat(support.owner().queryForList("select raw_payload::text from payment.payment_event "
                + "where intent_id = ? and type = 'STATUS_QUERY'", String.class, intentId))
                .allSatisfy(p -> assertThat(p).contains("\"input_QueryReference\": \"" + thirdPartyReference + "\"")
                        .contains("N/A"));

        // deixou de consultar
        clock.advance(Duration.ofMinutes(30));
        job.query();
        assertThat(((Number) row(intentId).get("query_attempts")).intValue()).isEqualTo(planned.size());
        // nenhuma consulta gerou um C2B
        assertThat(MpesaMockContainer.c2bWith(thirdPartyReference)).isEmpty();
        assertThat(MpesaMockContainer.c2bCount()).isEqualTo(c2bBefore);
    }
}
