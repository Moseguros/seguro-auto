package mz.co.moseguros.shared.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * Cifra de colunas com AES-256-GCM (AGENTS.md §6): documentos de identificação, NUIT de
 * pessoas singulares e segredos TOTP.
 *
 * <p>Formato gravado: {@code [versão da chave: 1 byte][IV: 12 bytes][texto cifrado + etiqueta de 128 bits]}.
 * O IV é aleatório por valor, por isso cifrar duas vezes o mesmo texto dá resultados
 * diferentes. Valores cifrados com versões antigas continuam legíveis enquanto a chave
 * dessa versão estiver configurada.
 */
@Service
@EnableConfigurationProperties(CryptoProperties.class)
public class CryptoService {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final int HEADER_BYTES = 1 + IV_BYTES;

    private final Map<Integer, SecretKey> keys = new HashMap<>();
    private final int currentVersion;
    private final SecureRandom random = new SecureRandom();

    public CryptoService(CryptoProperties properties) {
        if (!properties.isValid() || properties.dataKeys() == null || properties.currentKeyVersion() == null) {
            throw new IllegalStateException("Invalid moseguros.crypto configuration");
        }
        properties.dataKeys().forEach((version, base64) ->
                keys.put(version, new SecretKeySpec(CryptoProperties.decode(base64), "AES")));
        this.currentVersion = properties.currentKeyVersion();
    }

    /** Cifra com a chave actual; {@code null} → {@code null}. */
    public byte[] encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        byte[] iv = new byte[IV_BYTES];
        random.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keys.get(currentVersion), new GCMParameterSpec(TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.allocate(HEADER_BYTES + ciphertext.length)
                    .put((byte) currentVersion)
                    .put(iv)
                    .put(ciphertext)
                    .array();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("AES-GCM encryption failed", e);
        }
    }

    /** Decifra; {@code null} → {@code null}. Valor alterado ou chave desconhecida → {@code DECRYPTION_FAILED} (500). */
    public String decrypt(byte[] value) {
        if (value == null) {
            return null;
        }
        if (value.length <= HEADER_BYTES) {
            throw failed();
        }
        SecretKey key = keys.get(Byte.toUnsignedInt(value[0]));
        if (key == null) {
            throw failed();
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, value, 1, IV_BYTES));
            byte[] plaintext = cipher.doFinal(value, HEADER_BYTES, value.length - HEADER_BYTES);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw failed();
        }
    }

    private static DomainException failed() {
        return new DomainException(ErrorCodes.DECRYPTION_FAILED, 500, "Encrypted value could not be decrypted");
    }
}
