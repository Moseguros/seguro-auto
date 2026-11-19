package mz.co.moseguros.policy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.shared.domain.Money;

/**
 * Apólice para outros módulos ({@link PolicyApi#view}, S6-01 §9). Não é um DTO de API. Os campos são os
 * que as fichas S7–S12 usam; o titular é sempre {@code policyholderUserId} (algumas fichas chamam-lhe
 * {@code holderUserId}).
 *
 * @param coverStatus      {@code coverAt(agora)}
 * @param productType      {@code MTPL_ONLY}, {@code COMPREHENSIVE}, {@code TEMPORARY}, {@code BORDER} ou {@code FLEET}
 * @param channel          {@code APP}, {@code WEB} ou {@code BROKER}
 * @param brokerOrgId      {@code null} fora do canal {@code BROKER}
 * @param plate            matrícula formatada
 * @param vehicleSnapshot  o {@code VehicleSnapshot} da proposta
 * @param driverSnapshot   o {@code DriverSnapshot} da proposta (carta mascarada); pode ser {@code null}
 * @param endAt            exclusivo
 * @param issuedAt         não muda nas renovações (S7-03)
 */
public record PolicyView(UUID id, String number, PolicyStatus status, CoverStatus coverStatus,
                         UUID insurerId, String insurerName, UUID proposalId, UUID policyholderUserId,
                         String policyholderName, String channel, UUID brokerOrgId,
                         UUID productVersionId, String productName, String productType, CoverageType coverageType,
                         UUID vehicleId, String plate, JsonNode vehicleSnapshot, JsonNode driverSnapshot,
                         List<PolicyCoverageView> coverages, Instant startAt, Instant endAt, RenewalType renewalType,
                         PaymentPlan paymentPlan, Money totalAmount, Instant issuedAt) {

    /** Códigos das coberturas ({@code MTPL}, {@code OD_COLLISION}…). */
    public List<String> coverageCodes() {
        return coverages.stream().map(PolicyCoverageView::code).toList();
    }
}
