package mz.co.moseguros.paymentworker.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/** Ficheiros lidos (S5-09 §6). */
public interface ProcessedFileRepository extends CrudRepository<ProcessedFile, UUID> {

    /** Deduplicação por conteúdo (passo c): a mesma fonte com o mesmo SHA-256, qualquer que seja o nome. */
    Optional<ProcessedFile> findBySourceIdAndSha256(UUID sourceId, String sha256);

    /**
     * Já tratado sem descarregar (passo a0): mesmo nome, tamanho e data de modificação no SFTP, em
     * {@code DONE} ou {@code ERROR}.
     */
    @Query("""
            select * from payment_worker.processed_file
             where source_id = :sourceId and file_name = :fileName and size_bytes = :sizeBytes
               and remote_mtime = :remoteMtime and status in ('DONE', 'ERROR')
             order by received_at desc
             limit 1""")
    Optional<ProcessedFile> findFinishedListing(@Param("sourceId") UUID sourceId, @Param("fileName") String fileName,
                                                @Param("sizeBytes") long sizeBytes,
                                                @Param("remoteMtime") Instant remoteMtime);

    /** Outro ficheiro desta fonte, fora de {@code ERROR}, com o mesmo identificador no formato (passo e2). */
    @Query("""
            select exists (select 1 from payment_worker.processed_file
                            where source_id = :sourceId and format_file_id = :formatFileId
                              and status <> 'ERROR' and id <> :id)""")
    boolean existsOtherWithFormatFileId(@Param("sourceId") UUID sourceId, @Param("formatFileId") String formatFileId,
                                        @Param("id") UUID id);

    /** Último ficheiro {@code DONE} desta fonte com identificador no formato (passo e3). */
    @Query("""
            select * from payment_worker.processed_file
             where source_id = :sourceId and status = 'DONE' and format_file_id is not null
             order by completed_at desc
             limit 1""")
    Optional<ProcessedFile> findLastDone(@Param("sourceId") UUID sourceId);

    /** Ficheiros desta fonte num estado (recuperação de {@code PUBLISHED}). */
    @Query("""
            select * from payment_worker.processed_file
             where source_id = :sourceId and status = :status
             order by received_at""")
    List<ProcessedFile> findBySourceIdAndStatus(@Param("sourceId") UUID sourceId, @Param("status") String status);
}
