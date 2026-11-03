package mz.co.moseguros.rating.application;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.organization.ChargeRule;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.rating.application.provider.InternalRatingProvider;
import mz.co.moseguros.rating.application.provider.RatingRequest;
import mz.co.moseguros.rating.domain.QuoteRequestValidator;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.DriverInput;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.Errors;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.Terms;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.TermsInput;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.VehicleInput;
import mz.co.moseguros.rating.domain.tariff.EvaluationResult;
import mz.co.moseguros.rating.domain.tariff.ProductDefinition;
import mz.co.moseguros.rating.domain.tariff.RatedDriver;
import mz.co.moseguros.rating.domain.tariff.RatedVehicle;
import mz.co.moseguros.rating.domain.tariff.RatingRulesValidator;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simulador da tarifa para o gestor de produto (S3-02 §12): avalia uma versão da própria
 * seguradora (em qualquer estado, {@code CatalogApi.insurerVersion}) com a tarifa interna, mesmo
 * que a seguradora tarife por sistema próprio, e devolve a decomposição completa. Pode usar uma
 * tarifa e uns encargos ainda não gravados (os do editor). Não grava nada, não publica eventos e
 * não chama seguradoras externas.
 */
@Service
public class RatingSimulationService {

    private final CatalogApi catalog;
    private final OrganizationApi organizations;
    private final InternalRatingProvider provider;
    private final ReferenceValueLookup references;
    private final Clock clock;

    public RatingSimulationService(CatalogApi catalog, OrganizationApi organizations, InternalRatingProvider provider,
                                   ReferenceValueLookup references, Clock clock) {
        this.catalog = catalog;
        this.organizations = organizations;
        this.provider = provider;
        this.references = references;
        this.clock = clock;
    }

    /**
     * Pedido de simulação. O tipo de produto é o da versão.
     *
     * @param rating  tarifa ainda não gravada; {@code null} = a gravada
     * @param charges encargos ainda não gravados; {@code null} = os gravados
     */
    public record SimulationCommand(VehicleInput vehicle, DriverInput driver, Integer durationDays,
                                    LocalDate startDate, List<QuoteRequestValidator.CoverageInput> coverages,
                                    JsonNode rating, JsonNode charges) {
    }

    /**
     * @throws DomainException 404 {@code PRODUCT_VERSION_NOT_FOUND} (versão de outra seguradora), 400
     *                         {@code VALIDATION_ERROR} (pedido, tarifa ou encargos),
     *                         {@code RATING_RULES_TOO_LARGE} ou {@code VEHICLE_VALUE_REQUIRED}
     */
    @PreAuthorize("hasAuthority('rating:manage')")
    @Transactional(readOnly = true)
    public EvaluationResult simulate(UUID productVersionId, SimulationCommand command) {
        ProductVersionView version = catalog.insurerVersion(productVersionId);

        Errors errors = new Errors();
        LocalDate today = LocalDate.ofInstant(clock.instant(), QuoteRequestValidator.ZONE);
        RatedVehicle vehicle = QuoteRequestValidator.validateVehicle(command.vehicle(), null, clock.instant(), true,
                errors);
        RatedDriver driver = QuoteRequestValidator.validateDriver(command.driver(), references, today, errors);
        Terms terms = QuoteRequestValidator.validateTerms(new TermsInput(version.productType(), command.durationDays(),
                command.startDate(), command.coverages()), today, 0, false, errors);
        errors.throwIfAny();

        JsonNode rating = command.rating() == null || command.rating().isNull() ? null : command.rating();
        if (rating != null) {
            if (RatingRulesValidator.exceedsMaxSize(rating)) {
                throw DomainException.of(ErrorCodes.RATING_RULES_TOO_LARGE, "The tariff is larger than 256 KB");
            }
            requireValid(RatingRulesValidator.validate(rating, ProductDefinition.from(version)), "The tariff is not valid");
        } else {
            rating = RatingRulesService.ratingOf(version);
        }

        InsurerProfileView insurer = organizations.insurerProfile(version.insurerId())
                .orElseThrow(() -> DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_FOUND, "Insurer not found"));
        List<ChargeRule> charges;
        if (command.charges() == null || command.charges().isNull()) {
            charges = insurer.charges();
        } else {
            requireValid(organizations.validateCharges(command.charges()), "The charges are not valid");
            charges = organizations.parseCharges(command.charges());
        }

        QuoteRequestValidator.requireVehicleValue(terms, vehicle.commercialValue());
        RatingRequest request = new RatingRequest(Ids.newId(), version, insurer, vehicle, driver, terms.coverages(),
                terms.startDate(), terms.endDate(), terms.durationDays());
        return provider.evaluate(request, rating, charges);
    }

    private static void requireValid(List<FieldError> errors, String detail) {
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, detail).withProperty("errors", errors);
        }
    }
}
