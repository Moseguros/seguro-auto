package mz.co.moseguros.shared.security;

/**
 * Nomes das claims próprias do token de acesso (ADR-003) e valores conhecidos.
 */
public final class JwtClaims {

    /** {@code CUSTOMER} ou {@code STAFF}. */
    public static final String TYPE = "type";
    /** Id da organização do staff; ausente para clientes e staff da plataforma. */
    public static final String ORG_ID = "orgId";
    /** Tipo da organização ({@code INSURER}, {@code BROKER}…); ausente sem organização. */
    public static final String ORG_TYPE = "orgType";
    /** Códigos dos perfis. */
    public static final String ROLES = "roles";
    /** Permissões (união das permissões dos perfis, ordenada). */
    public static final String PERMISSIONS = "permissions";

    public static final String TYPE_CUSTOMER = "CUSTOMER";
    public static final String TYPE_STAFF = "STAFF";
    public static final String ORG_TYPE_INSURER = "INSURER";
    public static final String ROLE_PLATFORM_ADMIN = "PLATFORM_ADMIN";

    /** Prefixo dos perfis nas autoridades do Spring Security. */
    public static final String ROLE_PREFIX = "ROLE_";

    private JwtClaims() {
    }
}
