package mz.co.moseguros.vehicle.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Veículos dos clientes. Corre no âmbito de quem chama: o RLS só mostra os do próprio cliente. */
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    Page<Vehicle> findByOwnerUserIdAndStatus(UUID ownerUserId, RecordStatus status, Pageable pageable);

    Optional<Vehicle> findByIdAndOwnerUserIdAndStatus(UUID id, UUID ownerUserId, RecordStatus status);

    long countByOwnerUserIdAndStatus(UUID ownerUserId, RecordStatus status);

    boolean existsByOwnerUserIdAndPlateAndStatus(UUID ownerUserId, String plate, RecordStatus status);

    /** Veículo do cliente com a matrícula normalizada e o estado indicados (no máximo um {@code ACTIVE}). */
    Optional<Vehicle> findFirstByOwnerUserIdAndPlateAndStatus(UUID ownerUserId, String plate, RecordStatus status);

    boolean existsByOwnerUserIdAndPlateAndStatusAndIdNot(UUID ownerUserId, String plate, RecordStatus status, UUID id);

    /** Qualquer estado (o livrete de um veículo retirado continua legível pelo dono). */
    boolean existsByIdAndOwnerUserId(UUID id, UUID ownerUserId);
}
