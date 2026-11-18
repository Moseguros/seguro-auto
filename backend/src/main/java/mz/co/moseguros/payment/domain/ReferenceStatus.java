package mz.co.moseguros.payment.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/** Estado de uma referência Ponto24 ({@code payment.reference_payment.status}, S5-02 §2). */
@Schema(name = "ReferenceStatus", enumAsRef = true)
public enum ReferenceStatus {
    ACTIVE,
    PAID,
    EXPIRED,
    CANCELLED
}
