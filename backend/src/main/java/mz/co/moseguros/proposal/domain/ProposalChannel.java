package mz.co.moseguros.proposal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Por onde a proposta foi criada ({@code ck_proposal_channel}): a app ou a web do cliente, ou um
 * mediador em nome dele (S4-06). O S12-02 acrescenta {@code USSD}.
 */
@Schema(enumAsRef = true)
public enum ProposalChannel {
    APP,
    WEB,
    BROKER
}
