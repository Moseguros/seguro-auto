package mz.co.moseguros.mpesamock;

import java.util.function.ToIntFunction;

/**
 * Comportamento do C2B escolhido pelos três últimos dígitos de {@code input_CustomerMSISDN}
 * (ficha S5-03 §4): código da resposta, estado final registado e atraso.
 */
enum Behaviour {
    /** {@code …000}: sucesso depois de {@code MOCK_DELAY_000_SECONDS}. */
    SUCCESS_000(ResponseCodes.INS_0, MockTransaction.Status.Completed, MockProperties::getDelay000Seconds),
    /** {@code …001}: cancelado pelo cliente. */
    CANCELLED(ResponseCodes.INS_5, MockTransaction.Status.Cancelled, MockProperties::getFailureDelaySeconds),
    /** {@code …002}: saldo insuficiente. */
    INSUFFICIENT_BALANCE(ResponseCodes.INS_2006, MockTransaction.Status.Failed, MockProperties::getFailureDelaySeconds),
    /** {@code …003}: sucesso só depois de {@code MOCK_SLOW_DELAY_SECONDS} (força o timeout do cliente). */
    SLOW(ResponseCodes.INS_0, MockTransaction.Status.Completed, MockProperties::getSlowDelaySeconds),
    /** {@code …004}: duplicado, sem espera. */
    DUPLICATE(ResponseCodes.INS_10, MockTransaction.Status.Cancelled, p -> 0),
    /** {@code …005}: a transacção falhou. */
    FAILED(ResponseCodes.INS_6, MockTransaction.Status.Failed, MockProperties::getFailureDelaySeconds),
    /** Qualquer outro final: sucesso depois de {@code MOCK_DEFAULT_DELAY_SECONDS}. */
    SUCCESS(ResponseCodes.INS_0, MockTransaction.Status.Completed, MockProperties::getDefaultDelaySeconds);

    private final ResponseCodes responseCode;
    private final MockTransaction.Status finalStatus;
    private final ToIntFunction<MockProperties> delay;

    Behaviour(ResponseCodes responseCode, MockTransaction.Status finalStatus, ToIntFunction<MockProperties> delay) {
        this.responseCode = responseCode;
        this.finalStatus = finalStatus;
        this.delay = delay;
    }

    static Behaviour forMsisdn(String msisdn) {
        String ending = msisdn.length() >= 3 ? msisdn.substring(msisdn.length() - 3) : "";
        return switch (ending) {
            case "000" -> SUCCESS_000;
            case "001" -> CANCELLED;
            case "002" -> INSUFFICIENT_BALANCE;
            case "003" -> SLOW;
            case "004" -> DUPLICATE;
            case "005" -> FAILED;
            default -> SUCCESS;
        };
    }

    ResponseCodes responseCode() {
        return responseCode;
    }

    MockTransaction.Status finalStatus() {
        return finalStatus;
    }

    int delaySeconds(MockProperties props) {
        return Math.max(0, delay.applyAsInt(props));
    }
}
