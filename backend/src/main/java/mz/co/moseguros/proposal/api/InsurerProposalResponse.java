package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalChecklist;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalCoverage;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalDecision;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalDeclarations;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalOrganization;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalPolicyholder;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalPremium;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalProduct;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalSignature;
import mz.co.moseguros.proposal.api.ProposalResponse.ProposalSuitability;
import mz.co.moseguros.proposal.application.DecisionService.InsurerDetails;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.proposal.domain.DeclarationQuestion;
import mz.co.moseguros.proposal.domain.InspectionStatus;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalChannel;
import mz.co.moseguros.proposal.domain.Signature;
import mz.co.moseguros.proposal.domain.SlaStatus;
import mz.co.moseguros.proposal.domain.UnderwritingFailure;
import mz.co.moseguros.proposal.domain.UnderwritingOutcome;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Proposta para a seguradora ({@code getInsurerProposal} e as decisões, S4-05 §7): os campos do
 * {@link ProposalResponse} com o condutor (categorias da carta, data de nascimento) e o veículo
 * (chassis, lugares) completos, o KYC com a verificação, o registo das regras ({@code underwriting}),
 * o SLA e o histórico das assinaturas. O número da carta em claro não está na proposta e não sai.
 */
public record InsurerProposalResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123")
        String number,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalChannel channel,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID quoteId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalOrganization insurer,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        ProposalOrganization broker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalProduct product,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalPolicyholder policyholder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        InsurerProposalVehicle vehicle,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        InsurerProposalDriver driver,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ProposalCoverage> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalPremium premium,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant startAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant endAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate endDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        int durationDays,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        ProposalDeclarations declarations,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        ProposalSuitability suitability,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        InspectionResponse inspection,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        InsurerProposalKyc kyc,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        ProposalSignature signature,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalChecklist checklist,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        ProposalDecision decision,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        Instant expiresAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant submittedAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão para as decisões; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        long version,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "Última avaliação das regras de aceitação (underwriting_result)")
        UnderwritingResult underwriting,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        InsurerProposalSla sla,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Todas as assinaturas (válida e invalidadas), as mais recentes primeiro")
        List<InsurerProposalSignature> signatures) {

    /** Veículo como estava na criação da proposta, com o chassis e os lugares. */
    public record InsurerProposalVehicle(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC")
            String plate,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            String chassisNumber,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Toyota")
            String make,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Corolla")
            String model,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2018")
            int year,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
            int seats,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            VehicleCategory category,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            VehicleUsage usage,
            Money commercialValue) {
    }

    /** Condutor como estava na criação da proposta (carta mascarada; D30: categorias em lista). */
    public record InsurerProposalDriver(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa")
            String fullName,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            LocalDate birthDate,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "****4567")
            String licenceNumberMasked,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"B\"]")
            List<String> licenceCategories,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDate licenceIssueDate) {
    }

    /**
     * KYC do cliente: o bloco do {@code ProposalResponse} com a verificação. {@code checkId} e
     * {@code verifiedAt} só se a verificação for desta seguradora (S4-01 "Questões"); de outra
     * seguradora vê-se só {@code state} e {@code verifiedByThisInsurer = false}.
     */
    public record InsurerProposalKyc(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            KycState state,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean requiredForAutomaticAcceptance,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            UUID checkId,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true, description = "Só em VERIFIED e OUTDATED")
            Instant verifiedAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean verifiedByThisInsurer) {
    }

    /** Prazo de decisão da seguradora (S4-05 §5). */
    public record InsurerProposalSla(
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true, description = "Entrada em UNDER_REVIEW (reinicia em cada submissão)")
            Instant reviewStartedAt,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            Instant alertAt,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            Instant dueAt,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            Instant alertedAt,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true, description = "Só em UNDER_REVIEW")
            SlaStatus status) {
    }

    /** Assinatura da proposta, válida ou invalidada. */
    public record InsurerProposalSignature(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Instant signedAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Channel channel,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "+258*****4567")
            String destinationMasked,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID documentFileId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String documentHash,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean assisted,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            Instant invalidatedAt,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true, allowableValues = {"CONTENT_CHANGED", "INFO_REQUESTED"})
            String invalidatedReason) {

        static InsurerProposalSignature of(Signature s) {
            return new InsurerProposalSignature(s.getSignedAt(), s.getOtpChannel(), s.getDestinationMasked(),
                    s.getDocumentFileId(), s.getDocumentHash(), s.isAssisted(), s.getInvalidatedAt(),
                    s.getInvalidatedReason() == null ? null : s.getInvalidatedReason().name());
        }
    }

    /** Registo da avaliação das regras de aceitação (S4-05 §3). */
    public record UnderwritingResult(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
            int rulesSchemaVersion,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PRODUCT", "DEFAULT"})
            String rulesSource,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Instant evaluatedAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UnderwritingOutcome outcome,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<UnderwritingFailure> failures,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UnderwritingInputs inputs) {

        static UnderwritingResult of(JsonNode json) {
            if (json == null || json.isNull()) {
                return null;
            }
            List<UnderwritingFailure> failures = new ArrayList<>();
            json.path("failures").forEach(f -> failures.add(UnderwritingFailure.valueOf(f.asText())));
            JsonNode in = json.path("inputs");
            List<DeclarationQuestion> positive = new ArrayList<>();
            in.path("positiveDeclarations").forEach(q -> positive.add(DeclarationQuestion.valueOf(q.asText())));
            UnderwritingInputs inputs = new UnderwritingInputs(in.path("hasOwnDamage").asBoolean(),
                    in.path("totalAmount").asLong(), KycState.valueOf(in.path("kycState").asText()),
                    in.path("simplifiedKycMaxPremium").asLong(), positive,
                    in.hasNonNull("inspectionStatus") ? InspectionStatus.valueOf(in.get("inspectionStatus").asText())
                            : null,
                    in.hasNonNull("vehicleValue") ? in.get("vehicleValue").asLong() : null,
                    in.path("maxVehicleValue").asLong());
            return new UnderwritingResult(json.path("rulesSchemaVersion").asInt(), json.path("rulesSource").asText(),
                    Instant.parse(json.path("evaluatedAt").asText()),
                    UnderwritingOutcome.valueOf(json.path("outcome").asText()), failures, inputs);
        }
    }

    /** Factos usados na avaliação; montantes em centavos. */
    public record UnderwritingInputs(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean hasOwnDamage,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            long totalAmount,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            KycState kycState,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            long simplifiedKycMaxPremium,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<DeclarationQuestion> positiveDeclarations,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            InspectionStatus inspectionStatus,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            Long vehicleValue,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            long maxVehicleValue) {
    }

    static InsurerProposalResponse from(InsurerDetails d, ProposalJson json) {
        ProposalResponse base = ProposalResponse.from(d.details(), json);
        Proposal p = d.details().proposal();
        JsonNode v = p.getVehicleSnapshot();
        JsonNode dr = p.getDriverSnapshot();
        List<String> categories = new ArrayList<>();
        dr.path("licenceCategories").forEach(c -> categories.add(c.asText()));
        InsurerProposalVehicle vehicle = new InsurerProposalVehicle(base.vehicle().id(), base.vehicle().plate(),
                text(v, "chassisNumber"), base.vehicle().make(), base.vehicle().model(), base.vehicle().year(),
                v.path("seats").asInt(), base.vehicle().category(), base.vehicle().usage(),
                base.vehicle().commercialValue());
        InsurerProposalDriver driver = new InsurerProposalDriver(base.driver().id(), base.driver().fullName(),
                dr.hasNonNull("birthDate") ? LocalDate.parse(dr.get("birthDate").asText()) : null,
                base.driver().licenceNumberMasked(), categories, base.driver().licenceIssueDate());
        boolean ours = d.verifiedByThisInsurer();
        InsurerProposalKyc kyc = new InsurerProposalKyc(base.kyc().state(),
                base.kyc().requiredForAutomaticAcceptance(), ours ? d.kyc().checkId() : null,
                ours ? d.kyc().verifiedAt() : null, ours);
        InsurerProposalSla sla = new InsurerProposalSla(p.getReviewStartedAt(), p.getSlaAlertAt(), p.getSlaDueAt(),
                p.getSlaAlertedAt(), d.slaStatus());
        return new InsurerProposalResponse(base.id(), base.number(), base.status(), base.channel(), base.quoteId(),
                base.insurer(), base.broker(), base.product(), base.policyholder(), vehicle, driver,
                base.coverages(), base.premium(), base.startAt(), base.endAt(), base.endDate(), base.durationDays(),
                base.declarations(), base.suitability(), base.inspection(), kyc, base.signature(), base.checklist(),
                base.decision(), base.expiresAt(), base.submittedAt(), base.createdAt(), base.updatedAt(),
                base.version(), UnderwritingResult.of(p.getUnderwritingResult()), sla,
                d.signatures().stream().map(InsurerProposalSignature::of).toList());
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }
}
