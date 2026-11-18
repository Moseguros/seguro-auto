package mz.co.moseguros;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Garante que nenhum módulo acede ao interior de outro (ADR-001) e gera a
 * documentação dos módulos em {@code target/spring-modulith-docs}.
 */
class ModularityTest {

    private final ApplicationModules modules = ApplicationModules.of(MoSegurosApplication.class);

    @Test
    void modulesRespectTheirBoundaries() {
        modules.verify();
    }

    /**
     * S1-02 e S1-03: identity só usa shared e as APIs públicas de organization e notification;
     * organization e notification nunca usam identity. Desde o S4-01 também usa document (fotografias
     * do KYC; modelo transversal §1: {@code identity → organization, notification, document}).
     */
    @Test
    void identityDependsOnOrganizationNotificationAndDocumentOnly() {
        assertThat(dependenciesOf("identity")).contains("organization", "notification", "document")
                .isSubsetOf("shared", "organization", "notification", "document");
        assertThat(dependenciesOf("organization")).isSubsetOf("shared");
        assertThat(dependenciesOf("notification")).isSubsetOf("shared");
    }

    /** S2-01: document só depende de shared (os módulos donos dependem dele, nunca o contrário). */
    @Test
    void documentDependsOnSharedOnly() {
        assertThat(dependenciesOf("document")).isSubsetOf("shared");
    }

    /**
     * S2-02 e S2-04: catalog depende de shared, organization e document (modelo transversal §1:
     * {@code catalog → organization, document}); os outros módulos usam as tabelas de referência e
     * os formatos de matrícula pelas interfaces de shared ({@code ReferenceValueLookup},
     * {@code PlateFormatProvider}), sem dependerem do catalog. organization e document não
     * dependem do catalog.
     */
    @Test
    void catalogDependsOnOrganizationAndDocumentOnly() {
        assertThat(dependenciesOf("catalog")).contains("organization", "document")
                .isSubsetOf("shared", "organization", "document");
        assertThat(dependenciesOf("organization")).doesNotContain("catalog");
        assertThat(dependenciesOf("document")).doesNotContain("catalog");
        assertThat(ApplicationModules.class.getClassLoader()
                .getResource("mz/co/moseguros/shared/config/ConfigPlateFormatProvider.class")).isNull();
    }

    /**
     * S2-03: vehicle só usa document e shared (o vehicle → identity do grafo ainda não é usado). Desde
     * o S3-02 o rating depende de vehicle (snapshots do veículo e do condutor nas cotações) e desde o
     * S4-02 também o proposal (snapshots na proposta).
     */
    @Test
    void vehicleDependsOnDocumentAndSharedAndOnlyRatingAndProposalDependOnVehicle() {
        assertThat(dependenciesOf("vehicle")).contains("document").isSubsetOf("shared", "document");
        assertThat(modules.stream()
                .filter(m -> !m.getName().equals("vehicle"))
                .filter(m -> dependenciesOf(m.getName()).contains("vehicle"))
                .map(ApplicationModule::getName))
                .containsOnly("rating", "proposal");
    }

    /**
     * S3-01 e S3-02: rating só usa catalog, vehicle, organization e identity (modelo transversal §1:
     * {@code rating → catalog, vehicle, organization, identity, notification}; o notification entra
     * no S6). Desde o S4-02 só o proposal depende de rating ({@code RatingApi.quote}/{@code markUsed}).
     */
    @Test
    void ratingDependsOnCatalogVehicleOrganizationAndIdentityAndOnlyProposalDependsOnRating() {
        assertThat(dependenciesOf("rating")).contains("catalog", "vehicle", "organization", "identity")
                .isSubsetOf("shared", "catalog", "vehicle", "organization", "identity");
        assertThat(modules.stream()
                .filter(m -> !m.getName().equals("rating"))
                .filter(m -> dependenciesOf(m.getName()).contains("rating"))
                .map(ApplicationModule::getName))
                .containsOnly("proposal");
    }

    /**
     * S4-02 a S4-05: proposal usa rating, catalog, vehicle, identity, organization, document (as
     * fotografias da vistoria, S4-03, e o PDF e a assinatura, S4-04) e notification (os e-mails da
     * submissão, da decisão e do alerta de SLA, S4-05); modelo transversal §1: {@code proposal → rating,
     * catalog, vehicle, identity, organization, document, notification, payment}; payment entra no S5.
     * Nenhum módulo depende de proposal nesta fase (o catalog guarda as regras de aceitação sem conhecer
     * o proposal). O código de assinatura passa pelo identity ({@code identity → notification}); o
     * document continua a depender só do shared.
     */
    @Test
    void proposalDependsOnRatingCatalogVehicleIdentityOrganizationDocumentAndNotificationOnly() {
        assertThat(dependenciesOf("proposal"))
                .contains("rating", "catalog", "vehicle", "identity", "organization", "document", "notification")
                .isSubsetOf("shared", "rating", "catalog", "vehicle", "identity", "organization", "document",
                        "notification");
        assertThat(dependenciesOf("catalog")).doesNotContain("proposal");
        assertThat(dependenciesOf("identity")).contains("notification");
        assertThat(dependenciesOf("document")).isSubsetOf("shared");
        assertThat(modules.stream()
                .filter(m -> !m.getName().equals("proposal"))
                .filter(m -> dependenciesOf(m.getName()).contains("proposal"))
                .map(ApplicationModule::getName))
                .isEmpty();
    }

    /**
     * S5-01: payment só depende de shared e organization (modelo transversal §1: {@code payment →
     * organization, notification, document}; notification e document entram nas tarefas seguintes do
     * S5 e no S6). Nenhum módulo depende ainda de payment ({@code PaymentApi} entra no S5-02).
     */
    @Test
    void paymentDependsOnSharedAndOrganizationOnly() {
        assertThat(dependenciesOf("payment")).contains("organization").isSubsetOf("shared", "organization");
        assertThat(modules.stream()
                .filter(m -> !m.getName().equals("payment"))
                .filter(m -> dependenciesOf(m.getName()).contains("payment"))
                .map(ApplicationModule::getName))
                .isEmpty();
    }

    private Set<String> dependenciesOf(String name) {
        ApplicationModule module = modules.getModuleByName(name).orElseThrow();
        return module.getDependencies(modules).stream()
                .map(d -> d.getTargetModule().getName())
                .collect(Collectors.toSet());
    }

    @Test
    void writesModuleDocumentation() {
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()
                .writeModuleCanvases();
    }
}
