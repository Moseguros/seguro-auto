package mz.co.moseguros.policy.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.CertificateStatus;
import mz.co.moseguros.policy.CoverStatus;
import mz.co.moseguros.policy.CoverageType;
import mz.co.moseguros.policy.PaymentPlan;
import mz.co.moseguros.policy.PolicyCoverageView;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.RenewalType;
import mz.co.moseguros.policy.application.PolicyQueryService.Details;
import mz.co.moseguros.policy.domain.Certificate;
import mz.co.moseguros.policy.domain.Instalment;
import mz.co.moseguros.policy.domain.InstalmentStatus;
import mz.co.moseguros.policy.domain.Policy;
import mz.co.moseguros.policy.domain.PolicyEvent;
import mz.co.moseguros.policy.domain.PolicyEventType;
import mz.co.moseguros.shared.domain.Money;

/**
 * Detalhe de uma apólice ({@code getMyPolicy} e {@code getInsurerPolicy}, S6-01 §10). {@code events} e
 * {@code proposal} só vêm no detalhe da seguradora. O S6-02 acrescenta {@code documents}.
 *
 * @param coverStatus {@code coverAt(agora)}
 * @param certificate o certificado válido; ausente se não houver
 */
public record PolicyDetailResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AP-2027-000123") String number,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) CoverStatus coverStatus,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyRefs.InsurerRef insurer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) ProductRef product,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) CoverageType coverageType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ana Maria Mussa") String policyholderName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyRefs.VehicleRef vehicle,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant startAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant endAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) RenewalType renewalType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentPlan paymentPlan,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money totalPremium,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<CoverageResponse> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<InstalmentResponse> instalments,
        CertificateResponse certificate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant issuedAt,
        @Schema(description = "Só no detalhe da seguradora") List<EventResponse> events,
        @Schema(description = "Só no detalhe da seguradora") ProposalRef proposal) {

    /** O produto e a versão da emissão. */
    @Schema(name = "PolicyProductRef")
    public record ProductRef(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID versionId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "RC Automóvel") String name,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MTPL_ONLY",
                    allowableValues = {"MTPL_ONLY", "COMPREHENSIVE", "TEMPORARY", "BORDER", "FLEET"}) String type) {
    }

    /** Cobertura da apólice (cópia da proposta). */
    @Schema(name = "PolicyCoverage")
    public record CoverageResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MTPL") String code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean mandatory,
            Money capital,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"NONE", "FIXED", "PERCENT"})
            String deductibleType,
            Money deductibleAmount,
            @Schema(example = "10") BigDecimal deductiblePercent,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money premium) {

        static CoverageResponse from(PolicyCoverageView c) {
            return new CoverageResponse(c.code(), c.mandatory(), c.capital(), c.deductibleType(),
                    c.deductibleAmount(), c.deductiblePercent(), c.premium());
        }
    }

    /** Prestação. {@code dueDate} é uma data de Maputo. */
    @Schema(name = "PolicyInstalment")
    public record InstalmentResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1") int seq,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) LocalDate dueDate,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money amount,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) InstalmentStatus status,
            Instant paidAt) {

        static InstalmentResponse from(Instalment i) {
            return new InstalmentResponse(i.getSeq(), i.getDueDate(), Money.ofCentavos(i.getAmount()), i.getStatus(),
                    i.getPaidAt());
        }
    }

    /**
     * Certificado. O código de verificação só vai dentro de {@code verificationUrl} (o QR offline da app,
     * S6-07).
     *
     * @param fileId PDF do certificado (S6-02); ausente até estar pronto
     */
    @Schema(name = "PolicyCertificate")
    public record CertificateResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "CT-2027-0001234") String number,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) CertificateStatus status,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant validFrom,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant validTo,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    example = "https://moseguros.co.mz/v/Qm9hIHRhcmRlIHNyLg") String verificationUrl,
            UUID fileId) {

        static CertificateResponse from(Certificate c, String verificationUrl) {
            return new CertificateResponse(c.getId(), c.getNumber(), c.getStatus(), c.getValidFrom(), c.getValidTo(),
                    verificationUrl, c.getPdfFileId());
        }
    }

    /** Entrada da linha temporal (só para a seguradora). */
    @Schema(name = "PolicyTimelineEvent")
    public record EventResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PolicyEventType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant occurredAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"CUSTOMER", "STAFF", "SYSTEM"})
            String actorType) {

        static EventResponse from(PolicyEvent e) {
            return new EventResponse(e.getType(), e.getOccurredAt(), e.getActorType().name());
        }
    }

    /** A proposta de origem (só para a seguradora). */
    @Schema(name = "PolicyProposalRef")
    public record ProposalRef(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(example = "PR-2027-000123") String number) {
    }

    static PolicyDetailResponse from(Details d) {
        Policy p = d.policy();
        return new PolicyDetailResponse(p.getId(), p.getNumber(), p.getStatus(), p.coverAt(d.now()),
                new PolicyRefs.InsurerRef(p.getInsurerId(), d.insurerName()),
                new ProductRef(p.getProductVersionId(), p.getProductName(), p.getProductType()), p.getCoverageType(),
                p.getPolicyholderName(), PolicyRefs.VehicleRef.of(p, true), p.getStartAt(), p.getEndAt(),
                p.getRenewalType(), p.getPaymentPlan(), Money.ofCentavos(p.getTotalAmount()),
                d.coverages().stream().map(CoverageResponse::from).toList(),
                d.instalments().stream().map(InstalmentResponse::from).toList(),
                d.certificate() == null ? null : CertificateResponse.from(d.certificate(), d.verificationUrl()),
                p.getIssuedAt(),
                d.events() == null ? null : d.events().stream().map(EventResponse::from).toList(),
                d.proposal() == null ? null : new ProposalRef(d.proposal().id(), d.proposal().number()));
    }
}
