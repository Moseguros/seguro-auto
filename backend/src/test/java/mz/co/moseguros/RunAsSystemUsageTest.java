package mz.co.moseguros;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
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

    private static final DescribedPredicate<JavaClass> ALLOWED_OWNERS = new DescribedPredicate<>(
            "classes de identity/notification, ou classes application …Listener/…Job/<Modulo>SystemLookups") {
        @Override
        public boolean test(JavaClass owner) {
            String pkg = owner.getPackageName();
            if (pkg.startsWith("mz.co.moseguros.identity") || pkg.startsWith("mz.co.moseguros.notification")) {
                return true;
            }
            String name = owner.getSimpleName();
            boolean inApplication = pkg.matches("mz\\.co\\.moseguros\\.[a-z]+\\.application(\\..*)?");
            boolean modulePrefixedLookups = name.endsWith("SystemLookups") && name.length() > "SystemLookups".length();
            return inApplication && (name.endsWith("Listener") || name.endsWith("Job") || modulePrefixedLookups);
        }
    };

    private static final JavaClasses MAIN_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("mz.co.moseguros");

    static final ArchRule RUN_AS_SYSTEM_OWNERS = methods()
            .that().areAnnotatedWith(RunAsSystem.class)
            .should().beDeclaredInClassesThat(ALLOWED_OWNERS)
            .because("@RunAsSystem desliga o isolamento por âmbito; só identity, notification e, nos outros"
                    + " módulos, classes application …Listener, …Job ou <Modulo>SystemLookups o podem usar")
            .allowEmptyShould(true);

    @Test
    void runAsSystemIsOnlyUsedWhereAllowed() {
        RUN_AS_SYSTEM_OWNERS.check(MAIN_CLASSES);
    }

    /** S2-01: o download e a limpeza de órfãos do document correm em SYSTEM, em classes permitidas. */
    @Test
    void documentSystemLookupsAndOrphanCleanupJobAreAllowed() {
        JavaClasses document = new ClassFileImporter().importClasses(
                mz.co.moseguros.document.application.DocumentSystemLookups.class,
                mz.co.moseguros.document.application.OrphanFileCleanupJob.class);
        RUN_AS_SYSTEM_OWNERS.check(document);
        org.assertj.core.api.Assertions.assertThat(document.stream()
                        .flatMap(c -> c.getMethods().stream())
                        .filter(m -> m.isAnnotatedWith(RunAsSystem.class))
                        .map(m -> m.getOwner().getSimpleName() + "." + m.getName()))
                .contains("DocumentSystemLookups.find", "OrphanFileCleanupJob.purgeOrphans");
    }

    /** S2-04: o catálogo público e o CatalogApi lêem entre seguradoras só pelo CatalogSystemLookups. */
    @Test
    void catalogSystemLookupsIsAllowed() {
        JavaClasses catalog = new ClassFileImporter().importClasses(
                mz.co.moseguros.catalog.application.CatalogSystemLookups.class);
        RUN_AS_SYSTEM_OWNERS.check(catalog);
        org.assertj.core.api.Assertions.assertThat(catalog.stream()
                        .flatMap(c -> c.getMethods().stream())
                        .filter(m -> m.isAnnotatedWith(RunAsSystem.class))
                        .map(m -> m.getOwner().getSimpleName() + "." + m.getName()))
                .containsExactlyInAnyOrder("CatalogSystemLookups.publishedVersions",
                        "CatalogSystemLookups.publishedVersion", "CatalogSystemLookups.versionAccess");
    }

    /** S3-01: os perfis de seguradora (OrganizationApi.insurerProfile/insurerProfiles) lêem-se em SYSTEM. */
    @Test
    void organizationSystemLookupsIsAllowed() {
        JavaClasses organization = new ClassFileImporter().importClasses(
                mz.co.moseguros.organization.application.OrganizationSystemLookups.class);
        RUN_AS_SYSTEM_OWNERS.check(organization);
        org.assertj.core.api.Assertions.assertThat(organization.stream()
                        .flatMap(c -> c.getMethods().stream())
                        .filter(m -> m.isAnnotatedWith(RunAsSystem.class))
                        .map(m -> m.getOwner().getSimpleName() + "." + m.getName()))
                .containsExactlyInAnyOrder("OrganizationSystemLookups.insurerProfile",
                        "OrganizationSystemLookups.insurerProfiles");
    }

    /**
     * S4-02 e S4-06: a seguradora da cotação e a organização do mediador lêem-se e as propostas expiram em
     * SYSTEM, em classes permitidas.
     */
    @Test
    void proposalSystemLookupsAndExpiryJobAreAllowed() {
        JavaClasses proposal = new ClassFileImporter().importClasses(
                mz.co.moseguros.proposal.application.ProposalSystemLookups.class,
                mz.co.moseguros.proposal.application.ProposalExpiryJob.class);
        RUN_AS_SYSTEM_OWNERS.check(proposal);
        org.assertj.core.api.Assertions.assertThat(proposal.stream()
                        .flatMap(c -> c.getMethods().stream())
                        .filter(m -> m.isAnnotatedWith(RunAsSystem.class))
                        .map(m -> m.getOwner().getSimpleName() + "." + m.getName()))
                .containsExactlyInAnyOrder("ProposalSystemLookups.insurer", "ProposalSystemLookups.broker",
                        "ProposalExpiryJob.expire");
    }

    /** S4-06: o veículo e o condutor do cliente registam-se a pedido do mediador só pelo VehicleSystemLookups. */
    @Test
    void vehicleSystemLookupsIsAllowed() {
        JavaClasses vehicle = new ClassFileImporter().importClasses(
                mz.co.moseguros.vehicle.application.VehicleSystemLookups.class);
        RUN_AS_SYSTEM_OWNERS.check(vehicle);
        org.assertj.core.api.Assertions.assertThat(vehicle.stream()
                        .flatMap(c -> c.getMethods().stream())
                        .filter(m -> m.isAnnotatedWith(RunAsSystem.class))
                        .map(m -> m.getOwner().getSimpleName() + "." + m.getName()))
                .containsExactly("VehicleSystemLookups.registerForCustomer");
    }

    /** S5-01: as formas de pagamento lêem-se entre âmbitos só pelo PaymentSystemLookups. */
    @Test
    void paymentSystemLookupsIsAllowed() {
        JavaClasses payment = new ClassFileImporter().importClasses(
                mz.co.moseguros.payment.application.PaymentSystemLookups.class,
                mz.co.moseguros.payment.application.DevPaymentSecretsSeeder.class,
                mz.co.moseguros.payment.application.PaymentMethodConfigService.class);
        RUN_AS_SYSTEM_OWNERS.check(payment);
        org.assertj.core.api.Assertions.assertThat(payment.stream()
                        .flatMap(c -> c.getMethods().stream())
                        .filter(m -> m.isAnnotatedWith(RunAsSystem.class))
                        .map(m -> m.getOwner().getSimpleName() + "." + m.getName()))
                .containsExactlyInAnyOrder("PaymentSystemLookups.enabledConfig", "PaymentSystemLookups.enabledConfigs",
                        "PaymentSystemLookups.entityInUse", "PaymentSystemLookups.configsOf",
                        "PaymentSystemLookups.configById", "PaymentSystemLookups.insurerNames");
    }

    /**
     * S5-02: o envio assíncrono, a expiração das intenções e o ouvinte do pagamento das propostas correm em
     * SYSTEM, em classes …Job e …Listener de application.
     */
    @Test
    void paymentJobsAndProposalPaymentListenerAreAllowed() {
        JavaClasses classes = new ClassFileImporter().importClasses(
                mz.co.moseguros.payment.application.PaymentDispatchJob.class,
                mz.co.moseguros.payment.application.PaymentExpiryJob.class,
                mz.co.moseguros.proposal.application.ProposalPaymentListener.class);
        RUN_AS_SYSTEM_OWNERS.check(classes);
        org.assertj.core.api.Assertions.assertThat(classes.stream()
                        .flatMap(c -> c.getMethods().stream())
                        .filter(m -> m.isAnnotatedWith(RunAsSystem.class))
                        .map(m -> m.getOwner().getSimpleName() + "." + m.getName()))
                .containsExactlyInAnyOrder("PaymentDispatchJob.dispatch", "PaymentExpiryJob.expire",
                        "ProposalPaymentListener.on");
    }

    @Test
    void ruleRejectsUsageInOtherModules() {
        JavaClasses offending = new ClassFileImporter().importClasses(OffendingPolicyService.class);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> RUN_AS_SYSTEM_OWNERS.check(offending))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("OffendingPolicyService");
    }

    /** Fora de identity/notification e sem ser Listener/Job/SystemLookups de application: recusado. */
    static class OffendingPolicyService {
        @RunAsSystem
        void lookEverywhere() {
        }
    }
}
