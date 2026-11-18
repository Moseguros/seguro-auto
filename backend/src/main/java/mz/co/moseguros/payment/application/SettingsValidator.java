package mz.co.moseguros.payment.application;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.json.JsonSchemas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

/**
 * Validação dos dados de uma forma de pagamento (S5-01 §4): o JSON Schema do método
 * ({@code schemas/payment-method-<método>.json}) e as regras que ele não cobre, como erros de
 * campo ({@code settings.<caminho>}, {@code secrets.<campo>}, {@code maxAmount}).
 *
 * <p>Ao gravar ({@link Mode#SAVE}) valida-se o que vier, sem exigir os obrigatórios (a seguradora
 * pode gravar a meio); ao activar ({@link Mode#ENABLE}) exigem-se também os obrigatórios.
 */
@Component
public class SettingsValidator {

    /** Gravação ({@code PUT}) ou activação ({@code enable}). */
    public enum Mode {
        SAVE,
        ENABLE
    }

    private static final String SETTINGS = "settings";
    private static final String SECRETS = "secrets";

    private final BiPredicate<String, UUID> entityInUse;
    private final boolean requireHostKeyFingerprint;

    @Autowired
    public SettingsValidator(PaymentSystemLookups lookups, Environment environment) {
        this(lookups::entityInUse, !environment.acceptsProfiles(Profiles.of("local", "test")));
    }

    /**
     * @param entityInUse               {@code (entity, insurerId)} → há outra seguradora com a entidade
     * @param requireHostKeyFingerprint exigir {@code sftp.hostKeyFingerprint} ao activar (fora de
     *                                  {@code local} e {@code test})
     */
    SettingsValidator(BiPredicate<String, UUID> entityInUse, boolean requireHostKeyFingerprint) {
        this.entityInUse = Objects.requireNonNull(entityInUse, "entityInUse");
        this.requireHostKeyFingerprint = requireHostKeyFingerprint;
    }

    /** Nome do ficheiro do JSON Schema do método. */
    public static String schemaName(PaymentMethod method) {
        return "payment-method-" + method.lowerName() + ".json";
    }

    /** Conteúdo do JSON Schema do método (a web gera o formulário a partir dele, S5-12). */
    public static JsonNode schema(PaymentMethod method) {
        return JsonSchemas.schemaNode(schemaName(method));
    }

    /**
     * {@code settings} e as regras da secção 4 (sem os segredos nem os limites). Vazio = válido.
     *
     * @param insurerId seguradora dona (para {@code ENTITY_IN_USE})
     */
    public List<FieldError> validateSettings(PaymentMethod method, JsonNode settings, UUID insurerId, Mode mode) {
        if (settings == null || !settings.isObject()) {
            return List.of(new FieldError(SETTINGS, FieldErrorCodes.INVALID_FORMAT, "settings must be an object"));
        }
        Errors errors = new Errors();
        Set<String> schemaFields = new LinkedHashSet<>();
        for (JsonSchemas.Issue issue : JsonSchemas.issues(schemaName(method), settings)) {
            if (mode == Mode.SAVE && "required".equals(issue.keyword())) {
                continue;
            }
            String field = schemaField(issue);
            schemaFields.add(field);
            errors.add(field, codeOf(issue.keyword()), issue.message());
        }
        if (method == PaymentMethod.BANK_REFERENCE) {
            bankReferenceRules(settings, insurerId, mode, errors, schemaFields);
        }
        return errors.list();
    }

    /** Limites do valor: mínimo maior que o máximo → {@code maxAmount} {@code MIN_GREATER_THAN_MAX}. */
    public List<FieldError> validateLimits(Long minAmount, Long maxAmount) {
        Errors errors = new Errors();
        if (minAmount != null && minAmount <= 0) {
            errors.add("minAmount", FieldErrorCodes.OUT_OF_RANGE, "minAmount must be greater than zero");
        }
        if (maxAmount != null && maxAmount <= 0) {
            errors.add("maxAmount", FieldErrorCodes.OUT_OF_RANGE, "maxAmount must be greater than zero");
        }
        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            errors.add("maxAmount", FieldErrorCodes.MIN_GREATER_THAN_MAX, "minAmount is greater than maxAmount");
        }
        return errors.list();
    }

    /**
     * Segredos enviados num {@code PUT}: só os campos do método que se aplicam com {@code settings}
     * ({@code NOT_ALLOWED}), não vazios ({@code REQUIRED}), até {@value SecretField#MAX_LENGTH}
     * caracteres ({@code OUT_OF_RANGE}) e, para {@code publicKey}, uma chave RSA pública
     * ({@code INVALID_PUBLIC_KEY}). As mensagens nunca contêm o valor.
     */
    public List<FieldError> validateSecrets(PaymentMethod method, Map<String, String> secrets, JsonNode settings) {
        Errors errors = new Errors();
        if (secrets == null) {
            return errors.list();
        }
        for (Map.Entry<String, String> e : secrets.entrySet()) {
            String field = SECRETS + "." + e.getKey();
            SecretField secret = SecretField.of(method, e.getKey()).orElse(null);
            if (secret == null || !secret.applies(settings)) {
                errors.add(field, FieldErrorCodes.NOT_ALLOWED, "Secret not allowed for this payment method");
                continue;
            }
            String value = e.getValue();
            if (value == null || value.isBlank()) {
                errors.add(field, FieldErrorCodes.REQUIRED, "Secret value is empty");
            } else if (value.length() > SecretField.MAX_LENGTH) {
                errors.add(field, FieldErrorCodes.OUT_OF_RANGE, "Secret value is too long");
            } else if (secret == SecretField.MPESA_PUBLIC_KEY && !isRsaPublicKey(value)) {
                errors.add(field, FieldErrorCodes.INVALID_PUBLIC_KEY, "Not an RSA public key (Base64 DER or PEM)");
            }
        }
        return errors.list();
    }

    /** Segredos obrigatórios com estes {@code settings} que não estão configurados → {@code REQUIRED}. */
    public List<FieldError> missingSecrets(PaymentMethod method, JsonNode settings, Predicate<SecretField> configured) {
        Errors errors = new Errors();
        for (SecretField f : SecretField.of(method)) {
            if (f.required(settings) && !configured.test(f)) {
                errors.add(SECRETS + "." + f.fieldName(), FieldErrorCodes.REQUIRED, "Secret is required");
            }
        }
        return errors.list();
    }

    /** Valores por defeito do schema (o que um método ainda não configurado mostra). */
    public static ObjectNode defaults(PaymentMethod method) {
        return withDefaults(method, JsonNodeFactory.instance.objectNode());
    }

    /**
     * Cópia de {@code settings} com os {@code default} do schema nos campos em falta (também dentro
     * dos objectos, ex.: {@code ports}, {@code sftp}). Não altera os valores enviados.
     */
    public static ObjectNode withDefaults(PaymentMethod method, JsonNode settings) {
        ObjectNode copy = settings != null && settings.isObject() ? ((ObjectNode) settings).deepCopy()
                : JsonNodeFactory.instance.objectNode();
        applyDefaults(schema(method), copy);
        return copy;
    }

    private static void applyDefaults(JsonNode schema, ObjectNode target) {
        JsonNode properties = schema.path("properties");
        Iterator<Map.Entry<String, JsonNode>> it = properties.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> p = it.next();
            JsonNode propSchema = p.getValue();
            if (propSchema.has("default") && !target.has(p.getKey())) {
                target.set(p.getKey(), propSchema.get("default").deepCopy());
            } else if (propSchema.path("properties").isObject() && hasDefaults(propSchema)) {
                JsonNode current = target.get(p.getKey());
                if (current == null) {
                    applyDefaults(propSchema, target.putObject(p.getKey()));
                } else if (current.isObject()) {
                    applyDefaults(propSchema, (ObjectNode) current);
                }
            }
        }
    }

    private static boolean hasDefaults(JsonNode schema) {
        Iterator<JsonNode> it = schema.path("properties").elements();
        while (it.hasNext()) {
            JsonNode p = it.next();
            if (p.has("default") || (p.path("properties").isObject() && hasDefaults(p))) {
                return true;
            }
        }
        return false;
    }

    private void bankReferenceRules(JsonNode s, UUID insurerId, Mode mode, Errors errors, Set<String> schemaFields) {
        String format = text(s.path("fileFormat"));
        if ("BMEPS".equals(format) && mode == Mode.ENABLE) {
            for (String f : new String[] {"bmepsCompanyId", "bmepsProfileId"}) {
                if (!s.hasNonNull(f)) {
                    errors.add(SETTINGS + "." + f, FieldErrorCodes.REQUIRED, f + " is required with BMEPS");
                }
            }
        }
        if ("MEPS".equals(format)) {
            for (String f : new String[] {"bmepsCompanyId", "bmepsProfileId"}) {
                if (s.has(f)) {
                    errors.add(SETTINGS + "." + f, FieldErrorCodes.NOT_ALLOWED, f + " is not allowed with MEPS");
                }
            }
        }
        String entity = text(s.path("entity"));
        if (entity != null && !schemaFields.contains(SETTINGS + ".entity") && entityInUse.test(entity, insurerId)) {
            errors.add(SETTINGS + ".entity", FieldErrorCodes.ENTITY_IN_USE, "Entity already used by another insurer");
        }
        JsonNode sftp = s.path("sftp");
        if (!sftp.isObject()) {
            return;
        }
        JsonNode move = sftp.path("moveAfterProcessing");
        boolean moveAfterProcessing = !move.isBoolean() || move.asBoolean();
        if (moveAfterProcessing) {
            String inbound = text(sftp.path("inboundDir"));
            String processed = text(sftp.path("processedDir"));
            String error = text(sftp.path("errorDir"));
            if (processed != null && (processed.equals(inbound) || processed.equals(error))) {
                errors.add(SETTINGS + ".sftp.processedDir", FieldErrorCodes.DIRECTORY_CONFLICT,
                        "processedDir must differ from inboundDir and errorDir");
            } else if (error != null && error.equals(inbound)) {
                errors.add(SETTINGS + ".sftp.errorDir", FieldErrorCodes.DIRECTORY_CONFLICT,
                        "errorDir must differ from inboundDir");
            }
            if (mode == Mode.ENABLE) {
                if (processed == null) {
                    errors.add(SETTINGS + ".sftp.processedDir", FieldErrorCodes.REQUIRED,
                            "processedDir is required when moveAfterProcessing is true");
                }
                if (error == null) {
                    errors.add(SETTINGS + ".sftp.errorDir", FieldErrorCodes.REQUIRED,
                            "errorDir is required when moveAfterProcessing is true");
                }
            }
        }
        if (mode == Mode.ENABLE && requireHostKeyFingerprint && !sftp.hasNonNull("hostKeyFingerprint")) {
            errors.add(SETTINGS + ".sftp.hostKeyFingerprint", FieldErrorCodes.REQUIRED,
                    "hostKeyFingerprint is required to enable outside local and test");
        }
    }

    /** {@code settings.} + caminho do erro (+ a propriedade em falta ou a mais). */
    static String schemaField(JsonSchemas.Issue issue) {
        StringBuilder field = new StringBuilder(SETTINGS);
        String pointer = issue.pointer() == null ? "" : issue.pointer();
        List<String> segments = new ArrayList<>();
        for (String segment : pointer.split("/")) {
            if (!segment.isEmpty()) {
                segments.add(segment.replace("~1", "/").replace("~0", "~"));
            }
        }
        String property = issue.property();
        boolean appendProperty = property != null && ("required".equals(issue.keyword())
                || "additionalProperties".equals(issue.keyword()))
                && (segments.isEmpty() || !segments.get(segments.size() - 1).equals(property));
        if (appendProperty) {
            segments.add(property);
        }
        for (String s : segments) {
            field.append('.').append(s);
        }
        return field.toString();
    }

    /** Palavra-chave do JSON Schema → código do erro de campo. */
    static String codeOf(String keyword) {
        if (keyword == null) {
            return FieldErrorCodes.INVALID_FORMAT;
        }
        return switch (keyword) {
            case "required" -> FieldErrorCodes.REQUIRED;
            case "minimum", "maximum", "exclusiveMinimum", "exclusiveMaximum", "minLength", "maxLength",
                    "minItems", "maxItems", "minProperties", "maxProperties" -> FieldErrorCodes.OUT_OF_RANGE;
            case "additionalProperties", "unevaluatedProperties" -> FieldErrorCodes.NOT_ALLOWED;
            default -> FieldErrorCodes.INVALID_FORMAT; // pattern, format, enum, const, type
        };
    }

    /** Chave RSA pública em PEM ({@code BEGIN PUBLIC KEY}) ou em Base64 DER (X.509 SubjectPublicKeyInfo). */
    static boolean isRsaPublicKey(String value) {
        String body = value.strip();
        if (body.startsWith("-----BEGIN PUBLIC KEY-----")) {
            int end = body.indexOf("-----END PUBLIC KEY-----");
            if (end < 0) {
                return false;
            }
            body = body.substring("-----BEGIN PUBLIC KEY-----".length(), end);
        } else if (body.startsWith("-----")) {
            return false;
        }
        try {
            byte[] der = Base64.getMimeDecoder().decode(body.replaceAll("\\s", ""));
            if (der.length == 0) {
                return false;
            }
            PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
            return key instanceof RSAPublicKey rsa && rsa.getModulus().bitLength() >= 1024;
        } catch (IllegalArgumentException | java.security.GeneralSecurityException e) {
            return false;
        }
    }

    private static String text(JsonNode node) {
        return node != null && node.isTextual() && !node.asText().isEmpty() ? node.asText() : null;
    }

    /** Erros sem repetir o par campo + código, pela ordem em que aparecem. */
    private static final class Errors {

        private final List<FieldError> list = new ArrayList<>();
        private final Set<String> seen = new LinkedHashSet<>();

        void add(String field, String code, String message) {
            if (seen.add(field + "|" + code)) {
                list.add(new FieldError(field, code, message));
            }
        }

        List<FieldError> list() {
            return List.copyOf(list);
        }
    }
}
