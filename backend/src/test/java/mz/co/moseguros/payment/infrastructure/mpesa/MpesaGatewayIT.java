package mz.co.moseguros.payment.infrastructure.mpesa;

import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.payMpesa;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

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
import mz.co.moseguros.payment.application.MpesaC2bJob;
import mz.co.moseguros.payment.application.PaymentDispatchRecoveryJob;
import mz.co.moseguros.payment.application.PaymentStatusQueryJob;
import mz.co.moseguros.payment.application.PaymentSystemLookups;
import mz.co.moseguros.payment.application.gateway.RefundRequest;
import mz.co.moseguros.payment.application.gateway.RefundResult;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Adaptador M-Pesa contra o {@code mpesa-mock} (S5-04): os comportamentos por final de número pelo
 * endpoint {@code createProposalPaymentIntent}, exactamente um C2B por intenção, a consulta de estado das
 * {@code UNKNOWN}, credenciais e código de serviço errados, o compare-and-set em paralelo, a recuperação dos
 * envios perdidos, a reversão e a ausência de segredos e telemóveis completos nos registos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(OutputCaptureExtension.class)
class MpesaGatewayIT {

    static final Set<String> OPEN = Set.of("CREATED", "PENDING", "UNKNOWN");

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    MpesaC2bJob c2bJob;

    @Autowired
    PaymentStatusQueryJob statusJob;

    @Autowired
    PaymentDispatchRecoveryJob recoveryJob;

    @Autowired
    MpesaGateway gateway;

    @Autowired
    PaymentSystemLookups lookups;

    PaymentFlowSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() throws Exception {
        RestAssured.port = port;
        MpesaMockContainer.start();
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        insurer = support.insurer("Seguradora M-Pesa");
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

    Map<String, Object> row(UUID intentId) {
        return support.intentRow(intentId);
    }

    /** Espera o estado final, correndo as consultas de estado (os jobs não correm sozinhos nos testes). */
    Map<String, Object> awaitFinal(UUID intentId) {
        await().atMost(Duration.ofSeconds(40)).pollInterval(Duration.ofMillis(500)).until(() -> {
            statusJob.query();
            return !OPEN.contains((String) row(intentId).get("status"));
        });
        return row(intentId);
    }

    UUID pay(Seg seg, String national) {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), seg);
        return id(payMpesa(c.token(), proposal, national).then().statusCode(202).extract().response());
    }

    @ParameterizedTest(name = "{0} → {1} {2} {3}")
    @CsvSource(nullValues = "-", value = {
            "841000567, SUCCEEDED, -,                    PROVIDER,     false",
            "841000000, SUCCEEDED, -,                    PROVIDER,     false",
            "841000001, FAILED,    CUSTOMER_CANCELLED,   -,            false",
            "841000002, FAILED,    INSUFFICIENT_BALANCE, -,            false",
            "841000003, SUCCEEDED, -,                    STATUS_QUERY, true",
            "841000004, FAILED,    CUSTOMER_CANCELLED,   -,            true",
            "841000005, FAILED,    PROVIDER_REJECTED,    -,            false"})
    void simulatorBehavioursEndInTheExpectedStatesWithExactlyOneC2b(String national, String status, String failure,
                                                                    String confirmedBy, boolean viaUnknown) {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = id(payMpesa(c.token(), proposal, national).then().statusCode(202).extract().response());

        Map<String, Object> intent = awaitFinal(intentId);

        assertThat(intent).containsEntry("status", status).containsEntry("failure_code", failure)
                .containsEntry("confirmed_by", confirmedBy);
        assertThat(intent.get("dispatched_at")).isNotNull();
        String thirdPartyReference = (String) intent.get("third_party_reference");
        assertThat(thirdPartyReference).matches("[A-Z0-9]{12}");
        assertThat(MpesaMockContainer.c2bWith(thirdPartyReference)).hasSize(1).first()
                .satisfies(t -> {
                    assertThat(t.get("transactionReference")).isEqualTo(((String) intent.get("reference"))
                            .replace("-", ""));
                    assertThat(t.get("amount")).isEqualTo("2999");
                    assertThat(t.get("msisdn")).isEqualTo("258" + national);
                });
        List<String> events = MpesaTestSupport.events(support.owner(), intentId);
        assertThat(events).contains("DISPATCHED", "STATUS_CHANGED:PENDING", "PROVIDER_RESPONSE");
        if (viaUnknown) {
            assertThat(events).contains("STATUS_CHANGED:UNKNOWN", "STATUS_QUERY");
            assertThat(((Number) intent.get("query_attempts")).intValue()).isPositive();
        } else {
            assertThat(events).doesNotContain("STATUS_CHANGED:UNKNOWN");
        }
        if ("SUCCEEDED".equals(status)) {
            await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                    assertThat(support.proposalRow(proposal)).containsEntry("payment_intent_id", intentId)
                            .extractingByKey("status").isIn("PAID", "CONVERTED")); // CONVERTED: S6-01
        } else {
            assertThat(support.proposalRow(proposal)).containsEntry("status", "ACCEPTED");
        }
    }

    @Test
    void wrongApiKeyAndWrongServiceProviderCodeFailAsConfigErrorsUntilTheNextSuccess() {
        Seg wrongKey = support.insurer("Seguradora Chave Errada");
        Map<String, String> secrets = MpesaMockContainer.secrets();
        secrets.put("apiKey", "chave-errada");
        MpesaTestSupport.configure(wrongKey, MpesaMockContainer.settings(), secrets);
        Seg wrongCode = support.insurer("Seguradora Codigo Errado");
        Map<String, Object> settings = MpesaMockContainer.settings();
        settings.put("serviceProviderCode", "171718");
        MpesaTestSupport.configure(wrongCode, settings, MpesaMockContainer.secrets());

        for (Seg seg : List.of(wrongKey, wrongCode)) {
            UUID intentId = pay(seg, "841000567");
            assertThat(awaitFinal(intentId)).containsEntry("status", "FAILED")
                    .containsEntry("failure_code", "PROVIDER_CONFIG_ERROR");
            assertThat(MpesaTestSupport.lastErrorCode(support.owner(), seg.id())).isEqualTo("PROVIDER_CONFIG_ERROR");
            // o simulador recusou: nada ficou registado como C2B
            assertThat(MpesaMockContainer.c2bWith((String) row(intentId).get("third_party_reference"))).isEmpty();
        }

        // corrigida a chave, o primeiro INS-0 limpa o erro
        MpesaTestSupport.configure(wrongKey, MpesaMockContainer.settings(), MpesaMockContainer.secrets());
        assertThat(awaitFinal(pay(wrongKey, "841000567"))).containsEntry("status", "SUCCEEDED");
        assertThat(MpesaTestSupport.lastErrorCode(support.owner(), wrongKey.id())).isNull();
    }

    @Test
    void twoParallelRunsForTheSameIntentSendASingleC2b() throws Exception {
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = MpesaTestSupport.insertIntent(support.owner(), insurer, c.id(), proposal, "841000777",
                new Row("CREATED", clock.instant(), null, null, null, null));

        CountDownLatch go = new CountDownLatch(1);
        Runnable run = () -> {
            try {
                go.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            c2bJob.run(intentId);
        };
        CompletableFuture<Void> first = CompletableFuture.runAsync(run);
        CompletableFuture<Void> second = CompletableFuture.runAsync(run);
        go.countDown();
        CompletableFuture.allOf(first, second).get();

        Map<String, Object> intent = awaitFinal(intentId);
        assertThat(intent).containsEntry("status", "SUCCEEDED");
        assertThat(MpesaMockContainer.c2bWith((String) intent.get("third_party_reference"))).hasSize(1);
        assertThat(MpesaMockContainer.transactions().stream()
                .filter(t -> "C2B".equals(t.get("operation")) && "258841000777".equals(t.get("msisdn")))
                .filter(t -> ((String) intent.get("reference")).replace("-", "").equals(t.get("transactionReference"))))
                .hasSize(1);
        assertThat(MpesaTestSupport.events(support.owner(), intentId)).filteredOn("DISPATCHED"::equals).hasSize(1);
    }

    @Test
    void recoveryResubmitsAnUndispatchedIntent() {
        // Uma intenção CREATED há mais de 15 s é a que o executor cheio recusou (ExecutorAsyncDispatcherTest).
        Customer c = support.customer();
        UUID proposal = support.accepted(c.id(), insurer);
        UUID intentId = MpesaTestSupport.insertIntent(support.owner(), insurer, c.id(), proposal, "841000778",
                new Row("CREATED", clock.instant().minusSeconds(20), null, null, null, null));

        PaymentDispatchRecoveryJob.Outcome outcome = recoveryJob.recover();

        assertThat(outcome.resubmitted()).isGreaterThanOrEqualTo(1);
        Map<String, Object> intent = awaitFinal(intentId);
        assertThat(intent).containsEntry("status", "SUCCEEDED").containsEntry("confirmed_by", "PROVIDER");
        assertThat(MpesaMockContainer.c2bWith((String) intent.get("third_party_reference"))).hasSize(1);
        // um segundo ciclo já não a apanha
        recoveryJob.recover();
        assertThat(MpesaMockContainer.c2bWith((String) intent.get("third_party_reference"))).hasSize(1);
    }

    @Test
    void lostDispatchBecomesUnknownAndIsResolvedByTheStatusQueryWithoutANewC2b() {
        // …003: o C2B fica à espera (o simulador só responde aos 6 s; o nosso limite é 3 s)
        UUID intentId = pay(insurer, "841000003");
        await().atMost(Duration.ofSeconds(5)).until(() -> "PENDING".equals(row(intentId).get("status")));
        // a thread "perdeu-se": o envio parece antigo
        support.owner().update("update payment.payment_intent set dispatched_at = now() - interval '10 minutes' "
                + "where id = ?", intentId);

        PaymentDispatchRecoveryJob.Outcome outcome = recoveryJob.recover();

        assertThat(outcome.markedUnknown()).isGreaterThanOrEqualTo(1);
        assertThat(row(intentId)).containsEntry("status", "UNKNOWN");
        Map<String, Object> intent = awaitFinal(intentId);
        assertThat(intent).containsEntry("status", "SUCCEEDED").containsEntry("confirmed_by", "STATUS_QUERY");
        assertThat(MpesaMockContainer.c2bWith((String) intent.get("third_party_reference"))).hasSize(1);
        assertThat(MpesaTestSupport.events(support.owner(), intentId)).filteredOn("DISPATCHED"::equals).hasSize(1);
    }

    @Test
    void reversalOfASucceededTransaction() {
        UUID intentId = pay(insurer, "841000567");
        Map<String, Object> intent = awaitFinal(intentId);
        assertThat(intent).containsEntry("status", "SUCCEEDED");
        String txId = (String) intent.get("provider_tx_id");
        assertThat(txId).isNotBlank();
        var config = lookups.configById(MpesaTestSupport.configId(support.owner(), insurer.id())).orElseThrow();

        RefundResult first = gateway.refund(new RefundRequest(intentId, Money.ofCentavos(PaymentFlowSupport.AMOUNT),
                txId), config);
        RefundResult again = gateway.refund(new RefundRequest(intentId, Money.ofCentavos(PaymentFlowSupport.AMOUNT),
                txId), config);

        assertThat(first.outcome()).isEqualTo(RefundResult.Outcome.SUCCEEDED);
        assertThat(first.responseCode()).isEqualTo("INS-0");
        assertThat(first.providerTxId()).isNotBlank().isNotEqualTo(txId);
        assertThat(first.maskedRawResponse().toString()).doesNotContain(MpesaMockContainer.SECURITY_CREDENTIAL);
        assertThat(again.outcome()).isEqualTo(RefundResult.Outcome.FAILED);
        assertThat(MpesaMockContainer.transactions()).anySatisfy(t -> {
            assertThat(t.get("operation")).isEqualTo("REVERSAL");
            assertThat(t.get("originalTransactionId")).isEqualTo(txId);
        });
    }

    @Test
    void logsAndPaymentEventsHaveNoTokenApiKeyNorFullPhoneNumber(CapturedOutput output) {
        UUID ok = pay(insurer, "841000567");
        UUID unknown = pay(insurer, "841000004");
        awaitFinal(ok);
        awaitFinal(unknown);

        String events = String.join("\n", support.owner().queryForList("""
                select coalesce(raw_payload::text, '') from payment.payment_event where intent_id in (?, ?)""",
                String.class, ok, unknown));
        String logs = output.getAll();
        assertThat(events).contains("+258*****0567").contains("+258*****0004").contains("input_ThirdPartyReference");
        for (String text : List.of(events, logs)) {
            assertThat(text).doesNotContain(MpesaMockContainer.API_KEY)
                    .doesNotContain(MpesaMockContainer.SECURITY_CREDENTIAL)
                    .doesNotContain("Bearer ")
                    .doesNotContain("841000567").doesNotContain("841000004")
                    .doesNotContain("BEGIN PUBLIC KEY");
            // um token é o Base64 de 256 bytes (344 caracteres)
            assertThat(text).doesNotContainPattern("[A-Za-z0-9+/]{300,}={0,2}");
        }
    }
}
