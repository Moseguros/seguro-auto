package mz.co.moseguros.verification.domain;

import java.time.Instant;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.CoverStatus;
import mz.co.moseguros.policy.PolicyCoverView;
import mz.co.moseguros.policy.PolicyStatus;

/**
 * Resultado de uma verificação (S6-03 §3), calculado em cada pedido com o instante actual: o estado é
 * sempre o da apólice <b>agora</b>, mesmo que o certificado mostrado (papel ou ecrã) diga outra coisa.
 */
@Schema(enumAsRef = true)
public enum VerificationResult {
    VALID,
    NOT_YET_VALID,
    EXPIRED,
    SUSPENDED,
    CANCELLED,
    NOT_FOUND;

    /** Tabela da secção 3. */
    public static VerificationResult of(CoverStatus cover) {
        Objects.requireNonNull(cover, "cover");
        return switch (cover) {
            case COVERED -> VALID;
            case NOT_STARTED -> NOT_YET_VALID;
            case ENDED, LAPSED -> EXPIRED;
            case SUSPENDED -> SUSPENDED;
            case CANCELLED -> CANCELLED;
        };
    }

    /** Resultado da apólice encontrada em {@code at}; sem apólice → {@link #NOT_FOUND}. */
    public static VerificationResult of(PolicyCoverView cover, Instant at) {
        return cover == null ? NOT_FOUND : of(coverAt(cover.status(), cover.startAt(), cover.endAt(), at));
    }

    /**
     * {@code CoverStatus} da apólice em {@code at}, pela mesma tabela do S6-01 §4 ({@code Policy.coverAt},
     * interior do módulo {@code policy}): {@code COVERED} só com {@code ACTIVE} e
     * {@code start_at <= at < end_at}.
     */
    public static CoverStatus coverAt(PolicyStatus status, Instant startAt, Instant endAt, Instant at) {
        Objects.requireNonNull(at, "at");
        return switch (status) {
            case CANCELLED -> CoverStatus.CANCELLED;
            case SUSPENDED -> CoverStatus.SUSPENDED;
            case LAPSED -> CoverStatus.LAPSED;
            case EXPIRED -> CoverStatus.ENDED;
            case ACTIVE -> at.isBefore(startAt) ? CoverStatus.NOT_STARTED
                    : !at.isBefore(endAt) ? CoverStatus.ENDED : CoverStatus.COVERED;
        };
    }
}
