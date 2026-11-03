package mz.co.moseguros.shared.domain;

/**
 * Uso do veículo (S2-02 §1). Lista fechada, como {@link VehicleCategory}; os nomes visíveis vêm do
 * i18n ({@code vehicleUsage.<CODE>}).
 */
public enum VehicleUsage {
    PRIVATE,
    RENTAL,
    PASSENGER_TRANSPORT,
    GOODS,
    TAXI,
    DRIVING_SCHOOL
}
