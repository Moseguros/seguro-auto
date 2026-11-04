package mz.co.moseguros.rating.api;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.rating.application.RatingSimulationService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simulador da tarifa (S3-02 §12) para o gestor de produto da seguradora dona da versão.
 */
@RestController
@Tag(name = RatingTag.NAME, description = RatingTag.DESCRIPTION)
public class RatingSimulationController {

    private final RatingSimulationService service;
    private final ObjectMapper mapper;

    public RatingSimulationController(RatingSimulationService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/api/v1/insurer/product-versions/{id}/rating/simulate")
    @Operation(operationId = "simulateRating", summary = "Simula a tarifa de uma versão de produto",
            description = "Permissão rating:manage. Versões em qualquer estado da própria seguradora (outra → 404 "
                    + "PRODUCT_VERSION_NOT_FOUND). Usa sempre a tarifa interna: a gravada ou rating (validada como em "
                    + "updateRatingRules), com os encargos gravados ou charges. Devolve a decomposição completa. Não "
                    + "grava nada nem chama seguradoras externas.")
    public RatingSimulationResponse simulate(@PathVariable UUID id, @RequestBody SimulateRatingRequest request) {
        return RatingSimulationResponse.from(service.simulate(id, request.toCommand()), mapper);
    }
}
