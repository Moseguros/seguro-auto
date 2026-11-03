package mz.co.moseguros.rating.domain.tariff;

/**
 * Intervalo de inteiros {@code [gte, lt)}: {@code gte} inclusivo, {@code lt} exclusivo; um limite
 * {@code null} não limita.
 */
public record IntRange(Integer gte, Integer lt) implements Condition.Range {

    /** Valor ausente ({@code null}) nunca está no intervalo. */
    public boolean contains(Integer value) {
        if (value == null) {
            return false;
        }
        return (gte == null || value >= gte) && (lt == null || value < lt);
    }

    /** Os dois limites presentes com {@code gte ≥ lt} (intervalo vazio: erro {@code INVALID_RANGE}). */
    @Override
    public boolean isInverted() {
        return gte != null && lt != null && gte >= lt;
    }
}
