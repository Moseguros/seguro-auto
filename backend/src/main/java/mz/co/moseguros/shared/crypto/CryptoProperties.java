package mz.co.moseguros.shared.crypto;

import java.util.Map;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Chaves da cifra de colunas ({@code moseguros.crypto}).
 *
 * @param dataKeys          versão → chave AES de 32 bytes em Base64
 * @param currentKeyVersion versão usada para cifrar valores novos (1–255)
 */
@Validated
@ConfigurationProperties(prefix = "moseguros.crypto")
public record CryptoProperties(@NotEmpty Map<Integer, String> dataKeys, @NotNull Integer currentKeyVersion) {

    static final int KEY_BYTES = 32;

    /** A versão actual tem chave e todas as chaves são Base64 válido de 32 bytes. */
    @AssertTrue(message = "moseguros.crypto: the current key version must have a key and every key must be 32 bytes in Base64")
    public boolean isValid() {
        if (dataKeys == null || currentKeyVersion == null) {
            return true; // @NotEmpty/@NotNull já reportam
        }
        if (currentKeyVersion < 1 || currentKeyVersion > 255 || !dataKeys.containsKey(currentKeyVersion)) {
            return false;
        }
        for (Map.Entry<Integer, String> entry : dataKeys.entrySet()) {
            if (entry.getKey() == null || entry.getKey() < 1 || entry.getKey() > 255 || decode(entry.getValue()) == null) {
                return false;
            }
        }
        return true;
    }

    static byte[] decode(String base64) {
        if (base64 == null || base64.isBlank()) {
            return null;
        }
        try {
            byte[] key = java.util.Base64.getDecoder().decode(base64.trim());
            return key.length == KEY_BYTES ? key : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
