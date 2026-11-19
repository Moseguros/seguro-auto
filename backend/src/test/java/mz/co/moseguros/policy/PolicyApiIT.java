package mz.co.moseguros.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.policy.PolicyTestSupport.Row;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@code PolicyApi} (S6-01 §9) chamado sem utilizador (âmbito {@code NONE}, como a verificação pública do
 * S6-03): as três prioridades do {@code findLatestByPlate}, o {@code findByCertificateToken} (com o estado
 * do próprio certificado), o {@code findActiveByPlate} e o {@code coverAt}; o {@code view} no âmbito de
 * quem chama.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PolicyApiIT {

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    IntentStateService states;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    PolicyApi api;

    PolicyTestSupport support;
    UUID insurer;
    UUID holder;
    Instant now;

    @BeforeAll
    void seed() {
        support = new PolicyTestSupport(fixtures, identityApi, clock, tx, states);
        insurer = support.payments().insurer("Seguradora Api Apólices").id();
        holder = support.payments().customer().id();
    }

    @BeforeEach
    void none() {
        now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        TenantContext.set(TenantSnapshot.none());
    }

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    UUID policy(String plate, String status, Duration startFromNow, Duration endFromNow) {
        return support.insert(Row.of(insurer, holder, plate, status, now.plus(startFromNow), now.plus(endFromNow)));
    }

    @Test
    void findLatestByPlatePrefersTheCoveringPolicy() {
        String plate = PolicyTestSupport.plate();
        UUID old = policy(plate, "EXPIRED", Duration.ofDays(-400), Duration.ofDays(-35));
        UUID upcoming = policy(plate, "ACTIVE", Duration.ofDays(20), Duration.ofDays(385));
        UUID shorter = policy(plate, "ACTIVE", Duration.ofDays(-10), Duration.ofDays(100));
        UUID longer = policy(plate, "ACTIVE", Duration.ofDays(-5), Duration.ofDays(200));
        UUID certificate = support.certificate(longer, "VALID", PolicyTestSupport.token());

        // (a) a que cobre agora; havendo várias, a de end_at maior
        assertThat(api.findLatestByPlate(plate, now)).hasValueSatisfying(v -> {
            assertThat(v.policyId()).isEqualTo(longer);
            assertThat(v.plate()).isEqualTo(plate);
            assertThat(v.vehicleMake()).isEqualTo("Toyota");
            assertThat(v.vehicleModel()).isEqualTo("Corolla");
            assertThat(v.status()).isEqualTo(PolicyStatus.ACTIVE);
            assertThat(v.coverageType()).isEqualTo(CoverageType.MTPL_ONLY);
            assertThat(v.insurerId()).isEqualTo(insurer);
            assertThat(v.certificateId()).isEqualTo(certificate);
            assertThat(v.certificateNumber()).startsWith("CT-");
            assertThat(v.certificateStatus()).isEqualTo(CertificateStatus.VALID);
        });
        assertThat(api.findActiveByPlate(plate, now)).map(PolicyCoverView::policyId).contains(longer);
        // a matrícula escrita de outra forma é normalizada
        assertThat(api.findActiveByPlate(plate.replace("-", "").toLowerCase(), now))
                .map(PolicyCoverView::policyId).contains(longer);
        // sem certificado válido
        assertThat(api.findActiveByPlate(plate, now.minus(Duration.ofDays(7)))).hasValueSatisfying(v -> {
            assertThat(v.policyId()).isEqualTo(shorter);
            assertThat(v.certificateId()).isNull();
            assertThat(v.certificateStatus()).isNull();
        });
        assertThat(old).isNotEqualTo(upcoming);
    }

    @Test
    void findLatestByPlateThenPrefersTheNextActivePolicy() {
        String plate = PolicyTestSupport.plate();
        policy(plate, "EXPIRED", Duration.ofDays(-400), Duration.ofDays(-35));
        policy(plate, "ACTIVE", Duration.ofDays(60), Duration.ofDays(425));
        UUID next = policy(plate, "ACTIVE", Duration.ofDays(20), Duration.ofDays(385));
        policy(plate, "CANCELLED", Duration.ofDays(10), Duration.ofDays(900));

        assertThat(api.findActiveByPlate(plate, now)).isEmpty();
        assertThat(api.findLatestByPlate(plate, now)).map(PolicyCoverView::policyId).contains(next);
    }

    @Test
    void findLatestByPlateFinallyReturnsTheOneThatEndsLast() {
        String plate = PolicyTestSupport.plate();
        policy(plate, "EXPIRED", Duration.ofDays(-800), Duration.ofDays(-435));
        UUID latest = policy(plate, "CANCELLED", Duration.ofDays(-100), Duration.ofDays(265));
        policy(plate, "EXPIRED", Duration.ofDays(-400), Duration.ofDays(-35));

        assertThat(api.findActiveByPlate(plate, now)).isEmpty();
        assertThat(api.findLatestByPlate(plate, now)).hasValueSatisfying(v -> {
            assertThat(v.policyId()).isEqualTo(latest);
            assertThat(v.status()).isEqualTo(PolicyStatus.CANCELLED);
        });
        assertThat(api.findLatestByPlate(PolicyTestSupport.plate(), now)).isEmpty();
    }

    @Test
    void findByCertificateTokenReturnsThatCertificateStatus() {
        String plate = PolicyTestSupport.plate();
        UUID policy = policy(plate, "ACTIVE", Duration.ofDays(-5), Duration.ofDays(200));
        String revokedToken = PolicyTestSupport.token();
        UUID revoked = support.certificate(policy, "REVOKED", revokedToken);
        String validToken = PolicyTestSupport.token();
        UUID valid = support.certificate(policy, "VALID", validToken);

        assertThat(api.findByCertificateToken(revokedToken)).hasValueSatisfying(v -> {
            assertThat(v.policyId()).isEqualTo(policy);
            assertThat(v.certificateId()).isEqualTo(revoked);
            assertThat(v.certificateStatus()).isEqualTo(CertificateStatus.REVOKED);
        });
        assertThat(api.findByCertificateToken(validToken)).hasValueSatisfying(v -> {
            assertThat(v.certificateId()).isEqualTo(valid);
            assertThat(v.certificateStatus()).isEqualTo(CertificateStatus.VALID);
        });
        // em qualquer estado da apólice
        support.owner().update("update policy.policy set status = 'EXPIRED' where id = ?", policy);
        assertThat(api.findByCertificateToken(validToken)).map(PolicyCoverView::status).contains(PolicyStatus.EXPIRED);
        assertThat(api.findByCertificateToken(PolicyTestSupport.token())).isEmpty();
        assertThat(api.findByCertificateToken("curto")).isEmpty();
        assertThat(api.findByCertificateToken(null)).isEmpty();
    }

    @Test
    void coverAtAndViewInTheCallerScope() {
        String plate = PolicyTestSupport.plate();
        UUID policy = policy(plate, "ACTIVE", Duration.ofDays(-5), Duration.ofDays(200));
        assertThat(api.coverAt(policy, now)).isEqualTo(CoverStatus.COVERED);
        assertThat(api.coverAt(policy, now.minus(Duration.ofDays(6)))).isEqualTo(CoverStatus.NOT_STARTED);
        assertThat(api.coverAt(policy, now.plus(Duration.ofDays(201)))).isEqualTo(CoverStatus.ENDED);
        assertThatThrownBy(() -> api.coverAt(UUID.randomUUID(), now))
                .isInstanceOfSatisfying(DomainException.class, e -> assertThat(e.getCode())
                        .isEqualTo("POLICY_NOT_FOUND"));

        // view e customerPolicies aplicam o RLS: em NONE não há nada
        assertThatThrownBy(() -> api.view(policy)).isInstanceOfSatisfying(DomainException.class,
                e -> assertThat(e.getCode()).isEqualTo("POLICY_NOT_FOUND"));
        assertThat(api.customerPolicies(holder)).isEmpty();

        TenantContext.set(TenantSnapshot.customer(holder));
        PolicyView view = api.view(policy);
        assertThat(view.policyholderUserId()).isEqualTo(holder);
        assertThat(view.plate()).isEqualTo(plate);
        assertThat(view.coverStatus()).isEqualTo(CoverStatus.COVERED);
        assertThat(view.paymentPlan()).isEqualTo(PaymentPlan.FULL);
        assertThat(view.totalAmount().amount()).isEqualTo(299_900L);
        assertThat(view.coverageCodes()).isEmpty();
        assertThat(api.customerPolicies(holder)).extracting(PolicySummaryView::id).contains(policy);

        TenantContext.set(TenantSnapshot.org(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        assertThatThrownBy(() -> api.view(policy)).isInstanceOf(DomainException.class);
        TenantContext.set(TenantSnapshot.org(UUID.randomUUID(), insurer, insurer));
        assertThat(api.view(policy).insurerId()).isEqualTo(insurer);
    }
}
