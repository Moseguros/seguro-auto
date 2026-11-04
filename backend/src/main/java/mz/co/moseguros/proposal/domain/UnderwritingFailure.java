package mz.co.moseguros.proposal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Condição de aceitação automática que falhou (S4-05 §3), pela ordem de avaliação. Textos em
 * {@code proposal.underwriting.failure.<CODE>} (web e app).
 */
@Schema(enumAsRef = true)
public enum UnderwritingFailure {
    /** {@code autoAccept.enabled = false}: as restantes não se avaliam. */
    AUTO_ACCEPT_DISABLED,
    /** KYC não {@code VERIFIED} e total maior ou igual a {@code simplifiedKycMaxPremium}. */
    KYC_REQUIRED,
    /** Há declarações "sim" e as regras do tipo de proposta exigem todas "não". */
    DECLARATION_POSITIVE,
    /** Danos próprios com a vistoria não {@code APPROVED}. */
    INSPECTION_NOT_APPROVED,
    /** Danos próprios sem valor comercial do veículo. */
    VEHICLE_VALUE_MISSING,
    /** Danos próprios com o valor comercial acima de {@code ownDamage.maxVehicleValue}. */
    VEHICLE_VALUE_ABOVE_LIMIT
}
