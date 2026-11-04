package mz.co.moseguros.rating.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import mz.co.moseguros.rating.application.RatingProperties;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP do contrato {@code moseguros-rating-1} (S3-02 §8): {@code RestClient} sobre o
 * {@code HttpClient} do JDK ({@link JdkClientHttpRequestFactory}), com os tempos de ligação e de
 * leitura da configuração (lidos em cada chamada) e a resposta limitada a
 * {@code max-response-bytes}. Não segue redirecções. Não regista o corpo nem os cabeçalhos.
 */
@Component
public class ExternalRatingClient {

    private final RatingProperties properties;
    private volatile CachedClient cached;

    public ExternalRatingClient(RatingProperties properties) {
        this.properties = properties;
    }

    /** Estado HTTP e corpo (no máximo {@code max-response-bytes}). */
    public record HttpResult(int status, byte[] body) {
    }

    /** Porque a chamada não teve resposta. */
    public enum FailureKind {
        /** Tempo de ligação ou de leitura esgotado. */
        TIMEOUT,
        /** Erro de rede (ligação recusada, interrompida…). */
        NETWORK,
        /** Resposta acima de {@code max-response-bytes}. */
        TOO_LARGE
    }

    /** A chamada falhou sem resposta utilizável. */
    public static class CallFailedException extends RuntimeException {

        private final FailureKind kind;

        public CallFailedException(FailureKind kind, Throwable cause) {
            super(kind.name(), cause);
            this.kind = kind;
        }

        public FailureKind kind() {
            return kind;
        }
    }

    /**
     * {@code POST} do corpo exacto ({@code application/json}) com os cabeçalhos indicados.
     *
     * @throws CallFailedException tempo esgotado, erro de rede ou resposta grande de mais
     */
    public HttpResult post(URI url, Map<String, String> headers, byte[] body) {
        RatingProperties.External external = properties.getExternal();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient(external.getConnectTimeout()));
        factory.setReadTimeout(external.getReadTimeout());
        int max = external.getMaxResponseBytes();
        try {
            return RestClient.builder().requestFactory(factory).build()
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(h -> headers.forEach(h::set))
                    .body(body)
                    .exchange((request, response) ->
                            new HttpResult(response.getStatusCode().value(), read(response.getBody(), max)), true);
        } catch (CallFailedException e) {
            throw e;
        } catch (RestClientException e) {
            throw new CallFailedException(isTimeout(e) ? FailureKind.TIMEOUT : FailureKind.NETWORK, e);
        }
    }

    /** Lê até {@code max} bytes; mais do que isso → {@link FailureKind#TOO_LARGE}. */
    static byte[] read(InputStream in, int max) throws IOException {
        if (in == null) {
            return new byte[0];
        }
        byte[] bytes = in.readNBytes(max + 1);
        if (bytes.length > max) {
            throw new CallFailedException(FailureKind.TOO_LARGE, null);
        }
        return bytes;
    }

    static boolean isTimeout(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause() == t ? null : t.getCause()) {
            // O JdkClientHttpRequest espera a resposta com Future.get(readTimeout): TimeoutException.
            if (t instanceof HttpTimeoutException || t instanceof SocketTimeoutException
                    || t instanceof TimeoutException) {
                return true;
            }
        }
        return false;
    }

    private HttpClient httpClient(Duration connectTimeout) {
        CachedClient current = cached;
        if (current == null || !current.connectTimeout().equals(connectTimeout)) {
            current = new CachedClient(connectTimeout, HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .connectTimeout(connectTimeout)
                    .build());
            cached = current;
        }
        return current.client();
    }

    private record CachedClient(Duration connectTimeout, HttpClient client) {
    }
}
