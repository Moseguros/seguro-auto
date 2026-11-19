package mz.co.moseguros.notification;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Categoria de uma notificação ({@code NotificationApi.notify}, S6-04 §1). As obrigatórias chegam sempre
 * por pelo menos um canal externo (WhatsApp, SMS ou e-mail), mesmo com as preferências desligadas; as
 * outras seguem as preferências do cliente. Os sprints seguintes acrescentam categorias ({@code CLAIM},
 * {@code COMPLAINT}…): a coluna {@code category} não tem {@code check}.
 */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados
public enum NotificationCategory {
    PAYMENT(true),
    POLICY(true),
    LEGAL(true),
    PROPOSAL(false),
    QUOTE(false);

    private final boolean mandatory;

    NotificationCategory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean mandatory() {
        return mandatory;
    }
}
