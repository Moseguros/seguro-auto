package mz.co.moseguros.identity.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estado de uma verificação de identidade ({@code identity.kyc_check.status}). Só há duas
 * transições, ambas a partir de {@link #PENDING}: {@code verify} e {@code reject}.
 */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados
public enum KycCheckStatus {
    PENDING,
    VERIFIED,
    REJECTED
}
