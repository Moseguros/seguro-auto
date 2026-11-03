package mz.co.moseguros.catalog;

/**
 * Coberturas que um produto pode ter (S2-04). {@code MTPL} é a responsabilidade civil
 * obrigatória, {@code VTPL} a facultativa; as {@code OD_*} são de danos próprios.
 */
public enum CoverageCode {
    MTPL,
    VTPL,
    OD_COLLISION,
    OD_GLASS,
    OD_FIRE,
    OD_NATURE,
    OD_THEFT,
    OCCUPANTS,
    ASSISTANCE,
    LEGAL,
    SRCC,
    CROSS_BORDER;

    /** {@code true} nas coberturas de danos próprios ({@code OD_*}). */
    public boolean isOwnDamage() {
        return name().startsWith("OD_");
    }
}
