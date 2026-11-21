package mz.co.moseguros.notification.api;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.notification.application.InboxService;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Centro de notificações do cliente (S6-04 §8; {@code self:profile}, âmbito {@code CUSTOMER}). */
@RestController
@RequestMapping("/api/v1/me/notifications")
@Tag(name = NotificationTag.NAME, description = NotificationTag.DESCRIPTION)
public class MyNotificationController {

    private final InboxService inbox;

    public MyNotificationController(InboxService inbox) {
        this.inbox = inbox;
    }

    @GetMapping
    @Operation(operationId = "listMyNotifications", summary = "Lista as notificações do cliente",
            description = "Permissão self:profile. Só as do próprio cliente, por createdAt desc; unreadOnly=true "
                    + "mostra só as não lidas.")
    public PageResponse<MyNotificationResponse> listMyNotifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return PageResponse.of(inbox.list(unreadOnly, PageRequests.of(page, size, null, java.util.Set.of(),
                Sort.by(Sort.Direction.DESC, "createdAt"))), MyNotificationResponse::from);
    }

    @GetMapping("/unread-count")
    @Operation(operationId = "countMyUnreadNotifications", summary = "Conta as notificações por ler",
            description = "Permissão self:profile.")
    public UnreadCountResponse countMyUnreadNotifications() {
        return new UnreadCountResponse(inbox.unreadCount());
    }

    @PostMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "markMyNotificationRead", summary = "Marca uma notificação como lida",
            description = "Permissão self:profile. Idempotente; de outro cliente ou inexistente → 404 "
                    + "NOTIFICATION_NOT_FOUND.")
    public void markMyNotificationRead(@PathVariable UUID id) {
        inbox.markRead(id);
    }

    @PostMapping("/read-all")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(operationId = "markAllMyNotificationsRead", summary = "Marca todas as notificações como lidas",
            description = "Permissão self:profile.")
    public void markAllMyNotificationsRead() {
        inbox.markAllRead();
    }

    /** {@code { "count": 3 }}. */
    public record UnreadCountResponse(@Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "3") long count) {
    }
}
