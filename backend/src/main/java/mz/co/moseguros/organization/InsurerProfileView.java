package mz.co.moseguros.organization;

import java.util.List;
import java.util.UUID;

/**
 * Perfil de uma seguradora para outros módulos ({@link OrganizationApi#insurerProfile}, S3-01 §3).
 * Nunca traz segredos: {@code ratingSecretRef} é só a referência no cofre.
 *
 * @param displayName nome comercial, se existir, senão a denominação social
 * @param logoFileId  {@code null} sem logótipo
 * @param charges     encargos pela ordem configurada
 */
public record InsurerProfileView(UUID insurerId, String displayName, String shortCode, UUID logoFileId,
                                 OrganizationStatus status, RatingMode ratingMode,
                                 String ratingEndpointUrl, String ratingSecretRef,
                                 List<ChargeRule> charges) {

    public InsurerProfileView {
        charges = charges == null ? List.of() : List.copyOf(charges);
    }
}
