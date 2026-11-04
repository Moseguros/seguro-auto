package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import mz.co.moseguros.identity.KycState;
import mz.co.moseguros.identity.KycStatus;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;

/** Estado KYC a partir da verificação mais recente (S4-01 §5): as cinco linhas da tabela. */
class KycStatusTest {

    private static final UUID INSURER = UUID.randomUUID();
    private static final UUID REVIEWER = UUID.randomUUID();
    private static final Instant NOW = Instant.parse("2027-01-05T09:30:00Z");

    private static CustomerProfile profile(String docNumber) {
        CustomerProfile profile = CustomerProfile.emptyFor(UserAccount.newCustomer(Msisdn.of("841234567")));
        profile.update("Ana Maria Mussa", LocalDate.of(1990, 5, 14), IdDocType.BI, docNumber, "123456789",
                "Av. Julius Nyerere, 1234", Province.MAPUTO_CITY);
        return profile;
    }

    @Test
    void noCheckIsNone() {
        assertThat(KycCheck.statusOf(null, "abc")).isEqualTo(new KycStatus(KycState.NONE, null, null, null));
    }

    @Test
    void pendingIsPending() {
        KycCheck check = KycCheck.submit(profile("110100123456A"), INSURER, NOW);
        KycStatus status = KycCheck.statusOf(check, ProfileFingerprint.of(profile("110100123456A")));
        assertThat(status).isEqualTo(new KycStatus(KycState.PENDING, check.getId(), null, null));
    }

    @Test
    void rejectedIsRejected() {
        KycCheck check = KycCheck.submit(profile("110100123456A"), INSURER, NOW);
        check.reject(REVIEWER, NOW.plusSeconds(60), KycRejectionReason.DATA_MISMATCH, null);
        KycStatus status = KycCheck.statusOf(check, ProfileFingerprint.of(profile("110100123456A")));
        assertThat(status).isEqualTo(new KycStatus(KycState.REJECTED, check.getId(), null, null));
    }

    @Test
    void verifiedWithTheSameProfileIsVerified() {
        CustomerProfile profile = profile("110100123456A");
        KycCheck check = KycCheck.submit(profile, INSURER, NOW);
        check.verify(REVIEWER, NOW.plusSeconds(60));
        KycStatus status = KycCheck.statusOf(check, ProfileFingerprint.of(profile));
        assertThat(status).isEqualTo(new KycStatus(KycState.VERIFIED, check.getId(), NOW.plusSeconds(60), INSURER));
    }

    @Test
    void verifiedWithAChangedProfileIsOutdated() {
        KycCheck check = KycCheck.submit(profile("110100123456A"), INSURER, NOW);
        check.verify(REVIEWER, NOW.plusSeconds(60));
        KycStatus status = KycCheck.statusOf(check, ProfileFingerprint.of(profile("110100999999B")));
        assertThat(status).isEqualTo(new KycStatus(KycState.OUTDATED, check.getId(), NOW.plusSeconds(60), INSURER));
        // Um perfil que ficou incompleto também não coincide.
        assertThat(KycCheck.statusOf(check, null).state()).isEqualTo(KycState.OUTDATED);
    }

    @Test
    void onlyPendingChecksCanBeDecided() {
        KycCheck check = KycCheck.submit(profile("110100123456A"), INSURER, NOW);
        check.verify(REVIEWER, NOW);
        assertThatThrownBy(() -> check.verify(REVIEWER, NOW))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo("INVALID_STATE_TRANSITION"));
        assertThatThrownBy(() -> check.reject(REVIEWER, NOW, KycRejectionReason.OTHER, "Motivo qualquer."))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void copiesTheProfileAndMasksTheDocument() {
        KycCheck check = KycCheck.submit(profile("110100123456A"), INSURER, NOW);
        assertThat(check.getFullName()).isEqualTo("Ana Maria Mussa");
        assertThat(check.firstName()).isEqualTo("Ana");
        assertThat(check.idDocNumberMasked()).isEqualTo("*********456A");
        assertThat(check.getStatus()).isEqualTo(KycCheckStatus.PENDING);
        assertThat(KycCheck.requiresBack(IdDocType.BI)).isTrue();
        assertThat(KycCheck.requiresBack(IdDocType.DIRE)).isTrue();
        assertThat(KycCheck.requiresBack(IdDocType.PASSPORT)).isFalse();
    }
}
