package mz.co.moseguros.notification;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Apoio aos testes das notificações (S6-04): clientes com contactos, tokens, dispositivos, preferências e
 * leitura de {@code notification.message} como dono.
 */
public final class NotificationTestSupport {

    private final IdentityFixtures fixtures;
    private final IdentityApi identityApi;

    public NotificationTestSupport(IdentityFixtures fixtures, IdentityApi identityApi) {
        this.fixtures = fixtures;
        this.identityApi = identityApi;
    }

    /** Cliente activo com o telemóvel verificado. */
    public Client customer() {
        UserAccount account = fixtures.customer("Cliente Notificações " + digits(3));
        return new Client(account.getId(), account.getMsisdn().e164(), token(account));
    }

    public String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    public String staffToken(UUID orgId, String... roles) {
        return token(fixtures.staff("Staff " + digits(4), orgId, roles));
    }

    public String platformAdminToken() {
        return staffToken(null, "PLATFORM_ADMIN");
    }

    /** E-mail verificado. */
    public String verifiedEmail(UUID userId) {
        String email = "notif" + digits(8) + "@teste.co.mz";
        owner().update("update identity.user_account set email = ?, email_verified = true where id = ?", email, userId);
        return email;
    }

    public void unverifyPhone(UUID userId) {
        owner().update("update identity.user_account set msisdn_verified = false where id = ?", userId);
    }

    public void language(UUID userId, String language) {
        owner().update("update identity.user_account set preferred_language = ? where id = ?", language, userId);
    }

    /** Preferência gravada como dono. */
    public void preference(UUID userId, String category, String channel, boolean enabled) {
        owner().update("""
                insert into notification.preference (id, created_at, created_by, updated_at, updated_by, version,
                                                     user_id, category, channel, enabled)
                values (?, now(), 'test', now(), 'test', 0, ?, ?, ?, ?)
                on conflict (user_id, category, channel) do update set enabled = excluded.enabled""",
                UUID.randomUUID(), userId, category, channel, enabled);
    }

    /** Dispositivo activo como dono; devolve o id. */
    public UUID device(UUID userId, String token) {
        UUID id = UUID.randomUUID();
        owner().update("""
                insert into notification.device (id, created_at, created_by, updated_at, updated_by, version, user_id,
                                                 platform, token, active, last_seen_at)
                values (?, now(), 'test', now(), 'test', 0, ?, 'ANDROID', ?, true, now())""", id, userId, token);
        return id;
    }

    /** Linhas de uma notificação (sem a INBOX), por criação. */
    public List<Map<String, Object>> external(UUID notificationId) {
        return owner().queryForList("""
                select * from notification.message where notification_id = ? and channel <> 'INBOX'
                 order by created_at, id""", notificationId);
    }

    public List<Map<String, Object>> all(UUID notificationId) {
        return owner().queryForList("select * from notification.message where notification_id = ? order by created_at",
                notificationId);
    }

    /** Linhas das notificações de um utilizador com o código indicado. */
    public List<Map<String, Object>> byUser(UUID userId, String templateCode) {
        return owner().queryForList("""
                select * from notification.message where user_id = ? and template_code = ?
                 order by created_at, id""", userId, templateCode);
    }

    public Map<String, Object> inbox(UUID notificationId) {
        return owner().queryForMap("select * from notification.message where notification_id = ? and channel = 'INBOX'",
                notificationId);
    }

    public JdbcTemplate owner() {
        return fixtures.owner();
    }

    /** Espera até 10 s que a condição se verifique. */
    public static void await(BooleanSupplier condition) {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(10));
        while (!condition.getAsBoolean()) {
            if (Instant.now().isAfter(deadline)) {
                throw new AssertionError("Condition not met within 10 s");
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError(e);
            }
        }
    }

    /** Corre em {@code SYSTEM} (como um listener). */
    public static <T> T asSystem(java.util.function.Supplier<T> work) {
        TenantSnapshot previous = TenantContext.current();
        TenantContext.set(TenantSnapshot.system(null));
        try {
            return work.get();
        } finally {
            TenantContext.set(previous);
        }
    }

    public static String digits(int count) {
        StringBuilder sb = new StringBuilder();
        sb.append(ThreadLocalRandom.current().nextInt(1, 10));
        for (int i = 1; i < count; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }

    /** Parâmetros de um {@code PROPOSAL_ACCEPTED}. */
    public static Map<String, Object> accepted(UUID proposalId) {
        Map<String, Object> params = new java.util.LinkedHashMap<>();
        params.put("proposalNumber", "PR-2027-000999");
        params.put("insurerName", "Seguradora Exemplo");
        params.put("amount", mz.co.moseguros.shared.domain.Money.ofCentavos(466_300));
        params.put("webPath", "/cliente/propostas/" + proposalId);
        params.put("deepLink", "moseguros://proposal/" + proposalId);
        return params;
    }

    /** Cliente com o telemóvel em E.164 e o token de acesso. */
    public record Client(UUID id, String phone, String token) {
    }
}
