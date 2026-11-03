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

        // o script repetível volta a correr (como quando muda) sem erro nem duplicados
        owner().execute(devSeedSql());
        assertSevenRecordsOnce();

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
        // S2-03: veículo AAA-100-MC do cliente de teste, verificado pelo registo simulado
        assertThat(owner.queryForList("""
                select plate || '/' || owner_user_id || '/' || (inatro_verified_at is not null) as v
                  from vehicle.vehicle where id = '01930000-0000-7000-8000-000000000101'""", String.class))
                .containsExactly("AAA-100-MC/" + id(6) + "/true");
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
