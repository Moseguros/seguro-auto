package mz.co.moseguros.shared.crypto;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Índice cego (S2-03 §6): HMAC-SHA256 de um valor cifrado na coluna, para procurar e impedir
 * repetidos sem o decifrar (ex.: o número da carta de condução; os sprints de bónus/malus usam o
 * mesmo hash).
 *
 * <p>A chave ({@code moseguros.crypto.blind-index-key}, {@code MOSEGUROS_BLIND_INDEX_KEY}) é diferente
 * das chaves do {@link CryptoService}; mudá-la invalida todos os hashes gravados. Sem ela a aplicação
 * não arranca.
 */
@Component
@EnableConfigurationProperties(CryptoProperties.class)
public class BlindIndex {

    private static final String ALGORITHM = "HmacSHA256";

    private final SecretKeySpec key;

    public BlindIndex(CryptoProperties properties) {
        byte[] raw = CryptoProperties.decode(properties.blindIndexKey());
        if (raw == null) {
            throw new IllegalStateException(
                    "moseguros.crypto.blind-index-key (MOSEGUROS_BLIND_INDEX_KEY) must be 32 bytes in Base64");
        }
        this.key = new SecretKeySpec(raw, ALGORITHM);
    }

    /**
     * HMAC-SHA256, em hexadecimal minúsculo (64 caracteres), de {@code purpose + ":" + normalizedValue}.
     *
     * @param purpose         finalidade do índice (ex.: {@code licence}): o mesmo valor com finalidades
     *                        diferentes dá hashes diferentes
     * @param normalizedValue valor já normalizado por quem chama (maiúsculas, sem espaços…)
     */
    public String hash(String purpose, String normalizedValue) {
        Objects.requireNonNull(purpose, "purpose");
        Objects.requireNonNull(normalizedValue, "normalizedValue");
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            byte[] digest = mac.doFinal((purpose + ":" + normalizedValue).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA256 is not available", e);
        }
    }

    @Override
    public String toString() {
        return "BlindIndex[key=***]";
    }
}
