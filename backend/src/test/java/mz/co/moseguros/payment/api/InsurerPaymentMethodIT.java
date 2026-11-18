package mz.co.moseguros.payment.api;

import static io.restassured.RestAssured.given;
import static mz.co.moseguros.payment.PaymentTestSupport.METHODS;
import static mz.co.moseguros.payment.PaymentTestSupport.action;
import static mz.co.moseguros.payment.PaymentTestSupport.bankSettings;
import static mz.co.moseguros.payment.PaymentTestSupport.body;
import static mz.co.moseguros.payment.PaymentTestSupport.list;
import static mz.co.moseguros.payment.PaymentTestSupport.mpesaSettings;
import static mz.co.moseguros.payment.PaymentTestSupport.publicKeyPem;
import static mz.co.moseguros.payment.PaymentTestSupport.put;
import static mz.co.moseguros.payment.PaymentTestSupport.randomEntity;
import static mz.co.moseguros.payment.PaymentTestSupport.sftp;
import static mz.co.moseguros.payment.PaymentTestSupport.test;
import static mz.co.moseguros.payment.PaymentTestSupport.version;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.LinkedHashMap;
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
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.PaymentSystemLookups;
import mz.co.moseguros.payment.domain.InsurerPaymentMethod;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import mz.co.moseguros.shared.secrets.SecretStore;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Formas de pagamento por seguradora (S5-01 §3 a §5) contra Postgres real, com o
 * {@code InMemorySecretStore}: lista, gravação com segredos, regras de validação, activação, teste,
 * bloqueio optimista, isolamento entre seguradoras (serviço e RLS) e permissões.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class InsurerPaymentMethodIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    SecretStore secretStore;

    @Autowired
    InsurerPaymentMethodRepository repository;

    @Autowired
    PaymentSystemLookups lookups;

    @Autowired
    TransactionTemplate tx;

    CatalogTestSupport support;
    Insurer insurer;
    String admin;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support = new CatalogTestSupport(fixtures, identityApi);
        insurer = support.insurer("Seg Pagamentos " + UUID.randomUUID().toString().substring(0, 4));
        admin = support.staffToken(insurer.id(), "INSURER_ADMIN");
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    static Map<String, String> secrets(String... pairs) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            m.put(pairs[i], pairs[i + 1]);
        }
        return m;
    }

    String path(String method, String field) {
        return "secret/insurers/" + insurer.id() + "/payments/" + method + "/" + field;
    }

    static void fieldError(Response r, int status, String code, String field, String fieldCode) {
        r.then().statusCode(status)
                .body("code", equalTo(code))
                .body("errors.find { it.field == '" + field + "' }.code", equalTo(fieldCode));
    }

    @Test
    void listsBothMethodsWithDefaultsBeforeSaving() {
        list(admin).then().statusCode(200)
                .body("methods.method", contains("MPESA", "BANK_REFERENCE"))
                .body("methods.configured", contains(false, false))
                .body("methods.enabled", contains(false, false))
                .body("methods.environment", contains("SANDBOX", "SANDBOX"))
                .body("methods[0].settings.ports.c2b", equalTo(18352))
                .body("methods[0].settings.ports.reversal", equalTo(18354))
                .body("methods[0].settings.c2bTimeoutSeconds", equalTo(90))
                .body("methods[0].settingsSchema.$schema", equalTo("https://json-schema.org/draft/2020-12/schema"))
                .body("methods[0].settingsSchema.properties.serviceProviderCode.pattern", equalTo("^[0-9]{5,10}$"))
                .body("methods[0].secrets.apiKey.configured", equalTo(false))
                .body("methods[0].secrets.apiKey.required", equalTo(true))
                .body("methods[0].secrets.securityCredential.required", equalTo(false))
                .body("methods[0].intentValidityMinutes", equalTo(30))
                .body("methods[0].version", nullValue())
                .body("methods[1].settings.sftp.port", equalTo(22))
                .body("methods[1].settings.sftp.moveAfterProcessing", equalTo(true))
                .body("methods[1].secrets.'sftp.password'.required", equalTo(false));

        put(admin, "EMOLA", body(mpesaSettings(), null, null)).then().statusCode(404)
                .body("code", equalTo("PAYMENT_METHOD_NOT_FOUND"));
        put(admin, "mpesa", body(mpesaSettings(), null, null)).then().statusCode(404);
        action(admin, "MPESA", "enable", 0L).then().statusCode(404).body("code", equalTo("PAYMENT_METHOD_NOT_FOUND"));
        test(admin, "BANK_REFERENCE").then().statusCode(404).body("code", equalTo("PAYMENT_METHOD_NOT_FOUND"));
    }

    @Test
    void savesMpesaWithSecretsThatAreNeverReturned() {
        String apiKey = "api-key-" + UUID.randomUUID();
        Map<String, Object> b = body(mpesaSettings(), secrets("apiKey", apiKey, "publicKey", publicKeyPem()), null);
        b.put("displayName", "M-Pesa Seg");
        b.put("minAmount", Map.of("amount", 100, "currency", "MZN"));
        Response saved = put(admin, "MPESA", b);
        saved.then().statusCode(200)
                .body("method", equalTo("MPESA"))
                .body("configured", equalTo(true))
                .body("enabled", equalTo(false))
                .body("displayName", equalTo("M-Pesa Seg"))
                .body("settings.serviceProviderCode", equalTo("171717"))
                .body("settings.ports.query", equalTo(18353))
                .body("secrets.apiKey.configured", equalTo(true))
                .body("secrets.apiKey.updatedAt", notNullValue())
                .body("secrets.publicKey.configured", equalTo(true))
                .body("secrets.securityCredential.configured", equalTo(false))
                .body("minAmount.amount", equalTo(100))
                .body("minAmount.currency", equalTo("MZN"))
                .body("version", equalTo(0));
        assertThat(saved.asString()).doesNotContain(apiKey).doesNotContain("BEGIN PUBLIC KEY");
        assertThat(secretStore.get(path("MPESA", "apiKey"))).contains(apiKey);
        assertThat(secretStore.get(path("MPESA", "publicKey"))).contains(publicKeyPem());
        String refs = support.owner().queryForObject(
                "select secret_refs::text from payment.insurer_payment_method where insurer_id = ? and method = 'MPESA'",
                String.class, insurer.id());
        assertThat(refs).contains(path("MPESA", "apiKey")).doesNotContain(apiKey);

        // sem secrets: mantém
        put(admin, "MPESA", body(mpesaSettings(), null, 0L)).then().statusCode(200)
                .body("secrets.apiKey.configured", equalTo(true))
                .body("version", equalTo(1));
        assertThat(secretStore.get(path("MPESA", "apiKey"))).contains(apiKey);

        // com apiKey nova: substitui no cofre
        String newKey = "api-key-novo-" + UUID.randomUUID();
        Response replaced = put(admin, "MPESA", body(mpesaSettings(), secrets("apiKey", newKey), 1L));
        replaced.then().statusCode(200).body("secrets.apiKey.configured", equalTo(true));
        assertThat(replaced.asString()).doesNotContain(newKey);
        assertThat(secretStore.get(path("MPESA", "apiKey"))).contains(newKey);

        List<Map<String, Object>> audit = support.owner().queryForList("""
                select before_state::text as before, after_state::text as after, entity_type from audit.audit_event
                 where action = 'PAYMENT_METHOD.UPDATE' and insurer_id = ? order by occurred_at""", insurer.id());
        assertThat(audit).hasSize(3);
        assertThat(audit.get(0).get("before")).isNull();
        assertThat(audit.get(0).get("entity_type")).isEqualTo("InsurerPaymentMethod");
        assertThat((String) audit.get(0).get("after")).contains("\"apiKey\": \"changed\"", "\"publicKey\": \"changed\"");
        assertThat((String) audit.get(1).get("after")).contains("\"apiKey\": \"configured\"");
        assertThat((String) audit.get(2).get("after")).contains("\"apiKey\": \"changed\"", "\"publicKey\": \"configured\"");
        assertThat(audit.toString()).doesNotContain(apiKey).doesNotContain(newKey).doesNotContain("BEGIN PUBLIC KEY");
    }

    @Test
    void eachValidationRuleHasItsFieldAndCode() {
        String entity = randomEntity();
        // INVALID_FORMAT (pattern)
        fieldError(put(admin, "BANK_REFERENCE", body(bankSettings("1234", "MEPS", "PASSWORD"), null, null)),
                400, "VALIDATION_ERROR", "settings.entity", "INVALID_FORMAT");
        // NOT_ALLOWED: bmepsProfileId num MEPS
        Map<String, Object> meps = bankSettings(entity, "MEPS", "PASSWORD");
        meps.put("bmepsProfileId", "02");
        fieldError(put(admin, "BANK_REFERENCE", body(meps, null, null)), 400, "VALIDATION_ERROR",
                "settings.bmepsProfileId", "NOT_ALLOWED");
        // NOT_ALLOWED: campo que o schema não tem
        Map<String, Object> extra = bankSettings(entity, "MEPS", "PASSWORD");
        extra.put("frequency", "DAILY");
        fieldError(put(admin, "BANK_REFERENCE", body(extra, null, null)), 400, "VALIDATION_ERROR",
                "settings.frequency", "NOT_ALLOWED");
        // OUT_OF_RANGE
        Map<String, Object> port = bankSettings(entity, "MEPS", "PASSWORD");
        sftp(port).put("port", 70000);
        fieldError(put(admin, "BANK_REFERENCE", body(port, null, null)), 400, "VALIDATION_ERROR",
                "settings.sftp.port", "OUT_OF_RANGE");
        // DIRECTORY_CONFLICT
        Map<String, Object> dirs = bankSettings(entity, "MEPS", "PASSWORD");
        sftp(dirs).put("processedDir", "inbound");
        fieldError(put(admin, "BANK_REFERENCE", body(dirs, null, null)), 400, "VALIDATION_ERROR",
                "settings.sftp.processedDir", "DIRECTORY_CONFLICT");
        // MIN_GREATER_THAN_MAX
        Map<String, Object> limits = body(bankSettings(entity, "MEPS", "PASSWORD"), null, null);
        limits.put("minAmount", Map.of("amount", 5000, "currency", "MZN"));
        limits.put("maxAmount", Map.of("amount", 1000, "currency", "MZN"));
        fieldError(put(admin, "BANK_REFERENCE", limits), 400, "VALIDATION_ERROR", "maxAmount", "MIN_GREATER_THAN_MAX");
        // INVALID_PUBLIC_KEY
        fieldError(put(admin, "MPESA", body(mpesaSettings(), secrets("publicKey", "nao-e-uma-chave"), null)), 400,
                "VALIDATION_ERROR", "secrets.publicKey", "INVALID_PUBLIC_KEY");
        // segredos: outro campo → NOT_ALLOWED; vazio → REQUIRED
        fieldError(put(admin, "MPESA", body(mpesaSettings(), secrets("sftp.password", "x"), null)), 400,
                "VALIDATION_ERROR", "secrets.sftp.password", "NOT_ALLOWED");
        fieldError(put(admin, "MPESA", body(mpesaSettings(), secrets("apiKey", ""), null)), 400,
                "VALIDATION_ERROR", "secrets.apiKey", "REQUIRED");
        // pedido: intentValidityMinutes e sortOrder
        Map<String, Object> request = body(mpesaSettings(), null, null);
        request.put("intentValidityMinutes", 2);
        request.put("sortOrder", 100);
        Response r = put(admin, "MPESA", request);
        fieldError(r, 400, "VALIDATION_ERROR", "intentValidityMinutes", "OUT_OF_RANGE");
        fieldError(r, 400, "VALIDATION_ERROR", "sortOrder", "OUT_OF_RANGE");
        // nada foi gravado
        list(admin).then().body("methods.configured", contains(false, false));
        assertThat(secretStore.exists(path("MPESA", "apiKey"))).isFalse();

        // REQUIRED de bmepsCompanyId só ao activar um BMEPS
        Map<String, Object> bmeps = bankSettings(entity, "BMEPS", "PASSWORD");
        bmeps.remove("bmepsCompanyId");
        put(admin, "BANK_REFERENCE", body(bmeps, secrets("sftp.password", "p"), null)).then().statusCode(200);
        fieldError(action(admin, "BANK_REFERENCE", "enable", 0L), 409, "PAYMENT_METHOD_INCOMPLETE",
                "settings.bmepsCompanyId", "REQUIRED");

        // REQUIRED de processedDir/errorDir ao activar com mudança de pasta
        Map<String, Object> noDirs = bankSettings(entity, "MEPS", "PASSWORD");
        sftp(noDirs).remove("processedDir");
        sftp(noDirs).remove("errorDir");
        put(admin, "BANK_REFERENCE", body(noDirs, null, 0L)).then().statusCode(200);
        Response incomplete = action(admin, "BANK_REFERENCE", "enable", 1L);
        fieldError(incomplete, 409, "PAYMENT_METHOD_INCOMPLETE", "settings.sftp.processedDir", "REQUIRED");
        fieldError(incomplete, 409, "PAYMENT_METHOD_INCOMPLETE", "settings.sftp.errorDir", "REQUIRED");

        // sem mudança de pasta, activa sem processedDir nem errorDir
        sftp(noDirs).put("moveAfterProcessing", false);
        put(admin, "BANK_REFERENCE", body(noDirs, null, 1L)).then().statusCode(200);
        action(admin, "BANK_REFERENCE", "enable", 2L).then().statusCode(200)
                .body("enabled", equalTo(true))
                .body("settings.sftp.moveAfterProcessing", equalTo(false));
        assertThat(support.owner().queryForObject("""
                select enabled_at is not null from payment.insurer_payment_method
                 where insurer_id = ? and method = 'BANK_REFERENCE'""", Boolean.class, insurer.id())).isTrue();
    }

    @Test
    void enableRequiresTheSecretsAndIsAStateTransition() {
        put(admin, "MPESA", body(mpesaSettings(), secrets("apiKey", "k"), null)).then().statusCode(200);
        Response incomplete = action(admin, "MPESA", "enable", 0L);
        fieldError(incomplete, 409, "PAYMENT_METHOD_INCOMPLETE", "secrets.publicKey", "REQUIRED");
        incomplete.then().body("errors.field", contains("secrets.publicKey"));

        put(admin, "MPESA", body(mpesaSettings(), secrets("publicKey", publicKeyPem()), 0L)).then().statusCode(200);
        action(admin, "MPESA", "enable", 1L).then().statusCode(200)
                .body("enabled", equalTo(true))
                .body("version", equalTo(2));
        action(admin, "MPESA", "enable", 2L).then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));

        // um método activo pode ser alterado (continua activo)
        Map<String, Object> changed = mpesaSettings();
        changed.put("c2bTimeoutSeconds", 120);
        put(admin, "MPESA", body(changed, null, 2L)).then().statusCode(200)
                .body("enabled", equalTo(true))
                .body("settings.c2bTimeoutSeconds", equalTo(120));
        assertThat(lookups.enabledConfig(insurer.id(), PaymentMethod.MPESA)).hasValueSatisfying(c -> {
            assertThat(c.settings().path("c2bTimeoutSeconds").asInt()).isEqualTo(120);
            assertThat(c.secretRef(mz.co.moseguros.payment.domain.SecretField.MPESA_API_KEY))
                    .hasValueSatisfying(ref -> assertThat(ref.path()).isEqualTo(path("MPESA", "apiKey")));
        });

        action(admin, "MPESA", "disable", 3L).then().statusCode(200).body("enabled", equalTo(false));
        action(admin, "MPESA", "disable", 4L).then().statusCode(409).body("code", equalTo("INVALID_STATE_TRANSITION"));
        assertThat(lookups.enabledConfig(insurer.id(), PaymentMethod.MPESA)).isEmpty();

        assertThat(support.owner().queryForList("""
                select action from audit.audit_event where insurer_id = ? and entity_type = 'InsurerPaymentMethod'
                 order by occurred_at""", String.class, insurer.id()))
                .containsExactly("PAYMENT_METHOD.UPDATE", "PAYMENT_METHOD.UPDATE", "PAYMENT_METHOD.ENABLE",
                        "PAYMENT_METHOD.UPDATE", "PAYMENT_METHOD.DISABLE");
    }

    @Test
    void changingAuthTypeDeletesTheSecretThatNoLongerApplies() {
        Map<String, Object> settings = bankSettings(randomEntity(), "MEPS", "PASSWORD");
        put(admin, "BANK_REFERENCE", body(settings, secrets("sftp.password", "pass-1"), null)).then().statusCode(200)
                .body("secrets.'sftp.password'.configured", equalTo(true))
                .body("secrets.'sftp.password'.required", equalTo(true))
                .body("secrets.'sftp.privateKey'.required", equalTo(false));
        assertThat(secretStore.exists(path("BANK_REFERENCE", "sftp.password"))).isTrue();

        sftp(settings).put("authType", "KEY");
        put(admin, "BANK_REFERENCE", body(settings, secrets("sftp.privateKey", "-----BEGIN OPENSSH PRIVATE KEY-----"),
                0L)).then().statusCode(200)
                .body("secrets.'sftp.password'.configured", equalTo(false))
                .body("secrets.'sftp.privateKey'.configured", equalTo(true))
                .body("secrets.'sftp.privateKey'.required", equalTo(true));
        assertThat(secretStore.exists(path("BANK_REFERENCE", "sftp.password"))).isFalse();
        assertThat(support.owner().queryForObject("""
                select jsonb_exists(secret_refs, 'sftp.password') from payment.insurer_payment_method
                 where insurer_id = ? and method = 'BANK_REFERENCE'""", Boolean.class, insurer.id())).isFalse();

        // a palavra-passe já não se aplica com KEY
        fieldError(put(admin, "BANK_REFERENCE", body(settings, secrets("sftp.password", "x"), 1L)), 400,
                "VALIDATION_ERROR", "secrets.sftp.password", "NOT_ALLOWED");
    }

    @Test
    void staleVersionIsAConcurrentModification() {
        put(admin, "MPESA", body(mpesaSettings(), null, null)).then().statusCode(200);
        put(admin, "MPESA", body(mpesaSettings(), null, null)).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        Map<String, Object> changed = mpesaSettings();
        changed.put("c2bTimeoutSeconds", 120);
        put(admin, "MPESA", body(changed, null, 0L)).then().statusCode(200).body("version", equalTo(1));
        put(admin, "MPESA", body(mpesaSettings(), null, 0L)).then().statusCode(409)
                .body("code", equalTo("CONCURRENT_MODIFICATION"));
        action(admin, "MPESA", "enable", 0L).then().statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));
        put(admin, "BANK_REFERENCE", body(bankSettings(randomEntity(), "MEPS", "PASSWORD"), null, 5L)).then()
                .statusCode(409).body("code", equalTo("CONCURRENT_MODIFICATION"));
    }

    @Test
    void testOfBankReferenceChecksTheSavedConfiguration() {
        put(admin, "BANK_REFERENCE", body(bankSettings(randomEntity(), "MEPS", "PASSWORD"), null, null))
                .then().statusCode(200);
        test(admin, "BANK_REFERENCE").then().statusCode(200)
                .body("result", equalTo("FAILED"))
                .body("message", equalTo("SECRET_MISSING"))
                .body("testedAt", notNullValue());

        put(admin, "BANK_REFERENCE", body(bankSettings(randomEntity(), "MEPS", "PASSWORD"),
                secrets("sftp.password", "pass"), version(admin, "BANK_REFERENCE"))).then().statusCode(200);
        test(admin, "BANK_REFERENCE").then().statusCode(200)
                .body("result", equalTo("OK"))
                .body("message", equalTo("CONFIG_VALID"));
        list(admin).then().statusCode(200)
                .body("methods[1].lastTest.result", equalTo("OK"))
                .body("methods[1].lastTest.message", equalTo("CONFIG_VALID"))
                .body("methods[1].lastTest.at", notNullValue());

        Map<String, Object> partial = bankSettings(randomEntity(), "MEPS", "PASSWORD");
        partial.remove("fileFormat");
        put(admin, "BANK_REFERENCE", body(partial, null, version(admin, "BANK_REFERENCE"))).then().statusCode(200);
        test(admin, "BANK_REFERENCE").then().statusCode(200)
                .body("result", equalTo("FAILED"))
                .body("message", equalTo("SETTINGS_INVALID"));

        // o tester do M-Pesa entra no S5-04
        put(admin, "MPESA", body(mpesaSettings(), null, null)).then().statusCode(200);
        test(admin, "MPESA").then().statusCode(200).body("result", equalTo("NOT_SUPPORTED"));

        assertThat(support.owner().queryForList("""
                select after_state ->> 'result' from audit.audit_event
                 where insurer_id = ? and action = 'PAYMENT_METHOD.TEST' order by occurred_at""", String.class,
                insurer.id())).containsExactly("FAILED", "OK", "FAILED", "NOT_SUPPORTED");
    }

    @Test
    void anInsurerNeverSeesNorChangesTheMethodsOfAnother() {
        String entity = randomEntity();
        put(admin, "BANK_REFERENCE", body(bankSettings(entity, "MEPS", "PASSWORD"),
                secrets("sftp.password", "pass-a"), null)).then().statusCode(200);
        put(admin, "MPESA", body(mpesaSettings(), secrets("apiKey", "key-a"), null)).then().statusCode(200);

        Insurer other = support.insurer("Seg Pagamentos B " + UUID.randomUUID().toString().substring(0, 4));
        String otherAdmin = support.staffToken(other.id(), "INSURER_ADMIN");
        list(otherAdmin).then().statusCode(200).body("methods.configured", contains(false, false));

        // ENTITY_IN_USE funciona apesar do RLS (B tenta a entidade de A)
        fieldError(put(otherAdmin, "BANK_REFERENCE", body(bankSettings(entity, "MEPS", "PASSWORD"), null, null)), 400,
                "VALIDATION_ERROR", "settings.entity", "ENTITY_IN_USE");

        // o PUT de B cria a linha de B e não toca na de A
        Map<String, Object> mpesaB = mpesaSettings();
        mpesaB.put("serviceProviderCode", "999999");
        put(otherAdmin, "MPESA", body(mpesaB, secrets("apiKey", "key-b"), null)).then().statusCode(200)
                .body("version", equalTo(0));
        list(admin).then().statusCode(200)
                .body("methods[0].settings.serviceProviderCode", equalTo("171717"))
                .body("methods[0].version", equalTo(0));
        assertThat(secretStore.get(path("MPESA", "apiKey"))).contains("key-a");
        assertThat(secretStore.get("secret/insurers/" + other.id() + "/payments/MPESA/apiKey")).contains("key-b");
        assertThat(support.owner().queryForObject(
                "select count(*) from payment.insurer_payment_method where insurer_id in (?, ?)", Integer.class,
                insurer.id(), other.id())).isEqualTo(3);

        // directamente no repositório, em ORG de B: 0 linhas de A
        TenantContext.set(TenantSnapshot.org(UUID.randomUUID(), other.id(), other.id()));
        List<InsurerPaymentMethod> ofA = tx.execute(s -> repository.findByInsurerId(insurer.id()));
        assertThat(ofA).isEmpty();
        List<InsurerPaymentMethod> visibleToB = tx.execute(s -> repository.findAll());
        assertThat(visibleToB)
                .isNotEmpty()
                .allSatisfy(m -> assertThat(m.getInsurerId()).isEqualTo(other.id()));
        // em CUSTOMER não se vê nenhuma
        TenantContext.set(TenantSnapshot.customer(UUID.randomUUID()));
        List<InsurerPaymentMethod> ofAAsCustomer = tx.execute(s -> repository.findByInsurerId(insurer.id()));
        assertThat(ofAAsCustomer).isEmpty();
        // o PaymentSystemLookups (SYSTEM) filtra pela seguradora pedida
        assertThat(lookups.entityInUse(entity, other.id())).isTrue();
        assertThat(lookups.entityInUse(entity, insurer.id())).isFalse();
        TenantContext.clear();
    }

    @Test
    void permissions() {
        String finance = support.staffToken(insurer.id(), "FINANCE");
        put(finance, "MPESA", body(mpesaSettings(), null, null)).then().statusCode(200);
        list(finance).then().statusCode(200).body("methods[0].configured", equalTo(true));

        String customer = support.customerToken();
        String broker = support.staffToken(fixtures.organization(OrganizationType.BROKER, "Corretora Pagamentos").getId(),
                "BROKER");
        for (String token : List.of(customer, broker, insurer.underwriter(), insurer.manager())) {
            list(token).then().statusCode(403).body("code", equalTo("FORBIDDEN"));
            put(token, "MPESA", body(mpesaSettings(), null, 0L)).then().statusCode(403);
            action(token, "MPESA", "enable", 0L).then().statusCode(403);
            test(token, "MPESA").then().statusCode(403);
        }
        given().get(METHODS).then().statusCode(401);
        list(admin).then().statusCode(200).body("methods.method", hasItem("MPESA"));
    }
}
