package mz.co.moseguros.paymentworker.secrets;

import java.util.Optional;

/**
 * Leitura de um segredo do cofre pelo caminho que o backend dá ({@code secretRefs}, S5-01 §2): ex.
 * {@code secret/insurers/{insurerId}/payments/BANK_REFERENCE/sftp.password}. O worker nunca grava
 * segredos nem os regista em logs.
 */
public interface SecretReader {

    /**
     * Valor do segredo, ou vazio se não existir.
     *
     * @throws SecretStoreUnavailableException o cofre não respondeu
     */
    Optional<String> read(String path);

    /** O cofre não respondeu ou recusou o pedido (sem a causa: pode trazer o corpo do pedido). */
    class SecretStoreUnavailableException extends RuntimeException {

        public SecretStoreUnavailableException(String message) {
            super(message);
        }
    }
}
