package mz.co.moseguros.document;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.shared.security.CurrentUser;

/**
 * Quem pode ler os ficheiros de um tipo de dono (ADR-005; modelo transversal §1.1). O módulo
 * {@code document} guarda e serve os ficheiros mas não conhece as regras de negócio: cada módulo
 * dono implementa esta interface como bean Spring no seu pacote {@code application} (ex.:
 * {@code VehicleFileAccessPolicy} para {@code VEHICLE}).
 *
 * <p>Cada tipo de dono tem exactamente uma política: a aplicação não arranca com
 * {@link #ownerTypes()} vazio nem com dois beans para o mesmo tipo.
 */
public interface FileAccessPolicy {

    /** Tipos de dono que esta política trata (não vazio). */
    Set<String> ownerTypes();

    /** Equivale a {@code ownerTypes().contains(ownerType)}. */
    default boolean supports(String ownerType) {
        return ownerTypes().contains(ownerType);
    }

    /**
     * {@code true} se o utilizador pode ler os ficheiros do dono indicado. Corre no âmbito de quem
     * pede (o RLS aplica-se), numa transacção só de leitura. O pedido pode ser anónimo: chamar
     * {@link CurrentUser#isAuthenticated()} antes de {@code id()} ou {@code permissions()}.
     * {@code false} dá 404 {@code FILE_NOT_FOUND} (nunca 403).
     */
    boolean canRead(String ownerType, UUID ownerId, CurrentUser user);
}
