package mz.co.moseguros.rating.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Pedido de cotações ({@code rating.quote_request}, S3-02 §1): quem pediu, para que cliente, o
 * risco (snapshots do veículo e do condutor, como foram usados na tarifação), as coberturas pedidas
 * e o período. Só é visível ao cliente, ao mediador que pediu e à plataforma (RLS); a seguradora
 * vê só as suas linhas de {@link Quote}. Não se altera depois de gravado.
 */
@Entity
@Table(schema = "rating", name = "quote_request")
public class QuoteRequest extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false, updatable = false, length = 10)
    private QuoteOrigin origin;

    @Column(name = "customer_user_id", nullable = false, updatable = false)
    private UUID customerUserId;

    /** Só nas do mediador. */
    @Column(name = "client_msisdn", updatable = false, length = 12)
    private Msisdn clientMsisdn;

    @Column(name = "requested_by_user_id", nullable = false, updatable = false)
    private UUID requestedByUserId;

    /** Só nas do mediador. */
    @Column(name = "requested_by_org_id", updatable = false)
    private UUID requestedByOrgId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, updatable = false, length = 20)
    private ProductType productType;

    /** {@code RatedVehicle}. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "vehicle_snapshot", nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode vehicleSnapshot;

    /** {@code RatedDriver}. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "driver_snapshot", nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode driverSnapshot;

    /** {@code [{ "code": "VTPL", "capital": 500000000 }]}, tal como pedido. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "requested_coverages", nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode requestedCoverages;

    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDate startDate;

    @Column(name = "duration_days", nullable = false, updatable = false)
    private int durationDays;

    @Column(name = "end_date", nullable = false, updatable = false)
    private LocalDate endDate;

    @Column(name = "evaluation_ms", nullable = false, updatable = false)
    private int evaluationMs;

    protected QuoteRequest() {
    }

    /** Pedido do próprio cliente. */
    public static QuoteRequest byCustomer(UUID customerUserId, ProductType productType, JsonNode vehicle,
                                          JsonNode driver, JsonNode requestedCoverages, LocalDate startDate,
                                          int durationDays, LocalDate endDate, int evaluationMs) {
        QuoteRequest r = new QuoteRequest();
        r.origin = QuoteOrigin.CUSTOMER;
        r.customerUserId = Objects.requireNonNull(customerUserId, "customerUserId");
        r.requestedByUserId = customerUserId;
        r.fill(productType, vehicle, driver, requestedCoverages, startDate, durationDays, endDate, evaluationMs);
        return r;
    }

    /**
     * Pedido de um mediador em nome do cliente.
     *
     * @param customerUserId conta {@code CUSTOMER} do cliente (encontrada ou criada pelo telemóvel)
     */
    public static QuoteRequest byBroker(UUID customerUserId, Msisdn clientMsisdn, UUID requestedByUserId,
                                        UUID requestedByOrgId, ProductType productType, JsonNode vehicle,
                                        JsonNode driver, JsonNode requestedCoverages, LocalDate startDate,
                                        int durationDays, LocalDate endDate, int evaluationMs) {
        QuoteRequest r = new QuoteRequest();
        r.origin = QuoteOrigin.BROKER;
        r.customerUserId = Objects.requireNonNull(customerUserId, "customerUserId");
        r.clientMsisdn = Objects.requireNonNull(clientMsisdn, "clientMsisdn");
        r.requestedByUserId = Objects.requireNonNull(requestedByUserId, "requestedByUserId");
        r.requestedByOrgId = Objects.requireNonNull(requestedByOrgId, "requestedByOrgId");
        r.fill(productType, vehicle, driver, requestedCoverages, startDate, durationDays, endDate, evaluationMs);
        return r;
    }

    private void fill(ProductType productType, JsonNode vehicle, JsonNode driver, JsonNode requestedCoverages,
                      LocalDate startDate, int durationDays, LocalDate endDate, int evaluationMs) {
        this.productType = Objects.requireNonNull(productType, "productType");
        this.vehicleSnapshot = Objects.requireNonNull(vehicle, "vehicle").deepCopy();
        this.driverSnapshot = Objects.requireNonNull(driver, "driver").deepCopy();
        this.requestedCoverages = Objects.requireNonNull(requestedCoverages, "requestedCoverages").deepCopy();
        this.startDate = Objects.requireNonNull(startDate, "startDate");
        this.endDate = Objects.requireNonNull(endDate, "endDate");
        this.durationDays = durationDays;
        this.evaluationMs = Math.max(0, evaluationMs);
    }

    public QuoteOrigin getOrigin() {
        return origin;
    }

    public UUID getCustomerUserId() {
        return customerUserId;
    }

    public Msisdn getClientMsisdn() {
        return clientMsisdn;
    }

    public UUID getRequestedByUserId() {
        return requestedByUserId;
    }

    public UUID getRequestedByOrgId() {
        return requestedByOrgId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public JsonNode getVehicleSnapshot() {
        return vehicleSnapshot.deepCopy();
    }

    public JsonNode getDriverSnapshot() {
        return driverSnapshot.deepCopy();
    }

    public JsonNode getRequestedCoverages() {
        return requestedCoverages.deepCopy();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getEvaluationMs() {
        return evaluationMs;
    }
}
