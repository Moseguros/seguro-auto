package mz.co.moseguros.vehicle.application;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.security.JwtClaims;
import mz.co.moseguros.vehicle.domain.VehicleRepository;
import org.springframework.stereotype.Component;

/**
 * Quem lê os ficheiros dos veículos (livrete; S2-03 §4): só o cliente dono do veículo, em qualquer
 * estado do veículo. Seguradoras e plataforma não lêem (ver "Questões" da ficha). A consulta corre
 * no âmbito de quem pede (RLS).
 */
@Component
public class VehicleFileAccessPolicy implements FileAccessPolicy {

    private final VehicleRepository vehicles;

    public VehicleFileAccessPolicy(VehicleRepository vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public Set<String> ownerTypes() {
        return Set.of(RegistrationDocService.OWNER_TYPE);
    }

    @Override
    public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
        return user.isAuthenticated()
                && JwtClaims.TYPE_CUSTOMER.equals(user.type())
                && vehicles.existsByIdAndOwnerUserId(ownerId, user.id());
    }
}
