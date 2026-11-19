package mz.co.moseguros.paymentcontracts;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.UUID;

/**
 * O resultado de uma leitura de uma fonte (S5-09): alimenta a "Última leitura", o alerta de fonte
 * parada e o ecrã de operações (S5-10, S5-12). Chave {@link
 * PaymentsTopology#sourcePolledKey(UUID)}, fila {@link
 * PaymentsTopology#PAYMENT_FILE_EVENTS_QUEUE}. Schema: {@code
 * schemas/payment-source-polled.v1.json}.
 *
 * @param messageId {@link MessageIds#paymentSourcePolled()} (UUID aleatório)
 * @param outcome {@value #OUTCOME_OK} ou {@value #OUTCOME_ERROR}
 * @param errorCode {@code null} se {@value #OUTCOME_OK}; obrigatório se {@value #OUTCOME_ERROR}
 * @param filesFound ficheiros encontrados na pasta de entrada
 * @param filesProcessed ficheiros processados nesta leitura
 * @param filesRejected ficheiros rejeitados nesta leitura
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentSourcePolled(
        String messageId,
        int schemaVersion,
        UUID insurerId,
        UUID sourceId,
        Instant startedAt,
        Instant finishedAt,
        String outcome,
        PollErrorCode errorCode,
        int filesFound,
        int filesProcessed,
        int filesRejected) {

    public static final String OUTCOME_OK = "OK";
    public static final String OUTCOME_ERROR = "ERROR";
}
