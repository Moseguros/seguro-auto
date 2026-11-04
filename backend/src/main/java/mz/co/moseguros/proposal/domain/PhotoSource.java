package mz.co.moseguros.proposal.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/** Origem da fotografia indicada pelo cliente (S4-03 §1): câmara da app ou ficheiro enviado. */
@Schema(enumAsRef = true)
public enum PhotoSource {
    CAMERA,
    UPLOAD
}
