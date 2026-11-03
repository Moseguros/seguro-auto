package mz.co.moseguros.catalog.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** Versões de produtos visíveis no âmbito da transacção (Row Level Security por {@code insurer_id}). */
public interface ProductVersionRepository extends JpaRepository<ProductVersion, UUID> {

    List<ProductVersion> findByProductIdOrderByVersionNoDesc(UUID productId);

    List<ProductVersion> findByProductIdInAndStatusIn(Collection<UUID> productIds,
                                                      Collection<ProductVersionStatus> statuses);

    Optional<ProductVersion> findByProductIdAndStatus(UUID productId, ProductVersionStatus status);

    boolean existsByProductIdAndStatus(UUID productId, ProductVersionStatus status);

    Optional<ProductVersion> findFirstByProductIdAndStatusNotOrderByVersionNoDesc(UUID productId,
                                                                                ProductVersionStatus status);

    @Query("select coalesce(max(v.versionNo), 0) from ProductVersion v where v.productId = ?1")
    int maxVersionNo(UUID productId);

    /** Versões {@code PUBLISHED}, com os filtros opcionais ({@code null} = todos). */
    @Query(value = """
            select v.* from catalog.product_version v
              join catalog.product p on p.id = v.product_id
             where v.status = 'PUBLISHED' and p.status <> 'RETIRED'
               and (cast(:category as text) is null or cast(:category as text) = any (v.vehicle_categories))
               and (cast(:usage as text) is null or cast(:usage as text) = any (v.usages))
               and (cast(:type as text) is null or p.type = cast(:type as text))""", nativeQuery = true)
    List<ProductVersion> findPublished(String category, String usage, String type);
}
