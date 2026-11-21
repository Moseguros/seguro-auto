package mz.co.moseguros.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * {@code /api/v1/prometheus} (S6-08 §1, §6): responde a {@code 127.0.0.1} e às redes de
 * {@code moseguros.observability.allowed-cidrs}; um endereço remoto fora da lista tem 404, mesmo com
 * {@code X-Forwarded-For} de dentro da lista. Mesma configuração do {@code SecurityHeadersIT} (um só
 * contexto Spring).
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability(tracing = false)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class PrometheusEndpointIT {

    static final String PATH = "/api/v1/prometheus";

    @Autowired
    MockMvc mvc;

    static MockHttpServletRequestBuilder from(String remote) {
        return get(PATH).with(r -> {
            r.setRemoteAddr(remote);
            return r;
        });
    }

    @Test
    void localhostReadsTheMetricsWithoutToken() throws Exception {
        // um pedido antes, para existir o histograma do http.server.requests
        mvc.perform(get("/api/v1/meta/version"));

        MockHttpServletResponse response = mvc.perform(from("127.0.0.1")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentType()).startsWith("text/plain");
        String body = response.getContentAsString();
        assertThat(body).contains("jvm_memory_used_bytes");
        assertThat(body).contains("application=\"moseguros-backend\"");
        assertThat(body).contains("http_server_requests_seconds_bucket");
        assertThat(body).contains("le=\"0.25\"");
    }

    @Test
    void internalNetworksAndIpv6LoopbackAreAllowed() throws Exception {
        assertThat(mvc.perform(from("172.18.0.5")).andReturn().getResponse().getStatus()).isEqualTo(200);
        assertThat(mvc.perform(from("10.1.2.3")).andReturn().getResponse().getStatus()).isEqualTo(200);
        assertThat(mvc.perform(from("0:0:0:0:0:0:0:1")).andReturn().getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    void anInvalidTokenIsIgnored() throws Exception {
        MockHttpServletResponse response = mvc.perform(from("127.0.0.1").header("Authorization", "Bearer nope"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void remoteAddressOutsideTheListGets404() throws Exception {
        MockHttpServletResponse response = mvc.perform(from("203.0.113.7")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getContentType()).isEqualTo("application/problem+json");
        assertThat(response.getContentAsString()).contains("\"code\":\"NOT_FOUND\"").doesNotContain("jvm_");
    }

    @Test
    void forwardedForIsNotTrustedForTheMetrics() throws Exception {
        MockHttpServletResponse response = mvc.perform(from("203.0.113.7").header("X-Forwarded-For", "127.0.0.1"))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(404);

        MockHttpServletResponse v6 = mvc.perform(from("2001:db8::1")).andReturn().getResponse();
        assertThat(v6.getStatus()).isEqualTo(404);
    }
}
