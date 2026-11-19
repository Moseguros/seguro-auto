package mz.co.moseguros.payment.application;

import java.security.PublicKey;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.payment.domain.TestResult;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaClient;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaClient.Endpoint;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaClient.MpesaCallException;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaRequests;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaResponse;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaTokenFactory;
import mz.co.moseguros.payment.infrastructure.mpesa.MpesaTokenFactory.InvalidPublicKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Teste de ligação do M-Pesa (S5-04 §6) com os dados gravados: uma consulta de estado com
 * {@code input_QueryReference = MOSEGUROSTEST} e uma {@code input_ThirdPartyReference} nova, sem circuit
 * breaker. Qualquer resposta M-Pesa legível que não seja de credenciais é {@code OK}: a ligação, o token,
 * o {@code Origin} e o código de serviço foram aceites.
 *
 * <p>Mensagens: {@code SETTINGS_INVALID}, {@code SECRET_MISSING}, {@code PUBLIC_KEY_INVALID},
 * {@code HOST_UNREACHABLE}, {@code TIMEOUT}, {@code API_KEY_REJECTED} ({@code INS-2}),
 * {@code ORIGIN_REJECTED} ({@code INS-26}), {@code SERVICE_PROVIDER_CODE_REJECTED} ({@code INS-13}),
 * {@code PROVIDER_ERROR} (resposta ilegível); {@code OK} sem mensagem. Um {@code OK} limpa o último erro
 * de configuração (na gravação do resultado, {@code InsurerPaymentMethod.recordTest}).
 */
@Component
public class MpesaPaymentMethodTester implements PaymentMethodTester {

    public static final String TEST_QUERY_REFERENCE = "MOSEGUROSTEST";
    public static final String SETTINGS_INVALID = "SETTINGS_INVALID";
    public static final String SECRET_MISSING = "SECRET_MISSING";
    public static final String PUBLIC_KEY_INVALID = "PUBLIC_KEY_INVALID";
    public static final String HOST_UNREACHABLE = "HOST_UNREACHABLE";
    public static final String TIMEOUT = "TIMEOUT";
    public static final String API_KEY_REJECTED = "API_KEY_REJECTED";
    public static final String ORIGIN_REJECTED = "ORIGIN_REJECTED";
    public static final String SERVICE_PROVIDER_CODE_REJECTED = "SERVICE_PROVIDER_CODE_REJECTED";
    public static final String PROVIDER_ERROR = "PROVIDER_ERROR";

    private static final Logger log = LoggerFactory.getLogger(MpesaPaymentMethodTester.class);

    private final MpesaClient client;
    private final MpesaTokenFactory tokens;

    public MpesaPaymentMethodTester(MpesaClient client, MpesaTokenFactory tokens) {
        this.client = client;
        this.tokens = tokens;
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.MPESA;
    }

    @Override
    public TestResult test(UUID insurerId, JsonNode settings, Function<String, Optional<String>> secret) {
        Optional<String> host = text(settings, "host");
        Optional<String> origin = text(settings, "origin");
        Optional<String> serviceProviderCode = text(settings, "serviceProviderCode");
        JsonNode port = settings == null ? null : settings.path("ports").path("query");
        if (host.isEmpty() || origin.isEmpty() || serviceProviderCode.isEmpty() || port == null
                || !port.canConvertToInt()) {
            return TestResult.failed(SETTINGS_INVALID);
        }
        Optional<String> apiKey = secret.apply(SecretField.MPESA_API_KEY.fieldName()).filter(v -> !v.isBlank());
        Optional<String> publicKeyText = secret.apply(SecretField.MPESA_PUBLIC_KEY.fieldName())
                .filter(v -> !v.isBlank());
        if (apiKey.isEmpty() || publicKeyText.isEmpty()) {
            return TestResult.failed(SECRET_MISSING);
        }
        PublicKey publicKey;
        try {
            publicKey = MpesaTokenFactory.parse(publicKeyText.get());
        } catch (InvalidPublicKeyException e) {
            return TestResult.failed(PUBLIC_KEY_INVALID);
        }
        MpesaRequests.Query query = new MpesaRequests.Query(TEST_QUERY_REFERENCE, serviceProviderCode.get(),
                MpesaRequests.newThirdPartyReference());
        MpesaResponse response;
        try {
            response = client.queryWithoutBreaker(new Endpoint(host.get(), port.asInt(), origin.get()),
                    tokens.token(publicKey, apiKey.get()), query);
        } catch (MpesaCallException e) {
            log.info("M-Pesa connection test insurerId={} failure={}", insurerId, e.failure());
            return switch (e.failure()) {
                case TIMEOUT -> TestResult.failed(TIMEOUT);
                case CONNECT_FAILED, NETWORK -> TestResult.failed(HOST_UNREACHABLE);
                case UNREADABLE, CIRCUIT_OPEN -> TestResult.failed(PROVIDER_ERROR);
            };
        } catch (IllegalArgumentException e) {
            // anfitrião que não forma um URL válido
            return TestResult.failed(HOST_UNREACHABLE);
        }
        log.info("M-Pesa connection test insurerId={} code={}", insurerId, response.responseCode());
        return switch (response.responseCode().trim()) {
            case "INS-2" -> TestResult.failed(API_KEY_REJECTED);
            case "INS-26" -> TestResult.failed(ORIGIN_REJECTED);
            case "INS-13" -> TestResult.failed(SERVICE_PROVIDER_CODE_REJECTED);
            default -> TestResult.ok(null);
        };
    }

    private static Optional<String> text(JsonNode settings, String field) {
        if (settings == null) {
            return Optional.empty();
        }
        JsonNode node = settings.path(field);
        return node.isTextual() && !node.asText().isBlank() ? Optional.of(node.asText()) : Optional.empty();
    }
}
