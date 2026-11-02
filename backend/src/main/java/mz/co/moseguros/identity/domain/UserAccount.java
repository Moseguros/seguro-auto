package mz.co.moseguros.identity.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.crypto.EncryptedStringConverter;
import mz.co.moseguros.shared.domain.Emails;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Conta de utilizador ({@code identity.user_account}): cliente final ou staff.
 *
 * <p>A mesma pessoa pode ter uma conta de cliente e outra de staff com o mesmo e-mail; são
 * contas diferentes.
 */
@Entity
@Table(schema = "identity", name = "user_account")
public class UserAccount extends BaseEntity {

    public static final String DEFAULT_LANGUAGE = "pt";

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 10)
    private AccountType type;

    @Column(name = "msisdn", length = 12)
    private Msisdn msisdn;

    @Column(name = "msisdn_verified", nullable = false)
    private boolean msisdnVerified;

    @Column(name = "email", length = 254)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "display_name", length = 120)
    private String displayName;

    @Column(name = "preferred_language", nullable = false, length = 5)
    private String preferredLanguage = DEFAULT_LANGUAGE;

    /** Só staff; Argon2id. */
    @Column(name = "password_hash", length = 200)
    private String passwordHash;

    /** Só staff; em claro no Java, cifrado na coluna {@code totp_secret_enc}. */
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "totp_secret_enc")
    private String totpSecret;

    @Column(name = "last_totp_step")
    private Long lastTotpStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    /** Organização do staff; {@code null} para clientes e para staff da plataforma. */
    @Column(name = "organization_id", updatable = false)
    private UUID organizationId;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    protected UserAccount() {
    }

    /**
     * Conta de cliente criada depois de validar um OTP enviado para o telemóvel: {@code ACTIVE}
     * e com o telemóvel verificado.
     */
    public static UserAccount newCustomer(Msisdn msisdn) {
        UserAccount account = customer();
        account.msisdn = Objects.requireNonNull(msisdn, "msisdn");
        account.msisdnVerified = true;
        return account;
    }

    /**
     * Conta de cliente criada depois de validar um OTP enviado por e-mail: {@code ACTIVE} e
     * com o e-mail verificado. E-mail inválido → {@code INVALID_EMAIL}.
     */
    public static UserAccount newCustomer(String email) {
        UserAccount account = customer();
        account.email = Emails.normalize(email);
        account.emailVerified = true;
        return account;
    }

    /**
     * Staff convidado ({@code PENDING_ACTIVATION}). {@code organizationId} {@code null} = staff
     * da plataforma.
     */
    public static UserAccount newStaff(String email, String displayName, UUID organizationId) {
        UserAccount account = new UserAccount();
        account.type = AccountType.STAFF;
        account.email = Emails.normalize(email);
        account.displayName = displayName == null || displayName.isBlank() ? null : displayName.trim();
        account.organizationId = organizationId;
        account.status = AccountStatus.PENDING_ACTIVATION;
        return account;
    }

    private static UserAccount customer() {
        UserAccount account = new UserAccount();
        account.type = AccountType.CUSTOMER;
        account.status = AccountStatus.ACTIVE;
        return account;
    }

    /** O telemóvel foi confirmado por um código OTP. */
    public void markMsisdnVerified() {
        if (msisdn == null) {
            throw new IllegalStateException("Account has no msisdn");
        }
        this.msisdnVerified = true;
    }

    /** O e-mail foi confirmado por um código OTP. */
    public void markEmailVerified() {
        if (email == null) {
            throw new IllegalStateException("Account has no email");
        }
        this.emailVerified = true;
    }

    /**
     * Staff que aceitou o convite: grava a palavra-passe (hash Argon2id) e um segredo TOTP novo.
     * Pode repetir-se até à activação (cada chamada substitui os anteriores).
     */
    public void setCredentials(String displayName, String passwordHash, String totpSecret) {
        if (!isStaff()) {
            throw new IllegalStateException("Only staff accounts have a password and TOTP");
        }
        if (displayName != null && !displayName.isBlank()) {
            this.displayName = displayName.trim();
        }
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.totpSecret = Objects.requireNonNull(totpSecret, "totpSecret");
        this.lastTotpStep = null;
    }

    /**
     * Activação de staff com o primeiro código TOTP certo: {@code ACTIVE}, e-mail verificado (o
     * link do convite chegou-lhe) e o passo TOTP usado (não pode ser reutilizado).
     */
    public void activate(long totpStep) {
        if (!hasCredentials()) {
            throw new IllegalStateException("Staff account without credentials cannot be activated");
        }
        this.status = AccountStatus.ACTIVE;
        this.emailVerified = true;
        this.lastTotpStep = totpStep;
        this.failedAttempts = 0;
        this.lockedUntil = null;
    }

    /** Staff com palavra-passe e segredo TOTP. */
    public boolean hasCredentials() {
        return isStaff() && passwordHash != null && totpSecret != null;
    }

    /** Regista um login com sucesso. */
    public void recordLogin(Instant at) {
        this.lastLoginAt = at;
    }

    // --- login de staff e gestão (S1-05) ---

    /** {@code LOCKED} com {@code locked_until} ainda por passar. */
    public boolean isLockedAt(Instant now) {
        return status == AccountStatus.LOCKED && lockedUntil != null && lockedUntil.isAfter(now);
    }

    /**
     * Estado como é mostrado: {@code LOCKED} só enquanto {@code locked_until} não passou; depois
     * disso a conta já é {@code ACTIVE} (o desbloqueio grava-se no login seguinte).
     */
    public AccountStatus effectiveStatus(Instant now) {
        return status == AccountStatus.LOCKED && !isLockedAt(now) ? AccountStatus.ACTIVE : status;
    }

    /**
     * Conta bloqueada cujo prazo já passou: volta a {@code ACTIVE} com as falhas a zero. Devolve
     * {@code true} se mudou alguma coisa.
     */
    public boolean releaseExpiredLock(Instant now) {
        if (status != AccountStatus.LOCKED || isLockedAt(now)) {
            return false;
        }
        this.status = AccountStatus.ACTIVE;
        this.failedAttempts = 0;
        this.lockedUntil = null;
        return true;
    }

    /**
     * Palavra-passe ou código TOTP errados: mais uma falha. Ao chegar a {@code maxAttempts}, uma
     * conta {@code ACTIVE} fica {@code LOCKED} até {@code now + lockDuration}. Uma conta noutro
     * estado (ex.: {@code DISABLED}) só conta a falha: nunca passa a {@code LOCKED}, para não voltar
     * a {@code ACTIVE} quando o bloqueio expirar.
     *
     * @return {@code true} se a conta ficou bloqueada agora
     */
    public boolean registerFailedLogin(Instant now, int maxAttempts, Duration lockDuration) {
        requireStaff();
        this.failedAttempts++;
        if (status == AccountStatus.ACTIVE && failedAttempts >= maxAttempts) {
            this.status = AccountStatus.LOCKED;
            this.lockedUntil = now.plus(lockDuration);
            return true;
        }
        return false;
    }

    /** Login de staff com sucesso: falhas a zero, passo TOTP usado e data do login. */
    public void recordSuccessfulLogin(long totpStep, Instant at) {
        requireStaff();
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Only active accounts can sign in");
        }
        this.failedAttempts = 0;
        this.lastTotpStep = totpStep;
        this.lastLoginAt = at;
    }

    /**
     * Palavra-passe nova pela recuperação: falhas a zero e, se estava {@code LOCKED}, volta a
     * {@code ACTIVE}. O TOTP não muda.
     */
    public void resetPassword(String newPasswordHash) {
        requireStaff();
        if (status != AccountStatus.ACTIVE && status != AccountStatus.LOCKED) {
            throw new IllegalStateException("Only active or locked accounts can reset the password");
        }
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "newPasswordHash");
        this.failedAttempts = 0;
        this.lockedUntil = null;
        this.status = AccountStatus.ACTIVE;
    }

    /**
     * Desactivação pelo gestor: {@code ACTIVE} ou {@code LOCKED} → {@code DISABLED}.
     *
     * @throws DomainException {@code INVALID_STATE_TRANSITION} (409) noutro estado
     */
    public void disable() {
        requireStaff();
        requireStatus(AccountStatus.ACTIVE, AccountStatus.LOCKED);
        this.status = AccountStatus.DISABLED;
        this.lockedUntil = null;
    }

    /** Reactivação: {@code DISABLED} → {@code ACTIVE}, com as falhas a zero. */
    public void enable() {
        requireStaff();
        requireStatus(AccountStatus.DISABLED);
        this.status = AccountStatus.ACTIVE;
        this.failedAttempts = 0;
        this.lockedUntil = null;
    }

    /** Desbloqueio pelo gestor: {@code LOCKED} → {@code ACTIVE}, com as falhas a zero. */
    public void unlock() {
        requireStaff();
        requireStatus(AccountStatus.LOCKED);
        this.status = AccountStatus.ACTIVE;
        this.failedAttempts = 0;
        this.lockedUntil = null;
    }

    /**
     * Reposição de acesso: {@code ACTIVE} ou {@code LOCKED} → {@code PENDING_ACTIVATION}, sem
     * palavra-passe nem segredo TOTP. O utilizador repete a activação com um convite novo.
     */
    public void resetAccess() {
        requireStaff();
        requireStatus(AccountStatus.ACTIVE, AccountStatus.LOCKED);
        this.status = AccountStatus.PENDING_ACTIVATION;
        this.passwordHash = null;
        this.totpSecret = null;
        this.lastTotpStep = null;
        this.failedAttempts = 0;
        this.lockedUntil = null;
    }

    private void requireStaff() {
        if (!isStaff()) {
            throw new IllegalStateException("Only staff accounts");
        }
    }

    private void requireStatus(AccountStatus... allowed) {
        for (AccountStatus candidate : allowed) {
            if (status == candidate) {
                return;
            }
        }
        throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                "Transition not allowed from status " + status);
    }

    public boolean isDisabled() {
        return status == AccountStatus.DISABLED;
    }

    public boolean isCustomer() {
        return type == AccountType.CUSTOMER;
    }

    public boolean isStaff() {
        return type == AccountType.STAFF;
    }

    public AccountType getType() {
        return type;
    }

    public Msisdn getMsisdn() {
        return msisdn;
    }

    public boolean isMsisdnVerified() {
        return msisdnVerified;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getTotpSecret() {
        return totpSecret;
    }

    public Long getLastTotpStep() {
        return lastTotpStep;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }
}
