package mz.co.moseguros.paymentworker.processing;

import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.paymentcontracts.PaymentSourcePolled;
import mz.co.moseguros.paymentcontracts.PollErrorCode;

/**
 * Resultado de uma leitura de uma fonte (S5-09 §5): o mesmo que vai no {@code PaymentSourcePolled}.
 *
 * @param outcome   {@code OK} ou {@code ERROR}; ficheiros rejeitados por conteúdo não tornam a leitura {@code ERROR}
 * @param errorCode {@code null} se {@code OK}
 */
public record PollResult(UUID sourceId, UUID insurerId, Instant startedAt, Instant finishedAt, String outcome,
                         PollErrorCode errorCode, int filesFound, int filesProcessed, int filesRejected) {

    public boolean ok() {
        return PaymentSourcePolled.OUTCOME_OK.equals(outcome);
    }
}
