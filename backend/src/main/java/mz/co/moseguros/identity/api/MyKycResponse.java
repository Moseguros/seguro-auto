package mz.co.moseguros.identity.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.identity.application.KycService.MyKyc;

/** Estado KYC do cliente e a verificação mais recente ({@code check} omitido em {@code NONE}). */
public record MyKycResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "OUTDATED: verificada, mas o nome, a data de nascimento ou o documento mudaram depois")
        KycState state,
        KycCheckResponse check) {

    static MyKycResponse from(MyKyc kyc) {
        return new MyKycResponse(kyc.state(), kyc.latest() == null ? null : KycCheckResponse.from(kyc.latest()));
    }
}
