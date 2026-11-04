package mz.co.moseguros.proposal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/** Pedido de assinatura, assinatura e invalidação da proposta (S4-04 §5 e §7). */
class ProposalSignatureTest {

    static final Duration EXPIRY = Duration.ofDays(15);
    static final Instant NOW = Instant.parse("2027-01-05T09:30:00Z");
    static final String HASH = "a".repeat(64);

    private static Proposal proposal() {
        return ProposalTest.draft(NOW, List.of(ProposalTest.line("MTPL")));
    }

    private static Signature signed(Proposal p, UUID challengeId, String ua) {
        return Signature.of(p, new Signature.Signed(p.getSignRequestFileId(), p.getSignRequestHash(), challengeId,
                Channel.SMS, "+258*****4567", null, null, NOW.plusSeconds(60), "41.220.1.10", ua));
    }

    @Test
    void requestThenSignKeepsTheRevisionAndRenewsTheExpiry() {
        Proposal p = proposal();
        UUID file = UUID.randomUUID();
        UUID challenge = UUID.randomUUID();
        p.requestSignature(file, HASH, challenge, null);

        assertThat(p.getContentRevision()).isEqualTo(1);
        assertThat(p.getSignRequestRevision()).isEqualTo(1);
        assertThat(p.getSignRequestFileId()).isEqualTo(file);
        p.requireSignRequest(challenge);

        Signature signature = signed(p, UUID.randomUUID(), "x".repeat(300));
        assertThat(signature.getUserAgent()).hasSize(200);
        assertThat(signature.getContentRevision()).isEqualTo(1);
        assertThat(signature.getSignerUserId()).isEqualTo(p.getCustomerUserId());
        assertThat(signature.getMethod()).isEqualTo(Signature.Method.OTP);
        assertThat(signature.isAssisted()).isFalse();
        p.sign(signature, NOW.plusSeconds(60), EXPIRY);

        assertThat(p.getCurrentSignature()).isSameAs(signature);
        assertThat(p.getSignRequestChallengeId()).isNull();
        assertThat(p.getSignRequestFileId()).isNull();
        assertThat(p.getSignRequestHash()).isNull();
        assertThat(p.getSignRequestRevision()).isNull();
        assertThat(p.getExpiresAt()).isEqualTo(NOW.plusSeconds(60).plus(EXPIRY));
        assertThat(signature.isValid()).isTrue();
    }

    @Test
    void anyContentChangeInvalidatesTheSignatureAndTheRequest() {
        Proposal p = proposal();
        p.requestSignature(UUID.randomUUID(), HASH, UUID.randomUUID(), null);
        Signature signature = signed(p, UUID.randomUUID(), null);
        p.sign(signature, NOW, EXPIRY);

        p.confirmDeclarations(NOW.plusSeconds(120), EXPIRY);

        assertThat(p.getCurrentSignature()).isNull();
        assertThat(signature.isValid()).isFalse();
        assertThat(signature.getInvalidatedReason()).isEqualTo(Signature.InvalidationReason.CONTENT_CHANGED);
        assertThat(signature.getInvalidatedAt()).isEqualTo(NOW.plusSeconds(120));
        assertThat(p.getContentRevision()).isEqualTo(2);
    }

    @Test
    void infoRequestedInvalidatesWithItsReason() {
        Proposal p = proposal();
        p.requestSignature(UUID.randomUUID(), HASH, UUID.randomUUID(), null);
        Signature signature = signed(p, UUID.randomUUID(), null);
        p.sign(signature, NOW, EXPIRY);

        p.contentChanged(Signature.InvalidationReason.INFO_REQUESTED, NOW.plusSeconds(5));
        p.contentChanged(Signature.InvalidationReason.CONTENT_CHANGED, NOW.plusSeconds(10));

        assertThat(signature.getInvalidatedReason()).isEqualTo(Signature.InvalidationReason.INFO_REQUESTED);
        assertThat(signature.getInvalidatedAt()).isEqualTo(NOW.plusSeconds(5));
        assertThat(p.getContentRevision()).isEqualTo(1);
    }

    @Test
    void aChangeBetweenRequestAndConfirmationIsDetected() {
        Proposal p = proposal();
        UUID challenge = UUID.randomUUID();
        p.requestSignature(UUID.randomUUID(), HASH, challenge, null);
        // Uma alteração "a meio" sem passar pelo domínio (ex.: outra instância) também é apanhada.
        ReflectionTestUtils.setField(p, "contentRevision", 2);

        assertThatThrownBy(() -> p.requireSignRequest(challenge)).isInstanceOfSatisfying(DomainException.class,
                e -> assertThat(e.getCode()).isEqualTo("PROPOSAL_CHANGED_SINCE_SIGN_REQUEST"));
        assertThatThrownBy(() -> p.requireSignRequest(UUID.randomUUID())).isInstanceOfSatisfying(
                DomainException.class, e -> assertThat(e.getCode()).isEqualTo("SIGN_REQUEST_NOT_FOUND"));
    }

    @Test
    void withoutRequestThereIsNothingToConfirm() {
        Proposal p = proposal();
        assertThatThrownBy(() -> p.requireSignRequest(UUID.randomUUID())).isInstanceOfSatisfying(
                DomainException.class, e -> assertThat(e.getCode()).isEqualTo("SIGN_REQUEST_NOT_FOUND"));
        assertThatThrownBy(() -> p.requireSignRequest(null)).isInstanceOfSatisfying(
                DomainException.class, e -> assertThat(e.getCode()).isEqualTo("SIGN_REQUEST_NOT_FOUND"));
    }

    @Test
    void aChangeAfterTheRequestMakesItStaleAndANewRequestReplacesIt() {
        Proposal p = proposal();
        UUID challenge = UUID.randomUUID();
        p.requestSignature(UUID.randomUUID(), HASH, challenge, UUID.randomUUID());
        assertThat(p.getSignRequestAssistedByUserId()).isNotNull();

        p.confirmDeclarations(NOW, EXPIRY);

        assertThat(p.getSignRequestChallengeId()).isEqualTo(challenge);
        assertThat(p.getSignRequestRevision()).isEqualTo(1);
        assertThatThrownBy(() -> p.requireSignRequest(challenge)).isInstanceOfSatisfying(DomainException.class,
                e -> assertThat(e.getCode()).isEqualTo("PROPOSAL_CHANGED_SINCE_SIGN_REQUEST"));

        UUID again = UUID.randomUUID();
        p.requestSignature(UUID.randomUUID(), HASH, again, null);
        assertThat(p.getSignRequestRevision()).isEqualTo(2);
        assertThat(p.getSignRequestAssistedByUserId()).isNull();
        p.requireSignRequest(again);
    }

    @Test
    void notEditableProposalsCannotBeSigned() {
        Proposal p = proposal();
        p.requestSignature(UUID.randomUUID(), HASH, UUID.randomUUID(), null);
        Signature signature = signed(p, UUID.randomUUID(), null);
        ReflectionTestUtils.setField(p, "status", ProposalStatus.UNDER_REVIEW);

        assertThatThrownBy(() -> p.sign(signature, NOW, EXPIRY)).isInstanceOfSatisfying(DomainException.class,
                e -> assertThat(e.getCode()).isEqualTo("PROPOSAL_NOT_EDITABLE"));
        assertThatThrownBy(() -> p.requestSignature(UUID.randomUUID(), HASH, UUID.randomUUID(), null))
                .isInstanceOf(DomainException.class);
    }

    @Test
    void aSignatureOfAnotherRevisionIsRefused() {
        Proposal p = proposal();
        p.requestSignature(UUID.randomUUID(), HASH, UUID.randomUUID(), null);
        Signature old = signed(p, UUID.randomUUID(), null);
        p.confirmDeclarations(NOW, EXPIRY);

        assertThatThrownBy(() -> p.sign(old, NOW, EXPIRY)).isInstanceOf(IllegalArgumentException.class);
    }
}
