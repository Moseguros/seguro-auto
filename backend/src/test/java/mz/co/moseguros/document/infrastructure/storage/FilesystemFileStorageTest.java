package mz.co.moseguros.document.infrastructure.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Armazenamento em disco (S2-01 §3). */
class FilesystemFileStorageTest {

    @TempDir
    Path dir;

    @Test
    void putGetExistsAndDeleteTwice() throws IOException {
        FilesystemFileStorage storage = new FilesystemFileStorage(dir.resolve("files"));
        String key = "2026/12/01/" + UUID.randomUUID() + ".pdf";
        byte[] content = "%PDF-1.4 conteudo".getBytes(StandardCharsets.US_ASCII);

        assertThat(storage.exists(key)).isFalse();
        storage.put(key, new ByteArrayInputStream(content), content.length, "application/pdf");

        assertThat(storage.exists(key)).isTrue();
        try (InputStream in = storage.get(key)) {
            assertThat(in.readAllBytes()).isEqualTo(content);
        }
        try (var files = Files.walk(storage.baseDir())) {
            assertThat(files.filter(p -> p.toString().endsWith(".part"))).isEmpty();
        }

        storage.delete(key);
        assertThat(storage.exists(key)).isFalse();
        assertThatCode(() -> storage.delete(key)).doesNotThrowAnyException();
        assertThatThrownBy(() -> storage.get(key)).isInstanceOf(StoredObjectMissingException.class)
                .hasMessageNotContaining(key);
    }

    @Test
    void devSeedKeysAreAccepted() throws IOException {
        FilesystemFileStorage storage = new FilesystemFileStorage(dir);
        storage.put("dev-seed/dev-placeholder.pdf", new ByteArrayInputStream(new byte[] {1}), 1, "application/pdf");
        assertThat(storage.exists("dev-seed/dev-placeholder.pdf")).isTrue();
    }

    @Test
    void keysWithDotDotOrOtherShapesAreRefused() {
        FilesystemFileStorage storage = new FilesystemFileStorage(dir);
        for (String key : new String[] {
            "../segredo.pdf",
            "2026/12/01/../../../x.pdf",
            "dev-seed/../../x.pdf",
            "dev-seed/..",
            "/etc/passwd",
            "2026/12/01/" + UUID.randomUUID() + ".exe",
            "2026/12/01/livrete do cliente.pdf",
            "",
        }) {
            assertThatThrownBy(() -> storage.exists(key)).as(key).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> storage.put(key, InputStream.nullInputStream(), 0, "application/pdf"))
                    .as(key).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> storage.get(key)).as(key).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> storage.delete(key)).as(key).isInstanceOf(IllegalArgumentException.class);
        }
        assertThatThrownBy(() -> storage.exists(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createsTheBaseDirectoryWhenMissing() {
        Path base = dir.resolve("a/b/c");
        new FilesystemFileStorage(base);
        assertThat(base).isDirectory();
    }

    @Test
    void refusesABaseDirThatIsAFile() throws IOException {
        Path file = Files.writeString(dir.resolve("not-a-dir"), "x");
        assertThatThrownBy(() -> new FilesystemFileStorage(file)).isInstanceOf(IllegalStateException.class);
    }
}
