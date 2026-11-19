package mz.co.moseguros.mpesamock;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpRequest;
import org.junit.jupiter.api.Test;

class PortGuardFilterTest extends MpesaMockTestSupport {

    @Test
    void c2bOnTheQueryPortIsNotFound() {
        Response response = send(withAuth(HttpRequest.newBuilder(uri(QUERY_PORT, "/ipg/v1x/c2bPayment/singleStage/")),
                TOKEN, ORIGIN).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json(c2bBody("258841234567", nextReference())))));

        assertThat(response.status()).isEqualTo(404);
        assertThat(transactions()).isEmpty();
    }

    @Test
    void queryOnTheC2bPortIsNotFound() {
        Response response = send(withAuth(HttpRequest.newBuilder(uri(C2B_PORT,
                "/ipg/v1x/queryTransactionStatus/?input_QueryReference=A&input_ServiceProviderCode=171717"
                        + "&input_ThirdPartyReference=B")), TOKEN, ORIGIN).GET());

        assertThat(response.status()).isEqualTo(404);
    }

    @Test
    void reversalOnTheQueryPortIsNotFound() {
        Response response = send(withAuth(HttpRequest.newBuilder(uri(QUERY_PORT, "/ipg/v1x/reversal/")), TOKEN, ORIGIN)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json(reversalBody("ZZZZZZZZZZ")))));

        assertThat(response.status()).isEqualTo(404);
    }

    @Test
    void supportEndpointsOnlyOnTheMainPort() {
        assertThat(send(HttpRequest.newBuilder(uri(REVERSAL_PORT, "/__mock/transactions")).GET()).status())
                .isEqualTo(404);
        assertThat(send(HttpRequest.newBuilder(uri(QUERY_PORT, "/")).GET()).status()).isEqualTo(404);
    }

    @Test
    void pageIsServedOnTheMainPort() {
        Response page = send(HttpRequest.newBuilder(uri(C2B_PORT, "/")).GET());

        assertThat(page.status()).isEqualTo(200);
        assertThat(page.body()).contains("Transacções M-Pesa simuladas").contains("__mock/transactions");
    }

    @Test
    void healthIsOnTheManagementPort() {
        Response health = send(HttpRequest.newBuilder(uri(MANAGEMENT_PORT, "/actuator/health")).GET());

        assertThat(health.status()).isEqualTo(200);
        assertThat(health.body()).contains("UP");
    }

    @Test
    void normaliseRemovesTrailingSlashes() {
        assertThat(PortGuardFilter.normalise("/ipg/v1x/reversal/")).isEqualTo("/ipg/v1x/reversal");
        assertThat(PortGuardFilter.normalise("/")).isEqualTo("/");
    }
}
