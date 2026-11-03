package mz.co.moseguros.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/** Parâmetros da plataforma (sem RLS). */
public interface PlatformSettingRepository extends JpaRepository<PlatformSetting, UUID> {

    Optional<PlatformSetting> findByKey(String key);

    List<PlatformSetting> findAllByOrderByKeyAsc();
}
