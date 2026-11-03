package mz.co.moseguros.catalog.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Produto de uma seguradora ({@code catalog.product}, S2-04). O código não muda; o tipo só muda
 * enquanto o produto nunca foi publicado; retirar é irreversível e bloqueia qualquer escrita.
 */
@Entity
@Table(schema = "catalog", name = "product")
public class Product extends BaseEntity {

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "code", nullable = false, updatable = false, length = 30)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ProductType type;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private ProductStatus status;

    @Column(name = "retired_at")
    private Instant retiredAt;

    protected Product() {
    }

    /** Produto novo, em {@code DRAFT}. O formato do código é validado no pedido. */
    public static Product create(UUID insurerId, String code, String name, ProductType type, String description) {
        Product product = new Product();
        product.insurerId = Objects.requireNonNull(insurerId, "insurerId");
        product.code = Objects.requireNonNull(code, "code");
        product.name = Objects.requireNonNull(name, "name").trim();
        product.type = Objects.requireNonNull(type, "type");
        product.description = blankToNull(description);
        product.status = ProductStatus.DRAFT;
        return product;
    }

    /**
     * Altera nome, tipo e descrição.
     *
     * @throws DomainException {@code PRODUCT_RETIRED} ou {@code PRODUCT_TYPE_LOCKED} (409)
     */
    public void update(String name, ProductType type, String description) {
        requireNotRetired();
        Objects.requireNonNull(type, "type");
        if (type != this.type && status != ProductStatus.DRAFT) {
            throw DomainException.of(ErrorCodes.PRODUCT_TYPE_LOCKED,
                    "The type cannot change after the product was published");
        }
        this.name = Objects.requireNonNull(name, "name").trim();
        this.type = type;
        this.description = blankToNull(description);
    }

    /** Retira o produto (irreversível). @throws DomainException {@code PRODUCT_RETIRED} (409) */
    public void retire(Instant now) {
        requireNotRetired();
        this.status = ProductStatus.RETIRED;
        this.retiredAt = Objects.requireNonNull(now, "now");
    }

    /** Primeira publicação de uma versão: {@code DRAFT} → {@code PUBLISHED}. */
    public void markPublished() {
        requireNotRetired();
        if (status == ProductStatus.DRAFT) {
            status = ProductStatus.PUBLISHED;
        }
    }

    /** @throws DomainException {@code PRODUCT_RETIRED} (409) */
    public void requireNotRetired() {
        if (isRetired()) {
            throw DomainException.of(ErrorCodes.PRODUCT_RETIRED, "The product was retired");
        }
    }

    public boolean isRetired() {
        return status == ProductStatus.RETIRED;
    }

    static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public Instant getRetiredAt() {
        return retiredAt;
    }
}
