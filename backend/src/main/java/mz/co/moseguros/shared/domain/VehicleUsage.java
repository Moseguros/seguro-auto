package mz.co.moseguros.shared.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Uso do veículo (S2-02 §1). Lista fechada, como {@link VehicleCategory}; os nomes visíveis vêm do
 * i18n ({@code vehicleUsage.<CODE>}).
 */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados (o dart-dio repetia o enum em cada modelo)
public enum VehicleUsage {
    PRIVATE,
    RENTAL,
    PASSENGER_TRANSPORT,
    GOODS,
    TAXI,
    DRIVING_SCHOOL
}
