package mz.co.moseguros.payment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import mz.co.moseguros.payment.FailureCode;
import mz.co.moseguros.payment.InitiatedVia;
import mz.co.moseguros.payment.PaymentMethod;
import mz.co.moseguros.payment.PaymentPurpose;
import mz.co.moseguros.payment.PaymentStatus;
import mz.co.moseguros.payment.PaymentTargetType;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Tabela de estados da intenção (S5-02 §3): cada acção, em cada estado e em cada método, ou chega ao
 * estado da tabela ou dá 409 {@code INVALID_STATE_TRANSITION}.
 */
class PaymentIntentTest {

    private static final Instant NOW = Instant.parse("2027-01-15T10:00:00Z");

    /** Acções do domínio. */
    enum Action {
        MARK_PENDING(i -> i.markPending(NOW, "tx", "conv", "3rd")),
        MARK_SUCCEEDED(i -> i.markSucceeded(NOW, ConfirmedBy.PROVIDER, "tx")),
        MARK_FAILED(i -> i.markFailed(FailureCode.PROVIDER_REJECTED, NOW)),
        MARK_UNKNOWN(i -> i.markUnknown(NOW, NOW.plusSeconds(30))),
        EXPIRE(i -> i.expire(NOW)),
        CANCEL(i -> i.cancel(FailureCode.SUPERSEDED, NOW)),
        REFUND(PaymentIntent::markRefunded);

        final Consumer<PaymentIntent> run;

        Action(Consumer<PaymentIntent> run) {
            this.run = run;
        }
    }

    /** As linhas permitidas da tabela do §3: (método, estado de partida, acção) → estado de chegada. */
    private static final Map<String, PaymentStatus> ALLOWED = allowed();

    private static Map<String, PaymentStatus> allowed() {
        Map<String, PaymentStatus> m = new java.util.HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            m.put(key(method, PaymentStatus.CREATED, Action.MARK_PENDING), PaymentStatus.PENDING);
            for (PaymentStatus from : Set.of(PaymentStatus.PENDING, PaymentStatus.UNKNOWN, PaymentStatus.EXPIRED,
                    PaymentStatus.CANCELLED)) {
                m.put(key(method, from, Action.MARK_SUCCEEDED), PaymentStatus.SUCCEEDED);
            }
            m.put(key(method, PaymentStatus.PENDING, Action.MARK_FAILED), PaymentStatus.FAILED);
            m.put(key(method, PaymentStatus.UNKNOWN, Action.MARK_FAILED), PaymentStatus.FAILED);
            m.put(key(method, PaymentStatus.PENDING, Action.MARK_UNKNOWN), PaymentStatus.UNKNOWN);
            m.put(key(method, PaymentStatus.SUCCEEDED, Action.REFUND), PaymentStatus.REFUNDED);
        }
        m.put(key(PaymentMethod.BANK_REFERENCE, PaymentStatus.CREATED, Action.EXPIRE), PaymentStatus.EXPIRED);
        m.put(key(PaymentMethod.BANK_REFERENCE, PaymentStatus.PENDING, Action.EXPIRE), PaymentStatus.EXPIRED);
        m.put(key(PaymentMethod.MPESA, PaymentStatus.CREATED, Action.EXPIRE), PaymentStatus.EXPIRED);
        m.put(key(PaymentMethod.BANK_REFERENCE, PaymentStatus.CREATED, Action.CANCEL), PaymentStatus.CANCELLED);
        m.put(key(PaymentMethod.BANK_REFERENCE, PaymentStatus.PENDING, Action.CANCEL), PaymentStatus.CANCELLED);
        return Map.copyOf(m);
    }

    private static String key(PaymentMethod method, PaymentStatus from, Action action) {
        return method + "/" + from + "/" + action;
    }

    static Stream<Arguments> everyTransition() {
        List<Arguments> list = new ArrayList<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            for (PaymentStatus from : PaymentStatus.values()) {
                for (Action action : Action.values()) {
                    list.add(Arguments.of(method, from, action, ALLOWED.get(key(method, from, action))));
                }
            }
        }
        return list.stream();
    }

    @ParameterizedTest(name = "{0} {1} {2} → {3}")
    @MethodSource("everyTransition")
    void followsTheStateTable(PaymentMethod method, PaymentStatus from, Action action, PaymentStatus expected) {
        PaymentIntent intent = intent(method);
        ReflectionTestUtils.setField(intent, "status", from);
        if (expected == null) {
            assertThatThrownBy(() -> action.run.accept(intent))
                    .isInstanceOfSatisfying(DomainException.class, e -> {
                        assertThat(e.getCode()).isEqualTo("INVALID_STATE_TRANSITION");
                        assertThat(e.getHttpStatus()).isEqualTo(409);
                    });
            assertThat(intent.getStatus()).isEqualTo(from);
        } else {
            action.run.accept(intent);
            assertThat(intent.getStatus()).isEqualTo(expected);
        }
    }

    @Test
    void theTableHasEveryRowOfTheSpecification() {
        // 2 × (pendente, 4 confirmações, 2 falhas, desconhecido, reembolso) + 3 expirações + 2 cancelamentos
        assertThat(ALLOWED).hasSize(2 * 9 + 3 + 2);
    }

    @Test
    void createStartsInCreatedAndMpesaNeedsTheMsisdn() {
        PaymentIntent mpesa = intent(PaymentMethod.MPESA);
        assertThat(mpesa.getStatus()).isEqualTo(PaymentStatus.CREATED);
        assertThat(mpesa.getPayerMsisdn().value()).isEqualTo("258841234567");
        assertThatThrownBy(() -> PaymentIntent.create(newIntent(PaymentMethod.MPESA, null)))
                .isInstanceOf(IllegalArgumentException.class);
        PaymentIntent reference = PaymentIntent.create(newIntent(PaymentMethod.BANK_REFERENCE,
                Msisdn.of("841234567")));
        assertThat(reference.getPayerMsisdn()).as("ignored for references").isNull();
    }

    @Test
    void transitionsRecordTheirData() {
        PaymentIntent mpesa = intent(PaymentMethod.MPESA);
        mpesa.markPending(NOW, "TX1", "CONV1", "THIRD1");
        assertThat(mpesa.getDispatchedAt()).isEqualTo(NOW);
        assertThat(mpesa.getProviderTxId()).isEqualTo("TX1");
        assertThat(mpesa.getThirdPartyReference()).isEqualTo("THIRD1");
        mpesa.markUnknown(NOW, NOW.plusSeconds(30));
        assertThat(mpesa.getUnknownSince()).isEqualTo(NOW);
        assertThat(mpesa.getNextQueryAt()).isEqualTo(NOW.plusSeconds(30));
        mpesa.markSucceeded(NOW.plusSeconds(40), ConfirmedBy.STATUS_QUERY, null);
        assertThat(mpesa.getSucceededAt()).isEqualTo(NOW.plusSeconds(40));
        assertThat(mpesa.getConfirmedBy()).isEqualTo(ConfirmedBy.STATUS_QUERY);
        assertThat(mpesa.getNextQueryAt()).isNull();

        PaymentIntent reference = intent(PaymentMethod.BANK_REFERENCE);
        reference.markPending(NOW, null, null, null);
        assertThat(reference.getDispatchedAt()).as("only M-Pesa is dispatched").isNull();
        reference.cancel(FailureCode.SUPERSEDED, NOW);
        assertThat(reference.getFailureCode()).isEqualTo(FailureCode.SUPERSEDED);

        PaymentIntent expired = intent(PaymentMethod.BANK_REFERENCE);
        expired.expire(NOW);
        assertThat(expired.getFailureCode()).isEqualTo(FailureCode.EXPIRED);
        expired.markSucceeded(NOW, ConfirmedBy.REFERENCE_FILE, null);
        assertThat(expired.getFailureCode()).as("a late payment is still confirmed").isNull();
    }

    static PaymentIntent intent(PaymentMethod method) {
        return PaymentIntent.create(newIntent(method, Msisdn.of("841234567")));
    }

    static PaymentIntent.New newIntent(PaymentMethod method, Msisdn msisdn) {
        return new PaymentIntent.New("PG-2027-0000001", UUID.randomUUID(), UUID.randomUUID(),
                PaymentPurpose.NEW_POLICY, PaymentTargetType.PROPOSAL, UUID.randomUUID(), "PR-2027-000001",
                Money.ofCentavos(299_900), method, UUID.randomUUID(), msisdn, UUID.randomUUID(),
                NOW.plusSeconds(3600), UUID.randomUUID(), InitiatedVia.WEB);
    }
}
