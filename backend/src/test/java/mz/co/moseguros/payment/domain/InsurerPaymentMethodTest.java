package mz.co.moseguros.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Test;

/** Transições e referências de segredos de {@link InsurerPaymentMethod} (S5-01 §6). */
class InsurerPaymentMethodTest {

    static final Instant NOW = Instant.parse("2027-01-15T08:00:00Z");
    static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    static final UUID INSURER = UUID.randomUUID();

    @Test
    void newRowIsDisabledWithDefaults() {
        InsurerPaymentMethod m = InsurerPaymentMethod.create(INSURER, PaymentMethod.MPESA);
        assertThat(m.isEnabled()).isFalse();
        assertThat(m.getEnvironment()).isEqualTo(PaymentEnvironment.SANDBOX);
        assertThat(m.getIntentValidityMinutes()).isEqualTo(30);
        assertThat(m.getSettings().isObject()).isTrue();
        assertThat(m.secretPaths()).isEmpty();
    }

    @Test
    void enableAndDisable() {
        InsurerPaymentMethod m = InsurerPaymentMethod.create(INSURER, PaymentMethod.MPESA);
        assertThatThrownBy(m::disable).isInstanceOf(DomainException.class)
                .extracting(e -> ((DomainException) e).getCode()).isEqualTo("INVALID_STATE_TRANSITION");

        List<FieldError> missing = List.of(new FieldError("secrets.publicKey", "REQUIRED", "x"));
        assertThatThrownBy(() -> m.enable(missing, CLOCK)).isInstanceOf(DomainException.class)
                .satisfies(e -> {
                    DomainException d = (DomainException) e;
                    assertThat(d.getCode()).isEqualTo("PAYMENT_METHOD_INCOMPLETE");
                    assertThat(d.getHttpStatus()).isEqualTo(409);
                    assertThat(d.getProperties().get("errors")).isEqualTo(missing);
                });
        assertThat(m.isEnabled()).isFalse();

        m.enable(List.of(), CLOCK);
        assertThat(m.isEnabled()).isTrue();
        assertThat(m.getEnabledAt()).isEqualTo(NOW);
        assertThatThrownBy(() -> m.enable(List.of(), CLOCK)).isInstanceOf(DomainException.class)
                .extracting(e -> ((DomainException) e).getCode()).isEqualTo("INVALID_STATE_TRANSITION");

        m.disable();
        assertThat(m.isEnabled()).isFalse();
        assertThat(m.getEnabledAt()).isEqualTo(NOW);
    }

    @Test
    void updateConfigKeepsTheState() {
        InsurerPaymentMethod m = InsurerPaymentMethod.create(INSURER, PaymentMethod.BANK_REFERENCE);
        m.enable(List.of(), CLOCK);
        ObjectNode settings = new ObjectMapper().createObjectNode().put("entity", "90002");
        m.updateConfig(PaymentEnvironment.PRODUCTION, "  Referência  ", settings, 100L, 200L, 60, 2);
        settings.put("entity", "11111");
        assertThat(m.isEnabled()).isTrue();
        assertThat(m.getSettings().path("entity").asText()).isEqualTo("90002");
        assertThat(m.getDisplayName()).isEqualTo("Referência");
        assertThat(m.getMinAmount()).isEqualTo(100L);
        assertThat(m.getSortOrder()).isEqualTo(2);
        assertThatThrownBy(() -> m.updateConfig(PaymentEnvironment.SANDBOX, null, settings, 300L, 200L, 30, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> m.updateConfig(PaymentEnvironment.SANDBOX, null, settings, null, null, 4, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void secretRefsHoldOnlyPathsAndDates() {
        InsurerPaymentMethod m = InsurerPaymentMethod.create(INSURER, PaymentMethod.BANK_REFERENCE);
        String path = SecretField.SFTP_PASSWORD.path(INSURER);
        assertThat(path).isEqualTo("secret/insurers/" + INSURER + "/payments/BANK_REFERENCE/sftp.password");
        m.putSecretRef(SecretField.SFTP_PASSWORD, path, NOW);
        assertThat(m.secretPath(SecretField.SFTP_PASSWORD)).contains(path);
        assertThat(m.secretUpdatedAt(SecretField.SFTP_PASSWORD)).contains(NOW);
        assertThat(m.getSecretRefs().toString()).contains("\"path\"", "\"updatedAt\"");
        assertThat(m.removeSecretRef(SecretField.SFTP_PASSWORD)).isTrue();
        assertThat(m.removeSecretRef(SecretField.SFTP_PASSWORD)).isFalse();
        assertThatThrownBy(() -> m.putSecretRef(SecretField.MPESA_API_KEY, "secret/x", NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testResultIsRecorded() {
        InsurerPaymentMethod m = InsurerPaymentMethod.create(INSURER, PaymentMethod.BANK_REFERENCE);
        m.recordTest(TestResult.failed("SECRET_MISSING"), NOW);
        assertThat(m.getLastTestResult()).isEqualTo(TestResult.Status.FAILED);
        assertThat(m.getLastTestMessage()).isEqualTo("SECRET_MISSING");
        assertThat(m.getLastTestAt()).isEqualTo(NOW);
    }

    @Test
    void secretFieldsPerMethodAndAuthType() {
        ObjectNode password = new ObjectMapper().createObjectNode();
        password.putObject("sftp").put("authType", "PASSWORD");
        ObjectNode key = new ObjectMapper().createObjectNode();
        key.putObject("sftp").put("authType", "KEY");
        assertThat(SecretField.of(PaymentMethod.MPESA)).extracting(SecretField::fieldName)
                .containsExactly("apiKey", "publicKey", "securityCredential");
        assertThat(SecretField.SFTP_PASSWORD.required(password)).isTrue();
        assertThat(SecretField.SFTP_PASSWORD.applies(key)).isFalse();
        assertThat(SecretField.SFTP_PRIVATE_KEY.required(key)).isTrue();
        assertThat(SecretField.SFTP_PASSPHRASE.required(key)).isFalse();
        assertThat(SecretField.SFTP_PASSPHRASE.applies(password)).isFalse();
        assertThat(SecretField.MPESA_SECURITY_CREDENTIAL.required(password)).isFalse();
        assertThat(SecretField.of(PaymentMethod.BANK_REFERENCE, "sftp.privateKey")).contains(SecretField.SFTP_PRIVATE_KEY);
        assertThat(PaymentMethod.parse("EMOLA")).isEmpty();
        assertThat(PaymentMethod.parse("mpesa")).isEmpty();
        assertThat(PaymentMethod.parse("BANK_REFERENCE")).contains(PaymentMethod.BANK_REFERENCE);
    }
}
