package mz.co.moseguros.payment.domain;

import java.util.Objects;

/**
 * Resultado do teste de uma forma de pagamento (S5-01 §5).
 *
 * @param status  {@code OK}, {@code FAILED} ou {@code NOT_SUPPORTED}
 * @param message código curto (ex.: {@code CONFIG_VALID}, {@code SECRET_MISSING}, {@code TIMEOUT}),
 *                nunca dados secretos; {@code null} se não houver
 */
public record TestResult(Status status, String message) {

    public static final int MAX_MESSAGE = 200;

    public enum Status {
        OK,
        FAILED,
        NOT_SUPPORTED
    }

    public TestResult {
        Objects.requireNonNull(status, "status");
        if (message != null && message.length() > MAX_MESSAGE) {
            message = message.substring(0, MAX_MESSAGE);
        }
    }

    public static TestResult ok(String message) {
        return new TestResult(Status.OK, message);
    }

    public static TestResult failed(String message) {
        return new TestResult(Status.FAILED, message);
    }

    public static TestResult notSupported() {
        return new TestResult(Status.NOT_SUPPORTED, null);
    }
}
