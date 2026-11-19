package mz.co.moseguros.mpesamock;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Cipher;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Autenticação da API M-Pesa: {@code Authorization: Bearer <token>}, em que o token é o Base64 da
 * API Key cifrada com a chave pública ({@code RSA/ECB/PKCS1Padding}), e o cabeçalho {@code Origin}.
 * O simulador decifra com {@code keys/mock-private.pem}. Nunca regista o token nem a chave decifrada.
 */
@Component
class AuthVerifier {

    private static final String BEARER = "bearer ";

    private final MockProperties props;
    private final PrivateKey privateKey;

    AuthVerifier(MockProperties props) {
        this.props = props;
        this.privateKey = loadPrivateKey(props);
    }

    /** Vazio se o pedido está autenticado; senão INS-2 (token) ou INS-26 ({@code Origin}). */
    Optional<ResponseCodes> verify(HttpServletRequest request) {
        if (!validToken(request.getHeader(HttpHeaders.AUTHORIZATION))) {
            return Optional.of(ResponseCodes.INS_2);
        }
        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if (origin == null || !origin.equals(props.getAllowedOrigin())) {
            return Optional.of(ResponseCodes.INS_26);
        }
        return Optional.empty();
    }

    private boolean validToken(String authorization) {
        if (authorization == null || authorization.length() <= BEARER.length()
                || !authorization.regionMatches(true, 0, BEARER, 0, BEARER.length())) {
            return false;
        }
        try {
            byte[] encrypted = Base64.getDecoder().decode(authorization.substring(BEARER.length()).trim());
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] apiKey = cipher.doFinal(encrypted);
            return MessageDigest.isEqual(apiKey, props.getApiKey().getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException | GeneralSecurityException e) {
            return false;
        }
    }

    private static PrivateKey loadPrivateKey(MockProperties props) {
        if (props.getPrivateKey() == null) {
            throw new IllegalStateException("mock.private-key não está definido");
        }
        try (InputStream in = props.getPrivateKey().getInputStream()) {
            String pem = new String(in.readAllBytes(), StandardCharsets.US_ASCII);
            byte[] der = Pem.decode(pem, "PRIVATE KEY");
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Chave privada do simulador inválida", e);
        }
    }

    /** Leitura mínima de PEM: ignora o texto fora das linhas BEGIN/END (RFC 7468 §2). */
    static final class Pem {
        private Pem() {}

        static byte[] decode(String pem, String label) {
            String begin = "-----BEGIN " + label + "-----";
            String end = "-----END " + label + "-----";
            int start = pem.indexOf(begin);
            int stop = pem.indexOf(end);
            if (start < 0 || stop < start) {
                throw new IllegalArgumentException("PEM sem " + label);
            }
            String body = pem.substring(start + begin.length(), stop).replaceAll("\\s", "");
            return Base64.getDecoder().decode(body);
        }
    }
}
