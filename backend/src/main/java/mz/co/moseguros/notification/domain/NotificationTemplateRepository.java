package mz.co.moseguros.notification.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Modelos (só âmbitos SYSTEM e PLATFORM). */
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    Optional<NotificationTemplate> findByCodeAndChannelAndLocale(String code, MessageChannel channel, String locale);

    List<NotificationTemplate> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
            select t from NotificationTemplate t
             where (:code is null or t.code like concat(:code, '%'))
               and (:channel is null or t.channel = :channel)
               and (:locale is null or t.locale = :locale)
               and (:customized is null or t.customized = :customized)""")
    Page<NotificationTemplate> search(@Param("code") String codePrefix, @Param("channel") MessageChannel channel,
                                      @Param("locale") String locale, @Param("customized") Boolean customized,
                                      Pageable pageable);
}
