package mz.co.moseguros.paymentworker;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentworker.WorkerTestConfiguration.NackingRabbitTemplate;
import mz.co.moseguros.paymentworker.secrets.InMemorySecretReader;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Base dos testes de integração do worker (S5-09): Postgres com o {@code init-roles.sql} (a aplicação liga-se
 * como {@code moseguros_worker_user}, o Flyway como dono), RabbitMQ {@code rabbitmq:4.1-management}
 * (decisão D19), {@code atmoz/sftp} com o registo das operações SFTP em {@code /tmp/sftp.log}, e o backend simulado com WireMock
 * (token de serviço e uma lista de fontes vazia: os testes chamam o {@code SourcePoller} directamente). Os
 * ficheiros são MEPS e BMEPS reais, lidos pelos leitores do S5-08.
 * Uma fila de teste ligada a {@code payments.events} por {@code reference.#} recebe todas as mensagens.
 *
 * <p>Os contentores arrancam uma vez por execução e são partilhados pelas classes de teste.
 */
@SpringBootTest(properties = {
    "moseguros.worker.poll-interval=PT2S",
    "moseguros.worker.test-allow-short-poll-interval=true",
    "moseguros.worker.sources-refresh-interval=PT1H"
})
@ActiveProfiles("test")
@Import(WorkerTestConfiguration.class)
public abstract class WorkerITSupport {

    public static final String SFTP_IMAGE =
            "atmoz/sftp:alpine@sha256:43dd1c023cf1bae487127d4b54660b0aee6e94c188a6599be36fb89ba115f961";
    public static final String SFTP_USER = "demo";
    public static final String SFTP_PASSWORD = "demo-sftp-pass-for-tests";
    public static final String SERVICE_TOKEN = "wiremock-service-token-0123456789";
    public static final String TEST_QUEUE = "test.payment-events";
    public static final String ENTITY = "90002";
    public static final String BMEPS_ENTITY = "90090";
    static final String CRLF = "\r\n";

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withInitScript("db/init-roles.sql");
    static final RabbitMQContainer RABBIT = new RabbitMQContainer(DockerImageName.parse("rabbitmq:4.1-management"));
    public static final GenericContainer<?> SFTP = new GenericContainer<>(DockerImageName.parse(SFTP_IMAGE))
            .withCommand(SFTP_USER + ":" + SFTP_PASSWORD + ":1001")
            .withCopyFileToContainer(MountableFile.forClasspathResource("sftp/sshd_config", 0644),
                    "/etc/ssh/sshd_config")
            .withCopyFileToContainer(MountableFile.forClasspathResource("sftp/sftp-logged.sh", 0755),
                    "/usr/local/bin/sftp-logged.sh")
            .withExposedPorts(22)
            .waitingFor(Wait.forLogMessage(".*Server listening on.*", 1).withStartupTimeout(Duration.ofMinutes(2)));
    public static final WireMockServer BACKEND = new WireMockServer(wireMockConfig().dynamicPort());

    static {
        Startables.deepStart(POSTGRES, RABBIT, SFTP).join();
        BACKEND.start();
        BACKEND.stubFor(post(urlEqualTo("/internal/v1/auth/token")).willReturn(okJson(
                "{\"access_token\":\"" + SERVICE_TOKEN + "\",\"token_type\":\"Bearer\",\"expires_in\":900,"
                        + "\"scope\":\"payments:file-sources\"}")));
        BACKEND.stubFor(get(urlEqualTo("/internal/v1/payment-file-sources")).willReturn(okJson("{\"sources\":[]}")));
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", () -> "moseguros_worker_user");
        registry.add("spring.datasource.password", () -> "moseguros_worker");
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("spring.rabbitmq.host", RABBIT::getHost);
        registry.add("spring.rabbitmq.port", RABBIT::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBIT::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBIT::getAdminPassword);
        registry.add("moseguros.worker.backend-base-url", BACKEND::baseUrl);
    }

    @Autowired
    protected InMemorySecretReader secrets;

    @Autowired
    protected NackingRabbitTemplate rabbit;

    @Autowired
    protected RabbitAdmin rabbitAdmin;

    @Autowired
    protected JdbcTemplate jdbc;

    protected final ObjectMapper json = new ObjectMapper();

    @BeforeEach
    void testQueue() {
        Queue queue = new Queue(TEST_QUEUE, false, false, false);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue)
                .to(new TopicExchange(PaymentsTopology.EVENTS_EXCHANGE)).with("reference.#"));
        rabbitAdmin.purgeQueue(TEST_QUEUE, false);
        rabbit.reset();
    }

    // --- fontes ---

    /** Fonte MEPS da entidade {@link #ENTITY} com pastas próprias no SFTP ({@code <dir>/inbound}, …) e a palavra-passe no cofre. */
    protected FileSourceDescriptor source(String dir, boolean move) {
        return source(dir, move, null, SFTP_PASSWORD);
    }

    protected FileSourceDescriptor source(String dir, boolean move, String fingerprint, String password) {
        return source(dir, move, fingerprint, password, ENTITY, FileSourceDescriptor.FORMAT_MEPS, null, null, "*.txt");
    }

    /**
     * Fonte BMEPS (entidade {@link #BMEPS_ENTITY}, empresa {@code 800}, perfil {@code 02}, como o exemplo oficial),
     * com o padrão {@code *} (os nomes {@code BMEPS_out…} não têm extensão).
     */
    protected FileSourceDescriptor bmepsSource(String dir, boolean move) {
        return source(dir, move, null, SFTP_PASSWORD, BMEPS_ENTITY, FileSourceDescriptor.FORMAT_BMEPS, "800", "02", "*");
    }

    protected FileSourceDescriptor source(String dir, boolean move, String fingerprint, String password, String entity,
                                          String format, String bmepsCompanyId, String bmepsProfileId,
                                          String filePattern) {
        UUID sourceId = UUID.randomUUID();
        UUID insurerId = UUID.randomUUID();
        mkdirs(dir + "/inbound", dir + "/processed", dir + "/error");
        String path = "secret/insurers/" + insurerId + "/payments/BANK_REFERENCE/sftp.password";
        secrets.put(path, password);
        return new FileSourceDescriptor(sourceId, insurerId, 1, true, entity, format, bmepsCompanyId, bmepsProfileId,
                new FileSourceDescriptor.Sftp(SFTP.getHost(), SFTP.getMappedPort(22), SFTP_USER, "PASSWORD",
                        dir + "/inbound", move, move ? dir + "/processed" : null, move ? dir + "/error" : null,
                        filePattern, fingerprint),
                Map.of("sftp.password", path));
    }

    // --- SFTP ---

    protected static void mkdirs(String... dirs) {
        for (String dir : dirs) {
            exec("sh", "-c", "mkdir -p /home/demo/" + dir + " && chown 1001 /home/demo/" + dir);
        }
    }

    /** Põe um ficheiro na pasta indicada (relativa à raiz do utilizador). */
    protected static void put(String path, String content) {
        put(path, content.getBytes(StandardCharsets.ISO_8859_1));
    }

    /** Põe um ficheiro tal como está (byte a byte). */
    protected static void put(String path, byte[] content) {
        SFTP.copyFileToContainer(Transferable.of(content, 0644), "/home/demo/" + path);
    }

    protected static List<String> ls(String dir) {
        String out = exec("sh", "-c", "ls -1 /home/demo/" + dir + " 2>/dev/null || true");
        List<String> names = new ArrayList<>();
        for (String line : out.split("\n")) {
            if (!line.isBlank()) {
                names.add(line.trim());
            }
        }
        return names;
    }

    protected static String exec(String... command) {
        try {
            Container.ExecResult result = SFTP.execInContainer(command);
            if (result.getExitCode() != 0) {
                throw new IllegalStateException("exec failed: " + String.join(" ", command) + ": " + result.getStderr());
            }
            return result.getStdout();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    /** Downloads (aberturas para leitura) do ficheiro registados pelo servidor SFTP ({@code /tmp/sftp.log}). */
    protected static int serverDownloads(String path) {
        String logs = exec("sh", "-c", "cat /tmp/sftp.log 2>/dev/null || true");
        Matcher m = Pattern.compile("open \"[^\"]*/" + Pattern.quote(path) + "\" flags READ").matcher(logs);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count;
    }

    // --- ficheiros MEPS ---

    /** MEPS ({@code docs/specs/interbancos/README.md} §2) da entidade {@link #ENTITY}, com o rodapé certo. */
    protected static String mepsFile(String a005, String previous, long... amounts) {
        return mepsFile(ENTITY, a005, previous, amounts.length, sum(amounts), amounts);
    }

    /**
     * MEPS com o cabeçalho, os detalhes e o rodapé indicados: A005 {@code a005} (9 dígitos), A007 {@code previous}
     * (zeros se {@code null}), referências {@code 00548781108}, {@code 00548791108}, … pagas a 18/11/2026 às 10:30,
     * 10:31, … em Maputo, tarifa 10,00 cada; o rodapé declara {@code count} registos e o total {@code total}.
     */
    protected static String mepsFile(String entity, String a005, String previous, int count, long total,
                                     long... amounts) {
        StringBuilder sb = new StringBuilder();
        sb.append(pad("0MEPS00000008000" + entity + a005 + (previous == null ? "000000000" : previous) + entity
                + "943XX0000")).append(CRLF);
        for (int i = 0; i < amounts.length; i++) {
            sb.append(pad("2040000000000002026111810" + String.format("%02d", 30 + i) + String.format("%013d", amounts[i])
                    + "000001000" + "  " + "0".repeat(15) + " ".repeat(15) + String.format("%07d", 54878 + i) + "1108"
                    + "00")).append(CRLF);
        }
        sb.append(pad("9" + String.format("%08d", count) + String.format("%017d", total)
                + String.format("%012d", 1000L * amounts.length) + "00000000000 ")).append(CRLF);
        return sb.toString();
    }

    private static String pad(String line) {
        if (line.length() > 107) {
            throw new IllegalArgumentException("MEPS line longer than 107");
        }
        return line + " ".repeat(107 - line.length());
    }

    private static long sum(long... amounts) {
        long s = 0;
        for (long a : amounts) {
            s += a;
        }
        return s;
    }

    // --- mensagens ---

    /** Mensagem recebida na fila de teste: o tipo (propriedade AMQP {@code type}), as propriedades e o JSON. */
    public record Received(String type, String routingKey, String messageId, Object schemaVersion, JsonNode body) {
    }

    /** Todas as mensagens que chegaram à fila de teste (espera um pouco pela última). */
    protected List<Received> drain() {
        List<Received> out = new ArrayList<>();
        Message m;
        while ((m = rabbit.receive(TEST_QUEUE, 1500)) != null) {
            try {
                out.add(new Received(m.getMessageProperties().getType(), m.getMessageProperties().getReceivedRoutingKey(),
                        m.getMessageProperties().getMessageId(),
                        m.getMessageProperties().getHeaders().get("schemaVersion"), json.readTree(m.getBody())));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return out;
    }

    protected static List<Received> ofType(List<Received> messages, String type) {
        return messages.stream().filter(r -> type.equals(r.type())).toList();
    }

    protected String fileStatus(UUID sourceId, String sha256) {
        return jdbc.queryForObject("select status from payment_worker.processed_file where source_id = ? and sha256 = ?",
                String.class, sourceId, sha256);
    }
}
