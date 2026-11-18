package mz.co.moseguros.payment.infrastructure.mpesa;

import java.util.Set;

import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.application.gateway.InitiationResult;
import mz.co.moseguros.payment.application.gateway.RefundResult;
import mz.co.moseguros.payment.application.gateway.StatusResult;

/**
 * Tabelas da secção 3 da ficha S5-04: do {@code output_ResponseCode} (e, na consulta, do
 * {@code output_ResponseTransactionStatus}) para o resultado da intenção. Sem estado e sem efeitos:
 * o {@link MpesaGateway} trata os alertas e os registos.
 */
public final class MpesaResponseMapper {

    public static final String SUCCESS = "INS-0";

    /** Credenciais, código de serviço ou autorização (C2B e consulta): alerta {@code PAYMENT_CONFIG_ERROR}. */
    static final Set<String> CONFIG_ERRORS = Set.of("INS-2", "INS-13", "INS-25", "INS-26");
    /** Na reversão também o iniciador. */
    static final Set<String> REVERSAL_CONFIG_ERRORS = Set.of("INS-2", "INS-24", "INS-25", "INS-26");
    /** Erro do nosso pedido (campos em falta ou inválidos). */
    static final Set<String> REQUEST_ERRORS = Set.of("INS-20", "INS-21");
    /** O resultado não se sabe: consulta. */
    static final Set<String> AMBIGUOUS = Set.of("INS-1", "INS-9", "INS-10");

    private MpesaResponseMapper() {
    }

    /** O que fazer com o código. */
    public enum Signal {
        /** Nada de especial. */
        NONE,
        /** Alerta {@code PAYMENT_CONFIG_ERROR} (secção 5). */
        CONFIG_ERROR,
        /** {@code ERROR} no log: o pedido estava mal feito. */
        REQUEST_ERROR,
        /** {@code WARN} com o código: não está na tabela. */
        UNEXPECTED_CODE
    }

    /** Resultado do C2B. */
    public record C2bMapping(InitiationResult.Outcome outcome, FailureCode failureCode, Signal signal) {
    }

    /** Resultado da consulta. */
    public record QueryMapping(StatusResult.Outcome outcome, FailureCode failureCode, Signal signal) {
    }

    /** Resultado da reversão. */
    public record ReversalMapping(RefundResult.Outcome outcome, Signal signal) {
    }

    /** C2B com resposta legível (sem resposta → {@link #c2bWithoutResponse()}). */
    public static C2bMapping c2b(String code) {
        String c = code == null ? "" : code.trim();
        if (SUCCESS.equals(c)) {
            return new C2bMapping(InitiationResult.Outcome.SUCCEEDED, null, Signal.NONE);
        }
        if (CONFIG_ERRORS.contains(c)) {
            return failed(FailureCode.PROVIDER_CONFIG_ERROR, Signal.CONFIG_ERROR);
        }
        if (REQUEST_ERRORS.contains(c)) {
            return failed(FailureCode.PROVIDER_REJECTED, Signal.REQUEST_ERROR);
        }
        if (AMBIGUOUS.contains(c)) {
            return new C2bMapping(InitiationResult.Outcome.UNKNOWN, null, Signal.NONE);
        }
        return switch (c) {
            case "INS-5" -> failed(FailureCode.CUSTOMER_CANCELLED, Signal.NONE);
            case "INS-6" -> failed(FailureCode.PROVIDER_REJECTED, Signal.NONE);
            case "INS-2006" -> failed(FailureCode.INSUFFICIENT_BALANCE, Signal.NONE);
            case "INS-2051" -> failed(FailureCode.INVALID_MSISDN, Signal.NONE);
            default -> new C2bMapping(InitiationResult.Outcome.UNKNOWN, null, Signal.UNEXPECTED_CODE);
        };
    }

    /** Timeout, erro de rede ou corpo ilegível depois de começar o envio: não se sabe, consulta. */
    public static C2bMapping c2bWithoutResponse() {
        return new C2bMapping(InitiationResult.Outcome.UNKNOWN, null, Signal.NONE);
    }

    /** Consulta com resposta legível. */
    public static QueryMapping query(String code, String transactionStatus) {
        String c = code == null ? "" : code.trim();
        if (CONFIG_ERRORS.contains(c)) {
            return new QueryMapping(StatusResult.Outcome.STILL_UNKNOWN, null, Signal.CONFIG_ERROR);
        }
        if (!SUCCESS.equals(c)) {
            return new QueryMapping(StatusResult.Outcome.STILL_UNKNOWN, null, Signal.NONE);
        }
        String status = transactionStatus == null ? "" : transactionStatus.trim();
        if ("Completed".equalsIgnoreCase(status)) {
            return new QueryMapping(StatusResult.Outcome.SUCCEEDED, null, Signal.NONE);
        }
        if ("Cancelled".equalsIgnoreCase(status)) {
            return new QueryMapping(StatusResult.Outcome.FAILED, FailureCode.CUSTOMER_CANCELLED, Signal.NONE);
        }
        if ("Expired".equalsIgnoreCase(status)) {
            return new QueryMapping(StatusResult.Outcome.FAILED, FailureCode.TIMEOUT_EXPIRED, Signal.NONE);
        }
        if ("Failed".equalsIgnoreCase(status)) {
            return new QueryMapping(StatusResult.Outcome.FAILED, FailureCode.PROVIDER_REJECTED, Signal.NONE);
        }
        // Pending, N/A ou outro: continua por saber.
        return new QueryMapping(StatusResult.Outcome.STILL_UNKNOWN, null, Signal.NONE);
    }

    /** Reversão com resposta legível (sem resposta → {@code UNKNOWN}). */
    public static ReversalMapping reversal(String code) {
        String c = code == null ? "" : code.trim();
        if (SUCCESS.equals(c)) {
            return new ReversalMapping(RefundResult.Outcome.SUCCEEDED, Signal.NONE);
        }
        if (REVERSAL_CONFIG_ERRORS.contains(c)) {
            return new ReversalMapping(RefundResult.Outcome.FAILED, Signal.CONFIG_ERROR);
        }
        return new ReversalMapping(RefundResult.Outcome.FAILED, Signal.NONE);
    }

    private static C2bMapping failed(FailureCode code, Signal signal) {
        return new C2bMapping(InitiationResult.Outcome.FAILED, code, signal);
    }
}
