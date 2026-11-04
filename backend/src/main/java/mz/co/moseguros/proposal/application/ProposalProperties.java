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
 * Configuração das propostas ({@code moseguros.proposal}, S4-02 §2), validada no arranque (1–365).
 * O S4-05 acrescenta {@code decision-sla-days} e {@code decision-alert-days}.
 *
 * @param expiryDays   dias de calendário sem acção do cliente até {@code EXPIRED} (15)
 * @param maxStartDays a data de início vai no máximo até hoje + este número de dias, em Maputo (60)
 */
@Validated
@ConfigurationProperties("moseguros.proposal")
public record ProposalProperties(
        @DefaultValue("15") @Min(1) @Max(365) int expiryDays,
        @DefaultValue("60") @Min(1) @Max(365) int maxStartDays) {

    /** Prazo de expiração. */
    public Duration expiry() {
        return Duration.ofDays(expiryDays);
    }

    /** Activa as propriedades. */
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ProposalProperties.class)
    static class Config {
    }
}
