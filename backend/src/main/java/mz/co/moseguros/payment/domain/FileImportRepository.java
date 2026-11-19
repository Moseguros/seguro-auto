package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Ficheiros de movimentos (RLS: a seguradora, ou todos em SYSTEM e PLATFORM). */
public interface FileImportRepository extends JpaRepository<FileImport, UUID>, JpaSpecificationExecutor<FileImport> {

    /** Ficheiros e ficheiros rejeitados de cada fonte desde {@code since} (ecrã de operações). */
    @Query("""
            select new mz.co.moseguros.payment.domain.SourceFileCount(f.sourceId, count(f),
                   sum(case when f.status = :rejected then 1L else 0L end))
              from FileImport f
             where f.occurredAt >= :since
             group by f.sourceId""")
    List<SourceFileCount> countBySourceSince(@Param("since") Instant since,
                                             @Param("rejected") FileImport.Status rejected);
}
