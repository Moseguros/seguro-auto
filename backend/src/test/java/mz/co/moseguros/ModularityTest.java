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
