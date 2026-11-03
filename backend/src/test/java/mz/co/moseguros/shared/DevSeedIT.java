package mz.co.moseguros.shared;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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

        // o script repetível volta a correr (como quando muda) sem erro nem duplicados
        owner().execute(devSeedSql());
        assertSevenRecordsOnce();
        assertDemoProductsOnce();

        assertThat(owner().queryForObject("select count(*) from public.flyway_schema_history where script = ?",
                Integer.class, "R__dev_seed_s2.sql")).isEqualTo(1);
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
                        "RC-AUTO:MTPL_ONLY:PUBLISHED:1:PUBLISHED:2026-12-01:1:2");
        byte[] placeholder;
        try (InputStream in = new ClassPathResource("db/dev/files/dev-placeholder.pdf").getInputStream()) {
            placeholder = in.readAllBytes();
        }
        List<Map<String, Object>> files = owner.queryForList("""
                select f.storage_key, f.size_bytes, f.sha256, f.linked_at is not null as linked
                  from document.file f join catalog.product_document d on d.file_id = f.id
                 where d.insurer_id = ?::uuid and f.owner_type = 'PRODUCT_VERSION'""", id(1));
        assertThat(files).hasSize(4).allSatisfy(f -> {
            assertThat(f.get("size_bytes")).isEqualTo((long) placeholder.length);
            assertThat(f.get("sha256")).isEqualTo(java.util.HexFormat.of().formatHex(sha256(placeholder)));
            assertThat(f.get("linked")).isEqualTo(true);
            String name = ((String) f.get("storage_key")).substring("dev-seed/".length());
            assertThat(new ClassPathResource("db/dev/files/" + name).exists()).as(name).isTrue();
        });
    }

    private static byte[] sha256(byte[] value) {
        try {
            return java.security.MessageDigest.getInstance("SHA-256").digest(value);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
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

    private static String devSeedSql() throws IOException {
        try (InputStream in = new ClassPathResource("db/dev/R__dev_seed_s2.sql").getInputStream()) {
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
