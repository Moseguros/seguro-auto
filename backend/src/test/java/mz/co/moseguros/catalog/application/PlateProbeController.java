package mz.co.moseguros.catalog.application;

import io.swagger.v3.oas.annotations.Hidden;
import mz.co.moseguros.shared.domain.Matricula;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pedido de teste com uma matrícula (perfil {@code test}, fora do contrato): o corpo é lido com o
 * {@code PlateParser} e os formatos activos, como num pedido real (S2-03 cria os seus).
 */
@Hidden
@Profile("test")
@RestController
@RequestMapping("/api/v1/test-probes/plates")
public class PlateProbeController {

    @PostMapping
    public Result parse(@RequestBody Request request) {
        return new Result(request.plate().value(), request.plate().formatCode());
    }

    public record Request(Matricula plate) {
    }

    public record Result(String plate, String formatCode) {
    }
}
