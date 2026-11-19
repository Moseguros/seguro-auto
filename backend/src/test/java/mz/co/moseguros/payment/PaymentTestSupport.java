package mz.co.moseguros.payment;

import static io.restassured.RestAssured.given;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * Apoio aos testes das formas de pagamento (S5-01): corpos dos pedidos, chamadas à API e uma chave
 * pública RSA válida.
 */
public final class PaymentTestSupport {

    public static final String METHODS = "/api/v1/insurer/payment-methods";

    private static final String PUBLIC_KEY = generatePublicKey();

    private PaymentTestSupport() {
    }

    /** Chave pública RSA 2048 em PEM (gerada uma vez por execução). */
    public static String publicKeyPem() {
        return PUBLIC_KEY;
    }

    /** Entidade de 5 dígitos que não colide com as dos exemplos (90002, 90090) nem com outros testes. */
    public static String randomEntity() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(10000, 89999));
    }

    public static Map<String, Object> mpesaSettings() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("serviceProviderCode", "171717");
        s.put("host", "http://mpesa-mock");
        s.put("origin", "developer.mpesa.vm.co.mz");
        return s;
    }

    public static Map<String, Object> bankSettings(String entity, String format, String authType) {
        Map<String, Object> sftp = new LinkedHashMap<>();
        sftp.put("host", "sftp");
        sftp.put("port", 22);
        sftp.put("username", "demo");
        sftp.put("authType", authType);
        sftp.put("inboundDir", "inbound");
        sftp.put("moveAfterProcessing", true);
        sftp.put("processedDir", "processed");
        sftp.put("errorDir", "error");
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("entity", entity);
        s.put("fileFormat", format);
        if ("BMEPS".equals(format)) {
            s.put("bmepsCompanyId", "800");
            s.put("bmepsProfileId", "02");
        }
        s.put("sftp", sftp);
        return s;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> sftp(Map<String, Object> settings) {
        return (Map<String, Object>) settings.get("sftp");
    }

    /** Corpo do {@code PUT} com os campos obrigatórios. */
    public static Map<String, Object> body(Map<String, Object> settings, Map<String, String> secrets, Long version) {
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("environment", "SANDBOX");
        b.put("settings", settings);
        if (secrets != null) {
            b.put("secrets", secrets);
        }
        b.put("intentValidityMinutes", 30);
        b.put("sortOrder", 0);
        if (version != null) {
            b.put("version", version);
        }
        return b;
    }

    public static Response list(String token) {
        return given().auth().oauth2(token).get(METHODS);
    }

    public static Response put(String token, String method, Map<String, Object> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body).put(METHODS + "/{m}", method);
    }

    public static Response action(String token, String method, String action, Long version) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("version", version);
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
                .post(METHODS + "/{m}/" + action, method);
    }

    public static Response test(String token, String method) {
        return given().auth().oauth2(token).post(METHODS + "/{m}/test", method);
    }

    /** {@code version} do método na lista ({@code null} se ainda não configurado). */
    public static Long version(String token, String method) {
        List<Map<String, Object>> methods = list(token).then().statusCode(200).extract().jsonPath().getList("methods");
        for (Map<String, Object> m : methods) {
            if (method.equals(m.get("method"))) {
                Object v = m.get("version");
                return v == null ? null : ((Number) v).longValue();
            }
        }
        throw new IllegalArgumentException(method);
    }

    private static String generatePublicKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            byte[] der = generator.generateKeyPair().getPublic().getEncoded();
            return "-----BEGIN PUBLIC KEY-----\n" + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(der)
                    + "\n-----END PUBLIC KEY-----\n";
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
