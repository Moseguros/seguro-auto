package mz.co.moseguros.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import mz.co.moseguros.shared.domain.Money;
import org.junit.jupiter.api.Test;

/** Parâmetros de {@code notify} formatados na língua do destinatário (S6-04 §1). */
class ParamFormatterTest {

    @Test
    void formatsMoneyDatesAndTextInPortuguese() {
        Map<String, String> out = ParamFormatter.format(params(), "pt");
        assertThat(out).containsEntry("amount", "129 999,00 MT")
                .containsEntry("validUntil", "10/02/2027 10:31")
                .containsEntry("startDate", "10/02/2027")
                .containsEntry("count", "3")
                .containsEntry("policyNumber", "AP-2027-000123")
                .doesNotContainKeys("webPath", "deepLink", "link", "siteUrl");
    }

    @Test
    void formatsMoneyInEnglishAndDatesTheSame() {
        Map<String, String> out = ParamFormatter.format(params(), "en");
        assertThat(out).containsEntry("amount", "MZN 129,999.00")
                .containsEntry("validUntil", "10/02/2027 10:31")
                .containsEntry("startDate", "10/02/2027");
    }

    private static Map<String, Object> params() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("amount", Money.ofCentavos(12_999_900));
        params.put("validUntil", Instant.parse("2027-02-10T08:31:00Z"));
        params.put("startDate", LocalDate.of(2027, 2, 10));
        params.put("count", 3);
        params.put("policyNumber", "AP-2027-000123");
        params.put("webPath", "/cliente/apolices/1");
        params.put("deepLink", "moseguros://policy/1");
        params.put("link", "ignored");
        return params;
    }
}
