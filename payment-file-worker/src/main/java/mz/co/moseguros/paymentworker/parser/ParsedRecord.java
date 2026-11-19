package mz.co.moseguros.paymentworker.parser;

import java.time.Instant;

/**
 * Um registo de detalhe: um pagamento (S5-08 §1).
 *
 * @param lineNo            linha no ficheiro (1 = cabeçalho)
 * @param entity            entidade (5 dígitos)
 * @param reference         referência (11 dígitos)
 * @param amountCentavos    montante pago, em centavos
 * @param feeCentavos       tarifa (MEPS) ou comissão (BMEPS); {@code null} se não houver
 * @param paidAt            data e hora do pagamento do cliente
 * @param channel           tipo de terminal; {@code null} se vazio
 * @param bankTransactionId identificador do banco; {@code null} se só zeros
 * @param rawLine           a linha sem o terminador (sem dados pessoais, S5-08 §2)
 */
public record ParsedRecord(int lineNo, String entity, String reference, long amountCentavos, Long feeCentavos,
                           Instant paidAt, String channel, String bankTransactionId, String rawLine) {

    /** Não escreve a linha nem a referência completa (logs). */
    @Override
    public String toString() {
        return "ParsedRecord[lineNo=" + lineNo + "]";
    }
}
