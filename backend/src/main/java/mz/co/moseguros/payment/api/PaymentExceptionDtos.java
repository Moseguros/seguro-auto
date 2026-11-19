package mz.co.moseguros.payment.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.application.PaymentExceptionService;
import mz.co.moseguros.payment.application.PaymentExceptionService.ExceptionView;
import mz.co.moseguros.payment.domain.PaymentExceptionCase;
import mz.co.moseguros.payment.domain.PaymentExceptionType;
import mz.co.moseguros.payment.domain.PaymentResolution;
import mz.co.moseguros.shared.domain.Money;

/** Corpos dos endpoints das excepções de pagamento da seguradora (S5-10 §6). */
final class PaymentExceptionDtos {

    private PaymentExceptionDtos() {
    }

    /** Resumo de uma intenção de pagamento ligada a uma excepção. */
    @Schema(name = "PaymentExceptionIntent")
    public record PaymentExceptionIntent(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PG-2027-0001234") String reference,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money amount,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentStatus status,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PR-2027-000123") String targetLabel) {

        static PaymentExceptionIntent from(PaymentExceptionService.IntentSummary s) {
            return s == null ? null : new PaymentExceptionIntent(s.id(), s.reference(), Money.ofCentavos(s.amount()),
                    s.status(), s.targetLabel());
        }
    }

    /** Linha de {@code listPaymentExceptions}. */
    @Schema(name = "PaymentExceptionSummary")
    public record PaymentExceptionSummary(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentExceptionType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"OPEN", "RESOLVED"}) String status,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant createdAt,
            @Schema(description = "Entidade (5 dígitos)", example = "90002") String entity,
            @Schema(description = "Referência (11 dígitos)", example = "00001240106") String reference,
            @Schema(description = "Montante recebido") Money amount,
            Instant paidAt,
            @Schema(description = "Intenção da referência encontrada, se houver") PaymentExceptionIntent intent,
            String fileName,
            Integer lineNo,
            @Schema(description = "Ex.: INVALID_CHECK_DIGITS, NOT_ISSUED (UNKNOWN_REFERENCE)") String detail,
            PaymentResolution resolution) {

        static PaymentExceptionSummary from(ExceptionView v) {
            PaymentExceptionCase c = v.exception();
            return new PaymentExceptionSummary(c.getId(), c.getType(), c.getStatus().name(), c.getCreatedAt(),
                    c.getEntity(), c.getReference(), c.getAmount() == null ? null : Money.ofCentavos(c.getAmount()),
                    c.getPaidAt(), PaymentExceptionIntent.from(v.intent()), c.getFileName(), c.getLineNo(),
                    c.getDetail(), c.getResolution());
        }
    }

    /** {@code getPaymentException} e {@code resolvePaymentException}. */
    @Schema(name = "PaymentExceptionDetail")
    public record PaymentExceptionDetail(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentExceptionType type,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"OPEN", "RESOLVED"}) String status,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant createdAt,
            String entity,
            String reference,
            Money amount,
            Instant paidAt,
            PaymentExceptionIntent intent,
            String fileName,
            Integer lineNo,
            String detail,
            PaymentResolution resolution,
            @Schema(description = "Identificador da transacção no banco, se o ficheiro o trouxer") String bankTransactionId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "Resoluções permitidas para o tipo; vazio depois de resolvida")
            List<PaymentResolution> allowedResolutions,
            @Schema(description = "Intenção associada (LINKED)") PaymentExceptionIntent linkedIntent,
            @Schema(description = "Id do utilizador que resolveu") UUID resolvedBy,
            Instant resolvedAt,
            String note,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "Versão; enviada na resolução (diferente → 409 CONCURRENT_MODIFICATION)")
            long version) {

        static PaymentExceptionDetail from(ExceptionView v) {
            PaymentExceptionCase c = v.exception();
            return new PaymentExceptionDetail(c.getId(), c.getType(), c.getStatus().name(), c.getCreatedAt(),
                    c.getEntity(), c.getReference(), c.getAmount() == null ? null : Money.ofCentavos(c.getAmount()),
                    c.getPaidAt(), PaymentExceptionIntent.from(v.intent()), c.getFileName(), c.getLineNo(),
                    c.getDetail(), c.getResolution(), c.getBankTransactionId(), c.allowedResolutions(),
                    PaymentExceptionIntent.from(v.linkedIntent()), c.getResolvedBy(), c.getResolvedAt(), c.getNote(),
                    c.getVersion());
        }
    }

    /** Intenção candidata a {@code LINKED}. */
    @Schema(name = "PaymentExceptionCandidate")
    public record PaymentExceptionCandidate(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "PG-2027-0001234") String reference,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Money amount,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) PaymentStatus status,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String targetLabel,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant createdAt,
            @Schema(description = "Referência bancária (11 dígitos) da intenção", example = "00001240106")
            String bankReference,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean matchesReference,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean matchesAmount) {

        static PaymentExceptionCandidate from(PaymentExceptionService.Candidate c) {
            return new PaymentExceptionCandidate(c.id(), c.reference(), Money.ofCentavos(c.amount()), c.status(),
                    c.targetLabel(), c.createdAt(), c.bankReference(), c.matchesReference(), c.matchesAmount());
        }
    }

    /** {@code listPaymentExceptionCandidates}. */
    @Schema(name = "PaymentExceptionCandidates")
    public record PaymentExceptionCandidates(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<PaymentExceptionCandidate> candidates) {
    }

    /** Corpo de {@code resolvePaymentException}. */
    @Schema(name = "ResolvePaymentExceptionRequest")
    public record ResolvePaymentExceptionRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    allowableValues = {"LINKED", "REFUND", "IGNORED", "MARK_SUCCEEDED", "MARK_FAILED"},
                    description = "REFUND só regista a decisão: a seguradora devolve o dinheiro fora da plataforma")
            String resolution,
            @Schema(description = "Só LINKED (obrigatório): intenção BANK_REFERENCE desta seguradora em PENDING, "
                    + "EXPIRED ou CANCELLED") UUID intentId,
            @Schema(description = "Só MARK_SUCCEEDED: id da transacção no portal do fornecedor", maxLength = 100)
            String providerTxId,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 5, maxLength = 500) String note,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Long version) {
    }
}
