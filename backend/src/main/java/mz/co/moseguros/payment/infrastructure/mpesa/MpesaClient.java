package mz.co.moseguros.payment.infrastructure.mpesa;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.nio.channels.UnresolvedAddressException;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Cliente HTTP da API M-Pesa da Vodacom (S5-04 §1): {@code RestClient} sobre o {@code HttpClient} do
 * JDK, ligação em {@code connect-timeout} (5 s), leitura em {@code c2bTimeoutSeconds} no C2B (limitado
 * por {@code timeout-cap} nos testes) e {@code read-timeout} (15 s) na consulta e na reversão. Lê o
 * corpo JSON qualquer que seja o estado HTTP; quem decide é o {@code output_ResponseCode}.
 *
 * <p>Circuit breaker por seguradora ({@code mpesa-<insurerId>}): janela de 20 chamadas, abre com 50 %
 * de falhas técnicas (sem resposta legível), 60 s aberto. Com o circuito aberto o pedido não sai
 * ({@link Failure#CIRCUIT_OPEN}). O teste de ligação ({@link #queryWithoutBreaker}) não passa pelo
 * circuito.
 *
 * <p>Não regista o token, a {@code apiKey}, os corpos nem os cabeçalhos. O único chamador de
 * {@link #c2b} é o {@link MpesaGateway#initiate}, que só o {@code MpesaC2bJob} chama depois do
 * compare-and-set {@code CREATED → PENDING}.
 */
@Component
public class MpesaClient {

    public static final String C2B_PATH = "/ipg/v1x/c2bPayment/singleStage/";
    public static final String QUERY_PATH = "/ipg/v1x/queryTransactionStatus/";
    public static final String REVERSAL_PATH = "/ipg/v1x/reversal/";
    static final String BREAKER_PREFIX = "mpesa-";
    static final int MAX_RESPONSE_BYTES = 64 * 1024;

    private final MpesaProperties properties;
    private final ObjectMapper mapper;
    private final CircuitBreakerRegistry breakers;
    private final HttpClient httpClient;

    public MpesaClient(MpesaProperties properties, ObjectMapper mapper) {
        this.properties = properties;
        this.mapper = mapper;
        this.breakers = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(properties.getCircuitBreakerWindow())
                .minimumNumberOfCalls(properties.getCircuitBreakerWindow())
                .failureRateThreshold(properties.getCircuitBreakerFailureRate())
                .waitDurationInOpenState(properties.getCircuitBreakerOpenFor())
                .recordExceptions(Exception.class)
                .build());
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(properties.getConnectTimeout())
                .build();
    }

    /** Onde e como chamar: anfitrião ({@code settings.host}), porta da operação e {@code Origin}. */
    public record Endpoint(String host, int port, String origin) {

        public Endpoint {
            Objects.requireNonNull(host, "host");
            Objects.requireNonNull(origin, "origin");
        }

        URI uri(String path) {
            return URI.create(host + ":" + port + path);
        }
    }

    /** Porque a chamada não teve resposta legível. */
    public enum Failure {
        /** Circuito aberto: o pedido não saiu. */
        CIRCUIT_OPEN(false),
        /** Ligação recusada ou nome desconhecido. */
        CONNECT_FAILED(true),
        /** Tempo de ligação ou de leitura esgotado. */
        TIMEOUT(true),
        /** Ligação cortada ou outro erro de rede. */
        NETWORK(true),
        /** A resposta chegou mas não é JSON da API (ou é grande de mais). */
        UNREADABLE(true);

        private final boolean attempted;

        Failure(boolean attempted) {
            this.attempted = attempted;
        }

        /** {@code true} se o pedido pode ter chegado ao fornecedor (o resultado não se sabe). */
        public boolean attempted() {
            return attempted;
        }
    }

    /** A chamada não teve resposta legível. */
    public static class MpesaCallException extends RuntimeException {

        private final Failure failure;

        public MpesaCallException(Failure failure, Throwable cause) {
            super(failure.name(), cause);
            this.failure = failure;
        }

        public Failure failure() {
            return failure;
        }
    }

    /**
     * C2B. Só o {@link MpesaGateway#initiate} o chama.
     *
     * @throws MpesaCallException sem resposta legível
     */
    MpesaResponse c2b(UUID insurerId, Endpoint endpoint, String token, MpesaRequests.C2b body,
                      int c2bTimeoutSeconds) {
        Duration readTimeout = properties.c2bReadTimeout(c2bTimeoutSeconds);
        return guarded(insurerId, () -> exchange(HttpMethod.POST, endpoint.uri(C2B_PATH), endpoint, token, body,
                readTimeout));
    }

    /** Consulta de estado. @throws MpesaCallException sem resposta legível */
    public MpesaResponse query(UUID insurerId, Endpoint endpoint, String token, MpesaRequests.Query query) {
        return guarded(insurerId, () -> exchange(HttpMethod.GET, queryUri(endpoint, query), endpoint, token, null,
                properties.getReadTimeout()));
    }

    /** Consulta sem circuit breaker, para o teste de ligação. @throws MpesaCallException sem resposta legível */
    public MpesaResponse queryWithoutBreaker(Endpoint endpoint, String token, MpesaRequests.Query query) {
        return exchange(HttpMethod.GET, queryUri(endpoint, query), endpoint, token, null, properties.getReadTimeout());
    }

    /** Reversão. @throws MpesaCallException sem resposta legível */
    public MpesaResponse reversal(UUID insurerId, Endpoint endpoint, String token, MpesaRequests.Reversal body) {
        return guarded(insurerId, () -> exchange(HttpMethod.PUT, endpoint.uri(REVERSAL_PATH), endpoint, token, body,
                properties.getReadTimeout()));
    }

    /** O circuito da seguradora (para os testes e as métricas). */
    public CircuitBreaker circuitBreaker(UUID insurerId) {
        return breakers.circuitBreaker(BREAKER_PREFIX + insurerId);
    }

    private MpesaResponse guarded(UUID insurerId, java.util.function.Supplier<MpesaResponse> call) {
        CircuitBreaker breaker = circuitBreaker(insurerId);
        if (!breaker.tryAcquirePermission()) {
            throw new MpesaCallException(Failure.CIRCUIT_OPEN, null);
        }
        long start = System.nanoTime();
        try {
            MpesaResponse response = call.get();
            breaker.onSuccess(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            return response;
        } catch (MpesaCallException e) {
            breaker.onError(System.nanoTime() - start, TimeUnit.NANOSECONDS, e);
            throw e;
        } catch (RuntimeException e) {
            breaker.releasePermission();
            throw e;
        }
    }

    private static URI queryUri(Endpoint endpoint, MpesaRequests.Query query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(endpoint.uri(QUERY_PATH));
        query.params().forEach(builder::queryParam);
        return builder.encode().build().toUri();
    }

    private MpesaResponse exchange(HttpMethod method, URI uri, Endpoint endpoint, String token, Object body,
                                   Duration readTimeout) {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(readTimeout);
        RestClient.RequestBodySpec spec = RestClient.builder().requestFactory(factory).build()
                .method(method)
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ORIGIN, endpoint.origin())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        if (body != null) {
            spec = spec.body(body);
        }
        try {
            return spec.exchange((request, response) -> parse(response.getStatusCode().value(), response.getBody()),
                    true);
        } catch (MpesaCallException e) {
            throw e;
        } catch (RestClientException e) {
            throw new MpesaCallException(classify(e), null);
        }
    }

    private MpesaResponse parse(int status, InputStream in) throws IOException {
        byte[] bytes = in == null ? new byte[0] : in.readNBytes(MAX_RESPONSE_BYTES + 1);
        if (bytes.length == 0 || bytes.length > MAX_RESPONSE_BYTES) {
            throw new MpesaCallException(Failure.UNREADABLE, null);
        }
        try {
            MpesaResponse response = mapper.readValue(bytes, MpesaResponse.class);
            if (response == null || response.responseCode() == null || response.responseCode().isBlank()) {
                throw new MpesaCallException(Failure.UNREADABLE, null);
            }
            return response.withHttpStatus(status);
        } catch (IOException e) {
            throw new MpesaCallException(Failure.UNREADABLE, null);
        }
    }

    /** Classifica a excepção do cliente HTTP (a causa não se propaga: pode ter o URL com parâmetros). */
    static Failure classify(Throwable e) {
        Function<Throwable, Failure> kind = t -> {
            if (t instanceof HttpConnectTimeoutException || t instanceof HttpTimeoutException
                    || t instanceof SocketTimeoutException || t instanceof TimeoutException) {
                return Failure.TIMEOUT;
            }
            if (t instanceof ConnectException || t instanceof UnknownHostException
                    || t instanceof UnresolvedAddressException) {
                return Failure.CONNECT_FAILED;
            }
            if (t instanceof MpesaCallException m) {
                return m.failure();
            }
            return null;
        };
        for (Throwable t = e; t != null; t = t.getCause() == t ? null : t.getCause()) {
            Failure f = kind.apply(t);
            if (f != null) {
                return f;
            }
        }
        return Failure.NETWORK;
    }
}
