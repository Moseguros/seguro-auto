package mz.co.moseguros.rating.application.provider;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.rating.domain.tariff.ProductDefinition;
import mz.co.moseguros.rating.RatedDriver;
import mz.co.moseguros.rating.RatedVehicle;
import mz.co.moseguros.rating.domain.tariff.RatingInput;
import mz.co.moseguros.rating.domain.tariff.RatingInput.RequestedCoverage;

/**
 * Tarifação de uma versão de produto por uma seguradora (S3-02 §4, fase 1): tudo o que o provider
 * precisa, já lido da BD, para a fase 2 correr sem transacção nem {@code TenantContext}.
 *
 * @param quoteId id da futura linha de {@code rating.quote} (vai como {@code requestId} à
 *                seguradora externa)
 */
public record RatingRequest(UUID quoteId, ProductVersionView version, InsurerProfileView insurer,
                            RatedVehicle vehicle, RatedDriver driver, List<RequestedCoverage> requestedCoverages,
                            LocalDate startDate, LocalDate endDate, int durationDays) {

    public RatingRequest {
        Objects.requireNonNull(quoteId, "quoteId");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(insurer, "insurer");
        Objects.requireNonNull(vehicle, "vehicle");
        requestedCoverages = requestedCoverages == null ? List.of() : List.copyOf(requestedCoverages);
    }

    /** Entrada do {@code CoverageSelector} e do {@code TariffEvaluator} (S3-01 §9). */
    public RatingInput input() {
        return new RatingInput(version.productType(), requestedCoverages, vehicle, driver, startDate, durationDays);
    }

    public ProductDefinition product() {
        return ProductDefinition.from(version);
    }
}
