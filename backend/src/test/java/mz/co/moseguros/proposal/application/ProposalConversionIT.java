package mz.co.moseguros.proposal.application;

import static mz.co.moseguros.payment.PaymentFlowSupport.inSystem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.payment.PaymentFlowSupport;
import mz.co.moseguros.payment.PaymentFlowSupport.Seg;
import mz.co.moseguros.proposal.ProposalApi;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * {@code ProposalApi.markConverted} (S6-01): {@code PAID → CONVERTED} com {@code policy_id} e
 * {@code PROPOSAL.CONVERT}; idempotente com a mesma apólice; {@code INVALID_STATE_TRANSITION} com outra
 * apólice ou fora de {@code PAID}; a migração ({@code ck_proposal_converted}).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalConversionIT {

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    TransactionTemplate tx;

    @Autowired
    ProposalApi proposals;

    PaymentFlowSupport support;
    Seg insurer;
    Customer customer;

    @BeforeAll
    void setUpAll() throws Exception {
        support = new PaymentFlowSupport(fixtures, identityApi, clock);
        support.createTemplate();
        insurer = support.insurer("Seguradora Conversão");
        customer = support.customer();
    }

    /** Proposta {@code PAID} gravada como dono (sem publicar o pagamento: nenhuma apólice é emitida). */
    UUID paid() {
        UUID proposal = support.accepted(customer.id(), insurer);
        support.owner().update("""
                update proposal.proposal set status = 'PAID', paid_at = now(), payment_intent_id = ?,
                       expires_at = null where id = ?""", UUID.randomUUID(), proposal);
        return proposal;
    }

    Map<String, Object> row(UUID proposal) {
        return support.owner().queryForMap(
                "select status, policy_id, paid_at, payment_intent_id from proposal.proposal where id = ?", proposal);
    }

    List<Map<String, Object>> audits(UUID proposal) {
        return support.owner().queryForList("""
                select actor_type, actor_id, insurer_id, before_state::text as b, after_state::text as a
                  from audit.audit_event where action = 'PROPOSAL.CONVERT' and entity_id = ?""", proposal.toString());
    }

    void convert(UUID proposal, UUID policy) {
        inSystem(tx, () -> {
            proposals.markConverted(proposal, policy);
            return null;
        });
    }

    @Test
    void paidBecomesConvertedOnce() {
        UUID proposal = paid();
        Map<String, Object> before = row(proposal);
        UUID policy = UUID.randomUUID();

        convert(proposal, policy);

        Map<String, Object> after = row(proposal);
        assertThat(after).containsEntry("status", "CONVERTED").containsEntry("policy_id", policy)
                .containsEntry("payment_intent_id", before.get("payment_intent_id"))
                .containsEntry("paid_at", before.get("paid_at"));
        assertThat(audits(proposal)).singleElement().satisfies(a -> {
            assertThat(a).containsEntry("actor_type", "SYSTEM").containsEntry("insurer_id", insurer.id());
            assertThat((String) a.get("b")).contains("PAID");
            assertThat((String) a.get("a")).contains("CONVERTED").contains(policy.toString());
        });

        // segunda chamada com a mesma apólice: nada
        convert(proposal, policy);
        assertThat(audits(proposal)).hasSize(1);
        assertThat(row(proposal)).containsEntry("policy_id", policy);
    }

    @Test
    void anotherPolicyOrAProposalThatIsNotPaidIsAnInvalidTransition() {
        UUID proposal = paid();
        UUID policy = UUID.randomUUID();
        convert(proposal, policy);
        assertThatThrownBy(() -> convert(proposal, UUID.randomUUID()))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("INVALID_STATE_TRANSITION");
                    assertThat(e.getHttpStatus()).isEqualTo(409);
                });
        assertThat(row(proposal)).containsEntry("policy_id", policy);

        UUID accepted = support.accepted(customer.id(), insurer);
        assertThatThrownBy(() -> convert(accepted, UUID.randomUUID()))
                .isInstanceOfSatisfying(DomainException.class, e ->
                        assertThat(e.getCode()).isEqualTo("INVALID_STATE_TRANSITION"));
        assertThat(row(accepted)).containsEntry("status", "ACCEPTED").containsEntry("policy_id", null);
        assertThat(audits(accepted)).isEmpty();

        assertThatThrownBy(() -> convert(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOfSatisfying(DomainException.class, e ->
                        assertThat(e.getCode()).isEqualTo("PROPOSAL_NOT_FOUND"));
    }

    @Test
    void theMigrationRequiresThePolicyOnlyWhenConverted() {
        UUID proposal = paid();
        assertThatThrownBy(() -> support.setProposalStatus(proposal, "CONVERTED"))
                .as("CONVERTED without policy_id").isInstanceOf(DataIntegrityViolationException.class);
        UUID accepted = support.accepted(customer.id(), insurer);
        assertThatThrownBy(() -> support.owner().update("update proposal.proposal set policy_id = ? where id = ?",
                UUID.randomUUID(), accepted)).isInstanceOf(DataIntegrityViolationException.class);
    }
}
