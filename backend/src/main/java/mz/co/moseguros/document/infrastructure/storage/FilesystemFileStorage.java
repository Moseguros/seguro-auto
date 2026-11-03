package mz.co.moseguros.document.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Armazenamento em disco (perfis {@code local} e {@code test}, e docker-compose com o volume
 * {@code files}). Cada chave é um ficheiro debaixo de {@code moseguros.files.filesystem.base-dir}.
 *
 * <p>A chave é validada antes de qualquer operação (impede {@code ..} e caminhos absolutos).
 * {@code put} grava em {@code {chave}.part} e depois move de forma atómica: um leitor nunca vê um
 * ficheiro a meio.
 */
public class FilesystemFileStorage implements FileStorage {

    static final Pattern KEY = Pattern.compile(
            "^(\\d{4}/\\d{2}/\\d{2}/[0-9a-f-]{36}\\.(jpg|png|heic|pdf)|dev-seed/[a-z0-9._-]{1,100})$");

    private final Path baseDir;

    /** Cria a pasta se faltar; falha (a aplicação não arranca) se não for gravável. */
    public FilesystemFileStorage(Path baseDir) {
        this.baseDir = Objects.requireNonNull(baseDir, "baseDir").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create the files directory " + this.baseDir, e);
        }
        if (!Files.isDirectory(this.baseDir) || !Files.isWritable(this.baseDir)) {
            throw new IllegalStateException("The files directory is not writable: " + this.baseDir);
        }
    }

    public Path baseDir() {
        return baseDir;
    }

    @Override
    public void put(String key, InputStream content, long size, String contentType) {
        Path target = resolve(key);
        Path part = target.resolveSibling(target.getFileName() + ".part");
        try {
            Files.createDirectories(target.getParent());
            try (InputStream in = content) {
                Files.copy(in, part, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(part, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            deleteQuietly(part);
            throw new UncheckedIOException("Cannot write the stored object", e);
        }
    }

    @Override
    public InputStream get(String key) {
        Path path = resolve(key);
        try {
            return Files.newInputStream(path);
        } catch (NoSuchFileException e) {
            throw new StoredObjectMissingException(e);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read the stored object", e);
        }
    }

    @Override
    public boolean exists(String key) {
        return Files.isRegularFile(resolve(key));
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot delete the stored object", e);
        }
    }

    private Path resolve(String key) {
        if (key == null || !KEY.matcher(key).matches()) {
            throw new IllegalArgumentException("Invalid storage key");
        }
        Path raw = baseDir.resolve(key);
        Path path = raw.normalize();
        // o padrão aceita "dev-seed/.." (pontos são válidos no nome): nenhum segmento . ou ..
        if (!path.equals(raw) || !path.startsWith(baseDir) || path.getNameCount() <= baseDir.getNameCount() + 1) {
            throw new IllegalArgumentException("Invalid storage key");
        }
        return path;
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // o .part fica para trás; é substituído na próxima gravação da mesma chave
        }
    }
}
