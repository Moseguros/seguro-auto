package mz.co.moseguros.rating.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.application.QuoteDetails.QuoteRequestDetails;
import mz.co.moseguros.rating.domain.QuoteOrigin;
import mz.co.moseguros.rating.domain.QuoteRequest;
import mz.co.moseguros.rating.RatedDriver;
import mz.co.moseguros.rating.RatedVehicle;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;

/**
 * Pedido de cotações com uma cotação por versão de produto compatível (S3-02 §7), pela ordem:
 * primeiro as com preço, do mais barato para o mais caro; depois as sem preço.
 *
 * @param clientMsisdn telemóvel do cliente nas do mediador ({@code +258…}); {@code null} nas do cliente
 */
public record QuoteRequestResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuoteOrigin origin,
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @Schema(nullable = true, example = "+258841234567")
        String clientMsisdn,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Instant createdAt,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        ProductType productType,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuotedVehicle vehicle,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        QuotedDriver driver,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<RequestedCoverageResponse> requestedCoverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate startDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Inclusiva")
        LocalDate endDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        int durationDays,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<QuoteResponse> quotes) {

    /** Veículo como foi tarifado. {@code vehicleId} {@code null} nas cotações do mediador. */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record QuotedVehicle(
            @Schema(nullable = true)
            UUID vehicleId,
            @Schema(nullable = true, example = "ABC-123-MC")
            String plate,
            @Schema(nullable = true)
            String make,
            @Schema(nullable = true)
            String model,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            int year,
            @Schema(nullable = true)
            Integer engineCc,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            VehicleCategory category,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            VehicleUsage usage,
            @Schema(nullable = true)
            Integer seats,
            @Schema(nullable = true)
            Integer grossWeightKg,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Province provinceOfCirculation,
            @Schema(nullable = true)
            Money commercialValue,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            boolean hasTracker) {

        static QuotedVehicle from(RatedVehicle v) {
            return new QuotedVehicle(v.vehicleId(), v.plate(), v.make(), v.model(), v.year(), v.engineCc(),
                    v.category(), v.usage(), v.seats(), v.grossWeightKg(), v.provinceOfCirculation(),
                    v.commercialValue() == null ? null : Money.ofCentavos(v.commercialValue()), v.hasTracker());
        }
    }

    /** Condutor como foi tarifado (sem nome nem número da carta). {@code driverId} {@code null} nas do mediador. */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public record QuotedDriver(
            @Schema(nullable = true)
            UUID driverId,
            @Schema(nullable = true)
            LocalDate birthDate,
            @Schema(nullable = true)
            LocalDate licenceIssueDate,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            List<String> licenceCategories) {

        static QuotedDriver from(RatedDriver d) {
            return d == null ? new QuotedDriver(null, null, null, List.of())
                    : new QuotedDriver(d.driverId(), d.birthDate(), d.licenceIssueDate(),
                    d.licenceCategories() == null ? List.of() : d.licenceCategories());
        }
    }

    /** Cobertura pedida, tal como pedida. */
    public record RequestedCoverageResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            CoverageCode code,
            @JsonInclude(JsonInclude.Include.ALWAYS)
            @Schema(nullable = true)
            Money capital) {
    }

    static QuoteRequestResponse from(QuoteRequestDetails d) {
        QuoteRequest r = d.request();
        return new QuoteRequestResponse(r.getId(), r.getOrigin(),
                r.getClientMsisdn() == null ? null : r.getClientMsisdn().e164(), r.getCreatedAt(), r.getProductType(),
                QuotedVehicle.from(d.vehicle()), QuotedDriver.from(d.driver()),
                d.requestedCoverages().stream().map(c -> new RequestedCoverageResponse(c.code(),
                        c.capital() == null ? null : Money.ofCentavos(c.capital()))).toList(),
                r.getStartDate(), r.getEndDate(), r.getDurationDays(),
                d.quotes().stream().map(QuoteResponse::from).toList());
    }
}
