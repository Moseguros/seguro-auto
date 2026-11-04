package mz.co.moseguros.proposal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/** Resultado da avaliação das regras de aceitação (S4-05 §3). */
@Schema(enumAsRef = true)
public enum UnderwritingOutcome {
    /** Sem falhas: aceitação automática. */
    AUTOMATIC,
    /** Alguma falha: análise do subscritor. */
    MANUAL_REVIEW
}
