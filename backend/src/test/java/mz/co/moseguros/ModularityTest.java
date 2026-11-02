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

    /** S1-02: identity só usa shared e a API pública de organization; organization nunca usa identity. */
    @Test
    void identityDependsOnOrganizationButNotTheOtherWayAround() {
        assertThat(dependenciesOf("identity")).contains("organization").isSubsetOf("shared", "organization");
        assertThat(dependenciesOf("organization")).isSubsetOf("shared");
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
