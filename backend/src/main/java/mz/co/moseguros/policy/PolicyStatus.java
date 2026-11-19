package mz.co.moseguros.policy;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Estados da apólice (S6-01 §3). Nesta tarefa só há {@link #ACTIVE} (emissão); as transições chegam no
 * S7-01 (máquina de estados).
 */
@Schema(enumAsRef = true) // um só tipo nos clientes gerados
public enum PolicyStatus {
    ACTIVE,
    SUSPENDED,
    CANCELLED,
    EXPIRED,
    LAPSED
}
