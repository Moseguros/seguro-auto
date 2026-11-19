package mz.co.moseguros.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * A chave pública do simulador M-Pesa usada nos dados de demonstração
 * ({@code classpath:dev/mpesa-mock-public.pem}) é a mesma do {@code mpesa-mock} (S5-03): se o par
 * do simulador mudar, os dois ficheiros mudam juntos.
 */
class MpesaMockKeyConsistencyTest {

    static final Path MOCK_PUBLIC_KEY = Path.of(System.getProperty("mpesa-mock.public-key",
            "../infra/mocks/mpesa-mock/src/main/resources/keys/mock-public.pem")).toAbsolutePath().normalize();

    @Test
    void backendCopyIsIdenticalToTheMockPublicKey() throws IOException {
        byte[] backendCopy;
        try (InputStream in = getClass().getResourceAsStream("/dev/mpesa-mock-public.pem")) {
            assertThat(in).as("classpath:dev/mpesa-mock-public.pem").isNotNull();
            backendCopy = in.readAllBytes();
        }

        assertThat(MOCK_PUBLIC_KEY).as("chave pública do mpesa-mock").exists();
        assertThat(backendCopy).isEqualTo(Files.readAllBytes(MOCK_PUBLIC_KEY));
    }
}
