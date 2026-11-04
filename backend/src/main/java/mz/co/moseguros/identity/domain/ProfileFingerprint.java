package mz.co.moseguros.identity.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Impressão digital dos dados de identidade do perfil (S4-01 §4): SHA-256, em hexadecimal
 * minúsculo, de {@code upper(trim(fullName)) | birthDate (ISO) | idDocType | idDocNumber}, com o
 * número do documento em claro. Serve só para saber se o perfil mudou depois de uma verificação;
 * nunca sai na API nem vai para um log.
 */
public final class ProfileFingerprint {

    private ProfileFingerprint() {
    }

    /**
     * Impressão digital dos quatro valores; {@code null} se faltar algum (perfil incompleto: não
     * coincide com nenhuma verificação).
     */
    public static String of(String fullName, LocalDate birthDate, IdDocType idDocType, String idDocNumber) {
        if (fullName == null || birthDate == null || idDocType == null || idDocNumber == null) {
            return null;
        }
        String input = fullName.trim().toUpperCase(Locale.ROOT) + "|" + birthDate + "|" + idDocType.name() + "|"
                + idDocNumber;
        return sha256(input);
    }

    /** Impressão digital do perfil actual; {@code null} se faltar nome, data, tipo ou número do documento. */
    public static String of(CustomerProfile profile) {
        return of(profile.getFullName(), profile.getBirthDate(), profile.getIdDocType(), profile.getIdDocNumber());
    }

    private static String sha256(String input) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
