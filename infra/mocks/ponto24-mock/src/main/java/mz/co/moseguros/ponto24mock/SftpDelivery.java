package mz.co.moseguros.ponto24mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import mz.co.moseguros.ponto24mock.MockProperties.Source;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.stereotype.Component;

/**
 * Entrega um ficheiro no SFTP da seguradora como faria o banco (S5-08 §6.3): primeiro com o sufixo
 * {@code .part} e depois com um <i>rename</i>, para o worker nunca ler um ficheiro a meio. Aceita qualquer
 * chave do servidor (só local, testes e QA). A palavra-passe nunca vai para os logs nem para as mensagens.
 */
@Component
public class SftpDelivery {

    /** O envio falhou; os pagamentos ficam pendentes. */
    public static class DeliveryException extends RuntimeException {
        public DeliveryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void deliver(Source source, String password, String fileName, byte[] content) {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(source.host());
        factory.setPort(source.port());
        factory.setUser(source.username());
        factory.setPassword(password);
        factory.setAllowUnknownKeys(true);
        String dir = source.dir().isEmpty() || source.dir().endsWith("/") ? source.dir() : source.dir() + "/";
        String finalPath = dir + fileName;
        String partPath = finalPath + ".part";
        try {
            Session<SftpClient.DirEntry> session = factory.getSession();
            try {
                session.write(new ByteArrayInputStream(content), partPath);
                session.rename(partPath, finalPath);
            } finally {
                session.close();
            }
        } catch (IOException | RuntimeException e) {
            throw new DeliveryException("SFTP delivery of " + fileName + " to " + source.destination() + " failed: "
                    + e.getClass().getSimpleName(), e);
        } finally {
            try {
                factory.destroy();
            } catch (Exception ignored) {
                // o cliente SSH já está fechado ou nunca chegou a abrir
            }
        }
    }
}
