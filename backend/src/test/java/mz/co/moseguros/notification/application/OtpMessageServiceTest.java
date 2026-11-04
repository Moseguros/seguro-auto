package mz.co.moseguros.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.DeliveryStatus;
import mz.co.moseguros.notification.OtpDeliveryFailed;
import mz.co.moseguros.notification.SignOtpText;
import mz.co.moseguros.notification.domain.Destinations;
import mz.co.moseguros.notification.domain.OutboundMessage;
import mz.co.moseguros.notification.domain.OutboundMessageRepository;
import mz.co.moseguros.notification.infrastructure.NotificationProperties;
import mz.co.moseguros.notification.infrastructure.OtpDeliveryChannel;
import mz.co.moseguros.notification.infrastructure.ProviderResult;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Envio de OTP ({@link OtpMessageService}) e recibos de entrega ({@link DeliveryReceiptService})
 * sem base de dados.
 */
class OtpMessageServiceTest {

    static final Instant NOW = Instant.parse("2026-11-02T10:00:00Z");

    final OutboundMessageRepository messages = mock(OutboundMessageRepository.class);
    final List<OutboundMessage> saved = new ArrayList<>();
    final ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
    Recording sms;
    Recording email;
    OtpMessageService service;
    DeliveryReceiptService receipts;

    @BeforeEach
    void setUp() {
        when(messages.save(any(OutboundMessage.class))).thenAnswer(invocation -> {
            saved.add(invocation.getArgument(0));
            return invocation.getArgument(0);
        });
        when(messages.findById(any())).thenAnswer(invocation -> saved.stream()
                .filter(m -> m.getId().equals(invocation.getArgument(0))).findFirst());
        when(messages.findFirstByProviderAndProviderMessageId(anyString(), anyString())).thenAnswer(invocation ->
                saved.stream().filter(m -> m.getProvider().equals(invocation.getArgument(0))
                        && invocation.getArgument(1).equals(m.getProviderMessageId())).findFirst());
        sms = new Recording(Channel.SMS, "mock-sms", true);
        email = new Recording(Channel.EMAIL, "smtp", false);
        ResourceBundleMessageSource texts = new ResourceBundleMessageSource();
        texts.setBasename("messages/notifications");
        texts.setDefaultEncoding("UTF-8");
        texts.setFallbackToSystemLocale(false);
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        service = new OtpMessageService(List.of(sms, email), messages, texts,
                new TransactionTemplate(mock(PlatformTransactionManager.class)), clock);
        NotificationProperties properties = new NotificationProperties("secret", null, null, null, null);
        receipts = new DeliveryReceiptService(messages, service, properties, events, clock);
    }

    @Test
    void smsTextIsTranslatedAndNothingSensitiveIsStored() {
        DeliveryResult result = service.sendOtp(Channel.SMS, "+258841234567", "012345", 5, "pt", null);

        assertThat(result.status()).isEqualTo(DeliveryStatus.SENT);
        assertThat(sms.text).isEqualTo("MoSeguros: o seu código é 012345. Válido por 5 min. "
                + "Não o partilhe com ninguém.");
        assertThat(sms.params).containsEntry("code", "012345");
        OutboundMessage message = saved.get(0);
        assertThat(message.getStatus()).isEqualTo(DeliveryStatus.SENT);
        assertThat(message.getSentAt()).isEqualTo(NOW);
        assertThat(message.getProviderMessageId()).isEqualTo("p-1");
        assertThat(message.getDestinationMasked()).isEqualTo("+258*****4567");
        assertThat(message.getDestinationHash()).isEqualTo(Destinations.hash("258841234567"));

        service.sendOtp(Channel.SMS, "+258841234567", "999999", 10, "en", null);
        assertThat(sms.text).isEqualTo("MoSeguros: your code is 999999. Valid for 10 min. Do not share it with anyone.");
    }

    @Test
    void emailHasSubjectAndBody() {
        service.sendOtp(Channel.EMAIL, "ana@exemplo.co.mz", "123456", 5, "xx", null);

        assertThat(email.params).containsEntry("subject", "O seu código MoSeguros");
        assertThat(email.text).startsWith("Olá,\n\no seu código de acesso à MoSeguros é 123456. É válido durante 5 "
                + "minutos.");
        assertThat(saved.get(0).getDestinationMasked()).isEqualTo("a***@exemplo.co.mz");
    }

    @Test
    void signCodeTextsAreTranslatedAndStoredAsSignOtp() {
        SignOtpText text = new SignOtpText("PR-2027-000123", "Seguradora Demo", "4\u00A0663,00 MT", null);
        DeliveryResult result = service.sendSignOtp(Channel.SMS, "+258841234567", "012345", 5, text, "pt", null);

        assertThat(result.status()).isEqualTo(DeliveryStatus.SENT);
        assertThat(sms.text).isEqualTo("MoSeguros: o código 012345 assina a proposta PR-2027-000123 da Seguradora "
                + "Demo, prémio 4\u00A0663,00 MT. Válido 5 min. Não o partilhe com ninguém.");
        assertThat(sms.params).containsEntry("code", "012345").containsEntry("template", "moseguros_sign_otp")
                .containsEntry("ref", "PR-2027-000123").containsEntry("insurer", "Seguradora Demo")
                .containsEntry("total", "4\u00A0663,00 MT");
        assertThat(saved.get(0).getPurpose()).isEqualTo(OutboundMessage.Purpose.SIGN_OTP);
        assertThat(saved.get(0).isOtp()).isTrue();

        service.sendSignOtp(Channel.SMS, "+258841234567", "999999", 5,
                new SignOtpText("PR-2027-000123", "Demo", "MZN 4,663.00", "Corretora Demo"), "en", null);
        assertThat(sms.text).isEqualTo("MoSeguros: code 999999 signs proposal PR-2027-000123 from Demo, premium "
                + "MZN 4,663.00, prepared by Corretora Demo. Valid 5 min. Only tell it to Corretora Demo if you agree "
                + "with the proposal. MoSeguros never asks for codes by phone.");

        service.sendSignOtp(Channel.EMAIL, "ana@exemplo.co.mz", "123456", 5, text, "pt", null);
        assertThat(email.params).containsEntry("subject", "Código para assinar a proposta PR-2027-000123");
        assertThat(email.text).startsWith("Olá,\n\no código 123456 assina a proposta PR-2027-000123 da Seguradora "
                + "Demo, com o prémio total de 4\u00A0663,00 MT.");
    }

    @Test
    void providerErrorsBecomeShortFailures() {
        sms.error = HttpServerErrorException.create(HttpStatus.SERVICE_UNAVAILABLE, "x", null, null, null);
        assertThat(service.sendOtp(Channel.SMS, "+258841234567", "1", 5, "pt", null).failureReason())
                .isEqualTo("HTTP 503");
        sms.error = new ResourceAccessException("timeout");
        assertThat(service.sendOtp(Channel.SMS, "+258841234567", "1", 5, "pt", null).failureReason())
                .isEqualTo("provider unreachable or timeout");
        email.error = new MailSendException("down");
        assertThat(service.sendOtp(Channel.EMAIL, "a@b.co", "1", 5, "pt", null).failureReason())
                .startsWith("smtp error");
        sms.error = new IllegalStateException("boom +258841234567");
        DeliveryResult failed = service.sendOtp(Channel.SMS, "+258841234567", "1", 5, "pt", null);
        assertThat(failed.status()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(failed.failureReason()).isEqualTo("provider error: IllegalStateException");
        assertThat(saved.get(3).getFailureReason()).doesNotContain("841234567");

        DeliveryResult noChannel = service.sendOtp(Channel.WHATSAPP, "+258841234567", "1", 5, "pt", null);
        assertThat(noChannel.failureReason()).isEqualTo("channel not configured");
        assertThat(saved.get(4).getProvider()).isEqualTo("none");
    }

    @Test
    void twoChannelsForTheSameChannelAreRejected() {
        assertThatThrownBy(() -> new OtpMessageService(List.of(sms, new Recording(Channel.SMS, "other", false)),
                messages, new ResourceBundleMessageSource(), null, Clock.systemUTC()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void receiptsCheckSecretAndProviderAndPublishOtpFailures() {
        UUID challengeId = UUID.randomUUID();
        service.sendOtp(Channel.SMS, "+258841234567", "1", 5, "pt", challengeId);

        assertCode(() -> receipts.receive("mock-sms", "wrong", "p-1", DeliveryReceiptService.ReceiptStatus.FAILED,
                null), "UNAUTHORIZED");
        assertCode(() -> receipts.receive("mock-sms", null, "p-1", DeliveryReceiptService.ReceiptStatus.FAILED,
                null), "UNAUTHORIZED");
        assertCode(() -> receipts.receive("smtp", "secret", "p-1", DeliveryReceiptService.ReceiptStatus.FAILED,
                null), "NOT_FOUND");
        receipts.receive("mock-sms", "secret", "unknown", DeliveryReceiptService.ReceiptStatus.FAILED, null);
        verify(events, never()).publishEvent(any(Object.class));

        receipts.receive("mock-sms", "secret", "p-1", DeliveryReceiptService.ReceiptStatus.FAILED, "no route");
        assertThat(saved.get(0).getStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(saved.get(0).getFailureReason()).isEqualTo("receipt: no route");
        verify(events).publishEvent(new OtpDeliveryFailed(saved.get(0).getId(), challengeId, Channel.SMS));

        receipts.receive("mock-sms", "secret", "p-1", DeliveryReceiptService.ReceiptStatus.FAILED, null);
        verify(events).publishEvent(any(Object.class));
    }

    @Test
    void deliveredReceiptMarksTheMessage() {
        service.sendOtp(Channel.SMS, "+258841234567", "1", 5, "pt", null);

        receipts.receive("mock-sms", "secret", "p-1", DeliveryReceiptService.ReceiptStatus.DELIVERED, null);

        assertThat(saved.get(0).getStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(saved.get(0).getDeliveredAt()).isEqualTo(NOW);
        assertThat(Optional.ofNullable(saved.get(0).getFailureReason())).isEmpty();
    }

    @Test
    void destinationsMaskAndHash() {
        assertThat(Destinations.mask("+258841234567")).isEqualTo("+258*****4567");
        assertThat(Destinations.mask("ana@exemplo.co.mz")).isEqualTo("a***@exemplo.co.mz");
        assertThat(Destinations.mask("12345678")).isEqualTo("***5678");
        assertThat(Destinations.mask(null)).isEqualTo("***");
        assertThat(Destinations.hash("+258 84 123 4567")).isEqualTo(Destinations.hash("841234567"));
        assertThat(Destinations.hash("Ana@Exemplo.co.mz")).isEqualTo(Destinations.hash("ana@exemplo.co.mz"));
        assertThat(Destinations.hash("x")).hasSize(64);
    }

    private static void assertCode(Runnable action, String code) {
        assertThatThrownBy(action::run).isInstanceOfSatisfying(DomainException.class,
                e -> assertThat(e.getCode()).isEqualTo(code));
    }

    /** Canal que guarda o último envio e devolve ids {@code p-1}, {@code p-2}… */
    static final class Recording implements OtpDeliveryChannel {

        final Channel channel;
        final String provider;
        final boolean receipts;
        String text;
        Map<String, String> params;
        RuntimeException error;
        int count;

        Recording(Channel channel, String provider, boolean receipts) {
            this.channel = channel;
            this.provider = provider;
            this.receipts = receipts;
        }

        @Override
        public Channel channel() {
            return channel;
        }

        @Override
        public String provider() {
            return provider;
        }

        @Override
        public boolean acceptsDeliveryReceipts() {
            return receipts;
        }

        @Override
        public ProviderResult send(String destination, String text, Map<String, String> templateParams) {
            if (error != null) {
                throw error;
            }
            this.text = text;
            this.params = templateParams;
            return ProviderResult.accepted("p-" + (++count));
        }
    }
}
