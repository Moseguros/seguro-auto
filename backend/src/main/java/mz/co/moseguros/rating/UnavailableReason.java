package mz.co.moseguros.rating;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Motivo de uma cotação {@code NOT_AVAILABLE} (S3-01 §11). Não são códigos de erro da API (não
 * estão em {@code ErrorCodes}): a web e a app traduzem-nos em {@code quoteUnavailable.<REASON>}
 * (S3-04, S3-05). Os {@code INSURER_*}, {@code EXTERNAL_INVALID_RESPONSE} e
 * {@code EVALUATION_ERROR} são usados pela tarifação externa (S3-02).
 */
@Schema(enumAsRef = true, description = "Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)")
public enum UnavailableReason {
    /** A versão do produto não tem tarifa interna ({@code rules.rating}). */
    TARIFF_MISSING,
    /** A tarifa gravada não se consegue ler. */
    TARIFF_INVALID,
    /** Foi pedida uma cobertura que a versão não tem. */
    COVERAGE_NOT_OFFERED,
    /** Uma cobertura escolhida exige outra que não foi escolhida. */
    COVERAGE_REQUIREMENTS_NOT_MET,
    /** O capital pedido é maior do que o maior capital oferecido. */
    CAPITAL_NOT_OFFERED,
    /** Uma cobertura sobre o valor do veículo sem o valor comercial do veículo. */
    VEHICLE_VALUE_REQUIRED,
    /** A tarifa não tem factor para a duração pedida. */
    DURATION_NOT_RATED,
    /** Nenhuma linha da base ou de um factor se aplica ao pedido. */
    NO_MATCHING_ROW,
    INSURER_TIMEOUT,
    INSURER_UNAVAILABLE,
    INSURER_DECLINED,
    EXTERNAL_INVALID_RESPONSE,
    EVALUATION_ERROR
}
