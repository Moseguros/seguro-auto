package mz.co.moseguros.smsmock;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Modelos de WhatsApp que o simulador aceita, com os parâmetros obrigatórios (como um fornecedor
 * real, que só envia modelos aprovados): {@code moseguros_otp} (S1-03, {@code code}) e
 * {@code moseguros_sign_otp} (S4-04, {@code code}, {@code ref}, {@code insurer}, {@code total}).
 * Outro modelo ou um parâmetro em falta → 400.
 */
final class WhatsAppTemplates {

    static final Map<String, List<String>> PARAMS = Map.of(
            "moseguros_otp", List.of("code"),
            "moseguros_sign_otp", List.of("code", "ref", "insurer", "total"));

    private WhatsAppTemplates() {
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
