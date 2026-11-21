package mz.co.moseguros.policy.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.policy.PaymentPlan;
import mz.co.moseguros.policy.PolicyCoverageView;
import mz.co.moseguros.policy.application.PolicyPdfModelFactory.Header;
import mz.co.moseguros.policy.application.PolicyPdfModelFactory.Logo;
import mz.co.moseguros.policy.domain.Certificate;
import mz.co.moseguros.policy.domain.Instalment;
import mz.co.moseguros.policy.domain.InstalmentSchedule;
import mz.co.moseguros.policy.domain.Policy;
import mz.co.moseguros.shared.domain.Money;
import org.junit.jupiter.api.Test;

/**
 * Modelos dos PDF da apólice (S6-02 §2, §3 e critérios): sem documento, NUIT, telefone nem morada; do
 * condutor só o número da carta mascarado; o código de verificação só dentro do URL do QR.
 */
class PolicyPdfModelFactoryTest {

    static final Instant START = Instant.parse("2027-02-10T08:31:00Z");
    static final String TOKEN = "Qm9hIHRhcmRlIHNyLgAbCd";
    static final String URL = "https://verificar.moseguros.test/v/" + TOKEN;
    static final String HASH = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
    static final Pattern PERSONAL_KEYS = Pattern.compile("(?i).*(nuit|iddoc|document|msisdn|phone|address|email|"
            + "province|birth|hash).*");

    final ObjectMapper json = new ObjectMapper();
    final PolicyPdfModelFactory factory = new PolicyPdfModelFactory();

    @Test
    void theCertificateModelHasNoPersonalDataBesidesTheName() {
        Policy policy = policy();
        Map<String, Object> model = factory.certificate(policy, certificate(policy), header(), List.of("MZ", "ZA"),
                URL, "data:image/png;base64,AAAA");

        assertThat(keys(model)).noneMatch(k -> PERSONAL_KEYS.matcher(k).matches());
        assertThat(values(model)).doesNotContain("Rua da Paz, 10", "+258841234567", "100200300", "123456789A",
                HASH, "Lic-SECRET-1234");
        assertThat(model).containsEntry("policyholderName", "Ana Maria Mussa")
                .containsEntry("certificateNumber", "CT-2027-0001234")
                .containsEntry("policyNumber", "AP-2027-000123")
                .containsEntry("validFrom", START)
                .containsEntry("coverageType", "MTPL_ONLY")
                .containsEntry("territorialScope", "MZ, ZA")
                .containsEntry("insurerName", "Seguradora Demo");
        @SuppressWarnings("unchecked")
        Map<String, Object> vehicle = (Map<String, Object>) model.get("vehicle");
        assertThat(vehicle).containsOnlyKeys("plate", "make", "model", "year", "chassisNumber", "category", "usage")
                .containsEntry("plate", "ABC-123-MC").containsEntry("year", 2018)
                .containsEntry("category", "LIGHT_PASSENGER");
    }

    @Test
    void theVerificationCodeOnlyAppearsInsideTheQrUrl() {
        Policy policy = policy();
        Map<String, Object> model = factory.certificate(policy, certificate(policy), header(), List.of(), URL,
                "data:image/png;base64,AAAA");

        assertThat(model).containsEntry("verificationUrl", URL).containsEntry("territorialScope", null);
        assertThat(entries(model).stream().filter(e -> e.getValue() instanceof String s && s.contains(TOKEN))
                .map(Map.Entry::getKey)).containsExactly("verificationUrl");
        assertThat(keys(model)).doesNotContain("verificationToken", "token");
    }

    @Test
    void theScheduleModelHasOnlyTheMaskedLicenceNumber() {
        Policy policy = policy();
        List<Instalment> instalments = InstalmentSchedule.build(299_900, PaymentPlan.FULL, policy.period().startDate())
                .stream().map(l -> Instalment.of(policy, l, UUID.randomUUID(), START)).toList();
        Map<String, Object> model = factory.schedule(policy, header(), coverages(), instalments, 3);

        assertThat(keys(model)).noneMatch(k -> PERSONAL_KEYS.matcher(k).matches());
        assertThat(values(model)).doesNotContain(HASH, "Lic-SECRET-1234", TOKEN);
        @SuppressWarnings("unchecked")
        Map<String, Object> driver = (Map<String, Object>) model.get("driver");
        assertThat(driver).containsOnlyKeys("fullName", "licenceNumberMasked", "licenceCategories")
                .containsEntry("licenceNumberMasked", "****1234").containsEntry("licenceCategories", "B, C1");
        assertThat(model).containsEntry("total", Money.ofCentavos(299_900)).containsEntry("paymentPlan", "FULL")
                .containsEntry("renewalType", "ANNUAL_RENEWABLE").containsEntry("channel", "APP")
                .containsEntry("productVersionNo", 3).containsEntry("productName", "RC Automóvel");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) model.get("instalments");
        assertThat(rows).singleElement().satisfies(r -> assertThat(r).containsEntry("seq", 1)
                .containsEntry("status", "PAID").containsEntry("amount", Money.ofCentavos(299_900)));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> covers = (List<Map<String, Object>>) model.get("coverages");
        assertThat(covers).extracting(c -> c.get("code")).containsExactly("MTPL", "OD_COLLISION");
    }

    @Test
    void withoutADriverTheBlockIsOmitted() {
        Policy policy = policy(false);
        assertThat(factory.schedule(policy, header(), List.of(), List.of(), null))
                .containsEntry("driver", null).containsEntry("productVersionNo", null);
    }

    @Test
    void theLogoFitsInFortyByFifteenMillimetres() throws Exception {
        Logo wide = PolicyPdfModelFactory.logo(png(400, 100), "image/png");
        assertThat(wide.src()).startsWith("data:image/png;base64,");
        assertThat(wide.width()).isEqualTo("40mm");
        assertThat(wide.height()).isEqualTo("10mm");

        Logo tall = PolicyPdfModelFactory.logo(png(100, 100), "image/png");
        assertThat(tall.width()).isEqualTo("15mm");
        assertThat(tall.height()).isEqualTo("15mm");

        assertThat(PolicyPdfModelFactory.logo(png(10, 10), "image/gif")).isNull();
        assertThat(PolicyPdfModelFactory.logo("not an image".getBytes(), "image/png")).isNull();
    }

    // --- Dados ---

    Header header() {
        return new Header("Seguradora Demo", null, START);
    }

    Policy policy() {
        return policy(true);
    }

    Policy policy(boolean withDriver) {
        ObjectNode vehicle = json.createObjectNode()
                .put("vehicleId", UUID.randomUUID().toString())
                .put("ownerUserId", UUID.randomUUID().toString())
                .put("plate", "ABC-123-MC").put("chassisNumber", "JTDBR32E720123456")
                .put("make", "Toyota").put("model", "Corolla").put("year", 2018)
                .put("category", "LIGHT_PASSENGER").put("usage", "PRIVATE").put("seats", 5);
        ObjectNode driver = json.createObjectNode()
                .put("driverId", UUID.randomUUID().toString())
                .put("fullName", "Ana Maria Mussa").put("birthDate", "1990-05-01")
                .put("licenceNumberMasked", "****1234").put("licenceNumberHash", HASH);
        driver.putArray("licenceCategories").add("B").add("C1");
        Instant end = START.plus(Duration.ofDays(365));
        Policy policy = Policy.issue(new Policy.Issue("AP-2027-000123", UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), "RC Automóvel", "MTPL_ONLY", List.of("MTPL"),
                UUID.randomUUID(), "Ana Maria Mussa", UUID.randomUUID(), "ABC-123-MC", vehicle,
                withDriver ? driver : null, json.valueToTree(coverages()), new Policy.CoverPeriod(START, end),
                PaymentPlan.FULL, 299_900, "APP", null, START));
        return policy;
    }

    Certificate certificate(Policy policy) {
        return Certificate.issue(policy, "CT-2027-0001234", TOKEN);
    }

    static List<PolicyCoverageView> coverages() {
        return List.of(
                new PolicyCoverageView("MTPL", true, Money.ofCentavos(500_000_000), "NONE", null, null,
                        Money.ofCentavos(250_000)),
                new PolicyCoverageView("OD_COLLISION", false, Money.ofCentavos(80_000_000), "PERCENT", null,
                        new BigDecimal("10"), Money.ofCentavos(49_900)));
    }

    static byte[] png(int width, int height) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB), "PNG", out);
        return out.toByteArray();
    }

    // --- Percorrer o modelo ---

    static List<String> keys(Object node) {
        List<String> keys = new ArrayList<>();
        entries(node).forEach(e -> keys.add(e.getKey()));
        return keys;
    }

    static List<Object> values(Object node) {
        List<Object> values = new ArrayList<>();
        entries(node).forEach(e -> values.add(e.getValue()));
        return values;
    }

    /** Todas as entradas (chave, valor) dos mapas, em qualquer nível. */
    static List<Map.Entry<String, Object>> entries(Object node) {
        List<Map.Entry<String, Object>> out = new ArrayList<>();
        collect(node, out);
        return out;
    }

    @SuppressWarnings("unchecked")
    private static void collect(Object node, List<Map.Entry<String, Object>> out) {
        if (node instanceof Map<?, ?> map) {
            ((Map<String, Object>) map).forEach((k, v) -> {
                out.add(new java.util.AbstractMap.SimpleEntry<>(k, v));
                collect(v, out);
            });
        } else if (node instanceof Collection<?> list) {
            list.forEach(v -> collect(v, out));
        }
    }
}
