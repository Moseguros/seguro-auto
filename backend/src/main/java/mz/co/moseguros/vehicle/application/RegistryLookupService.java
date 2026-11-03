package mz.co.moseguros.vehicle.application;

import java.util.Locale;

import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.vehicle.infrastructure.RegistryVehicleData;
import mz.co.moseguros.vehicle.infrastructure.VehicleRegistryPort;
import mz.co.moseguros.vehicle.infrastructure.VehicleRegistryUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Consulta de uma matrícula no registo automóvel para pré-preencher o formulário (S2-03 §5). Não
 * grava nada; o cliente confirma os dados.
 */
@Service
public class RegistryLookupService {

    private final VehicleRegistryPort registry;
    private final PlateParser plates;
    private final String source;

    public RegistryLookupService(VehicleRegistryPort registry, PlateParser plates,
                                 @Value("${moseguros.vehicle.registry.provider}") String provider) {
        this.registry = registry;
        this.plates = plates;
        this.source = provider.toUpperCase(Locale.ROOT);
    }

    /**
     * @throws DomainException {@code VALIDATION_ERROR} ({@code plate}/{@code INVALID_PLATE}),
     *                         {@code VEHICLE_REGISTRY_NOT_FOUND} (404) ou
     *                         {@code VEHICLE_REGISTRY_UNAVAILABLE} (503)
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    public RegistryResult lookup(String rawPlate) {
        Matricula plate;
        try {
            plate = plates.parse(rawPlate);
        } catch (DomainException e) {
            throw new FieldValidationException("plate", FieldErrorCodes.INVALID_PLATE, e.getMessage());
        }
        try {
            return registry.lookup(plate)
                    .map(data -> new RegistryResult(data, source))
                    .orElseThrow(() -> DomainException.of(ErrorCodes.VEHICLE_REGISTRY_NOT_FOUND,
                            "The plate is not in the vehicle registry"));
        } catch (VehicleRegistryUnavailableException e) {
            throw DomainException.of(ErrorCodes.VEHICLE_REGISTRY_UNAVAILABLE, "The vehicle registry is unavailable");
        }
    }

    /** Dados encontrados e a origem ({@code MOCK} até haver acesso ao INATRO). */
    public record RegistryResult(RegistryVehicleData data, String source) {
    }
}
