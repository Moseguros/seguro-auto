package mz.co.moseguros.payment.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.SettingsValidator.Mode;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Test;

/**
 * Regras dos dados das formas de pagamento (S5-01 §4): os dois JSON Schemas (gravação a meio e
 * activação), as regras que eles não cobrem e os segredos.
 */
class SettingsValidatorTest {

    static final ObjectMapper MAPPER = new ObjectMapper();
    static final UUID INSURER = UUID.randomUUID();

    final List<String> entitiesInUse = new ArrayList<>(List.of("90002"));
    final SettingsValidator validator = new SettingsValidator((entity, insurer) -> entitiesInUse.contains(entity), false);
    final SettingsValidator strict = new SettingsValidator((entity, insurer) -> false, true);

    static ObjectNode json(String text) {
        try {
            return (ObjectNode) MAPPER.readTree(text);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    static ObjectNode mpesa() {
        return json("""
                {"serviceProviderCode":"171717","host":"http://mpesa-mock",
                 "ports":{"c2b":18352,"query":18353,"reversal":18354},
                 "origin":"developer.mpesa.vm.co.mz","c2bTimeoutSeconds":90}""");
    }

    static ObjectNode meps(String authType) {
        return json("""
                {"entity":"90100","fileFormat":"MEPS",
                 "sftp":{"host":"sftp.seguradora.co.mz","port":22,"username":"demo","authType":"%s",
                         "inboundDir":"inbound","moveAfterProcessing":true,"processedDir":"processed",
                         "errorDir":"/data/error","filePattern":"*.txt",
                         "hostKeyFingerprint":"SHA256:%s"}}""".formatted(authType, "A".repeat(43)));
    }

    static ObjectNode bmeps() {
        ObjectNode s = meps("KEY");
        s.put("fileFormat", "BMEPS");
        s.put("bmepsCompanyId", "800");
        s.put("bmepsProfileId", "02");
        return s;
    }

    static List<String> codes(List<FieldError> errors) {
        return errors.stream().map(e -> e.field() + " " + e.code()).toList();
    }

    List<String> enable(PaymentMethod method, JsonNode settings) {
        return codes(validator.validateSettings(method, settings, INSURER, Mode.ENABLE));
    }

    List<String> save(PaymentMethod method, JsonNode settings) {
        return codes(validator.validateSettings(method, settings, INSURER, Mode.SAVE));
    }

    @Test
    void validCompleteConfigurations() {
        assertThat(enable(PaymentMethod.MPESA, mpesa())).isEmpty();
        assertThat(enable(PaymentMethod.BANK_REFERENCE, meps("PASSWORD"))).isEmpty();
        assertThat(enable(PaymentMethod.BANK_REFERENCE, meps("KEY"))).isEmpty();
        assertThat(enable(PaymentMethod.BANK_REFERENCE, bmeps())).isEmpty();
        ObjectNode mpesa = mpesa();
        mpesa.put("initiatorIdentifier", "dev-initiator");
        mpesa.put("host", "https://api.sandbox.vm.co.mz");
        assertThat(enable(PaymentMethod.MPESA, mpesa)).isEmpty();
    }

    @Test
    void savingAcceptsPartialSettingsButEnablingRequiresEverything() {
        assertThat(save(PaymentMethod.MPESA, json("{\"serviceProviderCode\":\"171717\"}"))).isEmpty();
        assertThat(save(PaymentMethod.BANK_REFERENCE, json("{\"sftp\":{\"host\":\"sftp\"}}"))).isEmpty();
        assertThat(enable(PaymentMethod.MPESA, json("{\"serviceProviderCode\":\"171717\"}")))
                .contains("settings.host REQUIRED", "settings.origin REQUIRED", "settings.ports REQUIRED");
        assertThat(enable(PaymentMethod.BANK_REFERENCE, json("{\"sftp\":{\"host\":\"sftp\"}}")))
                .contains("settings.entity REQUIRED", "settings.fileFormat REQUIRED", "settings.sftp.username REQUIRED",
                        "settings.sftp.authType REQUIRED", "settings.sftp.inboundDir REQUIRED");
        // com os valores por defeito do schema, só faltam os que não têm defeito
        ObjectNode withDefaults = SettingsValidator.withDefaults(PaymentMethod.MPESA, json("{}"));
        assertThat(withDefaults.path("ports").path("c2b").asInt()).isEqualTo(18352);
        assertThat(withDefaults.path("c2bTimeoutSeconds").asInt()).isEqualTo(90);
        assertThat(enable(PaymentMethod.MPESA, withDefaults))
                .containsExactlyInAnyOrder("settings.serviceProviderCode REQUIRED", "settings.host REQUIRED",
                        "settings.origin REQUIRED");
        ObjectNode bank = SettingsValidator.defaults(PaymentMethod.BANK_REFERENCE);
        assertThat(bank.path("sftp").path("port").asInt()).isEqualTo(22);
        assertThat(bank.path("sftp").path("moveAfterProcessing").asBoolean()).isTrue();
        assertThat(bank.path("sftp").path("filePattern").asText()).isEqualTo("*");
    }

    @Test
    void schemaErrorsHaveTheFieldPathAndTheCode() {
        ObjectNode s = meps("PASSWORD");
        s.put("entity", "1234");
        ((ObjectNode) s.get("sftp")).put("port", 70000);
        ((ObjectNode) s.get("sftp")).put("username", "");
        ((ObjectNode) s.get("sftp")).put("authType", "TOKEN");
        s.put("frequency", "DAILY");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).containsExactlyInAnyOrder(
                "settings.entity INVALID_FORMAT",
                "settings.sftp.port OUT_OF_RANGE",
                "settings.sftp.username OUT_OF_RANGE",
                "settings.sftp.authType INVALID_FORMAT",
                "settings.frequency NOT_ALLOWED");

        ObjectNode m = mpesa();
        m.put("host", "https://api.vm.co.mz:18352/path");
        m.put("serviceProviderCode", 171717);
        m.put("c2bTimeoutSeconds", 10);
        ((ObjectNode) m.get("ports")).put("extra", 1);
        assertThat(save(PaymentMethod.MPESA, m)).containsExactlyInAnyOrder(
                "settings.host INVALID_FORMAT",
                "settings.serviceProviderCode INVALID_FORMAT",
                "settings.c2bTimeoutSeconds OUT_OF_RANGE",
                "settings.ports.extra NOT_ALLOWED");

        assertThat(codes(validator.validateSettings(PaymentMethod.MPESA, MAPPER.createArrayNode(), INSURER, Mode.SAVE)))
                .containsExactly("settings INVALID_FORMAT");
    }

    @Test
    void directoriesWithParentSegmentsAreRejected() {
        ObjectNode s = meps("PASSWORD");
        ((ObjectNode) s.get("sftp")).put("processedDir", "../other");
        ((ObjectNode) s.get("sftp")).put("errorDir", "/data/../etc");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s))
                .containsExactlyInAnyOrder("settings.sftp.processedDir INVALID_FORMAT", "settings.sftp.errorDir INVALID_FORMAT");
    }

    @Test
    void bmepsIdsAreRequiredOnlyToEnableAndNotAllowedWithMeps() {
        ObjectNode s = bmeps();
        s.remove("bmepsCompanyId");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).isEmpty();
        assertThat(enable(PaymentMethod.BANK_REFERENCE, s)).containsExactly("settings.bmepsCompanyId REQUIRED");

        ObjectNode m = meps("PASSWORD");
        m.put("bmepsProfileId", "02");
        assertThat(save(PaymentMethod.BANK_REFERENCE, m)).containsExactly("settings.bmepsProfileId NOT_ALLOWED");
        assertThat(enable(PaymentMethod.BANK_REFERENCE, m)).containsExactly("settings.bmepsProfileId NOT_ALLOWED");
    }

    @Test
    void entityUsedByAnotherInsurer() {
        ObjectNode s = meps("PASSWORD");
        s.put("entity", "90002");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).containsExactly("settings.entity ENTITY_IN_USE");
        assertThat(enable(PaymentMethod.BANK_REFERENCE, s)).containsExactly("settings.entity ENTITY_IN_USE");
        // entidade mal formada: só o erro do schema
        s.put("entity", "9000");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).containsExactly("settings.entity INVALID_FORMAT");
    }

    @Test
    void directoriesMustDifferWhenFilesAreMoved() {
        ObjectNode s = meps("PASSWORD");
        ((ObjectNode) s.get("sftp")).put("processedDir", "inbound");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).containsExactly("settings.sftp.processedDir DIRECTORY_CONFLICT");
        s = meps("PASSWORD");
        ((ObjectNode) s.get("sftp")).put("errorDir", "processed");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).containsExactly("settings.sftp.processedDir DIRECTORY_CONFLICT");
        s = meps("PASSWORD");
        ((ObjectNode) s.get("sftp")).put("errorDir", "inbound");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).containsExactly("settings.sftp.errorDir DIRECTORY_CONFLICT");
        // sem mudança de pasta, as pastas não interessam
        ((ObjectNode) s.get("sftp")).put("moveAfterProcessing", false);
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).isEmpty();
    }

    @Test
    void processedAndErrorDirsAreRequiredToEnableOnlyWhenFilesAreMoved() {
        ObjectNode s = meps("PASSWORD");
        ((ObjectNode) s.get("sftp")).remove(List.of("processedDir", "errorDir"));
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).isEmpty();
        assertThat(enable(PaymentMethod.BANK_REFERENCE, s))
                .containsExactly("settings.sftp.processedDir REQUIRED", "settings.sftp.errorDir REQUIRED");
        ((ObjectNode) s.get("sftp")).put("moveAfterProcessing", false);
        assertThat(enable(PaymentMethod.BANK_REFERENCE, s)).isEmpty();
    }

    @Test
    void hostKeyFingerprintIsRequiredToEnableOutsideLocalAndTest() {
        ObjectNode s = meps("PASSWORD");
        ((ObjectNode) s.get("sftp")).remove("hostKeyFingerprint");
        assertThat(enable(PaymentMethod.BANK_REFERENCE, s)).isEmpty();
        assertThat(codes(strict.validateSettings(PaymentMethod.BANK_REFERENCE, s, INSURER, Mode.SAVE))).isEmpty();
        assertThat(codes(strict.validateSettings(PaymentMethod.BANK_REFERENCE, s, INSURER, Mode.ENABLE)))
                .containsExactly("settings.sftp.hostKeyFingerprint REQUIRED");
        ((ObjectNode) s.get("sftp")).put("hostKeyFingerprint", "MD5:aa:bb");
        assertThat(save(PaymentMethod.BANK_REFERENCE, s)).containsExactly("settings.sftp.hostKeyFingerprint INVALID_FORMAT");
    }

    @Test
    void limits() {
        assertThat(codes(validator.validateLimits(100L, null))).isEmpty();
        assertThat(codes(validator.validateLimits(100L, 100L))).isEmpty();
        assertThat(codes(validator.validateLimits(500L, 100L))).containsExactly("maxAmount MIN_GREATER_THAN_MAX");
        assertThat(codes(validator.validateLimits(0L, null))).containsExactly("minAmount OUT_OF_RANGE");
    }

    @Test
    void secretsOfTheMethodOnly() throws Exception {
        String pem = publicKeyPem();
        Map<String, String> secrets = new LinkedHashMap<>();
        secrets.put("apiKey", "k");
        secrets.put("publicKey", pem);
        secrets.put("securityCredential", "c");
        assertThat(codes(validator.validateSecrets(PaymentMethod.MPESA, secrets, mpesa()))).isEmpty();
        // Base64 DER numa linha (o formato da Vodacom) também serve
        String der = pem.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        assertThat(codes(validator.validateSecrets(PaymentMethod.MPESA, Map.of("publicKey", der), mpesa()))).isEmpty();
        // PEM com uma linha de comentário antes do BEGIN (a chave do mpesa-mock, S5-03)
        assertThat(codes(validator.validateSecrets(PaymentMethod.MPESA,
                Map.of("publicKey", "# Chave só para o simulador.\n" + pem), mpesa()))).isEmpty();

        Map<String, String> bad = new LinkedHashMap<>();
        bad.put("publicKey", "not-a-key");
        bad.put("apiKey", " ");
        bad.put("sftp.password", "x");
        bad.put("securityCredential", "x".repeat(SecretField.MAX_LENGTH + 1));
        assertThat(codes(validator.validateSecrets(PaymentMethod.MPESA, bad, mpesa()))).containsExactly(
                "secrets.publicKey INVALID_PUBLIC_KEY", "secrets.apiKey REQUIRED", "secrets.sftp.password NOT_ALLOWED",
                "secrets.securityCredential OUT_OF_RANGE");
        Map<String, String> nullValue = new LinkedHashMap<>();
        nullValue.put("apiKey", null);
        assertThat(codes(validator.validateSecrets(PaymentMethod.MPESA, nullValue, mpesa())))
                .containsExactly("secrets.apiKey REQUIRED");
    }

    @Test
    void sftpSecretsDependOnTheAuthType() {
        assertThat(codes(validator.validateSecrets(PaymentMethod.BANK_REFERENCE, Map.of("sftp.password", "p"),
                meps("PASSWORD")))).isEmpty();
        assertThat(codes(validator.validateSecrets(PaymentMethod.BANK_REFERENCE,
                Map.of("sftp.privateKey", "k", "sftp.passphrase", "p"), meps("KEY")))).isEmpty();
        assertThat(codes(validator.validateSecrets(PaymentMethod.BANK_REFERENCE, Map.of("sftp.password", "p"),
                meps("KEY")))).containsExactly("secrets.sftp.password NOT_ALLOWED");
        assertThat(codes(validator.validateSecrets(PaymentMethod.BANK_REFERENCE, Map.of("sftp.passphrase", "p"),
                meps("PASSWORD")))).containsExactly("secrets.sftp.passphrase NOT_ALLOWED");
        assertThat(codes(validator.validateSecrets(PaymentMethod.BANK_REFERENCE, Map.of("apiKey", "p"),
                meps("PASSWORD")))).containsExactly("secrets.apiKey NOT_ALLOWED");

        assertThat(codes(validator.missingSecrets(PaymentMethod.BANK_REFERENCE, meps("PASSWORD"), f -> false)))
                .containsExactly("secrets.sftp.password REQUIRED");
        assertThat(codes(validator.missingSecrets(PaymentMethod.BANK_REFERENCE, meps("KEY"), f -> false)))
                .containsExactly("secrets.sftp.privateKey REQUIRED");
        assertThat(codes(validator.missingSecrets(PaymentMethod.MPESA, mpesa(),
                f -> Set.of(SecretField.MPESA_API_KEY).contains(f)))).containsExactly("secrets.publicKey REQUIRED");
    }

    @Test
    void theSchemaIsExposedWithoutSecrets() {
        JsonNode schema = SettingsValidator.schema(PaymentMethod.MPESA);
        assertThat(schema.path("$schema").asText()).isEqualTo("https://json-schema.org/draft/2020-12/schema");
        assertThat(schema.path("additionalProperties").asBoolean(true)).isFalse();
        assertThat(schema.path("properties").has("apiKey")).isFalse();
        JsonNode bank = SettingsValidator.schema(PaymentMethod.BANK_REFERENCE);
        assertThat(bank.path("properties").path("sftp").path("properties").has("password")).isFalse();
        assertThat(bank.path("properties").path("sftp").path("additionalProperties").asBoolean(true)).isFalse();
    }

    static String publicKeyPem() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        byte[] der = generator.generateKeyPair().getPublic().getEncoded();
        return "-----BEGIN PUBLIC KEY-----\n" + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(der)
                + "\n-----END PUBLIC KEY-----\n";
    }
}
