package mz.co.moseguros.proposal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Modo da vistoria ({@code proposal.inspection.mode}, S4-03 §1): fotografias tiradas pelo cliente
 * (ou pelo mediador) ou vistoria presencial, marcada fora da plataforma.
 */
@Schema(enumAsRef = true)
public enum InspectionMode {
    SELF_PHOTOS,
    ON_SITE
}
