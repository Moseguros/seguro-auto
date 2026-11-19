package mz.co.moseguros.paymentworker.sftp;

import java.net.SocketAddress;
import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.sshd.client.keyverifier.ServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.common.digest.BuiltinDigests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verifica a chave do servidor SFTP pela impressão digital configurada na fonte
 * ({@code sftp.hostKeyFingerprint}, S5-01), no formato {@code SHA256:<base64 sem =>} (o do
 * {@code ssh-keygen -lf}). Diferente → recusa ({@code SFTP_HOST_KEY_MISMATCH}). Sem impressão digital
 * aceita só com {@code allow-unknown-host-keys = true} ({@code local} e {@code test}).
 *
 * <p>Uma instância por ligação: {@link #rejected()} diz se a recusa veio daqui.
 */
public class FingerprintHostKeyVerifier implements ServerKeyVerifier {

    private static final Logger log = LoggerFactory.getLogger(FingerprintHostKeyVerifier.class);

    private final String expected;
    private final boolean allowUnknown;
    private final AtomicBoolean rejected = new AtomicBoolean();

    /**
     * @param expected     {@code SHA256:…} ou {@code null}
     * @param allowUnknown aceitar qualquer chave quando {@code expected} é {@code null}
     */
    public FingerprintHostKeyVerifier(String expected, boolean allowUnknown) {
        this.expected = expected == null || expected.isBlank() ? null : normalize(expected);
        this.allowUnknown = allowUnknown;
    }

    @Override
    public boolean verifyServerKey(ClientSession session, SocketAddress remoteAddress, PublicKey serverKey) {
        String actual = fingerprint(serverKey);
        if (expected == null) {
            if (allowUnknown) {
                log.debug("Accepting unknown SFTP host key {} of {} (allow-unknown-host-keys)", actual, remoteAddress);
                return true;
            }
            log.warn("SFTP host key of {} refused: the source has no hostKeyFingerprint", remoteAddress);
            rejected.set(true);
            return false;
        }
        if (expected.equals(actual)) {
            return true;
        }
        log.warn("SFTP host key of {} refused: fingerprint {} differs from the configured one", remoteAddress, actual);
        rejected.set(true);
        return false;
    }

    /** {@code true} se a chave foi recusada por esta instância. */
    public boolean rejected() {
        return rejected.get();
    }

    /** {@code SHA256:<base64 sem =>} da chave. */
    public static String fingerprint(PublicKey key) {
        return normalize(KeyUtils.getFingerPrint(BuiltinDigests.sha256, key));
    }

    /** Sem os {@code =} finais (o schema do S5-01 aceita os dois). */
    static String normalize(String fingerprint) {
        String f = fingerprint.trim();
        while (f.endsWith("=")) {
            f = f.substring(0, f.length() - 1);
        }
        return f;
    }
}
