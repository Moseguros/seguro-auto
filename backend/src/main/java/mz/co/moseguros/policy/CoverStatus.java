package mz.co.moseguros.policy;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Se a apólice cobre num instante ({@link PolicyApi#coverAt}, S6-01 §4): {@link #COVERED} só com a
 * apólice {@code ACTIVE} e o instante dentro do período.
 */
@Schema(enumAsRef = true)
public enum CoverStatus {
    COVERED,
    NOT_STARTED,
    ENDED,
    SUSPENDED,
    CANCELLED,
    LAPSED
}
