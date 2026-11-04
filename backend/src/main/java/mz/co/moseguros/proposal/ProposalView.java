package mz.co.moseguros.proposal;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.shared.domain.Money;

/**
 * Proposta para outros módulos ({@link ProposalApi#view}, S4-02 §12). Não é um DTO de API.
 *
 * @param productType             {@code MTPL_ONLY}, {@code COMPREHENSIVE}, {@code TEMPORARY}, {@code BORDER} ou {@code FLEET}
 * @param channel                 {@code APP}, {@code WEB} ou {@code BROKER}
 * @param brokerOrgId             só com {@code channel = BROKER} (S4-06)
 * @param vehicleSnapshot         o {@code VehicleSnapshot} gravado na proposta
 * @param driverSnapshot          o {@code DriverSnapshot} gravado, só com a carta mascarada
 * @param endAt                   exclusivo: 00:00 (Maputo) do dia seguinte ao último dia coberto
 * @param commercialPremiumAmount prémio comercial da cotação; base das comissões (S11-02)
 * @param signedDocumentFileId    PDF da assinatura válida ({@code document_file_id}, S4-04 §8); {@code null}
 *                                sem assinatura válida. O S6-02 junta-o aos documentos da apólice
 */
public record ProposalView(UUID id, String number, ProposalStatus status, UUID insurerId, UUID productVersionId,
                           String productType, UUID customerUserId, String policyholderName, String channel,
                           UUID brokerOrgId, UUID vehicleId, UUID driverId, JsonNode vehicleSnapshot,
                           JsonNode driverSnapshot, List<CoverageLineView> coverages, Instant startAt, Instant endAt,
                           int durationDays, Money totalAmount, Money commercialPremiumAmount,
                           UUID signedDocumentFileId) {
}
