package mz.co.moseguros.paymentcontracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PaymentsTopologyTest {

    private static final UUID INSURER = UUID.fromString("01930000-0000-7000-8000-000000000001");

    @Test
    void routingKeysEndWithTheInsurerId() {
        assertEquals("reference.paid.01930000-0000-7000-8000-000000000001", PaymentsTopology.referencePaidKey(INSURER));
        assertEquals("reference.file-processed.01930000-0000-7000-8000-000000000001",
                PaymentsTopology.fileProcessedKey(INSURER));
        assertEquals("reference.file-rejected.01930000-0000-7000-8000-000000000001",
                PaymentsTopology.fileRejectedKey(INSURER));
        assertEquals("reference.source-polled.01930000-0000-7000-8000-000000000001",
                PaymentsTopology.sourcePolledKey(INSURER));
        assertThrows(NullPointerException.class, () -> PaymentsTopology.referencePaidKey(null));
    }

    @Test
    void bindingsAreExplicitPerMessage() {
        assertEquals("reference.paid.*", PaymentsTopology.REFERENCE_PAID_BINDING);
        assertEquals(List.of("reference.file-processed.*", "reference.file-rejected.*", "reference.source-polled.*"),
                PaymentsTopology.PAYMENT_FILE_EVENTS_BINDINGS);
    }

    @Test
    void queueArguments() {
        assertEquals(Map.of("x-queue-type", "quorum", "x-dead-letter-exchange", "payments.dlx",
                        "x-dead-letter-routing-key", "core.reference-payments", "x-delivery-limit", 20),
                PaymentsTopology.mainQueueArguments(PaymentsTopology.REFERENCE_PAYMENTS_QUEUE));
        assertEquals(Map.of("x-queue-type", "quorum"), PaymentsTopology.deadLetterQueueArguments());
        assertEquals("core.reference-payments.dlq", PaymentsTopology.REFERENCE_PAYMENTS_DLQ);
        assertEquals("core.payment-file-events.dlq", PaymentsTopology.PAYMENT_FILE_EVENTS_DLQ);
    }

    @Test
    void messageTypeIsTheSimpleClassName() {
        assertEquals("ReferencePaymentReceived", MessageHeaders.typeOf(ReferencePaymentReceived.class));
        assertEquals("PaymentSourcePolled", MessageHeaders.typeOf(PaymentSourcePolled.class));
        assertEquals(1, MessageHeaders.SCHEMA_VERSION);
    }
}
