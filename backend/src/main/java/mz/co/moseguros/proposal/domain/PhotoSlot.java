package mz.co.moseguros.proposal.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ângulo de uma fotografia da vistoria (S4-03 §3, regra 2). Os seis primeiros são obrigatórios em
 * {@code SELF_PHOTOS} (um por vistoria: uma fotografia nova substitui a anterior); {@link #EXTRA}
 * admite até {@code max-extra-photos}. A ordem é a das fotografias na resposta. Provisórios (questão
 * da ficha): mudá-los é uma ficha própria.
 */
@Schema(enumAsRef = true)
public enum PhotoSlot {
    FRONT,
    REAR,
    LEFT,
    RIGHT,
    ODOMETER,
    CHASSIS,
    EXTRA;

    /** Os seis ângulos obrigatórios, por esta ordem. */
    public static final List<PhotoSlot> REQUIRED = Arrays.stream(values()).filter(s -> s != EXTRA).toList();

    /** {@code true} nos ângulos com uma só fotografia activa. */
    public boolean isFixed() {
        return this != EXTRA;
    }

    /** O ângulo com este nome exacto; vazio se não existir. */
    public static Optional<PhotoSlot> parse(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(s -> s.name().equals(value.trim())).findFirst();
    }
}
