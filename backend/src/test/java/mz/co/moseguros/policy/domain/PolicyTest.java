package mz.co.moseguros.policy.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import mz.co.moseguros.policy.CoverStatus;
import mz.co.moseguros.policy.CoverageType;
import mz.co.moseguros.policy.PaymentPlan;
import mz.co.moseguros.policy.PolicyStatus;
import mz.co.moseguros.policy.RenewalType;
import mz.co.moseguros.shared.audit.ActorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** {@code Policy.coverAt} (S6-01 §4) e as regras 3, 4 e 5 da emissão. */
class PolicyTest {

    static final Instant START = Instant.parse("2027-02-10T08:31:00Z");
    static final Instant END = Instant.parse("2028-02-10T08:31:00Z");

    // --- coverAt: as sete linhas da tabela ---

    @ParameterizedTest
    @CsvSource({
            "CANCELLED, 2027-06-01T00:00:00Z, CANCELLED",
            "SUSPENDED, 2027-06-01T00:00:00Z, SUSPENDED",
            "LAPSED,    2027-06-01T00:00:00Z, LAPSED",
            "EXPIRED,   2027-06-01T00:00:00Z, ENDED",
            "ACTIVE,    2027-02-10T08:30:59Z, NOT_STARTED",
            "ACTIVE,    2028-02-10T08:31:00Z, ENDED",
            "ACTIVE,    2027-02-10T08:31:00Z, COVERED"
    })
    void coverAtFollowsTheTable(PolicyStatus status, Instant at, CoverStatus expected) {
        assertThat(Policy.coverAt(status, START, END, at)).isEqualTo(expected);
    }

    @Test
    void coverAtIsTheCurrentStatusForAnyInstant() {
        // nesta tarefa uma apólice anulada não cobre nem datas passadas (o S7-01 refina)
        assertThat(Policy.coverAt(PolicyStatus.CANCELLED, START, END, START.plusSeconds(60)))
                .isEqualTo(CoverStatus.CANCELLED);
        assertThat(Policy.coverAt(PolicyStatus.ACTIVE, START, END, END.minusSeconds(1))).isEqualTo(CoverStatus.COVERED);
        assertThat(issued("MTPL_ONLY", List.of("MTPL")).coverAt(START.plusSeconds(1))).isEqualTo(CoverStatus.COVERED);
    }

    // --- Regra 3: datas ---

    @Test
    void paymentAfterTheRequestedStartMovesThePeriodKeepingTheDuration() {
        // proposta de 10/02/2027 00:00 a 10/02/2028 00:00 (Maputo), paga em 2027-02-10T08:31Z
        Policy.CoverPeriod period = Policy.coverPeriod(Instant.parse("2027-02-09T22:00:00Z"),
                Instant.parse("2028-02-09T22:00:00Z"), Instant.parse("2027-02-10T08:31:00Z"));
        assertThat(period.startAt()).isEqualTo(Instant.parse("2027-02-10T08:31:00Z"));
        assertThat(period.endAt()).isEqualTo(Instant.parse("2028-02-10T08:31:00Z"));
        assertThat(period.startDate()).isEqualTo(LocalDate.of(2027, 2, 10));
    }

    @Test
    void paymentBeforeTheRequestedStartKeepsTheProposalDates() {
        Instant start = Instant.parse("2027-02-14T22:00:00Z");
        Instant end = Instant.parse("2028-02-14T22:00:00Z");
        Policy.CoverPeriod period = Policy.coverPeriod(start, end, Instant.parse("2027-02-10T08:31:00Z"));
        assertThat(period.startAt()).isEqualTo(start);
        assertThat(period.endAt()).isEqualTo(end);
        assertThat(period.startDate()).isEqualTo(LocalDate.of(2027, 2, 15));
        assertThat(Policy.coverPeriod(start, end, start)).isEqualTo(new Policy.CoverPeriod(start, end));
    }

    // --- Regra 4: renovação ---

    @ParameterizedTest
    @CsvSource({
            "TEMPORARY, FIXED_TERM",
            "MTPL_ONLY, ANNUAL_RENEWABLE",
            "COMPREHENSIVE, ANNUAL_RENEWABLE",
            "BORDER, ANNUAL_RENEWABLE",
            "FLEET, ANNUAL_RENEWABLE"
    })
    void renewalType(String productType, RenewalType expected) {
        assertThat(Policy.renewalTypeFor(productType)).isEqualTo(expected);
    }

    // --- Regra 5: tipo de cobertura ---

    @Test
    void coverageType() {
        assertThat(Policy.coverageTypeFor("TEMPORARY", List.of("MTPL", "OD_COLLISION")))
                .isEqualTo(CoverageType.TEMPORARY);
        assertThat(Policy.coverageTypeFor("BORDER", List.of("MTPL"))).isEqualTo(CoverageType.BORDER);
        assertThat(Policy.coverageTypeFor("COMPREHENSIVE", List.of("MTPL", "OD_FIRE")))
                .isEqualTo(CoverageType.COMPREHENSIVE);
        assertThat(Policy.coverageTypeFor("MTPL_ONLY", List.of("MTPL", "OD_COLLISION")))
                .isEqualTo(CoverageType.COMPREHENSIVE);
        assertThat(Policy.coverageTypeFor("COMPREHENSIVE", List.of("MTPL", "VTPL")))
                .isEqualTo(CoverageType.MTPL_ONLY);
        assertThat(Policy.coverageTypeFor("MTPL_ONLY", List.of())).isEqualTo(CoverageType.MTPL_ONLY);
        assertThat(Policy.coverageTypeFor("MTPL_ONLY", null)).isEqualTo(CoverageType.MTPL_ONLY);
    }

    // --- Fábrica ---

    @Test
    void issueCreatesAnActivePolicyWithTheRules() {
        Policy p = issued("TEMPORARY", List.of("MTPL"));
        assertThat(p.getStatus()).isEqualTo(PolicyStatus.ACTIVE);
        assertThat(p.getStatusChangedAt()).isEqualTo(p.getIssuedAt());
        assertThat(p.getRenewalType()).isEqualTo(RenewalType.FIXED_TERM);
        assertThat(p.getCoverageType()).isEqualTo(CoverageType.TEMPORARY);
        assertThat(p.getPaymentPlan()).as("TEMPORARY is always FULL").isEqualTo(PaymentPlan.FULL);
        assertThat(p.getBrokerOrgId()).as("only for the BROKER channel").isNull();
        assertThat(p.period()).isEqualTo(new Policy.CoverPeriod(START, END));
        assertThat(Policy.paymentPlanFor("MTPL_ONLY", PaymentPlan.QUARTERLY)).isEqualTo(PaymentPlan.QUARTERLY);
        assertThat(Policy.paymentPlanFor("MTPL_ONLY", null)).isEqualTo(PaymentPlan.FULL);
    }

    @Test
    void issueRejectsInvalidData() {
        assertThatThrownBy(() -> issued("UNKNOWN", List.of("MTPL"))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Policy.issue(issue("MTPL_ONLY", List.of("MTPL"), 0, START, END)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Policy.issue(issue("MTPL_ONLY", List.of("MTPL"), 100, END, START)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void instalmentsCertificateAndEventsCopyThePolicy() {
        Policy p = issued("MTPL_ONLY", List.of("MTPL"));
        UUID intent = UUID.randomUUID();
        List<InstalmentSchedule.Line> lines = InstalmentSchedule.build(299_900, PaymentPlan.QUARTERLY,
                p.period().startDate());
        Instalment first = Instalment.of(p, lines.get(0), intent, START);
        Instalment second = Instalment.of(p, lines.get(1), intent, START);
        assertThat(first.getStatus()).isEqualTo(InstalmentStatus.PAID);
        assertThat(first.getPaymentIntentId()).isEqualTo(intent);
        assertThat(first.getPolicyholderUserId()).isEqualTo(p.getPolicyholderUserId());
        assertThat(second.getStatus()).isEqualTo(InstalmentStatus.DUE);
        assertThat(second.getPaidAt()).isNull();

        Certificate c = Certificate.issue(p, "CT-2027-0000001", "a".repeat(22));
        assertThat(c.getValidFrom()).isEqualTo(p.getStartAt());
        assertThat(c.getValidTo()).isEqualTo(p.getEndAt());
        assertThat(c.getInsurerId()).isEqualTo(p.getInsurerId());
        assertThat(c.toString()).doesNotContain("a".repeat(22));

        PolicyEvent e = PolicyEvent.of(p, PolicyEventType.ISSUED, START, ActorType.SYSTEM, "policy-issuance", null);
        assertThat(e.getPolicyId()).isEqualTo(p.getId());
        assertThatThrownBy(() -> PolicyEvent.of(p, PolicyEventType.ISSUED, START, ActorType.SERVICE, "x", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static Policy issued(String productType, List<String> codes) {
        return Policy.issue(issue(productType, codes, 299_900, START, END));
    }

    static Policy.Issue issue(String productType, List<String> codes, long total, Instant start, Instant end) {
        ObjectMapper json = new ObjectMapper();
        return new Policy.Issue("AP-2027-000001", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "RC Automóvel", productType, codes, UUID.randomUUID(), "Ana Maria Mussa",
                UUID.randomUUID(), "ABC-123-MC", json.createObjectNode().put("plate", "ABC-123-MC"),
                JsonNodeFactory.instance.nullNode(), json.createArrayNode(), new Policy.CoverPeriod(start, end),
                PaymentPlan.FULL, total, "APP", UUID.randomUUID(), START);
    }
}
