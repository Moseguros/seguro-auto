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
     * organization e notification nunca usam identity.
     */
    @Test
    void identityDependsOnOrganizationAndNotificationOnly() {
        assertThat(dependenciesOf("identity")).contains("organization", "notification")
                .isSubsetOf("shared", "organization", "notification");
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
