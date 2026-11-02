package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.application.ActivationService.TotpSetup;

/** Segredo TOTP para a aplicação de autenticação. */
public record TotpSetupResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Segredo em Base32 (entrada manual)")
        String totpSecret,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                example = "otpauth://totp/MoSeguros:ana@seguradora.co.mz?secret=JBSWY3DPEHPK3PXPJBSWY3DPEHPK3PXP"
                        + "&issuer=MoSeguros&algorithm=SHA1&digits=6&period=30",
                description = "URI para o QR code")
        String otpauthUri) {

    static TotpSetupResponse from(TotpSetup setup) {
        return new TotpSetupResponse(setup.totpSecret(), setup.otpauthUri());
    }

    @Override
    public String toString() {
        return "TotpSetupResponse[totpSecret=***, otpauthUri=***]";
    }
}
