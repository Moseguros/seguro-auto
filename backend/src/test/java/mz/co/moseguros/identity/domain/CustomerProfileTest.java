package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;

class CustomerProfileTest {

    private final UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));

    @Test
    void startsEmptyAndIncomplete() {
        CustomerProfile profile = CustomerProfile.emptyFor(customer);
        assertThat(profile.getUserId()).isEqualTo(customer.getId());
        assertThat(profile.isComplete()).isFalse();
    }

    @Test
    void completeWhenAllRequiredFieldsAreFilled() {
        CustomerProfile profile = CustomerProfile.emptyFor(customer);
        profile.update("Ana Silva", LocalDate.of(1990, 5, 1), IdDocType.BI, "110100123456B",
                "123 456 789", null, Province.MAPUTO_CITY);

        assertThat(profile.isComplete()).isTrue();
        assertThat(profile.getNuit()).isEqualTo("123456789");
        assertThat(profile.getAddress()).isNull();
    }

    @Test
    void anyMissingRequiredFieldMakesItIncomplete() {
        CustomerProfile profile = CustomerProfile.emptyFor(customer);
        profile.update("Ana Silva", LocalDate.of(1990, 5, 1), IdDocType.BI, "110100123456B", "123456789",
                "Av. 24 de Julho", null);
        assertThat(profile.isComplete()).isFalse();

        profile.update("Ana Silva", LocalDate.of(1990, 5, 1), IdDocType.BI, " ", "123456789", null, Province.GAZA);
        assertThat(profile.isComplete()).isFalse();
    }

    @Test
    void invalidNuitIsRejected() {
        CustomerProfile profile = CustomerProfile.emptyFor(customer);
        assertThatThrownBy(() -> profile.update("Ana", null, null, null, "12345", null, null))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("INVALID_NUIT"));
    }

    @Test
    void onlyForCustomerAccounts() {
        UserAccount staff = UserAccount.newStaff("a@b.mz", "A", null);
        assertThatThrownBy(() -> CustomerProfile.emptyFor(staff)).isInstanceOf(IllegalArgumentException.class);
    }
}
