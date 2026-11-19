package mz.co.moseguros.payment.infrastructure.mpesa;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;

import org.springframework.stereotype.Component;

/**
 * Token da API M-Pesa (S5-04 §1): {@code Base64(RSA/ECB/PKCS1Padding(apiKey))} com a chave pública da
 * seguradora, gerado em cada pedido (o PKCS#1 v1.5 é aleatório: cada token é diferente). A chave
 * pública aceita-se em PEM ({@code -----BEGIN PUBLIC KEY-----}, ignora o texto fora das linhas
 * {@code BEGIN}/{@code END}) ou em Base64 DER numa linha (como a Vodacom a entrega).
 *
 * <p>Só a {@link PublicKey} fica em cache, por {@code (method_config_id, version)}; a {@code apiKey}
 * e o token nunca ficam em memória além do pedido nem vão para logs.
 */
@Component
public class MpesaTokenFactory {

    private static final String BEGIN = "-----BEGIN PUBLIC KEY-----";
    private static final String END = "-----END PUBLIC KEY-----";

    private final Map<UUID, CachedKey> cache = new ConcurrentHashMap<>();

    /** A chave pública não se lê (nem PEM nem Base64 DER de uma chave RSA). */
    public static class InvalidPublicKeyException extends RuntimeException {

        public InvalidPublicKeyException(Throwable cause) {
            super("Invalid M-Pesa public key", cause);
        }
    }

    /**
     * A chave pública da configuração, da cache se a versão for a mesma.
     *
     * @throws InvalidPublicKeyException se o texto não for uma chave pública RSA
     */
    public PublicKey publicKey(UUID configId, long version, String publicKey) {
        Objects.requireNonNull(configId, "configId");
        CachedKey cached = cache.get(configId);
        if (cached != null && cached.version() == version) {
            return cached.key();
        }
        PublicKey parsed = parse(publicKey);
        cache.put(configId, new CachedKey(version, parsed));
        return parsed;
    }

    /** Token para o cabeçalho {@code Authorization: Bearer <token>}. */
    public String token(PublicKey publicKey, String apiKey) {
        Objects.requireNonNull(publicKey, "publicKey");
        Objects.requireNonNull(apiKey, "apiKey");
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(apiKey.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new InvalidPublicKeyException(e);
        }
    }

    /**
     * Lê uma chave pública RSA em PEM ou Base64 DER.
     *
     * @throws InvalidPublicKeyException se não for uma chave pública RSA
     */
    public static PublicKey parse(String text) {
        if (text == null || text.isBlank()) {
            throw new InvalidPublicKeyException(null);
        }
        String body = text;
        int begin = text.indexOf(BEGIN);
        if (begin >= 0) {
            int end = text.indexOf(END, begin);
            if (end < 0) {
                throw new InvalidPublicKeyException(null);
            }
            body = text.substring(begin + BEGIN.length(), end);
        }
        try {
            byte[] der = Base64.getDecoder().decode(body.replaceAll("\\s", ""));
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
        } catch (IllegalArgumentException | GeneralSecurityException e) {
            throw new InvalidPublicKeyException(e);
        }
    }

    private record CachedKey(long version, PublicKey key) {
    }
}
