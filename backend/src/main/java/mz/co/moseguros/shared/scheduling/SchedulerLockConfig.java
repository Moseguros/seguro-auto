package mz.co.moseguros.shared.scheduling;

import javax.sql.DataSource;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * O {@link LockProvider} do ShedLock (S6-01 §2): tabela {@code shared.shedlock}, com a hora da base de
 * dados (não depende do relógio de cada instância). Fica fora da condição do {@link SchedulingConfig}
 * para os testes o usarem com {@code moseguros.jobs.enabled=false}.
 */
@Configuration(proxyBeanMethods = false)
public class SchedulerLockConfig {

    /** Tabela dos locks. */
    public static final String TABLE = "shared.shedlock";

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName(TABLE)
                .usingDbTime()
                .build());
    }
}
