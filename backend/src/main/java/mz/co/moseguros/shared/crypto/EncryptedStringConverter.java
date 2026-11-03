package mz.co.moseguros.shared.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

/**
 * Grava um {@code String} cifrado ({@code bytea}) com o {@link CryptoService}. No Java o
 * valor está em claro; na base de dados nunca. Uso:
 * {@code @Convert(converter = EncryptedStringConverter.class)}.
 *
 * <p>É um bean Spring: o Hibernate obtém-no pelo {@code SpringBeanContainer} que o Spring
 * Boot configura.
 */
@Component
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, byte[]> {

    private final CryptoService cryptoService;

    public EncryptedStringConverter(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public byte[] convertToDatabaseColumn(String attribute) {
        return cryptoService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(byte[] dbData) {
        return cryptoService.decrypt(dbData);
    }
}
