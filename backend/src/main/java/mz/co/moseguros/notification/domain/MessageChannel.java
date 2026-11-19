package mz.co.moseguros.notification.domain;

import mz.co.moseguros.shared.domain.Channel;

/**
 * Canal de uma linha de {@code notification.message} (S6-04 §2): os quatro canais externos e o centro de
 * notificações ({@code INBOX}). Os modelos e as preferências usam só os externos.
 */
public enum MessageChannel {
    SMS, WHATSAPP, EMAIL, PUSH, INBOX;

    /** Canal dos adaptadores do S1-03 ({@code SMS}, {@code WHATSAPP}, {@code EMAIL}); {@code null} nos outros. */
    public Channel toDeliveryChannel() {
        return switch (this) {
            case SMS -> Channel.SMS;
            case WHATSAPP -> Channel.WHATSAPP;
            case EMAIL -> Channel.EMAIL;
            default -> null;
        };
    }

    /** {@code SMS}, {@code WHATSAPP}, {@code EMAIL} ou {@code PUSH}. */
    public boolean external() {
        return this != INBOX;
    }
}
