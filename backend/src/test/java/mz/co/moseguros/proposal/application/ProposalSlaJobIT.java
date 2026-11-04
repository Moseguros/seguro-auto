package mz.co.moseguros.proposal.application;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import mz.co.moseguros.SecurityTestConfiguration;
import mz.co.moseguros.SecurityTestConfiguration.IdentityFixtures;
import mz.co.moseguros.SecurityTestConfiguration.MutableClock;
import mz.co.moseguros.TestcontainersConfiguration;
import mz.co.moseguros.catalog.CatalogTestSupport.Insurer;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.StaffContact;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.notification.FakeOtpDeliveryChannels;
import mz.co.moseguros.proposal.ProposalTestSupport;
import mz.co.moseguros.proposal.SubmissionTestSupport;
import mz.co.moseguros.rating.QuoteTestSupport.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Alerta de SLA (S4-05 §10) com o relógio controlado: aos 10 dias o job envia um e-mail a cada
 * utilizador com {@code proposal:decide} da seguradora (e a nenhum de outra), grava
 * {@code sla_alerted_at} e não repete; aos 15 dias a proposta continua em análise e a fila mostra
 * {@code OVERDUE}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, SecurityTestConfiguration.class, SubmissionTestSupport.EventsConfig.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProposalSlaJobIT {

    @LocalServerPort
    int port;

    @Autowired
    IdentityFixtures fixtures;

    @Autowired
    IdentityApi identityApi;

    @Autowired
    MutableClock clock;

    @Autowired
    FakeOtpDeliveryChannels channels;

    @Autowired
    ProposalSlaJob job;

    ProposalTestSupport support;
    Insurer insurer;
    Insurer other;
    UserAccount underwriter;
    UserAccount admin;
    UserAccount finance;
    UserAccount otherUnderwriter;

    @BeforeAll
    void createInsurers() {
        RestAssured.port = port;
        support = new ProposalTestSupport(fixtures, identityApi, clock);
        support.atNow();
        insurer = support.insurerWithProducts("Seguradora SLA");
        other = support.insurerWithProducts("Seguradora SLA Outra");
        underwriter = fixtures.staff("Beatriz Subscritora", insurer.id(), "UNDERWRITER");
        admin = fixtures.staff("Carlos Administrador", insurer.id(), "INSURER_ADMIN");
        finance = fixtures.staff("Dora Financeira", insurer.id(), "FINANCE");
        otherUnderwriter = fixtures.staff("Eva Subscritora", other.id(), "UNDERWRITER");
        clock.reset();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        support.atNow();
        channels.reset();
    }

    @AfterEach
    void tearDown() {
        clock.reset();
        channels.reset();
    }

    /** Proposta em análise desde agora, com o SLA de 10/15 dias (como a submissão a deixa). */
    UUID underReview(Insurer of) throws Exception {
        Customer c = support.customerWithProfile();
        UUID id = ProposalTestSupport.created(c.token(), support.mtplOnly(c, of).quoteId());
        Instant now = clock.instant();
        support.owner().update("""
                update proposal.proposal
                   set status = 'UNDER_REVIEW', submitted_at = ?, expires_at = null, review_started_at = ?,
                       sla_alert_at = ?, sla_due_at = ?, sla_alerted_at = null
                 where id = ?""", Timestamp.from(now), Timestamp.from(now),
                Timestamp.from(now.plus(Duration.ofDays(10))), Timestamp.from(now.plus(Duration.ofDays(15))), id);
        return id;
    }

    List<FakeOtpDeliveryChannels.Sent> alertsTo(UserAccount staff) {
        return channels.sentTo(staff.getEmail()).stream()
                .filter(s -> s.params().getOrDefault("subject", "").contains("aguarda decisão")).toList();
    }

    @Test
    void staffWithPermissionAreTheActiveDecidersOfTheOrganization() {
        List<StaffContact> contacts = identityApi.staffWithPermission(insurer.id(), "proposal:decide");
        assertThat(contacts).extracting(StaffContact::userId).contains(underwriter.getId(), admin.getId())
                .doesNotContain(finance.getId(), otherUnderwriter.getId());
        assertThat(contacts).extracting(StaffContact::email).doesNotContainNull();
        UserAccount disabled = fixtures.staff("Zé Desactivado", insurer.id(), "UNDERWRITER");
        fixtures.setAccountStatus(disabled.getId(), "DISABLED");
        assertThat(identityApi.staffWithPermission(insurer.id(), "proposal:decide")).extracting(StaffContact::userId)
                .doesNotContain(disabled.getId());
        assertThat(identityApi.staffWithPermission(insurer.id(), "no:such-permission")).isEmpty();
    }

    @Test
    void alertsTheDecidersAtTenDaysOnceAndNeverChangesTheStatus() throws Exception {
        UUID id = underReview(insurer);
        UUID notDue = underReview(other);
        support.owner().update("update proposal.proposal set sla_alert_at = sla_alert_at + interval '1 day' "
                + "where id = ?", notDue);
        String number = support.owner().queryForObject("select number from proposal.proposal where id = ?",
                String.class, id);

        // Antes dos 10 dias nada é enviado.
        support.advance(Duration.ofDays(10).minusMinutes(1));
        job.alert();
        assertThat(alertsTo(underwriter)).isEmpty();
        assertThat(support.owner().queryForObject("select sla_alerted_at from proposal.proposal where id = ?",
                Timestamp.class, id)).isNull();

        // Aos 10 dias: um e-mail a cada subscritor da seguradora.
        support.advance(Duration.ofMinutes(1));
        Instant now = clock.instant();
        assertThat(job.alert()).isGreaterThanOrEqualTo(1);
        for (UserAccount staff : List.of(underwriter, admin)) {
            List<FakeOtpDeliveryChannels.Sent> sent = alertsTo(staff);
            assertThat(sent).hasSize(1);
            assertThat(sent.get(0).params().get("subject")).contains(number).contains("10 dias");
            assertThat(sent.get(0).text()).contains(staff.getDisplayName())
                    .contains("/seguradora/propostas/" + id);
        }
        assertThat(alertsTo(finance)).isEmpty();
        assertThat(alertsTo(otherUnderwriter)).isEmpty();
        assertThat(support.owner().queryForObject("select sla_alerted_at from proposal.proposal where id = ?",
                Timestamp.class, id).toInstant()).isCloseTo(now, org.assertj.core.api.Assertions.within(5, java.time.temporal.ChronoUnit.SECONDS));
        assertThat(support.owner().queryForObject("select sla_alerted_at from proposal.proposal where id = ?",
                Timestamp.class, notDue)).isNull();
        assertThat(support.owner().queryForObject("""
                select count(*) from notification.outbound_message where correlation_id = ? and purpose = 'PROPOSAL'""",
                Integer.class, id)).isGreaterThanOrEqualTo(2);

        // Segunda execução: não envia de novo.
        job.alert();
        assertThat(alertsTo(underwriter)).hasSize(1);
        assertThat(alertsTo(admin)).hasSize(1);

        // Aos 15 dias continua em análise e a fila mostra OVERDUE.
        support.advance(Duration.ofDays(5));
        job.alert();
        assertThat(support.owner().queryForObject("select status from proposal.proposal where id = ?", String.class,
                id)).isEqualTo("UNDER_REVIEW");
        String token = support.token(underwriter);
        JsonPath overdue = given().auth().oauth2(token).queryParam("slaStatus", "OVERDUE").queryParam("size", 100)
                .get(SubmissionTestSupport.INSURER_PROPOSALS).then().statusCode(200).extract().jsonPath();
        assertThat(overdue.getList("content.id", String.class)).contains(id.toString());
        assertThat(overdue.getList("content.slaStatus", String.class)).containsOnly("OVERDUE");
        SubmissionTestSupport.insurerGet(token, id).then().statusCode(200)
                .body("sla.status", org.hamcrest.Matchers.equalTo("OVERDUE"))
                .body("sla.alertedAt", SubmissionTestSupport.instantNear(now));
        JsonPath onTime = given().auth().oauth2(token).queryParam("slaStatus", "ON_TIME").queryParam("size", 100)
                .get(SubmissionTestSupport.INSURER_PROPOSALS).then().statusCode(200).extract().jsonPath();
        assertThat(onTime.getList("content.id", String.class)).doesNotContain(id.toString());
    }

    @Test
    void withoutRecipientsTheAlertIsMarkedAnyway() throws Exception {
        Insurer empty = support.quotes().insurerWithCharges("Seguradora Sem Subscritores");
        support.quotes().published(empty.manager(), "MTPL_ONLY", List.of("LIGHT_PASSENGER"), List.of("PRIVATE"),
                mz.co.moseguros.rating.QuoteTestSupport.demoRcCoverages(),
                mz.co.moseguros.rating.domain.tariff.ReferenceData.tariff("DEMO-RC"));
        // O subscritor criado com a seguradora fica desactivado: não há ninguém com proposal:decide.
        support.owner().update("""
                update identity.user_account set status = 'DISABLED'
                 where organization_id = ? and id in (select user_id from identity.user_role
                                                       where role_code in ('UNDERWRITER','INSURER_ADMIN'))""",
                empty.id());
        UUID id = underReview(empty);
        support.advance(Duration.ofDays(10));
        job.alert();
        assertThat(support.owner().queryForObject("select sla_alerted_at from proposal.proposal where id = ?",
                Timestamp.class, id)).isNotNull();
        assertThat(support.owner().queryForObject("""
                select count(*) from notification.outbound_message where correlation_id = ?""", Integer.class, id))
                .isZero();
    }
}
