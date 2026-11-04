package mz.co.moseguros.proposal.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.application.ProposalDetails;
import mz.co.moseguros.proposal.application.ProductDocuments;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.proposal.domain.CoverageLine;
import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.DeclarationQuestion;
import mz.co.moseguros.proposal.domain.PolicyholderSnapshot;
import mz.co.moseguros.proposal.domain.PremiumBreakdown;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalChannel;
import mz.co.moseguros.proposal.domain.Signature;
import mz.co.moseguros.proposal.domain.SuitabilityAnswer;
import mz.co.moseguros.proposal.domain.SuitabilityQuestion;
import mz.co.moseguros.proposal.domain.SuitabilityReason;
import mz.co.moseguros.proposal.domain.SuitabilityResult;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Proposta (S4-02 §10, S4-04 §8). {@code signature} é a assinatura válida ({@code null} sem ela); os
 * blocos {@code decision} (S4-05) e {@code broker} (S4-06) vêm a {@code null} nesta fase;
 * {@code inspection} só tem {@code required} (o S4-03 completa). O documento, o NUIT, o telemóvel e o e-mail do tomador e a carta do condutor
 * saem só mascarados; o chassis não sai.
 */
public record ProposalResponse(
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
        @Schema(nullable = true, description = "Mediador (S4-06); null nas propostas do cliente")
        ProposalOrganization broker,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalProduct product,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalPolicyholder policyholder,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalVehicle vehicle,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalDriver driver,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<ProposalCoverage> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalPremium premium,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "00:00 do primeiro dia em Africa/Maputo")
        Instant startAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Exclusivo: 00:00 (Africa/Maputo) do dia seguinte ao último dia coberto")
        Instant endAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Último dia coberto (Africa/Maputo)")
        LocalDate endDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "365")
        int durationDays,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "null até serem gravadas")
        ProposalDeclarations declarations,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "null até ser respondido")
        ProposalSuitability suitability,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalInspection inspection,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalKyc kyc,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "Assinatura válida (S4-04); null sem ela ou depois de uma alteração")
        ProposalSignature signature,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProposalChecklist checklist,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(ref = ProposalSchemas.DECISION_REF, nullable = true)
        JsonNode decision,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "Fim do prazo sem acção do cliente; null em UNDER_REVIEW e nos finais")
        Instant expiresAt,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "Primeira submissão (S4-05)")
        Instant submittedAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant updatedAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Versão para as escritas; diferente da gravada → 409 CONCURRENT_MODIFICATION")
        long version) {

    /** Seguradora ou mediador, com o nome congelado na proposta. */
    public record ProposalOrganization(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Seguradora Demo")
            String displayName) {
    }

    /** Produto e versão cotados (nome congelado) e os documentos a ler antes de assinar (S4-04 §8). */
    public record ProposalProduct(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID versionId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Base")
            String name,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            ProductType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "Condições Gerais e Informação Pré-contratual da versão, na língua do tomador "
                            + "(ou pt); descarregam-se em GET /api/v1/files/{fileId}")
            List<ProposalProductDocument> documents) {
    }

    /** Documento da versão do produto. */
    public record ProposalProductDocument(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            ProductDocumentType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID fileId) {
    }

    /**
     * Assinatura válida da proposta (S4-04 §8): o PDF assinado descarrega-se em
     * {@code GET /api/v1/files/{documentFileId}} e o seu SHA-256 tem de ser {@code documentHash}.
     */
    public record ProposalSignature(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Instant signedAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Channel channel,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "+258*****4567")
            String destinationMasked,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID documentFileId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "SHA-256 do PDF, hexadecimal minúsculo",
                    example = "9f2c4b7e0d1a3c5e7f9b2d4a6c8e0f1b3d5a7c9e1f3b5d7a9c1e3f5b7d9a1c3e")
            String documentHash,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Assinatura com a ajuda de um mediador (S4-06)")
            boolean assisted) {

        static ProposalSignature of(Signature s) {
            return s == null ? null : new ProposalSignature(s.getSignedAt(), s.getOtpChannel(),
                    s.getDestinationMasked(), s.getDocumentFileId(), s.getDocumentHash(), s.isAssisted());
        }
    }

    /** Tomador copiado do perfil, com os dados sensíveis mascarados. */
    public record ProposalPolicyholder(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa")
            String fullName,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDate birthDate,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "BI")
            String idDocType,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "*********345A")
            String idDocNumberMasked,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "*****6789")
            String nuitMasked,
            @Schema(example = "Av. Julius Nyerere, 1234")
            String address,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MAPUTO_CITY")
            String province,
            @Schema(example = "+258*****4567")
            String msisdnMasked,
            @Schema(example = "a***@exemplo.co.mz")
            String emailMasked) {
    }

    /** Veículo como estava na criação da proposta (sem o chassis). */
    public record ProposalVehicle(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "ABC-123-MC")
            String plate,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Toyota")
            String make,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Corolla")
            String model,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2018")
            int year,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            VehicleCategory category,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            VehicleUsage usage,
            Money commercialValue) {
    }

    /** Condutor como estava na criação da proposta (carta mascarada). */
    public record ProposalDriver(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa")
            String fullName,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "****4567")
            String licenceNumberMasked,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDate licenceIssueDate) {
    }

    /** Cobertura como foi cotada; o nome vem do i18n ({@code coverage.<CODE>}). */
    public record ProposalCoverage(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            CoverageCode code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean mandatory,
            @Schema(description = "null se a cobertura não tiver capital")
            Money capital,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            ProposalDeductible deductible,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Money premium) {
    }

    /**
     * Franquia: {@code { "type": "NONE" }}, {@code { "type": "FIXED", "amount" }} ou
     * {@code { "type": "PERCENT", "percent": "10", "minAmount" }}.
     */
    public record ProposalDeductible(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            DeductibleType type,
            Money amount,
            @Schema(example = "10")
            String percent,
            Money minAmount) {
    }

    /** Prémio cotado. */
    public record ProposalPremium(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Money commercial,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<ProposalCharge> charges,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Money roundingAdjustment,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Money total) {
    }

    /** Encargo; os nomes são dados da seguradora, em pt e en. */
    public record ProposalCharge(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "DEMO_STAMP")
            String code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String labelPt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String labelEn,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Money amount) {
    }

    /** Declarações de risco gravadas. */
    public record ProposalDeclarations(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Instant confirmedAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<DeclarationAnswerResponse> answers) {
    }

    /** Resposta a uma declaração de risco. */
    public record DeclarationAnswerResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            DeclarationQuestion questionCode,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean answer,
            @Schema(description = "Só em CLAIMS_LAST_3_YEARS com resposta sim")
            Integer claimsCount,
            @Schema(description = "Só com resposta sim")
            String details) {
    }

    /** Resultado e respostas do questionário de adequação. */
    public record ProposalSuitability(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            SuitabilityResult result,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Vazia em SUITABLE")
            List<SuitabilityReason> reasons,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Instant answeredAt,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true, description = "Confirmação consciente dos avisos (só em WARNED)")
            Instant acknowledgedAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<SuitabilityAnswerResponse> answers) {
    }

    /** Resposta a uma pergunta de adequação. */
    public record SuitabilityAnswerResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            SuitabilityQuestion questionCode,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "OWN_DAMAGE")
            String answer) {
    }

    /** Vistoria: nesta fase só se é obrigatória (alguma cobertura OD_*); o S4-03 acrescenta o resto. */
    public record ProposalInspection(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean required) {
    }

    /** Estado KYC do cliente; {@code requiredForAutomaticAcceptance} é calculado no S4-05. */
    public record ProposalKyc(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            KycState state,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true, description = "S4-05; null nesta fase")
            Boolean requiredForAutomaticAcceptance) {
    }

    /** O que falta para submeter. */
    public record ProposalChecklist(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"DONE", "MISSING"})
            String declarations,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"DONE", "MISSING", "ACK_REQUIRED"})
            String suitability,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"NOT_REQUIRED", "MISSING", "DONE", "REJECTED"})
            String inspection,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"MISSING", "DONE"})
            String signature) {
    }

    static ProposalResponse from(ProposalDetails details, ProposalJson json) {
        Proposal p = details.proposal();
        PolicyholderSnapshot holder = json.policyholder(p.getPolicyholder());
        JsonNode v = p.getVehicleSnapshot();
        JsonNode d = p.getDriverSnapshot();
        PremiumBreakdown premium = json.premium(p.getPremiumBreakdown());
        List<ProposalCoverage> coverages = json.coverages(p.getCoverages()).stream()
                .map(ProposalResponse::coverage).toList();
        return new ProposalResponse(p.getId(), p.getNumber(), p.getStatus(), p.getChannel(), p.getQuoteId(),
                new ProposalOrganization(p.getInsurerId(), p.getInsurerName()),
                p.getBrokerOrgId() == null ? null : new ProposalOrganization(p.getBrokerOrgId(), p.getBrokerName()),
                new ProposalProduct(p.getProductId(), p.getProductVersionId(), p.getProductName(), p.getProductType(),
                        documents(details.productDocuments())),
                new ProposalPolicyholder(holder.fullName(), holder.birthDate(), holder.idDocType(),
                        holder.idDocNumberMasked(), holder.nuitMasked(), holder.address(), holder.province(),
                        holder.msisdnMasked(), holder.emailMasked()),
                new ProposalVehicle(uuid(v, "vehicleId"), text(v, "plate"), text(v, "make"), text(v, "model"),
                        v.path("year").asInt(), VehicleCategory.valueOf(text(v, "category")),
                        VehicleUsage.valueOf(text(v, "usage")), money(v.get("commercialValue"))),
                new ProposalDriver(uuid(d, "driverId"), text(d, "fullName"), text(d, "licenceNumberMasked"),
                        d.hasNonNull("licenceIssueDate") ? LocalDate.parse(d.get("licenceIssueDate").asText()) : null),
                coverages,
                new ProposalPremium(premium.commercial(), premium.charges().stream()
                        .map(c -> new ProposalCharge(c.code(), c.labelPt(), c.labelEn(), c.amount())).toList(),
                        premium.roundingAdjustment(), premium.total()),
                p.getStartAt(), p.getEndAt(), p.endDate(), p.getDurationDays(),
                declarations(p, details.declarations()), suitability(p, details.suitabilityAnswers()),
                new ProposalInspection(p.isInspectionRequired()), new ProposalKyc(details.kycState(), null),
                ProposalSignature.of(details.signature()), checklist(p, details.signature() != null), null, p.getExpiresAt(), p.getSubmittedAt(), p.getCreatedAt(), p.getUpdatedAt(),
                p.getVersion());
    }

    private static List<ProposalProductDocument> documents(List<ProductDocuments.Selected> selected) {
        return selected == null ? List.of()
                : selected.stream().map(d -> new ProposalProductDocument(d.type(), d.fileId())).toList();
    }

    static ProposalChecklist checklist(Proposal p, boolean signed) {
        String declarations = p.getDeclarationsConfirmedAt() == null ? "MISSING" : "DONE";
        String suitability;
        if (p.getSuitabilityResult() == null) {
            suitability = "MISSING";
        } else if (p.getSuitabilityResult() == SuitabilityResult.WARNED && p.getSuitabilityAcknowledgedAt() == null) {
            suitability = "ACK_REQUIRED";
        } else {
            suitability = "DONE";
        }
        String inspection = p.isInspectionRequired() ? "MISSING" : "NOT_REQUIRED";
        return new ProposalChecklist(declarations, suitability, inspection, signed ? "DONE" : "MISSING");
    }

    private static ProposalDeclarations declarations(Proposal p, List<Declaration> answers) {
        if (p.getDeclarationsConfirmedAt() == null) {
            return null;
        }
        return new ProposalDeclarations(p.getDeclarationsConfirmedAt(), answers.stream()
                .map(a -> new DeclarationAnswerResponse(a.getQuestionCode(), a.getAnswer(), a.getClaimsCount(),
                        a.getDetails())).toList());
    }

    private static ProposalSuitability suitability(Proposal p, List<SuitabilityAnswer> answers) {
        if (p.getSuitabilityResult() == null) {
            return null;
        }
        return new ProposalSuitability(p.getSuitabilityResult(), p.getSuitabilityReasons(),
                p.getSuitabilityAnsweredAt(), p.getSuitabilityAcknowledgedAt(), answers.stream()
                .map(a -> new SuitabilityAnswerResponse(a.getQuestionCode(), a.getAnswer())).toList());
    }

    private static ProposalCoverage coverage(CoverageLine l) {
        CoverageLine.Deductible d = l.deductible();
        return new ProposalCoverage(CoverageCode.valueOf(l.code()), l.mandatory(), l.capital(),
                new ProposalDeductible(DeductibleType.valueOf(d.type()), d.amount(), d.percent(), d.minAmount()),
                l.premium());
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private static UUID uuid(JsonNode node, String field) {
        String value = text(node, field);
        return value == null ? null : UUID.fromString(value);
    }

    private static Money money(JsonNode node) {
        return node == null || node.isNull() ? null : Money.ofCentavos(node.get("amount").asLong());
    }
}
