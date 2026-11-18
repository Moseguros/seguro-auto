package mz.co.moseguros.shared.json;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.PathType;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

/**
 * Validação genérica por JSON Schema (draft 2020-12) de schemas guardados no classpath, em
 * {@code backend/src/main/resources/schemas/} (S3-01: {@code rating-v1.json} e
 * {@code charges-v1.json}; o S5 reutiliza-a).
 *
 * <p>Os schemas identificam-se pelo nome do ficheiro e o seu {@code $id} é
 * {@value #ID_PREFIX}{@code <nome>}: o prefixo é mapeado para {@code classpath:schemas/}, por isso
 * nada é descarregado da rede. Cada schema é lido uma vez e guardado em memória.
 *
 * <p>Sem estado mutável visível: pode ser usada em código de domínio e em testes sem Spring.
 */
public final class JsonSchemas {

    /** Prefixo do {@code $id} dos schemas da MoSeguros. */
    public static final String ID_PREFIX = "https://moseguros.co.mz/schemas/";

    private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012,
            builder -> builder.schemaMappers(mappers -> mappers.mapPrefix(ID_PREFIX, "classpath:schemas/")));

    private static final SchemaValidatorsConfig CONFIG = SchemaValidatorsConfig.builder()
            .pathType(PathType.JSON_POINTER)
            .locale(Locale.ENGLISH)
            .build();

    private static final Map<String, JsonSchema> CACHE = new ConcurrentHashMap<>();

    private JsonSchemas() {
    }

    /**
     * Erro de validação.
     *
     * @param pointer ponteiro JSON (RFC 6901) do elemento em causa; {@code ""} é a raiz
     * @param message mensagem técnica do validador, em inglês
     */
    public record Violation(String pointer, String message) {
    }

    /**
     * Valida {@code document} contra o schema {@code schemaName} (ex.: {@code rating-v1.json}).
     * Devolve todos os erros, ordenados pelo ponteiro e depois pela mensagem; vazio = válido.
     *
     * @throws IllegalArgumentException se o schema não existir no classpath
     */
    public static List<Violation> validate(String schemaName, JsonNode document) {
        Objects.requireNonNull(schemaName, "schemaName");
        Objects.requireNonNull(document, "document");
        return schema(schemaName).validate(document).stream()
                .map(JsonSchemas::violation)
                .distinct()
                .sorted(Comparator.comparing(Violation::pointer).thenComparing(Violation::message))
                .toList();
    }

    /**
     * Erro de validação com a palavra-chave do JSON Schema que falhou (S5-01: os erros de campo das
     * formas de pagamento dependem dela).
     *
     * @param pointer  ponteiro JSON (RFC 6901) do elemento em causa; {@code ""} é a raiz
     * @param keyword  palavra-chave que falhou ({@code required}, {@code pattern}, {@code maximum}…)
     * @param property propriedade em falta ({@code required}) ou a mais ({@code additionalProperties});
     *                 {@code null} nas outras
     * @param message  mensagem técnica do validador, em inglês
     */
    public record Issue(String pointer, String keyword, String property, String message) {
    }

    /** Como {@link #validate}, mas com a palavra-chave e a propriedade de cada erro. */
    public static List<Issue> issues(String schemaName, JsonNode document) {
        Objects.requireNonNull(schemaName, "schemaName");
        Objects.requireNonNull(document, "document");
        return schema(schemaName).validate(document).stream()
                .map(m -> new Issue(m.getInstanceLocation() == null ? "" : m.getInstanceLocation().toString(),
                        m.getType(), m.getProperty(), m.getError() != null ? m.getError() : m.getMessage()))
                .distinct()
                .sorted(Comparator.comparing(Issue::pointer).thenComparing(Issue::keyword)
                        .thenComparing(i -> i.property() == null ? "" : i.property()))
                .toList();
    }

    /** Conteúdo do schema {@code schemaName} (cópia), ex.: para a web gerar um formulário. */
    public static JsonNode schemaNode(String schemaName) {
        return schema(schemaName).getSchemaNode().deepCopy();
    }

    private static JsonSchema schema(String name) {
        return CACHE.computeIfAbsent(name, n -> {
            try {
                JsonSchema schema = FACTORY.getSchema(SchemaLocation.of(ID_PREFIX + n), CONFIG);
                schema.initializeValidators();
                return schema;
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Unknown or invalid JSON schema: " + n, e);
            }
        });
    }

    private static Violation violation(ValidationMessage message) {
        String pointer = message.getInstanceLocation() == null ? "" : message.getInstanceLocation().toString();
        String text = message.getError() != null ? message.getError() : message.getMessage();
        return new Violation(pointer, text);
    }
}
