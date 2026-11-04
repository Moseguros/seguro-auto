package mz.co.moseguros.identity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estado KYC de um cliente (S4-01 §5), calculado a partir da verificação mais recente:
 * {@link #NONE} sem nenhuma; {@link #PENDING} e {@link #REJECTED} como a verificação;
 * {@link #VERIFIED} se a verificação está verificada e o perfil não mudou desde o envio;
 * {@link #OUTDATED} se está verificada mas o nome, a data de nascimento ou o documento mudaram.
 */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados
public enum KycState {
    NONE,
    PENDING,
    VERIFIED,
    REJECTED,
    OUTDATED
}
