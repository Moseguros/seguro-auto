package mz.co.moseguros.shared.numbering;

import java.util.Map;

/**
 * Prefixos dos números de negócio e o número de dígitos de cada um (modelo transversal §4). Um
 * prefixo que não está aqui (ex.: {@code SIMO}, S5-07) usa {@value #DEFAULT_DIGITS} dígitos.
 */
public final class BusinessPrefix {

    /** Proposta: {@code PR-2027-000123}. */
    public static final String PROPOSAL = "PR";
    /** Pagamento: {@code PG-2027-0001234}. */
    public static final String PAYMENT = "PG";
    /** Apólice: {@code AP-2027-000123}. */
    public static final String POLICY = "AP";
    /** Certificado: {@code CT-2027-0001234}. */
    public static final String CERTIFICATE = "CT";
    /** Endosso: {@code EN-2027-000123}. */
    public static final String ENDORSEMENT = "EN";
    /** Sinistro: {@code SN-2027-000123}. */
    public static final String CLAIM = "SN";
    /** Reclamação: {@code RC-2027-000123}. */
    public static final String COMPLAINT = "RC";
    /** Assistência: {@code AS-2027-000123}. */
    public static final String ASSISTANCE = "AS";
    /** Extracto de comissões: {@code EX-2027-000123}. */
    public static final String COMMISSION_STATEMENT = "EX";

    /** Dígitos de um prefixo que não está na tabela. */
    public static final int DEFAULT_DIGITS = 6;

    private static final Map<String, Integer> DIGITS = Map.of(
            PROPOSAL, 6,
            PAYMENT, 7,
            POLICY, 6,
            CERTIFICATE, 7,
            ENDORSEMENT, 6,
            CLAIM, 6,
            COMPLAINT, 6,
            ASSISTANCE, 6,
            COMMISSION_STATEMENT, 6);

    private BusinessPrefix() {
    }

    /** Número mínimo de dígitos do valor (com zeros à esquerda). */
    public static int digits(String prefix) {
        return DIGITS.getOrDefault(prefix, DEFAULT_DIGITS);
    }
}
