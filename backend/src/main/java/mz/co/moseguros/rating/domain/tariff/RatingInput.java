package mz.co.moseguros.rating.domain.tariff;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.rating.RatedDriver;
import mz.co.moseguros.rating.RatedVehicle;

/**
 * Pedido de tarifação de uma versão de produto (S3-01 §9).
 *
 * @param requestedCoverages coberturas pedidas além das obrigatórias (o {@link CoverageSelector}
 *                           acrescenta as obrigatórias)
 * @param startDate          data de início {@code D} (data local, sem hora); as idades contam-se nela
 */
public record RatingInput(ProductType productType, List<RequestedCoverage> requestedCoverages, RatedVehicle vehicle,
                          RatedDriver driver, LocalDate startDate, int durationDays) {

    /**
     * Cobertura pedida.
     *
     * @param capital capital pedido em centavos (só conta em {@code OPTIONS}); {@code null} = o menor
     */
    public record RequestedCoverage(CoverageCode code, Long capital) {

        public RequestedCoverage {
            Objects.requireNonNull(code, "code");
        }
    }

    public RatingInput {
        Objects.requireNonNull(vehicle, "vehicle");
        Objects.requireNonNull(startDate, "startDate");
        requestedCoverages = requestedCoverages == null ? List.of() : List.copyOf(requestedCoverages);
    }
}
