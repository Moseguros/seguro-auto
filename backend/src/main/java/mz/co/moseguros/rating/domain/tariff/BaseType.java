package mz.co.moseguros.rating.domain.tariff;

/** Como se calcula o valor base {@code B} de uma cobertura (S3-01 §9, passo 3.2). */
public enum BaseType {
    /** {@code B = value} (centavos). */
    AMOUNT,
    /** {@code B = vehicleValue × value}; só para coberturas de capital {@code VEHICLE_VALUE}. */
    RATE_OF_VEHICLE_VALUE,
    /** {@code B = capital × value}; só para coberturas de capital {@code FIXED}, {@code OPTIONS}, {@code PER_PERSON}. */
    RATE_OF_CAPITAL
}
