package mz.co.moseguros;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.util.StringUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Postgres real para os testes de integração ({@code *IT}).
 *
 * <p>Como em produção, a aplicação liga-se com {@code moseguros_app_user} (sem privilégios de
 * dono, o RLS aplica-se a ela) e o Flyway com o dono das tabelas (o utilizador do contentor).
 * O {@code db/init-roles.sql} cria os papéis antes das migrações (ADR-002, adenda).
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    public static final String APP_USER = "moseguros_app_user";
    public static final String APP_PASSWORD = "moseguros_app";

    @Bean
    PostgreSQLContainer<?> postgres(DynamicPropertyRegistry registry) {
        var c = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withInitScript("db/init-roles.sql");
        registry.add("spring.datasource.url", c::getJdbcUrl);
        registry.add("spring.datasource.username", () -> APP_USER);
        registry.add("spring.datasource.password", () -> APP_PASSWORD);
        registry.add("spring.flyway.url", c::getJdbcUrl);
        registry.add("spring.flyway.user", c::getUsername);      // dono
        registry.add("spring.flyway.password", c::getPassword);
        return c;
    }

    /**
     * Sem {@code @ServiceConnection}, nada obriga o contentor a ser criado antes da
     * {@code DataSource} e do Flyway; sem isto as propriedades acima ainda não existem quando
     * eles são criados e a aplicação liga-se a {@code localhost:5432}.
     */
    @Bean
    static BeanFactoryPostProcessor postgresBeforeDataSource() {
        return beanFactory -> {
            for (String name : new String[] {"dataSource", "flyway"}) {
                if (beanFactory.containsBeanDefinition(name)) {
                    BeanDefinition definition = beanFactory.getBeanDefinition(name);
                    String[] dependsOn = definition.getDependsOn();
                    definition.setDependsOn(StringUtils.addStringToArray(dependsOn, "postgres"));
                }
            }
        };
    }

    /**
     * Ligação como dono das tabelas (o utilizador do contentor, superutilizador), para os testes
     * prepararem dados ou lerem colunas em bruto sem passar pelo RLS.
     */
    public static JdbcTemplate ownerJdbc(PostgreSQLContainer<?> container) {
        return new JdbcTemplate(new DriverManagerDataSource(
                container.getJdbcUrl(), container.getUsername(), container.getPassword()));
    }
}
