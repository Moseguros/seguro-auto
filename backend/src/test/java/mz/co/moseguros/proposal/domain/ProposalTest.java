package mz.co.moseguros.proposal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.proposal.domain.CoverageLine.Deductible;
import mz.co.moseguros.proposal.domain.Proposal.CoverPeriod;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/** Regras da proposta (S4-02 §3, §6 e §9): período coberto, prazo de expiração e transições. */
class ProposalTest {

    static final Duration EXPIRY = Duration.ofDays(15);

    static Proposal draft(Instant now, List<CoverageLine> lines) {
        ObjectNode empty = JsonNodeFactory.instance.objectNode();
        return Proposal.draft(new Proposal.Draft("PR-2027-000001", UUID.randomUUID(), UUID.randomUUID(), "Seguradora",
                UUID.randomUUID(), UUID.randomUUID(), "Auto Base", ProductType.COMPREHENSIVE, UUID.randomUUID(),
                UUID.randomUUID(), ProposalChannel.APP, null, null, UUID.randomUUID(), UUID.randomUUID(), empty,
                "110100123456A", "123456789", empty, empty, empty, lines, JsonNodeFactory.instance.arrayNode(), empty,
                4_145_500L, CoverPeriod.of(LocalDate.of(2027, 1, 15), 365), 365), now, EXPIRY);
    }

    static CoverageLine line(String code) {
        return new CoverageLine(code, code.equals("MTPL"), null, Deductible.none(), Money.ofCentavos(1000));
    }

    @Test
    void coverPeriodReferenceCases() {
        CoverPeriod year = CoverPeriod.of(LocalDate.of(2027, 1, 10), 365);
        assertThat(year.endDate()).isEqualTo(LocalDate.of(2028, 1, 9));
        assertThat(year.startAt()).isEqualTo(Instant.parse("2027-01-09T22:00:00Z"));
        assertThat(year.endAt()).isEqualTo(Instant.parse("2028-01-09T22:00:00Z"));

        CoverPeriod leap = CoverPeriod.of(LocalDate.of(2027, 3, 1), 365);
        assertThat(leap.endDate()).isEqualTo(LocalDate.of(2028, 2, 29));
        assertThat(leap.endAt()).isEqualTo(Instant.parse("2028-02-29T22:00:00Z"));

        CoverPeriod month = CoverPeriod.of(LocalDate.of(2027, 2, 1), 30);
        assertThat(month.endDate()).isEqualTo(LocalDate.of(2027, 3, 2));
        assertThat(month.endAt()).isEqualTo(Instant.parse("2027-03-02T22:00:00Z"));
    }

    @Test
    void expiryReferenceCase() {
        Proposal p = draft(Instant.parse("2027-01-05T09:30:00Z"), List.of(line("MTPL")));
        assertThat(p.getStatus()).isEqualTo(ProposalStatus.DRAFT);
        assertThat(p.getContentRevision()).isEqualTo(1);
        assertThat(p.getExpiresAt()).isEqualTo(Instant.parse("2027-01-20T09:30:00Z"));
        p.confirmDeclarations(Instant.parse("2027-01-07T10:00:00Z"), EXPIRY);
        assertThat(p.getExpiresAt()).isEqualTo(Instant.parse("2027-01-22T10:00:00Z"));
        assertThat(p.getContentRevision()).isEqualTo(2);
        assertThat(p.getDeclarationsConfirmedAt()).isEqualTo(Instant.parse("2027-01-07T10:00:00Z"));
        assertThat(p.startDate()).isEqualTo(LocalDate.of(2027, 1, 15));
        assertThat(p.endDate()).isEqualTo(LocalDate.of(2028, 1, 14));
    }

    @Test
    void inspectionIsRequiredOnlyWithOwnDamage() {
        assertThat(draft(Instant.now(), List.of(line("MTPL"), line("VTPL"))).isInspectionRequired()).isFalse();
        assertThat(draft(Instant.now(), List.of(line("MTPL"), line("OD_GLASS"))).isInspectionRequired()).isTrue();
    }

    @Test
    void suitabilityAcknowledgementNeedsWarnedAndASaveClearsIt() {
        Instant now = Instant.parse("2027-01-05T09:30:00Z");
        Proposal p = draft(now, List.of(line("MTPL")));
        assertThatThrownBy(() -> p.acknowledgeSuitability(now, EXPIRY)).isInstanceOf(DomainException.class)
                .extracting("code").isEqualTo("SUITABILITY_NOT_WARNED");
        p.recordSuitability(new SuitabilityEvaluator.Evaluation(SuitabilityResult.WARNED,
                List.of(SuitabilityReason.NEED_UNSURE)), now, EXPIRY);
        p.acknowledgeSuitability(now.plusSeconds(60), EXPIRY);
        assertThat(p.getSuitabilityAcknowledgedAt()).isEqualTo(now.plusSeconds(60));
        assertThat(p.getSuitabilityReasons()).containsExactly(SuitabilityReason.NEED_UNSURE);
        p.recordSuitability(new SuitabilityEvaluator.Evaluation(SuitabilityResult.SUITABLE, List.of()), now, EXPIRY);
        assertThat(p.getSuitabilityAcknowledgedAt()).isNull();
        assertThat(p.getSuitabilityReasons()).isEmpty();
        assertThat(p.getContentRevision()).isEqualTo(4);
    }

    @Test
    void transitionsFollowTheTable() {
        Instant now = Instant.parse("2027-01-05T09:30:00Z");
        Proposal p = draft(now, List.of(line("MTPL")));
        p.transitionTo(ProposalStatus.UNDER_REVIEW, now, EXPIRY);
        assertThat(p.getExpiresAt()).isNull();
        assertThatThrownBy(p::requireEditable).isInstanceOf(DomainException.class)
                .extracting("code").isEqualTo("PROPOSAL_NOT_EDITABLE");
        assertThatThrownBy(() -> p.touchContent(now, EXPIRY)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> p.expire(now)).isInstanceOf(DomainException.class)
                .extracting("code").isEqualTo("INVALID_STATE_TRANSITION");
        p.transitionTo(ProposalStatus.INFO_REQUESTED, now, EXPIRY);
        assertThat(p.getExpiresAt()).isEqualTo(now.plus(EXPIRY));
        p.requireEditable();
        p.transitionTo(ProposalStatus.UNDER_REVIEW, now, EXPIRY);
        p.transitionTo(ProposalStatus.ACCEPTED, now, EXPIRY);
        assertThat(p.getExpiresAt()).isEqualTo(now.plus(EXPIRY));
        assertThat(p.expire(now)).isEqualTo(ProposalStatus.ACCEPTED);
        assertThat(p.getStatus()).isEqualTo(ProposalStatus.EXPIRED);
        assertThat(p.getExpiresAt()).isNull();
        assertThatThrownBy(() -> p.transitionTo(ProposalStatus.DRAFT, now, EXPIRY))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void statusTable() {
        assertThat(ProposalStatus.DRAFT.isEditable()).isTrue();
        assertThat(ProposalStatus.INFO_REQUESTED.isEditable()).isTrue();
        assertThat(ProposalStatus.ACCEPTED.isEditable()).isFalse();
        assertThat(ProposalStatus.REJECTED.isFinal()).isTrue();
        assertThat(ProposalStatus.EXPIRED.isFinal()).isTrue();
        assertThat(ProposalStatus.CONVERTED.isFinal()).isTrue();
        assertThat(ProposalStatus.PAID.isFinal()).isFalse();
        assertThat(ProposalStatus.UNDER_REVIEW.expires()).isFalse();
        assertThat(ProposalStatus.PAID.canTransitionTo(ProposalStatus.CONVERTED)).isTrue();
        assertThat(ProposalStatus.ACCEPTED.canTransitionTo(ProposalStatus.PAID)).isTrue();
        assertThat(ProposalStatus.DRAFT.canTransitionTo(ProposalStatus.PAID)).isFalse();
        assertThat(ProposalStatus.UNDER_REVIEW.canTransitionTo(ProposalStatus.REJECTED)).isTrue();
    }

    @Test
    void versionMismatchIsAConcurrentModification() {
        Proposal p = draft(Instant.now(), List.of(line("MTPL")));
        ReflectionTestUtils.setField(p, "version", 3L);
        p.requireVersion(3);
        assertThatThrownBy(() -> p.requireVersion(2)).isInstanceOf(DomainException.class)
                .extracting("code").isEqualTo("CONCURRENT_MODIFICATION");
    }

    @Test
    void brokerChannelRequiresTheBrokerAndViceVersa() {
        ObjectNode empty = JsonNodeFactory.instance.objectNode();
        assertThatThrownBy(() -> Proposal.draft(new Proposal.Draft("PR-2027-000002", UUID.randomUUID(),
                UUID.randomUUID(), "S", UUID.randomUUID(), UUID.randomUUID(), "P", ProductType.MTPL_ONLY,
                UUID.randomUUID(), UUID.randomUUID(), ProposalChannel.BROKER, null, null, UUID.randomUUID(),
                UUID.randomUUID(), empty, "DOC12345", "123456789", empty, empty, empty, List.of(),
                JsonNodeFactory.instance.arrayNode(), empty, 100L, CoverPeriod.of(LocalDate.of(2027, 1, 15), 365),
                365), Instant.now(), EXPIRY)).isInstanceOf(IllegalArgumentException.class);
    }

    /** S4-06 §1: o tomador do mediador só existe no canal BROKER; por defeito vem do perfil. */
    @Test
    void policyholderSource() {
        assertThat(draft(Instant.now(), List.of(line("MTPL"))).getPolicyholderSource())
                .isEqualTo(PolicyholderSource.PROFILE);
        ObjectNode empty = JsonNodeFactory.instance.objectNode();
        Proposal.Draft broker = new Proposal.Draft("PR-2027-000003", UUID.randomUUID(), UUID.randomUUID(), "S",
                UUID.randomUUID(), UUID.randomUUID(), "P", ProductType.MTPL_ONLY, UUID.randomUUID(), UUID.randomUUID(),
                ProposalChannel.BROKER, UUID.randomUUID(), "Corretora", UUID.randomUUID(), UUID.randomUUID(), empty,
                "DOC12345", "123456789", empty, empty, empty, List.of(), JsonNodeFactory.instance.arrayNode(), empty,
                100L, CoverPeriod.of(LocalDate.of(2027, 1, 15), 365), 365);
        assertThat(Proposal.draft(broker, PolicyholderSource.BROKER, Instant.now(), EXPIRY).getPolicyholderSource())
                .isEqualTo(PolicyholderSource.BROKER);
        Proposal.Draft app = new Proposal.Draft("PR-2027-000004", UUID.randomUUID(), UUID.randomUUID(), "S",
                UUID.randomUUID(), UUID.randomUUID(), "P", ProductType.MTPL_ONLY, UUID.randomUUID(), UUID.randomUUID(),
                ProposalChannel.APP, null, null, UUID.randomUUID(), UUID.randomUUID(), empty, "DOC12345", "123456789",
                empty, empty, empty, List.of(), JsonNodeFactory.instance.arrayNode(), empty, 100L,
                CoverPeriod.of(LocalDate.of(2027, 1, 15), 365), 365);
        assertThatThrownBy(() -> Proposal.draft(app, PolicyholderSource.BROKER, Instant.now(), EXPIRY))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void policyholderMasks() {
        PolicyholderSnapshot p = new PolicyholderSnapshot("Ana", LocalDate.of(1990, 5, 14), "BI", "*****345A",
                "*****6789", "Av.", "MAPUTO_CITY", "+258841234567", "ana@exemplo.co.mz", "pt");
        assertThat(p.msisdnMasked()).isEqualTo("+258*****4567");
        assertThat(p.emailMasked()).isEqualTo("a***@exemplo.co.mz");
        PolicyholderSnapshot none = new PolicyholderSnapshot("Ana", null, null, null, null, null, null, null, null,
                null);
        assertThat(none.msisdnMasked()).isNull();
        assertThat(none.emailMasked()).isNull();
    }

    @Test
    void suitabilityQuestionsAcceptOnlyTheirOptions() {
        assertThat(SuitabilityQuestion.SUIT_NEED.accepts("FULL")).isTrue();
        assertThat(SuitabilityQuestion.SUIT_NEED.accepts("YES")).isFalse();
        assertThat(SuitabilityQuestion.SUIT_FINANCED.accepts("UNSURE")).isFalse();
        assertThat(SuitabilityQuestion.SUIT_DEDUCTIBLE.accepts("UNSURE")).isTrue();
        assertThat(SuitabilityQuestion.SUIT_CROSS_BORDER.accepts(null)).isFalse();
        assertThat(DeclarationQuestion.CLAIMS_LAST_3_YEARS.requiresClaimsCount()).isTrue();
        assertThat(DeclarationQuestion.NOVICE_DRIVERS.requiresClaimsCount()).isFalse();
    }
}
