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
 * Configuração das propostas ({@code moseguros.proposal}, S4-02 §2 e S4-05 §5), validada no arranque
 * (1–365; o alerta tem de vir antes do fim do prazo de decisão).
 *
 * @param expiryDays        dias de calendário sem acção do cliente até {@code EXPIRED} (15)
 * @param maxStartDays      a data de início vai no máximo até hoje + este número de dias, em Maputo (60)
 * @param decisionSlaDays   dias de calendário que a seguradora tem para decidir uma proposta em análise
 *                          (15; sem aceitação tácita: o fim do prazo é só um alerta)
 * @param decisionAlertDays dias até ao alerta aos subscritores (10); menor que {@code decisionSlaDays}
 */
@Validated
@ConfigurationProperties("moseguros.proposal")
public record ProposalProperties(
        @DefaultValue("15") @Min(1) @Max(365) int expiryDays,
        @DefaultValue("60") @Min(1) @Max(365) int maxStartDays,
        @DefaultValue("15") @Min(1) @Max(365) int decisionSlaDays,
        @DefaultValue("10") @Min(1) @Max(365) int decisionAlertDays) {

    public ProposalProperties {
        if (decisionAlertDays >= decisionSlaDays) {
            throw new IllegalArgumentException("moseguros.proposal.decision-alert-days (" + decisionAlertDays
                    + ") must be less than decision-sla-days (" + decisionSlaDays + ")");
        }
    }

    /** Prazo de expiração. */
    public Duration expiry() {
        return Duration.ofDays(expiryDays);
    }

    /** Prazo de decisão da seguradora, a partir da entrada em análise. */
    public Duration decisionSla() {
        return Duration.ofDays(decisionSlaDays);
    }

    /** Alerta aos subscritores, a partir da entrada em análise. */
    public Duration decisionAlert() {
        return Duration.ofDays(decisionAlertDays);
    }

    /** Activa as propriedades. */
    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ProposalProperties.class)
    static class Config {
    }
}
