package mz.co.moseguros.notification.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Mensagens das notificações (SYSTEM/PLATFORM; o cliente só as suas, pelo RLS). */
public interface NotificationMessageRepository extends JpaRepository<NotificationMessage, UUID> {

    /** A linha, se estiver {@code QUEUED} e na hora, bloqueada para esta transacção (senão vazio). */
    @Query(value = """
            select * from notification.message
             where id = :id and status = 'QUEUED' and next_attempt_at <= :now
             for update skip locked""", nativeQuery = true)
    Optional<NotificationMessage> lockDue(@Param("id") UUID id, @Param("now") Instant now);

    /** Ids das linhas {@code QUEUED} na hora, por {@code next_attempt_at}. */
    @Query(value = """
            select id from notification.message
             where status = 'QUEUED' and next_attempt_at <= :now
             order by next_attempt_at
             limit :limit""", nativeQuery = true)
    List<UUID> dueIds(@Param("now") Instant now, @Param("limit") int limit);

    /** Linhas em {@code SENDING} desde antes de {@code before} (a aplicação caiu a meio). */
    @Query("select m from NotificationMessage m where m.status = 'SENDING' and m.updatedAt < :before")
    List<NotificationMessage> stuckSending(@Param("before") Instant before);

    List<NotificationMessage> findByNotificationId(UUID notificationId);

    Optional<NotificationMessage> findFirstByProviderAndProviderMessageId(String provider, String providerMessageId);

    /** Centro de notificações do utilizador. */
    @Query("""
            select m from NotificationMessage m
             where m.userId = :userId and m.channel = 'INBOX' and (:unreadOnly = false or m.readAt is null)""")
    Page<NotificationMessage> inbox(@Param("userId") UUID userId, @Param("unreadOnly") boolean unreadOnly,
                                    Pageable pageable);

    @Query("""
            select count(m) from NotificationMessage m
             where m.userId = :userId and m.channel = 'INBOX' and m.readAt is null""")
    long countUnread(@Param("userId") UUID userId);

    Optional<NotificationMessage> findByIdAndUserIdAndChannel(UUID id, UUID userId, MessageChannel channel);

    @Modifying
    @Query("""
            update NotificationMessage m set m.readAt = :at
             where m.userId = :userId and m.channel = 'INBOX' and m.readAt is null""")
    int markAllRead(@Param("userId") UUID userId, @Param("at") Instant at);

    List<NotificationMessage> findByIdIn(Collection<UUID> ids);
}
