package mz.co.moseguros.policy;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Tipo de renovação (S6-01 regra 4): produto {@code TEMPORARY} → {@link #FIXED_TERM}; os outros →
 * {@link #ANNUAL_RENEWABLE}. No pacote público porque faz parte do {@link PolicyView}.
 */
@Schema(enumAsRef = true)
public enum RenewalType {
    ANNUAL_RENEWABLE,
    FIXED_TERM
}
