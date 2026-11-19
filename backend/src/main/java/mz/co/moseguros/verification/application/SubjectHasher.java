package mz.co.moseguros.verification.application;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import mz.co.moseguros.verification.domain.SubjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * {@code subject_hash} do registo das verificações (S6-03 §4): HMAC-SHA256 de
 * {@code subject_type + ":" + valor}, em hexadecimal minúsculo. Um SHA-256 simples não chegaria: o espaço
 * das matrículas é pequeno e daria para inverter por força bruta.
 *
 * <p>A chave é {@code moseguros.verification.hash-key} ({@code MOSEGUROS_VERIFICATION_HASH_KEY}, 32 bytes
 * em Base64); sem ela a aplicação não arranca. Mudá-la deixa de ligar as consultas novas às antigas.
 */
@Component
@EnableConfigurationProperties(VerificationProperties.class)
public class SubjectHasher {

    private static final String ALGORITHM = "HmacSHA256";
    /** Caracteres do hash que podem ir para os logs. */
    static final int LOG_PREFIX = 12;

    private final SecretKeySpec key;

    @Autowired
    public SubjectHasher(VerificationProperties properties) {
        this(properties.hashKey());
    }

    SubjectHasher(String base64Key) {
        byte[] raw = decode(base64Key);
        if (raw == null) {
            throw new IllegalStateException(
                    "moseguros.verification.hash-key (MOSEGUROS_VERIFICATION_HASH_KEY) must be 32 bytes in Base64");
        }
        this.key = new SecretKeySpec(raw, ALGORITHM);
    }

    /** HMAC-SHA256 (64 caracteres hexadecimais) de {@code type + ":" + value}. */
    public String hash(SubjectType type, String value) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(value, "value");
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(key);
            return HexFormat.of().formatHex(mac.doFinal((type.name() + ":" + value).getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA256 is not available", e);
        }
    }

    /** Os primeiros 12 caracteres do hash, para os logs (§4 regra 4). */
    public static String forLog(String hash) {
        return hash == null ? null : hash.substring(0, Math.min(LOG_PREFIX, hash.length()));
    }

    private static byte[] decode(String base64) {
        if (base64 == null || base64.isBlank()) {
            return null;
        }
        try {
            byte[] raw = Base64.getDecoder().decode(base64.strip());
            return raw.length == 32 ? raw : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "SubjectHasher[key=***]";
    }
}
