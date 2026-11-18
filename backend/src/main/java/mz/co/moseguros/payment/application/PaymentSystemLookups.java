package mz.co.moseguros.payment.application;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.domain.InsurerPaymentMethod;
import mz.co.moseguros.payment.domain.InsurerPaymentMethodRepository;
import mz.co.moseguros.payment.domain.PaymentEnvironment;
import mz.co.moseguros.payment.domain.SecretField;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.secrets.SecretRef;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Leituras de {@code payment.insurer_payment_method} em {@code SYSTEM} (S5-01 §7), para os serviços
 * do {@code payment} que correm em âmbitos em que o RLS não deixa ver a tabela (cliente, worker,
 * jobs) e para a regra {@code ENTITY_IN_USE}. Filtra <b>sempre</b> por {@code insurerId} no próprio
 * SQL. Nunca devolve valores secretos: quem precisa chama {@code SecretStore.get(ref.path())} no
 * momento. As tarefas seguintes acrescentam métodos (S5-02, S5-07, S5-09, S5-10).
 */
@Component
public class PaymentSystemLookups {

    private final InsurerPaymentMethodRepository methods;
    private final OrganizationApi organizations;

    public PaymentSystemLookups(InsurerPaymentMethodRepository methods, OrganizationApi organizations) {
        this.methods = methods;
        this.organizations = organizations;
    }

    /**
     * Configuração de uma forma de pagamento, sem valores secretos.
     *
     * @param secretRefs campo ({@link SecretField#fieldName()}) → caminho no cofre
     * @param minAmount  {@code null} = sem mínimo
     * @param maxAmount  {@code null} = sem máximo
     */
    public record MethodConfig(UUID id, UUID insurerId, PaymentMethod method, PaymentEnvironment environment,
                               String displayName, JsonNode settings, Map<String, SecretRef> secretRefs,
                               Money minAmount, Money maxAmount, int intentValidityMinutes, int sortOrder,
                               long version) {

        public MethodConfig {
            settings = settings == null ? null : settings.deepCopy();
            secretRefs = Map.copyOf(secretRefs);
        }

        public Optional<SecretRef> secretRef(SecretField field) {
            return Optional.ofNullable(secretRefs.get(field.fieldName()));
        }
    }

    /** Forma de pagamento activa da seguradora, ou vazio. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<MethodConfig> enabledConfig(UUID insurerId, PaymentMethod method) {
        Objects.requireNonNull(insurerId, "insurerId");
        Objects.requireNonNull(method, "method");
        return methods.findByInsurerIdAndMethodAndEnabledTrue(insurerId, method).map(PaymentSystemLookups::config);
    }

    /** Formas de pagamento activas da seguradora, por {@code sortOrder}. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public List<MethodConfig> enabledConfigs(UUID insurerId) {
        Objects.requireNonNull(insurerId, "insurerId");
        return methods.findByInsurerIdAndEnabledTrueOrderBySortOrderAscMethodAsc(insurerId).stream()
                .map(PaymentSystemLookups::config)
                .toList();
    }

    /** Há {@code BANK_REFERENCE} de outra seguradora (activo ou não) com esta entidade. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public boolean entityInUse(String entity, UUID exceptInsurerId) {
        Objects.requireNonNull(exceptInsurerId, "exceptInsurerId");
        if (entity == null || entity.isBlank()) {
            return false;
        }
        return methods.entityUsedByAnotherInsurer(entity, exceptInsurerId);
    }

    /**
     * Todas as formas de pagamento (activas ou não) das seguradoras indicadas: só para o
     * {@code DevPaymentSecretsSeeder} (perfil {@code local}), que corre sem utilizador.
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public List<MethodConfig> configsOf(Collection<UUID> insurerIds) {
        if (insurerIds.isEmpty()) {
            return List.of();
        }
        return methods.findByInsurerIdIn(List.copyOf(insurerIds)).stream()
                .map(PaymentSystemLookups::config)
                .toList();
    }

    /**
     * Configuração (activa ou não) com este id: a de uma intenção ({@code method_config_id}, S5-02),
     * para os prazos e a entidade de uma intenção já criada.
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<MethodConfig> configById(UUID id) {
        Objects.requireNonNull(id, "id");
        return methods.findById(id).map(PaymentSystemLookups::config);
    }

    /**
     * Nomes das seguradoras ({@code displayName}) indicadas que existem (S5-02: {@code IntentView.insurer}),
     * lidos em {@code SYSTEM}: o cliente não vê as organizações pelo RLS.
     */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Map<UUID, String> insurerNames(Set<UUID> insurerIds) {
        Map<UUID, String> names = new LinkedHashMap<>();
        if (insurerIds == null || insurerIds.isEmpty()) {
            return names;
        }
        organizations.findSummaries(insurerIds).forEach((id, summary) -> names.put(id, summary.displayName()));
        return names;
    }

    static MethodConfig config(InsurerPaymentMethod m) {
        Map<String, SecretRef> refs = new LinkedHashMap<>();
        m.secretPaths().forEach((field, path) -> refs.put(field.fieldName(), new SecretRef(path)));
        return new MethodConfig(m.getId(), m.getInsurerId(), m.getMethod(), m.getEnvironment(), m.getDisplayName(),
                m.getSettings(), refs, money(m.getMinAmount()), money(m.getMaxAmount()), m.getIntentValidityMinutes(),
                m.getSortOrder(), m.getVersion());
    }

    private static Money money(Long centavos) {
        return centavos == null ? null : Money.ofCentavos(centavos);
    }
}
