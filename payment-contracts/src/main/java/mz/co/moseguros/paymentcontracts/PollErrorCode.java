package mz.co.moseguros.paymentcontracts;

/** Erro de uma leitura de uma fonte ({@link PaymentSourcePolled} com {@code outcome = ERROR}). */
public enum PollErrorCode {
    SFTP_UNREACHABLE,
    SFTP_AUTH_FAILED,
    SFTP_HOST_KEY_MISMATCH,
    SFTP_DIRECTORY_NOT_FOUND,
    SECRET_NOT_FOUND,
    UNSUPPORTED_FORMAT,
    PUBLISH_FAILED,
    INTERNAL_ERROR
}
