package mz.co.moseguros.ponto24mock;

import java.time.Instant;

/**
 * Um pagamento a escrever num ficheiro de movimentos.
 *
 * @param reference      referência (11 dígitos)
 * @param amountCentavos montante pago, em centavos
 * @param feeCentavos    tarifa (MEPS A032) ou comissão (BMEPS), em centavos
 * @param paidAt         data e hora do pagamento
 * @param channel        tipo de terminal MEPS (A015, 2 letras) ou {@code null}
 * @param bankLog        contador do banco para o estilo {@code SPEC} (A008/A014 do MEPS, "ID da transacção" do
 *                       BMEPS); ignorado no estilo {@code EXAMPLE}, que escreve zeros
 */
public record FilePayment(String reference, long amountCentavos, long feeCentavos, Instant paidAt, String channel,
                          long bankLog) {
}
