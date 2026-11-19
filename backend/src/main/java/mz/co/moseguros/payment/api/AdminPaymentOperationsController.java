package mz.co.moseguros.payment.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.payment.application.PaymentOperationsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Operações dos pagamentos para o administrador da plataforma (S5-10 §8; {@code platform:operations}, âmbito
 * {@code PLATFORM}): fontes de ficheiros de todas as seguradoras e estado das filas e do worker. Sem montantes
 * nem dados de clientes.
 */
@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = PaymentTag.NAME, description = PaymentTag.DESCRIPTION)
public class AdminPaymentOperationsController {

    private final PaymentOperationsService service;

    public AdminPaymentOperationsController(PaymentOperationsService service) {
        this.service = service;
    }

    /** Seguradora de uma fonte. */
    @Schema(name = "PaymentFileSourceInsurer")
    public record PaymentFileSourceInsurer(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String displayName) {
    }

    /** Uma fonte de ficheiros. */
    @Schema(name = "AdminPaymentFileSource")
    public record AdminPaymentFileSource(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID sourceId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentFileSourceInsurer insurer,
            @Schema(example = "90002") String entity,
            @Schema(allowableValues = {"MEPS", "BMEPS"}) String fileFormat,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean enabled,
            Instant lastPollAt,
            @Schema(allowableValues = {"OK", "ERROR"}) String lastPollOutcome,
            @Schema(description = "PollErrorCode (i18n pollErrorCode.*)") String lastPollError,
            Instant lastSuccessAt,
            Instant staleSince,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long filesLast24h,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long rejectedLast24h) {

        static AdminPaymentFileSource from(PaymentOperationsService.SourceRow r) {
            return new AdminPaymentFileSource(r.sourceId(), new PaymentFileSourceInsurer(r.insurerId(), r.insurerName()),
                    r.entity(), r.fileFormat(), r.enabled(), r.lastPollAt(), r.lastPollOutcome(), r.lastPollError(),
                    r.lastSuccessAt(), r.staleSince(), r.filesLast24h(), r.rejectedLast24h());
        }
    }

    /** {@code listPaymentFileSources}. */
    @Schema(name = "AdminPaymentFileSources")
    public record AdminPaymentFileSources(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<AdminPaymentFileSource> sources) {
    }

    /** Uma fila. */
    @Schema(name = "PaymentQueueStatus")
    public record PaymentQueueStatus(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "core.reference-payments") String name,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "-1 se a fila não existir")
            long messages,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "-1 se a fila não existir")
            long consumers) {
    }

    /** {@code getPaymentWorkerStatus}. */
    @Schema(name = "PaymentWorkerStatus")
    public record PaymentWorkerStatus(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean brokerReachable,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Vazio se o broker não responder")
            List<PaymentQueueStatus> queues,
            @Schema(description = "Última mensagem recebida do worker") Instant lastMessageAt) {
    }

    @GetMapping("/payment-file-sources")
    @Operation(operationId = "listPaymentFileSources", summary = "Lista as fontes de ficheiros de todas as seguradoras",
            description = "Permissão platform:operations. Todas as formas de pagamento BANK_REFERENCE (activas e "
                    + "inactivas), por nome da seguradora, com a última leitura e os ficheiros das últimas 24 h.")
    public AdminPaymentFileSources listPaymentFileSources() {
        return new AdminPaymentFileSources(service.sources().stream().map(AdminPaymentFileSource::from).toList());
    }

    @GetMapping("/payment-worker-status")
    @Operation(operationId = "getPaymentWorkerStatus", summary = "Estado das filas e do worker de ficheiros",
            description = "Permissão platform:operations. Mensagens e consumidores das quatro filas dos pagamentos e "
                    + "a última mensagem recebida. Broker inacessível → brokerReachable false e queues vazio.")
    public PaymentWorkerStatus getPaymentWorkerStatus() {
        PaymentOperationsService.WorkerStatus s = service.workerStatus();
        return new PaymentWorkerStatus(s.brokerReachable(), s.queues().stream()
                .map(q -> new PaymentQueueStatus(q.name(), q.messages(), q.consumers())).toList(), s.lastMessageAt());
    }
}
