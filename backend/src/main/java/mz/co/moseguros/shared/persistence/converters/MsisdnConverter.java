package mz.co.moseguros.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import mz.co.moseguros.shared.domain.Msisdn;

/**
 * Guarda o telemóvel normalizado ({@code 258841234567}).
 */
@Converter(autoApply = true)
public class MsisdnConverter implements AttributeConverter<Msisdn, String> {

    @Override
    public String convertToDatabaseColumn(Msisdn attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public Msisdn convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Msisdn(dbData);
    }
}
