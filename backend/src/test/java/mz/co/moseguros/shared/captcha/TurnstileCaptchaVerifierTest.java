package mz.co.moseguros.shared.captcha;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import com.github.tomakehurst.wiremock.WireMockServer;
import mz.co.moseguros.shared.secrets.InMemorySecretStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

/** {@link TurnstileCaptchaVerifier} contra um {@code siteverify} falso (WireMock) e o {@link CaptchaConfig}. */
class TurnstileCaptchaVerifierTest {

    static final String PATH = "/turnstile/v0/siteverify";
    static final String SECRET_PATH = "secret/platform/captcha/turnstile";

    WireMockServer server;
    InMemorySecretStore secrets;
    TurnstileCaptchaVerifier verifier;

    @BeforeEach
    void setUp() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();
        secrets = new InMemorySecretStore();
        secrets.put(SECRET_PATH, "turnstile-secret");
        verifier = new TurnstileCaptchaVerifier(turnstile(Duration.ofMillis(500)), secrets);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    CaptchaProperties.Turnstile turnstile(Duration timeout) {
        return new CaptchaProperties.Turnstile("site-key-1", SECRET_PATH, List.of("verificar.moseguros.co.mz"),
                URI.create(server.baseUrl() + PATH), timeout);
    }

    @Test
    void successWithAnAllowedHostnameIsValidAndSendsTheForm() {
        server.stubFor(post(urlEqualTo(PATH)).willReturn(okJson(
                "{\"success\":true,\"hostname\":\"verificar.moseguros.co.mz\",\"error-codes\":[]}")));

        assertThat(verifier.verify("token-1", "41.220.15.77")).isEqualTo(CaptchaResult.VALID);
        server.verify(postRequestedFor(urlEqualTo(PATH))
                .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
                .withRequestBody(containing("secret=turnstile-secret"))
                .withRequestBody(containing("response=token-1"))
                .withRequestBody(containing("remoteip=41.220.15.77")));
        assertThat(verifier.provider()).isEqualTo(CaptchaProvider.TURNSTILE);
        assertThat(verifier.siteKey()).contains("site-key-1");
        assertThat(verifier.toString()).doesNotContain("turnstile-secret");
    }

    @Test
    void failureOrAnotherHostnameIsInvalid() {
        server.stubFor(post(urlEqualTo(PATH)).withRequestBody(containing("response=bad"))
                .willReturn(okJson("{\"success\":false,\"error-codes\":[\"invalid-input-response\"]}")));
        server.stubFor(post(urlEqualTo(PATH)).withRequestBody(containing("response=other"))
                .willReturn(okJson("{\"success\":true,\"hostname\":\"evil.example.com\"}")));

        assertThat(verifier.verify("bad", null)).isEqualTo(CaptchaResult.INVALID);
        assertThat(verifier.verify("other", null)).isEqualTo(CaptchaResult.INVALID);
        assertThat(verifier.verify("", null)).isEqualTo(CaptchaResult.INVALID);
        assertThat(verifier.verify("x".repeat(TurnstileCaptchaVerifier.MAX_TOKEN_LENGTH + 1), null))
                .isEqualTo(CaptchaResult.INVALID);
    }

    @Test
    void serverErrorTimeoutAndNetworkErrorAreUnavailable() {
        server.stubFor(post(urlEqualTo(PATH)).withRequestBody(containing("response=five"))
                .willReturn(aResponse().withStatus(503)));
        server.stubFor(post(urlEqualTo(PATH)).withRequestBody(containing("response=slow"))
                .willReturn(okJson("{\"success\":true,\"hostname\":\"verificar.moseguros.co.mz\"}")
                        .withFixedDelay(2_000)));
        server.stubFor(post(urlEqualTo(PATH)).withRequestBody(containing("response=garbage"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{\"unexpected\":1}")));

        assertThat(verifier.verify("five", null)).isEqualTo(CaptchaResult.UNAVAILABLE);
        assertThat(verifier.verify("slow", null)).isEqualTo(CaptchaResult.UNAVAILABLE);
        assertThat(verifier.verify("garbage", null)).isEqualTo(CaptchaResult.UNAVAILABLE);

        server.stop();
        assertThat(verifier.verify("network", null)).isEqualTo(CaptchaResult.UNAVAILABLE);
    }

    @Test
    void missingSecretIsUnavailableWithoutCallingTheProvider() {
        secrets.delete(SECRET_PATH);
        assertThat(verifier.verify("token", null)).isEqualTo(CaptchaResult.UNAVAILABLE);
        server.verify(0, postRequestedFor(urlEqualTo(PATH)));
    }

    @Test
    void theCircuitOpensAfterRepeatedFailures() {
        server.stubFor(post(urlEqualTo(PATH)).willReturn(aResponse().withStatus(500)));
        for (int i = 0; i < 10; i++) {
            assertThat(verifier.verify("t" + i, null)).isEqualTo(CaptchaResult.UNAVAILABLE);
        }
        assertThat(verifier.circuitState()).isEqualTo(io.github.resilience4j.circuitbreaker.CircuitBreaker.State.OPEN);
        int calls = server.getAllServeEvents().size();
        server.stubFor(post(urlEqualTo(PATH)).willReturn(okJson(
                "{\"success\":true,\"hostname\":\"verificar.moseguros.co.mz\"}")));
        assertThat(verifier.verify("after", null)).isEqualTo(CaptchaResult.UNAVAILABLE);
        assertThat(server.getAllServeEvents()).hasSize(calls);
    }

    @Test
    void configChoosesTheProviderAndRefusesNoneInProd() {
        CaptchaProperties none = new CaptchaProperties(CaptchaProperties.Provider.NONE, turnstile(Duration.ofSeconds(3)));
        assertThat(CaptchaConfig.create(none, secrets, new MockEnvironment()))
                .isInstanceOf(AlwaysValidCaptchaVerifier.class);
        assertThat(CaptchaConfig.create(none, secrets, env("staging"))).isInstanceOf(AlwaysValidCaptchaVerifier.class);
        assertThatThrownBy(() -> CaptchaConfig.create(none, secrets, env("prod")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("prod");

        CaptchaProperties ts = new CaptchaProperties(CaptchaProperties.Provider.TURNSTILE,
                turnstile(Duration.ofSeconds(3)));
        assertThat(CaptchaConfig.create(ts, secrets, env("prod"))).isInstanceOf(TurnstileCaptchaVerifier.class);

        CaptchaProperties noKey = new CaptchaProperties(CaptchaProperties.Provider.TURNSTILE,
                new CaptchaProperties.Turnstile("", SECRET_PATH, List.of("a"), URI.create("https://x"),
                        Duration.ofSeconds(3)));
        assertThatThrownBy(() -> CaptchaConfig.create(noKey, secrets, env("prod")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("site-key");
        CaptchaProperties noHost = new CaptchaProperties(CaptchaProperties.Provider.TURNSTILE,
                new CaptchaProperties.Turnstile("k", SECRET_PATH, List.of(" "), URI.create("https://x"),
                        Duration.ofSeconds(3)));
        assertThatThrownBy(() -> CaptchaConfig.create(noHost, secrets, env("prod")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("allowed-hostnames");

        AlwaysValidCaptchaVerifier always = new AlwaysValidCaptchaVerifier();
        assertThat(always.verify(null, null)).isEqualTo(CaptchaResult.VALID);
        assertThat(always.siteKey()).isEmpty();
    }

    static MockEnvironment env(String profile) {
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles(profile);
        return env;
    }
}
