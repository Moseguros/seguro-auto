package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jwt.SignedJWT;
import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.domain.RefreshToken;
import mz.co.moseguros.identity.domain.RefreshTokenRepository;
import mz.co.moseguros.identity.domain.RoleRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserRole;
import mz.co.moseguros.identity.domain.UserRoleRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.security.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class TokenServiceTest {

    static final Instant NOW = Instant.parse("2026-11-02T10:00:00Z");
    static final JwtProperties PROPERTIES = new JwtProperties("https://api.moseguros.co.mz", "moseguros-api",
            Duration.ofMinutes(15), Duration.ofDays(60), Duration.ofHours(12), "test-kid", null);

    UserRoleRepository userRoles = mock(UserRoleRepository.class);
    RoleRepository roles = mock(RoleRepository.class);
    RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
    OrganizationApi organizations = mock(OrganizationApi.class);
    NimbusJwtDecoder decoder;
    TokenService service;

    @BeforeEach
    void setUp() throws Exception {
        KeyPair keyPair = mz.co.moseguros.shared.security.TestKeys.rsa();
        RSAKey key = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).privateKey(keyPair.getPrivate())
                .keyID("test-kid").keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).build();
        decoder = NimbusJwtDecoder.withPublicKey(key.toRSAPublicKey()).build();
        // Valida exp/nbf com o mesmo relógio fixo da emissão (sem isto o teste falha depois de NOW + 15 min).
        JwtTimestampValidator timestamps = new JwtTimestampValidator();
        timestamps.setClock(Clock.fixed(NOW, ZoneOffset.UTC));
        decoder.setJwtValidator(timestamps);
        service = new TokenService(new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key))), PROPERTIES,
                Clock.fixed(NOW, ZoneOffset.UTC), userRoles, roles, refreshTokens, organizations);
        when(refreshTokens.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void customerToken() throws Exception {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        givenRoles(customer, Set.of("self:profile"), "CUSTOMER");

        TokenPair pair = service.issue(customer, "JUnit");

        assertThat(pair.expiresIn()).isEqualTo(900);
        Jwt jwt = decoder.decode(pair.accessToken());
        assertThat(jwt.getHeaders()).containsEntry("kid", "test-kid").containsEntry("alg", "RS256");
        assertThat(jwt.getIssuer().toString()).isEqualTo("https://api.moseguros.co.mz");
        assertThat(jwt.getAudience()).containsExactly("moseguros-api");
        assertThat(jwt.getSubject()).isEqualTo(customer.getId().toString());
        assertThat(jwt.getIssuedAt()).isEqualTo(NOW);
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).hasSeconds(900);
        assertThat(UUID.fromString(jwt.getId()).version()).isEqualTo(7);
        assertThat(jwt.getClaimAsString("type")).isEqualTo("CUSTOMER");
        assertThat(jwt.hasClaim("orgId")).isFalse();
        assertThat(jwt.hasClaim("orgType")).isFalse();
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("CUSTOMER");
        assertThat(jwt.getClaimAsStringList("permissions")).containsExactly("self:profile");
        assertThat(SignedJWT.parse(pair.accessToken()).getHeader().getKeyID()).isEqualTo("test-kid");

        RefreshToken saved = savedRefreshToken();
        assertThat(saved.getUserId()).isEqualTo(customer.getId());
        assertThat(saved.getExpiresAt()).isEqualTo(NOW.plus(Duration.ofDays(60)));
        assertThat(saved.getSessionExpiresAt()).isNull();
        assertThat(saved.getDeviceInfo()).isEqualTo("JUnit");
        assertThat(saved.getTokenHash()).isEqualTo(TokenService.hash(pair.refreshToken())).hasSize(64);
        assertThat(saved.getTokenHash()).doesNotContain(pair.refreshToken());
    }

    @Test
    void insurerStaffToken() {
        UUID orgId = UUID.randomUUID();
        UserAccount staff = UserAccount.newStaff("ana@seguradora.co.mz", "Ana", orgId);
        givenRoles(staff, Set.of("org-user:read", "org-user:manage"), "INSURER_ADMIN");
        when(organizations.findSummary(orgId)).thenReturn(Optional.of(
                new OrganizationSummary(orgId, OrganizationType.INSURER, OrganizationStatus.ACTIVE, "Seg")));

        TokenPair pair = service.issue(staff, null);

        Jwt jwt = decoder.decode(pair.accessToken());
        assertThat(jwt.getClaimAsString("type")).isEqualTo("STAFF");
        assertThat(jwt.getClaimAsString("orgId")).isEqualTo(orgId.toString());
        assertThat(jwt.getClaimAsString("orgType")).isEqualTo("INSURER");
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("INSURER_ADMIN");
        assertThat(jwt.getClaimAsStringList("permissions")).containsExactly("org-user:manage", "org-user:read");
        assertThat(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt())).hasSeconds(900);

        RefreshToken saved = savedRefreshToken();
        assertThat(saved.getExpiresAt()).isEqualTo(NOW.plus(Duration.ofHours(12)));
        assertThat(saved.getSessionExpiresAt()).isEqualTo(saved.getExpiresAt());
    }

    @Test
    void platformAdminToken() {
        UserAccount admin = UserAccount.newStaff("admin@moseguros.co.mz", "Admin", null);
        givenRoles(admin, Set.of("organization:manage", "otp-settings:read", "organization:read"), "PLATFORM_ADMIN");

        Jwt jwt = decoder.decode(service.issue(admin, null).accessToken());

        assertThat(jwt.getClaimAsString("type")).isEqualTo("STAFF");
        assertThat(jwt.hasClaim("orgId")).isFalse();
        assertThat(jwt.hasClaim("orgType")).isFalse();
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("PLATFORM_ADMIN");
        assertThat(jwt.getClaimAsStringList("permissions"))
                .containsExactly("organization:manage", "organization:read", "otp-settings:read");
        assertThat(jwt.getExpiresAt()).isEqualTo(NOW.plusSeconds(900));
    }

    @Test
    void accountWithoutRolesHasEmptyLists() {
        UserAccount staff = UserAccount.newStaff("x@moseguros.co.mz", null, null);
        when(userRoles.findByUserId(staff.getId())).thenReturn(List.of());

        Jwt jwt = decoder.decode(service.issue(staff, null).accessToken());

        assertThat(jwt.getClaimAsStringList("roles")).isEmpty();
        assertThat(jwt.getClaimAsStringList("permissions")).isEmpty();
    }

    @Test
    void refreshTokensAreRandomBase64UrlWithoutPadding() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        givenRoles(customer, Set.of(), "CUSTOMER");

        String first = service.issue(customer, null).refreshToken();
        String second = service.issue(customer, null).refreshToken();

        assertThat(first).matches("^[A-Za-z0-9_-]{43}$").isNotEqualTo(second);
    }

    @Test
    void hashIsLowercaseSha256Hex() {
        assertThat(TokenService.hash("abc"))
                .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    }

    private void givenRoles(UserAccount account, Set<String> permissions, String... roleCodes) {
        when(userRoles.findByUserId(account.getId())).thenReturn(
                java.util.Arrays.stream(roleCodes).map(code -> new UserRole(account.getId(), code)).toList());
        when(roles.findPermissionsByRoleCodes(any())).thenReturn(permissions);
    }

    private RefreshToken savedRefreshToken() {
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        org.mockito.Mockito.verify(refreshTokens, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
        return captor.getValue();
    }
}
