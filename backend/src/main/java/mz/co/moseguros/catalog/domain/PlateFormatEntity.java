package mz.co.moseguros.catalog.domain;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.domain.PlateFormat;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Formato de matrícula ({@code catalog.plate_format}), lido pelo {@code DbPlateFormatProvider}.
 * Os campos chegam já validados ({@code PlateFormatValidator}). Nunca se apaga, só se desactiva:
 * as matrículas já gravadas continuam a ler-se com {@code Matricula.restore}.
 */
@Entity
@Table(schema = "catalog", name = "plate_format")
public class PlateFormatEntity extends BaseEntity {

    @Column(name = "code", nullable = false, updatable = false, length = 20)
    private String code;

    @Column(name = "label", nullable = false, length = 80)
    private String label;

    @Column(name = "pattern", nullable = false, length = 200)
    private String pattern;

    @Column(name = "display_template", nullable = false, length = 100)
    private String displayTemplate;

    @Column(name = "example", nullable = false, length = 20)
    private String example;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected PlateFormatEntity() {
    }

    /** Novo formato activo, com campos já validados. */
    public static PlateFormatEntity create(String code, String label, String pattern, String displayTemplate,
                                           String example, int sortOrder) {
        PlateFormatEntity format = new PlateFormatEntity();
        format.code = Objects.requireNonNull(code, "code");
        format.apply(label, pattern, displayTemplate, example, sortOrder);
        format.active = true;
        return format;
    }

    /** Altera tudo excepto o código, com campos já validados. */
    public void update(String label, String pattern, String displayTemplate, String example, int sortOrder) {
        apply(label, pattern, displayTemplate, example, sortOrder);
    }

    private void apply(String label, String pattern, String displayTemplate, String example, int sortOrder) {
        this.label = Objects.requireNonNull(label, "label");
        this.pattern = Objects.requireNonNull(pattern, "pattern");
        this.displayTemplate = Objects.requireNonNull(displayTemplate, "displayTemplate");
        this.example = Objects.requireNonNull(example, "example");
        this.sortOrder = sortOrder;
    }

    /** Inactivo → activo; já activo → {@code INVALID_STATE_TRANSITION} (409). */
    public void activate() {
        if (active) {
            throw invalidTransition("activate");
        }
        active = true;
    }

    /**
     * Activo → inactivo; já inactivo → {@code INVALID_STATE_TRANSITION} (409). Quem chama garante
     * que não é o último activo ({@code LAST_ACTIVE_PLATE_FORMAT}).
     */
    public void deactivate() {
        if (!active) {
            throw invalidTransition("deactivate");
        }
        active = false;
    }

    /** Formato para o {@code PlateParser}; compila o padrão. */
    public PlateFormat toPlateFormat() {
        return new PlateFormat(code, label, Pattern.compile(pattern), displayTemplate, active);
    }

    private DomainException invalidTransition(String action) {
        return DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                "Plate format cannot " + action + " (active=" + active + ")");
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getPattern() {
        return pattern;
    }

    public String getDisplayTemplate() {
        return displayTemplate;
    }

    public String getExample() {
        return example;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isActive() {
        return active;
    }
}
