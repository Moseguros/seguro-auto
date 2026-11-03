package mz.co.moseguros.rating.api;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.application.QuoteService.CustomerQuoteCommand;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.TermsInput;

/**
 * Pedido de cotações do cliente para um veículo e um condutor seus (S3-02 §5). Os erros vêm todos
 * juntos em {@code errors[]} ({@code REQUIRED}, {@code NOT_ALLOWED}, {@code DURATION_NOT_ALLOWED},
 * {@code START_DATE_OUT_OF_RANGE}, {@code DUPLICATE_COVERAGE}, {@code COVERAGE_NOT_ALLOWED_FOR_TYPE},
 * {@code OWN_DAMAGE_REQUIRED}, {@code COVERAGE_REQUIRES}, {@code OUT_OF_RANGE}).
 */
public record CreateQuoteRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Veículo activo do cliente")
        UUID vehicleId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Condutor do cliente")
        UUID driverId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)")
        ProductType productType,
        @ArraySchema(arraySchema = @Schema(description = "Coberturas pedidas além das obrigatórias (pode ser vazia)"))
        List<RequestedCoverageRequest> coverages,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Entre hoje e hoje + 60 dias (Africa/Maputo)", example = "2027-01-15")
        LocalDate startDate,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "TEMPORARY: 7, 14, 30, 60 ou 365; os outros tipos: 365", example = "365")
        Integer durationDays) {

    CustomerQuoteCommand toCommand() {
        return new CustomerQuoteCommand(vehicleId, driverId,
                new TermsInput(productType, durationDays, startDate, RequestedCoverageRequest.toInputs(coverages)));
    }
}
