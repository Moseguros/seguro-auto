package mz.co.moseguros.smsmock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Recibos de entrega: com {@code OK} chega um {@code DELIVERED} e com {@code FAIL_RECEIPT} um
 * {@code FAILED}, com o segredo no cabeçalho, enviados para um "backend" falso no próprio
 * simulador.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "mock.receipt-callback-url=http://localhost:${local.server.port}/test-receipts",
        "mock.receipt-secret=s3cret",
        "mock.receipt-delay=PT0.1S"})
@Import(ReceiptSenderTest.FakeBackend.class)
class ReceiptSenderTest {

    @Autowired
    TestRestTemplate http;

    @Autowired
    FakeBackend backend;

    @BeforeEach
    void reset() {
        backend.received.clear();
        http.put("/config", Map.of("sms", "OK", "whatsapp", "FAIL_RECEIPT"));
    }

    @Test
    void sendsReceiptsWithTheProviderMessageId() throws InterruptedException {
        String smsId = (String) http.postForObject("/sms", Map.of("to", "+258841234567", "text", "t"), Map.class)
                .get("id");
        String whatsappId = (String) http.postForObject("/whatsapp",
                Map.of("to", "+258841234567", "template", "moseguros_otp", "params", Map.of("code", "1")), Map.class)
                .get("id");

        long deadline = System.currentTimeMillis() + 5000;
        while (backend.received.size() < 2 && System.currentTimeMillis() < deadline) {
            Thread.sleep(50);
        }

        assertThat(backend.received).containsExactlyInAnyOrder(
                "mock-sms|s3cret|" + smsId + "|DELIVERED|mock",
                "mock-whatsapp|s3cret|" + whatsappId + "|FAILED|mock");
    }

    /** Recebe os recibos como o backend. */
    @TestConfiguration
    @RestController
    static class FakeBackend {

        final List<String> received = new CopyOnWriteArrayList<>();

        @PostMapping("/test-receipts/{provider}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        void receive(@PathVariable String provider, @RequestHeader("X-Webhook-Secret") String secret,
                     @RequestBody Map<String, String> body) {
            received.add(provider + "|" + secret + "|" + body.get("providerMessageId") + "|" + body.get("status")
                    + "|" + body.get("reason"));
        }
    }
}
