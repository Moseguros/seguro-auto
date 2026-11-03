package mz.co.moseguros.document;

import java.io.IOException;
import java.io.InputStream;

/** Ficheiros reais de teste em {@code src/test/resources/files/}. */
public final class TestFiles {

    private TestFiles() {
    }

    public static byte[] bytes(String name) throws IOException {
        try (InputStream in = TestFiles.class.getResourceAsStream("/files/" + name)) {
            if (in == null) {
                throw new IllegalArgumentException("Missing test file: " + name);
            }
            return in.readAllBytes();
        }
    }
}
