package mz.co.moseguros.verification.api;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.policy.CertificateStatus;

/**
 * Se o certificado mostrado (papel ou ecrã) ainda é o actual da apólice (S6-03 §2): só nas consultas por
 * código.
 */
@Schema(enumAsRef = true)
public enum CertificateCheckStatus {
    CURRENT,
    REVOKED;

    static CertificateCheckStatus of(CertificateStatus status) {
        if (status == null) {
            return null;
        }
        return status == CertificateStatus.VALID ? CURRENT : REVOKED;
    }
}
