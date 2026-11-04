package mz.co.moseguros.proposal.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Fotografias visíveis no âmbito da transacção (as das vistorias visíveis, RLS). */
public interface InspectionPhotoRepository extends JpaRepository<InspectionPhoto, UUID> {

    /** Activas ({@code removed_at is null}), pela ordem de gravação. */
    @Query("""
            select ph from InspectionPhoto ph
             where ph.inspectionId = :inspectionId and ph.removedAt is null
             order by ph.createdAt, ph.id""")
    List<InspectionPhoto> findActive(@Param("inspectionId") UUID inspectionId);

    Optional<InspectionPhoto> findByInspectionIdAndClientPhotoId(UUID inspectionId, UUID clientPhotoId);

    /** Número de fotografias activas de cada vistoria: {@code [inspectionId, count]}. */
    @Query("""
            select ph.inspectionId, count(ph) from InspectionPhoto ph
             where ph.inspectionId in :ids and ph.removedAt is null
             group by ph.inspectionId""")
    List<Object[]> countActive(@Param("ids") Collection<UUID> inspectionIds);
}
