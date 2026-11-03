package mz.co.moseguros.document.domain;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Limpa o nome original de um ficheiro enviado (S2-01 §6):
 *
 * <ul>
 *   <li>fica só o nome base (tudo depois do último {@code /} ou {@code \});</li>
 *   <li>caracteres fora de {@code [\p{L}\p{N} ._()-]} passam a {@code _}; espaços repetidos passam
 *       a um;</li>
 *   <li>corta a {@value #MAX_LENGTH} caracteres mantendo a extensão;</li>
 *   <li>nome vazio → {@value #DEFAULT_NAME};</li>
 *   <li>a extensão é sempre a do tipo real ({@code foto.png} com conteúdo JPEG → {@code foto.jpg}).</li>
 * </ul>
 */
public final class FilenameSanitizer {

    public static final int MAX_LENGTH = 200;
    public static final String DEFAULT_NAME = "ficheiro";

    private static final Pattern NOT_ALLOWED = Pattern.compile("[^\\p{L}\\p{N} ._()-]");
    private static final Pattern SPACES = Pattern.compile(" {2,}");
    /** Extensão que se troca pela do tipo real: 1 a 4 letras ou dígitos depois do último ponto. */
    private static final Pattern EXTENSION = Pattern.compile("\\.[\\p{L}\\p{N}]{1,4}$");

    private FilenameSanitizer() {
    }

    /**
     * @param raw         nome indicado pelo cliente (pode ser {@code null})
     * @param contentType tipo real do conteúdo (um dos {@link AllowedContentType})
     */
    public static String clean(String raw, String contentType) {
        return clean(raw, AllowedContentType.fromMimeType(contentType)
                .orElseThrow(() -> new IllegalArgumentException("Content type not allowed: " + contentType)));
    }

    /**
     * @param raw         nome indicado pelo cliente (pode ser {@code null})
     * @param contentType tipo real do conteúdo
     */
    public static String clean(String raw, AllowedContentType contentType) {
        String extension = "." + contentType.extension();
        String name = raw == null ? "" : Normalizer.normalize(raw, Normalizer.Form.NFC);
        name = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1);
        name = NOT_ALLOWED.matcher(name).replaceAll("_");
        name = SPACES.matcher(name).replaceAll(" ");
        name = EXTENSION.matcher(name).replaceFirst("");
        name = stripDotsAndSpaces(name);
        if (name.isEmpty()) {
            name = DEFAULT_NAME;
        }
        int maxBase = MAX_LENGTH - extension.length();
        if (name.codePointCount(0, name.length()) > maxBase) {
            name = stripDotsAndSpaces(name.substring(0, name.offsetByCodePoints(0, maxBase)));
        }
        return name + extension;
    }

    /** Pontos e espaços nas pontas ({@code ..}, {@code .oculto}, {@code nome.}) não fazem sentido. */
    private static String stripDotsAndSpaces(String name) {
        int start = 0;
        int end = name.length();
        while (start < end && (name.charAt(start) == '.' || name.charAt(start) == ' ')) {
            start++;
        }
        while (end > start && (name.charAt(end - 1) == '.' || name.charAt(end - 1) == ' ')) {
            end--;
        }
        return name.substring(start, end);
    }
}
