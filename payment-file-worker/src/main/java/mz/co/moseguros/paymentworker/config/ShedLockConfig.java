package mz.co.moseguros.paymentworker.config;

import javax.sql.DataSource;

import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Trincos por fonte (S5-09 §4): duas instâncias do worker nunca leem a mesma fonte ao mesmo tempo
 * ({@code source:{sourceId}} em {@code payment_worker.shedlock}, com a hora da BD).
 */
@Configuration(proxyBeanMethods = false)
public class ShedLockConfig {

    public static final String TABLE = "payment_worker.shedlock";

    @Bean
    LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName(TABLE)
                .usingDbTime()
                .build());
    }

    @Bean
    LockingTaskExecutor lockingTaskExecutor(LockProvider lockProvider) {
        return new DefaultLockingTaskExecutor(lockProvider);
    }
}
