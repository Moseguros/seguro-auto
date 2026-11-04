package mz.co.moseguros.proposal.domain;

import java.util.Arrays;
import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Motivo da recusa da proposta pelo subscritor ({@code rejection_reason_code}, S4-05 §4). Textos em
 * {@code proposal.rejectionReason.<CODE>}.
 */
@Schema(enumAsRef = true)
public enum RejectionReasonCode {
    RISK_NOT_ACCEPTED,
    KYC_FAILED,
    INSPECTION_FAILED,
    FALSE_DECLARATION,
    DOCUMENTS_INVALID,
    OTHER;

    /** Valor do pedido; vazio se for desconhecido. */
    public static Optional<RejectionReasonCode> parse(String value) {
        return value == null ? Optional.empty()
                : Arrays.stream(values()).filter(v -> v.name().equals(value.strip())).findFirst();
    }
}
