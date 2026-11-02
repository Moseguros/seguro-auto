package mz.co.moseguros.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.CustomerProfile;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationSummary;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.security.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MeServiceTest {

    CurrentUser currentUser = mock(CurrentUser.class);
    UserAccountRepository accounts = mock(UserAccountRepository.class);
    CustomerProfileRepository profiles = mock(CustomerProfileRepository.class);
    OrganizationApi organizations = mock(OrganizationApi.class);
    MeService service;

    @BeforeEach
    void setUp() {
        service = new MeService(currentUser, accounts, profiles, organizations);
        when(currentUser.roles()).thenReturn(List.of("R"));
        when(currentUser.permissions()).thenReturn(List.of("p:x"));
    }

    @Test
    void customerWithCompleteProfile() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        CustomerProfile profile = CustomerProfile.emptyFor(customer);
        profile.update("Ana Mussa", LocalDate.of(1990, 1, 1), IdDocType.BI, "110100123456B", "123456789", null,
                Province.MAPUTO_CITY);
        given(customer);
        when(profiles.findByUserId(customer.getId())).thenReturn(Optional.of(profile));

        MeService.Me me = service.getMe();

        assertThat(me.type()).isEqualTo(AccountType.CUSTOMER);
        assertThat(me.displayName()).isEqualTo("Ana Mussa");
        assertThat(me.profileComplete()).isTrue();
        assertThat(me.organization()).isNull();
        assertThat(me.roles()).containsExactly("R");
        assertThat(me.permissions()).containsExactly("p:x");
    }

    @Test
    void customerWithoutProfile() {
        UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));
        given(customer);
        when(profiles.findByUserId(customer.getId())).thenReturn(Optional.empty());

        MeService.Me me = service.getMe();

        assertThat(me.displayName()).isNull();
        assertThat(me.profileComplete()).isFalse();
    }

    @Test
    void staffWithOrganization() {
        UUID orgId = UUID.randomUUID();
        UserAccount staff = UserAccount.newStaff("s@seg.co.mz", "Staff", orgId);
        OrganizationSummary summary = new OrganizationSummary(orgId, OrganizationType.INSURER,
                OrganizationStatus.ACTIVE, "Seg");
        given(staff);
        when(organizations.findSummary(orgId)).thenReturn(Optional.of(summary));

        MeService.Me me = service.getMe();

        assertThat(me.displayName()).isEqualTo("Staff");
        assertThat(me.organization()).isEqualTo(summary);
        assertThat(me.profileComplete()).isNull();
    }

    @Test
    void accountNotVisibleIsUnauthorized() {
        UUID id = UUID.randomUUID();
        when(currentUser.id()).thenReturn(id);
        when(accounts.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMe())
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getHttpStatus()).isEqualTo(401));
    }

    private void given(UserAccount account) {
        when(currentUser.id()).thenReturn(account.getId());
        when(accounts.findById(account.getId())).thenReturn(Optional.of(account));
    }
}
