package mz.co.moseguros.policy;

import io.swagger.v3.oas.annotations.media.Schema;

/** Estado de um certificado (S6-01 §3): {@link #REVOKED} a partir do S7. */
@Schema(enumAsRef = true)
public enum CertificateStatus {
    VALID,
    REVOKED
}
