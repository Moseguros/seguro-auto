package mz.co.moseguros.rating.domain.tariff;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import mz.co.moseguros.rating.UnavailableReason;

/** Resultado de uma avaliação: um preço ({@link Offered}) ou o motivo de não haver ({@link Unavailable}). */
public sealed interface EvaluationResult permits EvaluationResult.Offered, EvaluationResult.Unavailable {

    /** Cotação com preço. */
    record Offered(PremiumBreakdown breakdown) implements EvaluationResult {

        public Offered {
            Objects.requireNonNull(breakdown, "breakdown");
        }
    }

    /**
     * Sem preço, com o motivo e os pormenores (ex.: {@code {"coverageCode": "MTPL", "table": "BASE"}}).
     * Os valores do {@code detail} são texto, números ou listas de texto.
     */
    record Unavailable(UnavailableReason reason, Map<String, Object> detail) implements EvaluationResult {

        public Unavailable {
            Objects.requireNonNull(reason, "reason");
            detail = detail == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(detail));
        }

        public static Unavailable of(UnavailableReason reason) {
            return new Unavailable(reason, Map.of());
        }
    }
}
