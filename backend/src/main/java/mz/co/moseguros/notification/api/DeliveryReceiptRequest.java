package mz.co.moseguros.notification.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.notification.application.DeliveryReceiptService.ReceiptStatus;

/** Recibo de entrega de uma mensagem. */
public record DeliveryReceiptRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "id devolvido pelo fornecedor no envio")
        @NotBlank @Size(max = 100)
        String providerMessageId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        ReceiptStatus status,
        @Schema(description = "Motivo indicado pelo fornecedor (sem dados pessoais)", example = "mock")
        @Size(max = 150)
        String reason) {
}
