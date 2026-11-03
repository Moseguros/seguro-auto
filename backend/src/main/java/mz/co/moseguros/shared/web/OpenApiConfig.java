package mz.co.moseguros.shared.web;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Contrato OpenAPI exportado para {@code api/openapi.yaml}.
 *
 * <p>Segurança {@code bearerAuth} (JWT) global; os endpoints públicos declaram
 * {@code @SecurityRequirements} vazio. Todas as operações respondem
 * {@code ProblemDetail} (RFC 7807) em {@code 4XX} e {@code 5XX}. Os value objects
 * partilhados têm esquema próprio (ver {@link ValueObjectsModelConverter}).
 *
 * <p>O documento não pode depender de nada que mude entre execuções (hora, commit,
 * porta), porque o {@code scripts/check-openapi.sh} compara-o com o versionado.
 */
@Configuration
public class OpenApiConfig {

    public static final String TITLE = "MoSeguros API";
    public static final String BEARER_AUTH = "bearerAuth";
    public static final String PROBLEM_DETAIL = "ProblemDetail";
    public static final String FIELD_ERROR = "FieldError";
    public static final String PROBLEM_JSON = "application/problem+json";

    @Bean
    OpenAPI moSegurosOpenApi(ObjectProvider<BuildProperties> build) {
        BuildProperties b = build.getIfAvailable();
        String version = b != null ? b.getVersion() : "dev";
        return new OpenAPI()
                .info(new Info()
                        .title(TITLE)
                        .version(version)
                        .description("API REST da plataforma MoSeguros. Erros em RFC 7807 "
                                + "(application/problem+json) com `code` e `errors[]`."))
                .servers(List.of(new Server().url("/")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    @Bean
    ValueObjectsModelConverter valueObjectsModelConverter() {
        return new ValueObjectsModelConverter();
    }

    /** Acrescenta os esquemas partilhados e as respostas de erro por defeito. */
    @Bean
    OpenApiCustomizer moSegurosOpenApiCustomizer() {
        return openApi -> {
            Components components = openApi.getComponents();
            if (components == null) {
                components = new Components();
                openApi.setComponents(components);
            }
            sharedSchemas().forEach(components::addSchemas);
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().values().forEach(path -> path.readOperations()
                    .forEach(OpenApiConfig::addDefaultErrorResponses));
        };
    }

    static void addDefaultErrorResponses(Operation operation) {
        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }
        responses.putIfAbsent("4XX", problemResponse("Client error (RFC 7807)"));
        responses.putIfAbsent("5XX", problemResponse("Server error (RFC 7807)"));
    }

    private static ApiResponse problemResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(PROBLEM_JSON, new MediaType()
                        .schema(new Schema<>().$ref("#/components/schemas/" + PROBLEM_DETAIL))));
    }

    /** Esquemas de {@code ProblemDetail}, {@code FieldError} e dos value objects. */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static Map<String, Schema> sharedSchemas() {
        Map<String, Schema> schemas = new TreeMap<>();

        schemas.put(FIELD_ERROR, new ObjectSchema()
                .description("Erro de um campo do pedido")
                .addProperty("field", new StringSchema().description("Caminho JSON do campo")
                        .example("vehicle.plate"))
                .addProperty("code", new StringSchema().description("Código do erro do campo")
                        .example("INVALID_PLATE"))
                .addProperty("message", new StringSchema().description("Mensagem técnica (não para o utilizador)"))
                .required(List.of("field", "code")));

        schemas.put(PROBLEM_DETAIL, new ObjectSchema()
                .description("Erro RFC 7807 (application/problem+json)")
                .addProperty("type", new StringSchema().format("uri")
                        .example("https://moseguros.co.mz/problems/validation-error"))
                .addProperty("title", new StringSchema().example("Bad Request"))
                .addProperty("status", new IntegerSchema().format("int32").example(400))
                .addProperty("detail", new StringSchema().example("Request validation failed"))
                .addProperty("instance", new StringSchema().format("uri"))
                .addProperty("code", new StringSchema().description("Código do erro (ErrorCodes)")
                        .example("VALIDATION_ERROR"))
                .addProperty("errors", new ArraySchema()
                        .description("Erros por campo, quando o pedido é inválido")
                        .items(new Schema<>().$ref("#/components/schemas/" + FIELD_ERROR)))
                .required(List.of("type", "title", "status", "code")));

        schemas.put(Money.class.getSimpleName(), new ObjectSchema()
                .description("Valor monetário em centavos")
                .addProperty("amount", new IntegerSchema().format("int64").example(299900L))
                .addProperty("currency", new StringSchema()._enum(List.of(Money.MZN)).example(Money.MZN))
                .required(List.of("amount", "currency")));

        schemas.put(Msisdn.class.getSimpleName(), new StringSchema()
                .description("Telemóvel moçambicano em E.164")
                .pattern("^\\+2588[2-7]\\d{7}$")
                .example("+258841234567"));

        schemas.put(Nuit.class.getSimpleName(), new StringSchema()
                .description("NUIT: 9 dígitos")
                .pattern("^\\d{9}$")
                .example("123456789"));

        schemas.put(Matricula.class.getSimpleName(), new StringSchema()
                .description("Matrícula formatada; os formatos aceites são configuráveis")
                .pattern("^[A-Z0-9]+(-[A-Z0-9]+)*$")
                .example("ABC-123-MC"));

        return schemas;
    }
}
