package mz.co.moseguros.verification.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/** O que foi consultado (S6-03 §1): a matrícula ou o código do QR do certificado. */
@Schema(enumAsRef = true)
public enum SubjectType {
    PLATE,
    TOKEN
}
