package mz.co.moseguros.payment.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PreDestroy;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.application.SettingsValidator.Mode;
import mz.co.moseguros.payment.domain.InsurerPaymentMethod;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import mz.co.moseguros.payment.domain.PaymentEnvironment;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.payment.domain.TestResult;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.secrets.SecretStore;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Formas de pagamento da seguradora do utilizador (S5-01 §5): consultar, gravar (segredos no cofre,
 * referências na BD), activar, desactivar e testar. Só em âmbito {@code ORG} de uma seguradora
 * (outro âmbito → 403 {@code FORBIDDEN}); a seguradora é sempre a do token. O RLS mostra só as
 * linhas da própria.
 *
 * <p>Os valores dos segredos nunca saem daqui: não vão para respostas, logs nem auditoria.
 */
@Service
public class PaymentMethodConfigService {

    /** Tempo total do teste de um método (S5-01 §5). */
    static final Duration TEST_TIMEOUT = Duration.ofSeconds(15);
    static final String TIMEOUT = "TIMEOUT";
    static final String TEST_ERROR = "TEST_ERROR";
    static final int MAX_SORT_ORDER = 99;

    private static final Logger log = LoggerFactory.getLogger(PaymentMethodConfigService.class);

    private final InsurerPaymentMethodRepository methods;
    private final SettingsValidator validator;
    private final SecretStore secrets;
    private final OrganizationApi organizations;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final Clock clock;
    private final TransactionTemplate tx;
    private final Map<PaymentMethod, PaymentMethodTester> testers = new EnumMap<>(PaymentMethod.class);
    private final ExecutorService testExecutor = Executors.newCachedThreadPool(new TesterThreads());

    public PaymentMethodConfigService(InsurerPaymentMethodRepository methods, SettingsValidator validator,
                                      SecretStore secrets, OrganizationApi organizations, AuditService audit,
                                      CurrentUser currentUser, Clock clock, PlatformTransactionManager transactions,
                                      List<PaymentMethodTester> testers) {
        this.methods = methods;
        this.validator = validator;
        this.secrets = secrets;
        this.organizations = organizations;
        this.audit = audit;
        this.currentUser = currentUser;
        this.clock = clock;
        this.tx = new TransactionTemplate(transactions);
        for (PaymentMethodTester t : testers) {
            if (this.testers.put(t.method(), t) != null) {
                throw new IllegalStateException("Two PaymentMethodTester beans for " + t.method());
            }
        }
    }

    @PreDestroy
    void shutdown() {
        testExecutor.shutdownNow();
    }

    // Vistas (sem valores secretos)

    /** Estado de um segredo: configurado, obrigatório com os {@code settings} actuais, última gravação. */
    public record SecretStatus(boolean configured, boolean required, Instant updatedAt) {
    }

    public record LastTest(Instant at, TestResult.Status result, String message) {
    }

    public record LastError(String code, Instant at) {
    }

    /**
     * Uma forma de pagamento como a seguradora a vê. Sem linha: {@code configured = false}, os valores
     * por defeito e {@code version = null}.
     */
    public record MethodItem(PaymentMethod method, boolean configured, boolean enabled, PaymentEnvironment environment,
                             String displayName, JsonNode settings, JsonNode settingsSchema,
                             Map<String, SecretStatus> secrets, Long minAmount, Long maxAmount,
                             int intentValidityMinutes, int sortOrder, LastTest lastTest, LastError lastError,
                             Long version, Instant updatedAt) {
    }

    /**
     * Pedido de gravação.
     *
     * @param secrets campo → valor; ausente = mantém o segredo actual
     * @param version {@code null} só na criação
     */
    public record UpdateCommand(PaymentEnvironment environment, String displayName, JsonNode settings,
                                Map<String, String> secrets, Long minAmount, Long maxAmount,
                                Integer intentValidityMinutes, Integer sortOrder, Long version) {
    }

    public record TestOutcome(TestResult.Status result, String message, Instant testedAt) {
    }

    // Casos de uso

    /** Os dois métodos, pela ordem {@code MPESA}, {@code BANK_REFERENCE}. */
    @PreAuthorize("hasAuthority('payment:configure')")
    @Transactional(readOnly = true)
    public List<MethodItem> list() {
        UUID insurerId = requireInsurer();
        Map<PaymentMethod, InsurerPaymentMethod> rows = new EnumMap<>(PaymentMethod.class);
        for (InsurerPaymentMethod m : methods.findByInsurerId(insurerId)) {
            rows.put(m.getMethod(), m);
        }
        List<MethodItem> items = new ArrayList<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            InsurerPaymentMethod row = rows.get(method);
            items.add(row == null ? unconfigured(method) : item(row));
        }
        return items;
    }

    /**
     * Cria ou actualiza a configuração (sem mudar {@code enabled}). Ordem: validar tudo → gravar os
     * segredos novos no cofre → gravar a linha com as referências. Cofre indisponível → 503
     * {@code SECRET_STORE_UNAVAILABLE} e nada é gravado na BD.
     *
     * @throws DomainException 404 {@code PAYMENT_METHOD_NOT_FOUND}, 400 {@code VALIDATION_ERROR},
     *                         409 {@code CONCURRENT_MODIFICATION}, 503 {@code SECRET_STORE_UNAVAILABLE}
     */
    @PreAuthorize("hasAuthority('payment:configure')")
    @Transactional
    public MethodItem update(String rawMethod, UpdateCommand command) {
        UUID insurerId = requireInsurer();
        PaymentMethod method = parse(rawMethod);
        Objects.requireNonNull(command, "command");
        Optional<InsurerPaymentMethod> existing = methods.findByInsurerIdAndMethod(insurerId, method);

        JsonNode newSettings = command.settings() == null ? null : SettingsValidator.withDefaults(method, command.settings());
        List<FieldError> errors = new ArrayList<>(requestErrors(command));
        if (command.settings() != null) {
            errors.addAll(validator.validateSettings(method, command.settings(), insurerId, Mode.SAVE));
        }
        errors.addAll(validator.validateLimits(command.minAmount(), command.maxAmount()));
        if (newSettings != null) {
            errors.addAll(validator.validateSecrets(method, command.secrets(), newSettings));
        }
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        requireVersion(existing.orElse(null), command.version());

        InsurerPaymentMethod row = existing.orElseGet(() -> InsurerPaymentMethod.create(insurerId, method));
        Map<String, Object> before = existing.map(PaymentMethodConfigService::auditState).orElse(null);
        Map<SecretField, String> previousPaths = row.secretPaths();

        // 1. cofre: segredos novos e os que deixam de se aplicar (falha → nada na BD)
        Instant now = clock.instant();
        Set<SecretField> written = new LinkedHashSet<>();
        Map<String, String> sent = command.secrets() == null ? Map.of() : command.secrets();
        for (SecretField field : SecretField.of(method)) {
            String value = sent.get(field.fieldName());
            if (value != null) {
                secrets.put(field.path(insurerId), value);
                written.add(field);
            }
        }
        Set<SecretField> removed = new LinkedHashSet<>();
        for (Map.Entry<SecretField, String> e : previousPaths.entrySet()) {
            if (!e.getKey().applies(newSettings)) {
                secrets.delete(e.getValue());
                removed.add(e.getKey());
            }
        }

        // 2. linha com as referências
        row.updateConfig(command.environment(), command.displayName(), newSettings, command.minAmount(),
                command.maxAmount(), command.intentValidityMinutes(), command.sortOrder());
        for (SecretField field : written) {
            row.putSecretRef(field, field.path(insurerId), now);
        }
        for (SecretField field : removed) {
            row.removeSecretRef(field);
        }
        InsurerPaymentMethod saved = methods.saveAndFlush(row);

        Map<String, Object> after = auditState(saved);
        @SuppressWarnings("unchecked")
        Map<String, Object> afterSecrets = (Map<String, Object>) after.get("secrets");
        written.forEach(f -> afterSecrets.put(f.fieldName(), "changed"));
        audit("PAYMENT_METHOD.UPDATE", saved, before, after);
        log.info("Payment method {} of insurer {} saved ({} secrets written, {} removed, environment {})",
                method, insurerId, written.size(), removed.size(), saved.getEnvironment());
        return item(saved);
    }

    /**
     * Activa o método: exige os dados completos (schema com os obrigatórios, regras da secção 4 e
     * segredos obrigatórios).
     *
     * @throws DomainException 404 {@code PAYMENT_METHOD_NOT_FOUND}, 409 {@code CONCURRENT_MODIFICATION},
     *                         {@code INVALID_STATE_TRANSITION} ou {@code PAYMENT_METHOD_INCOMPLETE}
     */
    @PreAuthorize("hasAuthority('payment:configure')")
    @Transactional
    public MethodItem enable(String rawMethod, long version) {
        UUID insurerId = requireInsurer();
        InsurerPaymentMethod row = load(insurerId, parse(rawMethod));
        requireVersion(row, version);
        Map<String, Object> before = auditState(row);
        List<FieldError> errors = row.isEnabled() ? List.of() : completeness(row);
        row.enable(errors, clock);
        InsurerPaymentMethod saved = methods.saveAndFlush(row);
        audit("PAYMENT_METHOD.ENABLE", saved, before, auditState(saved));
        return item(saved);
    }

    /** @throws DomainException 404, 409 {@code CONCURRENT_MODIFICATION} ou {@code INVALID_STATE_TRANSITION} */
    @PreAuthorize("hasAuthority('payment:configure')")
    @Transactional
    public MethodItem disable(String rawMethod, long version) {
        UUID insurerId = requireInsurer();
        InsurerPaymentMethod row = load(insurerId, parse(rawMethod));
        requireVersion(row, version);
        Map<String, Object> before = auditState(row);
        row.disable();
        InsurerPaymentMethod saved = methods.saveAndFlush(row);
        audit("PAYMENT_METHOD.DISABLE", saved, before, auditState(saved));
        return item(saved);
    }

    /**
     * Testa a ligação com os dados <b>gravados</b>, no tester do método (sem tester →
     * {@code NOT_SUPPORTED}), até {@link #TEST_TIMEOUT}. Grava o resultado na linha e na auditoria
     * (só o resultado).
     */
    @PreAuthorize("hasAuthority('payment:configure')")
    public TestOutcome test(String rawMethod) {
        UUID insurerId = requireInsurer();
        PaymentMethod method = parse(rawMethod);
        InsurerPaymentMethod snapshot = tx.execute(status -> load(insurerId, method));
        JsonNode settings = snapshot.getSettings();
        Map<SecretField, String> paths = snapshot.secretPaths();

        PaymentMethodTester tester = testers.get(method);
        TestResult result = tester == null ? TestResult.notSupported() : runTester(tester, insurerId, settings,
                field -> SecretField.of(method, field).map(paths::get).flatMap(secrets::get));
        Instant testedAt = clock.instant();

        tx.executeWithoutResult(status -> {
            InsurerPaymentMethod row = load(insurerId, method);
            row.recordTest(result, testedAt);
            methods.saveAndFlush(row);
            Map<String, Object> outcome = new LinkedHashMap<>();
            outcome.put("result", result.status().name());
            outcome.put("message", result.message());
            audit("PAYMENT_METHOD.TEST", row, null, outcome);
        });
        log.info("Payment method {} of insurer {} tested: {} {}", method, insurerId, result.status(), result.message());
        return new TestOutcome(result.status(), result.message(), testedAt);
    }

    private TestResult runTester(PaymentMethodTester tester, UUID insurerId, JsonNode settings,
                                 Function<String, Optional<String>> secret) {
        CompletableFuture<TestResult> future = CompletableFuture.supplyAsync(
                () -> tester.test(insurerId, settings, secret), testExecutor);
        try {
            TestResult result = future.get(TEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            return result == null ? TestResult.failed(TEST_ERROR) : result;
        } catch (TimeoutException e) {
            future.cancel(true);
            return TestResult.failed(TIMEOUT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return TestResult.failed(TEST_ERROR);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DomainException domain && ErrorCodes.SECRET_STORE_UNAVAILABLE.equals(domain.getCode())) {
                return TestResult.failed(ErrorCodes.SECRET_STORE_UNAVAILABLE);
            }
            log.warn("Payment method tester {} failed: {}", tester.method(),
                    cause == null ? "unknown" : cause.getClass().getSimpleName());
            return TestResult.failed(TEST_ERROR);
        }
    }

    // Apoio

    private List<FieldError> completeness(InsurerPaymentMethod row) {
        JsonNode settings = row.getSettings();
        List<FieldError> errors = new ArrayList<>(
                validator.validateSettings(row.getMethod(), settings, row.getInsurerId(), Mode.ENABLE));
        errors.addAll(validator.validateLimits(row.getMinAmount(), row.getMaxAmount()));
        errors.addAll(validator.missingSecrets(row.getMethod(), settings, f -> row.secretPath(f).isPresent()));
        return errors;
    }

    private static List<FieldError> requestErrors(UpdateCommand c) {
        List<FieldError> errors = new ArrayList<>();
        if (c.environment() == null) {
            errors.add(new FieldError("environment", FieldErrorCodes.REQUIRED, "environment is required"));
        }
        if (c.settings() == null || c.settings().isNull()) {
            errors.add(new FieldError("settings", FieldErrorCodes.REQUIRED, "settings is required"));
        }
        if (c.displayName() != null && c.displayName().strip().length() > InsurerPaymentMethod.MAX_DISPLAY_NAME) {
            errors.add(new FieldError("displayName", FieldErrorCodes.OUT_OF_RANGE, "displayName has more than 60 characters"));
        }
        if (c.intentValidityMinutes() == null) {
            errors.add(new FieldError("intentValidityMinutes", FieldErrorCodes.REQUIRED, "intentValidityMinutes is required"));
        } else if (c.intentValidityMinutes() < InsurerPaymentMethod.MIN_INTENT_VALIDITY
                || c.intentValidityMinutes() > InsurerPaymentMethod.MAX_INTENT_VALIDITY) {
            errors.add(new FieldError("intentValidityMinutes", FieldErrorCodes.OUT_OF_RANGE,
                    "intentValidityMinutes must be between 5 and 1440"));
        }
        if (c.sortOrder() == null) {
            errors.add(new FieldError("sortOrder", FieldErrorCodes.REQUIRED, "sortOrder is required"));
        } else if (c.sortOrder() < 0 || c.sortOrder() > MAX_SORT_ORDER) {
            errors.add(new FieldError("sortOrder", FieldErrorCodes.OUT_OF_RANGE, "sortOrder must be between 0 and 99"));
        }
        return errors;
    }

    /** {@code version} igual à gravada; na criação tem de vir ausente. */
    private static void requireVersion(InsurerPaymentMethod row, Long expected) {
        boolean matches = row == null ? expected == null : expected != null && row.getVersion() == expected;
        if (!matches) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The payment method was changed by another request");
        }
    }

    private InsurerPaymentMethod load(UUID insurerId, PaymentMethod method) {
        return methods.findByInsurerIdAndMethod(insurerId, method)
                .orElseThrow(() -> DomainException.of(ErrorCodes.PAYMENT_METHOD_NOT_FOUND,
                        "Payment method not configured"));
    }

    private static PaymentMethod parse(String raw) {
        return PaymentMethod.parse(raw).orElseThrow(() -> DomainException.of(ErrorCodes.PAYMENT_METHOD_NOT_FOUND,
                "Unknown payment method"));
    }

    /** Seguradora do token, em âmbito {@code ORG} (senão 403 {@code FORBIDDEN}). */
    private UUID requireInsurer() {
        TenantSnapshot tenant = TenantContext.current();
        UUID insurerId = tenant.insurerId();
        if (tenant.scope() != Scope.ORG || insurerId == null) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only insurer staff can configure payment methods");
        }
        boolean insurer = organizations.findSummary(insurerId)
                .map(o -> o.type() == OrganizationType.INSURER)
                .orElse(false);
        if (!insurer) {
            throw DomainException.of(ErrorCodes.FORBIDDEN, "Only insurer staff can configure payment methods");
        }
        return insurerId;
    }

    private void audit(String action, InsurerPaymentMethod row, Object before, Object after) {
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), row.getInsurerId(), action,
                "InsurerPaymentMethod", row.getId().toString(), before, after));
    }

    /** Estado para a auditoria: sem segredos, só {@code "configured"} por campo. */
    private static Map<String, Object> auditState(InsurerPaymentMethod m) {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("method", m.getMethod().name());
        state.put("enabled", m.isEnabled());
        state.put("environment", m.getEnvironment().name());
        state.put("displayName", m.getDisplayName());
        state.put("settings", m.getSettings());
        state.put("minAmount", m.getMinAmount());
        state.put("maxAmount", m.getMaxAmount());
        state.put("intentValidityMinutes", m.getIntentValidityMinutes());
        state.put("sortOrder", m.getSortOrder());
        Map<String, Object> configured = new LinkedHashMap<>();
        m.secretPaths().keySet().forEach(f -> configured.put(f.fieldName(), "configured"));
        state.put("secrets", configured);
        return state;
    }

    private static MethodItem item(InsurerPaymentMethod m) {
        JsonNode settings = m.getSettings();
        Map<String, SecretStatus> status = new LinkedHashMap<>();
        for (SecretField f : SecretField.of(m.getMethod())) {
            status.put(f.fieldName(), new SecretStatus(m.secretPath(f).isPresent(), f.required(settings),
                    m.secretUpdatedAt(f).orElse(null)));
        }
        LastTest lastTest = m.getLastTestAt() == null ? null
                : new LastTest(m.getLastTestAt(), m.getLastTestResult(), m.getLastTestMessage());
        LastError lastError = m.getLastErrorCode() == null ? null : new LastError(m.getLastErrorCode(), m.getLastErrorAt());
        return new MethodItem(m.getMethod(), true, m.isEnabled(), m.getEnvironment(), m.getDisplayName(), settings,
                SettingsValidator.schema(m.getMethod()), status, m.getMinAmount(), m.getMaxAmount(),
                m.getIntentValidityMinutes(), m.getSortOrder(), lastTest, lastError, m.getVersion(), m.getUpdatedAt());
    }

    private static MethodItem unconfigured(PaymentMethod method) {
        JsonNode settings = SettingsValidator.defaults(method);
        Map<String, SecretStatus> status = new LinkedHashMap<>();
        for (SecretField f : SecretField.of(method)) {
            status.put(f.fieldName(), new SecretStatus(false, f.required(settings), null));
        }
        return new MethodItem(method, false, false, PaymentEnvironment.SANDBOX, null, settings,
                SettingsValidator.schema(method), status, null, null, InsurerPaymentMethod.DEFAULT_INTENT_VALIDITY, 0,
                null, null, null, null);
    }

    /** Threads dos testers ({@code payment-test-N}), em segundo plano. */
    private static final class TesterThreads implements java.util.concurrent.ThreadFactory {

        private final AtomicInteger count = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "payment-test-" + count.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }
}
