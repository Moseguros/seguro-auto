package mz.co.moseguros.organization;

/** Forma de cálculo de um encargo da seguradora (S3-01 §3). */
public enum ChargeType {
    /** Percentagem do prémio comercial. */
    PERCENT,
    /** Valor fixo em centavos. */
    FIXED
}
