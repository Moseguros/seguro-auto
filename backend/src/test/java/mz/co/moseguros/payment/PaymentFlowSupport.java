package mz.co.moseguros.payment;

import static io.restassured.RestAssured.given;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.organization.domain.Organization;
import mz.co.moseguros.organization.OrganizationType;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Apoio aos testes do núcleo de pagamentos (S5-02): uma proposta "modelo" criada pelo fluxo completo
 * (cotação, proposta) e copiada na BD (como dono) para cada cenário, com o cliente, a seguradora, o
 * estado, o prazo e o montante de cada teste; formas de pagamento activas gravadas directamente (sem
 * segredos: os adaptadores dos testes não os usam); e os pedidos REST.
 */
public final class PaymentFlowSupport {

    public static final String PROPOSALS = "/api/v1/proposals";
    public static final String INTENTS = "/api/v1/payment-intents";
    public static final String MY_PAYMENTS = "/api/v1/me/payments";
    public static final String INSURER_PAYMENTS = "/api/v1/insurer/payments";
    public static final long AMOUNT = 299_900L;

    private final IdentityFixtures fixtures;
    private final IdentityApi identityApi;
    private final MutableClock clock;
    private UUID template;
    private List<String> columns;

    public PaymentFlowSupport(IdentityFixtures fixtures, IdentityApi identityApi, MutableClock clock) {
        this.fixtures = fixtures;
        this.identityApi = identityApi;
        this.clock = clock;
    }

    public JdbcTemplate owner() {
        return fixtures.owner();
    }

    /** Cria a proposta modelo (uma vez por classe de teste); repõe o relógio no fim. */
    public void createTemplate() throws Exception {
        ProposalTestSupport proposals = new ProposalTestSupport(fixtures, identityApi, clock);
        proposals.atNow();
        try {
            Insurer insurer = proposals.insurerWithProducts("Seguradora Modelo Pagamentos");
            Customer c = proposals.customerWithProfile();
            template = ProposalTestSupport.created(c.token(), proposals.mtplOnly(c, insurer).quoteId());
        } finally {
            clock.reset();
        }
        columns = owner().queryForList("""
                select column_name from information_schema.columns
                 where table_schema = 'proposal' and table_name = 'proposal' order by ordinal_position""",
                String.class);
    }

    // --- Pessoas e organizações ---

    /** Seguradora com os tokens dos perfis. */
    public record Seg(UUID id, String name, String admin, String finance, String underwriter) {
    }

    public Seg insurer(String name) {
        Organization org = fixtures.organization(OrganizationType.INSURER, name + " " + digits(4));
        return new Seg(org.getId(), org.displayName(), staffToken(org.getId(), "INSURER_ADMIN"),
                staffToken(org.getId(), "FINANCE"), staffToken(org.getId(), "UNDERWRITER"));
    }

    /** Mediador (corretora) com um utilizador {@code BROKER}. */
    public record Broker(UUID id, String name, UserAccount user, String token) {
    }

    public Broker broker(String name) {
        Organization org = fixtures.organization(OrganizationType.BROKER, name + " " + digits(4));
        UserAccount user = fixtures.staff("Mediador " + digits(3), org.getId(), "BROKER");
        return new Broker(org.getId(), org.displayName(), user, token(user));
    }

    public String staffToken(UUID orgId, String... roles) {
        return token(fixtures.staff("Staff " + digits(4), orgId, roles));
    }

    public Customer customer() {
        UserAccount account = fixtures.customer("Cliente Pagador " + digits(3));
        return new Customer(account, token(account));
    }

    public String token(UserAccount account) {
        return identityApi.issueTokens(account, null).accessToken();
    }

    /** E-mail verificado do cliente (como dono), para receber os e-mails. */
    public String verifiedEmail(UUID userId) {
        String email = "pagador" + digits(8) + "@teste.co.mz";
        owner().update("update identity.user_account set email = ?, email_verified = true where id = ?", email,
                userId);
        return email;
    }

    public void preferredLanguage(UUID userId, String language) {
        owner().update("update identity.user_account set preferred_language = ? where id = ?", language, userId);
    }

    public String emailOf(UUID userId) {
        return owner().queryForObject("select email from identity.user_account where id = ?", String.class, userId);
    }

    // --- Formas de pagamento ---

    /** Forma de pagamento activa (gravada como dono); devolve a entidade (referências) ou {@code null}. */
    public String enable(UUID insurerId, String method, Long min, Long max, int sortOrder, String displayName) {
        String entity = "BANK_REFERENCE".equals(method) ? String.valueOf(ThreadLocalRandom.current()
                .nextInt(10000, 89999)) : null;
        String settings = entity != null
                ? "{\"entity\":\"" + entity + "\",\"fileFormat\":\"MEPS\"}"
                : "{\"serviceProviderCode\":\"171717\",\"host\":\"http://mpesa-mock\",\"c2bTimeoutSeconds\":90}";
        owner().update("""
                insert into payment.insurer_payment_method
                  (id, created_at, created_by, updated_at, updated_by, version, insurer_id, method, enabled, enabled_at,
                   environment, display_name, settings, secret_refs, min_amount, max_amount, intent_validity_minutes,
                   sort_order)
                values (?, now(), 'test', now(), 'test', 0, ?, ?, true, now(), 'SANDBOX', ?, ?::jsonb, '{}'::jsonb,
                        ?, ?, 30, ?)""",
                UUID.randomUUID(), insurerId, method, displayName, settings, min, max, sortOrder);
        return entity;
    }

    public String enableBoth(UUID insurerId) {
        enable(insurerId, "MPESA", null, null, 1, "M-Pesa Teste");
        return enable(insurerId, "BANK_REFERENCE", null, null, 2, "Referência Teste");
    }

    // --- Propostas ---

    /** Proposta {@code ACCEPTED} do cliente, com prazo para pagar de 15 dias e o montante {@link #AMOUNT}. */
    public UUID accepted(UUID customerId, Seg insurer) {
        return proposal(customerId, insurer, "ACCEPTED", clock.instant().plus(Duration.ofDays(15)), AMOUNT, null);
    }

    /** Copia a proposta modelo com os dados indicados (como dono, sem RLS). */
    public UUID proposal(UUID customerId, Seg insurer, String status, Instant expiresAt, long amount, Broker broker) {
        UUID id = UUID.randomUUID();
        Map<String, Object> overrides = new LinkedHashMap<>();
        overrides.put("id", id);
        overrides.put("number", "PR-9999-" + digits(6));
        overrides.put("quote_id", UUID.randomUUID());
        overrides.put("insurer_id", insurer.id());
        overrides.put("insurer_name", insurer.name());
        overrides.put("customer_user_id", customerId);
        overrides.put("created_by_user_id", customerId);
        overrides.put("channel", broker == null ? "APP" : "BROKER");
        overrides.put("broker_org_id", broker == null ? null : broker.id());
        overrides.put("broker_name", broker == null ? null : broker.name());
        overrides.put("status", status);
        overrides.put("expires_at", expiresAt == null ? null : Timestamp.from(expiresAt));
        overrides.put("total_amount", amount);
        overrides.put("version", 0L);
        List<String> select = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        for (String column : columns) {
            if (overrides.containsKey(column)) {
                select.add("?");
                args.add(overrides.get(column));
            } else {
                select.add(column);
            }
        }
        args.add(template);
        String sql = "insert into proposal.proposal (" + String.join(", ", columns) + ") select "
                + String.join(", ", select) + " from proposal.proposal where id = ?";
        owner().update(sql, args.toArray());
        return id;
    }

    public void setProposalStatus(UUID proposalId, String status) {
        owner().update("update proposal.proposal set status = ? where id = ?", status, proposalId);
    }

    public Map<String, Object> proposalRow(UUID proposalId) {
        return owner().queryForMap("select status, paid_at, payment_intent_id, expires_at from proposal.proposal "
                + "where id = ?", proposalId);
    }

    public Map<String, Object> intentRow(UUID intentId) {
        return owner().queryForMap("select * from payment.payment_intent where id = ?", intentId);
    }

    public String referenceStatus(UUID intentId) {
        return owner().queryForObject("select status from payment.reference_payment where intent_id = ?",
                String.class, intentId);
    }

    // --- Pedidos ---

    public static Map<String, Object> body(String method, String msisdn, String channel) {
        Map<String, Object> b = new LinkedHashMap<>();
        b.put("method", method);
        if (msisdn != null) {
            b.put("payerMsisdn", msisdn);
        }
        b.put("channel", channel);
        return b;
    }

    public static Response pay(String token, UUID proposalId, String key, Map<String, Object> body) {
        var request = token == null ? given() : given().auth().oauth2(token);
        if (key != null) {
            request = request.header("Idempotency-Key", key);
        }
        return request.contentType(ContentType.JSON).body(body).post(PROPOSALS + "/{id}/payment-intents", proposalId);
    }

    public static Response payReference(String token, UUID proposalId) {
        return pay(token, proposalId, UUID.randomUUID().toString(), body("BANK_REFERENCE", null, "WEB"));
    }

    public static Response payMpesa(String token, UUID proposalId, String msisdn) {
        return pay(token, proposalId, UUID.randomUUID().toString(), body("MPESA", msisdn, "APP"));
    }

    public static Response methods(String token, UUID proposalId) {
        return (token == null ? given() : given().auth().oauth2(token)).get(PROPOSALS + "/{id}/payment-methods",
                proposalId);
    }

    public static Response intent(String token, UUID intentId) {
        return (token == null ? given() : given().auth().oauth2(token)).get(INTENTS + "/{id}", intentId);
    }

    /** Corre {@code work} numa transacção em {@code SYSTEM} (como os jobs e listeners). */
    public static <T> T inSystem(org.springframework.transaction.support.TransactionTemplate tx,
                                 java.util.function.Supplier<T> work) {
        mz.co.moseguros.shared.tenant.TenantSnapshot previous = mz.co.moseguros.shared.tenant.TenantContext.current();
        mz.co.moseguros.shared.tenant.TenantContext.set(mz.co.moseguros.shared.tenant.TenantSnapshot.system(null));
        try {
            return tx.execute(status -> work.get());
        } finally {
            mz.co.moseguros.shared.tenant.TenantContext.set(previous);
        }
    }

    /** O id da intenção da resposta. */
    public static UUID id(Response r) {
        return UUID.fromString(r.path("id"));
    }

    public static String digits(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(10));
        }
        return sb.toString();
    }
}
