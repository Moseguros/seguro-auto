package mz.co.moseguros.paymentcontracts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Utilitários dos testes: o {@code ObjectMapper} como o do Spring Boot e a validação dos schemas. */
final class ContractSupport {

    /** Como o do Spring Boot nas duas aplicações (JavaTimeModule, datas em texto). */
    static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    private static final String ID_PREFIX = "https://moseguros.co.mz/schemas/";
    private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.getInstance(
            SpecVersion.VersionFlag.V202012,
            builder -> builder.schemaMappers(m -> m.mapPrefix(ID_PREFIX, "classpath:schemas/")));
    private static final SchemaValidatorsConfig CONFIG =
            SchemaValidatorsConfig.builder().locale(Locale.ENGLISH).build();
    private static final Map<String, JsonSchema> CACHE = new ConcurrentHashMap<>();

    private ContractSupport() {}

    static List<String> validate(String schemaName, JsonNode document) {
        JsonSchema schema = CACHE.computeIfAbsent(schemaName, n -> {
            JsonSchema s = FACTORY.getSchema(SchemaLocation.of(ID_PREFIX + n), CONFIG);
            s.initializeValidators();
            return s;
        });
        return schema.validate(document).stream().map(ValidationMessage::getMessage).toList();
    }

    static JsonNode sample(String name) {
        try (InputStream in = ContractSupport.class.getResourceAsStream("/samples/" + name)) {
            if (in == null) {
                throw new IllegalArgumentException("sample not found: " + name);
            }
            return MAPPER.readTree(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
