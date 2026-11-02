package mz.co.moseguros.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import mz.co.moseguros.shared.domain.Nuit;

/**
 * Guarda o NUIT em claro (9 dígitos). Serve para o NUIT de organizações, que é público;
 * o NUIT de pessoas singulares é cifrado com o {@code CryptoService} (Sprint 1, AGENTS.md §6)
 * e não usa este conversor.
 */
@Converter
public class NuitConverter implements AttributeConverter<Nuit, String> {

    @Override
    public String convertToDatabaseColumn(Nuit attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public Nuit convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Nuit(dbData);
    }
}
