package mz.co.moseguros.vehicle;

import java.util.UUID;

/**
 * Impede que um veículo seja retirado (S2-03 §7.1; modelo transversal §1.1). O {@code vehicle} não
 * pode depender do {@code policy}: é o {@code policy} que implementa esta interface (S6-01: apólice
 * {@code ACTIVE} ou {@code SUSPENDED} com o veículo). Sem implementações, a remoção é sempre
 * permitida (D29).
 */
public interface VehicleRemovalGuard {

    /** true se o veículo não pode ser retirado agora. Corre no âmbito de quem chama, dentro da transacção do DELETE. */
    boolean blocksRemoval(UUID vehicleId);
}
