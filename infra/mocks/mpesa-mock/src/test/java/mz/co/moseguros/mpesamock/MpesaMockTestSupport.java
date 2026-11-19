package mz.co.moseguros.mpesamock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.crypto.Cipher;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Base dos testes: o simulador arranca nas suas portas reais (portas livres escolhidas uma vez
 * por JVM, para o contexto ser partilhado) e os pedidos levam um token cifrado com
 * {@code keys/mock-public.pem}, como faria o adaptador do backend.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
abstract class MpesaMockTestSupport {

    static final int C2B_PORT = freePort();
    static final int QUERY_PORT = freePort();
    static final int REVERSAL_PORT = freePort();
    static final int MANAGEMENT_PORT = freePort();

    static final String API_KEY = "dev-mpesa-api-key";
    static final String ORIGIN = "developer.mpesa.vm.co.mz";
    static final String SERVICE_PROVIDER_CODE = "171717";

    static final ObjectMapper JSON = new ObjectMapper();
    static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    static final PublicKey MOCK_PUBLIC_KEY = loadPublicKey();
    static final String TOKEN = token(MOCK_PUBLIC_KEY, API_KEY);

    private static int referenceCounter;

    @DynamicPropertySource
    static void ports(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> C2B_PORT);
        registry.add("mock.ports.query", () -> QUERY_PORT);
        registry.add("mock.ports.reversal", () -> REVERSAL_PORT);
        registry.add("management.server.port", () -> MANAGEMENT_PORT);
    }

    @BeforeEach
    void resetMock() {
        Response reset = send(HttpRequest.newBuilder(uri(C2B_PORT, "/__mock/reset"))
                .POST(HttpRequest.BodyPublishers.noBody()));
        if (reset.status() != 204) {
            throw new IllegalStateException("reset falhou: " + reset.status());
        }
        setDelays(0, 0, 0, 0);
    }

    // --- pedidos ---

    static Response c2b(String msisdn, String thirdPartyReference) {
        return c2b(c2bBody(msisdn, thirdPartyReference), TOKEN, ORIGIN);
    }

    static Map<String, String> c2bBody(String msisdn, String thirdPartyReference) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("input_TransactionReference", "T12344C");
        body.put("input_CustomerMSISDN", msisdn);
        body.put("input_Amount", "10");
        body.put("input_ThirdPartyReference", thirdPartyReference);
        body.put("input_ServiceProviderCode", SERVICE_PROVIDER_CODE);
        return body;
    }

    static Response c2b(Map<String, String> body, String token, String origin) {
        return send(withAuth(HttpRequest.newBuilder(uri(C2B_PORT, "/ipg/v1x/c2bPayment/singleStage/")), token, origin)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json(body))));
    }

    static Response query(String queryReference, String thirdPartyReference) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("input_QueryReference", queryReference);
        params.put("input_ServiceProviderCode", SERVICE_PROVIDER_CODE);
        params.put("input_ThirdPartyReference", thirdPartyReference);
        return query(params);
    }

    static Response query(Map<String, String> params) {
        String qs = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return send(withAuth(HttpRequest.newBuilder(uri(QUERY_PORT, "/ipg/v1x/queryTransactionStatus/?" + qs)),
                TOKEN, ORIGIN).GET());
    }

    static Map<String, String> reversalBody(String transactionId) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("input_TransactionID", transactionId);
        body.put("input_SecurityCredential", "dev-security-credential");
        body.put("input_InitiatorIdentifier", "dev-initiator");
        body.put("input_ThirdPartyReference", nextReference());
        body.put("input_ServiceProviderCode", SERVICE_PROVIDER_CODE);
        return body;
    }

    static Response reversal(Map<String, String> body) {
        return send(withAuth(HttpRequest.newBuilder(uri(REVERSAL_PORT, "/ipg/v1x/reversal/")), TOKEN, ORIGIN)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json(body))));
    }

    static void setDelays(int defaultDelay, int delay000, int slowDelay, int failureDelay) {
        String body = String.format(
                "{\"defaultDelaySeconds\":%d,\"delay000Seconds\":%d,\"slowDelaySeconds\":%d,\"failureDelaySeconds\":%d}",
                defaultDelay, delay000, slowDelay, failureDelay);
        Response r = send(HttpRequest.newBuilder(uri(C2B_PORT, "/__mock/config"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body)));
        if (r.status() != 200) {
            throw new IllegalStateException("config falhou: " + r.status());
        }
    }

    static JsonNode transactions() {
        return send(HttpRequest.newBuilder(uri(C2B_PORT, "/__mock/transactions")).GET()).json();
    }

    static synchronized String nextReference() {
        referenceCounter++;
        return "REF" + System.nanoTime() % 1_000_000 + "X" + referenceCounter;
    }

    // --- apoio ---

    static URI uri(int port, String path) {
        return URI.create("http://localhost:" + port + path);
    }

    static HttpRequest.Builder withAuth(HttpRequest.Builder builder, String token, String origin) {
        builder.timeout(Duration.ofSeconds(30));
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        if (origin != null) {
            builder.header("Origin", origin);
        }
        return builder;
    }

    static Response send(HttpRequest.Builder builder) {
        try {
            HttpResponse<String> response = HTTP.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            return new Response(response.statusCode(), response.body());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    static String json(Object value) {
        try {
            return JSON.writeValueAsString(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Token como o da Vodacom: Base64 de RSA/ECB/PKCS1Padding(apiKey) com a chave pública dada. */
    static String token(PublicKey publicKey, String apiKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(apiKey.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static PublicKey loadPublicKey() {
        try (InputStream in = MpesaMockTestSupport.class.getResourceAsStream("/keys/mock-public.pem")) {
            String pem = new String(in.readAllBytes(), StandardCharsets.US_ASCII);
            byte[] der = AuthVerifier.Pem.decode(pem, "PUBLIC KEY");
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static int freePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    record Response(int status, String body) {
        JsonNode json() {
            try {
                return JSON.readTree(body);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        String code() {
            return json().path("output_ResponseCode").asText();
        }

        String field(String name) {
            return json().path(name).asText();
        }
    }
}
