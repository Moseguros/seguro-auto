package mz.co.moseguros.paymentworker.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/** Registos de detalhe dos ficheiros (S5-09 §6 e §7). */
public interface FileRecordRepository extends CrudRepository<FileRecord, UUID> {

    @Query("select * from payment_worker.file_record where file_id = :fileId order by line_no")
    List<FileRecord> findByFileId(@Param("fileId") UUID fileId);

    /** Os que faltam publicar, por ordem de linha (passo g). */
    @Query("""
            select * from payment_worker.file_record
             where file_id = :fileId and published_at is null
             order by line_no""")
    List<FileRecord> findUnpublished(@Param("fileId") UUID fileId);

    /** Confirmados pelo RabbitMQ (em lotes). */
    @Modifying
    @Query("""
            update payment_worker.file_record
               set published_at = :at, updated_at = :at, updated_by = 'payment-file-worker', version = version + 1
             where id in (:ids) and published_at is null""")
    int markPublished(@Param("ids") Collection<UUID> ids, @Param("at") Instant at);
}
