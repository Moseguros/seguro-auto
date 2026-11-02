package mz.co.moseguros.identity.domain;

/**
 * Tipo de conta: cliente final (entra com OTP) ou staff (palavra-passe e TOTP).
 */
public enum AccountType {
    CUSTOMER,
    STAFF
}
