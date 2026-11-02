package mz.co.moseguros.identity.domain;

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
