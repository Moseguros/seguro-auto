package mz.co.moseguros.policy.application;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.policy.CertificateStatus;
import mz.co.moseguros.policy.CoverStatus;
import mz.co.moseguros.policy.PolicyCoverView;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.domain.Certificate;
import mz.co.moseguros.policy.domain.CertificateRepository;
import mz.co.moseguros.policy.domain.Policy;
import mz.co.moseguros.policy.domain.PolicyRepository;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras das apólices entre âmbitos, em {@code SYSTEM} (S6-01 §9 e §9.1): a verificação pública (sem
 * utilizador, S6-03) e o {@link PolicyVehicleRemovalGuard}. Nunca devolvem dados pessoais: o
 * {@link PolicyCoverView} não tem nome nem contactos, e o guard só recebe {@code true}/{@code false}.
 */
@Component
public class PolicySystemLookups {

    /** Estados em que a apólice impede retirar o veículo (§9.1). */
    static final Set<PolicyStatus> BLOCKING = EnumSet.of(PolicyStatus.ACTIVE, PolicyStatus.SUSPENDED);
    private static final Pageable FIRST = PageRequest.of(0, 1);

    private final PolicyRepository policies;
    private final CertificateRepository certificates;
    private final PlateParser plates;
    private final Clock clock;

    public PolicySystemLookups(PolicyRepository policies, CertificateRepository certificates, PlateParser plates,
                               Clock clock) {
        this.policies = policies;
        this.certificates = certificates;
        this.plates = plates;
        this.clock = clock;
    }

    /** Apólice {@code ACTIVE} com {@code start_at <= at < end_at}; havendo várias, a de {@code end_at} maior. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<PolicyCoverView> findActiveByPlate(String plate, Instant at) {
        return covering(format(plate), at).map(this::withValidCertificate);
    }

    /**
     * A primeira que existir: (a) a de {@link #findActiveByPlate}; (b) a {@code ACTIVE} com
     * {@code start_at > at} de {@code start_at} menor; (c) a de {@code end_at} maior, em qualquer estado.
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<PolicyCoverView> findLatestByPlate(String plate, Instant at) {
        String formatted = format(plate);
        Optional<Policy> found = covering(formatted, at);
        if (found.isEmpty()) {
            found = policies.findUpcoming(formatted, at, FIRST).stream().findFirst();
        }
        if (found.isEmpty()) {
            found = policies.findLatest(formatted, FIRST).stream().findFirst();
        }
        return found.map(this::withValidCertificate);
    }

    /** A apólice do certificado com este código, em qualquer estado; o estado é o desse certificado. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<PolicyCoverView> findByCertificateToken(String token) {
        if (token == null || token.length() != VerificationTokens.LENGTH) {
            return Optional.empty();
        }
        return certificates.findByVerificationToken(token)
                .flatMap(c -> policies.findById(c.getPolicyId()).map(p -> view(p, c)));
    }

    /** {@code Policy.coverAt(at)}. @throws DomainException 404 {@code POLICY_NOT_FOUND} */
    @RunAsSystem
    @Transactional(readOnly = true)
    public CoverStatus coverAt(UUID policyId, Instant at) {
        return policies.findById(policyId).orElseThrow(PolicyQueryService::notFound).coverAt(at);
    }

    /**
     * Existe apólice {@code ACTIVE}/{@code SUSPENDED} do veículo com {@code end_at > agora} (§9.1), em
     * qualquer âmbito.
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public boolean vehicleHasPolicyInForce(UUID vehicleId) {
        return policies.existsInForce(vehicleId, BLOCKING, clock.instant());
    }

    private Optional<Policy> covering(String plate, Instant at) {
        return policies.findCovering(plate, at, FIRST).stream().findFirst();
    }

    private PolicyCoverView withValidCertificate(Policy p) {
        return view(p, certificates.findByPolicyIdAndStatus(p.getId(), CertificateStatus.VALID).orElse(null));
    }

    private static PolicyCoverView view(Policy p, Certificate c) {
        return new PolicyCoverView(p.getId(), p.getNumber(), p.getInsurerId(), p.getPlate(),
                p.getVehicleSnapshot().path("make").asText(null), p.getVehicleSnapshot().path("model").asText(null),
                p.getCoverageType(), p.getStatus(), p.getStartAt(), p.getEndAt(),
                c == null ? null : c.getId(), c == null ? null : c.getNumber(), c == null ? null : c.getStatus());
    }

    /** Matrícula formatada; o texto tal como veio se não corresponder a nenhum formato activo. */
    private String format(String plate) {
        if (plate == null) {
            return "";
        }
        try {
            return plates.parse(plate).value();
        } catch (DomainException e) {
            return plate.trim();
        }
    }
}
