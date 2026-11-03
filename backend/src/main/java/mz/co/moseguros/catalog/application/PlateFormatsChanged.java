package mz.co.moseguros.catalog.application;

/**
 * Evento interno do {@code catalog}: um formato de matrícula foi criado, alterado, activado ou
 * desactivado nesta instância. O {@link DbPlateFormatProvider} recarrega a cópia em memória depois
 * do commit.
 *
 * @param code código do formato alterado
 */
public record PlateFormatsChanged(String code) {
}
