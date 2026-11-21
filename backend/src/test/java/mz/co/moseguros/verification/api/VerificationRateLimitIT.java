package mz.co.moseguros.verification.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.OrganizationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Limites de pedidos da verificação (S6-03 §5) com o nginx simulado: o pedido vem de um proxy de confiança
 * ({@code moseguros.web.trusted-proxies=10.0.0.0/8}, contexto próprio) com {@code X-Forwarded-For}, como o
 * nginx da web; o mesmo cabeçalho de um endereço que não é de confiança é ignorado.
 *
 * <p>A recarga é gradual (60 por minuto = 1 por segundo): quando os pedidos demoram mais de um segundo,
 * entram mais alguns antes do 429; o teste admite esses e confirma o 61.º quando cabem no mesmo segundo.
 */
@SpringBootTest(properties = "moseguros.web.trusted-proxies=10.0.0.0/8")
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
class VerificationRateLimitIT {

    static final String PUBLIC = "/api/v1/public/verifications";
    static final String POLICE = "/api/v1/police/verifications";
    static final String PROXY = "10.0.0.2";

    @Autowired
    MockMvc mvc;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    ObjectMapper mapper;

    static MockHttpServletRequestBuilder from(MockHttpServletRequestBuilder request, String remote, String forwarded) {
        request.with(r -> {
            r.setRemoteAddr(remote);
            return r;
        });
        return forwarded == null ? request : request.header("X-Forwarded-For", forwarded);
    }

    MockHttpServletResponse publicCheck(String remote, String forwarded) throws Exception {
        return mvc.perform(from(get(PUBLIC + "/{token}", "x" + UUID.randomUUID()), remote, forwarded))
                .andReturn().getResponse();
    }

    /** Pedidos até ao primeiro 429; devolve quantos passaram e a resposta recusada. */
    record Exhausted(int accepted, long elapsedMillis, MockHttpServletResponse refused) {
    }

    interface Call {
        MockHttpServletResponse run() throws Exception;
    }

    static Exhausted exhaust(int limit, Call call) throws Exception {
        long start = System.nanoTime();
        int accepted = 0;
        while (accepted < limit * 2) {
            MockHttpServletResponse response = call.run();
            if (response.getStatus() == 429) {
                return new Exhausted(accepted, (System.nanoTime() - start) / 1_000_000, response);
            }
            assertThat(response.getStatus()).isEqualTo(200);
            accepted++;
        }
        throw new AssertionError("No 429 after " + accepted + " requests");
    }

    void assertRefused(Exhausted e, int limit, int perMinute) throws Exception {
        long refilled = e.elapsedMillis() * perMinute / 60_000 + 1;
        assertThat(e.accepted()).isBetween(limit, (int) (limit + refilled));
        if (e.elapsedMillis() < 60_000 / perMinute) {
            assertThat(e.accepted()).isEqualTo(limit);
        }
        MockHttpServletResponse refused = e.refused();
        assertThat(refused.getHeader("Retry-After")).isNotNull();
        assertThat(Long.parseLong(refused.getHeader("Retry-After"))).isBetween(1L, 60L);
        JsonNode problem = mapper.readTree(refused.getContentAsString());
        assertThat(problem.path("code").asText()).isEqualTo("RATE_LIMITED");
        assertThat(problem.path("status").asInt()).isEqualTo(429);
        assertThat(problem.path("retryAfterSeconds").asLong()).isPositive();
    }

    long rowsWithPrefix(String prefix) {
        Long n = fixtures.owner().queryForObject("select count(*) from verification.check_log where ip_prefix = ?",
                Long.class, prefix);
        return n == null ? 0 : n;
    }

    @Test
    void publicLimitIsPerClientIpBehindATrustedProxy() throws Exception {
        Exhausted e = exhaust(60, () -> publicCheck(PROXY, "1.2.3.4"));
        assertRefused(e, 60, 60);
        // o pedido recusado não grava registo
        assertThat(rowsWithPrefix("1.2.3.0/24")).isEqualTo(e.accepted());

        // a matrícula conta para o mesmo limite (antes do CAPTCHA)
        assertThat(mvc.perform(from(get(PUBLIC).queryParam("plate", "ABC-123-MC").header("X-Captcha-Token",
                "invalid"), PROXY, "1.2.3.4")).andReturn().getResponse().getStatus()).isEqualTo(429);

        // outro IP atrás do mesmo proxy continua a funcionar
        assertThat(publicCheck(PROXY, "5.6.7.8").getStatus()).isEqualTo(200);
        assertThat(rowsWithPrefix("5.6.7.0/24")).isOne();

        // o mesmo X-Forwarded-For de um endereço que não é de confiança é ignorado: conta para esse endereço
        MockHttpServletResponse spoofed = publicCheck("203.0.113.9", "1.2.3.4");
        assertThat(spoofed.getStatus()).isEqualTo(200);
        assertThat(rowsWithPrefix("203.0.113.0/24")).isOne();
        assertThat(rowsWithPrefix("1.2.3.0/24")).isEqualTo(e.accepted());

        // a configuração não tem limite
        assertThat(mvc.perform(from(get(PUBLIC + "/config"), PROXY, "1.2.3.4")).andReturn().getResponse()
                .getStatus()).isEqualTo(200);
    }

    @Test
    void policeLimitIsPerOfficer() throws Exception {
        UUID police = fixtures.organization(OrganizationType.POLICE, "PRM Limites").getId();
        UserAccount officer = fixtures.staff("Agente Limite", police, "POLICE_OFFICER");
        String token = identityApi.issueTokens(officer, null).accessToken();
        Exhausted e = exhaust(300, () -> mvc.perform(from(get(POLICE + "/{token}", "x" + UUID.randomUUID())
                .header("Authorization", "Bearer " + token), PROXY, "9.9.9.9")).andReturn().getResponse());
        long refusedAt = System.nanoTime();
        assertRefused(e, 300, 300);
        // o histórico conta para o mesmo limite: só passam as fichas repostas desde o 429 (uma a cada 200 ms, mais uma de margem),
        // nunca um balde novo de 300
        Exhausted history = exhaust(300, () -> mvc.perform(from(get("/api/v1/police/check-history")
                .header("Authorization", "Bearer " + token), PROXY, "9.9.9.9")).andReturn().getResponse());
        long elapsedMillis = (System.nanoTime() - refusedAt) / 1_000_000;
        assertThat(history.accepted()).isLessThanOrEqualTo((int) (elapsedMillis * 300 / 60_000 + 2));

        // outro agente (do mesmo IP) continua a funcionar
        String other = identityApi.issueTokens(fixtures.staff("Agente Outro", police, "POLICE_OFFICER"), null)
                .accessToken();
        assertThat(mvc.perform(from(get(POLICE + "/{token}", "x").header("Authorization", "Bearer " + other),
                PROXY, "9.9.9.9")).andReturn().getResponse().getStatus()).isEqualTo(200);
    }

    /** O nginx da web substitui o X-Forwarded-For do cliente (S6-03 §5); senão o limite por IP contornava-se. */
    @Test
    void webNginxReplacesTheForwardedForHeader() throws Exception {
        String nginx = Files.readString(Path.of("..", "web", "nginx.conf"));
        assertThat(nginx).contains("proxy_set_header X-Forwarded-For $remote_addr;")
                .doesNotContain("$proxy_add_x_forwarded_for");
        String compose = Files.readString(Path.of("..", "infra", "docker-compose.yml"));
        assertThat(compose).contains("MOSEGUROS_WEB_TRUSTED_PROXIES:");
    }
}
