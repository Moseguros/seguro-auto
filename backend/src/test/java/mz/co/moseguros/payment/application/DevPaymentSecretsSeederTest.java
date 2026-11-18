package mz.co.moseguros.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.domain.PaymentEnvironment;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.secrets.InMemorySecretStore;
import mz.co.moseguros.shared.secrets.SecretRef;
import mz.co.moseguros.shared.secrets.SecretStore;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Segredos de desenvolvimento (S5-01 §8): os valores dos simuladores, só os que faltam no cofre, e
 * um cofre indisponível não impede o arranque.
 */
class DevPaymentSecretsSeederTest {

    static final UUID DEMO = DevPaymentSecretsSeeder.DEMO;
    static final UUID DEMO_B = DevPaymentSecretsSeeder.DEMO_B;

    final PaymentSystemLookups lookups = mock(PaymentSystemLookups.class);
    final InMemorySecretStore store = new InMemorySecretStore();

    static MethodConfig config(UUID insurer, PaymentMethod method, SecretField... fields) {
        Map<String, SecretRef> refs = new java.util.LinkedHashMap<>();
        for (SecretField f : fields) {
            refs.put(f.fieldName(), new SecretRef(f.path(insurer)));
        }
        return new MethodConfig(UUID.randomUUID(), insurer, method, PaymentEnvironment.SANDBOX, null,
                JsonNodeFactory.instance.objectNode(), refs, null, null, 30, 0, 0);
    }

    @Test
    void writesTheDevelopmentValuesThatAreMissing() {
        when(lookups.configsOf(any())).thenReturn(List.of(
                config(DEMO, PaymentMethod.MPESA, SecretField.MPESA_API_KEY, SecretField.MPESA_PUBLIC_KEY),
                config(DEMO, PaymentMethod.BANK_REFERENCE, SecretField.SFTP_PASSWORD),
                config(DEMO_B, PaymentMethod.BANK_REFERENCE, SecretField.SFTP_PASSWORD)));
        store.put(SecretField.SFTP_PASSWORD.path(DEMO_B), "ja-existia");

        DevPaymentSecretsSeeder seeder = new DevPaymentSecretsSeeder(lookups, store);
        assertThat(seeder.seed()).isEqualTo(3);
        assertThat(store.get(SecretField.MPESA_API_KEY.path(DEMO))).contains("dev-mpesa-api-key");
        assertThat(store.get(SecretField.SFTP_PASSWORD.path(DEMO))).contains("demo-sftp-pass");
        assertThat(store.get(SecretField.SFTP_PASSWORD.path(DEMO_B))).contains("ja-existia");
        String publicKey = store.get(SecretField.MPESA_PUBLIC_KEY.path(DEMO)).orElseThrow();
        assertThat(publicKey).startsWith("-----BEGIN PUBLIC KEY-----");
        assertThat(SettingsValidator.isRsaPublicKey(publicKey)).isTrue();

        // segunda vez: nada a escrever
        assertThat(seeder.seed()).isZero();
    }

    @Test
    void demoBGetsItsOwnSftpPassword() {
        when(lookups.configsOf(any())).thenReturn(List.of(
                config(DEMO_B, PaymentMethod.BANK_REFERENCE, SecretField.SFTP_PASSWORD)));
        new DevPaymentSecretsSeeder(lookups, store).seed();
        assertThat(store.get(SecretField.SFTP_PASSWORD.path(DEMO_B))).contains("demob-sftp-pass");
    }

    @Test
    void unavailableStoreOnlyWarns() {
        when(lookups.configsOf(any())).thenReturn(List.of(
                config(DEMO, PaymentMethod.MPESA, SecretField.MPESA_API_KEY)));
        SecretStore down = mock(SecretStore.class);
        when(down.exists(any())).thenThrow(DomainException.of("SECRET_STORE_UNAVAILABLE", "down"));
        new DevPaymentSecretsSeeder(lookups, down).run(null);
    }

    /** A chave do backend é a mesma que o simulador usa (S5-03 lê o par de infra/mocks/mpesa-mock). */
    @Test
    void publicKeyIsTheSimulatorKey() throws IOException {
        String backend = new ClassPathResource(DevPaymentSecretsSeeder.PUBLIC_KEY_RESOURCE)
                .getContentAsString(StandardCharsets.UTF_8);
        java.nio.file.Path mock = java.nio.file.Path.of("..", "infra", "mocks", "mpesa-mock", "src", "main", "resources",
                "keys", "mock-public.pem");
        if (java.nio.file.Files.exists(mock)) {
            assertThat(backend).isEqualTo(java.nio.file.Files.readString(mock, StandardCharsets.UTF_8));
        }
        assertThat(backend).startsWith("# Chave só para o simulador. Nunca usar fora do desenvolvimento.");
    }
}
