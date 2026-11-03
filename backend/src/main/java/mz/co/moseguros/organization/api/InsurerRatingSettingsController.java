package mz.co.moseguros.organization.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mz.co.moseguros.organization.application.InsurerRatingSettingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Definições de tarifação da seguradora do utilizador (S3-01 §4): modo de tarifação (só leitura)
 * e encargos (impostos e taxas).
 */
@RestController
@RequestMapping("/api/v1/insurer/rating-settings")
@Tag(name = OrganizationTag.NAME, description = OrganizationTag.DESCRIPTION)
public class InsurerRatingSettingsController {

    private final InsurerRatingSettingsService service;

    public InsurerRatingSettingsController(InsurerRatingSettingsService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "getInsurerRatingSettings", summary = "Consulta as definições de tarifação",
            description = "Permissão rating:manage ou product:read, staff de uma seguradora (outro âmbito → 403 "
                    + "FORBIDDEN). Encargos pela ordem em que se calculam.")
    public InsurerRatingSettingsResponse get() {
        return InsurerRatingSettingsResponse.from(service.get());
    }

    @PutMapping
    @Operation(operationId = "updateInsurerRatingSettings", summary = "Altera os encargos da seguradora",
            description = "Permissão rating:manage. charges validado por charges-v1.json (SCHEMA_VIOLATION) e "
                    + "pelas regras DUPLICATE_CHARGE e VALUE_OUT_OF_RANGE (PERCENT acima de 100): 400 "
                    + "VALIDATION_ERROR com errors[].field = ponteiro JSON dentro de charges. version diferente → "
                    + "409 CONCURRENT_MODIFICATION. O modo de tarifação não se altera aqui.")
    public InsurerRatingSettingsResponse update(@Valid @RequestBody UpdateInsurerRatingSettingsRequest request) {
        return InsurerRatingSettingsResponse.from(service.update(request.charges(), request.version()));
    }
}
