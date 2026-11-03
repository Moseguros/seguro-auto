package mz.co.moseguros.document.domain;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Tipos de conteúdo aceites (ADR-005), com a extensão usada na chave de armazenamento e no nome
 * do ficheiro. Confirmados pelo conteúdo ({@link ContentSniffer}), nunca só pela extensão.
 */
public enum AllowedContentType {

    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    HEIC("image/heic", "heic"),
    PDF("application/pdf", "pdf");

    private final String mimeType;
    private final String extension;

    AllowedContentType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String mimeType() {
        return mimeType;
    }

    public String extension() {
        return extension;
    }

    /** Tipo com este MIME (sem parâmetros, sem diferenciar maiúsculas). */
    public static Optional<AllowedContentType> fromMimeType(String mimeType) {
        if (mimeType == null) {
            return Optional.empty();
        }
        String normalized = normalizeMimeType(mimeType);
        return Arrays.stream(values()).filter(t -> t.mimeType.equals(normalized)).findFirst();
    }

    /** Tipo com esta extensão (sem o ponto, sem diferenciar maiúsculas). */
    public static Optional<AllowedContentType> fromExtension(String extension) {
        if (extension == null) {
            return Optional.empty();
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        return Arrays.stream(values()).filter(t -> t.extension.equals(normalized)).findFirst();
    }

    /** {@code "Image/PNG; charset=x"} → {@code "image/png"}. */
    public static String normalizeMimeType(String mimeType) {
        int semicolon = mimeType.indexOf(';');
        String type = semicolon >= 0 ? mimeType.substring(0, semicolon) : mimeType;
        return type.trim().toLowerCase(Locale.ROOT);
    }
}
