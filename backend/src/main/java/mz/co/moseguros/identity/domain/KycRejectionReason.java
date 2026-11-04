package mz.co.moseguros.identity.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Motivo da recusa de uma verificação de identidade (S4-01 §7). Com {@link #OTHER} o subscritor
 * tem de escrever o motivo (10 a 500 caracteres).
 */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados
public enum KycRejectionReason {
    DOCUMENT_UNREADABLE,
    DOCUMENT_EXPIRED,
    DATA_MISMATCH,
    SELFIE_MISMATCH,
    OTHER
}
