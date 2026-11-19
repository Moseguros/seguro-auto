package mz.co.moseguros.payment.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.payment.application.PaymentSystemLookups.MethodConfig;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.secrets.SecretRef;
import mz.co.moseguros.shared.secrets.SecretStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Segredos de desenvolvimento das formas de pagamento das seguradoras de demonstração (S5-01 §8;
 * {@code db/dev/R__dev_seed_s5.sql}). Só no perfil {@code local}; só escreve no cofre, e só os que
 * lá não existem (o Vault em modo dev perde tudo ao reiniciar).
 *
 * <p>Os valores correspondem aos simuladores: {@code MOCK_API_KEY} do {@code mpesa-mock} (S5-03) e
 * os utilizadores do SFTP local (S5-06). Nunca regista os valores. Cofre indisponível (ex.: sem o
 * serviço {@code vault} do compose) → {@code WARN} e a aplicação arranca na mesma.
 */
// só desenvolvimento
@Component
@Profile("local")
public class DevPaymentSecretsSeeder implements ApplicationRunner {

    static final UUID DEMO = UUID.fromString("01930000-0000-7000-8000-000000000001");
    static final UUID DEMO_B = UUID.fromString("01930000-0000-7000-8000-000000000320");
    static final String DEV_MPESA_API_KEY = "dev-mpesa-api-key";
    static final String PUBLIC_KEY_RESOURCE = "dev/mpesa-mock-public.pem";

    private static final Logger log = LoggerFactory.getLogger(DevPaymentSecretsSeeder.class);

    private final PaymentSystemLookups lookups;
    private final SecretStore secrets;

    public DevPaymentSecretsSeeder(PaymentSystemLookups lookups, SecretStore secrets) {
        this.lookups = lookups;
        this.secrets = secrets;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            log.info("dev payment secrets seeded: {}", seed());
        } catch (DomainException e) {
            log.warn("dev payment secrets not seeded: {} (is the vault service running?)", e.getCode());
        }
    }

    /** Grava os segredos em falta; devolve quantos gravou. */
    int seed() {
        int written = 0;
        for (MethodConfig config : lookups.configsOf(java.util.List.of(DEMO, DEMO_B))) {
            for (Map.Entry<String, SecretRef> ref : config.secretRefs().entrySet()) {
                String value = value(config.insurerId(), SecretField.of(config.method(), ref.getKey()).orElse(null));
                if (value != null && !secrets.exists(ref.getValue().path())) {
                    secrets.put(ref.getValue().path(), value);
                    written++;
                }
            }
        }
        return written;
    }

    private static String value(UUID insurerId, SecretField field) {
        if (field == null) {
            return null;
        }
        return switch (field) {
            case MPESA_API_KEY -> DEV_MPESA_API_KEY;
            case MPESA_PUBLIC_KEY -> publicKey();
            case SFTP_PASSWORD -> DEMO.equals(insurerId) ? "demo-sftp-pass" : DEMO_B.equals(insurerId) ? "demob-sftp-pass" : null;
            default -> null;
        };
    }

    /** Só o bloco PEM do ficheiro (sem a linha de aviso que o antecede). */
    static String publicKey() {
        try (InputStream in = new ClassPathResource(PUBLIC_KEY_RESOURCE).getInputStream()) {
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            int begin = text.indexOf("-----BEGIN PUBLIC KEY-----");
            if (begin < 0) {
                throw new IllegalStateException(PUBLIC_KEY_RESOURCE + " has no PEM public key");
            }
            return text.substring(begin).strip() + "\n";
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
