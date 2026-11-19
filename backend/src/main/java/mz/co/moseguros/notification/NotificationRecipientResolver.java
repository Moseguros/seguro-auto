package mz.co.moseguros.notification;

import java.util.Optional;
import java.util.UUID;

/**
 * Contactos de um utilizador para as notificações. Implementada pelo {@code identity}
 * ({@code IdentityRecipientResolver}; {@code identity → notification}, o inverso não existe: modelo
 * transversal §1.1, decisão D6). Tem de existir exactamente um bean.
 */
public interface NotificationRecipientResolver {

    /** Vazio se a conta não existir. Pode ser chamada em qualquer âmbito (a implementação lê em SYSTEM). */
    Optional<NotificationRecipient> resolve(UUID userId);
}
