package mz.co.moseguros.paymentworker.sftp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentcontracts.PollErrorCode;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpSession;
import org.springframework.stereotype.Component;

/**
 * Ligações SFTP às fontes (S5-09 §5, passo 3): {@link DefaultSftpSessionFactory} do Spring Integration,
 * <i>timeout</i> de 30 s, palavra-passe ou chave privada (com <i>passphrase</i>) e a chave do servidor
 * verificada pelo {@link FingerprintHostKeyVerifier}. Uma ligação por leitura; fechá-la fecha também o
 * cliente SSH.
 *
 * <p>Os erros de ligação viram {@link SftpException} com o {@link PollErrorCode}: chave do servidor
 * recusada → {@code SFTP_HOST_KEY_MISMATCH}; autenticação recusada → {@code SFTP_AUTH_FAILED}; o resto
 * → {@code SFTP_UNREACHABLE}. As mensagens nunca trazem credenciais.
 */
@Component
public class SftpSessions {

    static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final boolean allowUnknownHostKeys;

    public SftpSessions(WorkerProperties properties) {
        this.allowUnknownHostKeys = properties.allowUnknownHostKeys();
    }

    /** Credenciais lidas do cofre no momento da leitura (nunca guardadas). */
    public record Credentials(String password, String privateKey, String passphrase) {

        @Override
        public String toString() {
            return "Credentials[***]";
        }
    }

    /** Falha da ligação ou de uma operação SFTP, com o código da leitura. */
    public static class SftpException extends RuntimeException {

        private final PollErrorCode code;

        public SftpException(PollErrorCode code, String message) {
            super(message);
            this.code = code;
        }

        public PollErrorCode code() {
            return code;
        }
    }

    /** Um ficheiro da listagem. */
    public record RemoteFile(String name, long size, Instant modifiedAt) {
    }

    /** Abre uma ligação à fonte. */
    public Connection open(FileSourceDescriptor.Sftp sftp, Credentials credentials) {
        Objects.requireNonNull(sftp, "sftp");
        Objects.requireNonNull(credentials, "credentials");
        FingerprintHostKeyVerifier verifier = new FingerprintHostKeyVerifier(sftp.hostKeyFingerprint(),
                allowUnknownHostKeys);
        SshClient client = SshClient.setUpDefaultClient();
        client.setServerKeyVerifier(verifier);
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(client, false);
        factory.setHost(sftp.host());
        factory.setPort(sftp.port());
        factory.setUser(sftp.username());
        factory.setTimeout((int) TIMEOUT.toMillis());
        if (FileSourceDescriptor.Sftp.AUTH_KEY.equals(sftp.authType())) {
            factory.setPrivateKey(new ByteArrayResource(credentials.privateKey().getBytes(StandardCharsets.UTF_8)));
            if (credentials.passphrase() != null) {
                factory.setPrivateKeyPassphrase(credentials.passphrase());
            }
        } else {
            factory.setPassword(credentials.password());
        }
        try {
            SftpSession session = factory.getSession();
            return new Connection(client, factory, session);
        } catch (RuntimeException e) {
            stop(client, factory);
            throw classify(e, verifier);
        }
    }

    static SftpException classify(Throwable e, FingerprintHostKeyVerifier verifier) {
        if (verifier.rejected()) {
            return new SftpException(PollErrorCode.SFTP_HOST_KEY_MISMATCH, "SFTP host key refused");
        }
        for (Throwable t = e; t != null; t = t.getCause()) {
            String message = t.getMessage() == null ? "" : t.getMessage().toLowerCase(Locale.ROOT);
            if (message.contains("authentication") || message.contains("auth fail")
                    || message.contains("no more authentication methods")) {
                return new SftpException(PollErrorCode.SFTP_AUTH_FAILED, "SFTP authentication refused");
            }
            if (message.contains("server key did not validate")) {
                return new SftpException(PollErrorCode.SFTP_HOST_KEY_MISMATCH, "SFTP host key refused");
            }
        }
        return new SftpException(PollErrorCode.SFTP_UNREACHABLE, "SFTP server unreachable: "
                + rootCause(e).getClass().getSimpleName());
    }

    private static Throwable rootCause(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t;
    }

    /** O cliente SSH é nosso (não do factory): pará-lo fecha as ligações. */
    private static void stop(SshClient client, DefaultSftpSessionFactory factory) {
        try {
            factory.destroy();
        } catch (Exception ignored) {
            // a fechar: nada a fazer
        }
        try {
            client.stop();
        } catch (RuntimeException ignored) {
            // a fechar: nada a fazer
        }
    }

    /** Uma ligação aberta. Os caminhos são relativos à raiz do utilizador no servidor. */
    public static final class Connection implements AutoCloseable {

        private final SshClient client;
        private final DefaultSftpSessionFactory factory;
        private final SftpSession session;

        Connection(SshClient client, DefaultSftpSessionFactory factory, SftpSession session) {
            this.client = client;
            this.factory = factory;
            this.session = session;
        }

        /** A pasta (ou o ficheiro) existe. */
        public boolean exists(String path) {
            try {
                return session.exists(path);
            } catch (RuntimeException e) {
                throw new SftpException(PollErrorCode.SFTP_UNREACHABLE, "SFTP exists failed: "
                        + e.getClass().getSimpleName());
            }
        }

        /** Só os ficheiros regulares de {@code dir} (sem pastas nem ligações). */
        public List<RemoteFile> listFiles(String dir) {
            SftpClient.DirEntry[] entries;
            try {
                entries = session.list(dir);
            } catch (IOException | RuntimeException e) {
                throw new SftpException(PollErrorCode.SFTP_UNREACHABLE, "SFTP list failed: "
                        + e.getClass().getSimpleName());
            }
            List<RemoteFile> files = new ArrayList<>();
            for (SftpClient.DirEntry entry : entries) {
                SftpClient.Attributes attributes = entry.getAttributes();
                if (attributes == null || !attributes.isRegularFile()) {
                    continue;
                }
                Instant modified = attributes.getModifyTime() == null ? null
                        : attributes.getModifyTime().toInstant();
                files.add(new RemoteFile(entry.getFilename(), attributes.getSize(), modified));
            }
            return files;
        }

        /** Descarrega {@code path} para {@code target}. */
        public void download(String path, Path target) {
            try (OutputStream out = Files.newOutputStream(target)) {
                session.read(path, out);
            } catch (IOException | RuntimeException e) {
                throw new SftpException(PollErrorCode.SFTP_UNREACHABLE, "SFTP download failed: "
                        + e.getClass().getSimpleName());
            }
        }

        /** Muda de nome/pasta. */
        public void rename(String from, String to) {
            try {
                session.rename(from, to);
            } catch (IOException | RuntimeException e) {
                throw new UncheckedIOException(new IOException("SFTP rename failed: " + e.getClass().getSimpleName()));
            }
        }

        @Override
        public void close() {
            try {
                session.close();
            } catch (RuntimeException ignored) {
                // a fechar
            } finally {
                stop(client, factory);
            }
        }
    }
}
