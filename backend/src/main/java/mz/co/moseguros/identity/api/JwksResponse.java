package mz.co.moseguros.identity.api;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/** Conjunto de chaves públicas de assinatura dos tokens (RFC 7517). */
public record JwksResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<Jwk> keys) {

    /** Chave pública RSA. */
    public record Jwk(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "dev")
            String kid,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "RSA")
            String kty,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "RS256")
            String alg,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "sig")
            String use,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Módulo (Base64 URL)")
            String n,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Expoente (Base64 URL)",
                    example = "AQAB")
            String e) {
    }
}
