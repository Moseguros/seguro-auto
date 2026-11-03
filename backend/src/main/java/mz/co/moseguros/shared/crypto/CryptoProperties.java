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
 * @param blindIndexKey     chave HMAC-SHA256 do {@link BlindIndex} (32 bytes em Base64), diferente das
 *                          chaves de cifra ({@code MOSEGUROS_BLIND_INDEX_KEY}; S2-03 §6)
 */
@Validated
@ConfigurationProperties(prefix = "moseguros.crypto")
public record CryptoProperties(@NotEmpty Map<Integer, String> dataKeys, @NotNull Integer currentKeyVersion,
                               String blindIndexKey) {

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

    /** A chave do índice cego existe e é Base64 válido de 32 bytes. */
    @AssertTrue(message = "moseguros.crypto.blind-index-key (MOSEGUROS_BLIND_INDEX_KEY) must be 32 bytes in Base64")
    public boolean isBlindIndexKeyValid() {
        return decode(blindIndexKey) != null;
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
