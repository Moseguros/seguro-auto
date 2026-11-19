package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Mensagens recebidas do worker (RLS: a seguradora, ou todas em SYSTEM e PLATFORM). */
public interface InboundMessageRepository extends JpaRepository<InboundMessage, UUID> {

    /**
     * Regista a mensagem se ainda não existir ({@code on conflict (message_id) do nothing}). Uma segunda
     * transacção com o mesmo {@code message_id} espera pelo fim da primeira e não insere.
     *
     * @return 1 se inseriu, 0 se a mensagem já existia (repetição)
     */
    @Modifying(flushAutomatically = true)
    @Query(value = """
            insert into payment.inbound_message
              (id, created_at, created_by, updated_at, updated_by, version, message_id, message_type, insurer_id,
               source_id, file_sha256, received_at, outcome)
            values (:id, :now, :actor, :now, :actor, 0, :messageId, :messageType, :insurerId, :sourceId,
                    cast(:fileSha256 as varchar), :now, :outcome)
            on conflict (message_id) do nothing""", nativeQuery = true)
    int insertIfAbsent(@Param("id") UUID id, @Param("messageId") String messageId,
                       @Param("messageType") String messageType, @Param("insurerId") UUID insurerId,
                       @Param("sourceId") UUID sourceId, @Param("fileSha256") String fileSha256,
                       @Param("outcome") String outcome, @Param("now") Instant now, @Param("actor") String actor);

    Optional<InboundMessage> findByMessageId(String messageId);

    /** Pagamentos de cada ficheiro por resultado, para {@code confirmedCount} e {@code exceptionCount}. */
    @Query("""
            select new mz.co.moseguros.payment.domain.FileOutcomeCount(m.sourceId, m.fileSha256, m.outcome, count(m))
              from InboundMessage m
             where m.messageType = 'ReferencePaymentReceived' and m.fileSha256 in :shas
             group by m.sourceId, m.fileSha256, m.outcome""")
    List<FileOutcomeCount> countByFile(@Param("shas") Collection<String> shas);

    /** {@code received_at} mais recente (estado do worker); {@code null} sem mensagens. */
    @Query("select max(m.receivedAt) from InboundMessage m")
    Instant lastReceivedAt();
}
