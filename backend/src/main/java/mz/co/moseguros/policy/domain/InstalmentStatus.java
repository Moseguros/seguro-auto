package mz.co.moseguros.policy.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/** Estado de uma prestação (S6-01 §3). {@code OVERDUE} e {@code CANCELLED} chegam no S7. */
@Schema(enumAsRef = true)
public enum InstalmentStatus {
    DUE,
    PAID,
    OVERDUE,
    CANCELLED
}
