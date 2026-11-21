package mz.co.moseguros;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * O ShedLock (modo {@code PROXY_METHOD}, o defeito do {@code @EnableSchedulerLock}) recusa bloquear métodos
 * com valor de retorno primitivo: cada execução lança {@code LockingNotSupportedException} e o job nunca
 * corre. Os testes chamam os jobs directamente, com os jobs desligados, por isso só este teste o apanha.
 */
class SchedulerLockUsageTest {

    private static final JavaClasses MAIN_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("mz.co.moseguros");

    static final ArchRule LOCKED_METHODS_RETURN_VOID = methods()
            .that().areAnnotatedWith(SchedulerLock.class)
            .should().haveRawReturnType(void.class)
            .andShould().beAnnotatedWith(Scheduled.class)
            .because("o ShedLock só bloqueia métodos void; um job que devolve contagens tem um método"
                    + " @Scheduled void que o chama")
            .allowEmptyShould(true);

    @Test
    void schedulerLockOnlyOnVoidScheduledMethods() {
        LOCKED_METHODS_RETURN_VOID.check(MAIN_CLASSES);
    }
}
