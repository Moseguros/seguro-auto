package mz.co.moseguros.document.infrastructure.storage;

/**
 * O conteúdo de uma chave não existe no armazenamento. A mensagem nunca inclui a chave.
 */
public class StoredObjectMissingException extends RuntimeException {

    public StoredObjectMissingException() {
        super("Stored object not found");
    }

    public StoredObjectMissingException(Throwable cause) {
        super("Stored object not found", cause);
    }
}
