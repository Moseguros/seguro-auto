package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;

class UserAccountTest {

    @Test
    void customerByMsisdnIsActiveWithVerifiedPhone() {
        UserAccount account = UserAccount.newCustomer(Msisdn.of("84 123 4567"));

        assertThat(account.getType()).isEqualTo(AccountType.CUSTOMER);
        assertThat(account.isCustomer()).isTrue();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getMsisdn().value()).isEqualTo("258841234567");
        assertThat(account.isMsisdnVerified()).isTrue();
        assertThat(account.getEmail()).isNull();
        assertThat(account.isEmailVerified()).isFalse();
        assertThat(account.getOrganizationId()).isNull();
        assertThat(account.getPreferredLanguage()).isEqualTo("pt");
        assertThat(account.getId().version()).isEqualTo(7);
    }

    @Test
    void customerByEmailIsNormalizedAndVerified() {
        UserAccount account = UserAccount.newCustomer(" Ana@Exemplo.CO.MZ ");

        assertThat(account.getEmail()).isEqualTo("ana@exemplo.co.mz");
        assertThat(account.isEmailVerified()).isTrue();
        assertThat(account.getMsisdn()).isNull();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void customerWithInvalidEmailIsRejected() {
        assertThatThrownBy(() -> UserAccount.newCustomer("not-an-email"))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("INVALID_EMAIL"));
    }

    @Test
    void staffIsPendingActivation() {
        UUID orgId = UUID.randomUUID();
        UserAccount account = UserAccount.newStaff("Gestor@Seguradora.co.mz", " Gestor ", orgId);

        assertThat(account.getType()).isEqualTo(AccountType.STAFF);
        assertThat(account.isStaff()).isTrue();
        assertThat(account.getStatus()).isEqualTo(AccountStatus.PENDING_ACTIVATION);
        assertThat(account.getEmail()).isEqualTo("gestor@seguradora.co.mz");
        assertThat(account.isEmailVerified()).isFalse();
        assertThat(account.getDisplayName()).isEqualTo("Gestor");
        assertThat(account.getOrganizationId()).isEqualTo(orgId);
        assertThat(account.getMsisdn()).isNull();
        assertThat(account.getPasswordHash()).isNull();
        assertThat(account.getFailedAttempts()).isZero();
    }

    @Test
    void platformStaffHasNoOrganization() {
        assertThat(UserAccount.newStaff("admin@moseguros.co.mz", "Admin", null).getOrganizationId()).isNull();
    }

    @Test
    void staffNeedsValidEmail() {
        assertThatThrownBy(() -> UserAccount.newStaff(null, "X", null)).isInstanceOf(DomainException.class);
    }
}
