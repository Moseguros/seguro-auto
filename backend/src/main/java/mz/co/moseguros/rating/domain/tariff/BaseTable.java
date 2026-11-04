package mz.co.moseguros.rating.domain.tariff;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/** Tabela base de uma cobertura: o tipo de cálculo e as linhas, pela ordem. */
public record BaseTable(BaseType type, List<TariffRow> rows) {

    public BaseTable {
        Objects.requireNonNull(type, "type");
        rows = List.copyOf(rows);
    }

    /** Índice da primeira linha que se cumpre; vazio se nenhuma. */
    public OptionalInt firstMatch(RatingVariables variables, Long capital) {
        return Tables.firstMatch(rows, variables, capital);
    }
}
