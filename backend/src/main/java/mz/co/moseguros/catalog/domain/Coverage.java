package mz.co.moseguros.catalog.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Cobertura de uma versão de produto ({@code catalog.coverage}, S2-04). Os valores monetários
 * gravam-se em centavos. Só se cria (e apaga) em versões {@code DRAFT}; validada antes por
 * {@link CoverageRules}.
 */
@Entity
@Table(schema = "catalog", name = "coverage")
public class Coverage extends BaseEntity {

    @Column(name = "product_version_id", nullable = false, updatable = false)
    private UUID productVersionId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, updatable = false, length = 20)
    private CoverageCode code;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "requires_codes", nullable = false, columnDefinition = "text[]")
    private List<String> requiresCodes;

    @Enumerated(EnumType.STRING)
    @Column(name = "capital_type", nullable = false, length = 15)
    private CapitalType capitalType;

    @Column(name = "capital_amount")
    private Long capitalAmount;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "capital_options_amount", columnDefinition = "bigint[]")
    private List<Long> capitalOptionsAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "deductible_type", nullable = false, length = 10)
    private DeductibleType deductibleType;

    @Column(name = "deductible_amount")
    private Long deductibleAmount;

    @Column(name = "deductible_percent", precision = 5, scale = 2)
    private BigDecimal deductiblePercent;

    @Column(name = "deductible_min_amount")
    private Long deductibleMinAmount;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "territorial_scope", nullable = false, columnDefinition = "text[]")
    private List<String> territorialScope;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "exclusions", length = 4000)
    private String exclusions;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected Coverage() {
    }

    /**
     * Cobertura da versão, a partir de dados já validados por {@link CoverageRules}.
     *
     * @param sortOrder posição na lista
     */
    public static Coverage create(ProductVersion version, CoverageSpec spec, int sortOrder) {
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(spec, "spec");
        Coverage c = new Coverage();
        c.productVersionId = version.getId();
        c.insurerId = version.getInsurerId();
        c.code = Objects.requireNonNull(spec.code(), "code");
        c.mandatory = Boolean.TRUE.equals(spec.mandatory());
        c.requiresCodes = new ArrayList<>(spec.requiresOrEmpty().stream().map(Enum::name).toList());
        c.capitalType = Objects.requireNonNull(spec.capitalType(), "capitalType");
        boolean amountCapital = c.capitalType == CapitalType.FIXED || c.capitalType == CapitalType.PER_PERSON;
        c.capitalAmount = amountCapital ? spec.capital().amount() : null;
        c.capitalOptionsAmount = c.capitalType == CapitalType.OPTIONS
                ? new ArrayList<>(spec.capitalOptions().stream().map(Money::amount).toList()) : null;
        c.deductibleType = Objects.requireNonNull(spec.deductibleType(), "deductibleType");
        c.deductibleAmount = c.deductibleType == DeductibleType.FIXED ? spec.deductibleAmount().amount() : null;
        boolean percent = c.deductibleType == DeductibleType.PERCENT;
        c.deductiblePercent = percent ? spec.deductiblePercent().setScale(2) : null;
        c.deductibleMinAmount = percent && spec.deductibleMinAmount() != null
                ? spec.deductibleMinAmount().amount() : null;
        c.territorialScope = new ArrayList<>(spec.scopeOrEmpty());
        c.description = Product.blankToNull(spec.description());
        c.exclusions = Product.blankToNull(spec.exclusions());
        c.sortOrder = sortOrder;
        return c;
    }

    /** Cópia para outra versão (nova versão {@code DRAFT}). */
    public Coverage copyTo(ProductVersion version) {
        return create(version, toSpec(), sortOrder);
    }

    /** Os dados gravados, com os valores em {@link Money}. */
    public CoverageSpec toSpec() {
        return new CoverageSpec(code, mandatory, getRequiresCodes(), capitalType, money(capitalAmount),
                capitalOptionsAmount == null ? null : capitalOptionsAmount.stream().map(Money::ofCentavos).toList(),
                deductibleType, money(deductibleAmount), deductiblePercent, money(deductibleMinAmount),
                List.copyOf(territorialScope), description, exclusions);
    }

    private static Money money(Long centavos) {
        return centavos == null ? null : Money.ofCentavos(centavos);
    }

    public UUID getProductVersionId() {
        return productVersionId;
    }

    public CoverageCode getCode() {
        return code;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public List<CoverageCode> getRequiresCodes() {
        return requiresCodes.stream().map(CoverageCode::valueOf).toList();
    }

    public CapitalType getCapitalType() {
        return capitalType;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
