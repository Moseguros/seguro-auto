package mz.co.moseguros.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * {@code spring.flyway.out-of-order: true} (todos os perfis; modelo transversal §6): numa BD já
 * migrada até {@code V202612010101}, uma migração com carimbo menor que chega depois é aplicada.
 */
class FlywayOutOfOrderIT {

    private PostgreSQLContainer<?> postgres;

    @BeforeEach
    void start() {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withInitScript("db/init-roles.sql");
        postgres.start();
    }

    @AfterEach
    void stop() {
        postgres.stop();
    }

    @Test
    void applicationConfigEnablesOutOfOrderInAllProfiles() {
        assertThat(DevSeedIT.yaml("application.yml").getProperty("spring.flyway.out-of-order")).isEqualTo("true");
        for (String profile : new String[] {"-local", "-test", "-staging", "-prod"}) {
            assertThat(DevSeedIT.yaml("application" + profile + ".yml").getProperty("spring.flyway.out-of-order"))
                    .as(profile).isNull(); // nenhum perfil o desliga
        }
    }

    @Test
    void aLaterArrivingMigrationWithASmallerStampIsApplied() {
        boolean outOfOrder = Boolean.parseBoolean(
                DevSeedIT.yaml("application.yml").getProperty("spring.flyway.out-of-order"));

        MigrateResult first = flyway(outOfOrder, "202612010101", "classpath:db/migration").migrate();
        assertThat(first.success).isTrue();
        assertThat(first.targetSchemaVersion).isEqualTo("202612010101");

        MigrateResult second = flyway(outOfOrder, "202612010101", "classpath:db/migration",
                "classpath:db/out-of-order").migrate();
        assertThat(second.success).isTrue();
        assertThat(second.migrationsExecuted).isEqualTo(1);
        assertThat(new JdbcTemplate(new DriverManagerDataSource(postgres.getJdbcUrl(), postgres.getUsername(),
                postgres.getPassword())).queryForObject(
                "select success from public.flyway_schema_history where version = '202611300000'", Boolean.class))
                .isTrue();
    }

    @Test
    void withoutOutOfOrderTheSameMigrationIsRefused() {
        flyway(false, "202612010101", "classpath:db/migration").migrate();
        assertThatThrownBy(() -> flyway(false, "202612010101", "classpath:db/migration",
                "classpath:db/out-of-order").migrate())
                .hasMessageContaining("202611300000");
    }

    private Flyway flyway(boolean outOfOrder, String target, String... locations) {
        return Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .defaultSchema("public")
                .outOfOrder(outOfOrder)
                .target(target)
                .locations(locations)
                .load();
    }
}
