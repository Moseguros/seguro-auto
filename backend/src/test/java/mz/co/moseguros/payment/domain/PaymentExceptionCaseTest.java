package mz.co.moseguros.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import mz.co.moseguros.paymentcontracts.MoneyAmount;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Resoluções permitidas por tipo e regras da resolução de uma excepção de pagamento (S5-10 §6). */
class PaymentExceptionCaseTest {

    private static final Instant NOW = Instant.parse("2027-01-15T10:00:00Z");
    private static final UUID USER = UUID.randomUUID();

    static Stream<Arguments> table() {
        Set<PaymentResolution> linkRefundIgnore =
                EnumSet.of(PaymentResolution.LINKED, PaymentResolution.REFUND, PaymentResolution.IGNORED);
        Set<PaymentResolution> refundIgnore = EnumSet.of(PaymentResolution.REFUND, PaymentResolution.IGNORED);
        return Stream.of(
                Arguments.of(PaymentExceptionType.AMOUNT_MISMATCH, linkRefundIgnore),
                Arguments.of(PaymentExceptionType.UNKNOWN_REFERENCE, linkRefundIgnore),
                Arguments.of(PaymentExceptionType.EXPIRED_REFERENCE, linkRefundIgnore),
                Arguments.of(PaymentExceptionType.CANCELLED_REFERENCE, linkRefundIgnore),
                Arguments.of(PaymentExceptionType.ALREADY_PAID, refundIgnore),
                Arguments.of(PaymentExceptionType.TARGET_NOT_PAYABLE, refundIgnore),
                Arguments.of(PaymentExceptionType.UNRESOLVED_STATUS,
                        EnumSet.of(PaymentResolution.MARK_SUCCEEDED, PaymentResolution.MARK_FAILED)));
    }

    @ParameterizedTest
    @MethodSource("table")
    void allowedResolutionsFollowTheTable(PaymentExceptionType type, Set<PaymentResolution> allowed) {
        assertThat(PaymentExceptionCase.allowedFor(type)).containsExactlyInAnyOrderElementsOf(allowed);
        if (type == PaymentExceptionType.TARGET_NOT_PAYABLE || type == PaymentExceptionType.UNRESOLVED_STATUS) {
            return; // não nascem do ficheiro do banco; o PaymentExceptionIT cobre-os
        }
        for (PaymentResolution r : PaymentResolution.values()) {
            PaymentExceptionCase c = bankCase(type);
            if (allowed.contains(r)) {
                c.requireResolvable(r, 0);
            } else {
                assertThatThrownBy(() -> c.requireResolvable(r, 0))
                        .isInstanceOfSatisfying(DomainException.class,
                                e -> assertThat(e.getCode()).isEqualTo(ErrorCodes.RESOLUTION_NOT_ALLOWED));
            }
        }
    }

    @Test
    void resolveClosesTheCase() {
        PaymentExceptionCase c = bankCase(PaymentExceptionType.UNKNOWN_REFERENCE);
        UUID intent = UUID.randomUUID();
        assertThat(c.allowedResolutions()).containsExactlyInAnyOrder(PaymentResolution.LINKED,
                PaymentResolution.REFUND, PaymentResolution.IGNORED);

        c.resolve(PaymentResolution.LINKED, intent, USER, NOW, "  Associado à proposta certa.  ", 0);

        assertThat(c.getStatus()).isEqualTo(PaymentExceptionCase.Status.RESOLVED);
        assertThat(c.getResolution()).isEqualTo(PaymentResolution.LINKED);
        assertThat(c.getLinkedIntentId()).isEqualTo(intent);
        assertThat(c.getResolvedBy()).isEqualTo(USER);
        assertThat(c.getResolvedAt()).isEqualTo(NOW);
        assertThat(c.getNote()).isEqualTo("Associado à proposta certa.");
        assertThat(c.allowedResolutions()).isEmpty();
    }

    @Test
    void refundOnlyRecordsTheDecision() {
        PaymentExceptionCase c = bankCase(PaymentExceptionType.ALREADY_PAID);
        c.resolve(PaymentResolution.REFUND, UUID.randomUUID(), USER, NOW, "Devolvido por transferência.", 0);
        assertThat(c.getResolution()).isEqualTo(PaymentResolution.REFUND);
        assertThat(c.getLinkedIntentId()).isNull();
    }

    @Test
    void resolvingTwiceIsAnInvalidTransition() {
        PaymentExceptionCase c = bankCase(PaymentExceptionType.ALREADY_PAID);
        c.resolve(PaymentResolution.IGNORED, null, USER, NOW, "Teste duplicado.", 0);
        assertThatThrownBy(() -> c.resolve(PaymentResolution.REFUND, null, USER, NOW, "Outra vez.", 0))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo(ErrorCodes.INVALID_STATE_TRANSITION));
    }

    @Test
    void anOldVersionIsAConcurrentModification() {
        PaymentExceptionCase c = bankCase(PaymentExceptionType.ALREADY_PAID);
        assertThatThrownBy(() -> c.requireResolvable(PaymentResolution.IGNORED, 3))
                .isInstanceOfSatisfying(DomainException.class,
                        e -> assertThat(e.getCode()).isEqualTo(ErrorCodes.CONCURRENT_MODIFICATION));
    }

    @Test
    void bankCaseKeepsTheFileData() {
        PaymentExceptionCase c = PaymentExceptionCase.fromBankPayment(PaymentExceptionType.UNKNOWN_REFERENCE,
                message("x".repeat(300), "y".repeat(70)), null, "NOT_ISSUED");
        assertThat(c.getFileName()).hasSize(PaymentExceptionCase.MAX_FILE_NAME);
        assertThat(c.getBankTransactionId()).hasSize(PaymentExceptionCase.MAX_BANK_TRANSACTION_ID);
        assertThat(c.getDetail()).isEqualTo("NOT_ISSUED");
        assertThat(c.getAmount()).isEqualTo(250_000L);
        assertThat(c.getLineNo()).isEqualTo(3);
        assertThat(c.getStatus()).isEqualTo(PaymentExceptionCase.Status.OPEN);
        assertThatThrownBy(() -> PaymentExceptionCase.fromBankPayment(PaymentExceptionType.UNRESOLVED_STATUS,
                message("f.txt", null), null, null)).isInstanceOf(IllegalArgumentException.class);
    }

    private static PaymentExceptionCase bankCase(PaymentExceptionType type) {
        return PaymentExceptionCase.fromBankPayment(type, message("MEPS_90002_202701151.txt", null), null, null);
    }

    private static ReferencePaymentReceived message(String fileName, String bankTx) {
        return new ReferencePaymentReceived("a".repeat(64), 1, UUID.randomUUID(), UUID.randomUUID(), "MEPS", "90002",
                "00001240106", MoneyAmount.mzn(250_000), null, NOW, null, bankTx, fileName, "b".repeat(64),
                "202701151", 3);
    }
}
