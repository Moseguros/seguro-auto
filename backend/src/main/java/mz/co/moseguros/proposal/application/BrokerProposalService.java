package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.PersonalData;
import mz.co.moseguros.identity.ValidPersonalData;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.Inspection;
import mz.co.moseguros.proposal.domain.InspectionRepository;
import mz.co.moseguros.proposal.domain.InspectionStatus;
import mz.co.moseguros.proposal.domain.PolicyholderSnapshot;
import mz.co.moseguros.proposal.domain.PolicyholderSource;
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
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.vehicle.NewDriver;
import mz.co.moseguros.vehicle.NewVehicle;
import mz.co.moseguros.vehicle.RegisteredVehicle;
import mz.co.moseguros.vehicle.VehicleApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Propostas criadas pelo mediador (corretor ou agente) em nome do cliente (S4-06): criação a partir da
 * cotação do mediador, com o registo do veículo e do condutor na conta do cliente e, se o perfil do
 * cliente estiver incompleto, o tomador preenchido pelo mediador; lista da organização; alteração da
 * data de início e do tomador. Corre no âmbito {@code ORG} do mediador (RLS por {@code broker_org_id}).
 *
 * <p>Os logs têm ids e números de negócio; nunca o documento, o NUIT, a carta nem o telemóvel completo.
 */
@Service
public class BrokerProposalService {

    private static final Logger log = LoggerFactory.getLogger(BrokerProposalService.class);
    private static final Set<String> BROKER_ORG_TYPES = Set.of("BROKER", "AGENT");

    private final ProposalRepository proposals;
    private final InspectionRepository inspections;
    private final ProposalService proposalService;
    private final ProposalQueryService queries;
    private final ProposalSystemLookups lookups;
    private final ProposalJson json;
    private final ProposalNotifier notifier;
    private final RatingApi rating;
    private final VehicleApi vehicles;
    private final IdentityApi identity;
    private final PlateParser plates;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ProposalProperties properties;
    private final Clock clock;

    public BrokerProposalService(ProposalRepository proposals, InspectionRepository inspections,
                                 ProposalService proposalService, ProposalQueryService queries,
                                 ProposalSystemLookups lookups, ProposalJson json, ProposalNotifier notifier,
                                 RatingApi rating, VehicleApi vehicles, IdentityApi identity, PlateParser plates,
                                 AuditService audit, CurrentUser currentUser, ProposalProperties properties,
                                 Clock clock) {
        this.proposals = proposals;
        this.inspections = inspections;
        this.proposalService = proposalService;
        this.queries = queries;
        this.lookups = lookups;
        this.json = json;
        this.notifier = notifier;
        this.rating = rating;
        this.vehicles = vehicles;
        this.identity = identity;
        this.plates = plates;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
        this.clock = clock;
    }

    /** Pedido de {@code createProposalForClient}; {@code policyholder} só com o perfil do cliente incompleto. */
    public record CreateCommand(UUID quoteId, NewVehicle vehicle, NewDriver driver, PersonalData policyholder) {
    }

    /** Linha da lista do mediador, com o estado da vistoria ({@code null} sem vistoria). */
    public record Row(Proposal proposal, InspectionStatus inspectionStatus) {
    }

    /**
     * {@code createProposalForClient} (S4-06 §5), numa só transacção: qualquer erro desfaz o veículo, o
     * condutor, a proposta e o uso da cotação.
     */
    @PreAuthorize("hasAuthority('proposal:create-for-client')")
    @Transactional
    public ProposalDetails create(CreateCommand command) {
        ProposalSystemLookups.BrokerRef broker = requireBroker();
        if (command.quoteId() == null) {
            throw new FieldValidationException("quoteId", FieldErrorCodes.REQUIRED, "The quote is required");
        }
        // 1. Cotação do mediador, no seu âmbito.
        QuoteView quote = rating.quote(command.quoteId());
        if (!"BROKER".equals(quote.origin()) || !broker.id().equals(quote.requestedByOrgId())) {
            throw quoteNotFound();
        }
        // 2. Uma proposta por cotação, estado efectivo, seguradora activa (S4-02).
        ProposalSystemLookups.InsurerRef insurer = proposalService.usableQuote(quote);
        // 3. Veículo e condutor, com o risco tarifado.
        FieldErrors errors = new FieldErrors();
        if (command.vehicle() == null) {
            errors.add("vehicle", FieldErrorCodes.REQUIRED, "The vehicle is required");
        }
        if (command.driver() == null) {
            errors.add("driver", FieldErrorCodes.REQUIRED, "The driver is required");
        }
        errors.throwIfAny();
        List<String> changed = changedFields(quote.vehicle(), command.vehicle(), quote.driver(), command.driver());
        if (!changed.isEmpty()) {
            throw DomainException.of(ErrorCodes.QUOTE_DATA_CHANGED,
                    "The vehicle or driver differ from the quote: " + changed).withProperty("fields", changed);
        }
        // 4. Conta do cliente (encontrada ou criada no pedido da cotação, S3-02).
        UUID customerId = quote.customerUserId();
        CustomerView customer = customerId == null ? null : identity.customer(customerId).orElse(null);
        if (customer == null) {
            throw quoteNotFound();
        }
        if (!customer.accountActive()) {
            throw DomainException.of(ErrorCodes.CLIENT_ACCOUNT_DISABLED, "The customer's account is disabled");
        }
        // 6 (antes do registo, para nada ser escrito sem tomador). Tomador: perfil completo ou o do mediador.
        Holder holder = holder(customer, command.policyholder());
        // 5. Veículo e condutor na conta do cliente.
        RegisteredVehicle registered = vehicles.registerForCustomer(customerId, command.vehicle(), command.driver());
        // 7. Gravação no canal BROKER e cotação USED.
        Proposal proposal = proposalService.persist(new ProposalService.NewProposal(quote, insurer, customerId,
                currentUser.id(), ProposalChannel.BROKER, broker.id(), broker.displayName(), registered.vehicle(),
                registered.driver(), holder.snapshot(), holder.idDocNumber(), holder.nuit(), holder.source()));
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), proposal.getInsurerId(),
                "PROPOSAL.CREATE", "Proposal", proposal.getId().toString(), null, BrokerCreateState.of(proposal,
                holder.snapshot())));
        log.info("Broker {} (user {}) created proposal {} from quote {} for customer {} (policyholder from {})",
                broker.id(), currentUser.id(), proposal.getNumber(), quote.id(), customerId, holder.source());
        // 8. E-mail ao cliente, se tiver e-mail verificado (depois do commit).
        notifier.createdByBroker(proposal);
        return queries.details(proposal);
    }

    /**
     * {@code listBrokerProposals}: propostas da organização do mediador (de todos os seus utilizadores).
     *
     * @param q número exacto ou parte do nome do tomador (sem distinguir maiúsculas)
     */
    @PreAuthorize("hasAuthority('proposal:create-for-client')")
    @Transactional(readOnly = true)
    public Page<Row> list(Collection<ProposalStatus> statuses, String q, Pageable pageable) {
        ProposalSystemLookups.BrokerRef broker = requireBroker();
        Page<Proposal> page = proposals.findAll(specification(broker.id(), statuses, q), pageable);
        List<UUID> withInspection = page.getContent().stream().filter(Proposal::isInspectionRequired)
                .map(Proposal::getId).toList();
        Map<UUID, InspectionStatus> inspectionByProposal = withInspection.isEmpty() ? Map.of()
                : inspections.findByProposalIdIn(withInspection).stream()
                .collect(Collectors.toMap(Inspection::getProposalId, Inspection::getStatus));
        return page.map(p -> new Row(p, inspectionByProposal.get(p.getId())));
    }

    /**
     * {@code updateBrokerProposal} (S4-06 §5): nova data de início e/ou o tomador preenchido pelo
     * mediador. Com o tomador do perfil ({@code PROFILE}), {@code policyholder} → 400 {@code NOT_ALLOWED} e
     * o tomador é copiado de novo do perfil (como no S4-02). O veículo e o condutor não mudam.
     */
    @PreAuthorize("hasAuthority('proposal:create-for-client')")
    @Transactional
    public ProposalDetails update(UUID proposalId, LocalDate startDate, PersonalData policyholder, Long version) {
        ProposalSystemLookups.BrokerRef broker = requireBroker();
        Proposal proposal = queries.accessible(proposalId);
        if (!broker.id().equals(proposal.getBrokerOrgId())) {
            throw ProposalQueryService.notFound();
        }
        proposal.requireEditable();
        if (version == null) {
            throw new FieldValidationException("version", FieldErrorCodes.REQUIRED, "The version is required");
        }
        proposal.requireVersion(version);
        if (policyholder != null && proposal.getPolicyholderSource() != PolicyholderSource.BROKER) {
            throw new FieldValidationException("policyholder", FieldErrorCodes.NOT_ALLOWED,
                    "The policyholder comes from the customer's profile");
        }
        if (startDate == null && policyholder == null) {
            throw new FieldValidationException("startDate", FieldErrorCodes.REQUIRED,
                    "Send a new start date or policyholder");
        }
        if (startDate != null) {
            proposalService.requireStartDateInRange(startDate);
        }
        CoverPeriod period = startDate == null ? CoverPeriod.of(proposal.startDate(), proposal.endDate())
                : CoverPeriod.of(startDate, proposal.getDurationDays());

        ProposalService.UpdateState before = ProposalService.UpdateState.of(proposal);
        Instant now = clock.instant();
        if (proposal.getPolicyholderSource() == PolicyholderSource.PROFILE) {
            CustomerView customer = identity.customer(proposal.getCustomerUserId())
                    .filter(CustomerView::profileComplete)
                    .orElseThrow(() -> DomainException.of(ErrorCodes.PROFILE_INCOMPLETE,
                            "Complete the profile before the proposal"));
            proposal.reschedule(period, json.policyholder(ProposalService.policyholder(customer)),
                    customer.idDocNumber(), customer.nuit(), now, properties.expiry());
        } else if (policyholder != null) {
            PolicyholderSnapshot current = json.policyholder(proposal.getPolicyholder());
            ValidPersonalData valid = identity.validatePersonalData(policyholder, current.idDocType(), true, true,
                    "policyholder.");
            String idDocNumber = valid.idDocNumber() == null ? proposal.getPolicyholderIdDocNumber()
                    : valid.idDocNumber();
            String nuit = valid.nuit() == null ? proposal.getPolicyholderNuit() : valid.nuit();
            PolicyholderSnapshot snapshot = snapshot(valid, idDocNumber, nuit, current.msisdn(), current.email(),
                    current.preferredLanguage());
            proposal.reschedule(period, json.policyholder(snapshot), idDocNumber, nuit, now, properties.expiry());
        } else {
            proposal.reschedule(period, proposal.getPolicyholder(), proposal.getPolicyholderIdDocNumber(),
                    proposal.getPolicyholderNuit(), now, properties.expiry());
        }
        proposals.saveAndFlush(proposal);
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("startAt", proposal.getStartAt());
        after.put("endAt", proposal.getEndAt());
        after.put("contentRevision", proposal.getContentRevision());
        after.put("expiresAt", proposal.getExpiresAt());
        after.put("policyholderChanged", policyholder != null);
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), proposal.getInsurerId(),
                "PROPOSAL.UPDATE", "Proposal", proposal.getId().toString(), before, after));
        return queries.details(proposal);
    }

    /**
     * Mediador actual: âmbito {@code ORG} de um corretor ou agente activo (S4-06 §4).
     *
     * @throws DomainException 403 {@code FORBIDDEN}
     */
    private ProposalSystemLookups.BrokerRef requireBroker() {
        UUID orgId = currentUser.orgId();
        if (orgId == null || !BROKER_ORG_TYPES.contains(currentUser.orgType())) {
            throw forbidden();
        }
        return lookups.broker(orgId).filter(ProposalSystemLookups.BrokerRef::active).orElseThrow(
                BrokerProposalService::forbidden);
    }

    private static DomainException forbidden() {
        return DomainException.of(ErrorCodes.FORBIDDEN, "Only broker or agent staff can propose for a client");
    }

    private static DomainException quoteNotFound() {
        return DomainException.of(ErrorCodes.QUOTE_NOT_FOUND, "Quote not found");
    }

    /** Tomador da proposta, os valores em claro (cifrados na proposta) e a origem. */
    private record Holder(PolicyholderSnapshot snapshot, String idDocNumber, String nuit, PolicyholderSource source) {

        @Override
        public String toString() {
            return "Holder[source=" + source + "]";
        }
    }

    /**
     * Perfil completo → copiado ({@code PROFILE}; com {@code policyholder} → 400 {@code NOT_ALLOWED});
     * incompleto → o do mediador, obrigatório e com as regras do S1-06 ({@code BROKER}). Telemóvel, e-mail
     * e língua vêm sempre da conta do cliente.
     */
    private Holder holder(CustomerView customer, PersonalData policyholder) {
        if (customer.profileComplete()) {
            if (policyholder != null) {
                throw new FieldValidationException("policyholder", FieldErrorCodes.NOT_ALLOWED,
                        "The customer's profile is complete: the policyholder comes from it");
            }
            return new Holder(ProposalService.policyholder(customer), customer.idDocNumber(), customer.nuit(),
                    PolicyholderSource.PROFILE);
        }
        if (policyholder == null) {
            throw new FieldValidationException("policyholder", FieldErrorCodes.REQUIRED,
                    "The customer's profile is incomplete: the policyholder is required");
        }
        ValidPersonalData valid = identity.validatePersonalData(policyholder, null, false, false, "policyholder.");
        Msisdn msisdn = customer.msisdn();
        return new Holder(snapshot(valid, valid.idDocNumber(), valid.nuit(), msisdn == null ? null : msisdn.e164(),
                customer.email(), customer.preferredLanguage()), valid.idDocNumber(), valid.nuit(),
                PolicyholderSource.BROKER);
    }

    private static PolicyholderSnapshot snapshot(ValidPersonalData valid, String idDocNumber, String nuit,
                                                 String msisdn, String email, String preferredLanguage) {
        return new PolicyholderSnapshot(valid.fullName(), valid.birthDate(), valid.idDocType(),
                ProposalService.maskDocument(idDocNumber), Nuit.of(nuit).masked(), valid.address(),
                valid.province().name(), msisdn, email, preferredLanguage);
    }

    /**
     * Campos usados na tarifa que diferem da cotação (S4-06 §5, passo 3): veículo {@code category},
     * {@code usage}, {@code year}, {@code provinceOfCirculation}, {@code hasTracker}, {@code commercialValue}
     * (a ausência conta como valor) e {@code plate}, {@code engineCc}, {@code seats}, {@code grossWeightKg}
     * quando a cotação os tiver; condutor {@code birthDate}, e {@code licenceIssueDate} e
     * {@code licenceCategories} quando a cotação os tiver. Um campo obrigatório em falta no pedido não conta
     * aqui: sai depois como {@code REQUIRED} (regras do S2-03).
     */
    List<String> changedFields(RatedVehicle rated, NewVehicle v, RatedDriver ratedDriver, NewDriver d) {
        Map<String, Boolean> same = new LinkedHashMap<>();
        if (rated.plate() != null && v.plate() != null && !v.plate().isBlank()) {
            String plate = normalizedPlate(v.plate());
            same.put("vehicle.plate", plate == null || rated.plate().equals(plate));
        }
        same.put("vehicle.category", v.category() == null || v.category() == rated.category());
        same.put("vehicle.usage", v.usage() == null || v.usage() == rated.usage());
        same.put("vehicle.year", v.year() == null || v.year() == rated.year());
        if (rated.engineCc() != null) {
            same.put("vehicle.engineCc", Objects.equals(rated.engineCc(), v.engineCc()));
        }
        if (rated.seats() != null) {
            same.put("vehicle.seats", v.seats() == null || rated.seats().equals(v.seats()));
        }
        if (rated.grossWeightKg() != null) {
            same.put("vehicle.grossWeightKg", Objects.equals(rated.grossWeightKg(), v.grossWeightKg()));
        }
        same.put("vehicle.provinceOfCirculation",
                v.provinceOfCirculation() == null || v.provinceOfCirculation() == rated.provinceOfCirculation());
        same.put("vehicle.commercialValue", Objects.equals(rated.commercialValue(),
                v.commercialValue() == null ? null : v.commercialValue().amount()));
        same.put("vehicle.hasTracker", rated.hasTracker() == Boolean.TRUE.equals(v.hasTracker()));
        if (ratedDriver != null) {
            same.put("driver.birthDate", d.birthDate() == null
                    || Objects.equals(ratedDriver.birthDate(), d.birthDate()));
            if (ratedDriver.licenceIssueDate() != null) {
                same.put("driver.licenceIssueDate", Objects.equals(ratedDriver.licenceIssueDate(),
                        d.licenceIssueDate()));
            }
            if (ratedDriver.licenceCategories() != null && !ratedDriver.licenceCategories().isEmpty()) {
                same.put("driver.licenceCategories", normalized(ratedDriver.licenceCategories())
                        .equals(normalized(d.licenceCategories())));
            }
        }
        return same.entrySet().stream().filter(e -> !e.getValue()).map(Map.Entry::getKey).toList();
    }

    /** Matrícula normalizada; {@code null} se não for válida (o erro sai depois, nas regras do S2-03). */
    private String normalizedPlate(String raw) {
        try {
            return plates.parse(raw).value();
        } catch (DomainException e) {
            return null;
        }
    }

    private static List<String> normalized(List<String> categories) {
        if (categories == null) {
            return List.of();
        }
        return categories.stream().filter(Objects::nonNull).map(c -> c.strip().toUpperCase(Locale.ROOT)).sorted()
                .toList();
    }

    static Specification<Proposal> specification(UUID brokerOrgId, Collection<ProposalStatus> statuses, String q) {
        return (root, query, cb) -> {
            List<Predicate> where = new ArrayList<>();
            where.add(cb.equal(root.get("brokerOrgId"), brokerOrgId));
            if (statuses != null && !statuses.isEmpty()) {
                where.add(root.get("status").in(statuses));
            }
            String text = q == null ? null : q.strip();
            if (text != null && !text.isEmpty()) {
                Expression<String> fullName = cb.function("jsonb_extract_path_text", String.class,
                        root.get("policyholder"), cb.literal("fullName"));
                String pattern = "%" + escapeLike(text.toLowerCase(Locale.ROOT)) + "%";
                where.add(cb.or(cb.equal(root.get("number"), text.toUpperCase(Locale.ROOT)),
                        cb.like(cb.lower(fullName), pattern, '\\')));
            }
            return cb.and(where.toArray(Predicate[]::new));
        };
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /** Estado auditado na criação pelo mediador: o tomador só mascarado. */
    public record BrokerCreateState(String number, UUID quoteId, UUID insurerId, String status, long totalAmount,
                                    String channel, UUID brokerOrgId, UUID customerUserId, UUID vehicleId,
                                    UUID driverId, String policyholderSource, String policyholderIdDocNumberMasked,
                                    String policyholderNuitMasked) {

        static BrokerCreateState of(Proposal p, PolicyholderSnapshot holder) {
            return new BrokerCreateState(p.getNumber(), p.getQuoteId(), p.getInsurerId(), p.getStatus().name(),
                    p.getTotalAmount(), p.getChannel().name(), p.getBrokerOrgId(), p.getCustomerUserId(),
                    p.getVehicleId(), p.getDriverId(), p.getPolicyholderSource().name(), holder.idDocNumberMasked(),
                    holder.nuitMasked());
        }
    }
}
