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

    // --- preferências e contactos (S1-06) ---

    @Test
    void preferencesChangeLanguageAndOnlyStaffName() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        customer.updatePreferences("Ignorado", "en");
        assertThat(customer.getPreferredLanguage()).isEqualTo("en");
        assertThat(customer.getDisplayName()).isNull();

        UserAccount staff = UserAccount.newStaff("a@b.mz", "Antigo", null);
        staff.updatePreferences(" Novo Nome ", "pt");
        assertThat(staff.getDisplayName()).isEqualTo("Novo Nome");
        staff.updatePreferences(null, "en");
        assertThat(staff.getDisplayName()).isEqualTo("Novo Nome");
        assertThatThrownBy(() -> staff.updatePreferences(null, "fr")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void replaceContactStoresItVerified() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        OtpIdentifier email = OtpIdentifier.parse("Ana@Exemplo.co.mz", OtpChallenge.IdentifierType.EMAIL, "value");
        assertThat(customer.hasVerifiedContact(email)).isFalse();

        customer.replaceContact(email);
        assertThat(customer.getEmail()).isEqualTo("ana@exemplo.co.mz");
        assertThat(customer.isEmailVerified()).isTrue();
        assertThat(customer.hasVerifiedContact(email)).isTrue();

        OtpIdentifier phone = OtpIdentifier.parse("821112222", OtpChallenge.IdentifierType.MSISDN, "value");
        customer.replaceContact(phone);
        assertThat(customer.getMsisdn().value()).isEqualTo("258821112222");
        assertThat(customer.hasVerifiedContact(phone)).isTrue();
        assertThat(customer.hasVerifiedContact(
                OtpIdentifier.parse("841234567", OtpChallenge.IdentifierType.MSISDN, "value"))).isFalse();
    }

    @Test
    void removeContactKeepsAtLeastOneVerified() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        assertThatThrownBy(() -> customer.removeContact(OtpChallenge.IdentifierType.MSISDN))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("LAST_CONTACT"));
        assertThat(customer.removeContact(OtpChallenge.IdentifierType.EMAIL)).isFalse();

        customer.replaceContact(OtpIdentifier.parse("ana@exemplo.co.mz", OtpChallenge.IdentifierType.EMAIL, "value"));
        assertThat(customer.removeContact(OtpChallenge.IdentifierType.MSISDN)).isTrue();
        assertThat(customer.getMsisdn()).isNull();
        assertThat(customer.isMsisdnVerified()).isFalse();
        assertThatThrownBy(() -> customer.removeContact(OtpChallenge.IdentifierType.EMAIL))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getHttpStatus()).isEqualTo(400));
    }

    @Test
    void staffHasNoCustomerContacts() {
        UserAccount staff = UserAccount.newStaff("a@b.mz", "A", null);
        assertThatThrownBy(() -> staff.removeContact(OtpChallenge.IdentifierType.EMAIL))
                .isInstanceOf(IllegalStateException.class);
    }
}
