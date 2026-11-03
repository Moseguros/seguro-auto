package mz.co.moseguros.document.infrastructure.storage;

import java.io.InputStream;

/**
 * Porta de armazenamento do conteúdo dos ficheiros (ADR-005), interna ao módulo
 * {@code document}. Implementações: {@link FilesystemFileStorage} (local, testes, compose) e
 * {@link S3FileStorage} (staging e produção), escolhidas por {@code moseguros.files.storage}.
 *
 * <p>Chaves {@code {aaaa}/{mm}/{dd}/{fileId}.{ext}} (ou {@code dev-seed/{nome}} nos dados de
 * demonstração): nunca contêm o nome original nem dados pessoais, e nunca vão para os logs.
 */
public interface FileStorage {

    void put(String key, InputStream content, long size, String contentType);

    /** Quem recebe fecha o stream. Chave inexistente → {@link StoredObjectMissingException}. */
    InputStream get(String key);

    boolean exists(String key);

    /** Idempotente. Usado só pela limpeza de órfãos e quando a gravação da linha falha. */
    void delete(String key);
}
