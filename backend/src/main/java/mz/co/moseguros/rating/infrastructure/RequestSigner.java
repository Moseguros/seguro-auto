package mz.co.moseguros.rating.infrastructure;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Assinatura dos pedidos à seguradora externa (contrato {@code moseguros-rating-1}, S3-02 §8):
 * {@code X-MoSeguros-Signature: v1=<hex minúsculo de HMAC-SHA256(segredo, timestamp + "." + corpo)>},
 * com o corpo exacto em bytes UTF-8. Nunca se regista em log (nem o segredo, nem a assinatura).
 */
public final class RequestSigner {

    public static final String VERSION_PREFIX = "v1=";
    private static final String ALGORITHM = "HmacSHA256";

    private RequestSigner() {
    }

    /**
     * @param timestampSeconds segundos desde 1970 (UTC), o mesmo de {@code X-MoSeguros-Timestamp}
     * @param body             o corpo exacto enviado
     * @return {@code v1=<64 caracteres hexadecimais minúsculos>}
     */
    public static String sign(String secret, long timestampSeconds, byte[] body) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM));
            mac.update((timestampSeconds + ".").getBytes(StandardCharsets.UTF_8));
            mac.update(body);
            return VERSION_PREFIX + HexFormat.of().formatHex(mac.doFinal());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HMAC-SHA256 not available", e);
        }
    }
}
