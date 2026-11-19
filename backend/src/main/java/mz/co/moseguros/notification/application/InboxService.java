package mz.co.moseguros.notification.application;

import java.time.Clock;
import java.util.UUID;

import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.domain.NotificationMessage;
import mz.co.moseguros.notification.domain.NotificationMessageRepository;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Centro de notificações do cliente (S6-04 §8; {@code self:profile}, âmbito {@code CUSTOMER}): só as linhas
 * {@code INBOX} do próprio cliente (filtro pelo {@code sub} e RLS).
 */
@Service
public class InboxService {

    private final NotificationMessageRepository messages;
    private final CurrentUser currentUser;
    private final Clock clock;

    public InboxService(NotificationMessageRepository messages, CurrentUser currentUser, Clock clock) {
        this.messages = messages;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional(readOnly = true)
    public Page<NotificationMessage> list(boolean unreadOnly, Pageable pageable) {
        return messages.inbox(currentUser.id(), unreadOnly, pageable);
    }

    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional(readOnly = true)
    public long unreadCount() {
        return messages.countUnread(currentUser.id());
    }

    /** Idempotente; de outro cliente ou inexistente → 404 {@code NOTIFICATION_NOT_FOUND}. */
    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional
    public void markRead(UUID id) {
        NotificationMessage message = messages.findByIdAndUserIdAndChannel(id, currentUser.id(), MessageChannel.INBOX)
                .orElseThrow(() -> new DomainException(ErrorCodes.NOTIFICATION_NOT_FOUND, 404,
                        "Notification not found"));
        message.markRead(clock.instant());
    }

    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional
    public void markAllRead() {
        messages.markAllRead(currentUser.id(), clock.instant());
    }
}
