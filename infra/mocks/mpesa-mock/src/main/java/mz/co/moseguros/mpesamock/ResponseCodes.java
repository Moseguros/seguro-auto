package mz.co.moseguros.mpesamock;

/**
 * Códigos de resposta da API M-Pesa ({@code output_ResponseCode}) com a descrição e o estado HTTP
 * que o simulador devolve (tabela de trabalho da ficha S5-03 §7). Quem consome decide sempre pelo
 * código, nunca pelo estado HTTP.
 */
enum ResponseCodes {
    INS_0("INS-0", "Request processed successfully", 200),
    INS_1("INS-1", "Internal Error", 500),
    INS_2("INS-2", "Invalid API Key", 401),
    INS_5("INS-5", "Transaction cancelled by customer", 401),
    INS_6("INS-6", "Transaction Failed", 401),
    INS_9("INS-9", "Request timeout", 408),
    INS_10("INS-10", "Duplicate Transaction", 409),
    INS_13("INS-13", "Invalid Shortcode Used", 400),
    INS_15("INS-15", "Invalid Amount Used", 400),
    INS_18("INS-18", "Invalid TransactionID Used", 400),
    INS_20("INS-20", "Not All Parameters Provided. Please try again.", 422),
    INS_21("INS-21", "Parameter validations failed. Please try again.", 422),
    INS_24("INS-24", "Invalid InitiatorIdentifier Used", 400),
    INS_25("INS-25", "Invalid SecurityCredential Used", 400),
    INS_26("INS-26", "Not authorized", 401),
    INS_2006("INS-2006", "Insufficient balance", 422);

    /** Estado HTTP de um INS-0 no C2B (a consulta e a reversão devolvem 200). */
    static final int C2B_SUCCESS_HTTP_STATUS = 201;

    private final String code;
    private final String description;
    private final int httpStatus;

    ResponseCodes(String code, String description, int httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    String code() {
        return code;
    }

    String description() {
        return description;
    }

    int httpStatus() {
        return httpStatus;
    }
}
