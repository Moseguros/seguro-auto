package mz.co.moseguros.rating.domain.tariff;

/**
 * Intervalo de valores em centavos {@code [gte, lt)}: {@code gte} inclusivo, {@code lt} exclusivo;
 * um limite {@code null} não limita.
 */
public record AmountRange(Long gte, Long lt) implements Condition.Range {

    /** Valor ausente ({@code null}) nunca está no intervalo. */
    public boolean contains(Long value) {
        if (value == null) {
            return false;
        }
        return (gte == null || value >= gte) && (lt == null || value < lt);
    }

    /** Os dois limites presentes com {@code gte ≥ lt}. */
    @Override
    public boolean isInverted() {
        return gte != null && lt != null && gte >= lt;
    }
}
