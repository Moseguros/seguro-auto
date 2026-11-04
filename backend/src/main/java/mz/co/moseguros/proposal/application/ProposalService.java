package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.proposal.domain.CoverageLine;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionRepository;
import mz.co.moseguros.proposal.domain.PolicyholderSnapshot;
import mz.co.moseguros.proposal.domain.PolicyholderSource;
import mz.co.moseguros.proposal.domain.PremiumBreakdown;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.Proposal.CoverPeriod;
import mz.co.moseguros.proposal.domain.ProposalChannel;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.rating.QuoteView;
import mz.co.moseguros.rating.RatedDriver;
import mz.co.moseguros.rating.RatedVehicle;
import mz.co.moseguros.rating.RatingApi;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.numbering.BusinessNumberGenerator;
import mz.co.moseguros.shared.numbering.BusinessPrefix;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.vehicle.DriverSnapshot;
import mz.co.moseguros.vehicle.VehicleApi;
import mz.co.moseguros.vehicle.VehicleSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Criação de uma proposta a partir de uma cotação do cliente e alteração da data de início
 * (S4-02 §9). Corre no âmbito do cliente: o RLS do rating só lhe mostra as suas cotações e o do
 * vehicle os seus veículos e condutores.
 */
@Service
public class ProposalService {

    private static final Logger log = LoggerFactory.getLogger(ProposalService.class);

    private final ProposalRepository proposals;
    private final InspectionRepository inspections;
    private final ProposalQueryService queries;
    private final ProposalSystemLookups lookups;
    private final ProposalJson json;
    private final RatingApi rating;
    private final CatalogApi catalog;
    private final VehicleApi vehicles;
    private final IdentityApi identity;
    private final BusinessNumberGenerator numbers;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ProposalProperties properties;
    private final Clock clock;

    public ProposalService(ProposalRepository proposals, InspectionRepository inspections,
                           ProposalQueryService queries, ProposalSystemLookups lookups,
                           ProposalJson json, RatingApi rating, CatalogApi catalog, VehicleApi vehicles,
                           IdentityApi identity, BusinessNumberGenerator numbers, AuditService audit,
                           CurrentUser currentUser, ProposalProperties properties, Clock clock) {
        this.proposals = proposals;
        this.inspections = inspections;
        this.queries = queries;
        this.lookups = lookups;
        this.json = json;
        this.rating = rating;
        this.catalog = catalog;
        this.vehicles = vehicles;
        this.identity = identity;
        this.numbers = numbers;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Cria uma proposta {@code DRAFT} a partir de uma cotação do cliente (S4-02 §9, passos 1 a 9).
     *
     * @param channel {@code APP} ou {@code WEB}
     */
    @PreAuthorize("hasAuthority('self:proposals')")
    @Transactional
    public ProposalDetails create(UUID quoteId, ProposalChannel channel) {
        FieldErrors errors = new FieldErrors();
        if (quoteId == null) {
            errors.add("quoteId", FieldErrorCodes.REQUIRED, "The quote is required");
        }
        if (channel == null) {
            errors.add("channel", FieldErrorCodes.REQUIRED, "The channel is required");
        } else if (channel == ProposalChannel.BROKER) {
            errors.add("channel", FieldErrorCodes.NOT_ALLOWED, "A customer creates proposals from the app or the web");
        }
        errors.throwIfAny();
        UUID customerId = currentUser.id();

        // 1. Cotação do cliente, no seu âmbito.
        QuoteView quote = rating.quote(quoteId);
        if (!"CUSTOMER".equals(quote.origin()) || !customerId.equals(quote.customerUserId())) {
            throw DomainException.of(ErrorCodes.QUOTE_NOT_FOUND, "Quote not found");
        }
        // 2 a 4. Uma proposta por cotação, estado efectivo, seguradora activa.
        ProposalSystemLookups.InsurerRef insurer = usableQuote(quote);
        // 5. Perfil completo.
        CustomerView customer = completeCustomer(customerId);
        // 6. Snapshots e coerência com a cotação.
        Snapshots snapshots = snapshots(quote, customerId);
        // 7 e 8. Versão do produto e gravação, com o número e o uso da cotação na mesma transacção.
        Proposal proposal = persist(new NewProposal(quote, insurer, customerId, customerId, channel, null, null,
                snapshots.vehicle(), snapshots.driver(), policyholder(customer), customer.idDocNumber(),
                customer.nuit(), PolicyholderSource.PROFILE));

        // 9. Auditoria.
        audit.record(new AuditRecord(ActorType.CUSTOMER, customerId.toString(), proposal.getInsurerId(),
                "PROPOSAL.CREATE", "Proposal", proposal.getId().toString(), null, CreateState.of(proposal)));
        log.info("Customer {} created proposal {} from quote {}", customerId, proposal.getNumber(), quoteId);
        return queries.details(proposal);
    }

    /**
     * Nova data de início (S4-02 §9, {@code updateProposal}): mesma duração e prémio, tomador copiado
     * de novo do perfil; com o tomador preenchido pelo mediador ({@code policyholder_source = BROKER},
     * S4-06 §5) mantém-se o gravado.
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public ProposalDetails update(UUID proposalId, LocalDate startDate, Long version) {
        Proposal proposal = queries.accessible(proposalId);
        proposal.requireEditable();
        if (version == null) {
            throw new FieldValidationException("version", FieldErrorCodes.REQUIRED, "The version is required");
        }
        proposal.requireVersion(version);
        if (startDate == null) {
            throw new FieldValidationException("startDate", FieldErrorCodes.REQUIRED, "The start date is required");
        }
        requireStartDateInRange(startDate);
        UpdateState before = UpdateState.of(proposal);
        Instant now = clock.instant();
        CoverPeriod period = CoverPeriod.of(startDate, proposal.getDurationDays());
        if (proposal.getPolicyholderSource() == PolicyholderSource.BROKER) {
            // Tomador preenchido pelo mediador (S4-06 §5): mantém-se; só muda a data.
            proposal.reschedule(period, proposal.getPolicyholder(), proposal.getPolicyholderIdDocNumber(),
                    proposal.getPolicyholderNuit(), now, properties.expiry());
        } else {
            CustomerView customer = completeCustomer(proposal.getCustomerUserId());
            proposal.reschedule(period, json.policyholder(policyholder(customer)), customer.idDocNumber(),
                    customer.nuit(), now, properties.expiry());
        }
        proposals.saveAndFlush(proposal);
        audit.record(new AuditRecord(actorType(), currentUser.id().toString(), proposal.getInsurerId(),
                "PROPOSAL.UPDATE", "Proposal", proposal.getId().toString(), before, UpdateState.of(proposal)));
        return queries.details(proposal);
    }

    /**
     * Regras 2 a 4 do {@code createProposal} (S4-02 §9): uma proposta por cotação (409
     * {@code PROPOSAL_ALREADY_EXISTS}), cotação {@code OFFERED} ({@code QUOTE_NOT_AVAILABLE},
     * {@code QUOTE_ALREADY_USED}, {@code QUOTE_EXPIRED}) e seguradora activa ({@code INSURER_NOT_ACTIVE}).
     */
    ProposalSystemLookups.InsurerRef usableQuote(QuoteView quote) {
        proposals.findByQuoteId(quote.id()).ifPresent(existing -> {
            throw alreadyExists(existing.getId());
        });
        switch (quote.status()) {
            case NOT_AVAILABLE -> throw DomainException.of(ErrorCodes.QUOTE_NOT_AVAILABLE,
                    "The insurer made no offer for this quote");
            case USED -> throw DomainException.of(ErrorCodes.QUOTE_ALREADY_USED, "The quote was already used");
            case EXPIRED -> throw DomainException.of(ErrorCodes.QUOTE_EXPIRED, "The quote has expired");
            default -> {
                // OFFERED
            }
        }
        return lookups.insurer(quote.insurerId())
                .filter(ProposalSystemLookups.InsurerRef::active)
                .orElseThrow(() -> DomainException.of(ErrorCodes.INSURER_NOT_ACTIVE,
                        "The insurer is not accepting proposals"));
    }

    /**
     * Dados de uma proposta nova já verificados (cotação, seguradora, snapshots e tomador).
     *
     * @param idDocNumber documento do tomador em claro (fica cifrado)
     * @param nuit        NUIT do tomador em claro (fica cifrado)
     */
    record NewProposal(QuoteView quote, ProposalSystemLookups.InsurerRef insurer, UUID customerUserId,
                       UUID createdByUserId, ProposalChannel channel, UUID brokerOrgId, String brokerName,
                       VehicleSnapshot vehicle, DriverSnapshot driver, PolicyholderSnapshot policyholder,
                       String idDocNumber, String nuit, PolicyholderSource policyholderSource) {

        @Override
        public String toString() {
            return "NewProposal[quote=" + quote.id() + ", channel=" + channel + ", source=" + policyholderSource + "]";
        }
    }

    /**
     * Passos 7 e 8 do {@code createProposal} (S4-02 §9): coberturas e prémio da cotação, número,
     * gravação, vistoria e cotação {@code USED}, tudo na transacção de quem chama.
     */
    Proposal persist(NewProposal n) {
        QuoteView quote = n.quote();
        ProductVersionView version = catalog.version(quote.productVersionId());
        Map<String, CoverageView> catalogCoverages = version.coverages().stream()
                .collect(Collectors.toMap(c -> c.code().name(), Function.identity(), (a, b) -> a));
        List<CoverageLine> lines = ProposalJson.coverageLines(quote.coverages(), catalogCoverages);
        PremiumBreakdown premium = ProposalJson.premiumOf(quote);
        CoverPeriod period = CoverPeriod.of(quote.startDate(), quote.endDate());

        Instant now = clock.instant();
        Proposal proposal = Proposal.draft(new Proposal.Draft(numbers.next(BusinessPrefix.PROPOSAL), quote.id(),
                quote.insurerId(), n.insurer().displayName(), quote.productId(), quote.productVersionId(),
                version.productName(), ProductType.valueOf(quote.productType()), n.customerUserId(),
                n.createdByUserId(), n.channel(), n.brokerOrgId(), n.brokerName(), n.vehicle().vehicleId(),
                n.driver().driverId(), json.policyholder(n.policyholder()), n.idDocNumber(), n.nuit(),
                json.insuredSameAsPolicyholder(), json.vehicle(n.vehicle()), json.driver(n.driver()),
                lines, json.coverages(lines), json.premium(premium), premium.total().amount(), period,
                quote.durationDays()), n.policyholderSource(), now, properties.expiry());
        try {
            proposals.saveAndFlush(proposal);
        } catch (DataIntegrityViolationException e) {
            // Duas propostas em simultâneo para a mesma cotação: a unicidade de quote_id recusa a segunda.
            throw alreadyExists(null);
        }
        createInspectionIfRequired(proposal);
        rating.markUsed(quote.id());
        return proposal;
    }

    /**
     * Data de início entre hoje e hoje + {@code max-start-days} (Africa/Maputo; S4-02 §9).
     *
     * @throws FieldValidationException {@code START_DATE_OUT_OF_RANGE}
     */
    void requireStartDateInRange(LocalDate startDate) {
        LocalDate today = LocalDate.ofInstant(clock.instant(), Proposal.ZONE);
        if (startDate.isBefore(today) || startDate.isAfter(today.plusDays(properties.maxStartDays()))) {
            throw new FieldValidationException("startDate", FieldErrorCodes.START_DATE_OUT_OF_RANGE,
                    "The start date must be between today and today + " + properties.maxStartDays() + " days");
        }
    }

    /** Vistoria {@code PENDING} na mesma transacção, se a proposta tiver danos próprios (S4-03 §3, regra 1). */
    void createInspectionIfRequired(Proposal proposal) {
        if (proposal.isInspectionRequired()) {
            inspections.saveAndFlush(Inspection.pending(proposal));
        }
    }

    /** Actor das escritas sobre a proposta: o cliente ou o utilizador do mediador (S4-06). */
    ActorType actorType() {
        return currentUser.isCustomer() ? ActorType.CUSTOMER : ActorType.STAFF;
    }

    private CustomerView completeCustomer(UUID customerId) {
        CustomerView customer = identity.customer(customerId).orElse(null);
        if (customer == null || !customer.profileComplete()) {
            throw DomainException.of(ErrorCodes.PROFILE_INCOMPLETE, "Complete the profile before the proposal");
        }
        return customer;
    }

    private record Snapshots(VehicleSnapshot vehicle, DriverSnapshot driver) {
    }

    /**
     * Snapshots actuais do veículo e do condutor (no âmbito do cliente) e a verificação de que os
     * campos usados na tarifa não mudaram desde a cotação (S4-02 §5).
     */
    private Snapshots snapshots(QuoteView quote, UUID customerId) {
        RatedVehicle rated = quote.vehicle();
        RatedDriver ratedDriver = quote.driver();
        if (rated == null || rated.vehicleId() == null
                || vehicles.findOwned(rated.vehicleId(), customerId).isEmpty()) {
            throw DomainException.of(ErrorCodes.VEHICLE_NOT_FOUND, "Vehicle not found");
        }
        if (ratedDriver == null || ratedDriver.driverId() == null) {
            throw DomainException.of(ErrorCodes.DRIVER_NOT_FOUND, "Driver not found");
        }
        DriverSnapshot driver = vehicles.driverSnapshot(ratedDriver.driverId());
        if (!customerId.equals(driver.ownerUserId())) {
            throw DomainException.of(ErrorCodes.DRIVER_NOT_FOUND, "Driver not found");
        }
        VehicleSnapshot vehicle = vehicles.snapshot(rated.vehicleId());
        List<String> changed = changedFields(rated, vehicle, ratedDriver, driver);
        if (!changed.isEmpty()) {
            throw DomainException.of(ErrorCodes.QUOTE_DATA_CHANGED,
                    "The vehicle or driver changed after the quote: " + changed).withProperty("fields", changed);
        }
        return new Snapshots(vehicle, driver);
    }

    /** Campos usados na tarifa que mudaram, pela ordem do S4-02 §5 ({@code vehicle.commercialValue}…). */
    static List<String> changedFields(RatedVehicle rated, VehicleSnapshot vehicle, RatedDriver ratedDriver,
                                      DriverSnapshot driver) {
        Map<String, Boolean> same = new LinkedHashMap<>();
        same.put("vehicle.category", Objects.equals(rated.category(), vehicle.category()));
        same.put("vehicle.usage", Objects.equals(rated.usage(), vehicle.usage()));
        same.put("vehicle.year", rated.year() == vehicle.year());
        same.put("vehicle.engineCc", Objects.equals(rated.engineCc(), vehicle.engineCc()));
        same.put("vehicle.seats", rated.seats() != null && rated.seats() == vehicle.seats());
        same.put("vehicle.grossWeightKg", Objects.equals(rated.grossWeightKg(), vehicle.grossWeightKg()));
        same.put("vehicle.provinceOfCirculation",
                Objects.equals(rated.provinceOfCirculation(), vehicle.provinceOfCirculation()));
        same.put("vehicle.commercialValue", Objects.equals(rated.commercialValue(),
                vehicle.commercialValue() == null ? null : vehicle.commercialValue().amount()));
        same.put("vehicle.hasTracker", rated.hasTracker() == vehicle.hasTracker());
        same.put("driver.birthDate", Objects.equals(ratedDriver.birthDate(), driver.birthDate()));
        same.put("driver.licenceIssueDate", Objects.equals(ratedDriver.licenceIssueDate(), driver.licenceIssueDate()));
        same.put("driver.licenceCategories", sorted(ratedDriver.licenceCategories())
                .equals(sorted(driver.licenceCategories())));
        return same.entrySet().stream().filter(e -> !e.getValue()).map(Map.Entry::getKey).toList();
    }

    private static List<String> sorted(List<String> values) {
        return values == null ? List.of() : values.stream().sorted().toList();
    }

    /** Tomador copiado do perfil, com o documento e o NUIT só mascarados. */
    static PolicyholderSnapshot policyholder(CustomerView c) {
        Msisdn msisdn = c.msisdn();
        return new PolicyholderSnapshot(c.fullName(), c.birthDate(), c.idDocType(), maskDocument(c.idDocNumber()),
                c.nuit() == null ? null : Nuit.of(c.nuit()).masked(), c.address(), c.province(),
                msisdn == null ? null : msisdn.e164(), c.email(), c.preferredLanguage());
    }

    /** Como no S1-06: só os últimos 4 caracteres visíveis. */
    static String maskDocument(String value) {
        if (value == null) {
            return null;
        }
        int keep = Math.min(4, value.length());
        return "*".repeat(value.length() - keep) + value.substring(value.length() - keep);
    }

    private static DomainException alreadyExists(UUID proposalId) {
        DomainException e = DomainException.of(ErrorCodes.PROPOSAL_ALREADY_EXISTS,
                "A proposal already exists for this quote");
        return proposalId == null ? e : e.withProperty("proposalId", proposalId);
    }

    /** Estado auditado na criação (sem dados pessoais). */
    public record CreateState(String number, UUID quoteId, UUID insurerId, String status, long totalAmount,
                              String channel) {

        static CreateState of(Proposal p) {
            return new CreateState(p.getNumber(), p.getQuoteId(), p.getInsurerId(), p.getStatus().name(),
                    p.getTotalAmount(), p.getChannel().name());
        }
    }

    /** Estado auditado na alteração da data de início. */
    public record UpdateState(Instant startAt, Instant endAt, int contentRevision, Instant expiresAt) {

        static UpdateState of(Proposal p) {
            return new UpdateState(p.getStartAt(), p.getEndAt(), p.getContentRevision(), p.getExpiresAt());
        }
    }
}
