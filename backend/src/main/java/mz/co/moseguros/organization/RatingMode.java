package mz.co.moseguros.organization;

/**
 * Como a seguradora tarifa (S3-01 §2): com a tarifa interna de cada versão de produto
 * ({@code INTERNAL}) ou chamando o seu próprio serviço ({@code EXTERNAL}, S3-02).
 */
public enum RatingMode {
    INTERNAL,
    EXTERNAL
}
