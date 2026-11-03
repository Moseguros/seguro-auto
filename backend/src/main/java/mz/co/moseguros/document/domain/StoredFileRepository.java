package mz.co.moseguros.document.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Ficheiros guardados. As consultas correm no âmbito de quem chama (RLS), excepto nas classes
 * {@code DocumentSystemLookups} e {@code OrphanFileCleanupJob} ({@code SYSTEM}).
 */
public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {

    /**
     * Órfãos (nunca ligados, ainda com conteúdo) criados antes de {@code cutoff}, por ordem de
     * criação, a seguir à posição ({@code afterCreatedAt}, {@code afterId}): assim um ficheiro que
     * falhou não volta a aparecer no lote seguinte da mesma execução.
     */
    @Query("""
            select f from StoredFile f
             where f.linkedAt is null and f.purgedAt is null and f.createdAt < :cutoff
               and (f.createdAt > :afterCreatedAt or (f.createdAt = :afterCreatedAt and f.id > :afterId))
             order by f.createdAt, f.id""")
    List<StoredFile> findOrphansAfter(@Param("cutoff") Instant cutoff,
                                      @Param("afterCreatedAt") Instant afterCreatedAt,
                                      @Param("afterId") UUID afterId,
                                      Pageable page);
}
