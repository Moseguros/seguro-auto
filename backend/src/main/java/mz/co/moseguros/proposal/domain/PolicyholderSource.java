package mz.co.moseguros.proposal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Origem do tomador da proposta ({@code proposal.policyholder_source}, S4-06 §1).
 */
@Schema(enumAsRef = true)
public enum PolicyholderSource {
    /** Copiado do perfil do cliente (S4-02); volta a ser copiado quando a proposta muda. */
    PROFILE,
    /** Preenchido pelo mediador porque o perfil do cliente estava incompleto; fica só na proposta. */
    BROKER
}
