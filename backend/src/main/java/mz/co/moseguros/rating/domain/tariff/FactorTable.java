package mz.co.moseguros.rating.domain.tariff;

import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Tabela de um factor multiplicativo de uma cobertura.
 *
 * @param code  identificador do factor ({@code BONUS_MALUS} é reservado ao S7)
 * @param label {@code null} sem etiqueta
 */
public record FactorTable(String code, String label, List<TariffRow> rows) {

    /** Código reservado ao factor bónus/malus calculado pelo sistema (S7). */
    public static final String BONUS_MALUS = "BONUS_MALUS";

    public FactorTable {
        Objects.requireNonNull(code, "code");
        rows = List.copyOf(rows);
    }

    /** Índice da primeira linha que se cumpre; vazio se nenhuma. */
    public OptionalInt firstMatch(RatingVariables variables, Long capital) {
        return Tables.firstMatch(rows, variables, capital);
    }
}
