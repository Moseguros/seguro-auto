package mz.co.moseguros.rating.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Assinatura {@code v1=} do contrato {@code moseguros-rating-1} (S3-02 §8). O valor esperado foi
 * calculado fora da aplicação ({@code printf '%s' '1767225600.{"requestId":"abc"}' | openssl dgst
 * -sha256 -hmac 'segredo-de-teste'}).
 */
class RequestSignerTest {

    static final byte[] BODY = "{\"requestId\":\"abc\"}".getBytes(StandardCharsets.UTF_8);

    @Test
    void knownSignatureForAFixedBodySecretAndTimestamp() {
        assertThat(RequestSigner.sign("segredo-de-teste", 1_767_225_600L, BODY))
                .isEqualTo("v1=3720d0ecd812a695ea5da60b5a85cb2833d280c6f693f229ea853f842982fd87");
    }

    @Test
    void changesWithTheBodyTheTimestampAndTheSecret() {
        String base = RequestSigner.sign("segredo-de-teste", 1_767_225_600L, BODY);
        assertThat(RequestSigner.sign("segredo-de-teste", 1_767_225_601L, BODY)).isNotEqualTo(base);
        assertThat(RequestSigner.sign("outro-segredo", 1_767_225_600L, BODY)).isNotEqualTo(base);
        assertThat(RequestSigner.sign("segredo-de-teste", 1_767_225_600L,
                "{\"requestId\":\"abd\"}".getBytes(StandardCharsets.UTF_8))).isNotEqualTo(base);
        assertThat(base).matches("^v1=[0-9a-f]{64}$");
    }

    @Test
    void usesTheExactUtf8Bytes() {
        byte[] accented = "{\"make\":\"Citroën\"}".getBytes(StandardCharsets.UTF_8);
        assertThat(RequestSigner.sign("s", 1L, accented))
                .isNotEqualTo(RequestSigner.sign("s", 1L, "{\"make\":\"Citroen\"}".getBytes(StandardCharsets.UTF_8)));
    }
}
