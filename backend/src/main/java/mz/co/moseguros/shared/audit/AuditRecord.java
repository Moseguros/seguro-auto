package mz.co.moseguros.shared.audit;

import java.util.Objects;
import java.util.UUID;

/**
 * Pedido de registo de um evento de auditoria.
 *
 * <p>{@code beforeState}/{@code afterState} são gravados tal como o objecto serializado,
 * sem mascaramento: quem chama usa um DTO de auditoria com os valores sensíveis já
 * mascarados (nunca segredos nem documentos em claro).
 *
 * @param actorType   obrigatório
 * @param actorId     {@code sub} do utilizador, ou nome do job/serviço (obrigatório, ≤ 64)
 * @param insurerId   {@code null} quando não se aplica
 * @param action      verbo em maiúsculas com ponto: {@code POLICY.ISSUE} (obrigatório, ≤ 80)
 * @param entityType  nome da entidade: {@code Policy} (obrigatório, ≤ 60)
 * @param entityId    id da entidade em texto (obrigatório, ≤ 64)
 * @param beforeState {@code null} na criação
 * @param afterState  {@code null} na eliminação lógica
 */
public record AuditRecord(
        ActorType actorType,
        String actorId,
        UUID insurerId,
        String action,
        String entityType,
        String entityId,
        Object beforeState,
        Object afterState) {

    public AuditRecord {
        Objects.requireNonNull(actorType, "actorType");
        requireText(actorId, "actorId", 64);
        requireText(action, "action", 80);
        requireText(entityType, "entityType", 60);
        requireText(entityId, "entityId", 64);
    }

    private static void requireText(String value, String name, int max) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required");
        }
        if (value.length() > max) {
            throw new IllegalArgumentException(name + " must have at most " + max + " characters");
        }
    }
}
