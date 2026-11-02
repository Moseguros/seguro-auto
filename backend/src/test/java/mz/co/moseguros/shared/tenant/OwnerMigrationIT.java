package mz.co.moseguros.shared.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * As migrações correm com um dono que <b>não</b> é superutilizador (como em staging e produção,
 * onde os papéis são criados pela equipa de operações) e uma migração desse dono insere dados
 * em tabelas com RLS sem erro (as tabelas não têm {@code force row level security}).
 */
class OwnerMigrationIT {

    private static final String DB = "moseguros_ns";
    private static final String OWNER = "moseguros_owner";
    private static final String OWNER_PASSWORD = "owner";

    private static PostgreSQLContainer<?> postgres;

    @BeforeAll
    static void start() throws Exception {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withInitScript("db/init-roles.sql");
        postgres.start();
        try (Connection c = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(),
                postgres.getPassword()); Statement s = c.createStatement()) {
            s.execute("create role " + OWNER + " login password '" + OWNER_PASSWORD
                    + "' nosuperuser nocreaterole nocreatedb nobypassrls");
            s.execute("create database " + DB + " owner " + OWNER);
        }
    }

    @AfterAll
    static void stop() {
        if (postgres != null) {
            postgres.stop();
        }
    }

    @Test
    void nonSuperuserOwnerRunsAllMigrationsAndInsertsIntoTablesWithRls() {
        var result = Flyway.configure()
                .dataSource(url(), OWNER, OWNER_PASSWORD)
                .defaultSchema("public")
                .locations("classpath:db/migration", "classpath:db/testmigration")
                .load()
                .migrate();
        assertThat(result.success).isTrue();

        JdbcTemplate owner = new JdbcTemplate(new DriverManagerDataSource(url(), OWNER, OWNER_PASSWORD));
        assertThat(owner.queryForObject("select rolsuper from pg_roles where rolname = current_user", Boolean.class))
                .isFalse();
        assertThat(owner.queryForObject("select relrowsecurity from pg_class where oid = 'organization.organization'::regclass",
                Boolean.class)).isTrue();
        assertThat(owner.queryForObject("select count(*) from organization.organization", Integer.class)).isOne();
        assertThat(owner.queryForObject("select count(*) from identity.role", Integer.class)).isEqualTo(18);
    }

    @Test
    void applicationStillSeesTheRowsOnlyWithScope() throws Exception {
        nonSuperuserOwnerRunsAllMigrationsAndInsertsIntoTablesWithRls();
        try (Connection c = DriverManager.getConnection(url(), "moseguros_app_user", "moseguros_app")) {
            c.setAutoCommit(false);
            assertThat(count(c, "organization.organization")).isZero();
            try (Statement s = c.createStatement()) {
                s.execute("select set_config('app.scope', 'PLATFORM', true)");
            }
            assertThat(count(c, "organization.organization")).isOne();
            assertThat(count(c, "identity.user_account")).isOne();
            c.rollback();
        }
    }

    private static int count(Connection c, String table) throws Exception {
        try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("select count(*) from " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private static String url() {
        return postgres.getJdbcUrl().replace("/" + postgres.getDatabaseName(), "/" + DB);
    }
}
