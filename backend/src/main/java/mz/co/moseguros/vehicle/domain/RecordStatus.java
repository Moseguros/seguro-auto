package mz.co.moseguros.vehicle.domain;

/**
 * Estado de um veículo ou condutor do cliente. Retirar não apaga a linha: passa a {@link #REMOVED}
 * com {@code removed_at}.
 */
public enum RecordStatus {
    ACTIVE,
    REMOVED
}
