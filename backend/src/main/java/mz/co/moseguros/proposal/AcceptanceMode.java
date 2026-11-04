package mz.co.moseguros.proposal;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Como a proposta foi aceite ({@code acceptance_mode}, S4-05 §4): {@link #AUTOMATIC} pelas regras de
 * aceitação da versão do produto (na submissão ou ao aprovar a vistoria), {@link #MANUAL} pelo
 * subscritor.
 */
@Schema(enumAsRef = true)
public enum AcceptanceMode {
    AUTOMATIC,
    MANUAL
}
