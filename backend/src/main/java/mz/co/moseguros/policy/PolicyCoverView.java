package mz.co.moseguros.policy;

import java.time.Instant;
import java.util.UUID;

/**
 * Cobertura de uma matrícula ou de um certificado, para a verificação pública (S6-01 §9; S6-03). Nunca
 * tem dados pessoais (nem nome nem contactos do tomador).
 *
 * @param plate             matrícula formatada
 * @param certificateId     o certificado {@code VALID} da apólice (ou o do código, em
 *                          {@link PolicyApi#findByCertificateToken}); {@code null} se não houver
 * @param certificateStatus o estado desse certificado
 */
public record PolicyCoverView(UUID policyId, String policyNumber, UUID insurerId, String plate,
                              String vehicleMake, String vehicleModel, CoverageType coverageType,
                              PolicyStatus status, Instant startAt, Instant endAt, UUID certificateId,
                              String certificateNumber, CertificateStatus certificateStatus) {
}
