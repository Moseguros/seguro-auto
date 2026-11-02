package mz.co.moseguros.smsmock;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Envia recibos de entrega ao backend, como um fornecedor real:
 * {@code POST {RECEIPT_CALLBACK_URL}/{provider}} com o cabeçalho {@code X-Webhook-Secret} e
 * {@code { providerMessageId, status, reason: "mock" }}, depois de {@code mock.receipt-delay}
 * (2 s). Sem {@code RECEIPT_CALLBACK_URL} não envia nada.
 */
@Component
public class ReceiptSender {

    private static final Logger log = LoggerFactory.getLogger(ReceiptSender.class);

    private final Environment environment;
    private final RestClient http;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "receipt-sender");
        thread.setDaemon(true);
        return thread;
    });

    public ReceiptSender(Environment environment, RestClient.Builder builder) {
        this.environment = environment;
        this.http = builder.build();
    }

    /**
     * Agenda o recibo da mensagem.
     *
     * @param provider {@code mock-sms} ou {@code mock-whatsapp}
     * @param status   {@code DELIVERED} ou {@code FAILED}
     */
    public void schedule(String provider, String messageId, String status) {
        // Lido em cada envio: o URL pode depender da porta do servidor (testes).
        String callbackUrl = environment.getProperty("mock.receipt-callback-url", "");
        if (callbackUrl.isBlank()) {
            return;
        }
        String secret = environment.getProperty("mock.receipt-secret", "");
        Duration delay = environment.getProperty("mock.receipt-delay", Duration.class, Duration.ofSeconds(2));
        scheduler.schedule(() -> send(callbackUrl, provider, secret, messageId, status), delay.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    private void send(String callbackUrl, String provider, String secret, String messageId, String status) {
        try {
            http.post()
                    .uri(callbackUrl + "/" + provider)
                    .header("X-Webhook-Secret", secret)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("providerMessageId", messageId, "status", status, "reason", "mock"))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Receipt {} for {} message {} sent", status, provider, messageId);
        } catch (RuntimeException e) {
            log.warn("Receipt {} for {} message {} could not be sent: {}", status, provider, messageId,
                    e.getMessage());
        }
    }

    @PreDestroy
    void shutdown() {
        scheduler.shutdownNow();
    }
}
