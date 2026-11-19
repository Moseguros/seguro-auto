package mz.co.moseguros.paymentworker.sources;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import mz.co.moseguros.paymentcontracts.FileSourceList;
import mz.co.moseguros.paymentworker.config.WorkerProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestClient;

/** Token de serviço (S5-09 §4): reutilizado até perto de expirar; um 401 pede um novo e repete uma vez. */
class ServiceTokenProviderTest {

    static final String TOKEN_PATH = "/internal/v1/auth/token";
    static final String SOURCES_PATH = "/internal/v1/payment-file-sources";

    WireMockServer wiremock;
    MovableClock clock;
    ServiceTokenProvider provider;
    BackendSourceClient client;

    static class MovableClock extends Clock {
        final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-11-18T08:00:00Z"));

        void advance(Duration d) {
            now.updateAndGet(i -> i.plus(d));
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now.get();
        }
    }

    static WorkerProperties properties(String baseUrl) {
        return new WorkerProperties(baseUrl, "payment-file-worker", "segredo-do-teste", Duration.ofMinutes(1),
                Duration.ofSeconds(60), Path.of("target", "work"), DataSize.ofMegabytes(20), 50,
                Duration.ofMinutes(30), Duration.ofSeconds(10), Duration.ofSeconds(10), true);
    }

    @BeforeEach
    void setUp() {
        wiremock = new WireMockServer(wireMockConfig().dynamicPort());
        wiremock.start();
        clock = new MovableClock();
        WorkerProperties props = properties(wiremock.baseUrl());
        RestClient rest = RestClient.create();
        provider = new ServiceTokenProvider(rest, props, clock);
        client = new BackendSourceClient(rest, provider, props);
    }

    @AfterEach
    void tearDown() {
        wiremock.stop();
    }

    void tokenResponses(String... tokens) {
        String state = Scenario.STARTED;
        for (int i = 0; i < tokens.length; i++) {
            String next = "token-" + (i + 1);
            wiremock.stubFor(post(urlEqualTo(TOKEN_PATH)).inScenario("tokens").whenScenarioStateIs(state)
                    .withBasicAuth("payment-file-worker", "segredo-do-teste")
                    .withRequestBody(containing("grant_type=client_credentials"))
                    .willReturn(okJson("{\"access_token\":\"" + tokens[i] + "\",\"token_type\":\"Bearer\","
                            + "\"expires_in\":900,\"scope\":\"payments:file-sources\"}"))
                    .willSetStateTo(next));
            state = next;
        }
    }

    @Test
    void tokenIsReusedUntilCloseToExpiry() {
        tokenResponses("A", "B");
        assertThat(provider.token()).isEqualTo("A");
        clock.advance(Duration.ofSeconds(900 - 61));
        assertThat(provider.token()).isEqualTo("A");
        wiremock.verify(1, postRequestedFor(urlEqualTo(TOKEN_PATH)));

        clock.advance(Duration.ofSeconds(2)); // a menos de 60 s de expirar
        assertThat(provider.token()).isEqualTo("B");
        wiremock.verify(2, postRequestedFor(urlEqualTo(TOKEN_PATH)));
    }

    @Test
    void unauthorizedRequestsANewTokenAndRetriesOnce() {
        tokenResponses("A", "B");
        wiremock.stubFor(get(urlEqualTo(SOURCES_PATH)).withHeader("Authorization", equalTo("Bearer A"))
                .willReturn(aResponse().withStatus(401)));
        wiremock.stubFor(get(urlEqualTo(SOURCES_PATH)).withHeader("Authorization", equalTo("Bearer B"))
                .willReturn(okJson("{\"sources\":[]}")));

        FileSourceList list = client.fetch();

        assertThat(list.sources()).isEmpty();
        wiremock.verify(2, postRequestedFor(urlEqualTo(TOKEN_PATH)));
        wiremock.verify(2, getRequestedFor(urlEqualTo(SOURCES_PATH)));
    }

    @Test
    void secondUnauthorizedIsAnError() {
        tokenResponses("A", "B", "C");
        wiremock.stubFor(get(urlEqualTo(SOURCES_PATH)).willReturn(aResponse().withStatus(401)));

        org.assertj.core.api.Assertions.assertThatThrownBy(client::fetch)
                .isInstanceOf(org.springframework.web.client.HttpClientErrorException.Unauthorized.class);
        wiremock.verify(2, postRequestedFor(urlEqualTo(TOKEN_PATH)));
        wiremock.verify(2, getRequestedFor(urlEqualTo(SOURCES_PATH)));
    }
}
