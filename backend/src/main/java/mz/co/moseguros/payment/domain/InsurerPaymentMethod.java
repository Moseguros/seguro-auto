package mz.co.moseguros.payment.domain;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Configuração de uma forma de pagamento de uma seguradora ({@code payment.insurer_payment_method},
 * S5-01 §3). Uma linha por seguradora e método.
 *
 * <p>Não tem valores secretos: {@code secretRefs} é o mapa {@code campo → { path, updatedAt }} com
 * o caminho no cofre ({@link SecretField}). {@code enabled} só muda por {@link #enable} e
 * {@link #disable}.
 */
@Entity
@Table(schema = "payment", name = "insurer_payment_method")
public class InsurerPaymentMethod extends BaseEntity {

    public static final int MIN_INTENT_VALIDITY = 5;
    public static final int MAX_INTENT_VALIDITY = 1440;
    public static final int DEFAULT_INTENT_VALIDITY = 30;
    public static final int MAX_DISPLAY_NAME = 60;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, updatable = false, length = 20)
    private PaymentMethod method;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "enabled_at")
    private Instant enabledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "environment", nullable = false, length = 12)
    private PaymentEnvironment environment = PaymentEnvironment.SANDBOX;

    @Column(name = "display_name", length = MAX_DISPLAY_NAME)
    private String displayName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", nullable = false, columnDefinition = "jsonb")
    private JsonNode settings = JsonNodeFactory.instance.objectNode();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "secret_refs", nullable = false, columnDefinition = "jsonb")
    private JsonNode secretRefs = JsonNodeFactory.instance.objectNode();

    @Column(name = "min_amount")
    private Long minAmount;

    @Column(name = "max_amount")
    private Long maxAmount;

    @Column(name = "intent_validity_minutes", nullable = false)
    private int intentValidityMinutes = DEFAULT_INTENT_VALIDITY;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "last_test_at")
    private Instant lastTestAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_test_result", length = 15)
    private TestResult.Status lastTestResult;

    @Column(name = "last_test_message", length = TestResult.MAX_MESSAGE)
    private String lastTestMessage;

    @Column(name = "last_error_code", length = 40)
    private String lastErrorCode;

    @Column(name = "last_error_at")
    private Instant lastErrorAt;

    protected InsurerPaymentMethod() {
    }

    /** Linha nova, desactivada, com os valores por defeito. */
    public static InsurerPaymentMethod create(UUID insurerId, PaymentMethod method) {
        InsurerPaymentMethod m = new InsurerPaymentMethod();
        m.insurerId = Objects.requireNonNull(insurerId, "insurerId");
        m.method = Objects.requireNonNull(method, "method");
        return m;
    }

    /**
     * Substitui os dados não secretos, já validados pelo serviço ({@code SettingsValidator}). Não
     * muda {@code enabled}: um método activo continua activo e a mudança vale no pedido seguinte.
     */
    public void updateConfig(PaymentEnvironment environment, String displayName, JsonNode settings, Long minAmount,
                             Long maxAmount, int intentValidityMinutes, int sortOrder) {
        Objects.requireNonNull(environment, "environment");
        if (settings == null || !settings.isObject()) {
            throw new IllegalArgumentException("settings must be a JSON object");
        }
        String name = displayName == null || displayName.isBlank() ? null : displayName.strip();
        if (name != null && name.length() > MAX_DISPLAY_NAME) {
            throw new IllegalArgumentException("displayName too long");
        }
        if ((minAmount != null && minAmount <= 0) || (maxAmount != null && maxAmount <= 0)
                || (minAmount != null && maxAmount != null && minAmount > maxAmount)) {
            throw new IllegalArgumentException("Invalid amount limits");
        }
        if (intentValidityMinutes < MIN_INTENT_VALIDITY || intentValidityMinutes > MAX_INTENT_VALIDITY) {
            throw new IllegalArgumentException("intentValidityMinutes out of range");
        }
        this.environment = environment;
        this.displayName = name;
        this.settings = settings.deepCopy();
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.intentValidityMinutes = intentValidityMinutes;
        this.sortOrder = sortOrder;
    }

    /** Regista (ou actualiza) a referência de um segredo do método, depois de gravado no cofre. */
    public void putSecretRef(SecretField field, String path, Instant updatedAt) {
        requireOwnField(field);
        ObjectNode refs = mutableRefs();
        ObjectNode entry = refs.putObject(field.fieldName());
        entry.put("path", Objects.requireNonNull(path, "path"));
        entry.put("updatedAt", Objects.requireNonNull(updatedAt, "updatedAt").toString());
        this.secretRefs = refs;
    }

    /** Retira a referência; {@code true} se existia. */
    public boolean removeSecretRef(SecretField field) {
        requireOwnField(field);
        if (!secretRefs.has(field.fieldName())) {
            return false;
        }
        ObjectNode refs = mutableRefs();
        refs.remove(field.fieldName());
        this.secretRefs = refs;
        return true;
    }

    public Optional<String> secretPath(SecretField field) {
        JsonNode path = secretRefs.path(field.fieldName()).path("path");
        return path.isTextual() ? Optional.of(path.asText()) : Optional.empty();
    }

    public Optional<Instant> secretUpdatedAt(SecretField field) {
        JsonNode at = secretRefs.path(field.fieldName()).path("updatedAt");
        return at.isTextual() ? Optional.of(Instant.parse(at.asText())) : Optional.empty();
    }

    /** Caminhos de todos os segredos configurados, por campo (pela ordem de {@link SecretField}). */
    public Map<SecretField, String> secretPaths() {
        Map<SecretField, String> paths = new LinkedHashMap<>();
        for (SecretField f : SecretField.of(method)) {
            secretPath(f).ifPresent(p -> paths.put(f, p));
        }
        return paths;
    }

    /**
     * Activa o método.
     *
     * @param validation erros da validação completa (schema com os obrigatórios, regras da secção 4
     *                   e segredos obrigatórios); vazio = completo
     * @throws DomainException 409 {@code INVALID_STATE_TRANSITION} se já estiver activo, ou 409
     *                         {@code PAYMENT_METHOD_INCOMPLETE} com {@code errors[]}
     */
    public void enable(List<FieldError> validation, Clock clock) {
        if (enabled) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION, "The payment method is already enabled");
        }
        if (validation != null && !validation.isEmpty()) {
            throw DomainException.of(ErrorCodes.PAYMENT_METHOD_INCOMPLETE,
                    "The payment method configuration is incomplete").withProperty("errors", List.copyOf(validation));
        }
        this.enabled = true;
        this.enabledAt = clock.instant();
    }

    /** @throws DomainException 409 {@code INVALID_STATE_TRANSITION} se já estiver inactivo */
    public void disable() {
        if (!enabled) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION, "The payment method is already disabled");
        }
        this.enabled = false;
    }

    /** Resultado do teste de ligação; um {@code OK} limpa o último erro de configuração (S5-04 §5). */
    public void recordTest(TestResult result, Instant at) {
        this.lastTestResult = result.status();
        this.lastTestMessage = result.message();
        this.lastTestAt = Objects.requireNonNull(at, "at");
        if (result.status() == TestResult.Status.OK) {
            this.lastErrorCode = null;
            this.lastErrorAt = null;
        }
    }

    private ObjectNode mutableRefs() {
        return secretRefs != null && secretRefs.isObject() ? ((ObjectNode) secretRefs).deepCopy()
                : JsonNodeFactory.instance.objectNode();
    }

    private void requireOwnField(SecretField field) {
        if (Objects.requireNonNull(field, "field").method() != method) {
            throw new IllegalArgumentException(field + " is not a secret of " + method);
        }
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getEnabledAt() {
        return enabledAt;
    }

    public PaymentEnvironment getEnvironment() {
        return environment;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Cópia de {@code settings}. */
    public JsonNode getSettings() {
        return settings.deepCopy();
    }

    /** Cópia de {@code secret_refs} (só caminhos e datas). */
    public JsonNode getSecretRefs() {
        return secretRefs.deepCopy();
    }

    public Long getMinAmount() {
        return minAmount;
    }

    public Long getMaxAmount() {
        return maxAmount;
    }

    public int getIntentValidityMinutes() {
        return intentValidityMinutes;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public Instant getLastTestAt() {
        return lastTestAt;
    }

    public TestResult.Status getLastTestResult() {
        return lastTestResult;
    }

    public String getLastTestMessage() {
        return lastTestMessage;
    }

    public String getLastErrorCode() {
        return lastErrorCode;
    }

    public Instant getLastErrorAt() {
        return lastErrorAt;
    }
}
