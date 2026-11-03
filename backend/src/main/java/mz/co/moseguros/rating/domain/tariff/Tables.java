package mz.co.moseguros.rating.domain.tariff;

import java.util.List;
import java.util.OptionalInt;

/** Procura da linha aplicável, comum à base e aos factores: a primeira que se cumpre ganha. */
final class Tables {

    private Tables() {
    }

    static OptionalInt firstMatch(List<TariffRow> rows, RatingVariables variables, Long capital) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).when().matches(variables, capital)) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }
}
