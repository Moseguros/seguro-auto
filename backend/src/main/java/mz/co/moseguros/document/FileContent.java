package mz.co.moseguros.document;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Conteúdo de um ficheiro. Quem recebe fecha o stream (ou o próprio {@code FileContent}).
 */
public record FileContent(FileRef ref, InputStream content) implements Closeable {

    @Override
    public void close() throws IOException {
        content.close();
    }
}
