package mz.co.moseguros.shared.tenant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Implementa {@link RunAsSystem}. Corre antes do interceptor de transacções
 * ({@link Ordered#HIGHEST_PRECEDENCE}):
 * <ul>
 *   <li>sem transacção activa, a transacção que começar a seguir já nasce em {@code SYSTEM};</li>
 *   <li>com transacção activa, faz {@code flush()}, aplica {@code SYSTEM} na ligação, executa e,
 *       no fim, faz {@code flush()} e volta a aplicar o âmbito anterior.</li>
 * </ul>
 * Em {@code SYSTEM} mantém-se o {@code userId}; a organização e a seguradora ficam vazias.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RunAsSystemAspect {

    // Obtida só na primeira chamada: os aspectos são criados cedo no arranque.
    private final ObjectProvider<EntityManagerFactory> entityManagerFactory;

    public RunAsSystemAspect(ObjectProvider<EntityManagerFactory> entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Around("@annotation(mz.co.moseguros.shared.tenant.RunAsSystem)")
    public Object runAsSystem(ProceedingJoinPoint joinPoint) throws Throwable {
        TenantSnapshot previous = TenantContext.current();
        EntityManager entityManager = TransactionSynchronizationManager.isActualTransactionActive()
                ? EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory.getObject())
                : null;
        if (entityManager != null) {
            entityManager.flush();
        }
        TenantContext.set(TenantSnapshot.system(previous.userId()));
        try {
            if (entityManager != null) {
                TenantSettingsApplier.apply(entityManager);
            }
            return joinPoint.proceed();
        } finally {
            TenantContext.set(previous);
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.flush();
                TenantSettingsApplier.apply(entityManager);
            }
        }
    }
}
