package mz.co.moseguros.paymentworker.parser;

import java.util.Objects;

import mz.co.moseguros.paymentcontracts.FileRejectionReason;

/**
 * O ficheiro não se lê (S5-08 §1). A mensagem nunca traz o conteúdo das linhas, só o que falhou e o
 * número da linha.
 */
public class PaymentFileParseException extends Exception {

    private final FileRejectionReason code;
    private final Integer lineNo;

    public PaymentFileParseException(FileRejectionReason code, Integer lineNo, String message) {
        super(message);
        this.code = Objects.requireNonNull(code, "code");
        this.lineNo = lineNo;
    }

    /** {@code PARSE_ERROR}, {@code ENCODING_ERROR}, {@code MISSING_HEADER}, {@code MISSING_TRAILER} ou {@code UNSUPPORTED_RECORD}. */
    public FileRejectionReason code() {
        return code;
    }

    /** Linha do problema, ou {@code null}. */
    public Integer lineNo() {
        return lineNo;
    }
}
