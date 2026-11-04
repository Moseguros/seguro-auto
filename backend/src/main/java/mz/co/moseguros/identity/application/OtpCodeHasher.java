package mz.co.moseguros.identity.application;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Códigos OTP: geração ({@link SecureRandom}) e hash
 * {@code HMAC-SHA256(pepper, challengeId + ":" + code)} em hexadecimal, comparado em tempo
 * constante. Os códigos de assinatura (S4-04) ligam-se ao documento:
 * {@code HMAC-SHA256(pepper, challengeId + ":" + code + ":" + documentHash)}, e só servem para o
 * documento com esse hash. O código nunca é gravado nem registado.
 */
public class OtpCodeHasher {

    private static final String ALGORITHM = "HmacSHA256";

    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public OtpCodeHasher(String pepper) {
        if (pepper == null || pepper.isBlank()) {
            throw new IllegalArgumentException("pepper is required");
        }
        this.key = new SecretKeySpec(pepper.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    /** {@code length} dígitos aleatórios (pode começar por 0). */
    public String generate(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append((char) ('0' + random.nextInt(10)));
        }
        return code.toString();
    }

    /** Hash gravado em {@code otp_challenge.code_hash} (64 caracteres hexadecimais). */
    public String hash(UUID challengeId, String code) {
        Objects.requireNonNull(challengeId, "challengeId");
        Objects.requireNonNull(code, "code");
        return HexFormat.of().formatHex(mac(challengeId + ":" + code));
    }

    /** Hash de um código de assinatura, ligado ao documento (S4-04 §4, regra 5). */
    public String hash(UUID challengeId, String code, String documentHash) {
        Objects.requireNonNull(challengeId, "challengeId");
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(documentHash, "documentHash");
        return HexFormat.of().formatHex(mac(challengeId + ":" + code + ":" + documentHash));
    }

    /** Compara em tempo constante ({@link MessageDigest#isEqual}). */
    public boolean matches(UUID challengeId, String code, String expectedHash) {
        if (code == null || expectedHash == null) {
            return false;
        }
        return equal(hash(challengeId, code), expectedHash);
    }

    /** Como {@link #matches(UUID, String, String)}, para um código de assinatura do documento indicado. */
    public boolean matches(UUID challengeId, String code, String documentHash, String expectedHash) {
        if (code == null || documentHash == null || expectedHash == null) {
            return false;
        }
        return equal(hash(challengeId, code, documentHash), expectedHash);
    }

    private static boolean equal(String actual, String expected) {
        return MessageDigest.isEqual(actual.getBytes(StandardCharsets.US_ASCII),
                expected.getBytes(StandardCharsets.US_ASCII));
    }

    private byte[] mac(String value) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HMAC-SHA256 is not available", e);
        }
    }

    @Override
    public String toString() {
        return "OtpCodeHasher[pepper=***]";
    }
}
