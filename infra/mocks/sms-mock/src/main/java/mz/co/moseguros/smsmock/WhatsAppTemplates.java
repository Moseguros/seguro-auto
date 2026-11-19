package mz.co.moseguros.smsmock;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Modelos de WhatsApp que o simulador aceita, com os parâmetros obrigatórios (como um fornecedor
 * real, que só envia modelos aprovados): {@code moseguros_otp} (S1-03, {@code code}),
 * {@code moseguros_sign_otp} (S4-04, {@code code}, {@code ref}, {@code insurer}, {@code total}) e os nove
 * modelos das notificações (S6-04 §10), com os parâmetros por ordem ({@code p1} a {@code pn}).
 * Outro modelo ou um parâmetro em falta → 400.
 */
final class WhatsAppTemplates {

    static final Map<String, List<String>> PARAMS = Map.ofEntries(
            Map.entry("moseguros_otp", List.of("code")),
            Map.entry("moseguros_sign_otp", List.of("code", "ref", "insurer", "total")),
            // S6-04 §10: proposalNumber, insurerName, amount
            Map.entry("moseguros_proposal_accepted", positional(3)),
            // proposalNumber, insurerName
            Map.entry("moseguros_proposal_rejected", positional(2)),
            Map.entry("moseguros_proposal_info_requested", positional(2)),
            // proposalNumber
            Map.entry("moseguros_proposal_expired", positional(1)),
            // paymentReference, amount, insurerName
            Map.entry("moseguros_payment_succeeded", positional(3)),
            // paymentReference, amount
            Map.entry("moseguros_payment_failed", positional(2)),
            // amount, entity, reference, validUntil
            Map.entry("moseguros_payment_reference", positional(4)),
            // policyNumber, plate, validTo
            Map.entry("moseguros_policy_issued", positional(3)),
            // amount, validUntil
            Map.entry("moseguros_quote_expiring", positional(2)));

    private WhatsAppTemplates() {
    }

    /** {@code p1} a {@code pn}. */
    static List<String> positional(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count).mapToObj(i -> "p" + i).toList();
    }

    static void validate(String template, Map<String, String> params) {
        List<String> required = PARAMS.get(template);
        if (required == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown template: " + template);
        }
        for (String name : required) {
            String value = params == null ? null : params.get(name);
            if (value == null || value.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Missing parameter '" + name + "' for template " + template);
            }
        }
    }
}
