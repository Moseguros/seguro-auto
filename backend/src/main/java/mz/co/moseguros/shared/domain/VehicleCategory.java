package mz.co.moseguros.shared.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Categoria do veículo (S2-02 §1). Lista fechada: entra em {@code check} de várias tabelas e nas
 * regras de tarifação; mudá-la é uma tarefa própria. Os nomes visíveis vêm do i18n
 * ({@code vehicleCategory.<CODE>}), não da API.
 */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados (o dart-dio repetia o enum em cada modelo)
public enum VehicleCategory {
    LIGHT_PASSENGER,
    LIGHT_MIXED,
    HEAVY,
    MOTORCYCLE,
    TRAILER,
    BUS
}
