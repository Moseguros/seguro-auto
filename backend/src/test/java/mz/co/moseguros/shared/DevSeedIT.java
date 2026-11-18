package mz.co.moseguros.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.domain.tariff.CoverageDefinition;
import mz.co.moseguros.rating.domain.tariff.ProductDefinition;
import mz.co.moseguros.rating.domain.tariff.RatingRulesValidator;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Dados de demonstração (S2-01 §9): com as {@code spring.flyway.locations} do perfil {@code local}
 * as migrações aplicam duas vezes seguidas sem erro, e o script repetível é idempotente (correr
 * outra vez não duplica nada).
 */
class DevSeedIT {

    static final String ID = "01930000-0000-7000-8000-00000000000";

    private static PostgreSQLContainer<?> postgres;

    @BeforeAll
    static void start() {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withInitScript("db/init-roles.sql");
        postgres.start();
    }

    @AfterAll
    static void stop() {
        if (postgres != null) {
            postgres.stop();
        }
    }

    @Test
    void localLocationsMigrateTwiceAndTheSevenRecordsExistOnce() throws IOException {
        String[] locations = localFlywayLocations();
        assertThat(locations).containsExactly("classpath:db/migration", "classpath:db/dev");

        assertThat(flyway(locations).migrate().success).isTrue();
        assertThat(flyway(locations).migrate().success).isTrue();
        assertSevenRecordsOnce();
        assertDemoProductsOnce();
        assertRatingDemoOnce();
        assertPaymentDemoOnce();

        // os scripts repetíveis voltam a correr (como quando mudam) sem erro nem duplicados
        owner().execute(devSeedSql("R__dev_seed_s2.sql"));
        owner().execute(devSeedSql("R__dev_seed_s3.sql"));
        owner().execute(devSeedSql("R__dev_seed_s5.sql"));
        assertSevenRecordsOnce();
        assertDemoProductsOnce();
        assertRatingDemoOnce();
        assertPaymentDemoOnce();

        for (String script : new String[] {"R__dev_seed_s2.sql", "R__dev_seed_s3.sql", "R__dev_seed_s5.sql"}) {
            assertThat(owner().queryForObject("select count(*) from public.flyway_schema_history where script = ?",
                    Integer.class, script)).as(script).isEqualTo(1);
        }
    }

    @Test
    void otherProfilesDoNotIncludeTheDevSeed() throws IOException {
        for (String profile : new String[] {"", "-test", "-staging", "-prod"}) {
            Object locations = yaml("application" + profile + ".yml").get("spring.flyway.locations");
            assertThat(locations).as(profile).isNull();
        }
    }

    private void assertSevenRecordsOnce() {
        JdbcTemplate owner = owner();
        List<Map<String, Object>> counts = owner.queryForList("""
                select 'organization' as t, count(*) as n from organization.organization where id in (?::uuid, ?::uuid)
                union all select 'insurer_profile', count(*) from organization.insurer_profile where id = ?::uuid
                union all select 'invitation', count(*) from identity.invitation where id in (?::uuid, ?::uuid)
                union all select 'user_account', count(*) from identity.user_account where id = ?::uuid
                union all select 'customer_profile', count(*) from identity.customer_profile where id = ?::uuid""",
                id(1), id(3), id(2), id(4), id(5), id(6), id(7));
        assertThat(counts).extracting(r -> r.get("t") + "=" + r.get("n")).containsExactly(
                "organization=2", "insurer_profile=1", "invitation=2", "user_account=1", "customer_profile=1");
        assertThat(owner.queryForObject("select count(*) from organization.organization where nuit in ('400000001','400000002')",
                Integer.class)).isEqualTo(2);
        assertThat(owner.queryForObject("select count(*) from identity.user_role where user_id = ?::uuid and role_code = 'CUSTOMER'",
                Integer.class, id(6))).isEqualTo(1);
        assertThat(owner.queryForObject("select token_hash from identity.invitation where id = ?::uuid", String.class, id(4)))
                .isEqualTo(sha256Hex("dev-convite-gestor-demo"));
        assertThat(owner.queryForObject("select short_code from organization.insurer_profile where id = ?::uuid",
                String.class, id(2))).isEqualTo("DEMO");
        // S2-03: veículo AAA-100-MC do cliente de teste, verificado pelo registo simulado
        assertThat(owner.queryForList("""
                select plate || '/' || owner_user_id || '/' || (inatro_verified_at is not null) as v
                  from vehicle.vehicle where id = '01930000-0000-7000-8000-000000000101'""", String.class))
                .containsExactly("AAA-100-MC/" + id(6) + "/true");
    }

    /** S2-04: os dois produtos publicados da Seguradora Demo, com as coberturas e os PDFs em pt. */
    private void assertDemoProductsOnce() throws IOException {
        JdbcTemplate owner = owner();
        assertThat(owner.queryForList("""
                select p.code || ':' || p.type || ':' || p.status || ':' || v.version_no || ':' || v.status || ':'
                       || v.valid_from || ':' || (select count(*) from catalog.coverage c where c.product_version_id = v.id)
                       || ':' || (select count(*) from catalog.product_document d where d.product_version_id = v.id)
                  from catalog.product p join catalog.product_version v on v.product_id = p.id
                 where p.insurer_id = ?::uuid order by p.code""", String.class, id(1)))
                .containsExactly("AUTO-COMPLETO:COMPREHENSIVE:PUBLISHED:1:PUBLISHED:2026-12-01:9:2",
                        "RC-AUTO:MTPL_ONLY:PUBLISHED:1:PUBLISHED:2026-12-01:1:2",
                        "TEMP-AUTO:TEMPORARY:PUBLISHED:1:PUBLISHED:2026-12-15:1:2");
        byte[] placeholder;
        try (InputStream in = new ClassPathResource("db/dev/files/dev-placeholder.pdf").getInputStream()) {
            placeholder = in.readAllBytes();
        }
        List<Map<String, Object>> files = owner.queryForList("""
                select f.storage_key, f.size_bytes, f.sha256, f.linked_at is not null as linked
                  from document.file f join catalog.product_document d on d.file_id = f.id
                 where d.insurer_id = ?::uuid and f.owner_type = 'PRODUCT_VERSION'""", id(1));
        // S3-01: os documentos do TEMP-AUTO são os ficheiros do RC-AUTO
        assertThat(files).hasSize(6).allSatisfy(f -> {
            assertThat(f.get("size_bytes")).isEqualTo((long) placeholder.length);
            assertThat(f.get("sha256")).isEqualTo(java.util.HexFormat.of().formatHex(sha256(placeholder)));
            assertThat(f.get("linked")).isEqualTo(true);
            String name = ((String) f.get("storage_key")).substring("dev-seed/".length());
            assertThat(new ClassPathResource("db/dev/files/" + name).exists()).as(name).isTrue();
        });
    }

    /**
     * S3-01: encargos das duas seguradoras de demonstração, a Seguradora Demo B e os produtos
     * RC-AUTO, AUTO-COMPLETO e TEMP-AUTO das duas, publicados e com tarifa válida (as mesmas
     * validações do {@code validateRatingRules}).
     */
    private void assertRatingDemoOnce() throws IOException {
        JdbcTemplate owner = owner();
        String demoB = "01930000-0000-7000-8000-000000000320";
        assertThat(owner.queryForList("""
                select o.legal_name || '/' || o.trade_name || '/' || o.nuit || '/' || p.short_code || '/'
                       || p.rating_mode || '/' || jsonb_array_length(p.charges)
                  from organization.organization o join organization.insurer_profile p on p.organization_id = o.id
                 where o.id in (?::uuid, ?::uuid) order by o.nuit""", String.class, id(1), demoB))
                .containsExactly("Seguradora Demo, SA/Seguradora Demo/400000001/DEMO/INTERNAL/2",
                        "Seguradora Demo B, SA/Seguradora Demo B/400000010/DEMOB/INTERNAL/2");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode charges;
        try (InputStream in = new ClassPathResource("rating/reference/charges.json").getInputStream()) {
            charges = mapper.readTree(in);
        }
        for (String insurer : new String[] {id(1), demoB}) {
            assertThat(mapper.readTree(owner.queryForObject(
                    "select charges::text from organization.insurer_profile where organization_id = ?::uuid",
                    String.class, insurer))).as(insurer).isEqualTo(charges);
        }

        List<Map<String, Object>> versions = owner.queryForList("""
                select v.id, p.code, p.type, v.status, v.insurer_id, (v.rules -> 'rating')::text as rating
                  from catalog.product p join catalog.product_version v on v.product_id = p.id
                 where p.insurer_id in (?::uuid, ?::uuid) order by p.insurer_id, p.code""", id(1), demoB);
        assertThat(versions).extracting(v -> v.get("code") + ":" + v.get("status")).containsExactly(
                "AUTO-COMPLETO:PUBLISHED", "RC-AUTO:PUBLISHED", "TEMP-AUTO:PUBLISHED",
                "AUTO-COMPLETO:PUBLISHED", "RC-AUTO:PUBLISHED", "TEMP-AUTO:PUBLISHED");
        for (Map<String, Object> v : versions) {
            String label = v.get("insurer_id") + " " + v.get("code");
            assertThat(v.get("rating")).as(label).isNotNull();
            JsonNode rating = mapper.readTree((String) v.get("rating"));
            ProductDefinition product = productDefinition(owner, (UUID) v.get("id"), (String) v.get("type"));
            assertThat(RatingRulesValidator.validate(rating, product)).as(label).isEmpty();
            boolean demoBProduct = demoB.equals(v.get("insurer_id").toString());
            rating.get("coverages").forEach(c -> {
                JsonNode factors = c.path("factors");
                boolean commercialLast = factors.size() > 0
                        && "COMMERCIAL".equals(factors.get(factors.size() - 1).get("code").asText());
                assertThat(commercialLast).as(label + " " + c.get("coverageCode")).isEqualTo(demoBProduct);
            });
        }
        assertThat(owner.queryForObject("""
                select count(*) from catalog.product_document d
                  join catalog.product_version v on v.id = d.product_version_id
                 where v.insurer_id = ?::uuid""", Integer.class, demoB)).isEqualTo(6);
        assertThat(owner.queryForObject("select count(*) from identity.user_account where organization_id = ?::uuid",
                Integer.class, demoB)).isZero();
    }

    /** Coberturas de uma versão lidas da BD, como o {@code ProductDefinition.from(CatalogApi…)}. */
    private static ProductDefinition productDefinition(JdbcTemplate owner, UUID versionId, String type) {
        List<CoverageDefinition> coverages = owner.query("""
                select code, mandatory, array_to_string(requires_codes, ',') as requires, capital_type,
                       capital_amount, array_to_string(capital_options_amount, ',') as options, deductible_type,
                       deductible_amount, deductible_percent, deductible_min_amount
                  from catalog.coverage where product_version_id = ? order by sort_order""", (rs, n) ->
                new CoverageDefinition(CoverageCode.valueOf(rs.getString("code")), rs.getBoolean("mandatory"),
                        Arrays.stream(rs.getString("requires").split(",")).filter(x -> !x.isBlank())
                                .map(CoverageCode::valueOf).toList(),
                        CapitalType.valueOf(rs.getString("capital_type")), rs.getObject("capital_amount", Long.class),
                        rs.getString("options") == null ? null
                                : Arrays.stream(rs.getString("options").split(",")).map(Long::valueOf).toList(),
                        DeductibleType.valueOf(rs.getString("deductible_type")),
                        rs.getObject("deductible_amount", Long.class), rs.getBigDecimal("deductible_percent"),
                        rs.getObject("deductible_min_amount", Long.class)), versionId);
        return new ProductDefinition(ProductType.valueOf(type), coverages);
    }

    private static byte[] sha256(byte[] value) {
        try {
            return java.security.MessageDigest.getInstance("SHA-256").digest(value);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * S5-01: as formas de pagamento das seguradoras de demonstração, activas, com os dados completos
     * (schema do método) e só os caminhos dos segredos.
     */
    private void assertPaymentDemoOnce() throws IOException {
        JdbcTemplate owner = owner();
        String demoB = "01930000-0000-7000-8000-000000000320";
        List<Map<String, Object>> rows = owner.queryForList("""
                select insurer_id::text as insurer, method, enabled, enabled_at is not null as enabled_at,
                       settings::text as settings, secret_refs::text as refs, created_by
                  from payment.insurer_payment_method where insurer_id in (?::uuid, ?::uuid)
                 order by insurer_id, method desc""", id(1), demoB);
        assertThat(rows).extracting(r -> r.get("insurer") + " " + r.get("method") + " " + r.get("enabled") + " "
                + r.get("enabled_at") + " " + r.get("created_by")).containsExactly(
                id(1) + " MPESA true true dev-seed",
                id(1) + " BANK_REFERENCE true true dev-seed",
                demoB + " BANK_REFERENCE true true dev-seed");
        ObjectMapper mapper = new ObjectMapper();
        for (Map<String, Object> r : rows) {
            JsonNode settings = mapper.readTree((String) r.get("settings"));
            String method = (String) r.get("method");
            String schema = "payment-method-" + method.toLowerCase(java.util.Locale.ROOT) + ".json";
            assertThat(mz.co.moseguros.shared.json.JsonSchemas.validate(schema, settings)).as(method).isEmpty();
            JsonNode refs = mapper.readTree((String) r.get("refs"));
            List<String> fields = new java.util.ArrayList<>();
            refs.fieldNames().forEachRemaining(fields::add);
            assertThat(fields).as(method).containsExactlyInAnyOrderElementsOf(
                    method.equals("MPESA") ? List.of("apiKey", "publicKey") : List.of("sftp.password"));
            refs.fields().forEachRemaining(e -> {
                assertThat(e.getValue().get("path").asText())
                        .isEqualTo("secret/insurers/" + r.get("insurer") + "/payments/" + method + "/" + e.getKey());
                assertThat(java.time.Instant.parse(e.getValue().get("updatedAt").asText())).isNotNull();
            });
        }
        assertThat(owner.queryForList("""
                select settings ->> 'entity' || '/' || (settings ->> 'fileFormat') || '/'
                       || coalesce(settings ->> 'bmepsCompanyId', '-') || '/' || coalesce(settings ->> 'bmepsProfileId', '-')
                       || '/' || (settings -> 'sftp' ->> 'username')
                  from payment.insurer_payment_method where method = 'BANK_REFERENCE' order by settings ->> 'entity'""",
                String.class)).containsExactly("90002/MEPS/-/-/demo", "90090/BMEPS/800/02/demob");
        assertThat(owner.queryForObject("""
                select settings ->> 'serviceProviderCode' || '/' || (settings ->> 'host')
                  from payment.insurer_payment_method where method = 'MPESA' and insurer_id = ?::uuid""",
                String.class, id(1))).isEqualTo("171717/http://mpesa-mock");
    }

    private static String id(int n) {
        return ID + n;
    }

    private static String sha256Hex(String value) {
        try {
            return java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Flyway flyway(String[] locations) {
        return Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .defaultSchema("public")
                .outOfOrder(true)
                .locations(locations)
                .load();
    }

    private static JdbcTemplate owner() {
        return new JdbcTemplate(new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(),
                postgres.getPassword()));
    }

    private static String devSeedSql(String script) throws IOException {
        try (InputStream in = new ClassPathResource("db/dev/" + script).getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static String[] localFlywayLocations() {
        Object value = yaml("application-local.yml").get("spring.flyway.locations");
        return value.toString().split("\\s*,\\s*");
    }

    static java.util.Properties yaml(String name) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource(name));
        return factory.getObject();
    }
}
