package mz.co.moseguros.catalog.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.catalog.application.ProductVersionService.Editing;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.catalog.domain.ProductVersionRepository;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.json.JsonSchemas;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de aceitação de uma versão de produto da seguradora do utilizador (S4-05 §2), guardadas na
 * chave {@code underwriting} de {@code catalog.product_version.rules}. O {@code catalog} não conhece o
 * {@code proposal}: só valida o JSON pelo {@code schemas/underwriting-v1.json} e grava-o. O
 * {@code proposal} lê-o com {@code CatalogApi.version(id).rules()}.
 *
 * <p>A versão lê-se pelo repositório do próprio módulo (o {@code CatalogApi.version} só vê versões
 * publicadas): versão de outra seguradora ou inexistente → 404 {@code PRODUCT_VERSION_NOT_FOUND}.
 */
@Service
public class UnderwritingRulesService {

    /** Chave reservada em {@code rules} (S2-04). */
    public static final String UNDERWRITING = "underwriting";
    /** Schema das regras. */
    public static final String SCHEMA = "underwriting-v1.json";
    /** Valores por defeito, também lidos pelo {@code proposal} ({@code UnderwritingRules.DEFAULTS}). */
    public static final String DEFAULTS_RESOURCE = "/schemas/underwriting-v1-defaults.json";

    private static final JsonNode DEFAULTS = readDefaults();

    private final ProductVersionService versions;
    private final ProductVersionRepository repository;

    public UnderwritingRulesService(ProductVersionService versions, ProductVersionRepository repository) {
        this.versions = versions;
        this.repository = repository;
    }

    /**
     * Regras da versão e se são as por defeito.
     *
     * @param version {@code version} da versão do produto (para o {@code PUT})
     */
    public record UnderwritingRulesDetails(JsonNode rules, boolean isDefault, long version) {
    }

    /** {@code getUnderwritingRules}: as regras gravadas ou, sem elas, os valores por defeito. */
    @PreAuthorize("hasAuthority('product:read')")
    @Transactional(readOnly = true)
    public UnderwritingRulesDetails get(UUID versionId) {
        return details(versions.load(versionId));
    }

    /**
     * {@code updateUnderwritingRules}: versão visível (404), {@code DRAFT} (409
     * {@code PRODUCT_VERSION_NOT_EDITABLE}), produto não retirado (409 {@code PRODUCT_RETIRED}),
     * {@code version} igual (409 {@code CONCURRENT_MODIFICATION}) e o JSON válido pelo schema (400
     * {@code VALIDATION_ERROR} com {@code errors[].field = rules.<caminho>} e {@code INVALID_RULES}).
     * Grava {@code rules.underwriting}, incrementa a {@code version} e audita
     * {@code PRODUCT_VERSION.UNDERWRITING_UPDATE} (antes e depois).
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public UnderwritingRulesDetails update(UUID versionId, JsonNode rules, Long expectedVersion) {
        if (versionId == null) {
            throw notFound();
        }
        versions.load(versionId);
        if (expectedVersion == null) {
            throw new FieldValidationException("version", FieldErrorCodes.REQUIRED, "The version is required");
        }
        Editing editing = versions.beginEdit(versionId, expectedVersion, false);
        List<FieldError> errors = validate(rules);
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "The underwriting rules are not valid")
                    .withProperty("errors", errors);
        }
        ProductVersion version = editing.version();
        JsonNode before = stored(version);
        version.replaceUnderwritingRules(rules);
        repository.flush();
        if (version.getVersion() == expectedVersion) {
            versions.forceIncrement(version); // regras iguais: a escrita conta na mesma
        }
        versions.audit(version, "PRODUCT_VERSION.UNDERWRITING_UPDATE", before, rules);
        return details(version);
    }

    /**
     * Erros do JSON pelo schema: um por violação, com {@code field = rules} (raiz) ou
     * {@code rules.<a>.<b>} e o código {@code INVALID_RULES}. Vazia = válido.
     */
    public static List<FieldError> validate(JsonNode rules) {
        if (rules == null || rules.isNull() || rules.isMissingNode()) {
            return List.of(new FieldError("rules", FieldErrorCodes.REQUIRED, "The rules are required"));
        }
        return JsonSchemas.validate(SCHEMA, rules).stream()
                .map(v -> new FieldError(field(v.pointer()), FieldErrorCodes.INVALID_RULES, v.message()))
                .toList();
    }

    /** Ponteiro JSON ({@code /autoAccept/enabled}) → campo do pedido ({@code rules.autoAccept.enabled}). */
    static String field(String pointer) {
        if (pointer == null || pointer.isEmpty() || "/".equals(pointer)) {
            return "rules";
        }
        StringBuilder field = new StringBuilder("rules");
        for (String part : pointer.substring(1).split("/", -1)) {
            String token = part.replace("~1", "/").replace("~0", "~");
            if (!token.isEmpty() && token.chars().allMatch(Character::isDigit)) {
                field.append('[').append(token).append(']');
            } else {
                field.append('.').append(token);
            }
        }
        return field.toString();
    }

    /** Cópia dos valores por defeito (S4-05 §1). */
    public static JsonNode defaults() {
        return DEFAULTS.deepCopy();
    }

    private static UnderwritingRulesDetails details(ProductVersion version) {
        JsonNode stored = stored(version);
        return stored == null ? new UnderwritingRulesDetails(defaults(), true, version.getVersion())
                : new UnderwritingRulesDetails(stored, false, version.getVersion());
    }

    private static JsonNode stored(ProductVersion version) {
        JsonNode rules = version.getRules();
        JsonNode underwriting = rules == null ? null : rules.get(UNDERWRITING);
        return underwriting == null || underwriting.isNull() ? null : underwriting;
    }

    private static JsonNode readDefaults() {
        try (InputStream in = UnderwritingRulesService.class.getResourceAsStream(DEFAULTS_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException("Missing " + DEFAULTS_RESOURCE);
            }
            return new ObjectMapper().readTree(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static DomainException notFound() {
        return DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_FOUND, "Product version not found");
    }
}
