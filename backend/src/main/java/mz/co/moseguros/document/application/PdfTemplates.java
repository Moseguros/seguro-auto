package mz.co.moseguros.document.application;

import java.util.Map;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.springframework.stereotype.Component;

/**
 * Modelos de PDF registados (S4-04 §1): código → modelo em {@code classpath:templates/pdf/}. Cada
 * ficha que acrescenta um modelo regista-o aqui e põe os textos em
 * {@code messages/pdf_{pt,en}.properties} (decisão D7).
 */
@Component
public class PdfTemplates {

    /** PDF da proposta (S4-04 §3). */
    public static final String PROPOSAL = "proposal";
    /** Entidade e referência para pagar (S5-02 §7). */
    public static final String PAYMENT_REFERENCE = "payment-reference";
    /** Certificado de seguro da apólice, com o código QR da verificação (S6-02 §2). */
    public static final String POLICY_CERTIFICATE = "policy-certificate";
    /** Condições particulares da apólice (S6-02 §3). */
    public static final String POLICY_SCHEDULE = "policy-schedule";

    private static final Map<String, String> TEMPLATES = Map.of(PROPOSAL, "proposal",
            PAYMENT_REFERENCE, "payment-reference",
            POLICY_CERTIFICATE, "policy-certificate",
            POLICY_SCHEDULE, "policy-schedule");

    /**
     * Nome do modelo (sem prefixo nem extensão) do código indicado.
     *
     * @throws DomainException 500 {@code PDF_TEMPLATE_NOT_FOUND} se o código não estiver registado
     */
    public String template(String templateCode) {
        String template = templateCode == null ? null : TEMPLATES.get(templateCode);
        if (template == null) {
            throw DomainException.of(ErrorCodes.PDF_TEMPLATE_NOT_FOUND, "Unknown PDF template: " + templateCode);
        }
        return template;
    }
}
