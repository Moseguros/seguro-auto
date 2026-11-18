package mz.co.moseguros.shared.idempotency;

import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Resposta de um pedido idempotente, tal como foi devolvida da primeira vez (S5-02 §5).
 *
 * @param status     estado HTTP (ex.: 202)
 * @param body       corpo da resposta em JSON (ex.: o {@code IntentView} da altura)
 * @param resourceId id do recurso criado ou devolvido, se houver
 */
public record IdempotentResponse(int status, JsonNode body, UUID resourceId) {
}
