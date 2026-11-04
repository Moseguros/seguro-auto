package mz.co.moseguros.rating.application.provider;

import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.RatingMode;
import mz.co.moseguros.rating.QuoteResult;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.QuotedCharge;
import mz.co.moseguros.rating.QuotedCoverage;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.rating.domain.RatingProviderKind;
import mz.co.moseguros.rating.domain.StoredBreakdown;
import mz.co.moseguros.rating.domain.tariff.CoverageSelector;
import mz.co.moseguros.rating.domain.tariff.CoverageSelector.SelectedCoverage;
import mz.co.moseguros.rating.domain.tariff.CoverageSelector.Selection;
import mz.co.moseguros.rating.domain.tariff.PremiumBreakdown.Deductible;
import mz.co.moseguros.rating.RatedDriver;
import mz.co.moseguros.rating.RatedVehicle;
import mz.co.moseguros.rating.infrastructure.ExternalRatingClient;
import mz.co.moseguros.rating.infrastructure.ExternalRatingClient.CallFailedException;
import mz.co.moseguros.rating.infrastructure.ExternalRatingClient.FailureKind;
import mz.co.moseguros.rating.infrastructure.ExternalRatingClient.HttpResult;
import mz.co.moseguros.rating.infrastructure.ExternalRatingContract;
import mz.co.moseguros.rating.infrastructure.ExternalRatingContract.Amount;
import mz.co.moseguros.rating.infrastructure.ExternalRatingContract.Declined;
import mz.co.moseguros.rating.infrastructure.ExternalRatingContract.InvalidResponseException;
import mz.co.moseguros.rating.infrastructure.ExternalRatingContract.Offered;
import mz.co.moseguros.rating.infrastructure.ExternalRatingContract.Response;
import mz.co.moseguros.rating.infrastructure.RatingResilienceConfig;
import mz.co.moseguros.rating.infrastructure.RatingSecretResolver;
import mz.co.moseguros.rating.infrastructure.RatingSecretResolver.UnknownSecretRefException;
import mz.co.moseguros.rating.infrastructure.RequestSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Tarifação pelo sistema da seguradora (S3-02 §8, contrato {@code moseguros-rating-1}):
 * {@code CoverageSelector} (se der {@code Unavailable}, a seguradora nem é chamada) e um
 * {@code POST} assinado com HMAC-SHA256, protegido por circuit breaker e bulkhead
 * ({@code rating-<insurerId>}).
 *
 * <ul>
 *   <li>{@code DECLINED} → {@code INSURER_DECLINED} ({@code detail.declineReason});</li>
 *   <li>tempo esgotado → {@code INSURER_TIMEOUT};</li>
 *   <li>outro estado HTTP, corpo que não é JSON, resposta grande de mais, erro de rede, circuito
 *       aberto ou bulkhead cheio → {@code INSURER_UNAVAILABLE};</li>
 *   <li>resposta que não cumpre o contrato → {@code EXTERNAL_INVALID_RESPONSE}.</li>
 * </ul>
 * Contam como falha no circuito: tempo esgotado, erro de rede, 5xx e resposta inválida;
 * {@code DECLINED} e 4xx não. Os logs têm só a seguradora, a cotação, o estado HTTP, a duração e o
 * resultado (nunca o corpo, o segredo nem a assinatura).
 */
@Component
public class HttpRatingProvider implements RatingProvider {

    private static final Logger log = LoggerFactory.getLogger(HttpRatingProvider.class);
    private static final String EXTERNAL = RatingProviderKind.EXTERNAL.name();

    static final String HEADER_REQUEST_ID = "X-MoSeguros-Request-Id";
    static final String HEADER_TIMESTAMP = "X-MoSeguros-Timestamp";
    static final String HEADER_SIGNATURE = "X-MoSeguros-Signature";

    /** Inclui os {@code null} (o contrato mostra {@code "grossWeightKg": null}); datas ISO 8601. */
    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    private final ExternalRatingClient client;
    private final RatingSecretResolver secrets;
    private final RatingResilienceConfig resilience;
    private final Clock clock;

    public HttpRatingProvider(ExternalRatingClient client, RatingSecretResolver secrets,
                              RatingResilienceConfig resilience, Clock clock) {
        this.client = client;
        this.secrets = secrets;
        this.resilience = resilience;
        this.clock = clock;
    }

    @Override
    public boolean supports(RatingMode mode) {
        return mode == RatingMode.EXTERNAL;
    }

    @Override
    public ProviderResult rate(RatingRequest request) {
        long start = System.nanoTime();
        Selection selection = CoverageSelector.select(request.input(), request.product());
        if (!selection.isAvailable()) {
            return unavailable(selection.unavailable().reason(), selection.unavailable().detail(), start);
        }
        InsurerProfileView insurer = request.insurer();
        String secret;
        try {
            secret = secrets.resolve(insurer.ratingSecretRef());
        } catch (UnknownSecretRefException e) {
            log.warn("External rating: insurer {} quote {}: unknown secret reference {}", insurer.insurerId(),
                    request.quoteId(), insurer.ratingSecretRef());
            return unavailable(UnavailableReason.INSURER_UNAVAILABLE, null, start);
        }

        CircuitBreaker breaker = resilience.circuitBreaker(insurer.insurerId());
        Bulkhead bulkhead = resilience.bulkhead(insurer.insurerId());
        if (!breaker.tryAcquirePermission()) {
            log.info("External rating: insurer {} quote {}: circuit open, not called", insurer.insurerId(),
                    request.quoteId());
            return unavailable(UnavailableReason.INSURER_UNAVAILABLE, null, start);
        }
        if (!bulkhead.tryAcquirePermission()) {
            breaker.releasePermission();
            log.info("External rating: insurer {} quote {}: bulkhead full, not called", insurer.insurerId(),
                    request.quoteId());
            return unavailable(UnavailableReason.INSURER_UNAVAILABLE, null, start);
        }
        long callStart = System.nanoTime();
        Outcome outcome;
        try {
            outcome = call(request, selection, secret);
        } finally {
            bulkhead.onComplete();
        }
        long callNanos = System.nanoTime() - callStart;
        if (outcome.failure() != null) {
            breaker.onError(callNanos, TimeUnit.NANOSECONDS, outcome.failure());
        } else {
            breaker.onSuccess(callNanos, TimeUnit.NANOSECONDS);
        }
        log.info("External rating: insurer {} quote {}: HTTP {} in {} ms → {}", insurer.insurerId(), request.quoteId(),
                outcome.httpStatus(), callNanos / 1_000_000, outcome.label());
        return new ProviderResult(outcome.result(), outcome.breakdown(), InternalRatingProvider.elapsedMs(start));
    }

    /**
     * Resultado de uma chamada.
     *
     * @param failure    excepção a registar no circuito ({@code null} = conta como sucesso)
     * @param httpStatus {@code null} sem resposta
     */
    private record Outcome(QuoteResult result, JsonNode breakdown, Throwable failure, Integer httpStatus,
                           String label) {
    }

    private Outcome call(RatingRequest request, Selection selection, String secret) {
        String requestId = request.quoteId().toString();
        byte[] body;
        try {
            body = MAPPER.writeValueAsBytes(body(request, selection));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize the rating request", e);
        }
        long timestamp = clock.instant().getEpochSecond();
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HEADER_REQUEST_ID, requestId);
        headers.put(HEADER_TIMESTAMP, String.valueOf(timestamp));
        headers.put(HEADER_SIGNATURE, RequestSigner.sign(secret, timestamp, body));

        HttpResult http;
        try {
            http = client.post(URI.create(request.insurer().ratingEndpointUrl()), headers, body);
        } catch (CallFailedException e) {
            UnavailableReason reason = e.kind() == FailureKind.TIMEOUT
                    ? UnavailableReason.INSURER_TIMEOUT : UnavailableReason.INSURER_UNAVAILABLE;
            return new Outcome(notAvailable(reason, null), null, e, null, reason.name() + " (" + e.kind() + ")");
        } catch (IllegalArgumentException e) {
            return new Outcome(notAvailable(UnavailableReason.INSURER_UNAVAILABLE, null), null, e, null,
                    "INSURER_UNAVAILABLE (invalid endpoint)");
        }

        if (http.status() != 200) {
            boolean serverError = http.status() >= 500;
            return new Outcome(notAvailable(UnavailableReason.INSURER_UNAVAILABLE, null), null,
                    serverError ? new IOException("HTTP " + http.status()) : null, http.status(), "INSURER_UNAVAILABLE");
        }
        JsonNode json;
        try {
            json = MAPPER.readTree(http.body());
        } catch (IOException e) {
            return new Outcome(notAvailable(UnavailableReason.INSURER_UNAVAILABLE, null), null, e, http.status(),
                    "INSURER_UNAVAILABLE (not JSON)");
        }
        Set<String> sentCodes = new LinkedHashSet<>();
        selection.coverages().forEach(c -> sentCodes.add(c.coverage().code().name()));
        Response response;
        try {
            response = ExternalRatingContract.read(json, requestId, sentCodes);
        } catch (InvalidResponseException e) {
            return new Outcome(notAvailable(UnavailableReason.EXTERNAL_INVALID_RESPONSE, null), null, e,
                    http.status(), "EXTERNAL_INVALID_RESPONSE (" + e.getMessage() + ")");
        }
        if (response instanceof Declined declined) {
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("declineReason", declined.declineReason());
            return new Outcome(notAvailable(UnavailableReason.INSURER_DECLINED, detail), null, null, http.status(),
                    "INSURER_DECLINED");
        }
        Offered offered = (Offered) response;
        return new Outcome(offered(request, selection, offered), breakdown(selection, offered), null, http.status(),
                "OFFERED");
    }

    static ExternalRatingContract.Request body(RatingRequest request, Selection selection) {
        RatedVehicle v = request.vehicle();
        RatedDriver d = request.driver();
        List<ExternalRatingContract.Coverage> coverages = selection.coverages().stream()
                .map(c -> new ExternalRatingContract.Coverage(c.coverage().code().name(), Amount.of(c.capital())))
                .toList();
        return new ExternalRatingContract.Request(ExternalRatingContract.VERSION, request.quoteId().toString(),
                request.insurer().shortCode(),
                new ExternalRatingContract.Product(request.version().productCode(), request.version().versionNo(),
                        request.version().productType().name()),
                request.startDate(), request.endDate(), request.durationDays(),
                new ExternalRatingContract.Vehicle(name(v.category()), name(v.usage()), v.make(), v.model(), v.year(),
                        v.engineCc(), v.seats(), v.grossWeightKg(), name(v.provinceOfCirculation()),
                        Amount.of(v.commercialValue()), v.hasTracker()),
                new ExternalRatingContract.Driver(d == null ? null : d.birthDate(),
                        d == null ? null : d.licenceIssueDate(),
                        d == null || d.licenceCategories() == null ? List.of() : d.licenceCategories()),
                coverages);
    }

    private static QuoteResult offered(RatingRequest request, Selection selection, Offered offered) {
        Map<String, Long> premiums = new LinkedHashMap<>();
        offered.coverages().forEach(c -> premiums.put(c.code(), c.premiumAmount()));
        List<QuotedCoverage> coverages = selection.coverages().stream().map(c -> {
            Deductible deductible = Deductible.of(c.coverage());
            String value = deductible.type() == DeductibleType.FIXED
                    ? (deductible.amount() == null ? null : String.valueOf(deductible.amount()))
                    : deductible.type() == DeductibleType.PERCENT && deductible.percent() != null
                    ? StoredBreakdown.plain(deductible.percent()) : null;
            return new QuotedCoverage(c.coverage().code().name(), c.coverage().mandatory(),
                    c.capital() == null ? 0 : c.capital(), deductible.type().name(), value,
                    premiums.get(c.coverage().code().name()));
        }).toList();
        List<QuotedCharge> charges = offered.charges().stream()
                .map(c -> new QuotedCharge(c.code(), c.labelPt(), c.labelEn(), c.amount())).toList();
        return new QuoteResult(QuoteStatus.OFFERED, null, null, coverages, offered.commercialPremiumAmount(), charges,
                0L, offered.totalAmount(), EXTERNAL, offered.externalReference());
    }

    /**
     * Decomposição gravada (S3-02 §8): {@code provider}, coberturas ({@code code},
     * {@code capitalAmount}, {@code deductible}, {@code premiumAmount}), prémio comercial,
     * encargos e total. Sem variáveis, bases nem factores.
     */
    static ObjectNode breakdown(Selection selection, Offered offered) {
        Map<String, Long> premiums = new LinkedHashMap<>();
        offered.coverages().forEach(c -> premiums.put(c.code(), c.premiumAmount()));
        JsonNodeFactory f = JsonNodeFactory.instance;
        ObjectNode root = f.objectNode();
        root.put("provider", EXTERNAL);
        ArrayNode coverages = root.putArray("coverages");
        for (SelectedCoverage c : selection.coverages()) {
            ObjectNode n = coverages.addObject();
            n.put("code", c.coverage().code().name());
            if (c.capital() == null) {
                n.putNull("capitalAmount");
            } else {
                n.put("capitalAmount", c.capital().longValue());
            }
            Deductible d = Deductible.of(c.coverage());
            ObjectNode dn = n.putObject("deductible");
            dn.put("type", d.type().name());
            if (d.type() == DeductibleType.FIXED) {
                putLong(dn, "amount", d.amount());
            } else if (d.type() == DeductibleType.PERCENT) {
                dn.put("percent", d.percent() == null ? null : d.percent().toPlainString());
                putLong(dn, "minAmount", d.minAmount());
            }
            n.put("premiumAmount", premiums.get(c.coverage().code().name()).longValue());
        }
        root.put("commercialPremiumAmount", offered.commercialPremiumAmount());
        ArrayNode charges = root.putArray("charges");
        for (ExternalRatingContract.Charge c : offered.charges()) {
            ObjectNode n = charges.addObject();
            n.put("code", c.code());
            n.put("labelPt", c.labelPt());
            n.put("labelEn", c.labelEn());
            n.put("amount", c.amount());
        }
        root.put("totalAmount", offered.totalAmount());
        return root;
    }

    private static ProviderResult unavailable(UnavailableReason reason, Map<String, Object> detail, long start) {
        return new ProviderResult(notAvailable(reason, detail), null, InternalRatingProvider.elapsedMs(start));
    }

    private static QuoteResult notAvailable(UnavailableReason reason, Map<String, Object> detail) {
        return QuoteResult.notAvailable(reason, detail, EXTERNAL);
    }

    private static String name(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private static void putLong(ObjectNode node, String field, Long value) {
        if (value == null) {
            node.putNull(field);
        } else {
            node.put(field, value.longValue());
        }
    }
}
