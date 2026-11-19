package mz.co.moseguros.paymentworker.processing;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentcontracts.FileSourceList;
import mz.co.moseguros.paymentcontracts.MessageIds;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentworker.WorkerITSupport;
import mz.co.moseguros.paymentworker.metrics.WorkerMetrics;
import mz.co.moseguros.paymentworker.sftp.SftpSessions;
import mz.co.moseguros.paymentworker.sources.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Leitura de ficheiros de ponta a ponta no worker (S5-09 §5 a §7), com o leitor {@code TEST_STUB}:
 * publicação com confirms, mudança de pasta, deduplicação por conteúdo e pelo identificador do ficheiro,
 * SFTP só de leitura, ficheiros a meio de envio, agendamento, sequência, rejeições, falha de publicação a
 * meio e erros da fonte.
 */
@ExtendWith(OutputCaptureExtension.class)
class FileProcessingIT extends WorkerITSupport {

    @Autowired
    SourcePoller poller;

    @Autowired
    SourceRegistry registry;

    @Autowired
    WorkerMetrics metrics;

    @Autowired
    SftpSessions sftpSessions;

    @Autowired
    FileProcessor processor;

    static String sha(String content) {
        return MessageIds.sha256Hex(content.getBytes(StandardCharsets.ISO_8859_1));
    }

    static String resource(String path) {
        try (var in = FileProcessingIT.class.getClassLoader().getResourceAsStream(path)) {
            return new String(in.readAllBytes(), StandardCharsets.ISO_8859_1);
        } catch (java.io.IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static String dir() {
        return "t" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void fileWithTwoRecordsIsPublishedAndMovedToProcessed() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        String content = resource("files/two-records.txt");
        put(dir + "/inbound/a.txt", content);

        PollResult result = poller.poll(source);

        assertThat(result.ok()).isTrue();
        assertThat(result.filesFound()).isEqualTo(1);
        assertThat(result.filesProcessed()).isEqualTo(1);
        List<Received> messages = drain();
        List<Received> payments = ofType(messages, "ReferencePaymentReceived");
        assertThat(payments).hasSize(2);
        String sha = sha(content);
        Received first = payments.get(0);
        assertThat(first.messageId()).isEqualTo(MessageIds.referencePaymentReceived(source.sourceId(), sha, 2));
        assertThat(first.routingKey()).isEqualTo(PaymentsTopology.referencePaidKey(source.insurerId()));
        assertThat(first.schemaVersion()).isEqualTo(1);
        assertThat(first.body().path("messageId").asText()).isEqualTo(first.messageId());
        assertThat(first.body().path("schemaVersion").asInt()).isEqualTo(1);
        assertThat(first.body().path("insurerId").asText()).isEqualTo(source.insurerId().toString());
        assertThat(first.body().path("sourceId").asText()).isEqualTo(source.sourceId().toString());
        assertThat(first.body().path("fileFormat").asText()).isEqualTo("TEST_STUB");
        assertThat(first.body().path("entity").asText()).isEqualTo(ENTITY);
        assertThat(first.body().path("reference").asText()).isEqualTo("00548781108");
        assertThat(first.body().path("amount").path("amount").asLong()).isEqualTo(300_000);
        assertThat(first.body().path("amount").path("currency").asText()).isEqualTo("MZN");
        assertThat(first.body().path("paidAt").asText()).isEqualTo("2026-11-18T08:30:00Z"); // 10:30 em Maputo
        assertThat(first.body().path("fileName").asText()).isEqualTo("a.txt");
        assertThat(first.body().path("fileSha256").asText()).isEqualTo(sha);
        assertThat(first.body().path("formatFileId").asText()).isEqualTo("1");
        assertThat(first.body().path("lineNo").asInt()).isEqualTo(2);
        assertThat(first.body().has("fee")).isFalse();
        Received second = payments.get(1);
        assertThat(second.messageId()).isEqualTo(MessageIds.referencePaymentReceived(source.sourceId(), sha, 3));
        assertThat(second.body().path("reference").asText()).isEqualTo("00548791108");
        assertThat(second.body().path("amount").path("amount").asLong()).isEqualTo(275_000);

        List<Received> processed = ofType(messages, "PaymentFileProcessed");
        assertThat(processed).hasSize(1);
        assertThat(processed.get(0).messageId()).isEqualTo(MessageIds.paymentFileProcessed(source.sourceId(), sha));
        assertThat(processed.get(0).body().path("recordCount").asInt()).isEqualTo(2);
        assertThat(processed.get(0).body().path("totalAmount").path("amount").asLong()).isEqualTo(575_000);
        assertThat(processed.get(0).routingKey()).isEqualTo(PaymentsTopology.fileProcessedKey(source.insurerId()));

        List<Received> polled = ofType(messages, "PaymentSourcePolled");
        assertThat(polled).hasSize(1);
        assertThat(polled.get(0).body().path("outcome").asText()).isEqualTo("OK");
        assertThat(polled.get(0).body().path("filesFound").asInt()).isEqualTo(1);
        assertThat(polled.get(0).body().path("filesProcessed").asInt()).isEqualTo(1);
        assertThat(polled.get(0).body().path("filesRejected").asInt()).isZero();
        assertThat(polled.get(0).body().has("errorCode")).isFalse();

        assertThat(ls(dir + "/inbound")).isEmpty();
        assertThat(ls(dir + "/processed")).containsExactly("a.txt");
        assertThat(fileStatus(source.sourceId(), sha)).isEqualTo("DONE");
        Map<String, Object> row = jdbc.queryForMap(
                "select * from payment_worker.processed_file where sha256 = ? and source_id = ?", sha, source.sourceId());
        assertThat(row).containsEntry("record_count", 2).containsEntry("total_amount", 575_000L)
                .containsEntry("format_file_id", "1").containsEntry("created_by", "payment-file-worker");
        assertThat(jdbc.queryForObject("select count(*) from payment_worker.file_record r join "
                + "payment_worker.processed_file f on f.id = r.file_id where f.sha256 = ? and f.source_id = ? "
                + "and r.published_at is not null", Integer.class, sha, source.sourceId())).isEqualTo(2);
    }

    @Test
    void theSameFileAgainWithTheSameOrAnotherNamePublishesNothing() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        String content = stubFile("7", null, 100_000);
        put(dir + "/inbound/a.txt", content);
        poller.poll(source);
        drain();

        put(dir + "/inbound/a.txt", content);
        PollResult again = poller.poll(source);
        assertThat(again.ok()).isTrue();
        assertThat(ofType(drain(), "ReferencePaymentReceived")).isEmpty();
        assertThat(ls(dir + "/inbound")).isEmpty();
        assertThat(ls(dir + "/processed")).hasSize(2).contains("a.txt")
                .anyMatch(n -> n.matches("a\\.txt\\.\\d{14}"));

        put(dir + "/inbound/outro-nome.txt", content);
        poller.poll(source);
        List<Received> messages = drain();
        assertThat(ofType(messages, "ReferencePaymentReceived")).isEmpty();
        assertThat(ofType(messages, "PaymentFileProcessed")).isEmpty();
        assertThat(ls(dir + "/inbound")).isEmpty();
        assertThat(ls(dir + "/processed")).contains("outro-nome.txt");
    }

    @Test
    void otherContentWithTheSameFileIdIsRejected(CapturedOutput output) {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        put(dir + "/inbound/a.txt", stubFile("5", null, 100_000));
        poller.poll(source);
        drain();

        String other = stubFile("5", null, 200_000, 300_000);
        put(dir + "/inbound/b.txt", other);
        PollResult result = poller.poll(source);

        assertThat(result.ok()).isTrue();
        assertThat(result.filesRejected()).isEqualTo(1);
        List<Received> messages = drain();
        assertThat(ofType(messages, "ReferencePaymentReceived")).isEmpty();
        List<Received> rejected = ofType(messages, "PaymentFileRejected");
        assertThat(rejected).hasSize(1);
        assertThat(rejected.get(0).body().path("reason").asText()).isEqualTo("DUPLICATE_FILE_ID");
        assertThat(rejected.get(0).body().path("formatFileId").asText()).isEqualTo("5");
        assertThat(rejected.get(0).routingKey()).isEqualTo(PaymentsTopology.fileRejectedKey(source.insurerId()));
        assertThat(ls(dir + "/error")).containsExactly("b.txt");
        assertThat(fileStatus(source.sourceId(), sha(other))).isEqualTo("ERROR");
        assertThat(output).contains("alert=PAYMENT_FILE_DUPLICATE_ID");

        // o mesmo ficheiro rejeitado outra vez: nada de novo
        put(dir + "/inbound/b.txt", other);
        poller.poll(source);
        assertThat(ofType(drain(), "PaymentFileRejected")).isEmpty();
    }

    @Test
    void readOnlySourceKeepsTheFileAndNeverDownloadsItAgain() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, false);
        put(dir + "/inbound/a.txt", stubFile("1", null, 100_000, 200_000));

        poller.poll(source);
        poller.poll(source);
        poller.poll(source);

        List<Received> messages = drain();
        assertThat(ofType(messages, "ReferencePaymentReceived")).hasSize(2);
        assertThat(ofType(messages, "PaymentFileProcessed")).hasSize(1);
        assertThat(ofType(messages, "PaymentSourcePolled")).hasSize(3);
        assertThat(ls(dir + "/inbound")).containsExactly("a.txt");
        assertThat(serverDownloads(dir + "/inbound/a.txt")).isEqualTo(1);
        assertThat(metrics.downloads(source.sourceId())).isEqualTo(1.0);
    }

    @Test
    void partialFilesAreIgnoredUntilRenamed() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        put(dir + "/inbound/x.txt.part", stubFile("1", null, 100_000));
        put(dir + "/inbound/.y.txt", stubFile("2", null, 100_000));

        PollResult first = poller.poll(source);
        assertThat(first.filesFound()).isZero();
        assertThat(ofType(drain(), "ReferencePaymentReceived")).isEmpty();
        assertThat(ls(dir + "/inbound")).contains("x.txt.part");

        exec("mv", "/home/demo/" + dir + "/inbound/x.txt.part", "/home/demo/" + dir + "/inbound/x.txt");
        PollResult second = poller.poll(source);
        assertThat(second.filesProcessed()).isEqualTo(1);
        assertThat(ofType(drain(), "ReferencePaymentReceived")).hasSize(1);
        assertThat(ls(dir + "/processed")).containsExactly("x.txt");
    }

    @Test
    void aNewFileIsReadWithinThePollIntervalPlusFiveSeconds() throws Exception {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        String sources = new ObjectMapper().writeValueAsString(new FileSourceList(List.of(source)));
        BACKEND.stubFor(get(urlEqualTo("/internal/v1/payment-file-sources")).willReturn(okJson(sources)));
        try {
            String first = stubFile("1", null, 100_000);
            put(dir + "/inbound/a.txt", first);
            assertThat(registry.refresh()).isTrue();
            await().dontCatchUncaughtExceptions().atMost(Duration.ofSeconds(20)).until(() -> "DONE".equals(statusOrNull(source, sha(first))));

            String second = stubFile("2", "1", 200_000);
            Instant put = Instant.now();
            put(dir + "/inbound/b.txt", second);
            await().dontCatchUncaughtExceptions().atMost(Duration.ofSeconds(15)).until(() -> "DONE".equals(statusOrNull(source, sha(second))));
            assertThat(Duration.between(put, Instant.now())).isLessThan(Duration.ofSeconds(2 + 5));
        } finally {
            BACKEND.stubFor(get(urlEqualTo("/internal/v1/payment-file-sources")).willReturn(okJson("{\"sources\":[]}")));
            registry.refresh();
        }
        assertThat(registry.descriptor(source.sourceId())).isEmpty();
    }

    String statusOrNull(FileSourceDescriptor source, String sha) {
        List<String> s = jdbc.queryForList(
                "select status from payment_worker.processed_file where source_id = ? and sha256 = ?", String.class,
                source.sourceId(), sha);
        return s.isEmpty() ? null : s.get(0);
    }

    @Test
    void aMissingFileInTheSequenceOnlyWarns(CapturedOutput output) {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        put(dir + "/inbound/a.txt", stubFile("1", null, 100_000));
        poller.poll(source);
        assertThat(output).doesNotContain("alert=PAYMENT_FILE_SEQUENCE_GAP");

        String third = stubFile("3", "1", 100_000);
        put(dir + "/inbound/c.txt", third);
        PollResult result = poller.poll(source);

        assertThat(result.filesProcessed()).isEqualTo(1);
        assertThat(fileStatus(source.sourceId(), sha(third))).isEqualTo("DONE");
        assertThat(output).contains("alert=PAYMENT_FILE_SEQUENCE_GAP");
    }

    @Test
    void wrongTrailerTotalIsRejected() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        String content = resource("files/wrong-trailer-total.txt");
        put(dir + "/inbound/a.txt", content);

        PollResult result = poller.poll(source);

        List<Received> messages = drain();
        assertThat(ofType(messages, "ReferencePaymentReceived")).isEmpty();
        List<Received> rejected = ofType(messages, "PaymentFileRejected");
        assertThat(rejected).hasSize(1);
        assertThat(rejected.get(0).body().path("reason").asText()).isEqualTo("TRAILER_TOTAL_MISMATCH");
        assertThat(rejected.get(0).messageId()).isEqualTo(rejected.get(0).body().path("messageId").asText());
        assertThat(ofType(messages, "PaymentSourcePolled").get(0).body().path("outcome").asText()).isEqualTo("OK");
        assertThat(result.ok()).isTrue();
        assertThat(result.filesRejected()).isEqualTo(1);
        assertThat(ls(dir + "/error")).containsExactly("a.txt");
        assertThat(fileStatus(source.sourceId(), sha(content))).isEqualTo("ERROR");
        assertThat(jdbc.queryForObject("select error_code from payment_worker.processed_file where sha256 = ? and source_id = ?",
                String.class, sha(content), source.sourceId())).isEqualTo("TRAILER_TOTAL_MISMATCH");
    }

    @Test
    void otherEntityIsRejected() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        put(dir + "/inbound/a.txt", stubFile("90003", "1", null, 1, 100_000, 100_000));

        poller.poll(source);

        List<Received> rejected = ofType(drain(), "PaymentFileRejected");
        assertThat(rejected).hasSize(1);
        assertThat(rejected.get(0).body().path("reason").asText()).isEqualTo("ENTITY_MISMATCH");
        assertThat(ls(dir + "/error")).containsExactly("a.txt");
    }

    @Test
    void emptyFileWithZeroTrailerIsDone() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        String content = stubFile("1", null);
        put(dir + "/inbound/a.txt", content);

        poller.poll(source);

        List<Received> messages = drain();
        assertThat(ofType(messages, "ReferencePaymentReceived")).isEmpty();
        List<Received> processed = ofType(messages, "PaymentFileProcessed");
        assertThat(processed).hasSize(1);
        assertThat(processed.get(0).body().path("recordCount").asInt()).isZero();
        assertThat(fileStatus(source.sourceId(), sha(content))).isEqualTo("DONE");
    }

    @Test
    void fileTooLargeIsRejectedWithoutDownloading() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        put(dir + "/inbound/big.txt", stubFile("1", null, 100_000));
        long max = (long) ReflectionTestUtils.getField(processor, "maxFileSize");
        ReflectionTestUtils.setField(processor, "maxFileSize", 10L);
        try {
            poller.poll(source);
        } finally {
            ReflectionTestUtils.setField(processor, "maxFileSize", max);
        }
        List<Received> rejected = ofType(drain(), "PaymentFileRejected");
        assertThat(rejected).hasSize(1);
        assertThat(rejected.get(0).body().path("reason").asText()).isEqualTo("FILE_TOO_LARGE");
        assertThat(ls(dir + "/error")).containsExactly("big.txt");
        assertThat(metrics.downloads(source.sourceId())).isZero();
    }

    @Test
    void publishFailureInTheMiddleResumesOnTheNextPoll() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        String content = stubFile("1", null, 100_000, 200_000);
        String sha = sha(content);
        put(dir + "/inbound/a.txt", content);
        rabbit.nackReferencePayment(2);

        PollResult failed = poller.poll(source);

        assertThat(failed.ok()).isFalse();
        assertThat(failed.errorCode()).hasToString("PUBLISH_FAILED");
        assertThat(fileStatus(source.sourceId(), sha)).isEqualTo("PARSED");
        List<Map<String, Object>> rows = jdbc.queryForList("""
                select r.line_no, r.published_at from payment_worker.file_record r
                  join payment_worker.processed_file f on f.id = r.file_id
                 where f.sha256 = ? and f.source_id = ? order by r.line_no""", sha, source.sourceId());
        assertThat(rows.get(0).get("published_at")).isNotNull();
        assertThat(rows.get(1).get("published_at")).isNull();
        List<Received> firstMessages = drain();
        assertThat(ofType(firstMessages, "ReferencePaymentReceived")).hasSize(1);
        assertThat(ofType(firstMessages, "PaymentSourcePolled").get(0).body().path("errorCode").asText())
                .isEqualTo("PUBLISH_FAILED");
        assertThat(ls(dir + "/inbound")).containsExactly("a.txt");

        PollResult resumed = poller.poll(source);

        assertThat(resumed.ok()).isTrue();
        List<Received> payments = ofType(drain(), "ReferencePaymentReceived");
        assertThat(payments).hasSize(1);
        assertThat(payments.get(0).body().path("lineNo").asInt()).isEqualTo(3);
        assertThat(fileStatus(source.sourceId(), sha)).isEqualTo("DONE");
        assertThat(ls(dir + "/processed")).containsExactly("a.txt");
    }

    @Test
    void wrongSftpPasswordIsAuthFailed() {
        FileSourceDescriptor source = source(dir(), true, null, "palavra-passe-errada");

        PollResult result = poller.poll(source);

        assertThat(result.ok()).isFalse();
        assertThat(result.errorCode()).hasToString("SFTP_AUTH_FAILED");
        List<Received> polled = ofType(drain(), "PaymentSourcePolled");
        assertThat(polled).hasSize(1);
        assertThat(polled.get(0).body().path("outcome").asText()).isEqualTo("ERROR");
        assertThat(polled.get(0).body().path("errorCode").asText()).isEqualTo("SFTP_AUTH_FAILED");
    }

    @Test
    void wrongHostKeyFingerprintIsRefused() {
        ReflectionTestUtils.setField(sftpSessions, "allowUnknownHostKeys", false);
        try {
            FileSourceDescriptor wrong = source(dir(), true, "SHA256:47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU",
                    SFTP_PASSWORD);
            assertThat(poller.poll(wrong).errorCode()).hasToString("SFTP_HOST_KEY_MISMATCH");
            FileSourceDescriptor none = source(dir(), true, null, SFTP_PASSWORD);
            assertThat(poller.poll(none).errorCode()).hasToString("SFTP_HOST_KEY_MISMATCH");
        } finally {
            ReflectionTestUtils.setField(sftpSessions, "allowUnknownHostKeys", true);
        }
        List<Received> polled = ofType(drain(), "PaymentSourcePolled");
        assertThat(polled).extracting(r -> r.body().path("errorCode").asText())
                .containsOnly("SFTP_HOST_KEY_MISMATCH");
    }

    @Test
    void missingSecretAndUnsupportedFormatAreSourceErrors() {
        FileSourceDescriptor source = source(dir(), true);
        secrets.remove(source.secretRefs().get("sftp.password"));
        assertThat(poller.poll(source).errorCode()).hasToString("SECRET_NOT_FOUND");

        FileSourceDescriptor meps = new FileSourceDescriptor(UUID.randomUUID(), UUID.randomUUID(), 1, true, ENTITY,
                "MEPS", null, null, source.sftp(), source.secretRefs());
        assertThat(poller.poll(meps).errorCode()).hasToString("UNSUPPORTED_FORMAT");
    }

    @Test
    void missingInboundDirectoryIsReported() {
        FileSourceDescriptor source = source(dir(), true);
        FileSourceDescriptor.Sftp s = source.sftp();
        FileSourceDescriptor missing = new FileSourceDescriptor(source.sourceId(), source.insurerId(), 1, true, ENTITY,
                source.fileFormat(), null, null, new FileSourceDescriptor.Sftp(s.host(), s.port(), s.username(),
                s.authType(), "nao-existe/inbound", true, s.processedDir(), s.errorDir(), s.filePattern(), null),
                source.secretRefs());
        assertThat(poller.poll(missing).errorCode()).hasToString("SFTP_DIRECTORY_NOT_FOUND");
    }

    @Test
    void logsOfACompletePollHaveNoSecretsTokensOrLines(CapturedOutput output) {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        String content = stubFile("1", null, 100_000, 200_000);
        put(dir + "/inbound/a.txt", content);

        registry.refresh(); // usa o token de serviço
        poller.poll(source);
        drain();

        String logs = output.getAll();
        assertThat(logs).contains("a.txt").contains(source.sourceId().toString());
        assertThat(logs).doesNotContain(SFTP_PASSWORD).doesNotContain(SERVICE_TOKEN)
                .doesNotContain("test-worker-secret-only-for-tests-0001");
        for (String line : content.split("\r\n")) {
            assertThat(logs).doesNotContain(line);
        }
    }
}
