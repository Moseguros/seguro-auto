package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.identity.domain.OtpChallenge.DeliveryStatus;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.OtpChallenge.Purpose;
import mz.co.moseguros.identity.domain.RefreshToken.RevokedReason;
import mz.co.moseguros.shared.domain.Channel;
import org.junit.jupiter.api.Test;

class TokensAndChallengesTest {

    private static final Instant NOW = Instant.parse("2026-11-02T09:00:00Z");
    private static final String HASH = "a".repeat(64);

    @Test
    void firstChallengeIsItsOwnRootAndFallbackKeepsTheRoot() {
        OtpChallenge first = OtpChallenge.issue("258841234567", IdentifierType.MSISDN, Channel.SMS, Purpose.LOGIN,
                null, HASH, NOW.plusSeconds(300), null);
        OtpChallenge second = OtpChallenge.issue("258841234567", IdentifierType.MSISDN, Channel.WHATSAPP,
                Purpose.LOGIN, null, HASH, NOW.plusSeconds(300), first);
        OtpChallenge third = OtpChallenge.issue("258841234567", IdentifierType.MSISDN, Channel.EMAIL,
                Purpose.LOGIN, null, HASH, NOW.plusSeconds(300), second);

        assertThat(first.getRootChallengeId()).isEqualTo(first.getId());
        assertThat(first.getFallbackFromChallengeId()).isNull();
        assertThat(first.getDeliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
        assertThat(second.getFallbackFromChallengeId()).isEqualTo(first.getId());
        assertThat(third.getRootChallengeId()).isEqualTo(first.getId());

        first.supersede(NOW);
        first.supersede(NOW.plusSeconds(5));
        assertThat(first.getSupersededAt()).isEqualTo(NOW);
        second.markSent(NOW);
        assertThat(second.getDeliveryStatus()).isEqualTo(DeliveryStatus.SENT);
        second.registerFailedAttempt();
        assertThat(second.getAttempts()).isEqualTo(1);
    }

    @Test
    void refreshTokenTruncatesDeviceInfoAndRevokesOnce() {
        RefreshToken token = RefreshToken.issue(UUID.randomUUID(), UUID.randomUUID(), HASH, NOW.plusSeconds(60),
                null, "x".repeat(300));
        assertThat(token.getDeviceInfo()).hasSize(200);
        assertThat(token.isRevoked()).isFalse();

        UUID next = UUID.randomUUID();
        token.revoke(RevokedReason.ROTATED, NOW, next);
        token.revoke(RevokedReason.LOGOUT, NOW.plusSeconds(1), null);
        assertThat(token.getRevokedReason()).isEqualTo(RevokedReason.ROTATED);
        assertThat(token.getRevokedAt()).isEqualTo(NOW);
        assertThat(token.getReplacedById()).isEqualTo(next);
    }

    @Test
    void invitationNormalizesEmailAndNeedsRoles() {
        Invitation invitation = Invitation.create("Novo@Corretora.MZ", " Novo Utilizador ", UUID.randomUUID(),
                List.of("ORG_ADMIN"), HASH, NOW.plusSeconds(3600), null);
        assertThat(invitation.getEmail()).isEqualTo("novo@corretora.mz");
        assertThat(invitation.getFullName()).isEqualTo("Novo Utilizador");
        assertThat(invitation.getRoleCodes()).containsExactly("ORG_ADMIN");

        assertThatThrownBy(() -> Invitation.create("a@b.mz", "A", null, List.of(), HASH, NOW, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void passwordResetIsInvalidatedOnce() {
        PasswordReset reset = PasswordReset.create(UUID.randomUUID(), HASH, NOW.plusSeconds(1800));
        reset.invalidate(NOW);
        reset.invalidate(NOW.plusSeconds(1));
        assertThat(reset.getInvalidatedAt()).isEqualTo(NOW);
    }

    @Test
    void roleAssignability() {
        Role role = new Role();
        assertThat(role.isAssignableIn(null)).isTrue(); // org_types null = só fora de organizações
        assertThat(role.isAssignableIn("INSURER")).isFalse();
    }
}
