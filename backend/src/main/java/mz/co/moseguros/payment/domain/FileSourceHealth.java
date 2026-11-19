package mz.co.moseguros.payment.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Estado de uma fonte de ficheiros {@code BANK_REFERENCE} activa, para o alerta de fonte parada (S5-10 §5).
 *
 * @param enabledAt         última activação (referência quando nunca houve uma leitura com sucesso)
 * @param lastPollSuccessAt fim da última leitura com sucesso, ou {@code null}
 * @param staleSince        desde quando a fonte está parada, ou {@code null}
 */
public record FileSourceHealth(UUID id, UUID insurerId, Instant enabledAt, Instant lastPollSuccessAt,
                               Instant staleSince) {

    /** {@code last_poll_success_at}, ou {@code enabled_at} se nunca houve sucesso; pode ser {@code null}. */
    public Instant lastGoodAt() {
        return lastPollSuccessAt != null ? lastPollSuccessAt : enabledAt;
    }
}
