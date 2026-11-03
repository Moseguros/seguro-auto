package mz.co.moseguros.rating.domain;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.UnavailableReason;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Cotação de uma versão de produto ({@code rating.quote}, S3-02 §1): {@code OFFERED} com o preço
 * e a validade, ou {@code NOT_AVAILABLE} com o motivo. Uma cotação oferecida pode ser usada uma
 * única vez numa proposta ({@link #markUsed}); nas leituras vale o estado efectivo
 * ({@link #effectiveStatus}). Não se apaga.
 */
@Entity
@Table(schema = "rating", name = "quote")
public class Quote extends BaseEntity {

    /** Fuso em que se conta "hoje" para a data de início. */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");

    @Column(name = "quote_request_id", nullable = false, updatable = false)
    private UUID quoteRequestId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "customer_user_id", nullable = false, updatable = false)
    private UUID customerUserId;

    @Column(name = "requested_by_org_id", updatable = false)
    private UUID requestedByOrgId;

    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "product_version_id", nullable = false, updatable = false)
    private UUID productVersionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, updatable = false, length = 10)
    private RatingProviderKind provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private QuoteStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "unavailable_reason", updatable = false, length = 40)
    private UnavailableReason unavailableReason;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "unavailable_detail", updatable = false, columnDefinition = "jsonb")
    private JsonNode unavailableDetail;

    /** {@code PremiumBreakdown} (S3-01 §10) ou a decomposição do externo (S3-02 §8). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "premium_breakdown", updatable = false, columnDefinition = "jsonb")
    private JsonNode premiumBreakdown;

    @Column(name = "commercial_premium_amount", updatable = false)
    private Long commercialPremiumAmount;

    @Column(name = "charges_amount", updatable = false)
    private Long chargesAmount;

    @Column(name = "total_amount", updatable = false)
    private Long totalAmount;

    @Column(name = "valid_until", updatable = false)
    private Instant validUntil;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "external_reference", updatable = false, length = 100)
    private String externalReference;

    @Column(name = "rating_ms", nullable = false, updatable = false)
    private int ratingMs;

    protected Quote() {
    }

    private Quote(UUID id, QuoteRequest request, UUID insurerId, UUID productId, UUID productVersionId,
                  RatingProviderKind provider, int ratingMs) {
        super(id);
        this.quoteRequestId = Objects.requireNonNull(request, "request").getId();
        this.customerUserId = request.getCustomerUserId();
        this.requestedByOrgId = request.getRequestedByOrgId();
        this.insurerId = Objects.requireNonNull(insurerId, "insurerId");
        this.productId = Objects.requireNonNull(productId, "productId");
        this.productVersionId = Objects.requireNonNull(productVersionId, "productVersionId");
        this.provider = Objects.requireNonNull(provider, "provider");
        this.ratingMs = Math.max(0, ratingMs);
    }

    /**
     * Cotação com preço, válida até {@code createdAt + validity}.
     *
     * @param id o {@code quoteId} usado na avaliação (vai no pedido à seguradora externa)
     */
    public static Quote offered(UUID id, QuoteRequest request, UUID insurerId, UUID productId, UUID productVersionId,
                                RatingProviderKind provider, JsonNode breakdown, long commercialPremiumAmount,
                                long chargesAmount, long totalAmount, String externalReference, Instant createdAt,
                                Duration validity, int ratingMs) {
        Quote q = new Quote(id, request, insurerId, productId, productVersionId, provider, ratingMs);
        q.status = QuoteStatus.OFFERED;
        q.premiumBreakdown = Objects.requireNonNull(breakdown, "breakdown").deepCopy();
        q.commercialPremiumAmount = commercialPremiumAmount;
        q.chargesAmount = chargesAmount;
        q.totalAmount = totalAmount;
        q.externalReference = externalReference;
        q.validUntil = Objects.requireNonNull(createdAt, "createdAt").plus(validity);
        return q;
    }

    /** Cotação sem preço, com o motivo e os pormenores. */
    public static Quote notAvailable(UUID id, QuoteRequest request, UUID insurerId, UUID productId,
                                     UUID productVersionId, RatingProviderKind provider, UnavailableReason reason,
                                     JsonNode detail, int ratingMs) {
        Quote q = new Quote(id, request, insurerId, productId, productVersionId, provider, ratingMs);
        q.status = QuoteStatus.NOT_AVAILABLE;
        q.unavailableReason = Objects.requireNonNull(reason, "reason");
        q.unavailableDetail = detail == null ? null : detail.deepCopy();
        return q;
    }

    /**
     * Estado efectivo (S3-02 §10.1): uma cotação {@code OFFERED} com {@code now ≥ validUntil}, ou
     * cuja data de início já passou (hoje em {@code Africa/Maputo} depois de {@code startDate}), é
     * {@code EXPIRED}. Os outros estados mantêm-se.
     *
     * @param startDate data de início do pedido
     */
    public QuoteStatus effectiveStatus(Clock clock, LocalDate startDate) {
        if (status != QuoteStatus.OFFERED) {
            return status;
        }
        Instant now = clock.instant();
        boolean outOfValidity = !now.isBefore(validUntil);
        boolean startPassed = startDate != null && LocalDate.ofInstant(now, ZONE).isAfter(startDate);
        return outOfValidity || startPassed ? QuoteStatus.EXPIRED : QuoteStatus.OFFERED;
    }

    /**
     * Marca a cotação como usada numa proposta (S3-02 §10.2).
     *
     * @throws DomainException {@code QUOTE_NOT_AVAILABLE}, {@code QUOTE_ALREADY_USED} ou
     *                         {@code QUOTE_EXPIRED} (409)
     */
    public void markUsed(Clock clock, LocalDate startDate) {
        switch (effectiveStatus(clock, startDate)) {
            case NOT_AVAILABLE -> throw DomainException.of(ErrorCodes.QUOTE_NOT_AVAILABLE,
                    "The insurer made no offer for this quote");
            case USED -> throw DomainException.of(ErrorCodes.QUOTE_ALREADY_USED, "The quote was already used");
            case EXPIRED -> throw DomainException.of(ErrorCodes.QUOTE_EXPIRED, "The quote has expired");
            case OFFERED -> {
                this.status = QuoteStatus.USED;
                this.usedAt = clock.instant();
            }
            default -> throw new IllegalStateException("Unknown status " + status);
        }
    }

    public UUID getQuoteRequestId() {
        return quoteRequestId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getCustomerUserId() {
        return customerUserId;
    }

    public UUID getRequestedByOrgId() {
        return requestedByOrgId;
    }

    public UUID getProductId() {
        return productId;
    }

    public UUID getProductVersionId() {
        return productVersionId;
    }

    public RatingProviderKind getProvider() {
        return provider;
    }

    /** Estado gravado; nas leituras usar {@link #effectiveStatus}. */
    public QuoteStatus getStatus() {
        return status;
    }

    public UnavailableReason getUnavailableReason() {
        return unavailableReason;
    }

    public JsonNode getUnavailableDetail() {
        return unavailableDetail == null ? null : unavailableDetail.deepCopy();
    }

    public JsonNode getPremiumBreakdown() {
        return premiumBreakdown == null ? null : premiumBreakdown.deepCopy();
    }

    public Long getCommercialPremiumAmount() {
        return commercialPremiumAmount;
    }

    public Long getChargesAmount() {
        return chargesAmount;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public int getRatingMs() {
        return ratingMs;
    }
}
