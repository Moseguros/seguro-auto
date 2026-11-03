package mz.co.moseguros.shared.tenant;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link JpaTransactionManager} que, ao começar cada transacção, define na mesma ligação o
 * âmbito do {@link TenantContext} ({@code app.scope}, {@code app.user_id}, {@code app.org_id},
 * {@code app.insurer_id}), locais à transacção. É o {@code transactionManager} principal
 * ({@link TenantConfig}).
 */
public class TenantJpaTransactionManager extends JpaTransactionManager {

    public TenantJpaTransactionManager(EntityManagerFactory emf) {
        super(emf);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        super.doBegin(transaction, definition);
        EntityManagerHolder holder = (EntityManagerHolder)
                TransactionSynchronizationManager.getResource(obtainEntityManagerFactory());
        TenantSettingsApplier.apply(holder.getEntityManager());
    }
}
