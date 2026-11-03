package mz.co.moseguros.document.domain;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

/**
 * Descobre o tipo real de um ficheiro pelos bytes iniciais (S2-01 §6):
 *
 * <ul>
 *   <li>{@code image/jpeg}: {@code FF D8 FF};</li>
 *   <li>{@code image/png}: {@code 89 50 4E 47 0D 0A 1A 0A};</li>
 *   <li>{@code application/pdf}: {@code %PDF-};</li>
 *   <li>{@code image/heic}: bytes 4–7 = {@code ftyp} e bytes 8–11 ∈ {@code heic}, {@code heix},
 *       {@code hevc}, {@code hevx}, {@code mif1}, {@code msf1}.</li>
 * </ul>
 * Qualquer outro conteúdo é desconhecido.
 */
public final class ContentSniffer {

    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] PDF = "%PDF-".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] FTYP = "ftyp".getBytes(StandardCharsets.US_ASCII);
    private static final Set<String> HEIC_BRANDS = Set.of("heic", "heix", "hevc", "hevx", "mif1", "msf1");

    private ContentSniffer() {
    }

    public static Optional<AllowedContentType> sniff(byte[] content) {
        if (content == null) {
            return Optional.empty();
        }
        if (startsWith(content, 0, JPEG)) {
            return Optional.of(AllowedContentType.JPEG);
        }
        if (startsWith(content, 0, PNG)) {
            return Optional.of(AllowedContentType.PNG);
        }
        if (startsWith(content, 0, PDF)) {
            return Optional.of(AllowedContentType.PDF);
        }
        if (content.length >= 12 && startsWith(content, 4, FTYP)
                && HEIC_BRANDS.contains(new String(content, 8, 4, StandardCharsets.US_ASCII))) {
            return Optional.of(AllowedContentType.HEIC);
        }
        return Optional.empty();
    }

    private static boolean startsWith(byte[] content, int offset, byte[] signature) {
        if (content.length < offset + signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (content[offset + i] != signature[i]) {
                return false;
            }
        }
        return true;
    }
}
