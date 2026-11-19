package mz.co.moseguros.paymentworker.sources;

import mz.co.moseguros.paymentcontracts.FileSourceList;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Fontes activas do backend (S5-09 §4): {@code GET /internal/v1/payment-file-sources} com o token de
 * serviço ({@link ServiceTokenProvider}); <i>timeout</i> de 10 s ({@code backendRestClient}).
 */
@Component
public class BackendSourceClient {

    static final String SOURCES_PATH = "/internal/v1/payment-file-sources";

    private final RestClient rest;
    private final ServiceTokenProvider tokens;
    private final String url;

    public BackendSourceClient(RestClient backendRestClient, ServiceTokenProvider tokens, WorkerProperties properties) {
        this.rest = backendRestClient;
        this.tokens = tokens;
        this.url = properties.backendBaseUrl() + SOURCES_PATH;
    }

    /** As fontes; qualquer falha (backend em baixo, 5xx, resposta inválida) é uma excepção. */
    public FileSourceList fetch() {
        FileSourceList list = tokens.withToken(token -> rest.get()
                .uri(url)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .body(FileSourceList.class));
        return list == null ? new FileSourceList(null) : list;
    }
}
