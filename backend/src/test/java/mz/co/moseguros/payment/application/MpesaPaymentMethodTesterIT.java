package mz.co.moseguros.payment.application;

import static mz.co.moseguros.payment.PaymentTestSupport.test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import java.util.Map;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.MpesaMockContainer;
import mz.co.moseguros.payment.MpesaTestSupport;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste de ligação do M-Pesa pelo ecrã de configuração ({@code POST /insurer/payment-methods/MPESA/test},
 * S5-04 §6) contra o {@code mpesa-mock}: {@code OK} com os dados de desenvolvimento; chave, {@code Origin},
 * código de serviço e anfitrião errados dão a mensagem certa; um {@code OK} limpa o último erro.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MpesaPaymentMethodTesterIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    PaymentFlowSupport support;
    Seg insurer;

    @BeforeAll
    void setUpAll() {
        RestAssured.port = port;
        MpesaMockContainer.start();
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        insurer = support.insurer("Seguradora Teste Ligacao");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    void expect(String result, String message) {
        var response = test(insurer.admin(), "MPESA").then().statusCode(200).body("result", equalTo(result));
        if (message == null) {
            response.body("message", nullValue());
        } else {
            response.body("message", equalTo(message));
        }
    }

    @Test
    void developmentDataPassAndWrongDataGiveTheRightMessage() {
        MpesaTestSupport.configure(insurer, MpesaMockContainer.settings(), MpesaMockContainer.secrets());
        expect("OK", null);

        Map<String, String> wrongKey = MpesaMockContainer.secrets();
        wrongKey.put("apiKey", "chave-errada");
        MpesaTestSupport.configure(insurer, MpesaMockContainer.settings(), wrongKey);
        expect("FAILED", MpesaPaymentMethodTester.API_KEY_REJECTED);

        Map<String, Object> wrongOrigin = MpesaMockContainer.settings();
        wrongOrigin.put("origin", "outra.origem.co.mz");
        MpesaTestSupport.configure(insurer, wrongOrigin, MpesaMockContainer.secrets());
        expect("FAILED", MpesaPaymentMethodTester.ORIGIN_REJECTED);

        Map<String, Object> wrongCode = MpesaMockContainer.settings();
        wrongCode.put("serviceProviderCode", "171718");
        MpesaTestSupport.configure(insurer, wrongCode, MpesaMockContainer.secrets());
        expect("FAILED", MpesaPaymentMethodTester.SERVICE_PROVIDER_CODE_REJECTED);

        Map<String, Object> wrongHost = MpesaMockContainer.settings();
        wrongHost.put("host", "http://anfitriao-inexistente.invalid");
        MpesaTestSupport.configure(insurer, wrongHost, MpesaMockContainer.secrets());
        expect("FAILED", MpesaPaymentMethodTester.HOST_UNREACHABLE);

        Map<String, Object> closedPort = MpesaMockContainer.settings();
        @SuppressWarnings("unchecked")
        Map<String, Object> ports = (Map<String, Object>) closedPort.get("ports");
        ports.put("query", 1);
        MpesaTestSupport.configure(insurer, closedPort, MpesaMockContainer.secrets());
        expect("FAILED", MpesaPaymentMethodTester.HOST_UNREACHABLE);

        // um erro de configuração gravado por um pagamento é limpo pelo teste OK seguinte
        support.owner().update("update payment.insurer_payment_method set last_error_code = 'PROVIDER_CONFIG_ERROR', "
                + "last_error_at = now() where insurer_id = ? and method = 'MPESA'", insurer.id());
        MpesaTestSupport.configure(insurer, MpesaMockContainer.settings(), MpesaMockContainer.secrets());
        expect("OK", null);
        assertThat(MpesaTestSupport.lastErrorCode(support.owner(), insurer.id())).isNull();
    }
}
