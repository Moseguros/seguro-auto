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
    void invitationLifecycle() {
        UUID org = UUID.randomUUID();
        UUID inviter = UUID.randomUUID();
        Invitation invitation = Invitation.create("a@b.mz", "A", org, List.of("INSURER_ADMIN"), HASH,
                NOW.plusSeconds(60), inviter);
        UUID user = UUID.randomUUID();
        invitation.linkUser(user);
        invitation.registerFailedActivation();
        invitation.accept(NOW);
        invitation.revoke(NOW);
        invitation.revoke(NOW.plusSeconds(1));

        assertThat(invitation.getOrganizationId()).isEqualTo(org);
        assertThat(invitation.getInvitedByUserId()).isEqualTo(inviter);
        assertThat(invitation.getUserId()).isEqualTo(user);
        assertThat(invitation.getActivationAttempts()).isEqualTo(1);
        assertThat(invitation.getAcceptedAt()).isEqualTo(NOW);
        assertThat(invitation.getRevokedAt()).isEqualTo(NOW);
        assertThat(invitation.getTokenHash()).isEqualTo(HASH);
        assertThat(invitation.getExpiresAt()).isEqualTo(NOW.plusSeconds(60));
        assertThatThrownBy(() -> Invitation.create("a@b.mz", " ", null, List.of("X"), HASH, NOW, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void challengeConsumptionAndFailure() {
        UUID user = UUID.randomUUID();
        OtpChallenge challenge = OtpChallenge.issue("a@b.mz", IdentifierType.EMAIL, Channel.EMAIL,
                Purpose.VERIFY_CONTACT, user, HASH, NOW.plusSeconds(60), null);
        challenge.markFailed();
        challenge.consume(NOW);

        assertThat(challenge.getDeliveryStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(challenge.getConsumedAt()).isEqualTo(NOW);
        assertThat(challenge.getUserId()).isEqualTo(user);
        assertThat(challenge.getIdentifier()).isEqualTo("a@b.mz");
        assertThat(challenge.getIdentifierType()).isEqualTo(IdentifierType.EMAIL);
        assertThat(challenge.getChannel()).isEqualTo(Channel.EMAIL);
        assertThat(challenge.getPurpose()).isEqualTo(Purpose.VERIFY_CONTACT);
        assertThat(challenge.getCodeHash()).isEqualTo(HASH);
        assertThat(challenge.getExpiresAt()).isEqualTo(NOW.plusSeconds(60));
        assertThat(challenge.getSentAt()).isNull();
    }

    @Test
    void refreshTokenAndPasswordResetAccessors() {
        UUID user = UUID.randomUUID();
        UUID family = UUID.randomUUID();
        RefreshToken token = RefreshToken.issue(user, family, HASH, NOW, NOW.plusSeconds(3600), null);
        assertThat(token.getUserId()).isEqualTo(user);
        assertThat(token.getFamilyId()).isEqualTo(family);
        assertThat(token.getTokenHash()).isEqualTo(HASH);
        assertThat(token.getExpiresAt()).isEqualTo(NOW);
        assertThat(token.getSessionExpiresAt()).isEqualTo(NOW.plusSeconds(3600));
        assertThat(token.getDeviceInfo()).isNull();

        PasswordReset reset = PasswordReset.create(user, HASH, NOW);
        reset.markUsed(NOW);
        assertThat(reset.getUsedAt()).isEqualTo(NOW);
        assertThat(reset.getUserId()).isEqualTo(user);
        assertThat(reset.getTokenHash()).isEqualTo(HASH);
        assertThat(reset.getExpiresAt()).isEqualTo(NOW);
    }

    @Test
    void associationKeys() {
        UUID user = UUID.randomUUID();
        UserRole role = new UserRole(user, "CUSTOMER");
        assertThat(role.isNew()).isTrue();
        assertThat(role.getId()).isEqualTo(new UserRole.Key(user, "CUSTOMER"));
        assertThat(role.getId()).hasSameHashCodeAs(new UserRole.Key(user, "CUSTOMER"));
        assertThat(role.getId()).isNotEqualTo(new UserRole.Key(user, "BROKER"));
        assertThat(role.getId().getUserId()).isEqualTo(user);
        assertThat(role.getId().getRoleCode()).isEqualTo("CUSTOMER");
        assertThat(role.getUserId()).isEqualTo(user);
        assertThat(role.getRoleCode()).isEqualTo("CUSTOMER");
        role.markNotNew();
        assertThat(role.isNew()).isFalse();
        assertThatThrownBy(() -> new UserRole(null, "X")).isInstanceOf(NullPointerException.class);

        assertThat(new RolePermission.Key("CUSTOMER", "self:profile"))
                .isEqualTo(new RolePermission.Key("CUSTOMER", "self:profile"))
                .hasSameHashCodeAs(new RolePermission.Key("CUSTOMER", "self:profile"))
                .isNotEqualTo(new RolePermission.Key("CUSTOMER", "x"));
    }

    @Test
    void roleAssignability() {
        Role role = new Role();
        assertThat(role.isAssignableIn(null)).isTrue(); // org_types null = só fora de organizações
        assertThat(role.isAssignableIn("INSURER")).isFalse();
    }
}
