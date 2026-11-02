package mz.co.moseguros.organization.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class OrganizationTest {

    @ParameterizedTest
    @EnumSource(value = OrganizationType.class, names = {"INSURER", "BROKER", "AGENT"})
    void licenseNumberIsRequiredForInsurersAndIntermediaries(OrganizationType type) {
        assertThatThrownBy(() -> create(type, " "))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("LICENSE_NUMBER_REQUIRED");
                    assertThat(e.getHttpStatus()).isEqualTo(400);
                });
        assertThat(create(type, "ASFPM-001").getLicenseNumber()).isEqualTo("ASFPM-001");
    }

    @ParameterizedTest
    @EnumSource(value = OrganizationType.class, names = {"INSURER", "BROKER", "AGENT"}, mode = EnumSource.Mode.EXCLUDE)
    void licenseNumberIsOptionalForOthers(OrganizationType type) {
        assertThat(create(type, null).getLicenseNumber()).isNull();
    }

    @Test
    void createdActiveWithNormalizedContacts() {
        Organization org = Organization.create(OrganizationType.INSURER, "Seguradora Exemplo, SA", null,
                Nuit.of("400123456"), "ASFPM-7", " Geral@Exemplo.CO.MZ", "+258 21 123 456", null, Province.SOFALA);

        assertThat(org.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        assertThat(org.isActive()).isTrue();
        assertThat(org.getContactEmail()).isEqualTo("geral@exemplo.co.mz");
        assertThat(org.getContactPhone()).isEqualTo("+25821123456");
        assertThat(org.displayName()).isEqualTo("Seguradora Exemplo, SA");
        assertThat(org.getProvince()).isEqualTo(Province.SOFALA);
    }

    @Test
    void tradeNameIsShownWhenPresent() {
        Organization org = Organization.create(OrganizationType.WORKSHOP, "Oficina Central, Lda", "Oficina Central",
                Nuit.of("400123457"), null, "oficina@exemplo.mz", null, null, null);
        assertThat(org.displayName()).isEqualTo("Oficina Central");
    }

    @Test
    void invalidContactsAreRejected() {
        assertThatThrownBy(() -> Organization.create(OrganizationType.WORKSHOP, "X", null, Nuit.of("400123457"),
                null, "invalid", null, null, null))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("INVALID_EMAIL"));
        assertThatThrownBy(() -> Organization.create(OrganizationType.WORKSHOP, "X", null, Nuit.of("400123457"),
                null, "a@b.mz", "12-34", null, null))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode()).isEqualTo("INVALID_PHONE"));
        assertThatThrownBy(() -> Organization.create(OrganizationType.WORKSHOP, " ", null, Nuit.of("400123457"),
                null, "a@b.mz", null, null, null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void suspendAndReactivate() {
        Organization org = create(OrganizationType.BROKER, "L-1");

        org.suspend();
        assertThat(org.getStatus()).isEqualTo(OrganizationStatus.SUSPENDED);
        assertThatThrownBy(org::suspend)
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("INVALID_STATE_TRANSITION");
                    assertThat(e.getHttpStatus()).isEqualTo(409);
                });

        org.reactivate();
        assertThat(org.getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        assertThatThrownBy(org::reactivate)
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("INVALID_STATE_TRANSITION"));
    }

    @Test
    void insurerProfileRules() {
        Organization insurer = create(OrganizationType.INSURER, "L-2");
        Organization ombudsman = create(OrganizationType.OMBUDSMAN, null);
        Organization workshop = create(OrganizationType.WORKSHOP, null);

        InsurerProfile profile = InsurerProfile.create(insurer, "SEGEX", "Reclamacoes@Exemplo.MZ", ombudsman);
        assertThat(profile.getOrganizationId()).isEqualTo(insurer.getId());
        assertThat(profile.getComplaintsEmail()).isEqualTo("reclamacoes@exemplo.mz");
        assertThat(profile.getOmbudsmanOrgId()).isEqualTo(ombudsman.getId());

        assertThatThrownBy(() -> InsurerProfile.create(workshop, "SEGEX", "a@b.mz", null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> InsurerProfile.create(insurer, "SEGEX", "a@b.mz", workshop))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> InsurerProfile.create(insurer, "segex", "a@b.mz", null))
                .isInstanceOf(DomainException.class);
    }

    private static Organization create(OrganizationType type, String license) {
        return Organization.create(type, "Organização " + type, null, Nuit.of("400000001"), license,
                "geral@org.mz", null, null, null);
    }
}
