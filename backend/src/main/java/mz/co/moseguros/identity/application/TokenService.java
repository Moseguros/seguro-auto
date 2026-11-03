package mz.co.moseguros.identity.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.domain.RefreshToken;
import mz.co.moseguros.identity.domain.RefreshTokenRepository;
import mz.co.moseguros.identity.domain.RoleRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserRole;
import mz.co.moseguros.identity.domain.UserRoleRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.security.JwtClaims;
import mz.co.moseguros.shared.security.JwtProperties;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Emissão de tokens (ADR-003): o token de acesso JWT RS256 e o refresh token opaco.
 *
 * <p>Usado pelo login (OTP no S1-03, staff no S1-05, através do {@code IdentityApi}) e pela
 * renovação ({@link SessionService}). Corre em {@code SYSTEM}: não há utilizador autenticado.
 */
@Service
public class TokenService {

    static final int REFRESH_TOKEN_BYTES = 32;

    private final JwtEncoder encoder;
    private final JwtProperties properties;
    private final Clock clock;
    private final UserRoleRepository userRoles;
    private final RoleRepository roles;
    private final RefreshTokenRepository refreshTokens;
    private final OrganizationApi organizations;
    private final SecureRandom random = new SecureRandom();

    public TokenService(JwtEncoder encoder, JwtProperties properties, Clock clock, UserRoleRepository userRoles,
                        RoleRepository roles, RefreshTokenRepository refreshTokens, OrganizationApi organizations) {
        this.encoder = encoder;
        this.properties = properties;
        this.clock = clock;
        this.userRoles = userRoles;
        this.roles = roles;
        this.refreshTokens = refreshTokens;
        this.organizations = organizations;
    }

    /**
     * Abre uma sessão nova (família nova de refresh tokens) e devolve o par de tokens.
     * Cliente: o refresh token vale 60 dias. Staff: a sessão acaba 12 h depois, sem extensão.
     *
     * @param deviceInfo o {@code User-Agent} do pedido (pode ser {@code null})
     */
    @RunAsSystem
    @Transactional
    public TokenPair issue(UserAccount account, String deviceInfo) {
        Instant now = clock.instant();
        Instant expiresAt;
        Instant sessionExpiresAt;
        if (account.isStaff()) {
            sessionExpiresAt = now.plus(properties.staffRefreshTtl());
            expiresAt = sessionExpiresAt;
        } else {
            sessionExpiresAt = null;
            expiresAt = now.plus(properties.customerRefreshTtl());
        }
        IssuedRefreshToken refresh = newRefreshToken(account.getId(), Ids.newId(), expiresAt, sessionExpiresAt,
                deviceInfo);
        return new TokenPair(accessToken(account, now), refresh.value(), expiresInSeconds());
    }

    /** Grava um refresh token novo na família indicada e devolve o valor em claro. */
    IssuedRefreshToken newRefreshToken(UUID userId, UUID familyId, Instant expiresAt, Instant sessionExpiresAt,
                                       String deviceInfo) {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        random.nextBytes(bytes);
        String value = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        RefreshToken token = refreshTokens.save(RefreshToken.issue(userId, familyId, hash(value), expiresAt,
                sessionExpiresAt, deviceInfo));
        return new IssuedRefreshToken(token.getId(), value);
    }

    /**
     * Token de acesso com os perfis e permissões lidos agora da BD e o tipo da organização
     * ({@code OrganizationApi}).
     */
    String accessToken(UserAccount account, Instant now) {
        List<String> roleCodes = userRoles.findByUserId(account.getId()).stream()
                .map(UserRole::getRoleCode)
                .sorted()
                .toList();
        List<String> permissions = roleCodes.isEmpty() ? List.of()
                : roles.findPermissionsByRoleCodes(roleCodes).stream().sorted().toList();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .audience(List.of(properties.audience()))
                .issuedAt(now)
                .expiresAt(now.plus(properties.accessTokenTtl()))
                .id(Ids.newId().toString())
                .subject(account.getId().toString())
                .claim(JwtClaims.TYPE, account.getType().name())
                .claim(JwtClaims.ROLES, roleCodes)
                .claim(JwtClaims.PERMISSIONS, permissions);
        UUID orgId = account.getOrganizationId();
        if (orgId != null) {
            claims.claim(JwtClaims.ORG_ID, orgId.toString());
            Optional<OrganizationSummary> organization = organizations.findSummary(orgId);
            organization.ifPresent(org -> claims.claim(JwtClaims.ORG_TYPE, org.type().name()));
        }
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).keyId(properties.keyId()).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }

    long expiresInSeconds() {
        return properties.accessTokenTtl().toSeconds();
    }

    /** SHA-256 em hexadecimal (minúsculas): o que se grava em {@code refresh_token.token_hash}. */
    static String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    /** Refresh token acabado de gravar: o id da linha e o valor em claro. */
    record IssuedRefreshToken(UUID id, String value) {

        @Override
        public String toString() {
            return "IssuedRefreshToken[id=" + id + ", value=***]";
        }
    }
}
