package mz.co.moseguros.payment.infrastructure.mpesa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;

import mz.co.moseguros.payment.infrastructure.mpesa.MpesaTokenFactory.InvalidPublicKeyException;
import org.junit.jupiter.api.Test;

/**
 * Token M-Pesa (S5-04 §1): decifrado com a chave privada do simulador dá a {@code apiKey}; cada token é
 * diferente (PKCS#1 v1.5); a chave pública lê-se em PEM e em Base64 DER.
 */
class MpesaTokenFactoryTest {

    static final Path KEYS = Path.of("../infra/mocks/mpesa-mock/src/main/resources/keys").toAbsolutePath()
            .normalize();
    static final String API_KEY = "dev-mpesa-api-key";

    final MpesaTokenFactory factory = new MpesaTokenFactory();

    static String pem() throws IOException {
        try (InputStream in = MpesaTokenFactoryTest.class.getResourceAsStream("/dev/mpesa-mock-public.pem")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static String base64Der() throws IOException {
        return Files.readString(KEYS.resolve("mock-public.b64"), StandardCharsets.UTF_8).trim();
    }

    static PrivateKey mockPrivateKey() throws Exception {
        String pem = Files.readString(KEYS.resolve("mock-private.pem"), StandardCharsets.UTF_8);
        String body = pem.substring(pem.indexOf("-----BEGIN PRIVATE KEY-----") + 27,
                pem.indexOf("-----END PRIVATE KEY-----")).replaceAll("\\s", "");
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder()
                .decode(body)));
    }

    static String decrypt(String token) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, mockPrivateKey());
        return new String(cipher.doFinal(Base64.getDecoder().decode(token)), StandardCharsets.UTF_8);
    }

    @Test
    void tokenDecryptedWithTheMockPrivateKeyIsTheApiKey() throws Exception {
        PublicKey key = MpesaTokenFactory.parse(pem());
        String token = factory.token(key, API_KEY);

        assertThat(token).doesNotContain(API_KEY);
        assertThat(decrypt(token)).isEqualTo(API_KEY);
    }

    @Test
    void twoTokensInARowAreDifferent() throws Exception {
        PublicKey key = MpesaTokenFactory.parse(pem());
        String first = factory.token(key, API_KEY);
        String second = factory.token(key, API_KEY);

        assertThat(first).isNotEqualTo(second);
        assertThat(decrypt(first)).isEqualTo(API_KEY);
        assertThat(decrypt(second)).isEqualTo(API_KEY);
    }

    @Test
    void acceptsPemAndBase64Der() throws Exception {
        PublicKey fromPem = MpesaTokenFactory.parse(pem());
        PublicKey fromDer = MpesaTokenFactory.parse(base64Der());
        // também com quebras de linha no Base64
        PublicKey wrapped = MpesaTokenFactory.parse(base64Der().replaceAll("(.{64})", "$1\n"));

        assertThat(fromDer.getEncoded()).isEqualTo(fromPem.getEncoded());
        assertThat(wrapped.getEncoded()).isEqualTo(fromPem.getEncoded());
        assertThat(decrypt(factory.token(fromDer, API_KEY))).isEqualTo(API_KEY);
    }

    @Test
    void rejectsWhatIsNotAPublicKey() {
        assertThatThrownBy(() -> MpesaTokenFactory.parse("nao-e-uma-chave")).isInstanceOf(
                InvalidPublicKeyException.class);
        assertThatThrownBy(() -> MpesaTokenFactory.parse("")).isInstanceOf(InvalidPublicKeyException.class);
        assertThatThrownBy(() -> MpesaTokenFactory.parse("-----BEGIN PUBLIC KEY-----\nAAAA\n"))
                .isInstanceOf(InvalidPublicKeyException.class);
        assertThatThrownBy(() -> MpesaTokenFactory.parse(Base64.getEncoder().encodeToString(new byte[64])))
                .isInstanceOf(InvalidPublicKeyException.class);
    }

    @Test
    void publicKeyIsCachedByConfigurationVersion() throws Exception {
        UUID config = UUID.randomUUID();
        PublicKey first = factory.publicKey(config, 1, pem());
        assertThat(factory.publicKey(config, 1, pem())).isSameAs(first);
        assertThat(factory.publicKey(config, 2, base64Der())).isNotSameAs(first);
    }
}
