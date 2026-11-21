package mz.co.moseguros.verification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import mz.co.moseguros.policy.CertificateStatus;
import mz.co.moseguros.policy.CoverStatus;
import mz.co.moseguros.policy.CoverageType;
import mz.co.moseguros.policy.PolicyCoverView;
import mz.co.moseguros.policy.PolicyStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Tabela da secção 3 do S6-03: {@code CoverStatus} → resultado, e {@code NOT_FOUND} sem apólice. */
class VerificationResultMappingTest {

    static final Instant NOW = Instant.parse("2027-03-01T09:15:02Z");

    @ParameterizedTest
    @CsvSource({
        "COVERED, VALID",
        "NOT_STARTED, NOT_YET_VALID",
        "ENDED, EXPIRED",
        "LAPSED, EXPIRED",
        "SUSPENDED, SUSPENDED",
        "CANCELLED, CANCELLED"
    })
    void mapsEachCoverStatus(CoverStatus cover, VerificationResult expected) {
        assertThat(VerificationResult.of(cover)).isEqualTo(expected);
    }

    @Test
    void withoutPolicyIsNotFound() {
        assertThat(VerificationResult.of((PolicyCoverView) null, NOW)).isEqualTo(VerificationResult.NOT_FOUND);
    }

    @ParameterizedTest
    @CsvSource({
        // estado, início (dias a partir de agora), fim, resultado
        "ACTIVE, -10, 300, VALID",
        "ACTIVE, 5, 370, NOT_YET_VALID",
        "ACTIVE, -400, -35, EXPIRED",
        "ACTIVE, -365, 0, EXPIRED",
        "EXPIRED, -400, -35, EXPIRED",
        "LAPSED, -100, 200, EXPIRED",
        "SUSPENDED, -10, 300, SUSPENDED",
        "CANCELLED, -10, 300, CANCELLED"
    })
    void computesTheStatusFromTheViewNow(PolicyStatus status, long startDays, long endDays,
                                         VerificationResult expected) {
        PolicyCoverView view = new PolicyCoverView(UUID.randomUUID(), "AP-2027-000001", UUID.randomUUID(),
                "ABC-123-MC", "Toyota", "Corolla", CoverageType.MTPL_ONLY, status,
                NOW.plus(Duration.ofDays(startDays)), NOW.plus(Duration.ofDays(endDays)), UUID.randomUUID(),
                "CT-2027-0000001", CertificateStatus.VALID);
        assertThat(VerificationResult.of(view, NOW)).isEqualTo(expected);
    }

    @Test
    void theStartIsInclusiveAndTheEndExclusive() {
        Instant start = NOW;
        Instant end = NOW.plus(Duration.ofDays(1));
        assertThat(VerificationResult.coverAt(PolicyStatus.ACTIVE, start, end, NOW)).isEqualTo(CoverStatus.COVERED);
        assertThat(VerificationResult.coverAt(PolicyStatus.ACTIVE, start, end, NOW.minusMillis(1)))
                .isEqualTo(CoverStatus.NOT_STARTED);
        assertThat(VerificationResult.coverAt(PolicyStatus.ACTIVE, start, end, end)).isEqualTo(CoverStatus.ENDED);
    }
}
