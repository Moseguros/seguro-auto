package mz.co.moseguros.policy.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.policy.CoverStatus;
import mz.co.moseguros.policy.CoverageType;
import mz.co.moseguros.policy.PaymentPlan;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.RenewalType;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Apólice ({@code policy.policy}, S6-01 §3). {@link #issue} é a única fábrica e cria a apólice
 * {@code ACTIVE}; as outras transições chegam no S7-01. As regras 3 a 5 da emissão estão aqui
 * ({@link #coverPeriod}, {@link #renewalTypeFor}, {@link #coverageTypeFor}).
 */
@Entity
@Table(schema = "policy", name = "policy")
public class Policy extends BaseEntity {

    /** Fuso das datas de negócio (vencimentos, filtros). */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");
    /** Tipos de produto aceites ({@code catalog.ProductType}). */
    public static final Set<String> PRODUCT_TYPES = Set.of("MTPL_ONLY", "COMPREHENSIVE", "TEMPORARY", "BORDER",
            "FLEET");
    static final String TEMPORARY = "TEMPORARY";
    static final String BORDER = "BORDER";
    static final String OWN_DAMAGE_PREFIX = "OD_";

    @Column(name = "number", nullable = false, updatable = false, length = 14)
    private String number;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "proposal_id", nullable = false, updatable = false)
    private UUID proposalId;

    @Column(name = "payment_intent_id", nullable = false, updatable = false)
    private UUID paymentIntentId;

    @Column(name = "product_version_id", nullable = false, updatable = false)
    private UUID productVersionId;

    @Column(name = "product_name", nullable = false, updatable = false, length = 120)
    private String productName;

    @Column(name = "product_type", nullable = false, updatable = false, length = 15)
    private String productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "coverage_type", nullable = false, length = 15)
    private CoverageType coverageType;

    @Column(name = "policyholder_user_id", nullable = false, updatable = false)
    private UUID policyholderUserId;

    @Column(name = "policyholder_name", nullable = false, length = 120)
    private String policyholderName;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "plate", nullable = false, length = 20)
    private String plate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "vehicle_snapshot", nullable = false, columnDefinition = "jsonb")
    private JsonNode vehicleSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "driver_snapshot", columnDefinition = "jsonb")
    private JsonNode driverSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "coverages", nullable = false, columnDefinition = "jsonb")
    private JsonNode coverages;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "renewal_type", nullable = false, length = 20)
    private RenewalType renewalType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_plan", nullable = false, length = 12)
    private PaymentPlan paymentPlan;

    @Column(name = "total_amount", nullable = false)
    private long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    private PolicyStatus status;

    @Column(name = "status_reason", length = 30)
    private String statusReason;

    @Column(name = "status_changed_at", nullable = false)
    private Instant statusChangedAt;

    @Column(name = "channel", nullable = false, updatable = false, length = 10)
    private String channel;

    @Column(name = "broker_org_id", updatable = false)
    private UUID brokerOrgId;

    @Column(name = "schedule_file_id")
    private UUID scheduleFileId;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    protected Policy() {
    }

    /** Período de cobertura da apólice (regra 3). {@code endAt} exclusivo. */
    public record CoverPeriod(Instant startAt, Instant endAt) {

        /** Data de início em {@code Africa/Maputo} (base dos vencimentos das prestações). */
        public LocalDate startDate() {
            return LocalDate.ofInstant(startAt, ZONE);
        }
    }

    /**
     * Regra 3: {@code start_at = max(início pedido, momento do pagamento)} e
     * {@code end_at = fim pedido + (start_at − início pedido)} (a duração contratada mantém-se). Um
     * pagamento antes do início pedido deixa as datas da proposta.
     */
    public static CoverPeriod coverPeriod(Instant proposalStartAt, Instant proposalEndAt, Instant paidAt) {
        Objects.requireNonNull(proposalStartAt, "proposalStartAt");
        Objects.requireNonNull(proposalEndAt, "proposalEndAt");
        Objects.requireNonNull(paidAt, "paidAt");
        Instant start = paidAt.isAfter(proposalStartAt) ? paidAt : proposalStartAt;
        Instant end = proposalEndAt.plus(Duration.between(proposalStartAt, start));
        return new CoverPeriod(start, end);
    }

    /** Regra 4: produto {@code TEMPORARY} → {@code FIXED_TERM}; os outros → {@code ANNUAL_RENEWABLE}. */
    public static RenewalType renewalTypeFor(String productType) {
        return TEMPORARY.equals(productType) ? RenewalType.FIXED_TERM : RenewalType.ANNUAL_RENEWABLE;
    }

    /**
     * Regra 5: {@code TEMPORARY} → {@code TEMPORARY}; {@code BORDER} → {@code BORDER}; alguma cobertura
     * {@code OD_*} → {@code COMPREHENSIVE}; senão {@code MTPL_ONLY}.
     */
    public static CoverageType coverageTypeFor(String productType, Collection<String> coverageCodes) {
        if (TEMPORARY.equals(productType)) {
            return CoverageType.TEMPORARY;
        }
        if (BORDER.equals(productType)) {
            return CoverageType.BORDER;
        }
        boolean ownDamage = coverageCodes != null
                && coverageCodes.stream().anyMatch(c -> c != null && c.startsWith(OWN_DAMAGE_PREFIX));
        return ownDamage ? CoverageType.COMPREHENSIVE : CoverageType.MTPL_ONLY;
    }

    /** Plano de pagamento (regra 2): produto {@code TEMPORARY} é sempre {@code FULL}. */
    public static PaymentPlan paymentPlanFor(String productType, PaymentPlan requested) {
        return TEMPORARY.equals(productType) || requested == null ? PaymentPlan.FULL : requested;
    }

    /** Dados da emissão (S6-01 §5); o período já calculado pela regra 3. */
    public record Issue(String number, UUID insurerId, UUID proposalId, UUID paymentIntentId,
                        UUID productVersionId, String productName, String productType,
                        Collection<String> coverageCodes, UUID policyholderUserId, String policyholderName,
                        UUID vehicleId, String plate, JsonNode vehicleSnapshot, JsonNode driverSnapshot,
                        JsonNode coverages, CoverPeriod period, PaymentPlan paymentPlan, long totalAmount,
                        String channel, UUID brokerOrgId, Instant issuedAt) {
    }

    /** Emite a apólice {@code ACTIVE} (única fábrica). */
    public static Policy issue(Issue i) {
        Objects.requireNonNull(i, "issue");
        if (!PRODUCT_TYPES.contains(i.productType())) {
            throw new IllegalArgumentException("Unknown product type: " + i.productType());
        }
        if (i.totalAmount() <= 0) {
            throw new IllegalArgumentException("The total amount must be positive");
        }
        Policy p = new Policy();
        p.number = Objects.requireNonNull(i.number(), "number");
        p.insurerId = Objects.requireNonNull(i.insurerId(), "insurerId");
        p.proposalId = Objects.requireNonNull(i.proposalId(), "proposalId");
        p.paymentIntentId = Objects.requireNonNull(i.paymentIntentId(), "paymentIntentId");
        p.productVersionId = Objects.requireNonNull(i.productVersionId(), "productVersionId");
        p.productName = Objects.requireNonNull(i.productName(), "productName");
        p.productType = i.productType();
        p.coverageType = coverageTypeFor(i.productType(), i.coverageCodes());
        p.policyholderUserId = Objects.requireNonNull(i.policyholderUserId(), "policyholderUserId");
        p.policyholderName = Objects.requireNonNull(i.policyholderName(), "policyholderName");
        p.vehicleId = Objects.requireNonNull(i.vehicleId(), "vehicleId");
        p.plate = Objects.requireNonNull(i.plate(), "plate");
        p.vehicleSnapshot = Objects.requireNonNull(i.vehicleSnapshot(), "vehicleSnapshot");
        p.driverSnapshot = i.driverSnapshot() == null || i.driverSnapshot().isNull() ? null : i.driverSnapshot();
        p.coverages = Objects.requireNonNull(i.coverages(), "coverages");
        p.startAt = Objects.requireNonNull(i.period(), "period").startAt();
        p.endAt = i.period().endAt();
        if (!p.endAt.isAfter(p.startAt)) {
            throw new IllegalArgumentException("The policy must end after it starts");
        }
        p.renewalType = renewalTypeFor(i.productType());
        p.paymentPlan = paymentPlanFor(i.productType(), i.paymentPlan());
        p.totalAmount = i.totalAmount();
        p.status = PolicyStatus.ACTIVE;
        p.issuedAt = Objects.requireNonNull(i.issuedAt(), "issuedAt");
        p.statusChangedAt = p.issuedAt;
        p.channel = Objects.requireNonNull(i.channel(), "channel");
        p.brokerOrgId = "BROKER".equals(i.channel()) ? i.brokerOrgId() : null;
        return p;
    }

    /**
     * Cobertura no instante {@code at} (S6-01 §4). Nesta tarefa o estado é o actual, para qualquer
     * {@code at}; o S7-01 refina o cálculo para datas passadas.
     */
    public CoverStatus coverAt(Instant at) {
        return coverAt(status, startAt, endAt, at);
    }

    /** {@link #coverAt(Instant)} sem a entidade (para as leituras por projecção). */
    public static CoverStatus coverAt(PolicyStatus status, Instant startAt, Instant endAt, Instant at) {
        Objects.requireNonNull(at, "at");
        return switch (status) {
            case CANCELLED -> CoverStatus.CANCELLED;
            case SUSPENDED -> CoverStatus.SUSPENDED;
            case LAPSED -> CoverStatus.LAPSED;
            case EXPIRED -> CoverStatus.ENDED;
            case ACTIVE -> at.isBefore(startAt) ? CoverStatus.NOT_STARTED
                    : !at.isBefore(endAt) ? CoverStatus.ENDED : CoverStatus.COVERED;
        };
    }

    public CoverPeriod period() {
        return new CoverPeriod(startAt, endAt);
    }

    public String getNumber() {
        return number;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getProposalId() {
        return proposalId;
    }

    public UUID getPaymentIntentId() {
        return paymentIntentId;
    }

    public UUID getProductVersionId() {
        return productVersionId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductType() {
        return productType;
    }

    public CoverageType getCoverageType() {
        return coverageType;
    }

    public UUID getPolicyholderUserId() {
        return policyholderUserId;
    }

    public String getPolicyholderName() {
        return policyholderName;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public String getPlate() {
        return plate;
    }

    public JsonNode getVehicleSnapshot() {
        return vehicleSnapshot;
    }

    public JsonNode getDriverSnapshot() {
        return driverSnapshot;
    }

    public JsonNode getCoverages() {
        return coverages;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public RenewalType getRenewalType() {
        return renewalType;
    }

    public PaymentPlan getPaymentPlan() {
        return paymentPlan;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public Instant getStatusChangedAt() {
        return statusChangedAt;
    }

    public String getChannel() {
        return channel;
    }

    public UUID getBrokerOrgId() {
        return brokerOrgId;
    }

    public UUID getScheduleFileId() {
        return scheduleFileId;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }
}
