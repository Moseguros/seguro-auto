package mz.co.moseguros.ponto24mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import mz.co.moseguros.ponto24mock.MockProperties.Format;
import mz.co.moseguros.ponto24mock.MockProperties.Source;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/** Entrega por SFTP (S5-08 §6.3) num {@code atmoz/sftp} real: o ficheiro chega com o nome final, sem {@code .part}. */
@Testcontainers
class SftpDeliveryIT {

    /** A mesma imagem do compose (S5-06), fixada pelo digest. */
    static final String SFTP_IMAGE =
            "atmoz/sftp:alpine@sha256:43dd1c023cf1bae487127d4b54660b0aee6e94c188a6599be36fb89ba115f961";

    @org.testcontainers.junit.jupiter.Container
    static final GenericContainer<?> SFTP = new GenericContainer<>(DockerImageName.parse(SFTP_IMAGE))
            .withCommand("demo:demo-sftp-pass:1001::inbound,processed,error")
            .withExposedPorts(22)
            .waitingFor(Wait.forLogMessage(".*Server listening on.*", 1).withStartupTimeout(Duration.ofMinutes(2)));

    final SftpDelivery delivery = new SftpDelivery();

    Source source() {
        return new Source("90002", Format.MEPS, null, null, SFTP.getHost(), SFTP.getMappedPort(22), "demo", "inbound");
    }

    @Test
    void fileArrivesInInboundWithTheFinalNameAndWithoutPart() throws Exception {
        byte[] content = MepsFileWriter.write("90002", "202611191", null, FileWritersTest.mepsPayments(),
                MockProperties.FileStyle.EXAMPLE, FileWritersTest.MAPUTO);

        delivery.deliver(source(), "demo-sftp-pass", "MEPS_90002_202611191.txt", content);

        assertThat(exec("ls", "-1", "/home/demo/inbound").trim()).isEqualTo("MEPS_90002_202611191.txt");
        Container.ExecResult cat = SFTP.execInContainer("cat", "/home/demo/inbound/MEPS_90002_202611191.txt");
        assertThat(cat.getStdout().getBytes(StandardCharsets.ISO_8859_1)).isEqualTo(content);
    }

    @Test
    void wrongPasswordFailsWithoutLeakingIt() {
        assertThatThrownBy(() -> delivery.deliver(source(), "palavra-passe-errada", "x.txt", new byte[] {1}))
                .isInstanceOf(SftpDelivery.DeliveryException.class)
                .hasMessageNotContaining("palavra-passe-errada");
    }

    static String exec(String... command) throws Exception {
        Container.ExecResult result = SFTP.execInContainer(command);
        assertThat(result.getExitCode()).as(result.getStderr()).isZero();
        return result.getStdout();
    }
}
