package mz.co.moseguros.shared.domain;

/**
 * Categoria do veículo (S2-02 §1). Lista fechada: entra em {@code check} de várias tabelas e nas
 * regras de tarifação; mudá-la é uma tarefa própria. Os nomes visíveis vêm do i18n
 * ({@code vehicleCategory.<CODE>}), não da API.
 */
public enum VehicleCategory {
    LIGHT_PASSENGER,
    LIGHT_MIXED,
    HEAVY,
    MOTORCYCLE,
    TRAILER,
    BUS
}
