package mz.co.moseguros.policy;

import static mz.co.moseguros.payment.PaymentFlowSupport.digits;
import static mz.co.moseguros.payment.PaymentFlowSupport.id;
import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static mz.co.moseguros.payment.PaymentFlowSupport.payReference;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.payment.application.IntentStateService;
import mz.co.moseguros.payment.domain.ConfirmedBy;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.domain.Ids;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Apoio aos testes das apólices (S6-01): propostas pagas pelo fluxo real do S5 (proposta modelo do
 * {@link PaymentFlowSupport}, intenção por referência, confirmação), e apólices e certificados gravados
 * directamente na BD (como dono) para os testes de leitura, de RLS e do guard.
 */
public final class PolicyTestSupport {

    public static final String MY_POLICIES = "/api/v1/me/policies";
    public static final String INSURER_POLICIES = "/api/v1/insurer/policies";

    private final PaymentFlowSupport payments;
    private final TransactionTemplate tx;
    private final IntentStateService states;

    public PolicyTestSupport(IdentityFixtures fixtures, IdentityApi identityApi, MutableClock clock,
                             TransactionTemplate tx, IntentStateService states) {
        this.payments = new PaymentFlowSupport(fixtures, identityApi, clock);
        this.tx = tx;
        this.states = states;
    }

    public PaymentFlowSupport payments() {
        return payments;
    }

    public JdbcTemplate owner() {
        return payments.owner();
    }

    /** Cria a proposta modelo e a seguradora com as duas formas de pagamento. */
    public Seg setUp(String insurerName) throws Exception {
        payments.createTemplate();
        return insurer(insurerName);
    }

    /** Uma nova seguradora (com {@code insurer_profile}, para o nome) com as duas formas de pagamento. */
    public Seg insurer(String name) {
        Seg insurer = payments.insurer(name);
        owner().update("""
                insert into organization.insurer_profile
                  (id, created_at, created_by, updated_at, updated_by, organization_id, short_code, complaints_email)
                values (?, now(), 'test', now(), 'test', ?, ?, ?)""",
                UUID.randomUUID(), insurer.id(), "P" + digits(8), "reclamacoes" + digits(6) + "@teste.co.mz");
        payments.enableBoth(insurer.id());
        return insurer;
    }

    /** Proposta paga (fluxo real) e o id da intenção; a apólice é emitida pelo listener. */
    public record Paid(UUID proposalId, UUID intentId) {
    }

    /** Proposta {@code ACCEPTED} paga por referência ({@code REFERENCE_FILE}, {@code paidAt} = agora). */
    public Paid paid(Customer c, Seg insurer) {
        UUID proposal = payments.accepted(c.id(), insurer);
        UUID intentId = id(payReference(c.token(), proposal));
        inSystem(tx, () -> states.markSucceeded(intentId, null, ConfirmedBy.REFERENCE_FILE, null, "test"));
        return new Paid(proposal, intentId);
    }

    /** Proposta paga e a apólice já emitida; devolve o id da apólice. */
    public UUID issued(Customer c, Seg insurer) {
        return awaitPolicy(paid(c, insurer).proposalId());
    }

    /** Espera pela apólice da proposta (até 10 s). */
    public UUID awaitPolicy(UUID proposalId) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(policyCount(proposalId)).isOne());
        return owner().queryForObject("select id from policy.policy where proposal_id = ?", UUID.class, proposalId);
    }

    /**
     * Espera pelo certificado e pelas condições particulares da apólice (S6-02, até 10 s); devolve os ids
     * {@code [certificado, condições particulares]}.
     */
    public UUID[] awaitDocuments(UUID policyId) {
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> assertThat(documentsReady(policyId)).isTrue());
        return new UUID[] {
            owner().queryForObject("select pdf_file_id from policy.certificate where policy_id = ? and status = 'VALID'",
                    UUID.class, policyId),
            owner().queryForObject("select schedule_file_id from policy.policy where id = ?", UUID.class, policyId)};
    }

    public boolean documentsReady(UUID policyId) {
        Integer n = owner().queryForObject("""
                select count(*) from policy.policy p join policy.certificate c on c.policy_id = p.id
                 where p.id = ? and c.status = 'VALID' and c.pdf_file_id is not null
                   and p.schedule_file_id is not null""", Integer.class, policyId);
        return n != null && n > 0;
    }

    public int policyCount(UUID proposalId) {
        Integer n = owner().queryForObject("select count(*) from policy.policy where proposal_id = ?", Integer.class,
                proposalId);
        return n == null ? 0 : n;
    }

    public Map<String, Object> policyRow(UUID policyId) {
        return owner().queryForMap("select * from policy.policy where id = ?", policyId);
    }

    public Map<String, Object> proposalRow(UUID proposalId) {
        return owner().queryForMap("select status, policy_id, paid_at, payment_intent_id from proposal.proposal "
                + "where id = ?", proposalId);
    }

    // --- Apólices gravadas directamente ---

    /** Dados de uma apólice gravada como dono. */
    public record Row(UUID insurerId, UUID holderId, UUID vehicleId, String plate, String status, Instant startAt,
                      Instant endAt, String holderName) {

        public static Row of(UUID insurerId, UUID holderId, String plate, String status, Instant startAt,
                             Instant endAt) {
            return new Row(insurerId, holderId, UUID.randomUUID(), plate, status, startAt, endAt,
                    "Tomador " + digits(4));
        }

        public Row vehicle(UUID vehicleId) {
            return new Row(insurerId, holderId, vehicleId, plate, status, startAt, endAt, holderName);
        }

        public Row named(String name) {
            return new Row(insurerId, holderId, vehicleId, plate, status, startAt, endAt, name);
        }
    }

    /** Apólice gravada como dono (sem RLS), com uma prestação e uma entrada da linha temporal. */
    public UUID insert(Row r) {
        UUID id = Ids.newId();
        String number = "AP-9999-" + digits(6);
        owner().update("""
                insert into policy.policy
                  (id, created_at, created_by, updated_at, updated_by, number, insurer_id, proposal_id,
                   payment_intent_id, product_version_id, product_name, product_type, coverage_type,
                   policyholder_user_id, policyholder_name, vehicle_id, plate, vehicle_snapshot, coverages, start_at,
                   end_at, renewal_type, payment_plan, total_amount, status, status_changed_at, channel, issued_at)
                values (?, now(), 'test', now(), 'test', ?, ?, ?, ?, ?, 'RC Teste', 'MTPL_ONLY', 'MTPL_ONLY', ?, ?,
                        ?, ?, ?::jsonb, '[]'::jsonb, ?, ?, 'ANNUAL_RENEWABLE', 'FULL', 299900, ?, now(), 'APP',
                        now())""",
                id, number, r.insurerId(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), r.holderId(),
                r.holderName(), r.vehicleId(), r.plate(),
                "{\"plate\":\"" + r.plate() + "\",\"make\":\"Toyota\",\"model\":\"Corolla\",\"year\":2018}",
                Timestamp.from(r.startAt()), Timestamp.from(r.endAt()), r.status());
        owner().update("""
                insert into policy.instalment
                  (id, created_at, created_by, updated_at, updated_by, policy_id, insurer_id, policyholder_user_id,
                   seq, due_date, amount, status)
                values (?, now(), 'test', now(), 'test', ?, ?, ?, 1, current_date, 299900, 'DUE')""",
                Ids.newId(), id, r.insurerId(), r.holderId());
        owner().update("""
                insert into policy.policy_event
                  (id, created_at, created_by, updated_at, updated_by, policy_id, insurer_id, policyholder_user_id,
                   type, occurred_at, actor_type, actor_id)
                values (?, now(), 'test', now(), 'test', ?, ?, ?, 'ISSUED', now(), 'SYSTEM', 'test')""",
                Ids.newId(), id, r.insurerId(), r.holderId());
        return id;
    }

    /** Certificado da apólice gravado como dono; devolve o id. */
    public UUID certificate(UUID policyId, String status, String token) {
        UUID id = Ids.newId();
        Map<String, Object> p = owner().queryForMap(
                "select insurer_id, policyholder_user_id, start_at, end_at from policy.policy where id = ?", policyId);
        owner().update("""
                insert into policy.certificate
                  (id, created_at, created_by, updated_at, updated_by, policy_id, insurer_id, policyholder_user_id,
                   number, valid_from, valid_to, verification_token, status, revoked_at, revoked_reason)
                values (?, now(), 'test', now(), 'test', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                id, policyId, p.get("insurer_id"), p.get("policyholder_user_id"), "CT-9999-" + digits(7),
                p.get("start_at"), p.get("end_at"), token, status,
                "REVOKED".equals(status) ? Timestamp.from(Instant.now()) : null,
                "REVOKED".equals(status) ? "TEST" : null);
        return id;
    }

    /** Matrícula aleatória válida ({@code AAA-123-MC}). */
    public static String plate() {
        String letters = "ABCDEFGHJKLMNPRSTUVWXZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(letters.charAt(java.util.concurrent.ThreadLocalRandom.current().nextInt(letters.length())));
        }
        return sb + "-" + digits(3) + "-MC";
    }

    /** Código de verificação aleatório de teste (22 caracteres). */
    public static String token() {
        return ("T" + UUID.randomUUID().toString().replace("-", "")).substring(0, 22);
    }

    public static List<String> statuses(JdbcTemplate jdbc, String table) {
        return jdbc.queryForList("select status from " + table, String.class);
    }
}
