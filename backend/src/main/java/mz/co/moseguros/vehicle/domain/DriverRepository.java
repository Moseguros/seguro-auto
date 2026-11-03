package mz.co.moseguros.vehicle.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Condutores dos clientes. Corre no âmbito de quem chama: o RLS só mostra os do próprio cliente. */
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    Page<Driver> findByOwnerUserIdAndStatus(UUID ownerUserId, RecordStatus status, Pageable pageable);

    Optional<Driver> findByIdAndOwnerUserIdAndStatus(UUID id, UUID ownerUserId, RecordStatus status);

    List<Driver> findByIdInAndStatus(Collection<UUID> ids, RecordStatus status);

    long countByOwnerUserIdAndStatus(UUID ownerUserId, RecordStatus status);

    boolean existsByOwnerUserIdAndLicenceNumberHashAndStatus(UUID ownerUserId, String licenceNumberHash,
                                                            RecordStatus status);

    boolean existsByOwnerUserIdAndLicenceNumberHashAndStatusAndIdNot(UUID ownerUserId, String licenceNumberHash,
                                                                    RecordStatus status, UUID id);

    boolean existsByOwnerUserIdAndOwnerTrueAndStatus(UUID ownerUserId, RecordStatus status);

    boolean existsByOwnerUserIdAndOwnerTrueAndStatusAndIdNot(UUID ownerUserId, RecordStatus status, UUID id);
}
