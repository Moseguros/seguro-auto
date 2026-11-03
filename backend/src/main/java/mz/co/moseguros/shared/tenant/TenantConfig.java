package mz.co.moseguros.shared.tenant;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Regista o {@link TenantJpaTransactionManager} como {@code transactionManager} principal
 * (o do Spring Boot deixa de ser criado) e o aspecto de {@link RunAsSystem}.
 */
@Configuration(proxyBeanMethods = false)
public class TenantConfig {

    @Bean
    @Primary
    PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory,
                                                  ObjectProvider<TransactionManagerCustomizers> customizers) {
        TenantJpaTransactionManager transactionManager = new TenantJpaTransactionManager(entityManagerFactory);
        customizers.ifAvailable(c -> c.customize(transactionManager));
        return transactionManager;
    }

    @Bean
    RunAsSystemAspect runAsSystemAspect(ObjectProvider<EntityManagerFactory> entityManagerFactory) {
        return new RunAsSystemAspect(entityManagerFactory);
    }
}
