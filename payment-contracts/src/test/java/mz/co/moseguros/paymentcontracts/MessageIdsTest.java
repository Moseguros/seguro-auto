package mz.co.moseguros.paymentcontracts;

import static mz.co.moseguros.paymentcontracts.ContractSupport.sample;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Vectores fixos dos {@code messageId} (S5-06 §2). Calculados à parte com {@code sha256sum}. */
class MessageIdsTest {

    private static final UUID SOURCE_MEPS = UUID.fromString("01930000-0000-7000-8000-000000000501");
    private static final UUID SOURCE_BMEPS = UUID.fromString("01930000-0000-7000-8000-000000000502");
    /** SHA-256 de docs/specs/interbancos/exemplo-ficheiro-meps.txt. */
    private static final String MEPS_SHA = "1b96115a3a8e399da57a6bdb3e7371a8ca4bbada5aca146d1e2077507217256e";
    /** SHA-256 de docs/specs/interbancos/exemplo-ficheiro-bmeps.txt. */
    private static final String BMEPS_SHA = "851bd3a0f75a2b7b96b9662b9f98218f97f48fdd145459520d91f11ec9cb383a";

    @Test
    void referencePaymentReceivedIsPerFileAndLine() {
        // printf '%s' "01930000-0000-7000-8000-000000000501|<MEPS_SHA>|2" | sha256sum
        assertEquals("50065d4ad175a10c3f6add98fe08abb964baa0b8189ea6749420597ca6b4ff01",
                MessageIds.referencePaymentReceived(SOURCE_MEPS, MEPS_SHA, 2));
        assertEquals("76c022acf297d4427fd0c72bf8c82d706d773a6bcade03a432c58e333db8fa3e",
                MessageIds.referencePaymentReceived(SOURCE_MEPS, MEPS_SHA, 3));
        assertEquals("ce2b5014746967d73fe998d2b8a8bf2e056c5ed4a798755cf70d3a3cd3ed46d2",
                MessageIds.referencePaymentReceived(SOURCE_BMEPS, BMEPS_SHA, 2));
    }

    @Test
    void samePaymentDataOnDifferentLinesGivesDifferentIds() {
        // MEPS linhas 7 e 8: a mesma referência (00620400869) e o mesmo montante, pagas duas vezes.
        String line7 = MessageIds.referencePaymentReceived(SOURCE_MEPS, MEPS_SHA, 7);
        String line8 = MessageIds.referencePaymentReceived(SOURCE_MEPS, MEPS_SHA, 8);
        assertNotEquals(line7, line8);
    }

    @Test
    void sameLineInAnotherSourceOrFileGivesDifferentIds() {
        assertNotEquals(MessageIds.referencePaymentReceived(SOURCE_MEPS, MEPS_SHA, 2),
                MessageIds.referencePaymentReceived(SOURCE_BMEPS, MEPS_SHA, 2));
        assertNotEquals(MessageIds.referencePaymentReceived(SOURCE_MEPS, MEPS_SHA, 2),
                MessageIds.referencePaymentReceived(SOURCE_MEPS, BMEPS_SHA, 2));
    }

    @Test
    void paymentFileProcessed() {
        // printf '%s' "processed|01930000-0000-7000-8000-000000000501|<MEPS_SHA>" | sha256sum
        assertEquals("dd5b32d55381e3bd47597d331126f7c6734e245ccbecbf1d11143cf6185d789a",
                MessageIds.paymentFileProcessed(SOURCE_MEPS, MEPS_SHA));
    }

    @Test
    void paymentFileRejected() {
        // printf '%s' "rejected|01930000-0000-7000-8000-000000000502|<BMEPS_SHA>|2027-01-15T09:12:44Z" | sha256sum
        assertEquals("74737c2f35b2365404568708363e6fcf224a206e54374f4e21943dc91a49474a",
                MessageIds.paymentFileRejected(SOURCE_BMEPS, BMEPS_SHA, Instant.parse("2027-01-15T09:12:44Z")));
        assertNotEquals(MessageIds.paymentFileRejected(SOURCE_BMEPS, BMEPS_SHA, Instant.parse("2027-01-15T09:12:44Z")),
                MessageIds.paymentFileRejected(SOURCE_BMEPS, BMEPS_SHA, Instant.parse("2027-01-15T09:12:45Z")));
    }

    @Test
    void paymentSourcePolledIsRandomUuid() {
        String a = MessageIds.paymentSourcePolled();
        String b = MessageIds.paymentSourcePolled();
        assertNotEquals(a, b);
        assertDoesNotThrow(() -> UUID.fromString(a));
        assertEquals(a, UUID.fromString(a).toString());
    }

    @Test
    void samplesCarryTheComputedIds() {
        assertEquals(MessageIds.referencePaymentReceived(SOURCE_MEPS, MEPS_SHA, 2),
                sample("reference-payment-received-meps.json").get("messageId").asText());
        assertEquals(MessageIds.referencePaymentReceived(SOURCE_BMEPS, BMEPS_SHA, 2),
                sample("reference-payment-received-bmeps.json").get("messageId").asText());
        assertEquals(MessageIds.paymentFileProcessed(SOURCE_MEPS, MEPS_SHA),
                sample("payment-file-processed.json").get("messageId").asText());
        assertEquals(MessageIds.paymentFileRejected(SOURCE_BMEPS, BMEPS_SHA, Instant.parse("2027-01-15T09:12:44Z")),
                sample("payment-file-rejected.json").get("messageId").asText());
    }
}
