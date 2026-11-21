package mz.co.moseguros.policy.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tipos da linha temporal da apólice (S6-01 §3). {@code DOCUMENTS_READY} é gravado pelo S6-02; o S7
 * acrescenta valores (com uma migração nova da constraint).
 */
@Schema(enumAsRef = true)
public enum PolicyEventType {
    ISSUED,
    CERTIFICATE_ISSUED,
    DOCUMENTS_READY
}
