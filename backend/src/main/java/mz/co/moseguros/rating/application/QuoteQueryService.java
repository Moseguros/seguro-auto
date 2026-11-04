package mz.co.moseguros.rating.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.QuoteView;
import mz.co.moseguros.rating.application.QuoteDetails.QuoteRequestDetails;
import mz.co.moseguros.rating.domain.Quote;
import mz.co.moseguros.rating.domain.QuoteRepository;
import mz.co.moseguros.rating.domain.QuoteRequest;
import mz.co.moseguros.rating.domain.QuoteRequestRepository;
import mz.co.moseguros.rating.domain.StoredBreakdown;
import mz.co.moseguros.rating.domain.tariff.RatedVehicle;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras das cotações (S3-02 §10 e §11) e o uso numa proposta ({@code RatingApi.markUsed}).
 * Correm no âmbito de quem pede: o RLS mostra ao cliente os seus pedidos (também os do mediador em
 * nome dele) e ao mediador os da sua organização. O estado é sempre o efectivo.
 */
@Service
public class QuoteQueryService {

    private final QuoteRequestRepository requests;
    private final QuoteRepository quotes;
    private final CatalogApi catalog;
    private final OrganizationApi organizations;
    private final QuoteSnapshots snapshots;
    private final CurrentUser currentUser;
    private final Clock clock;

    public QuoteQueryService(QuoteRequestRepository requests, QuoteRepository quotes, CatalogApi catalog,
                             OrganizationApi organizations, QuoteSnapshots snapshots, CurrentUser currentUser,
                             Clock clock) {
        this.requests = requests;
        this.quotes = quotes;
        this.catalog = catalog;
        this.organizations = organizations;
        this.snapshots = snapshots;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    /**
     * Resumo de um pedido para as listas (S3-02 §11).
     *
     * @param offeredCount cotações com estado efectivo {@code OFFERED}
     * @param bestTotal    o menor total dessas; {@code null} se não houver
     * @param validUntil   o maior {@code valid_until} dessas; {@code null} se não houver
     * @param expired      {@code offeredCount == 0} e houve cotações com preço
     */
    public record QuoteRequestSummary(QuoteRequest request, RatedVehicle vehicle, long offeredCount, Long bestTotal,
                                      Instant validUntil, boolean expired) {
    }

    /** @throws DomainException 404 {@code QUOTE_REQUEST_NOT_FOUND} se não for visível */
    @PreAuthorize("hasAnyAuthority('self:quotes','quote:create-for-client')")
    @Transactional(readOnly = true)
    public QuoteRequestDetails get(UUID requestId) {
        QuoteRequest request = requests.findById(requestId)
                .orElseThrow(() -> DomainException.of(ErrorCodes.QUOTE_REQUEST_NOT_FOUND, "Quote request not found"));
        List<Quote> found = quotes.findByQuoteRequestId(requestId);
        Map<UUID, InsurerProfileView> insurers = organizations.insurerProfiles(
                found.stream().map(Quote::getInsurerId).collect(Collectors.toSet()));
        Map<UUID, ProductVersionView> versions = versions(found.stream().map(Quote::getProductVersionId).toList());
        List<QuoteDetails> details = found.stream()
                .map(q -> new QuoteDetails(q, q.effectiveStatus(clock, request.getStartDate()),
                        insurers.get(q.getInsurerId()), versions.get(q.getProductVersionId()),
                        q.getPremiumBreakdown() == null ? null : StoredBreakdown.parse(q.getPremiumBreakdown())))
                .toList();
        return new QuoteRequestDetails(request, snapshots.vehicle(request.getVehicleSnapshot()),
                snapshots.driver(request.getDriverSnapshot()), snapshots.coverages(request.getRequestedCoverages()),
                details);
    }

    /** Pedidos do cliente (os seus e os que um mediador fez em nome dele). */
    @PreAuthorize("hasAuthority('self:quotes')")
    @Transactional(readOnly = true)
    public Page<QuoteRequestSummary> listMine(Pageable pageable) {
        return summaries(requests.findByCustomerUserId(currentUser.id(), pageable));
    }

    /**
     * Pedidos da organização do mediador (de todos os seus utilizadores).
     *
     * @param clientMsisdn filtro opcional, normalizado com {@link Msisdn} ({@code INVALID_MSISDN})
     */
    @PreAuthorize("hasAuthority('quote:create-for-client')")
    @Transactional(readOnly = true)
    public Page<QuoteRequestSummary> listForBroker(String clientMsisdn, Pageable pageable) {
        UUID orgId = currentUser.orgId();
        if (orgId == null) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only broker or agent staff can list client quotes");
        }
        if (clientMsisdn == null || clientMsisdn.isBlank()) {
            return summaries(requests.findByRequestedByOrgId(orgId, pageable));
        }
        Msisdn msisdn;
        try {
            msisdn = Msisdn.of(clientMsisdn);
        } catch (DomainException e) {
            throw FieldValidationException.of("clientMsisdn", e);
        }
        return summaries(requests.findByRequestedByOrgIdAndClientMsisdn(orgId, msisdn, pageable));
    }

    /**
     * Cotação no âmbito de quem chama ({@code RatingApi.quote}).
     *
     * @throws DomainException 404 {@code QUOTE_NOT_FOUND} se não for visível
     */
    @Transactional(readOnly = true)
    public QuoteView view(UUID quoteId) {
        Quote quote = quotes.findById(quoteId).orElseThrow(QuoteQueryService::quoteNotFound);
        QuoteRequest request = requests.findById(quote.getQuoteRequestId()).orElseThrow(QuoteQueryService::quoteNotFound);
        return view(quote, request);
    }

    /**
     * Marca a cotação como usada, na transacção de quem chama (a proposta, S4), com
     * {@code PESSIMISTIC_WRITE}.
     *
     * @throws DomainException 404 {@code QUOTE_NOT_FOUND}; 409 {@code QUOTE_NOT_AVAILABLE},
     *                         {@code QUOTE_ALREADY_USED} ou {@code QUOTE_EXPIRED}
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void markUsed(UUID quoteId) {
        Quote quote = quotes.findForUpdate(quoteId).orElseThrow(QuoteQueryService::quoteNotFound);
        QuoteRequest request = requests.findById(quote.getQuoteRequestId()).orElseThrow(QuoteQueryService::quoteNotFound);
        quote.markUsed(clock, request.getStartDate());
        quotes.flush();
    }

    private QuoteView view(Quote quote, QuoteRequest request) {
        QuoteStatus status = quote.effectiveStatus(clock, request.getStartDate());
        StoredBreakdown breakdown = quote.getPremiumBreakdown() == null ? null
                : StoredBreakdown.parse(quote.getPremiumBreakdown());
        Set<String> mandatory = Optional.ofNullable(versions(List.of(quote.getProductVersionId()))
                        .get(quote.getProductVersionId()))
                .map(v -> v.coverages().stream().filter(CoverageView::mandatory).map(c -> c.code().name())
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
        return new QuoteView(quote.getId(), request.getId(), quote.getInsurerId(), quote.getProductId(),
                quote.getProductVersionId(), request.getProductType().name(), status, request.getOrigin().name(),
                request.getCustomerUserId(), request.getClientMsisdn() == null ? null : request.getClientMsisdn().value(),
                request.getRequestedByOrgId(), snapshots.vehicle(request.getVehicleSnapshot()),
                snapshots.driver(request.getDriverSnapshot()),
                breakdown == null ? List.of() : breakdown.quotedCoverages(mandatory), request.getStartDate(),
                request.getEndDate(), request.getDurationDays(), quote.getCommercialPremiumAmount(),
                breakdown == null ? List.of() : breakdown.charges(),
                breakdown == null ? null : breakdown.roundingAdjustmentAmount(), quote.getTotalAmount(),
                quote.getValidUntil(), quote.getProvider().name(), quote.getExternalReference());
    }

    private Page<QuoteRequestSummary> summaries(Page<QuoteRequest> page) {
        List<UUID> ids = page.getContent().stream().map(QuoteRequest::getId).toList();
        Map<UUID, List<Quote>> byRequest = new HashMap<>();
        if (!ids.isEmpty()) {
            for (Quote q : quotes.findByQuoteRequestIdIn(ids)) {
                byRequest.computeIfAbsent(q.getQuoteRequestId(), k -> new ArrayList<>()).add(q);
            }
        }
        return page.map(r -> summary(r, byRequest.getOrDefault(r.getId(), List.of())));
    }

    private QuoteRequestSummary summary(QuoteRequest request, List<Quote> found) {
        LocalDate start = request.getStartDate();
        List<Quote> offered = found.stream()
                .filter(q -> q.effectiveStatus(clock, start) == QuoteStatus.OFFERED).toList();
        boolean hadPrice = found.stream().anyMatch(q -> q.getStatus() != QuoteStatus.NOT_AVAILABLE);
        Long best = offered.stream().map(Quote::getTotalAmount).min(Long::compare).orElse(null);
        Instant validUntil = offered.stream().map(Quote::getValidUntil).max(Instant::compareTo).orElse(null);
        return new QuoteRequestSummary(request, snapshots.vehicle(request.getVehicleSnapshot()), offered.size(), best,
                validUntil, offered.isEmpty() && hadPrice);
    }

    /** Versões publicadas (de qualquer seguradora, {@code CatalogApi.version}); as ilegíveis faltam no mapa. */
    private Map<UUID, ProductVersionView> versions(Collection<UUID> ids) {
        Map<UUID, ProductVersionView> result = new LinkedHashMap<>();
        for (UUID id : Set.copyOf(ids)) {
            try {
                result.put(id, catalog.version(id));
            } catch (DomainException e) {
                // versão nunca publicada (não acontece: só se cotam versões publicadas)
            }
        }
        return result;
    }

    private static DomainException quoteNotFound() {
        return DomainException.of(ErrorCodes.QUOTE_NOT_FOUND, "Quote not found");
    }
}
