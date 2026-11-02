package mz.co.moseguros;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.junit.jupiter.api.Test;

/**
 * {@code @RunAsSystem} (vê todas as linhas) só pode ser usado nos módulos {@code identity} e
 * {@code notification} (ADR-002, adenda de 01/11/2026).
 */
class RunAsSystemUsageTest {

    private static final JavaClasses MAIN_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("mz.co.moseguros");

    static final ArchRule ONLY_IDENTITY_AND_NOTIFICATION = methods()
            .that().areAnnotatedWith(RunAsSystem.class)
            .should().beDeclaredInClassesThat()
            .resideInAnyPackage("mz.co.moseguros.identity..", "mz.co.moseguros.notification..")
            .because("@RunAsSystem desliga o isolamento por âmbito; só identity e notification o podem usar")
            .allowEmptyShould(true);

    @Test
    void runAsSystemIsOnlyUsedInIdentityAndNotification() {
        ONLY_IDENTITY_AND_NOTIFICATION.check(MAIN_CLASSES);
    }

    @Test
    void ruleRejectsUsageInOtherModules() {
        JavaClasses offending = new ClassFileImporter().importClasses(OffendingPolicyService.class);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> ONLY_IDENTITY_AND_NOTIFICATION.check(offending))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("OffendingPolicyService");
    }

    /** Fora de identity/notification: a regra tem de o recusar. */
    static class OffendingPolicyService {
        @RunAsSystem
        void lookEverywhere() {
        }
    }
}
