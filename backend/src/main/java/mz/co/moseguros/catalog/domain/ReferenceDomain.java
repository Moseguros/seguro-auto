package mz.co.moseguros.catalog.domain;

import java.util.Optional;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;

/**
 * Domínio de valores de referência ({@code catalog.reference_value.domain}). Os nomes são os de
 * {@code ReferenceValueLookup} ({@code shared.reference}).
 */
public enum ReferenceDomain {

    VEHICLE_MAKE,
    /** Modelo de uma marca: o {@code parentCode} é o código da marca ({@link #VEHICLE_MAKE}). */
    VEHICLE_MODEL,
    LICENCE_CATEGORY;

    /** {@code true} se os valores deste domínio têm um pai (só {@link #VEHICLE_MODEL}). */
    public boolean hasParent() {
        return this == VEHICLE_MODEL;
    }

    /** Domínio com este nome exacto, se existir. */
    public static Optional<ReferenceDomain> find(String name) {
        if (name == null) {
            return Optional.empty();
        }
        for (ReferenceDomain domain : values()) {
            if (domain.name().equals(name)) {
                return Optional.of(domain);
            }
        }
        return Optional.empty();
    }

    /** @throws DomainException {@code REFERENCE_DOMAIN_UNKNOWN} (404) */
    public static ReferenceDomain parse(String name) {
        return find(name).orElseThrow(() -> DomainException.of(ErrorCodes.REFERENCE_DOMAIN_UNKNOWN,
                "Unknown reference domain"));
    }
}
