package mz.co.moseguros.proposal.application;

import java.time.Duration;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuração da vistoria ({@code moseguros.proposal.inspection}, S4-03 §2), validada no arranque.
 *
 * @param maxExtraPhotos  fotografias {@code EXTRA} activas por vistoria (6)
 * @param maxPhotoAgeDays {@code takenAt} no máximo este número de dias antes do envio (7): a app pode
 *                        enviar mais tarde, sem rede
 */
@Validated
@ConfigurationProperties("moseguros.proposal.inspection")
public record InspectionProperties(
        @DefaultValue("6") @Min(0) @Max(50) int maxExtraPhotos,
        @DefaultValue("7") @Min(1) @Max(365) int maxPhotoAgeDays) {

    /** Uma fotografia com {@code takenAt} além de agora + esta margem está "no futuro". */
    public static final Duration FUTURE_TOLERANCE = Duration.ofMinutes(5);

    /** Idade máxima da fotografia. */
    public Duration maxPhotoAge() {
        return Duration.ofDays(maxPhotoAgeDays);
    }

    /** Activa as propriedades. */
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(InspectionProperties.class)
    static class Config {
    }
}
