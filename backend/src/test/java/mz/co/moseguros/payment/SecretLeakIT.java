package mz.co.moseguros.payment;

import static mz.co.moseguros.payment.PaymentTestSupport.action;
import static mz.co.moseguros.payment.PaymentTestSupport.bankSettings;
import static mz.co.moseguros.payment.PaymentTestSupport.body;
import static mz.co.moseguros.payment.PaymentTestSupport.list;
import static mz.co.moseguros.payment.PaymentTestSupport.mpesaSettings;
import static mz.co.moseguros.payment.PaymentTestSupport.publicKeyPem;
import static mz.co.moseguros.payment.PaymentTestSupport.put;
import static mz.co.moseguros.payment.PaymentTestSupport.randomEntity;
import static mz.co.moseguros.payment.PaymentTestSupport.test;
import static mz.co.moseguros.payment.PaymentTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.shared.secrets.SecretStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Os segredos das formas de pagamento (S5-01 §2, AGENTS.md §6) nunca aparecem em respostas, nos
 * logs nem em {@code audit.audit_event}: procura textual de cada valor conhecido depois de gravar,
 * listar, activar, testar e de pedidos com erro.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class SecretLeakIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    SecretStore secretStore;

    @Test
    void secretValuesNeverLeak(CapturedOutput output) {
        RestAssured.port = port;
        CatalogTestSupport support = new CatalogTestSupport(fixtures, identityApi);
        Insurer insurer = support.insurer("Seg Fugas " + UUID.randomUUID().toString().substring(0, 4));
        String admin = support.staffToken(insurer.id(), "INSURER_ADMIN");

        String apiKey = "fuga-api-key-" + UUID.randomUUID();
        String credential = "fuga-credential-" + UUID.randomUUID();
        String password = "fuga-sftp-pass-" + UUID.randomUUID();
        String privateKey = "fuga-private-key-" + UUID.randomUUID();
        String publicKeyBody = publicKeyPem().lines().filter(l -> !l.startsWith("-----")).findFirst().orElseThrow();
        List<String> values = List.of(apiKey, credential, password, privateKey, publicKeyBody);

        List<Response> responses = new ArrayList<>();
        responses.add(put(admin, "MPESA", body(mpesaSettings(),
                Map.of("apiKey", apiKey, "publicKey", publicKeyPem(), "securityCredential", credential), null)));
        responses.add(put(admin, "BANK_REFERENCE", body(bankSettings(randomEntity(), "MEPS", "PASSWORD"),
                Map.of("sftp.password", password), null)));
        // pedido com erro que inclui um segredo
        responses.add(put(admin, "BANK_REFERENCE", body(bankSettings("1", "MEPS", "PASSWORD"),
                Map.of("sftp.privateKey", privateKey), version(admin, "BANK_REFERENCE"))));
        responses.add(put(admin, "MPESA", body(mpesaSettings(), Map.of("publicKey", "x" + apiKey), 0L)));
        responses.add(action(admin, "MPESA", "enable", 0L));
        responses.add(action(admin, "BANK_REFERENCE", "enable", 0L));
        responses.add(test(admin, "BANK_REFERENCE"));
        responses.add(test(admin, "MPESA"));
        responses.add(list(admin));

        assertThat(responses.get(0).statusCode()).isEqualTo(200);
        assertThat(responses.get(1).statusCode()).isEqualTo(200);
        assertThat(responses.get(2).statusCode()).isEqualTo(400);
        assertThat(responses.get(4).statusCode()).isEqualTo(200);
        assertThat(secretStore.get("secret/insurers/" + insurer.id() + "/payments/MPESA/apiKey")).contains(apiKey);

        String bodies = String.join("\n", responses.stream().map(Response::asString).toList());
        String audit = String.join("\n", support.owner().queryForList("""
                select coalesce(before_state::text, '') || ' ' || coalesce(after_state::text, '')
                  from audit.audit_event where insurer_id = ?""", String.class, insurer.id()));
        String rows = String.join("\n", support.owner().queryForList("""
                select settings::text || ' ' || secret_refs::text || ' ' || coalesce(last_test_message, '')
                  from payment.insurer_payment_method where insurer_id = ?""", String.class, insurer.id()));
        assertThat(audit).contains("PASSWORD").contains("configured");
        assertThat(rows).contains("secret/insurers/" + insurer.id());
        for (String value : values) {
            assertThat(bodies).as("responses").doesNotContain(value);
            assertThat(output.getAll()).as("logs").doesNotContain(value);
            assertThat(audit).as("audit_event").doesNotContain(value);
            assertThat(rows).as("insurer_payment_method").doesNotContain(value);
        }
    }
}
