package mz.co.moseguros.notification.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Modelos (só âmbitos SYSTEM e PLATFORM). */
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID>,
        JpaSpecificationExecutor<NotificationTemplate> {

    Optional<NotificationTemplate> findByCodeAndChannelAndLocale(String code, MessageChannel channel, String locale);

    List<NotificationTemplate> findByCode(String code);

    boolean existsByCode(String code);

}
