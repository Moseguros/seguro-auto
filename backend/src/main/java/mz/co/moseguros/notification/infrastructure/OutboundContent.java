package mz.co.moseguros.notification.infrastructure;

import java.util.List;
import java.util.Objects;

/**
 * Conteúdo de uma mensagem para um {@link ChannelSender} (S6-04 §6).
 *
 * @param destination      telemóvel em E.164 ({@code +258…}) ou e-mail
 * @param subject          assunto (só e-mail)
 * @param text             texto do SMS ou corpo do e-mail; no WhatsApp, a cópia local do texto aprovado
 * @param whatsappTemplate modelo WhatsApp aprovado ({@code moseguros_otp}, {@code moseguros_sign_otp},
 *                         {@code moseguros_policy_issued}…); os outros canais ignoram-no
 * @param whatsappParams   valores dos parâmetros do modelo WhatsApp, por ordem
 */
public record OutboundContent(String destination, String subject, String text, String whatsappTemplate,
                              List<String> whatsappParams) {

    public OutboundContent {
        Objects.requireNonNull(destination, "destination");
        whatsappParams = whatsappParams == null ? List.of()
                : java.util.Collections.unmodifiableList(new java.util.ArrayList<>(whatsappParams));
    }

    /** Valor de um parâmetro WhatsApp pela posição (0 = o primeiro); {@code null} se não existir. */
    public String whatsappParam(int index) {
        return index < whatsappParams.size() ? whatsappParams.get(index) : null;
    }
}
