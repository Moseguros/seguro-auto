package mz.co.moseguros.vehicle.infrastructure;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.VehicleCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

/**
 * Registo automóvel simulado ({@code moseguros.vehicle.registry.provider=mock}), com dados fictícios
 * e fixos (S2-03 §5; tabela também em {@code infra/README.md}). {@value #UNAVAILABLE_PLATE} simula
 * a indisponibilidade; qualquer outra matrícula não tem registo.
 */
@Component
@ConditionalOnProperty(name = "moseguros.vehicle.registry.provider", havingValue = "mock")
public class MockVehicleRegistry implements VehicleRegistryPort {

    private static final Logger log = LoggerFactory.getLogger(MockVehicleRegistry.class);

    /** Matrícula que lança {@link VehicleRegistryUnavailableException}. */
    public static final String UNAVAILABLE_PLATE = "AAA-999-MC";

    static final Map<String, RegistryVehicleData> VEHICLES = Stream.of(
                    new RegistryVehicleData("AAA-100-MC", "JTDBR32E720012345", "Toyota", "Corolla", 2018, 1598,
                            VehicleCategory.LIGHT_PASSENGER, 5, null),
                    new RegistryVehicleData("AAA-200-MC", "AHTFR22G106012345", "Toyota", "Hilux", 2015, 2982,
                            VehicleCategory.LIGHT_MIXED, 5, null),
                    new RegistryVehicleData("AAA-300-MC", "WDB9340321L123456", "Mercedes-Benz", "Actros", 2012, 11946,
                            VehicleCategory.HEAVY, 2, 40000),
                    new RegistryVehicleData("AAA-400-MC", "LTMKD0791L5123456", "Honda", "CG 125", 2020, 124,
                            VehicleCategory.MOTORCYCLE, 2, null),
                    new RegistryVehicleData("AAA-500-MC", "JTFSS22P900123456", "Toyota", "Quantum", 2017, 2494,
                            VehicleCategory.BUS, 15, 3300),
                    new RegistryVehicleData("MLA-12-34", "ADNJ720000E123456", "Nissan", "Hardbody", 2004, 2389,
                            VehicleCategory.LIGHT_MIXED, 5, null))
            .collect(Collectors.toUnmodifiableMap(RegistryVehicleData::plate, Function.identity()));

    public MockVehicleRegistry(Environment environment) {
        if (environment.acceptsProfiles(Profiles.of("staging", "prod"))) {
            log.warn("The vehicle registry (INATRO) is simulated (moseguros.vehicle.registry.provider=mock): "
                    + "no vehicle is checked against the real registry");
        }
    }

    @Override
    public Optional<RegistryVehicleData> lookup(Matricula plate) {
        if (UNAVAILABLE_PLATE.equals(plate.value())) {
            throw new VehicleRegistryUnavailableException("Simulated registry unavailability");
        }
        return Optional.ofNullable(VEHICLES.get(plate.value()));
    }
}
