package mz.co.moseguros.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Test;

/** Renderização e validação dos modelos (S6-04 §3). */
class TemplateRendererTest {

    final TemplateRenderer renderer = new TemplateRenderer(true);
    final List<String> variables = List.of("policyNumber", "link", "siteUrl");
    final Map<String, String> samples = Map.of("policyNumber", "AP-2027-000123", "link", "https://x/y",
            "siteUrl", "https://x");

    @Test
    void rendersWithoutHtmlEscaping() {
        assertThat(renderer.render("Apólice {{policyNumber}} <b> & {{siteUrl}}", Map.of("policyNumber", "A&B",
                "siteUrl", "https://x?a=1&b=2"))).isEqualTo("Apólice A&B <b> & https://x?a=1&b=2");
    }

    @Test
    void unknownVariableFails() {
        assertThatThrownBy(() -> renderer.render("{{nope}}", Map.of("policyNumber", "x")))
                .isInstanceOfSatisfying(TemplateRenderer.TemplateException.class,
                        e -> assertThat(e.code()).isEqualTo("TEMPLATE_UNKNOWN_VARIABLE"));
    }

    @Test
    void badSyntaxFails() {
        assertThatThrownBy(() -> renderer.render("Olá {{policyNumber", Map.of("policyNumber", "x")))
                .isInstanceOfSatisfying(TemplateRenderer.TemplateException.class,
                        e -> assertThat(e.code()).isEqualTo("TEMPLATE_SYNTAX"));
        assertThatThrownBy(() -> renderer.render("{{#policyNumber}}x", Map.of("policyNumber", "x")))
                .isInstanceOfSatisfying(TemplateRenderer.TemplateException.class,
                        e -> assertThat(e.code()).isEqualTo("TEMPLATE_SYNTAX"));
    }

    @Test
    void validationRulesGiveTheFieldCodes() {
        assertThat(codes(MessageChannel.SMS, null, "MoSeguros {{policyNumber}} {{siteUrl}}", null)).isEmpty();
        assertThat(codes(MessageChannel.SMS, null, "{{policyNumber", null)).containsExactly("body:TEMPLATE_SYNTAX");
        assertThat(codes(MessageChannel.SMS, null, "{{plate}}", null))
                .containsExactly("body:TEMPLATE_UNKNOWN_VARIABLE");
        assertThat(codes(MessageChannel.SMS, null, "x".repeat(161), null)).containsExactly("body:SMS_TOO_LONG");
        assertThat(codes(MessageChannel.EMAIL, " ", "Olá {{link}}", null)).containsExactly("subject:REQUIRED");
        assertThat(codes(MessageChannel.EMAIL, "s".repeat(201), "Olá", null)).containsExactly("subject:TOO_LONG");
        assertThat(codes(MessageChannel.EMAIL, "{{x}}", "Olá", null))
                .containsExactly("subject:TEMPLATE_UNKNOWN_VARIABLE");
        assertThat(codes(MessageChannel.PUSH, "t".repeat(66), "Olá", null)).containsExactly("subject:TOO_LONG");
        assertThat(codes(MessageChannel.PUSH, "Título", "p".repeat(241), null)).containsExactly("body:TOO_LONG");
        assertThat(codes(MessageChannel.WHATSAPP, null, "Olá", " ")).containsExactly("whatsappTemplateName:REQUIRED");
        assertThat(codes(MessageChannel.WHATSAPP, null, "Olá", "moseguros_x")).isEmpty();
    }

    private List<String> codes(MessageChannel channel, String subject, String body, String waName) {
        return renderer.validate(channel, subject, body, waName, variables, samples).stream()
                .map((FieldError e) -> e.field() + ":" + e.code()).toList();
    }
}
