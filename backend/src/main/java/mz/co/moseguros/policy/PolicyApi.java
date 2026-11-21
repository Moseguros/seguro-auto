package mz.co.moseguros.policy;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.policy.application.PolicyQueryService;
import mz.co.moseguros.policy.application.PolicySystemLookups;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Apólices (modelo transversal §3; S6-01 §9). {@code recordClaimOutcome}
 * chega no S7.
 *
 * <p>{@link #findActiveByPlate}, {@link #findLatestByPlate}, {@link #findByCertificateToken} e
 * {@link #coverAt} correm em {@code SYSTEM} ({@code PolicySystemLookups}): são chamados pela verificação
 * pública, sem utilizador, e nunca devolvem dados pessoais. {@link #view} e {@link #customerPolicies}
 * correm no âmbito de quem chama (RLS). O S6-03 depende exactamente destes nomes.
 */
@Service
public class PolicyApi {

    private final PolicySystemLookups lookups;
    private final PolicyQueryService queries;

    public PolicyApi(PolicySystemLookups lookups, PolicyQueryService queries) {
        this.lookups = lookups;
        this.queries = queries;
    }

    /**
     * Apólice {@code ACTIVE} com {@code start_at <= at < end_at} para a matrícula (formatada); havendo
     * várias, a de {@code end_at} maior.
     */
    public Optional<PolicyCoverView> findActiveByPlate(String plate, Instant at) {
        return lookups.findActiveByPlate(plate, at);
    }

    /** Cobertura da apólice em {@code at}. @throws mz.co.moseguros.shared.error.DomainException 404 {@code POLICY_NOT_FOUND} */
    public CoverStatus coverAt(UUID policyId, Instant at) {
        return lookups.coverAt(policyId, at);
    }

    /** Apólice no âmbito de quem chama. @throws mz.co.moseguros.shared.error.DomainException 404 {@code POLICY_NOT_FOUND} */
    public PolicyView view(UUID policyId) {
        return queries.view(policyId);
    }

    /** Apólices do cliente visíveis no âmbito de quem chama, as mais recentes primeiro. */
    public List<PolicySummaryView> customerPolicies(UUID userId) {
        return queries.customerPolicies(userId);
    }

    /**
     * Por esta ordem, a primeira que existir: (a) a de {@link #findActiveByPlate}; (b) a apólice
     * {@code ACTIVE} com {@code start_at > at} de {@code start_at} menor; (c) a de {@code end_at} maior,
     * em qualquer estado (S6-03).
     */
    public Optional<PolicyCoverView> findLatestByPlate(String plate, Instant at) {
        return lookups.findLatestByPlate(plate, at);
    }

    /** A apólice do certificado com esse código, em qualquer estado; o estado é o desse certificado (S6-03). */
    public Optional<PolicyCoverView> findByCertificateToken(String token) {
        return lookups.findByCertificateToken(token);
    }
}
