package mz.co.moseguros.identity.application;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.PasswordReset;
import mz.co.moseguros.identity.domain.PasswordResetRepository;
import mz.co.moseguros.identity.domain.RefreshToken.RevokedReason;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.notification.DeliveryResult;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Recuperação de palavra-passe de staff por e-mail (S1-05 §2).
 *
 * <p>Endpoints públicos: tudo corre em {@code SYSTEM} ({@code password_reset} só é visível nesse
 * âmbito) e filtra pelo e-mail ou pelo hash do token. O token só existe em claro no link do
 * e-mail: grava-se o SHA-256 e nunca é registado, tal como a palavra-passe nova.
 */
@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    static final int TOKEN_BYTES = 32;
    static final String EMAIL_TEMPLATE = "password-reset.email";
    static final String PURPOSE = "PASSWORD_RESET";
    static final Duration RATE_LIMIT_WINDOW = Duration.ofHours(1);

    private final UserAccountRepository accounts;
    private final PasswordResetRepository resets;
    private final OrganizationApi organizations;
    private final NotificationApi notifications;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessions;
    private final AuditService audit;
    private final StaffAuthProperties properties;
    private final TransactionTemplate tx;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetService(UserAccountRepository accounts, PasswordResetRepository resets,
                                OrganizationApi organizations, NotificationApi notifications,
                                PasswordPolicy passwordPolicy, PasswordEncoder passwordEncoder,
                                SessionService sessions, AuditService audit, StaffAuthProperties properties,
                                TransactionTemplate tx, Clock clock) {
        this.accounts = accounts;
        this.resets = resets;
        this.organizations = organizations;
        this.notifications = notifications;
        this.passwordPolicy = passwordPolicy;
        this.passwordEncoder = passwordEncoder;
        this.sessions = sessions;
        this.audit = audit;
        this.properties = properties;
        this.tx = tx;
        this.clock = clock;
    }

    /**
     * Pedido de recuperação. Só faz alguma coisa para uma conta de staff {@code ACTIVE} ou
     * {@code LOCKED} que não tenha excedido os 3 e-mails na última hora; de fora não se vê a
     * diferença (o endpoint responde sempre 202).
     */
    @RunAsSystem
    public void forgot(String rawEmail) {
        Optional<String> email = normalize(rawEmail);
        if (email.isEmpty()) {
            log.info("Password reset requested for an invalid e-mail address: ignored");
            return;
        }
        Prepared prepared = tx.execute(status -> prepare(email.get()));
        if (prepared == null) {
            return;
        }
        DeliveryResult result = notifications.sendEmail(prepared.email(), EMAIL_TEMPLATE,
                new Object[] {prepared.name(), properties.resetLink(prepared.token())}, prepared.language(),
                PURPOSE, prepared.resetId());
        if (result.failed()) {
            log.warn("Password reset {}: e-mail not delivered ({})", prepared.resetId(), result.failureReason());
        } else {
            log.info("Password reset {} sent for account {}", prepared.resetId(), prepared.userId());
        }
    }

    /**
     * Define a palavra-passe nova: grava o hash, marca o token como usado, desbloqueia a conta e
     * revoga todas as sessões ({@code PASSWORD_RESET}). O TOTP não muda.
     *
     * @throws DomainException {@code RESET_TOKEN_INVALID} (400) ou {@code PASSWORD_TOO_WEAK} (400,
     *                         com os motivos no campo {@code newPassword})
     */
    @RunAsSystem
    public void reset(String token, String newPassword) {
        tx.executeWithoutResult(status -> {
            Instant now = clock.instant();
            PasswordReset reset = resets.findByTokenHashForUpdate(TokenService.hash(token == null ? "" : token))
                    .filter(r -> r.isUsable(now))
                    .orElseThrow(PasswordResetService::invalid);
            UserAccount account = accounts.findById(reset.getUserId())
                    .filter(a -> a.isStaff() && a.hasCredentials())
                    .filter(a -> a.getStatus() == AccountStatus.ACTIVE || a.getStatus() == AccountStatus.LOCKED)
                    .orElseThrow(PasswordResetService::invalid);
            passwordPolicy.check(newPassword, account.getEmail(), "newPassword");

            AccountStatus before = account.getStatus();
            account.resetPassword(passwordEncoder.encode(newPassword));
            accounts.save(account);
            reset.markUsed(now);
            resets.save(reset);
            int revoked = sessions.revokeAllForUser(account.getId(), RevokedReason.PASSWORD_RESET);

            Map<String, Object> after = new LinkedHashMap<>();
            after.put("status", account.getStatus().name());
            after.put("passwordResetId", reset.getId());
            after.put("revokedSessions", revoked);
            audit.record(new AuditRecord(ActorType.STAFF, account.getId().toString(), insurerId(account),
                    "USER.PASSWORD_RESET", "UserAccount", account.getId().toString(),
                    Map.of("status", before.name()), after));
            log.info("Password reset {}: new password set for account {} ({} sessions revoked)", reset.getId(),
                    account.getId(), revoked);
        });
    }

    /** Cria o pedido (ou {@code null} se não houver nada a enviar). Corre na transacção do {@link #forgot}. */
    private Prepared prepare(String email) {
        Instant now = clock.instant();
        Optional<UserAccount> found = accounts.findStaffByEmailForUpdate(email)
                .filter(UserAccount::hasCredentials)
                .filter(a -> a.getStatus() == AccountStatus.ACTIVE || a.getStatus() == AccountStatus.LOCKED);
        if (found.isEmpty()) {
            log.info("Password reset requested for an unknown or unavailable account: ignored");
            return null;
        }
        UserAccount account = found.get();
        long recent = resets.countByUserIdAndCreatedAtAfter(account.getId(), now.minus(RATE_LIMIT_WINDOW));
        if (recent >= properties.maxResetsPerHour()) {
            log.info("Password reset for account {}: limit of {} e-mails per hour reached", account.getId(),
                    properties.maxResetsPerHour());
            return null;
        }
        List<PasswordReset> previous = resets.findByUserIdAndUsedAtIsNullAndInvalidatedAtIsNull(account.getId());
        previous.forEach(r -> r.invalidate(now));
        String token = newToken();
        PasswordReset reset = resets.save(PasswordReset.create(account.getId(), TokenService.hash(token),
                now.plus(properties.resetValidity())));
        resets.flush();
        String name = account.getDisplayName() == null ? account.getEmail() : account.getDisplayName();
        return new Prepared(reset.getId(), account.getId(), account.getEmail(), name,
                account.getPreferredLanguage(), token);
    }

    private UUID insurerId(UserAccount account) {
        if (account.getOrganizationId() == null) {
            return null;
        }
        return organizations.findSummary(account.getOrganizationId())
                .filter(org -> org.type() == OrganizationType.INSURER)
                .map(OrganizationSummary::id)
                .orElse(null);
    }

    private String newToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static Optional<String> normalize(String email) {
        if (email == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Emails.normalize(email));
        } catch (DomainException e) {
            return Optional.empty();
        }
    }

    private static DomainException invalid() {
        return DomainException.of(ErrorCodes.RESET_TOKEN_INVALID, "The password reset link is not valid");
    }

    /** Pedido gravado e o token em claro (só para o link do e-mail). */
    private record Prepared(UUID resetId, UUID userId, String email, String name, String language, String token) {

        @Override
        public String toString() {
            return "Prepared[resetId=" + resetId + ", token=***]";
        }
    }
}
