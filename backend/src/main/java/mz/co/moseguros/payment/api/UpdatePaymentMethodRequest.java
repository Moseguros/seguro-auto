package mz.co.moseguros.payment.api;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.payment.domain.PaymentEnvironment;
import mz.co.moseguros.shared.domain.Money;

/**
 * Dados de uma forma de pagamento (S5-01 §5). Os obrigatórios do schema só se exigem ao activar.
 *
 * @param secrets campo → valor ({@code apiKey}, {@code publicKey}, {@code securityCredential} no
 *                M-Pesa; {@code sftp.password}, {@code sftp.privateKey}, {@code sftp.passphrase}
 *                nas referências). Campo ausente mantém o segredo; presente substitui-o. Nunca é
 *                devolvido.
 * @param version a {@code version} lida; ausente só na criação
 */
public record UpdatePaymentMethodRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentEnvironment environment,
        @Schema(nullable = true, maxLength = 60) String displayName,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, ref = PaymentMethodSchemas.SETTINGS_REF) JsonNode settings,
        @Schema(nullable = true, description = "Campo → valor do segredo (até 8000 caracteres); escrito só no cofre de "
                + "segredos") Map<String, String> secrets,
        @Schema(nullable = true, description = "amount > 0") Money minAmount,
        @Schema(nullable = true, description = "amount > 0; não inferior a minAmount") Money maxAmount,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minimum = "5", maximum = "1440",
                description = "Validade das intenções M-Pesa ainda não enviadas (ignorado em BANK_REFERENCE)")
        Integer intentValidityMinutes,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minimum = "0", maximum = "99") Integer sortOrder,
        @Schema(nullable = true) Long version) {

    /** Sem os valores dos segredos (só os nomes dos campos). */
    @Override
    public String toString() {
        return "UpdatePaymentMethodRequest[environment=" + environment + ", secrets="
                + (secrets == null ? "[]" : secrets.keySet()) + ", version=" + version + "]";
    }
}
