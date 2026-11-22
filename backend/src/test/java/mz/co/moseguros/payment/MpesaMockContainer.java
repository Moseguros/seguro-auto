package mz.co.moseguros.payment;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

/**
 * O {@code mpesa-mock} (S5-03) para os testes de integração do adaptador M-Pesa (S5-04): imagem construída
 * com {@link ImageFromDockerfile} a partir de {@code infra/mocks/mpesa-mock} (um só contentor por execução,
 * partilhado pelas classes de teste) e os endpoints de apoio ({@code /__mock/config},
 * {@code /__mock/transactions}, {@code /__mock/reset}).
 *
 * <p>O {@code Dockerfile} do simulador usa {@code RUN --mount=type=cache} (BuildKit), que o construtor da
 * API do Docker usado pelo Testcontainers não aceita: o teste usa o mesmo {@code Dockerfile} sem essa
 * opção (só a cache do Maven muda).
 */
public final class MpesaMockContainer {

    public static final String API_KEY = "dev-mpesa-api-key";
    public static final String SERVICE_PROVIDER_CODE = "171717";
    public static final String ORIGIN = "developer.mpesa.vm.co.mz";
    public static final String INITIATOR = "dev-initiator";
    public static final String SECURITY_CREDENTIAL = "dev-security-credential";

    static final Path DIR = Path.of(System.getProperty("mpesa-mock.dir", "../infra/mocks/mpesa-mock"))
            .toAbsolutePath().normalize();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static GenericContainer<?> container;

    private MpesaMockContainer() {
    }

    /** Arranca o simulador (uma vez) com os atrasos curtos da ficha S5-04 e limpa as transacções. */
    public static synchronized void start() {
        if (container == null) {
            GenericContainer<?> c = new GenericContainer<>(image())
                    .withExposedPorts(18352, 18353, 18354, 18350)
                    .waitingFor(Wait.forHttp("/actuator/health").forPort(18350).forStatusCode(200))
                    .withStartupTimeout(Duration.ofMinutes(5));
            c.start();
            container = c;
        }
        shortDelays();
        reset();
    }

    private static ImageFromDockerfile image() {
        try {
            String dockerfile = Files.readString(DIR.resolve("Dockerfile"), StandardCharsets.UTF_8)
                    .replaceAll("--mount=type=cache,target=/root/\\.m2(,sharing=locked)? ", "");
            return new ImageFromDockerfile("moseguros/mpesa-mock-it", false)
                    .withFileFromString("Dockerfile", dockerfile)
                    .withFileFromPath("pom.xml", DIR.resolve("pom.xml"))
                    .withFileFromPath("mvnw", DIR.resolve("mvnw"))
                    .withFileFromPath(".mvn", DIR.resolve(".mvn"))
                    .withFileFromPath("src", DIR.resolve("src"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** {@code defaultDelaySeconds 0}, {@code delay000Seconds 1}, {@code slowDelaySeconds 6}, falhas sem espera. */
    public static void shortDelays() {
        Map<String, Integer> config = new LinkedHashMap<>();
        config.put("defaultDelaySeconds", 0);
        config.put("delay000Seconds", 1);
        config.put("slowDelaySeconds", 6);
        config.put("failureDelaySeconds", 0);
        send("PUT", "/__mock/config", config);
    }

    public static void reset() {
        send("POST", "/__mock/reset", null);
    }

    /** Transacções registadas (mais recente primeiro). */
    public static List<Map<String, Object>> transactions() {
        try {
            return MAPPER.readValue(send("GET", "/__mock/transactions", null), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** C2B recebidos com esta {@code input_ThirdPartyReference}. */
    public static List<Map<String, Object>> c2bWith(String thirdPartyReference) {
        return transactions().stream()
                .filter(t -> "C2B".equals(t.get("operation")))
                .filter(t -> thirdPartyReference.equals(t.get("thirdPartyReference")))
                .toList();
    }

    public static long c2bCount() {
        return transactions().stream().filter(t -> "C2B".equals(t.get("operation"))).count();
    }

    /** {@code http://<anfitrião>} (sem porta), como o {@code settings.host}. */
    public static String host() {
        return "http://" + container.getHost();
    }

    public static int port(int containerPort) {
        return container.getMappedPort(containerPort);
    }

    /** {@code settings} da configuração M-Pesa que apontam para o simulador. */
    public static Map<String, Object> settings() {
        Map<String, Object> ports = new LinkedHashMap<>();
        ports.put("c2b", port(18352));
        ports.put("query", port(18353));
        ports.put("reversal", port(18354));
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("serviceProviderCode", SERVICE_PROVIDER_CODE);
        s.put("host", host());
        s.put("ports", ports);
        s.put("origin", ORIGIN);
        s.put("initiatorIdentifier", INITIATOR);
        s.put("c2bTimeoutSeconds", 30);
        return s;
    }

    /** Chave pública do simulador (PEM, cópia do backend). */
    public static String publicKey() {
        try (var in = MpesaMockContainer.class.getResourceAsStream("/dev/mpesa-mock-public.pem")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Segredos de desenvolvimento do simulador. */
    public static Map<String, String> secrets() {
        Map<String, String> s = new LinkedHashMap<>();
        s.put("apiKey", API_KEY);
        s.put("publicKey", publicKey());
        s.put("securityCredential", SECURITY_CREDENTIAL);
        return s;
    }

    private static String send(String method, String path, Object body) {
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(host() + ":" + port(18352) + path))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json");
            request = body == null ? request.method(method, HttpRequest.BodyPublishers.noBody())
                    : request.method(method, HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(body)));
            HttpResponse<String> response = HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IllegalStateException("mpesa-mock " + method + " " + path + " → " + response.statusCode());
            }
            return response.body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
