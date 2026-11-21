package mz.co.moseguros.policy;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento público: uma apólice foi emitida (S6-01 §6), na transacção da emissão (outbox do Spring
 * Modulith). Ouvido pelo {@code policy} (PDFs e aviso {@code POLICY_ISSUED}, S6-02) e pelo
 * {@code verification} (S6-03); no S11 também pelo {@code fraud} e pelas comissões.
 *
 * @param plate matrícula formatada
 * @param endAt exclusivo
 */
public record PolicyIssued(UUID eventId, Instant occurredAt, UUID policyId, String policyNumber,
                           UUID insurerId, UUID policyholderUserId, UUID proposalId,
                           UUID certificateId, String plate, Instant startAt, Instant endAt) {
}
