package mz.co.moseguros.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.persistence.BaseEntity;

/**
 * Parâmetro da plataforma ({@code catalog.platform_setting}). As chaves e os tipos só entram por
 * migração; a API só altera o valor. Nunca há segredos nesta tabela: todos os parâmetros são
 * públicos.
 */
@Entity
@Table(schema = "catalog", name = "platform_setting")
public class PlatformSetting extends BaseEntity {

    @Column(name = "key", nullable = false, updatable = false, length = 60)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false, updatable = false, length = 10)
    private SettingValueType valueType;

    @Column(name = "value", nullable = false, length = 500)
    private String value;

    protected PlatformSetting() {
    }

    /** @throws FieldValidationException {@code INVALID_VALUE} no campo {@code value} */
    public void changeValue(String newValue) {
        if (!valueType.accepts(newValue)) {
            throw new FieldValidationException("value", FieldErrorCodes.INVALID_VALUE,
                    "Invalid value for a " + valueType + " setting");
        }
        this.value = newValue;
    }

    public String getKey() {
        return key;
    }

    public SettingValueType getValueType() {
        return valueType;
    }

    public String getValue() {
        return value;
    }
}
