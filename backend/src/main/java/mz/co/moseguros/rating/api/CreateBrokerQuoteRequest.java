package mz.co.moseguros.rating.api;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.application.QuoteService.BrokerQuoteCommand;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.TermsInput;

/**
 * Pedido de cotações do mediador em nome do cliente (S3-02 §6). Os erros vêm todos juntos em
 * {@code errors[]}, com os campos do veículo e do condutor prefixados ({@code vehicle.year},
 * {@code driver.licenceCategories[0]}).
 */
public record CreateBrokerQuoteRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Telemóvel do cliente (INVALID_MSISDN)",
                example = "+258841234567")
        String clientMsisdn,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        BrokerVehicleRequest vehicle,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        RiskDriverRequest driver,
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

    BrokerQuoteCommand toCommand() {
        return new BrokerQuoteCommand(clientMsisdn, vehicle == null ? null : vehicle.toInput(),
                driver == null ? null : driver.toInput(),
                new TermsInput(productType, durationDays, startDate, RequestedCoverageRequest.toInputs(coverages)));
    }
}
