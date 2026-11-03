package mz.co.moseguros.rating.application;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.rating.domain.tariff.ProductDefinition;
import mz.co.moseguros.rating.domain.tariff.RatingRulesValidator;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tarifa interna das versões de produto da seguradora do utilizador (S3-01 §5). A tarifa vive em
 * {@code catalog.product_version.rules.rating}: lê-se com {@code CatalogApi.insurerVersion} (RLS:
 * só a seguradora dona; outra → 404 {@code PRODUCT_VERSION_NOT_FOUND}) e grava-se com
 * {@code CatalogApi.updateRatingRules} (só {@code DRAFT}).
 */
@Service
public class RatingRulesService {

    static final String RATING = "rating";

    private final CatalogApi catalog;
    private final AuditService audit;
    private final CurrentUser currentUser;

    public RatingRulesService(CatalogApi catalog, AuditService audit, CurrentUser currentUser) {
        this.catalog = catalog;
        this.audit = audit;
        this.currentUser = currentUser;
    }

    /**
     * Versão e a sua tarifa.
     *
     * @param rating {@code null} quando a versão não tem tarifa
     */
    public record RatingRulesDetails(ProductVersionView version, JsonNode rating) {

        public boolean editable() {
            return "DRAFT".equals(version.status());
        }

        public boolean configured() {
            return rating != null;
        }
    }

    @PreAuthorize("hasAnyAuthority('rating:manage','product:read')")
    @Transactional(readOnly = true)
    public RatingRulesDetails get(UUID productVersionId) {
        return details(catalog.insurerVersion(productVersionId));
    }

    /**
     * Valida e grava a tarifa de uma versão {@code DRAFT}, como chegou (sem reordenar linhas).
     *
     * @throws DomainException 400 {@code RATING_RULES_TOO_LARGE} ou {@code VALIDATION_ERROR} (com
     *                         {@code errors[]}), 404 {@code PRODUCT_VERSION_NOT_FOUND}, 409
     *                         {@code PRODUCT_VERSION_NOT_EDITABLE} ou {@code CONCURRENT_MODIFICATION}
     */
    @PreAuthorize("hasAuthority('rating:manage')")
    @Transactional
    public RatingRulesDetails update(UUID productVersionId, JsonNode rating, long expectedVersion) {
        requireWithinSize(rating);
        ProductVersionView version = catalog.insurerVersion(productVersionId);
        if (!"DRAFT".equals(version.status())) {
            throw DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_EDITABLE, "Only a DRAFT version can be changed");
        }
        List<FieldError> errors = RatingRulesValidator.validate(rating, ProductDefinition.from(version));
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "The tariff is not valid")
                    .withProperty("errors", errors);
        }
        JsonNode before = ratingOf(version);
        catalog.updateRatingRules(productVersionId, rating, expectedVersion);
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), version.insurerId(),
                "RATING_RULES.UPDATE", "ProductVersion", productVersionId.toString(), before, rating));
        return details(catalog.insurerVersion(productVersionId));
    }

    /**
     * Valida a tarifa contra as coberturas da versão (qualquer estado), sem gravar.
     *
     * @return todos os erros; vazia = válida
     */
    @PreAuthorize("hasAuthority('rating:manage')")
    @Transactional(readOnly = true)
    public List<FieldError> validate(UUID productVersionId, JsonNode rating) {
        requireWithinSize(rating);
        ProductVersionView version = catalog.insurerVersion(productVersionId);
        return RatingRulesValidator.validate(rating, ProductDefinition.from(version));
    }

    private static void requireWithinSize(JsonNode rating) {
        if (RatingRulesValidator.exceedsMaxSize(rating)) {
            throw DomainException.of(ErrorCodes.RATING_RULES_TOO_LARGE, "The tariff is larger than 256 KB");
        }
    }

    private static RatingRulesDetails details(ProductVersionView version) {
        return new RatingRulesDetails(version, ratingOf(version));
    }

    static JsonNode ratingOf(ProductVersionView version) {
        JsonNode rules = version.rules();
        JsonNode rating = rules == null ? null : rules.get(RATING);
        return rating == null || rating.isNull() ? null : rating;
    }
}
