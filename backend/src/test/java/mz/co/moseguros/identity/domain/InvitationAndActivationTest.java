package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.organization.OrganizationType;
import org.junit.jupiter.api.Test;

/**
 * Regras de domínio do S1-04: estado calculado do convite, reenvio, tentativas de activação,
 * credenciais e activação do staff, e perfis de gestão.
 */
class InvitationAndActivationTest {

    static final Instant NOW = Instant.parse("2026-11-02T10:00:00Z");

    @Test
    void invitationStatusIsComputedFromTheDates() {
        Invitation invitation = invitation(NOW.plus(Duration.ofDays(7)));
        assertThat(invitation.status(NOW)).isEqualTo(InvitationStatus.PENDING);
        assertThat(invitation.isUsable(NOW)).isTrue();
        assertThat(invitation.status(NOW.plus(Duration.ofDays(7)))).isEqualTo(InvitationStatus.EXPIRED);

        invitation.accept(NOW);
        assertThat(invitation.status(NOW)).isEqualTo(InvitationStatus.ACCEPTED);
        invitation.revoke(NOW);
        assertThat(invitation.status(NOW)).isEqualTo(InvitationStatus.REVOKED);
        assertThat(invitation.isUsable(NOW)).isFalse();
    }

    @Test
    void revokeKeepsTheFirstDate() {
        Invitation invitation = invitation(NOW.plusSeconds(60));
        invitation.revoke(NOW);
        invitation.revoke(NOW.plusSeconds(5));
        assertThat(invitation.getRevokedAt()).isEqualTo(NOW);
    }

    @Test
    void renewChangesTheTokenAndTheValidity() {
        Invitation invitation = invitation(NOW.minusSeconds(1));
        assertThat(invitation.status(NOW)).isEqualTo(InvitationStatus.EXPIRED);
        invitation.renew("b".repeat(64), NOW.plus(Duration.ofDays(7)));
        assertThat(invitation.getTokenHash()).isEqualTo("b".repeat(64));
        assertThat(invitation.status(NOW)).isEqualTo(InvitationStatus.PENDING);
    }

    @Test
    void failedActivationsAreCounted() {
        Invitation invitation = invitation(NOW.plusSeconds(60));
        assertThat(invitation.registerFailedActivation()).isEqualTo(1);
        assertThat(invitation.registerFailedActivation()).isEqualTo(2);
        assertThat(invitation.getActivationAttempts()).isEqualTo(2);
    }

    @Test
    void staffCredentialsAndActivation() {
        UserAccount staff = UserAccount.newStaff("Ana@Seguradora.co.mz", "Ana", UUID.randomUUID());
        assertThat(staff.hasCredentials()).isFalse();
        assertThatThrownBy(() -> staff.activate(1L)).isInstanceOf(IllegalStateException.class);

        staff.setCredentials("Ana Machava", "$argon2id$hash", "SECRET");
        assertThat(staff.hasCredentials()).isTrue();
        assertThat(staff.getDisplayName()).isEqualTo("Ana Machava");
        assertThat(staff.getStatus()).isEqualTo(AccountStatus.PENDING_ACTIVATION);

        staff.activate(42L);
        assertThat(staff.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(staff.isEmailVerified()).isTrue();
        assertThat(staff.getLastTotpStep()).isEqualTo(42L);

        staff.setCredentials(null, "$argon2id$other", "OTHER");
        assertThat(staff.getDisplayName()).isEqualTo("Ana Machava");
        assertThat(staff.getLastTotpStep()).isNull();
    }

    @Test
    void customersHaveNoStaffCredentials() {
        UserAccount customer = UserAccount.newCustomer("cliente@exemplo.co.mz");
        assertThatThrownBy(() -> customer.setCredentials("X", "hash", "secret"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(customer.hasCredentials()).isFalse();
    }

    @Test
    void managerRoles() {
        assertThat(ManagerRoles.managerRoles(null)).containsExactly("PLATFORM_ADMIN");
        assertThat(ManagerRoles.managerRoles("INSURER")).containsExactly("INSURER_ADMIN");
        assertThat(ManagerRoles.managerRoles("WORKSHOP")).containsExactly("ORG_ADMIN");
        assertThat(ManagerRoles.managerInviteRoles(null)).containsExactly("PLATFORM_ADMIN");
        assertThat(ManagerRoles.managerInviteRoles("INSURER")).containsExactly("INSURER_ADMIN");
        assertThat(ManagerRoles.managerInviteRoles("ASSISTANCE")).containsExactly("ORG_ADMIN", "ASSISTANCE_PROVIDER");
        assertThat(ManagerRoles.managerInviteRoles("CORPORATE_CLIENT")).containsExactly("ORG_ADMIN", "FLEET_MANAGER");
        for (OrganizationType type : OrganizationType.values()) {
            assertThat(ManagerRoles.managerInviteRoles(type.name())).isNotEmpty();
        }
        assertThatThrownBy(() -> ManagerRoles.managerInviteRoles("BANK"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Invitation invitation(Instant expiresAt) {
        return Invitation.create("ana@seguradora.co.mz", "Ana", UUID.randomUUID(), List.of("UNDERWRITER"),
                "a".repeat(64), expiresAt, null);
    }
}
