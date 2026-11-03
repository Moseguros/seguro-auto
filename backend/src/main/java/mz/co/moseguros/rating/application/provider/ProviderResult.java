package mz.co.moseguros.rating.application.provider;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.rating.QuoteResult;

/**
 * Resultado de um {@link RatingProvider}.
 *
 * @param breakdown decomposição a gravar em {@code rating.quote.premium_breakdown}: o
 *                  {@code PremiumBreakdown} da tarifa interna (S3-01 §10) ou a do externo (S3-02
 *                  §8); {@code null} em {@code NOT_AVAILABLE}
 * @param ratingMs  duração da avaliação desta seguradora
 */
public record ProviderResult(QuoteResult result, JsonNode breakdown, int ratingMs) {

    public ProviderResult {
        Objects.requireNonNull(result, "result");
        if (result.isOffered() && breakdown == null) {
            throw new IllegalArgumentException("An offered quote needs the breakdown");
        }
    }
}
