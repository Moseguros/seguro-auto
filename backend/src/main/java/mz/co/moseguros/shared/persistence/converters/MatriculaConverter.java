package mz.co.moseguros.shared.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import mz.co.moseguros.shared.domain.Matricula;

/**
 * Guarda a matrícula formatada (ex.: {@code ABC-123-MC}) numa coluna e lê-a com
 * {@link Matricula#restore(String, String)}, sem validar de novo.
 *
 * <p>Um conversor só tem uma coluna, por isso o {@code formatCode} não é guardado e
 * fica {@code null} na leitura. A entidade que precise do formato guarda-o numa coluna
 * própria (ver "Questões" do S0-03).
 */
@Converter(autoApply = true)
public class MatriculaConverter implements AttributeConverter<Matricula, String> {

    @Override
    public String convertToDatabaseColumn(Matricula attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public Matricula convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Matricula.restore(dbData, null);
    }
}
