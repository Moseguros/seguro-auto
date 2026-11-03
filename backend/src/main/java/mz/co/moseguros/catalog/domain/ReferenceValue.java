package mz.co.moseguros.catalog.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Valor de uma tabela de referência ({@code catalog.reference_value}): marca, modelo ou
 * categoria de carta. São dados mantidos pelo administrador; o rótulo vem da API. Nunca se apaga,
 * só se desactiva.
 */
@Entity
@Table(schema = "catalog", name = "reference_value")
public class ReferenceValue extends BaseEntity {

    public static final Pattern CODE = Pattern.compile("^[A-Z0-9_]{1,40}$");
    public static final int DEFAULT_SORT_ORDER = 100;
    public static final int MAX_SORT_ORDER = 9999;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain", nullable = false, updatable = false, length = 30)
    private ReferenceDomain domain;

    @Column(name = "code", nullable = false, updatable = false, length = 40)
    private String code;

    @Column(name = "label_pt", nullable = false, length = 120)
    private String labelPt;

    @Column(name = "label_en", length = 120)
    private String labelEn;

    @Column(name = "parent_code", updatable = false, length = 40)
    private String parentCode;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected ReferenceValue() {
    }

    /**
     * Novo valor activo. O {@code parentCode} tem de vir só em {@link ReferenceDomain#VEHICLE_MODEL}
     * ({@code REQUIRED}/{@code NOT_ALLOWED}); a existência da marca é verificada pelo serviço.
     *
     * @param sortOrder {@code null} = {@value #DEFAULT_SORT_ORDER}
     * @throws FieldValidationException {@code INVALID_CODE}, {@code REQUIRED}, {@code NOT_ALLOWED} ou
     *                                  {@code OUT_OF_RANGE}
     */
    public static ReferenceValue create(ReferenceDomain domain, String code, String labelPt, String labelEn,
                                        String parentCode, Integer sortOrder) {
        Objects.requireNonNull(domain, "domain");
        checkCode(code);
        checkParent(domain, parentCode);
        ReferenceValue value = new ReferenceValue();
        value.domain = domain;
        value.code = code;
        value.parentCode = domain.hasParent() ? parentCode : null;
        value.labelPt = requireLabel(labelPt);
        value.labelEn = blankToNull(labelEn);
        value.sortOrder = sortOrder(sortOrder == null ? DEFAULT_SORT_ORDER : sortOrder);
        value.active = true;
        return value;
    }

    /** Altera os rótulos e a ordem (o código e a marca não mudam). */
    public void update(String labelPt, String labelEn, Integer sortOrder) {
        String label = requireLabel(labelPt);
        int order = sortOrder(sortOrder == null ? DEFAULT_SORT_ORDER : sortOrder);
        this.labelPt = label;
        this.labelEn = blankToNull(labelEn);
        this.sortOrder = order;
    }

    /** Inactivo → activo; já activo → {@code INVALID_STATE_TRANSITION} (409). */
    public void activate() {
        if (active) {
            throw invalidTransition("activate");
        }
        active = true;
    }

    /** Activo → inactivo; já inactivo → {@code INVALID_STATE_TRANSITION} (409). */
    public void deactivate() {
        if (!active) {
            throw invalidTransition("deactivate");
        }
        active = false;
    }

    /** @throws FieldValidationException {@code INVALID_CODE} no campo {@code code} */
    public static void checkCode(String code) {
        if (code == null || !CODE.matcher(code).matches()) {
            throw new FieldValidationException("code", FieldErrorCodes.INVALID_CODE,
                    "code must match " + CODE.pattern());
        }
    }

    private static void checkParent(ReferenceDomain domain, String parentCode) {
        boolean present = parentCode != null && !parentCode.isBlank();
        if (domain.hasParent() && !present) {
            throw new FieldValidationException("parentCode", FieldErrorCodes.REQUIRED,
                    "parentCode is required for " + domain);
        }
        if (!domain.hasParent() && parentCode != null) {
            throw new FieldValidationException("parentCode", FieldErrorCodes.NOT_ALLOWED,
                    "parentCode is only allowed for " + ReferenceDomain.VEHICLE_MODEL);
        }
    }

    private static String requireLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new FieldValidationException("labelPt", FieldErrorCodes.NOT_BLANK, "labelPt is required");
        }
        return label.trim();
    }

    private static int sortOrder(int sortOrder) {
        if (sortOrder < 0 || sortOrder > MAX_SORT_ORDER) {
            throw new FieldValidationException("sortOrder", FieldErrorCodes.OUT_OF_RANGE,
                    "sortOrder must be between 0 and " + MAX_SORT_ORDER);
        }
        return sortOrder;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private DomainException invalidTransition(String action) {
        return DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                "Reference value cannot " + action + " (active=" + active + ")");
    }

    public ReferenceDomain getDomain() {
        return domain;
    }

    public String getCode() {
        return code;
    }

    public String getLabelPt() {
        return labelPt;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public String getParentCode() {
        return parentCode;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isActive() {
        return active;
    }
}
