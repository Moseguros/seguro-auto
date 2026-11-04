package mz.co.moseguros.proposal.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.restassured.RestAssured;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.proposal.ProposalExpired;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.PublishedEvents;
import org.springframework.modulith.test.PublishedEventsExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Expiração das propostas (S4-02 §13) com o relógio controlado: {@code DRAFT} vencida passa a
 * {@code EXPIRED} com o evento e a auditoria; {@code UNDER_REVIEW} antiga não muda; uma segunda
 * execução não muda nada.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(PublishedEventsExtension.class)
class ProposalExpiryJobIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    ProposalExpiryJob job;

    ProposalTestSupport support;
    Insurer insurer;

    @BeforeAll
    void createInsurer() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora Expiração");
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
    }

    UUID proposal() throws Exception {
        Customer c = support.customerWithProfile();
        return ProposalTestSupport.created(c.token(), support.mtplOnly(c, insurer).quoteId());
    }

    Map<String, Object> row(UUID id) {
        return support.owner().queryForMap("select status, expires_at from proposal.proposal where id = ?", id);
    }

    int audits(UUID id) {
        return support.owner().queryForObject("select count(*) from audit.audit_event where action = 'PROPOSAL.EXPIRE' "
                + "and entity_id = ?", Integer.class, id.toString());
    }

    @Test
    void expiredDraftBecomesExpiredWithTheEventAndTheAuditAndARerunChangesNothing(PublishedEvents events)
            throws Exception {
        UUID draft = proposal();
        UUID fresh = proposal();
        UUID underReview = proposal();
        support.setStatus(underReview, "UNDER_REVIEW");
        support.owner().update("update proposal.proposal set expires_at = null where id = ?", underReview);
        // a UNDER_REVIEW "antiga": criada há muito tempo e sem prazo
        support.owner().update("update proposal.proposal set created_at = created_at - interval '90 days' "
                + "where id = ?", underReview);

        // 15 dias e um minuto depois: o rascunho expira; o criado agora (com o mesmo relógio) também,
        // por isso o "fresco" é renovado antes
        support.advance(Duration.ofDays(15).plusMinutes(1));
        Instant now = clock.instant();
        support.owner().update("update proposal.proposal set expires_at = ? where id = ?",
                Timestamp.from(now.plus(Duration.ofDays(3))), fresh);

        int expired = job.expire();
        assertThat(expired).isGreaterThanOrEqualTo(1);
        assertThat(row(draft)).containsEntry("status", "EXPIRED").containsEntry("expires_at", null);
        assertThat(row(fresh).get("status")).isEqualTo("DRAFT");
        assertThat(row(underReview).get("status")).isEqualTo("UNDER_REVIEW");
        assertThat(audits(draft)).isEqualTo(1);
        assertThat(audits(fresh)).isZero();
        assertThat(audits(underReview)).isZero();
        Map<String, Object> audit = support.owner().queryForMap("""
                select actor_type, actor_id, insurer_id, before_state::text as before, after_state::text as after
                  from audit.audit_event where action = 'PROPOSAL.EXPIRE' and entity_id = ?""", draft.toString());
        assertThat(audit.get("actor_type")).isEqualTo("SYSTEM");
        assertThat(audit.get("actor_id")).isEqualTo("ProposalExpiryJob");
        assertThat(audit.get("insurer_id")).isEqualTo(insurer.id());
        assertThat((String) audit.get("before")).contains("DRAFT");
        assertThat((String) audit.get("after")).contains("EXPIRED");

        List<ProposalExpired> published = new ArrayList<>();
        events.ofType(ProposalExpired.class).matching(e -> e.proposalId().equals(draft)).forEach(published::add);
        assertThat(published).hasSize(1);
        ProposalExpired event = published.get(0);
        assertThat(event.fromStatus()).isEqualTo(ProposalStatus.DRAFT);
        assertThat(event.insurerId()).isEqualTo(insurer.id());
        assertThat(event.brokerOrgId()).isNull();
        assertThat(event.eventId()).isNotNull();
        assertThat(event.occurredAt()).isNotNull();

        // segunda execução: nada muda
        int again = job.expire();
        assertThat(again).isZero();
        assertThat(audits(draft)).isEqualTo(1);
        assertThat(row(draft).get("status")).isEqualTo("EXPIRED");
        assertThat(row(underReview).get("status")).isEqualTo("UNDER_REVIEW");
    }

    @Test
    void acceptedAndInfoRequestedAlsoExpireButFinalStatesDoNot() throws Exception {
        UUID accepted = proposal();
        UUID infoRequested = proposal();
        UUID rejected = proposal();
        support.setStatus(accepted, "ACCEPTED");
        support.setStatus(infoRequested, "INFO_REQUESTED");
        support.setStatus(rejected, "REJECTED");
        support.advance(Duration.ofDays(16));

        job.expire();
        assertThat(row(accepted).get("status")).isEqualTo("EXPIRED");
        assertThat(row(infoRequested).get("status")).isEqualTo("EXPIRED");
        assertThat(row(rejected).get("status")).isEqualTo("REJECTED");
        assertThat(audits(rejected)).isZero();
    }
}
