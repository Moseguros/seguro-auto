package mz.co.moseguros.rating.api;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.application.QuoteDetails;
import mz.co.moseguros.rating.domain.Quote;
import mz.co.moseguros.rating.domain.StoredBreakdown;
import mz.co.moseguros.shared.domain.Money;

/**
 * Cotação de uma versão de produto (S3-02 §7). {@code status} é o estado efectivo. Em
 * {@code NOT_AVAILABLE} não há {@code coverages}, {@code commercialPremium}, {@code charges},
 * {@code roundingAdjustment}, {@code totalAmount} nem {@code validUntil}; há
 * {@code unavailableReason} e {@code unavailableDetail}. A decomposição com bases e factores não
 * sai para clientes nem mediadores (só no simulador).
 */
public record QuoteResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuoteStatus status,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuoteInsurer insurer,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuoteProduct product,
        List<QuoteCoverageResponse> coverages,
        Money commercialPremium,
        List<QuoteChargeResponse> charges,
        @Schema(description = "Ajuste do arredondamento do total ao metical (0 a 99 centavos)")
        Money roundingAdjustment,
        Money totalAmount,
        @Schema(description = "Até quando se pode usar numa proposta")
        Instant validUntil,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, description = "Só em NOT_AVAILABLE")
        UnavailableReason unavailableReason,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(ref = QuoteSchemas.UNAVAILABLE_DETAIL_REF, nullable = true)
        JsonNode unavailableDetail) {

    /** Seguradora da cotação. */
    public record QuoteInsurer(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Seguradora Demo")
            String displayName,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "DEMO")
            String shortCode,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            UUID logoFileId) {
    }

    /** Produto e versão cotados, com os documentos legais em pt. */
    public record QuoteProduct(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "DEMO-AUTO")
            String code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Auto Demo")
            String name,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            ProductType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            UUID versionId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            int versionNo,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            UUID generalConditionsFileId,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            UUID preContractInfoFileId) {
    }

    /** Cobertura cotada. */
    public record QuoteCoverageResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            CoverageCode code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean mandatory,
            @Schema(description = "Capital resolvido (por pessoa em PER_PERSON)")
            Money capital,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            QuoteDeductible deductible,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Money premium) {
    }

    /**
     * Franquia da cobertura: {@code { "type": "NONE" }}, {@code { "type": "FIXED", "amount" }} ou
     * {@code { "type": "PERCENT", "percent": "10", "minAmount" }}.
     *
     * @param percent percentagem em texto ({@code "10"} = 10 %)
     */
    public record QuoteDeductible(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            DeductibleType type,
            Money amount,
            @Schema(example = "10")
            String percent,
            Money minAmount) {
    }

    /** Encargo calculado. Os nomes são dados da seguradora, em pt e en. */
    public record QuoteChargeResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "DEMO_STAMP")
            String code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String labelPt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            String labelEn,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Money amount) {
    }

    static QuoteResponse from(QuoteDetails d) {
        Quote q = d.quote();
        InsurerProfileView insurer = d.insurer();
        ProductVersionView v = d.version();
        QuoteInsurer insurerDto = new QuoteInsurer(q.getInsurerId(), insurer == null ? null : insurer.displayName(),
                insurer == null ? null : insurer.shortCode(), insurer == null ? null : insurer.logoFileId());
        QuoteProduct product = new QuoteProduct(q.getProductId(), v == null ? null : v.productCode(),
                v == null ? null : v.productName(), v == null ? null : v.productType(), q.getProductVersionId(),
                v == null ? 0 : v.versionNo(), v == null ? null : v.generalConditionsFileId(),
                v == null ? null : v.preContractInfoFileId());
        if (d.status() == QuoteStatus.NOT_AVAILABLE || d.breakdown() == null) {
            JsonNode detail = q.getUnavailableDetail();
            return new QuoteResponse(q.getId(), d.status(), insurerDto, product, null, null, null, null, null, null,
                    q.getUnavailableReason(), detail == null ? JsonNodeFactory.instance.objectNode() : detail);
        }
        StoredBreakdown b = d.breakdown();
        Set<String> mandatory = v == null ? Set.of() : v.coverages().stream().filter(CoverageView::mandatory)
                .map(c -> c.code().name()).collect(Collectors.toSet());
        List<QuoteCoverageResponse> coverages = b.coverages().stream().map(l -> new QuoteCoverageResponse(
                CoverageCode.valueOf(l.code()), mandatory.contains(l.code()), money(l.capitalAmount()),
                deductible(l), Money.ofCentavos(l.premiumAmount()))).toList();
        List<QuoteChargeResponse> charges = b.charges().stream().map(c -> new QuoteChargeResponse(c.code(),
                c.labelPt(), c.labelEn(), Money.ofCentavos(c.amount()))).toList();
        return new QuoteResponse(q.getId(), d.status(), insurerDto, product, coverages,
                Money.ofCentavos(b.commercialPremiumAmount()), charges, Money.ofCentavos(b.roundingAdjustmentAmount()),
                Money.ofCentavos(b.totalAmount()), q.getValidUntil(), null, null);
    }

    private static QuoteDeductible deductible(StoredBreakdown.Line l) {
        DeductibleType type = DeductibleType.valueOf(l.deductibleType());
        return switch (type) {
            case NONE -> new QuoteDeductible(type, null, null, null);
            case FIXED -> new QuoteDeductible(type, money(l.deductibleAmount()), null, null);
            case PERCENT -> new QuoteDeductible(type, null,
                    l.deductiblePercent() == null ? null : StoredBreakdown.plain(l.deductiblePercent()),
                    money(l.deductibleMinAmount()));
        };
    }

    private static Money money(Long centavos) {
        return centavos == null ? null : Money.ofCentavos(centavos);
    }
}
