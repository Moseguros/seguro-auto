package mz.co.moseguros.notification.domain;

/** Estado de uma linha de {@code notification.message} (S6-04 §2 e §4). */
public enum MessageStatus {
    QUEUED, SENDING, SENT, DELIVERED, FAILED, SKIPPED;

    /** Estado final (a linha já não volta a ser enviada). */
    public boolean terminal() {
        return this == SENT || this == DELIVERED || this == FAILED || this == SKIPPED;
    }
}
