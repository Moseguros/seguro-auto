package mz.co.moseguros.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Cabeçalhos de segurança da API (S6-08 §6; ASVS V14.4.3, V14.4.4, V8.2.1): {@code X-Content-Type-Options:
 * nosniff}, {@code X-Frame-Options: DENY} e {@code Cache-Control} com {@code no-store} em {@code /me/**},
 * {@code /public/**}, {@code /files/**} e {@code /auth/**}, nas respostas de sucesso e de erro; os erros são
 * {@code application/problem+json} sem <i>stack traces</i> nem nomes de classes (V7.4.1, V14.3.2). Mesma
 * configuração do {@code PrometheusEndpointIT} (um só contexto Spring).
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureObservability(tracing = false)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class SecurityHeadersIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    record Call(String name, MockHttpServletRequestBuilder request) {
    }

    String customerToken() {
        return identityApi.issueTokens(fixtures.customer("Cliente Cabeçalhos"), null).accessToken();
    }

    @Test
    void sensitiveAreasHaveTheSecurityHeaders() throws Exception {
        String token = customerToken();
        List<Call> calls = List.of(
                new Call("me sem token", get("/api/v1/me/profile")),
                new Call("me com token", get("/api/v1/me/policies").header("Authorization", "Bearer " + token)),
                new Call("public config", get("/api/v1/public/verifications/config")),
                new Call("public token", get("/api/v1/public/verifications/{token}", "AAAAAAAAAAAAAAAAAAAAAA")),
                new Call("files", get("/api/v1/files/{id}", UUID.randomUUID())),
                new Call("auth options", get("/api/v1/auth/otp/options")),
                new Call("auth refresh", post("/api/v1/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"invalido\"}")));
        for (Call call : calls) {
            MockHttpServletResponse response = mvc.perform(call.request()).andReturn().getResponse();
            String what = call.name() + " (" + response.getStatus() + ")";
            assertThat(response.getHeader("X-Content-Type-Options")).as(what).isEqualTo("nosniff");
            assertThat(response.getHeader("X-Frame-Options")).as(what).isEqualTo("DENY");
            assertThat(String.join(",", response.getHeaders("Cache-Control"))).as(what).contains("no-store");
        }
    }

    @Test
    void errorsAreProblemJsonWithoutStackTraces() throws Exception {
        String token = customerToken();
        List<Call> calls = List.of(
                new Call("JSON inválido", post("/api/v1/auth/otp/request").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":")),
                new Call("UUID inválido", get("/api/v1/me/policies/{id}", "nao-e-uuid")
                        .header("Authorization", "Bearer " + token)),
                new Call("método não suportado", delete("/api/v1/meta/version")),
                new Call("sem token", get("/api/v1/insurer/policies")),
                new Call("token inválido", get("/api/v1/me/profile").header("Authorization", "Bearer x.y.z")),
                new Call("sem permissão", get("/api/v1/insurer/policies").header("Authorization", "Bearer " + token)));
        for (Call call : calls) {
            MockHttpServletResponse response = mvc.perform(call.request()).andReturn().getResponse();
            String body = response.getContentAsString();
            String what = call.name() + " (" + response.getStatus() + "): " + body;
            assertThat(response.getStatus()).as(what).isBetween(400, 499);
            assertThat(response.getContentType()).as(what).startsWith("application/problem+json");
            for (String leak : Arrays.asList("\tat ", "Exception", "java.", "mz.co.moseguros", "org.springframework",
                    "\"trace\"")) {
                assertThat(body).as(what).doesNotContain(leak);
            }
        }
    }
}
