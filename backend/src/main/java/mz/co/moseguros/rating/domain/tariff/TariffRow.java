package mz.co.moseguros.rating.domain.tariff;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Linha de uma tabela da tarifa.
 *
 * @param label {@code null} sem etiqueta
 * @param value base {@code AMOUNT}: centavos; base {@code RATE_OF_*}: taxa; factor: multiplicador
 */
public record TariffRow(String label, Condition when, BigDecimal value) {

    public TariffRow {
        Objects.requireNonNull(when, "when");
        Objects.requireNonNull(value, "value");
    }
}
