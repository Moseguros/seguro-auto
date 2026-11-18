package mz.co.moseguros;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.util.StringUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Postgres real para os testes de integração ({@code *IT}).
 *
 * <p>Como em produção, a aplicação liga-se com {@code moseguros_app_user} (sem privilégios de
 * dono, o RLS aplica-se a ela) e o Flyway com o dono das tabelas (o utilizador do contentor).
 * O {@code db/init-roles.sql} cria os papéis antes das migrações (ADR-002, adenda).
 *
 * <p>Também um RabbitMQ real (S5-06), onde o backend declara a topologia dos pagamentos.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    public static final String APP_USER = "moseguros_app_user";
    public static final String APP_PASSWORD = "moseguros_app";
    public static final String RABBITMQ_IMAGE = "rabbitmq:4.1-management";

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
     * RabbitMQ real (S5-06, decisão D19): a mesma imagem do {@code infra/docker-compose.yml}. Um
     * por contexto Spring, partilhado pelos testes que reutilizam o contexto, como o Postgres.
     */
    @Bean
    RabbitMQContainer rabbitmq(DynamicPropertyRegistry registry) {
        var c = new RabbitMQContainer(DockerImageName.parse(RABBITMQ_IMAGE));
        registry.add("spring.rabbitmq.host", c::getHost);
        registry.add("spring.rabbitmq.port", c::getAmqpPort);
        registry.add("spring.rabbitmq.username", c::getAdminUsername);
        registry.add("spring.rabbitmq.password", c::getAdminPassword);
        return c;
    }

    /**
     * Sem {@code @ServiceConnection}, nada obriga os contentores a serem criados antes da
     * {@code DataSource}, do Flyway e da ligação ao RabbitMQ; sem isto as propriedades acima ainda
     * não existem quando eles são criados e a aplicação liga-se a {@code localhost:5432} (ou
     * {@code localhost:5672}).
     */
    @Bean
    static BeanFactoryPostProcessor postgresBeforeDataSource() {
        return beanFactory -> {
            dependOn(beanFactory, "postgres", "dataSource", "flyway");
            dependOn(beanFactory, "rabbitmq", "rabbitConnectionFactory");
        };
    }

    private static void dependOn(ConfigurableListableBeanFactory beanFactory, String container, String... beans) {
        for (String name : beans) {
            if (beanFactory.containsBeanDefinition(name)) {
                BeanDefinition definition = beanFactory.getBeanDefinition(name);
                String[] dependsOn = definition.getDependsOn();
                definition.setDependsOn(StringUtils.addStringToArray(dependsOn, container));
            }
        }
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
