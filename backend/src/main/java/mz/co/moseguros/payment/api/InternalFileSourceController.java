package mz.co.moseguros.payment.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.payment.application.PaymentSystemLookups;
import mz.co.moseguros.paymentcontracts.FileSourceList;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fontes de ficheiros de movimentos para o {@code payment-file-worker} (S5-09 §1). Interno: só com o
 * token de serviço ({@code InternalSecurityConfig}, permissão {@code payments:file-sources}) e fora do
 * {@code api/openapi.yaml}. O contrato é o {@link FileSourceList} do {@code payment-contracts}
 * ({@code schemas/file-source-list.v1.json}). Nunca devolve segredos, só os caminhos no cofre.
 */
@RestController
@Tag(name = PaymentTag.NAME, description = PaymentTag.DESCRIPTION)
public class InternalFileSourceController {

    private final PaymentSystemLookups lookups;

    public InternalFileSourceController(PaymentSystemLookups lookups) {
        this.lookups = lookups;
    }

    @GetMapping("/internal/v1/payment-file-sources")
    @PreAuthorize("hasAuthority('payments:file-sources')")
    @Operation(operationId = "listInternalFileSources", summary = "Fontes de ficheiros de movimentos (interno)")
    public FileSourceList listInternalFileSources() {
        return lookups.activeFileSources();
    }
}
