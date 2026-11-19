package mz.co.moseguros.payment.api;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.payment.application.FileImportQueryService;
import mz.co.moseguros.payment.domain.FileImport;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ficheiros de movimentos e estado da fonte de ficheiros da seguradora (S5-10 §7; {@code payment:read}, âmbito
 * {@code ORG} de uma seguradora).
 */
@RestController
@RequestMapping("/api/v1/insurer")
@Tag(name = PaymentTag.NAME, description = PaymentTag.DESCRIPTION)
public class InsurerPaymentFileController {

    private static final Set<String> SORTS = Set.of("occurredAt");

    private final FileImportQueryService service;

    public InsurerPaymentFileController(FileImportQueryService service) {
        this.service = service;
    }

    /** Linha de {@code listPaymentFileImports}. */
    @Schema(name = "PaymentFileImport")
    public record PaymentFileImport(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String fileName,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String sha256,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"MEPS", "BMEPS"})
            String fileFormat,
            @Schema(description = "Identificador do ficheiro no formato (MEPS AAAAMMDDS; BMEPS empresa-perfil-ID)")
            String formatFileId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"PROCESSED", "REJECTED"})
            String status,
            @Schema(description = "Só PROCESSED") Integer recordCount,
            @Schema(description = "Só PROCESSED") Money totalAmount,
            @Schema(description = "Tarifas (MEPS) ou comissões (BMEPS); informativo") Money totalFees,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Pagamentos confirmados")
            long confirmedCount,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Pagamentos que geraram excepções")
            long exceptionCount,
            @Schema(description = "Só REJECTED: FileRejectionReason (i18n fileRejectionReason.*)") String reason,
            String detail,
            Integer lineNo,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant occurredAt) {

        static PaymentFileImport from(FileImportQueryService.FileView v) {
            FileImport f = v.file();
            return new PaymentFileImport(f.getId(), f.getFileName(), f.getSha256(), f.getFileFormat(),
                    f.getFormatFileId(), f.getStatus().name(), f.getRecordCount(),
                    f.getTotalAmount() == null ? null : Money.ofCentavos(f.getTotalAmount()),
                    f.getTotalFees() == null ? null : Money.ofCentavos(f.getTotalFees()), v.confirmedCount(),
                    v.exceptionCount(), f.getReason(), f.getDetail(), f.getLineNo(), f.getOccurredAt());
        }
    }

    /** {@code getPaymentFileSourceStatus}. */
    @Schema(name = "PaymentFileSourceStatus")
    public record PaymentFileSourceStatus(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "false sem forma de pagamento BANK_REFERENCE (os outros campos não vêm)")
            boolean configured,
            Boolean enabled,
            @Schema(example = "90002") String entity,
            @Schema(allowableValues = {"MEPS", "BMEPS"}) String fileFormat,
            @Schema(description = "Minutos sem leitura com sucesso a partir dos quais a fonte está parada")
            Long staleAfterMinutes,
            Instant lastPollAt,
            @Schema(allowableValues = {"OK", "ERROR"}) String lastPollOutcome,
            @Schema(description = "PollErrorCode (i18n pollErrorCode.*)") String lastPollError,
            Instant lastSuccessAt,
            Instant staleSince) {

        static PaymentFileSourceStatus from(FileImportQueryService.SourceStatus s) {
            return new PaymentFileSourceStatus(s.configured(), s.enabled(), s.entity(), s.fileFormat(),
                    s.staleAfterMinutes(), s.lastPollAt(), s.lastPollOutcome(), s.lastPollError(), s.lastSuccessAt(),
                    s.staleSince());
        }
    }

    @GetMapping("/payment-files")
    @Operation(operationId = "listPaymentFileImports", summary = "Lista os ficheiros de movimentos da seguradora",
            description = "Permissão payment:read. Ficheiros MEPS ou BMEPS processados ou rejeitados pelo worker, "
                    + "com os pagamentos confirmados e as excepções de cada um. Filtros: status (PROCESSED, "
                    + "REJECTED), from e to em datas de Africa/Maputo (inclusivas, sobre occurredAt). Ordenação "
                    + "occurredAt (defeito desc).")
    public PageResponse<PaymentFileImport> listPaymentFileImports(
            @Parameter(description = "PROCESSED ou REJECTED") @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @Parameter(description = "occurredAt, com ,asc ou ,desc", example = "occurredAt,desc")
            @RequestParam(required = false) String sort) {
        return PageResponse.of(service.list(new FileImportQueryService.Filter(status(status), from, to),
                        PageRequests.of(page, size, sort, SORTS, Sort.by(Sort.Direction.DESC, "occurredAt")
                                .and(Sort.by("id")))),
                PaymentFileImport::from);
    }

    @GetMapping("/payment-file-source")
    @Operation(operationId = "getPaymentFileSourceStatus", summary = "Estado da fonte de ficheiros da seguradora",
            description = "Permissão payment:read. Última leitura do SFTP, último sucesso e se a fonte está parada "
                    + "(sem leitura com sucesso há mais de staleAfterMinutes). Sem BANK_REFERENCE → configured false.")
    public PaymentFileSourceStatus getPaymentFileSourceStatus() {
        return PaymentFileSourceStatus.from(service.sourceStatus());
    }

    private static FileImport.Status status(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return FileImport.Status.valueOf(raw.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new FieldValidationException("status", FieldErrorCodes.INVALID_VALUE, "Unknown status");
        }
    }
}
