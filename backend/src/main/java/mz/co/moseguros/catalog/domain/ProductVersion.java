package mz.co.moseguros.catalog.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Versão de um produto ({@code catalog.product_version}, S2-04). Só a versão {@code DRAFT} se
 * altera; publicada é imutável (as cotações, propostas e apólices referem-na). As coberturas e os
 * documentos são linhas próprias ({@link Coverage}, {@link ProductDocument}).
 */
@Entity
@Table(schema = "catalog", name = "product_version")
public class ProductVersion extends BaseEntity {

    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "version_no", nullable = false, updatable = false)
    private int versionNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    private ProductVersionStatus status;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "vehicle_categories", nullable = false, columnDefinition = "text[]")
    private List<String> vehicleCategories;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "usages", nullable = false, columnDefinition = "text[]")
    private List<String> usages;

    /** Chaves reservadas: {@code rating} (S3), {@code underwriting} (S4), {@code bonusMalus} (S7). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rules", nullable = false, columnDefinition = "jsonb")
    private JsonNode rules;

    @Column(name = "change_notes", length = 1000)
    private String changeNotes;

    @Column(name = "based_on_version_id", updatable = false)
    private UUID basedOnVersionId;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "published_by", length = 64)
    private String publishedBy;

    protected ProductVersion() {
    }

    /** Versão 1 de um produto novo: {@code DRAFT}, vazia. */
    public static ProductVersion first(Product product) {
        return draft(product, 1, null);
    }

    /**
     * Nova versão {@code DRAFT} que copia categorias, usos e regras de {@code base} (ou vazia sem
     * base). As coberturas e os documentos copiam-se à parte.
     */
    public static ProductVersion draftFrom(Product product, int versionNo, ProductVersion base) {
        ProductVersion version = draft(product, versionNo, base == null ? null : base.getId());
        if (base != null) {
            version.vehicleCategories = new ArrayList<>(base.vehicleCategories);
            version.usages = new ArrayList<>(base.usages);
            version.rules = base.rules.deepCopy();
        }
        return version;
    }

    private static ProductVersion draft(Product product, int versionNo, UUID basedOn) {
        Objects.requireNonNull(product, "product");
        ProductVersion version = new ProductVersion();
        version.productId = product.getId();
        version.insurerId = product.getInsurerId();
        version.versionNo = versionNo;
        version.status = ProductVersionStatus.DRAFT;
        version.vehicleCategories = new ArrayList<>();
        version.usages = new ArrayList<>();
        version.rules = JsonNodeFactory.instance.objectNode();
        version.basedOnVersionId = basedOn;
        return version;
    }

    /** @throws DomainException {@code PRODUCT_VERSION_NOT_EDITABLE} (409) se não estiver {@code DRAFT} */
    public void requireEditable() {
        if (status != ProductVersionStatus.DRAFT) {
            throw DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_EDITABLE,
                    "Only a DRAFT version can be changed");
        }
    }

    /** Elegibilidade e notas. Sem repetidos (a ordem pedida mantém-se). */
    public void updateEligibility(List<VehicleCategory> categories, List<VehicleUsage> usageList, String notes) {
        requireEditable();
        this.vehicleCategories = new ArrayList<>(categories.stream().map(Enum::name).distinct().toList());
        this.usages = new ArrayList<>(usageList.stream().map(Enum::name).distinct().toList());
        this.changeNotes = Product.blankToNull(notes);
    }

    /**
     * Substitui só a chave {@code rating} de {@code rules} (tarifa interna, S3-01 §5); as outras
     * chaves ficam como estão. O conteúdo é validado pelo módulo {@code rating}, não aqui.
     */
    public void replaceRatingRules(JsonNode rating) {
        requireEditable();
        Objects.requireNonNull(rating, "rating");
        ObjectNode copy = rules != null && rules.isObject()
                ? (ObjectNode) rules.deepCopy() : JsonNodeFactory.instance.objectNode();
        copy.set("rating", rating.deepCopy());
        this.rules = copy;
    }

    /** {@code DRAFT} → {@code PUBLISHED}, em vigor desde {@code today}. */
    public void publish(LocalDate today, Instant now, String publishedBy) {
        requireEditable();
        this.status = ProductVersionStatus.PUBLISHED;
        this.validFrom = Objects.requireNonNull(today, "today");
        this.publishedAt = Objects.requireNonNull(now, "now");
        this.publishedBy = Objects.requireNonNull(publishedBy, "publishedBy");
    }

    /** {@code PUBLISHED} → {@code SUPERSEDED} (outra versão foi publicada); deixa de vigorar hoje. */
    public void supersede(LocalDate today) {
        requirePublished();
        this.status = ProductVersionStatus.SUPERSEDED;
        this.validTo = today;
    }

    /** {@code PUBLISHED} → {@code RETIRED} (o produto foi retirado); deixa de vigorar hoje. */
    public void retire(LocalDate today) {
        requirePublished();
        this.status = ProductVersionStatus.RETIRED;
        this.validTo = today;
    }

    private void requirePublished() {
        if (status != ProductVersionStatus.PUBLISHED) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION, "The version is not PUBLISHED");
        }
    }

    public boolean isDraft() {
        return status == ProductVersionStatus.DRAFT;
    }

    public UUID getProductId() {
        return productId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public ProductVersionStatus getStatus() {
        return status;
    }

    public List<VehicleCategory> getVehicleCategories() {
        return vehicleCategories.stream().map(VehicleCategory::valueOf).toList();
    }

    public List<VehicleUsage> getUsages() {
        return usages.stream().map(VehicleUsage::valueOf).toList();
    }

    public JsonNode getRules() {
        return rules.deepCopy();
    }

    public String getChangeNotes() {
        return changeNotes;
    }

    public UUID getBasedOnVersionId() {
        return basedOnVersionId;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public String getPublishedBy() {
        return publishedBy;
    }
}
