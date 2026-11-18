package mz.co.moseguros.payment.api;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.PaymentMethodConfigService.MethodItem;
import mz.co.moseguros.payment.domain.PaymentEnvironment;
import mz.co.moseguros.payment.domain.TestResult;
import mz.co.moseguros.shared.domain.Money;

/**
 * Uma forma de pagamento da seguradora (S5-01 §5). Sem valores secretos: {@code secrets} diz só se
 * cada segredo está configurado, se é obrigatório e quando foi gravado.
 *
 * @param configured {@code false} se a seguradora ainda não gravou nada (valores por defeito)
 * @param version    ausente enquanto {@code configured = false}
 */
public record PaymentMethodResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentMethod method,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean configured,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean enabled,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentEnvironment environment,
        @Schema(nullable = true, maxLength = 60) String displayName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, ref = PaymentMethodSchemas.SETTINGS_REF) JsonNode settings,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, ref = PaymentMethodSchemas.SETTINGS_SCHEMA_REF)
        JsonNode settingsSchema,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                description = "Campo do segredo (apiKey, publicKey, securityCredential, sftp.password, sftp.privateKey, "
                        + "sftp.passphrase) → estado")
        Map<String, SecretStatusResponse> secrets,
        @Schema(nullable = true) Money minAmount,
        @Schema(nullable = true) Money maxAmount,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int intentValidityMinutes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int sortOrder,
        @Schema(nullable = true) LastTestResponse lastTest,
        @Schema(nullable = true) LastErrorResponse lastError,
        @Schema(nullable = true) Long version,
        @Schema(nullable = true) Instant updatedAt) {

    /** Estado de um segredo, nunca o valor. */
    public record SecretStatusResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean configured,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean required,
            @Schema(nullable = true) Instant updatedAt) {
    }

    /** Último teste ({@code POST …/test}); {@code message} é um código curto (ex.: {@code CONFIG_VALID}). */
    public record LastTestResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant at,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) TestResult.Status result,
            @Schema(nullable = true) String message) {
    }

    /** Último erro de configuração detectado em produção (ex.: {@code PROVIDER_CONFIG_ERROR}, S5-04). */
    public record LastErrorResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String code,
            @Schema(nullable = true) Instant at) {
    }

    static PaymentMethodResponse from(MethodItem i) {
        Map<String, SecretStatusResponse> secrets = new LinkedHashMap<>();
        i.secrets().forEach((field, s) -> secrets.put(field, new SecretStatusResponse(s.configured(), s.required(),
                s.updatedAt())));
        return new PaymentMethodResponse(i.method(), i.configured(), i.enabled(), i.environment(), i.displayName(),
                i.settings(), i.settingsSchema(), secrets, money(i.minAmount()), money(i.maxAmount()),
                i.intentValidityMinutes(), i.sortOrder(),
                i.lastTest() == null ? null
                        : new LastTestResponse(i.lastTest().at(), i.lastTest().result(), i.lastTest().message()),
                i.lastError() == null ? null : new LastErrorResponse(i.lastError().code(), i.lastError().at()),
                i.version(), i.updatedAt());
    }

    private static Money money(Long centavos) {
        return centavos == null ? null : Money.ofCentavos(centavos);
    }
}
