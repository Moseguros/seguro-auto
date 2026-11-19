package mz.co.moseguros.shared.idempotency;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Idempotência partilhada (S5-02 §5; decisão D11): a mesma {@code Idempotency-Key} do mesmo utilizador
 * devolve a mesma resposta durante {@value #TTL_HOURS} h (AGENTS.md §4). Usam-na o
 * {@code PaymentApi.createIntent} (S5, S7, S12-02) e as indemnizações (S9-04).
 *
 * <p>Corre na transacção de quem chama ({@link Propagation#MANDATORY}):
 * <ol>
 *   <li>{@code insert … on conflict (user_id, idempotency_key) do nothing}: um pedido simultâneo com a
 *       mesma chave espera neste {@code insert} até a outra transacção terminar (índice único);</li>
 *   <li>inseriu → corre o pedido e grava a resposta;</li>
 *   <li>não inseriu → lê o registo bloqueado: expirado → substitui-o e corre o pedido; outra operação
 *       ou outro pedido → 422 {@code IDEMPOTENCY_KEY_REUSED}; igual → devolve a resposta gravada;</li>
 *   <li>erro no pedido → a transacção desfaz-se e o registo também (a chave pode ser usada de novo).</li>
 * </ol>
 */
@Service
public class IdempotencyService {

    static final int TTL_HOURS = 24;
    static final Duration TTL = Duration.ofHours(TTL_HOURS);

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);
    private static final String INSERT = """
            insert into shared.idempotency_record (id, created_at, user_id, idempotency_key, operation, request_hash,
                                                   expires_at)
            values (:id, :now, :userId, :key, :operation, :hash, :expiresAt)
            on conflict (user_id, idempotency_key) do nothing
            returning id""";

    @PersistenceContext
    private EntityManager entityManager;

    private final IdempotencyRecordRepository records;
    private final Clock clock;

    public IdempotencyService(IdempotencyRecordRepository records, Clock clock) {
        this.records = records;
        this.clock = clock;
    }

    /**
     * @param userId      quem enviou o pedido
     * @param key         valor do cabeçalho ({@link IdempotencyKeys#require})
     * @param operation   nome da operação em maiúsculas (ex.: {@code CREATE_PAYMENT_INTENT})
     * @param requestHash {@link CanonicalJson#requestHash}
     * @param action      o pedido; só corre se a chave for nova (ou tiver expirado)
     * @throws DomainException 422 {@code IDEMPOTENCY_KEY_REUSED}
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public IdempotentResponse execute(UUID userId, UUID key, String operation, String requestHash,
                                      Supplier<IdempotentResponse> action) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(operation, "operation");
        Objects.requireNonNull(requestHash, "requestHash");
        Instant now = clock.instant();
        List<?> inserted = entityManager.createNativeQuery(INSERT)
                .setParameter("id", Ids.newId())
                .setParameter("now", now)
                .setParameter("userId", userId)
                .setParameter("key", key)
                .setParameter("operation", operation)
                .setParameter("hash", requestHash)
                .setParameter("expiresAt", now.plus(TTL))
                .getResultList();
        IdempotencyRecord record = records.lock(userId, key)
                .orElseThrow(() -> new IllegalStateException("Idempotency record not visible after insert"));
        if (inserted.isEmpty()) {
            if (!record.getExpiresAt().isAfter(now)) {
                log.debug("Idempotency key expired, replacing operation={} recordId={}", operation, record.getId());
                record.restart(operation, requestHash, now, now.plus(TTL));
            } else if (!record.matches(operation, requestHash)) {
                throw DomainException.of(ErrorCodes.IDEMPOTENCY_KEY_REUSED,
                        "The Idempotency-Key was already used for a different request");
            } else if (record.getResponseStatus() == null) {
                // Não devia acontecer: a resposta é gravada na mesma transacção do registo.
                throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                        "The request with this Idempotency-Key is still being processed");
            } else {
                log.debug("Idempotent replay operation={} recordId={}", operation, record.getId());
                return record.response();
            }
        }
        IdempotentResponse response = Objects.requireNonNull(action.get(), "response");
        record.complete(response);
        records.saveAndFlush(record);
        return response;
    }
}
