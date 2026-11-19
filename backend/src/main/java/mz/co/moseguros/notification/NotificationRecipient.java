package mz.co.moseguros.notification;

import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;

/**
 * Destinatário de uma notificação, com os contactos actuais (S6-04 §1; modelo transversal §1.1).
 *
 * @param msisdn   {@code null} sem telemóvel
 * @param email    normalizado; {@code null} sem e-mail
 * @param language {@code pt} ou {@code en}
 * @param active   a conta está {@code ACTIVE}
 */
public record NotificationRecipient(UUID userId, Msisdn msisdn, boolean msisdnVerified, String email,
                                    boolean emailVerified, String language, boolean active) {
}
