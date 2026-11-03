package mz.co.moseguros.shared.tenant;

import java.sql.PreparedStatement;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;

/**
 * Copia o {@link TenantContext} actual para a ligação da transacção em curso, com
 * {@code set_config(..., true)} (valores locais à transacção). As políticas de Row Level
 * Security lêem {@code app.scope}, {@code app.user_id}, {@code app.org_id} e
 * {@code app.insurer_id}.
 *
 * <p>Usado pelo {@link TenantJpaTransactionManager} e pelo {@link RunAsSystemAspect}.
 */
public final class TenantSettingsApplier {

    static final String SQL = """
            select set_config('app.scope', ?, true),
                   set_config('app.user_id', ?, true),
                   set_config('app.org_id', ?, true),
                   set_config('app.insurer_id', ?, true)""";

    private TenantSettingsApplier() {
    }

    /** Aplica o âmbito de {@link TenantContext#current()} na transacção do {@code entityManager}. */
    public static void apply(EntityManager entityManager) {
        TenantSnapshot snapshot = TenantContext.current();
        entityManager.unwrap(Session.class).doWork(connection -> {
            try (PreparedStatement ps = connection.prepareStatement(SQL)) {
                ps.setString(1, snapshot.scope().name());
                ps.setString(2, text(snapshot.userId()));
                ps.setString(3, text(snapshot.orgId()));
                ps.setString(4, text(snapshot.insurerId()));
                ps.execute();
            }
        });
    }

    private static String text(UUID id) {
        return id == null ? "" : id.toString();
    }
}
