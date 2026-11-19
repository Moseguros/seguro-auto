package mz.co.moseguros.paymentcontracts;

import static mz.co.moseguros.paymentcontracts.ContractSupport.MAPPER;
import static mz.co.moseguros.paymentcontracts.ContractSupport.sample;
import static mz.co.moseguros.paymentcontracts.ContractSupport.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Amostras contra os schemas, desserialização nos records e ida e volta (S5-06 §2). */
class ContractSamplesTest {

    static Stream<Arguments> samples() {
        return Stream.of(
                Arguments.of("reference-payment-received-meps.json", "reference-payment-received.v1.json",
                        ReferencePaymentReceived.class),
                Arguments.of("reference-payment-received-bmeps.json", "reference-payment-received.v1.json",
                        ReferencePaymentReceived.class),
                Arguments.of("payment-file-processed.json", "payment-file-processed.v1.json",
                        PaymentFileProcessed.class),
                Arguments.of("payment-file-rejected.json", "payment-file-rejected.v1.json",
                        PaymentFileRejected.class),
                Arguments.of("payment-source-polled.json", "payment-source-polled.v1.json",
                        PaymentSourcePolled.class),
                Arguments.of("payment-source-polled-error.json", "payment-source-polled.v1.json",
                        PaymentSourcePolled.class),
                Arguments.of("file-source-list.json", "file-source-list.v1.json", FileSourceList.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("samples")
    void sampleValidatesAndDeserializes(String sampleName, String schemaName, Class<?> type) throws Exception {
        JsonNode json = sample(sampleName);
        assertEquals(List.of(), validate(schemaName, json), "sample must match its schema");

        Object record = MAPPER.treeToValue(json, type);
        assertEquals(type, record.getClass());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("samples")
    void serializedRecordValidatesAndRoundTrips(String sampleName, String schemaName, Class<?> type)
            throws Exception {
        Object record = MAPPER.treeToValue(sample(sampleName), type);

        JsonNode serialized = MAPPER.valueToTree(record);
        assertEquals(List.of(), validate(schemaName, serialized), "serialized record must match its schema");
        assertEquals(record, MAPPER.treeToValue(serialized, type), "round trip");
        assertEquals(sample(sampleName), MAPPER.readTree(MAPPER.writeValueAsString(record)),
                "same JSON as the sample (no nulls written)");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("samples")
    void extraFieldFails(String sampleName, String schemaName, Class<?> type) {
        ObjectNode json = (ObjectNode) sample(sampleName);
        json.put("unexpected", "x");
        assertFalse(validate(schemaName, json).isEmpty());
    }

    @Test
    void currencyOtherThanMznFails() {
        ObjectNode json = (ObjectNode) sample("reference-payment-received-meps.json");
        ((ObjectNode) json.get("amount")).put("currency", "USD");
        assertFalse(validate("reference-payment-received.v1.json", json).isEmpty());
        assertThrows(Exception.class, () -> MAPPER.treeToValue(json, ReferencePaymentReceived.class));

        ObjectNode processed = (ObjectNode) sample("payment-file-processed.json");
        ((ObjectNode) processed.get("totalFees")).put("currency", "ZAR");
        assertFalse(validate("payment-file-processed.v1.json", processed).isEmpty());
    }

    @Test
    void extraFieldInsideMoneyFails() {
        ObjectNode json = (ObjectNode) sample("reference-payment-received-meps.json");
        ((ObjectNode) json.get("fee")).put("decimals", 2);
        assertFalse(validate("reference-payment-received.v1.json", json).isEmpty());
    }

    @Test
    void zeroPaymentAmountFails() {
        ObjectNode json = (ObjectNode) sample("reference-payment-received-bmeps.json");
        ((ObjectNode) json.get("amount")).put("amount", 0);
        assertFalse(validate("reference-payment-received.v1.json", json).isEmpty());
    }

    @Test
    void explicitNullsInOptionalFieldsAreAccepted() {
        ObjectNode json = (ObjectNode) sample("reference-payment-received-meps.json");
        json.putNull("fee");
        json.putNull("channel");
        json.putNull("bankTransactionId");
        assertEquals(List.of(), validate("reference-payment-received.v1.json", json));

        ObjectNode rejected = (ObjectNode) sample("payment-file-rejected.json");
        rejected.putNull("formatFileId");
        rejected.putNull("lineNo");
        assertEquals(List.of(), validate("payment-file-rejected.v1.json", rejected));
    }

    @Test
    void requiredFieldMissingFails() {
        ObjectNode json = (ObjectNode) sample("reference-payment-received-meps.json");
        json.remove("lineNo");
        assertFalse(validate("reference-payment-received.v1.json", json).isEmpty());
    }

    @Test
    void schemaVersionOtherThanOneFails() {
        ObjectNode json = (ObjectNode) sample("payment-file-processed.json");
        json.put("schemaVersion", 2);
        assertFalse(validate("payment-file-processed.v1.json", json).isEmpty());
    }

    @Test
    void pollOutcomeAndErrorCodeMustAgree() {
        ObjectNode errorWithoutCode = (ObjectNode) sample("payment-source-polled-error.json");
        errorWithoutCode.remove("errorCode");
        assertFalse(validate("payment-source-polled.v1.json", errorWithoutCode).isEmpty());

        ObjectNode okWithCode = (ObjectNode) sample("payment-source-polled.json");
        okWithCode.put("errorCode", "SFTP_UNREACHABLE");
        assertFalse(validate("payment-source-polled.v1.json", okWithCode).isEmpty());

        ObjectNode unknownCode = (ObjectNode) sample("payment-source-polled-error.json");
        unknownCode.put("errorCode", "SOMETHING_ELSE");
        assertFalse(validate("payment-source-polled.v1.json", unknownCode).isEmpty());
    }

    @Test
    void bmepsSourceNeedsCompanyAndProfile() {
        ObjectNode list = (ObjectNode) sample("file-source-list.json");
        ((ObjectNode) list.get("sources").get(1)).remove("bmepsProfileId");
        assertFalse(validate("file-source-list.v1.json", list).isEmpty());
    }

    @Test
    void movingSourceNeedsProcessedAndErrorDirs() {
        ObjectNode list = (ObjectNode) sample("file-source-list.json");
        ((ObjectNode) list.get("sources").get(0).get("sftp")).remove("errorDir");
        assertFalse(validate("file-source-list.v1.json", list).isEmpty());

        ObjectNode parent = (ObjectNode) sample("file-source-list.json");
        ((ObjectNode) parent.get("sources").get(0).get("sftp")).put("processedDir", "../outside");
        assertFalse(validate("file-source-list.v1.json", parent).isEmpty());
    }

    @Test
    void fileSourceListNeverCarriesSecretValues() {
        ObjectNode list = (ObjectNode) sample("file-source-list.json");
        ((ObjectNode) list.get("sources").get(0).get("secretRefs")).put("sftp.password", "demo-sftp-pass");
        assertFalse(validate("file-source-list.v1.json", list).isEmpty(), "only vault references are allowed");
    }

    @Test
    void recordBuiltInCodeSerializesWithoutNullsAndValidates() {
        UUID insurerId = UUID.fromString("01930000-0000-7000-8000-000000000001");
        UUID sourceId = UUID.fromString("01930000-0000-7000-8000-000000000501");
        String fileSha = "1b96115a3a8e399da57a6bdb3e7371a8ca4bbada5aca146d1e2077507217256e";
        var message = new ReferencePaymentReceived(
                MessageIds.referencePaymentReceived(sourceId, fileSha, 3), MessageHeaders.SCHEMA_VERSION,
                insurerId, sourceId, FileSourceDescriptor.FORMAT_MEPS, "90002", "00604600879",
                MoneyAmount.mzn(300000), null, Instant.parse("2012-11-22T09:03:00Z"), null, null,
                "exemplo-ficheiro-meps.txt", fileSha, "201211230", 3);

        JsonNode json = MAPPER.valueToTree(message);
        assertEquals(List.of(), validate("reference-payment-received.v1.json", json));
        assertFalse(json.has("fee"));
        assertFalse(json.has("channel"));
        assertEquals("2012-11-22T09:03:00Z", json.get("paidAt").asText());

        var rejected = new PaymentFileRejected(
                MessageIds.paymentFileRejected(sourceId, fileSha, Instant.parse("2027-01-15T09:12:44Z")),
                MessageHeaders.SCHEMA_VERSION, insurerId, sourceId, FileSourceDescriptor.FORMAT_MEPS,
                "garbage.bin", fileSha, null, FileRejectionReason.ENCODING_ERROR, "not ISO-8859-1 text", null,
                Instant.parse("2027-01-15T09:12:44Z"));
        assertEquals(List.of(), validate("payment-file-rejected.v1.json", MAPPER.valueToTree(rejected)));

        var list = new FileSourceList(List.of(new FileSourceDescriptor(sourceId, insurerId, 1, false, "90002",
                FileSourceDescriptor.FORMAT_MEPS, null, null,
                new FileSourceDescriptor.Sftp("sftp", 22, "demo", FileSourceDescriptor.Sftp.AUTH_PASSWORD,
                        "inbound", false, null, null, FileSourceDescriptor.Sftp.DEFAULT_FILE_PATTERN, null),
                Map.of("sftp.password",
                        "secret/insurers/" + insurerId + "/payments/BANK_REFERENCE/sftp.password"))));
        assertEquals(List.of(), validate("file-source-list.v1.json", MAPPER.valueToTree(list)));
    }

    @Test
    void moneyAmountRejectsOtherCurrenciesAndNegatives() {
        assertThrows(IllegalArgumentException.class, () -> new MoneyAmount(100, "USD"));
        assertThrows(IllegalArgumentException.class, () -> MoneyAmount.mzn(-1));
        assertThrows(NullPointerException.class, () -> new MoneyAmount(100, null));
        assertEquals(new MoneyAmount(0, "MZN"), MoneyAmount.mzn(0));
    }

    @Test
    void fileSourceListWithoutSourcesIsEmpty() {
        assertTrue(new FileSourceList(null).sources().isEmpty());
        assertNull(MAPPER.convertValue(Map.of("sources", List.of()), FileSourceList.class).sources().stream()
                .findFirst().orElse(null));
    }
}
