package mz.co.moseguros.proposal.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.shared.json.JsonSchemas;

/**
 * Regras de aceitação automática de uma versão de produto ({@code rules.underwriting}, versão 1;
 * S4-05 §1), já validadas pelo {@code schemas/underwriting-v1.json}. Montantes em centavos.
 *
 * <p>Os valores por defeito ({@link #DEFAULTS}, provisórios: ver "Questões" da ficha) vêm do mesmo
 * ficheiro que o {@code catalog} devolve em {@code getUnderwritingRules} com {@code isDefault = true}
 * ({@value #DEFAULTS_RESOURCE}): os dois módulos não se conhecem e partilham só o recurso.
 *
 * @param source {@link Source#PRODUCT} (regras da versão) ou {@link Source#DEFAULT}
 */
public record UnderwritingRules(int schemaVersion, boolean enabled, long simplifiedKycMaxPremium,
                                boolean liabilityRequireAllDeclarationsNegative, long ownDamageMaxVehicleValue,
                                boolean ownDamageRequireAllDeclarationsNegative, Source source) {

    /** Origem das regras ({@code rulesSource} do {@code underwriting_result}). */
    public enum Source { PRODUCT, DEFAULT }

    /** Schema das regras. */
    public static final String SCHEMA = "underwriting-v1.json";
    /** Recurso com os valores por defeito (o mesmo do {@code catalog}). */
    public static final String DEFAULTS_RESOURCE = "/schemas/underwriting-v1-defaults.json";

    /** Valores por defeito: 10 000 MT de limiar do KYC simplificado e 1 500 000 MT de valor do veículo. */
    public static final UnderwritingRules DEFAULTS = parse(readDefaults(), Source.DEFAULT);

    /**
     * Regras de {@code rules.underwriting}; sem a chave (ou {@code null}) → {@link #DEFAULTS}.
     *
     * @throws IllegalArgumentException se o JSON gravado não cumprir o schema (o {@code catalog}
     *                                  valida-o na gravação: só uma alteração manual o permite)
     */
    public static UnderwritingRules fromProductRules(JsonNode productRules) {
        JsonNode underwriting = productRules == null ? null : productRules.get("underwriting");
        if (underwriting == null || underwriting.isNull()) {
            return DEFAULTS;
        }
        if (!JsonSchemas.validate(SCHEMA, underwriting).isEmpty()) {
            throw new IllegalArgumentException("Invalid underwriting rules");
        }
        return parse(underwriting, Source.PRODUCT);
    }

    /**
     * Regras de um JSON já válido.
     *
     * @param source origem a registar
     */
    public static UnderwritingRules parse(JsonNode json, Source source) {
        JsonNode auto = json.path("autoAccept");
        return new UnderwritingRules(json.path("schemaVersion").asInt(),
                auto.path("enabled").asBoolean(),
                auto.path("simplifiedKycMaxPremium").path("amount").asLong(),
                auto.path("liability").path("requireAllDeclarationsNegative").asBoolean(),
                auto.path("ownDamage").path("maxVehicleValue").path("amount").asLong(),
                auto.path("ownDamage").path("requireAllDeclarationsNegative").asBoolean(),
                source);
    }

    /** Regras desligadas (aceitação sempre manual), com a origem indicada. */
    public static UnderwritingRules disabled(Source source) {
        return new UnderwritingRules(DEFAULTS.schemaVersion(), false, DEFAULTS.simplifiedKycMaxPremium(),
                DEFAULTS.liabilityRequireAllDeclarationsNegative(), DEFAULTS.ownDamageMaxVehicleValue(),
                DEFAULTS.ownDamageRequireAllDeclarationsNegative(), source);
    }

    /** O KYC simplificado basta: total estritamente menor do que o limiar. */
    public boolean simplifiedKycSuffices(long totalAmount) {
        return totalAmount < simplifiedKycMaxPremium;
    }

    /** Com estas regras, falta verificar o KYC para a aceitação automática. */
    public boolean kycRequired(boolean kycVerified, long totalAmount) {
        return !kycVerified && !simplifiedKycSuffices(totalAmount);
    }

    private static JsonNode readDefaults() {
        try (InputStream in = UnderwritingRules.class.getResourceAsStream(DEFAULTS_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + DEFAULTS_RESOURCE);
            }
            return new ObjectMapper().readTree(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
