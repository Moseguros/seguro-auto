package mz.co.moseguros.shared.reference;

/**
 * Consulta de valores de referência ({@code catalog.reference_value}) por outros módulos, sem
 * dependerem do {@code catalog} (S2-02 §7): por exemplo, o {@code vehicle} valida a categoria de
 * carta de um condutor. Implementada pelo {@code catalog}; funciona em qualquer âmbito, dentro ou
 * fora de uma transacção.
 */
public interface ReferenceValueLookup {

    String VEHICLE_MAKE = "VEHICLE_MAKE";
    String VEHICLE_MODEL = "VEHICLE_MODEL";
    String LICENCE_CATEGORY = "LICENCE_CATEGORY";

    /**
     * {@code true} se o valor existe e está activo no domínio indicado ({@code VEHICLE_MAKE},
     * {@code VEHICLE_MODEL} ou {@code LICENCE_CATEGORY}); domínio ou código desconhecido →
     * {@code false}.
     */
    boolean isActive(String domain, String code);
}
