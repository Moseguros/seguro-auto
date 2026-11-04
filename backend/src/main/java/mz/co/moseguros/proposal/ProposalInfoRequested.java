package mz.co.moseguros.proposal;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Evento público: o subscritor pediu informação ao cliente (S4-05 §7). Publicado na mesma
 * transacção (outbox).
 *
 * @param items valores de {@code InfoRequestItem}, pela ordem pedida
 */
public record ProposalInfoRequested(UUID eventId, Instant occurredAt, UUID proposalId, UUID insurerId,
                                    UUID customerUserId, UUID brokerOrgId, List<String> items) {

    public ProposalInfoRequested {
        items = List.copyOf(items);
    }
}
