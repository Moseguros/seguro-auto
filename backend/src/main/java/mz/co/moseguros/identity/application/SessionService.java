package mz.co.moseguros.identity.application;

import java.time.Clock;
import java.time.Instant;
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
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.JwtProperties;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sessões (famílias de refresh tokens, ADR-003): renovação com rotação e detecção de
 * reutilização, logout e revogação de todas as sessões de um utilizador.
 *
 * <p>Corre em {@code SYSTEM} (não há utilizador autenticado); cada operação filtra pelo token
 * ou pelo utilizador indicado.
 */
@Service
public class SessionService {

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private final RefreshTokenRepository refreshTokens;
    private final UserAccountRepository accounts;
    private final OrganizationApi organizations;
    private final TokenService tokenService;
    private final JwtProperties properties;
    private final Clock clock;

    public SessionService(RefreshTokenRepository refreshTokens, UserAccountRepository accounts,
                          OrganizationApi organizations, TokenService tokenService, JwtProperties properties,
                          Clock clock) {
        this.refreshTokens = refreshTokens;
        this.accounts = accounts;
        this.organizations = organizations;
        this.tokenService = tokenService;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Troca um refresh token por um par novo na mesma família; o actual fica revogado
     * ({@code ROTATED}). Cliente: o novo vale mais 60 dias. Staff: não passa do fim da sessão.
     * Os perfis e as permissões são lidos de novo.
     *
     * <p>A linha é lida com {@code PESSIMISTIC_WRITE}: dois pedidos simultâneos com o mesmo
     * token não geram dois sucessores (o segundo vê-o revogado e cai na regra de reutilização).
     * As revogações feitas antes de um erro ficam gravadas ({@code noRollbackFor}).
     *
     * @throws DomainException {@code REFRESH_TOKEN_REUSED} (401) se o token já tinha sido
     *                         renovado: toda a família fica revogada; {@code REFRESH_TOKEN_INVALID}
     *                         (401) nos restantes casos de recusa
     */
    @RunAsSystem
    @Transactional(noRollbackFor = DomainException.class)
    public TokenPair refresh(String refreshToken, String deviceInfo) {
        Instant now = clock.instant();
        RefreshToken current = find(refreshToken).orElseThrow(SessionService::invalid);
        if (current.isRevoked()) {
            if (current.getRevokedReason() == RevokedReason.ROTATED) {
                int revoked = revokeFamily(current.getFamilyId(), RevokedReason.REUSE_DETECTED, now);
                log.warn("Refresh token reuse detected: revoked session family {} of user {} ({} tokens)",
                        current.getFamilyId(), current.getUserId(), revoked);
                throw new DomainException(ErrorCodes.REFRESH_TOKEN_REUSED, 401, "Refresh token was already used");
            }
            throw invalid();
        }
        if (!current.getExpiresAt().isAfter(now)
                || current.getSessionExpiresAt() != null && !current.getSessionExpiresAt().isAfter(now)) {
            throw invalid();
        }
        UserAccount account = accounts.findById(current.getUserId())
                .filter(a -> a.getStatus() == AccountStatus.ACTIVE)
                .orElseThrow(SessionService::invalid);
        if (account.isStaff() && account.getOrganizationId() != null) {
            Optional<OrganizationSummary> organization = organizations.findSummary(account.getOrganizationId());
            if (organization.map(org -> org.status() == OrganizationStatus.SUSPENDED).orElse(true)) {
                revokeFamily(current.getFamilyId(), RevokedReason.ORG_SUSPENDED, now);
                throw invalid();
            }
        }

        Instant expiresAt = account.isStaff() && current.getSessionExpiresAt() != null
                ? current.getSessionExpiresAt()
                : now.plus(properties.customerRefreshTtl());
        TokenService.IssuedRefreshToken next = tokenService.newRefreshToken(account.getId(), current.getFamilyId(),
                expiresAt, current.getSessionExpiresAt(), deviceInfo);
        current.revoke(RevokedReason.ROTATED, now, next.id());
        return new TokenPair(tokenService.accessToken(account, now), next.value(), tokenService.expiresInSeconds());
    }

    /**
     * Revoga a família do token ({@code LOGOUT}), se existir. Um token desconhecido não é erro.
     * O token de acesso continua válido até expirar (no máximo 15 min).
     */
    @RunAsSystem
    @Transactional
    public void logout(String refreshToken) {
        find(refreshToken).ifPresent(token ->
                revokeFamily(token.getFamilyId(), RevokedReason.LOGOUT, clock.instant()));
    }

    /**
     * Revoga todas as sessões activas do utilizador (ex.: reposição de palavra-passe, conta
     * desactivada). Devolve o número de tokens revogados.
     */
    @RunAsSystem
    @Transactional
    public int revokeAllForUser(UUID userId, RevokedReason reason) {
        Instant now = clock.instant();
        List<RefreshToken> active = refreshTokens.findByUserIdAndRevokedAtIsNull(userId);
        active.forEach(token -> token.revoke(reason, now, null));
        return active.size();
    }

    private Optional<RefreshToken> find(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return Optional.empty();
        }
        return refreshTokens.findByTokenHashForUpdate(TokenService.hash(refreshToken));
    }

    private int revokeFamily(UUID familyId, RevokedReason reason, Instant now) {
        List<RefreshToken> active = refreshTokens.findByFamilyIdAndRevokedAtIsNull(familyId);
        active.forEach(token -> token.revoke(reason, now, null));
        return active.size();
    }

    private static DomainException invalid() {
        return new DomainException(ErrorCodes.REFRESH_TOKEN_INVALID, 401, "Refresh token is not valid");
    }
}
