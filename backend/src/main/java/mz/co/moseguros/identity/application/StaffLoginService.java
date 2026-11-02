package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationStatus;
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
import org.springframework.transaction.annotation.Transactional;

/**
 * Login de staff com e-mail, palavra-passe e TOTP (S1-05 §1, ADR-003).
 *
 * <p>Endpoint público: corre em {@code SYSTEM} e filtra pelo e-mail. A conta é lida com
 * {@code select ... for update} e as falhas ficam gravadas mesmo com a resposta de erro
 * ({@code noRollbackFor}). Nunca regista a palavra-passe nem o código TOTP.
 */
@Service
public class StaffLoginService {

    private static final Logger log = LoggerFactory.getLogger(StaffLoginService.class);

    static final String SYSTEM_ACTOR = "staff-login";

    private final UserAccountRepository accounts;
    private final OrganizationApi organizations;
    private final PasswordEncoder passwordEncoder;
    private final TotpService totp;
    private final TokenService tokenService;
    private final AuditService audit;
    private final StaffAuthProperties properties;
    private final Clock clock;
    /**
     * Hash Argon2id de uma palavra-passe fictícia: verificado quando a conta não existe, para o
     * tempo de resposta não revelar se o e-mail existe.
     */
    private final String dummyHash;

    public StaffLoginService(UserAccountRepository accounts, OrganizationApi organizations,
                             PasswordEncoder passwordEncoder, TotpService totp, TokenService tokenService,
                             AuditService audit, StaffAuthProperties properties, Clock clock) {
        this.accounts = accounts;
        this.organizations = organizations;
        this.passwordEncoder = passwordEncoder;
        this.totp = totp;
        this.tokenService = tokenService;
        this.audit = audit;
        this.properties = properties;
        this.clock = clock;
        this.dummyHash = passwordEncoder.encode("moseguros-timing-" + UUID.randomUUID());
    }

    /**
     * Verifica as credenciais e abre uma sessão de staff (12 h).
     *
     * @param deviceInfo o {@code User-Agent} do pedido
     * @throws DomainException {@code INVALID_CREDENTIALS} (401), {@code ACCOUNT_LOCKED} (403, com
     *                         {@code retryAfterSeconds}), {@code ACCOUNT_DISABLED} (403) ou
     *                         {@code ORGANIZATION_SUSPENDED} (403)
     */
    @RunAsSystem
    @Transactional(noRollbackFor = DomainException.class)
    public TokenPair login(String email, String password, String totpCode, String deviceInfo) {
        Instant now = clock.instant();
        Optional<UserAccount> found = normalize(email).flatMap(accounts::findStaffByEmailForUpdate)
                .filter(UserAccount::hasCredentials)
                // convite aceite mas por activar: tem credenciais, mas não pode entrar (S1-05 §1.1)
                .filter(account -> account.getStatus() != AccountStatus.PENDING_ACTIVATION);
        if (found.isEmpty()) {
            passwordEncoder.matches(password == null ? "" : password, dummyHash);
            log.info("Staff login refused: unknown or not activated account");
            throw invalidCredentials();
        }
        UserAccount account = found.get();

        if (account.isLockedAt(now)) {
            log.info("Staff login refused: account {} is locked", account.getId());
            throw DomainException.of(ErrorCodes.ACCOUNT_LOCKED, "The account is temporarily locked")
                    .withProperty(DomainException.RETRY_AFTER_SECONDS,
                            secondsUntil(account.getLockedUntil(), now));
        }
        if (account.releaseExpiredLock(now)) {
            log.info("Staff account {}: lock expired", account.getId());
        }

        boolean passwordOk = password != null && passwordEncoder.matches(password, account.getPasswordHash());
        OptionalLong step = passwordOk
                ? totp.verify(account.getTotpSecret(), totpCode, account.getLastTotpStep())
                : OptionalLong.empty();
        if (step.isEmpty()) {
            boolean locked = account.registerFailedLogin(now, properties.maxFailedAttempts(),
                    properties.lockDuration());
            accounts.save(account);
            if (locked) {
                recordLock(account);
                log.info("Staff account {} locked after {} failed logins", account.getId(),
                        account.getFailedAttempts());
            } else {
                log.info("Staff login failed for account {} ({} failed attempts)", account.getId(),
                        account.getFailedAttempts());
            }
            throw invalidCredentials();
        }

        if (account.isDisabled()) {
            log.info("Staff login refused: account {} is disabled", account.getId());
            throw DomainException.of(ErrorCodes.ACCOUNT_DISABLED, "The account is disabled");
        }
        if (account.getOrganizationId() != null) {
            boolean suspended = organizations.findSummary(account.getOrganizationId())
                    .map(org -> org.status() == OrganizationStatus.SUSPENDED)
                    .orElse(true);
            if (suspended) {
                log.info("Staff login refused: organization of account {} is suspended", account.getId());
                throw DomainException.of(ErrorCodes.ORGANIZATION_SUSPENDED, "The organization is suspended");
            }
        }

        account.recordSuccessfulLogin(step.getAsLong(), now);
        accounts.save(account);
        log.info("Staff account {} signed in", account.getId());
        return tokenService.issue(account, deviceInfo);
    }

    private void recordLock(UserAccount account) {
        UUID insurerId = null;
        if (account.getOrganizationId() != null) {
            insurerId = organizations.findSummary(account.getOrganizationId())
                    .filter(org -> org.type() == OrganizationType.INSURER)
                    .map(OrganizationSummary::id)
                    .orElse(null);
        }
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("status", account.getStatus().name());
        after.put("lockedUntil", account.getLockedUntil());
        after.put("failedAttempts", account.getFailedAttempts());
        audit.record(new AuditRecord(ActorType.SYSTEM, SYSTEM_ACTOR, insurerId, "USER.LOCK", "UserAccount",
                account.getId().toString(), null, after));
    }

    /** Segundos até {@code until}, arredondados para cima (mínimo 1). */
    static long secondsUntil(Instant until, Instant now) {
        long millis = Duration.between(now, until).toMillis();
        return Math.max(1, (millis + 999) / 1000);
    }

    /** E-mail em minúsculas; um e-mail inválido não corresponde a nenhuma conta. */
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

    private static DomainException invalidCredentials() {
        return DomainException.of(ErrorCodes.INVALID_CREDENTIALS, "Invalid credentials");
    }
}
