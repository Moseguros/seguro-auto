package mz.co.moseguros.rating;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.rating.domain.tariff.RatedDriver;
import mz.co.moseguros.rating.domain.tariff.RatedVehicle;

/**
 * Cotação para outros módulos ({@link RatingApi#quote}, S3-02 §10): a linha de
 * {@code rating.quote} com os dados do pedido. {@code status} é o estado efectivo.
 *
 * @param origin            {@code CUSTOMER} ou {@code BROKER}
 * @param clientMsisdn      {@code 258XXXXXXXXX} nas cotações do mediador; senão {@code null}
 * @param requestedByOrgId  organização do mediador; {@code null} nas do cliente
 * @param coverages         vazia em {@code NOT_AVAILABLE}
 * @param provider          {@code INTERNAL} ou {@code EXTERNAL}
 */
public record QuoteView(UUID id, UUID quoteRequestId, UUID insurerId, UUID productId, UUID productVersionId,
                        String productType, QuoteStatus status, String origin,
                        UUID customerUserId, String clientMsisdn, UUID requestedByOrgId,
                        RatedVehicle vehicle, RatedDriver driver, List<QuotedCoverage> coverages,
                        LocalDate startDate, LocalDate endDate, int durationDays,
                        Long commercialPremiumAmount, List<QuotedCharge> charges,
                        Long roundingAdjustmentAmount, Long totalAmount, Instant validUntil,
                        String provider, String externalReference) {
}
