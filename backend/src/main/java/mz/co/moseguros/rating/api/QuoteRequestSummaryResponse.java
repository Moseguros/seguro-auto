package mz.co.moseguros.rating.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.application.QuoteQueryService.QuoteRequestSummary;
import mz.co.moseguros.rating.domain.QuoteOrigin;
import mz.co.moseguros.rating.domain.QuoteRequest;
import mz.co.moseguros.shared.domain.Money;

/**
 * Pedido de cotações numa lista (S3-02 §11).
 *
 * @param offeredCount cotações com estado efectivo {@code OFFERED}
 * @param bestTotal    o menor total dessas; {@code null} se não houver
 * @param validUntil   a maior validade dessas; {@code null} se não houver
 * @param expired      havia cotações com preço e já nenhuma está {@code OFFERED}
 */
public record QuoteRequestSummaryResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuoteOrigin origin,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType productType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuoteSummaryVehicle vehicle,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, example = "+258841234567")
        String clientMsisdn,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate startDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        int durationDays,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long offeredCount,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        Money bestTotal,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true)
        Instant validUntil,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        boolean expired) {

    /** Identificação do veículo (o que houver). */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record QuoteSummaryVehicle(
            @Schema(nullable = true, example = "ABC-123-MC")
            String plate,
            @Schema(nullable = true, example = "Toyota")
            String make,
            @Schema(nullable = true, example = "Corolla")
            String model) {
    }

    static QuoteRequestSummaryResponse from(QuoteRequestSummary s) {
        QuoteRequest r = s.request();
        return new QuoteRequestSummaryResponse(r.getId(), r.getCreatedAt(), r.getOrigin(), r.getProductType(),
                new QuoteSummaryVehicle(s.vehicle().plate(), s.vehicle().make(), s.vehicle().model()),
                r.getClientMsisdn() == null ? null : r.getClientMsisdn().e164(), r.getStartDate(), r.getDurationDays(),
                s.offeredCount(), s.bestTotal() == null ? null : Money.ofCentavos(s.bestTotal()), s.validUntil(),
                s.expired());
    }
}
