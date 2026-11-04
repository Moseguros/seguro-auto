package mz.co.moseguros.rating.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationStatus;
import mz.co.moseguros.rating.QuoteCreated;
import mz.co.moseguros.rating.QuoteResult;
import mz.co.moseguros.rating.QuotedCharge;
import mz.co.moseguros.rating.application.QuoteDetails.QuoteRequestDetails;
import mz.co.moseguros.rating.application.QuoteEvaluationOrchestrator.Evaluation;
import mz.co.moseguros.rating.application.provider.ProviderResult;
import mz.co.moseguros.rating.application.provider.RatingRequest;
import mz.co.moseguros.rating.domain.Quote;
import mz.co.moseguros.rating.domain.QuoteRepository;
import mz.co.moseguros.rating.domain.QuoteRequest;
import mz.co.moseguros.rating.domain.QuoteRequestRepository;
import mz.co.moseguros.rating.domain.QuoteRequestValidator;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.DriverInput;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.Errors;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.Terms;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.TermsInput;
import mz.co.moseguros.rating.domain.QuoteRequestValidator.VehicleInput;
import mz.co.moseguros.rating.domain.RatingProviderKind;
import mz.co.moseguros.rating.domain.StoredBreakdown;
import mz.co.moseguros.rating.domain.tariff.RatedDriver;
import mz.co.moseguros.rating.domain.tariff.RatedVehicle;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.vehicle.DriverSnapshot;
import mz.co.moseguros.vehicle.VehicleApi;
import mz.co.moseguros.vehicle.VehicleSnapshot;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Pedido de cotações (S3-02 §3 a §6): do cliente, com um veículo e um condutor seus, e do mediador,
 * com dados escritos à mão e o telemóvel do cliente.
 *
 * <p>Três fases, cada uma com a sua transacção ({@link TransactionTemplate}); este serviço não é
 * {@code @Transactional} e nenhuma ligação à BD fica presa durante as chamadas HTTP:
 * <ol>
 *   <li>leitura, no âmbito de quem pede: snapshots, versões publicadas compatíveis e em vigor na
 *       data de início, seguradoras activas;</li>
 *   <li>avaliação em paralelo ({@link QuoteEvaluationOrchestrator}), sem BD;</li>
 *   <li>gravação, no âmbito de quem pede: o pedido, uma cotação por versão e um
 *       {@link QuoteCreated} por cotação.</li>
 * </ol>
 * As cotações não são escritas sobre apólice, pagamento, sinistro ou reclamação: não se auditam.
 */
@Service
public class QuoteService {

    static final Duration RATE_LIMIT_WINDOW = Duration.ofHours(1);

    private final VehicleApi vehicles;
    private final CatalogApi catalog;
    private final OrganizationApi organizations;
    private final IdentityApi identity;
    private final QuoteRequestRepository requests;
    private final QuoteRepository quotes;
    private final QuoteEvaluationOrchestrator orchestrator;
    private final QuoteSnapshots snapshots;
    private final RatingProperties properties;
    private final PlateParser plates;
    private final ReferenceValueLookup references;
    private final CurrentUser currentUser;
    private final ApplicationEventPublisher events;
    private final TransactionTemplate readTx;
    private final TransactionTemplate writeTx;
    private final Clock clock;

    public QuoteService(VehicleApi vehicles, CatalogApi catalog, OrganizationApi organizations, IdentityApi identity,
                        QuoteRequestRepository requests, QuoteRepository quotes,
                        QuoteEvaluationOrchestrator orchestrator, QuoteSnapshots snapshots,
                        RatingProperties properties, PlateParser plates, ReferenceValueLookup references,
                        CurrentUser currentUser, ApplicationEventPublisher events,
                        PlatformTransactionManager transactionManager, Clock clock) {
        this.vehicles = vehicles;
        this.catalog = catalog;
        this.organizations = organizations;
        this.identity = identity;
        this.requests = requests;
        this.quotes = quotes;
        this.orchestrator = orchestrator;
        this.snapshots = snapshots;
        this.properties = properties;
        this.plates = plates;
        this.references = references;
        this.currentUser = currentUser;
        this.events = events;
        this.readTx = new TransactionTemplate(transactionManager);
        this.readTx.setReadOnly(true);
        this.writeTx = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    /** Pedido do cliente ({@code POST /quotes}). */
    public record CustomerQuoteCommand(UUID vehicleId, UUID driverId, TermsInput terms) {
    }

    /** Pedido do mediador ({@code POST /broker/quotes}). */
    public record BrokerQuoteCommand(String clientMsisdn, VehicleInput vehicle, DriverInput driver, TermsInput terms) {
    }

    /**
     * Cotações para um veículo e um condutor do cliente.
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR} (regras 1 a 5) ou
     *                         {@code VEHICLE_VALUE_REQUIRED}, 404 {@code VEHICLE_NOT_FOUND} ou
     *                         {@code DRIVER_NOT_FOUND}, 429 {@code QUOTE_RATE_LIMITED}
     */
    @PreAuthorize("hasAuthority('self:quotes')")
    public QuoteRequestDetails createForCustomer(CustomerQuoteCommand command) {
        UUID customerId = currentUser.id();
        Errors errors = new Errors();
        if (command.vehicleId() == null) {
            errors.required("vehicleId");
        }
        if (command.driverId() == null) {
            errors.required("driverId");
        }
        Terms terms = QuoteRequestValidator.validateTerms(command.terms(), today(), properties.getMaxStartDaysAhead(),
                true, errors);
        errors.throwIfAny();
        checkRateLimit(customerId);

        Phase1 read = readTx.execute(status -> {
            if (vehicles.findOwned(command.vehicleId(), customerId).isEmpty()) {
                throw DomainException.of(ErrorCodes.VEHICLE_NOT_FOUND, "Vehicle not found");
            }
            DriverSnapshot driver = vehicles.driverSnapshot(command.driverId());
            if (!customerId.equals(driver.ownerUserId())) {
                throw DomainException.of(ErrorCodes.DRIVER_NOT_FOUND, "Driver not found");
            }
            VehicleSnapshot v = vehicles.snapshot(command.vehicleId());
            RatedVehicle vehicle = new RatedVehicle(v.vehicleId(), v.plate(), v.make(), v.model(), v.year(),
                    v.engineCc(), v.category(), v.usage(), v.seats(), v.grossWeightKg(), v.provinceOfCirculation(),
                    v.commercialValue() == null ? null : v.commercialValue().amount(), v.hasTracker());
            RatedDriver rated = new RatedDriver(driver.driverId(), driver.birthDate(), driver.licenceIssueDate(),
                    driver.licenceCategories() == null ? List.of() : List.copyOf(driver.licenceCategories()));
            QuoteRequestValidator.requireVehicleValue(terms, vehicle.commercialValue());
            return new Phase1(vehicle, rated, ratingRequests(terms, vehicle, rated));
        });

        Evaluation evaluation = orchestrator.evaluate(read.requests());

        return writeTx.execute(status -> save(QuoteRequest.byCustomer(customerId, terms.productType(),
                snapshots.vehicle(read.vehicle()), snapshots.driver(read.driver()),
                snapshots.coverages(terms.coverages()), terms.startDate(), terms.durationDays(), terms.endDate(),
                evaluation.evaluationMs()), read, terms, evaluation));
    }

    /**
     * Cotações pedidas por um corretor ou agente em nome do cliente (telemóvel). A conta
     * {@code CUSTOMER} do cliente é encontrada ou criada pelo telemóvel
     * ({@link IdentityApi#findOrCreateCustomerByMsisdn}); o cliente não recebe mensagem nenhuma.
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR} ou {@code VEHICLE_VALUE_REQUIRED}, 403
     *                         {@code FORBIDDEN} sem organização, 429 {@code QUOTE_RATE_LIMITED}
     */
    @PreAuthorize("hasAuthority('quote:create-for-client')")
    public QuoteRequestDetails createForBroker(BrokerQuoteCommand command) {
        UUID requesterId = currentUser.id();
        UUID orgId = currentUser.orgId();
        if (orgId == null) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only broker or agent staff can quote for a client");
        }
        Errors errors = new Errors();
        Msisdn msisdn = null;
        if (command.clientMsisdn() == null || command.clientMsisdn().isBlank()) {
            errors.required("clientMsisdn");
        } else {
            try {
                msisdn = Msisdn.of(command.clientMsisdn());
            } catch (DomainException e) {
                errors.add("clientMsisdn", FieldErrorCodes.INVALID_MSISDN, "Invalid Mozambican mobile number");
            }
        }
        LocalDate today = today();
        RatedVehicle vehicle = QuoteRequestValidator.validateVehicle(command.vehicle(), plates, clock.instant(), false,
                errors);
        RatedDriver driver = QuoteRequestValidator.validateDriver(command.driver(), references, today, errors);
        Terms terms = QuoteRequestValidator.validateTerms(command.terms(), today, properties.getMaxStartDaysAhead(),
                true, errors);
        errors.throwIfAny();
        QuoteRequestValidator.requireVehicleValue(terms, vehicle.commercialValue());
        checkRateLimit(requesterId);

        Phase1 read = readTx.execute(status -> new Phase1(vehicle, driver, ratingRequests(terms, vehicle, driver)));
        Evaluation evaluation = orchestrator.evaluate(read.requests());

        UUID customerId = identity.findOrCreateCustomerByMsisdn(msisdn);
        Msisdn clientMsisdn = msisdn;
        return writeTx.execute(status -> save(QuoteRequest.byBroker(customerId, clientMsisdn, requesterId, orgId,
                terms.productType(), snapshots.vehicle(vehicle), snapshots.driver(driver),
                snapshots.coverages(terms.coverages()), terms.startDate(), terms.durationDays(), terms.endDate(),
                evaluation.evaluationMs()), read, terms, evaluation));
    }

    /** Resultado da fase 1. */
    private record Phase1(RatedVehicle vehicle, RatedDriver driver, List<RatingRequest> requests) {
    }

    /**
     * Regra 7: no máximo {@code max-requests-per-hour} pedidos do mesmo utilizador na última hora;
     * senão 429 {@code QUOTE_RATE_LIMITED} com os segundos até o pedido mais antigo da janela sair dela.
     */
    private void checkRateLimit(UUID requesterId) {
        Instant now = clock.instant();
        Instant since = now.minus(RATE_LIMIT_WINDOW);
        int max = properties.getMaxRequestsPerHour();
        Long retryAfter = readTx.execute(status -> {
            if (requests.countByRequestedByUserIdAndCreatedAtAfter(requesterId, since) < max) {
                return null;
            }
            Instant oldest = requests.findFirstByRequestedByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(requesterId,
                    since).map(QuoteRequest::getCreatedAt).orElse(now);
            Duration wait = Duration.between(now, oldest.plus(RATE_LIMIT_WINDOW));
            return Math.max(1, (wait.toMillis() + 999) / 1000);
        });
        if (retryAfter != null) {
            throw DomainException.of(ErrorCodes.QUOTE_RATE_LIMITED, "Too many quote requests in the last hour")
                    .withProperty(DomainException.RETRY_AFTER_SECONDS, retryAfter);
        }
    }

    /**
     * Fase 1, passos 2 a 5: versões publicadas compatíveis, em vigor na data de início, de
     * seguradoras activas, cada uma com o seu {@code quoteId}.
     */
    private List<RatingRequest> ratingRequests(Terms terms, RatedVehicle vehicle, RatedDriver driver) {
        List<ProductVersionView> versions = catalog.findPublishedVersions(vehicle.category(), vehicle.usage(),
                        terms.productType()).stream()
                .filter(v -> v.validFrom() == null || !terms.startDate().isBefore(v.validFrom()))
                .filter(v -> v.validTo() == null || !terms.startDate().isAfter(v.validTo()))
                .toList();
        if (versions.isEmpty()) {
            return List.of();
        }
        Set<UUID> insurerIds = versions.stream().map(ProductVersionView::insurerId).collect(Collectors.toSet());
        Map<UUID, InsurerProfileView> profiles = organizations.insurerProfiles(insurerIds);
        List<RatingRequest> result = new ArrayList<>();
        for (ProductVersionView v : versions) {
            InsurerProfileView insurer = profiles.get(v.insurerId());
            if (insurer == null || insurer.status() != OrganizationStatus.ACTIVE) {
                continue;
            }
            result.add(new RatingRequest(Ids.newId(), v, insurer, vehicle, driver, terms.coverages(),
                    terms.startDate(), terms.endDate(), terms.durationDays()));
        }
        return result;
    }

    /** Fase 3: grava o pedido e as cotações e publica um {@link QuoteCreated} por cotação. */
    private QuoteRequestDetails save(QuoteRequest request, Phase1 read, Terms terms, Evaluation evaluation) {
        requests.save(request);
        Instant now = clock.instant();
        Duration validity = properties.getQuoteValidity();
        List<QuoteDetails> details = new ArrayList<>();
        for (RatingRequest rating : read.requests()) {
            ProviderResult provided = evaluation.of(rating);
            QuoteResult result = provided.result();
            ProductVersionView version = rating.version();
            RatingProviderKind provider = RatingProviderKind.valueOf(result.provider());
            Quote quote;
            if (result.isOffered()) {
                long chargesAmount = result.charges().stream().mapToLong(QuotedCharge::amount).sum();
                quote = Quote.offered(rating.quoteId(), request, version.insurerId(), version.productId(),
                        version.id(), provider, provided.breakdown(), result.commercialPremiumAmount(), chargesAmount,
                        result.totalAmount(), result.externalReference(), now, validity, provided.ratingMs());
            } else {
                quote = Quote.notAvailable(rating.quoteId(), request, version.insurerId(), version.productId(),
                        version.id(), provider, result.unavailableReason(),
                        snapshots.detail(result.unavailableDetail()), provided.ratingMs());
            }
            quotes.save(quote);
            details.add(new QuoteDetails(quote, quote.getStatus(), rating.insurer(), version,
                    provided.breakdown() == null ? null : StoredBreakdown.parse(provided.breakdown())));
        }
        quotes.flush();
        for (QuoteDetails d : details) {
            Quote q = d.quote();
            events.publishEvent(new QuoteCreated(Ids.newId(), now, q.getId(), request.getId(), q.getInsurerId(),
                    q.getProductVersionId(), request.getOrigin().name(), q.getStatus().name(), q.getTotalAmount()));
        }
        return new QuoteRequestDetails(request, read.vehicle(), read.driver(), terms.coverages(), details);
    }

    private LocalDate today() {
        return LocalDate.ofInstant(clock.instant(), QuoteRequestValidator.ZONE);
    }
}
