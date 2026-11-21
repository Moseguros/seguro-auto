package mz.co.moseguros.verification.api;

/** Tag OpenAPI dos controladores do módulo Verificação. */
final class VerificationTag {

    static final String NAME = "Verification";
    static final String DESCRIPTION = "Verificação de seguro pela matrícula ou pelo QR do certificado: pública "
            + "(com CAPTCHA na matrícula) e para os agentes da polícia; nunca devolve dados pessoais do tomador";

    private VerificationTag() {
    }
}
