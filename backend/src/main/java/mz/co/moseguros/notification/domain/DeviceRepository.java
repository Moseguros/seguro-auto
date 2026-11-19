package mz.co.moseguros.notification.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Dispositivos (o cliente só os seus, pelo RLS). */
public interface DeviceRepository extends JpaRepository<Device, UUID> {

    List<Device> findByUserIdAndActiveTrue(UUID userId);

    boolean existsByUserIdAndActiveTrue(UUID userId);

    List<Device> findByToken(String token);

    Optional<Device> findByIdAndUserId(UUID id, UUID userId);
}
