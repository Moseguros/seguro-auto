package mz.co.moseguros.proposal.application;

import static mz.co.moseguros.proposal.application.ProposalCompleteness.Item.DECLARATIONS;
import static mz.co.moseguros.proposal.application.ProposalCompleteness.Item.INSPECTION;
import static mz.co.moseguros.proposal.application.ProposalCompleteness.Item.SIGNATURE;
import static mz.co.moseguros.proposal.application.ProposalCompleteness.Item.START_DATE;
import static mz.co.moseguros.proposal.application.ProposalCompleteness.Item.SUITABILITY;
import static mz.co.moseguros.proposal.application.ProposalCompleteness.Item.SUITABILITY_ACKNOWLEDGEMENT;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import mz.co.moseguros.proposal.application.ProposalCompleteness.Facts;
import mz.co.moseguros.proposal.domain.SuitabilityResult;
import org.junit.jupiter.api.Test;

/** {@link ProposalCompleteness}: uma verificação por linha da tabela do S4-04 §6. */
class ProposalCompletenessTest {

    /** 10/01/2027, 08:15 UTC = 10:15 em Maputo. */
    static final Instant NOW = Instant.parse("2027-01-10T08:15:00Z");
    /** 00:00 de 10/01/2027 em Maputo. */
    static final Instant START_OF_TODAY = Instant.parse("2027-01-09T22:00:00Z");
    static final Instant CONFIRMED = Instant.parse("2027-01-09T10:00:00Z");

    /** Proposta completa (sem vistoria), ainda por assinar. */
    static Facts complete() {
        return new Facts(CONFIRMED, SuitabilityResult.SUITABLE, null, false, null, START_OF_TODAY, false);
    }

    @Test
    void completeProposalHasNothingMissingBeforeSigning() {
        assertThat(ProposalCompleteness.missing(complete(), NOW, false)).isEmpty();
    }

    @Test
    void declarationsMissing() {
        Facts f = new Facts(null, SuitabilityResult.SUITABLE, null, false, null, START_OF_TODAY, false);
        assertThat(ProposalCompleteness.missing(f, NOW, false)).containsExactly(DECLARATIONS);
    }

    @Test
    void suitabilityMissing() {
        Facts f = new Facts(CONFIRMED, null, null, false, null, START_OF_TODAY, false);
        assertThat(ProposalCompleteness.missing(f, NOW, false)).containsExactly(SUITABILITY);
    }

    @Test
    void warnedWithoutAcknowledgement() {
        Facts warned = new Facts(CONFIRMED, SuitabilityResult.WARNED, null, false, null, START_OF_TODAY, false);
        assertThat(ProposalCompleteness.missing(warned, NOW, false)).containsExactly(SUITABILITY_ACKNOWLEDGEMENT);

        Facts acknowledged = new Facts(CONFIRMED, SuitabilityResult.WARNED, CONFIRMED, false, null, START_OF_TODAY,
                false);
        assertThat(ProposalCompleteness.missing(acknowledged, NOW, false)).isEmpty();
    }

    @Test
    void inspectionRequiredAndNotSubmittedNorApproved() {
        for (String status : new String[] {null, "PENDING", "REJECTED"}) {
            Facts f = new Facts(CONFIRMED, SuitabilityResult.SUITABLE, null, true, status, START_OF_TODAY, false);
            assertThat(ProposalCompleteness.missing(f, NOW, false)).as(String.valueOf(status))
                    .containsExactly(INSPECTION);
        }
        for (String status : new String[] {"SUBMITTED", "APPROVED"}) {
            Facts f = new Facts(CONFIRMED, SuitabilityResult.SUITABLE, null, true, status, START_OF_TODAY, false);
            assertThat(ProposalCompleteness.missing(f, NOW, false)).as(status).isEmpty();
        }
        Facts notRequired = new Facts(CONFIRMED, SuitabilityResult.SUITABLE, null, false, "PENDING", START_OF_TODAY,
                false);
        assertThat(ProposalCompleteness.missing(notRequired, NOW, false)).isEmpty();
    }

    @Test
    void startDateBeforeTodayInMaputo() {
        Facts yesterday = new Facts(CONFIRMED, SuitabilityResult.SUITABLE, null, false, null,
                START_OF_TODAY.minusSeconds(86_400), false);
        assertThat(ProposalCompleteness.missing(yesterday, NOW, false)).containsExactly(START_DATE);

        // 23:30 UTC de 9 de Janeiro já é dia 10 em Maputo: início hoje ainda serve.
        assertThat(ProposalCompleteness.missing(complete(), Instant.parse("2027-01-09T23:30:00Z"), false)).isEmpty();
    }

    @Test
    void signatureOnlyWhenRequested() {
        assertThat(ProposalCompleteness.missing(complete(), NOW, true)).containsExactly(SIGNATURE);
        Facts signed = new Facts(CONFIRMED, SuitabilityResult.SUITABLE, null, false, null, START_OF_TODAY, true);
        assertThat(ProposalCompleteness.missing(signed, NOW, true)).isEmpty();
    }

    @Test
    void itemsComeInTheOrderOfTheTable() {
        Facts nothing = new Facts(null, SuitabilityResult.WARNED, null, true, null,
                START_OF_TODAY.minusSeconds(1), false);
        assertThat(ProposalCompleteness.missing(nothing, NOW, true))
                .containsExactly(DECLARATIONS, SUITABILITY_ACKNOWLEDGEMENT, INSPECTION, START_DATE, SIGNATURE);
        Facts noSuitability = new Facts(null, null, null, false, null, START_OF_TODAY, false);
        assertThat(ProposalCompleteness.missing(noSuitability, NOW, true))
                .containsExactly(DECLARATIONS, SUITABILITY, SIGNATURE);
    }
}
