package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.TokenPair;
import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.RefreshToken;
import mz.co.moseguros.identity.domain.RefreshToken.RevokedReason;
import mz.co.moseguros.identity.domain.RefreshTokenRepository;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class SessionServiceTest {

    static final Instant NOW = Instant.parse("2026-11-02T10:00:00Z");
    static final String RAW = "raw-refresh-token";

    RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
    UserAccountRepository accounts = mock(UserAccountRepository.class);
    OrganizationApi organizations = mock(OrganizationApi.class);
    TokenService tokenService = mock(TokenService.class);
    SessionService service;
    UUID newTokenId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new SessionService(refreshTokens, accounts, organizations, tokenService,
                TokenServiceTest.PROPERTIES, Clock.fixed(NOW, ZoneOffset.UTC));
        when(tokenService.newRefreshToken(any(), any(), any(), any(), any()))
                .thenReturn(new TokenService.IssuedRefreshToken(newTokenId, "next"));
        when(tokenService.accessToken(any(), eq(NOW))).thenReturn("access");
        when(tokenService.expiresInSeconds()).thenReturn(900L);
    }

    @Test
    void customerRefreshRotatesAndSlidesTheExpiry() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        RefreshToken current = given(customer, NOW.plus(Duration.ofDays(1)), null);

        TokenPair pair = service.refresh(RAW, "UA");

        assertThat(pair).isEqualTo(new TokenPair("access", "next", 900));
        assertThat(current.getRevokedReason()).isEqualTo(RevokedReason.ROTATED);
        assertThat(current.getReplacedById()).isEqualTo(newTokenId);
        verify(tokenService).newRefreshToken(customer.getId(), current.getFamilyId(), NOW.plus(Duration.ofDays(60)),
                null, "UA");
    }

    @Test
    void staffRefreshKeepsTheSessionEnd() {
        UUID orgId = UUID.randomUUID();
        UserAccount staff = activeStaff(orgId);
        Instant sessionEnd = NOW.plus(Duration.ofHours(1));
        RefreshToken current = given(staff, sessionEnd, sessionEnd);
        when(organizations.findSummary(orgId)).thenReturn(Optional.of(
                new OrganizationSummary(orgId, OrganizationType.INSURER, OrganizationStatus.ACTIVE, "Seg")));

        service.refresh(RAW, null);

        verify(tokenService).newRefreshToken(staff.getId(), current.getFamilyId(), sessionEnd, sessionEnd, null);
    }

    @Test
    void reuseRevokesTheFamily() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        RefreshToken current = given(customer, NOW.plus(Duration.ofDays(1)), null);
        current.revoke(RevokedReason.ROTATED, NOW.minusSeconds(5), UUID.randomUUID());
        RefreshToken successor = RefreshToken.issue(customer.getId(), current.getFamilyId(), "h2",
                NOW.plus(Duration.ofDays(60)), null, null);
        when(refreshTokens.findByFamilyIdAndRevokedAtIsNull(current.getFamilyId())).thenReturn(List.of(successor));

        assertThatThrownBy(() -> service.refresh(RAW, null))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("REFRESH_TOKEN_REUSED");
                    assertThat(e.getHttpStatus()).isEqualTo(401);
                });
        assertThat(successor.getRevokedReason()).isEqualTo(RevokedReason.REUSE_DETECTED);
    }

    @Test
    void otherRevocationsExpiryAndInactiveAccountsAreInvalid() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        RefreshToken loggedOut = given(customer, NOW.plus(Duration.ofDays(1)), null);
        loggedOut.revoke(RevokedReason.LOGOUT, NOW, null);
        assertInvalid();

        given(customer, NOW, null);
        assertInvalid();

        given(customer, NOW.plus(Duration.ofDays(1)), null);
        ReflectionTestUtils.setField(customer, "status", AccountStatus.DISABLED);
        assertInvalid();

        when(refreshTokens.findByTokenHashForUpdate(any())).thenReturn(Optional.empty());
        assertInvalid();
        verify(tokenService, never()).newRefreshToken(any(), any(), any(), any(), any());
    }

    @Test
    void suspendedOrganizationRevokesTheFamily() {
        UUID orgId = UUID.randomUUID();
        UserAccount staff = activeStaff(orgId);
        RefreshToken current = given(staff, NOW.plus(Duration.ofHours(1)), NOW.plus(Duration.ofHours(1)));
        when(refreshTokens.findByFamilyIdAndRevokedAtIsNull(current.getFamilyId())).thenReturn(List.of(current));
        when(organizations.findSummary(orgId)).thenReturn(Optional.of(
                new OrganizationSummary(orgId, OrganizationType.BROKER, OrganizationStatus.SUSPENDED, "Corretor")));

        assertInvalid();
        assertThat(current.getRevokedReason()).isEqualTo(RevokedReason.ORG_SUSPENDED);
    }

    @Test
    void logoutRevokesTheFamilyAndIgnoresUnknownTokens() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        RefreshToken current = given(customer, NOW.plus(Duration.ofDays(1)), null);
        when(refreshTokens.findByFamilyIdAndRevokedAtIsNull(current.getFamilyId())).thenReturn(List.of(current));

        service.logout(RAW);
        service.logout(null);
        service.logout(" ");

        assertThat(current.getRevokedReason()).isEqualTo(RevokedReason.LOGOUT);
    }

    @Test
    void revokeAllForUser() {
        UUID userId = UUID.randomUUID();
        RefreshToken a = RefreshToken.issue(userId, UUID.randomUUID(), "a", NOW.plusSeconds(60), null, null);
        RefreshToken b = RefreshToken.issue(userId, UUID.randomUUID(), "b", NOW.plusSeconds(60), null, null);
        when(refreshTokens.findByUserIdAndRevokedAtIsNull(userId)).thenReturn(List.of(a, b));

        assertThat(service.revokeAllForUser(userId, RevokedReason.PASSWORD_RESET)).isEqualTo(2);
        assertThat(List.of(a, b)).allSatisfy(t -> assertThat(t.getRevokedReason())
                .isEqualTo(RevokedReason.PASSWORD_RESET));
    }

    private RefreshToken given(UserAccount account, Instant expiresAt, Instant sessionExpiresAt) {
        RefreshToken token = RefreshToken.issue(account.getId(), UUID.randomUUID(), TokenService.hash(RAW), expiresAt,
                sessionExpiresAt, null);
        when(refreshTokens.findByTokenHashForUpdate(TokenService.hash(RAW))).thenReturn(Optional.of(token));
        when(accounts.findById(account.getId())).thenReturn(Optional.of(account));
        return token;
    }

    private static UserAccount activeStaff(UUID orgId) {
        UserAccount staff = UserAccount.newStaff("s@seg.co.mz", "S", orgId);
        ReflectionTestUtils.setField(staff, "status", AccountStatus.ACTIVE);
        return staff;
    }

    private void assertInvalid() {
        assertThatThrownBy(() -> service.refresh(RAW, null))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("REFRESH_TOKEN_INVALID"));
    }
}
