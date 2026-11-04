package mz.co.moseguros.proposal.domain;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.proposal.ProposalStatus;

/**
 * Estado do prazo de decisão da seguradora (S4-05 §5), calculado na leitura: {@link #ON_TIME} antes
 * de {@code sla_alert_at}, {@link #ALERT} até {@code sla_due_at}, {@link #OVERDUE} depois. Só em
 * {@code UNDER_REVIEW}; nos outros estados é {@code null}. Não há aceitação tácita: o fim do prazo é
 * só um alerta.
 */
@Schema(enumAsRef = true)
public enum SlaStatus {
    ON_TIME,
    ALERT,
    OVERDUE;

    /** Estado em {@code now}; {@code null} fora de {@code UNDER_REVIEW} ou sem prazo. */
    public static SlaStatus of(ProposalStatus status, Instant alertAt, Instant dueAt, Instant now) {
        if (status != ProposalStatus.UNDER_REVIEW || alertAt == null || dueAt == null) {
            return null;
        }
        if (now.isBefore(alertAt)) {
            return ON_TIME;
        }
        return now.isBefore(dueAt) ? ALERT : OVERDUE;
    }
}
