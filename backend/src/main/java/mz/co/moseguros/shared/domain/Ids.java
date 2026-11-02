package mz.co.moseguros.shared.domain;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

/**
 * Identificadores públicos: UUID v7 (ordenados pelo tempo).
 */
public final class Ids {

    private Ids() {
    }

    public static UUID newId() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
