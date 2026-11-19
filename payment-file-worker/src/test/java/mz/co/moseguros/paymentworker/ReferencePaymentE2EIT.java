package mz.co.moseguros.paymentworker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import mz.co.moseguros.paymentcontracts.MessageIds;
import mz.co.moseguros.paymentworker.processing.PollResult;
import mz.co.moseguros.paymentworker.processing.SourcePoller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * De ponta a ponta no worker com os leitores reais (S5-08 §7): os exemplos oficiais MEPS e BMEPS postos no SFTP
 * são publicados com os valores do ficheiro, vão para {@code processed} e não voltam a ser processados com outro
 * nome; um BMEPS de outra empresa vai para {@code error}. Testcontainers SFTP, RabbitMQ e Postgres; backend
 * simulado com WireMock ({@link WorkerITSupport}).
 */
class ReferencePaymentE2EIT extends WorkerITSupport {

    @Autowired
    SourcePoller poller;

    static byte[] resource(String name) {
        try (InputStream in = ReferencePaymentE2EIT.class.getResourceAsStream("/interbancos/" + name)) {
            assertThat(in).as(name).isNotNull();
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static String dir() {
        return "e2e" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void officialMepsExampleIsPublishedOnceWithTheValuesOfTheFile() {
        String dir = dir();
        FileSourceDescriptor source = source(dir, true);
        byte[] content = resource("exemplo-ficheiro-meps.txt");
        String sha = MessageIds.sha256Hex(content);
        put(dir + "/inbound/MEPS_90002_201211230.txt", content);

        PollResult result = poller.poll(source);

        assertThat(result.ok()).isTrue();
        assertThat(result.filesProcessed()).isEqualTo(1);
        List<Received> messages = drain();
        List<Received> payments = ofType(messages, "ReferencePaymentReceived");
        assertThat(payments).hasSize(11);
        assertThat(payments).allSatisfy(p -> {
            assertThat(p.body().path("fileFormat").asText()).isEqualTo("MEPS");
            assertThat(p.body().path("entity").asText()).isEqualTo("90002");
            assertThat(p.body().path("formatFileId").asText()).isEqualTo("201211230");
            assertThat(p.body().path("fee").path("amount").asLong()).isEqualTo(1_000);
            assertThat(p.body().has("bankTransactionId")).isFalse();
        });
        Received first = payments.get(0);
        assertThat(first.body().path("lineNo").asInt()).isEqualTo(2);
        assertThat(first.body().path("reference").asText()).isEqualTo("00548781108");
        assertThat(first.body().path("amount").path("amount").asLong()).isEqualTo(300_000);
        assertThat(first.body().path("paidAt").asText()).isEqualTo("2012-11-22T08:34:00Z"); // 10:34 em Maputo
        assertThat(payments.stream().mapToLong(p -> p.body().path("amount").path("amount").asLong()).sum())
                .isEqualTo(5_132_000);

        // a mesma referência paga duas vezes: duas mensagens com messageId diferentes
        List<Received> twice = payments.stream()
                .filter(p -> p.body().path("reference").asText().equals("00620400869")).toList();
        assertThat(twice).hasSize(2);
        assertThat(twice.get(0).messageId()).isNotEqualTo(twice.get(1).messageId());
        assertThat(twice).extracting(p -> p.body().path("paidAt").asText())
                .containsExactly("2012-11-22T07:26:00Z", "2012-11-22T07:29:00Z");

        List<Received> processed = ofType(messages, "PaymentFileProcessed");
        assertThat(processed).hasSize(1);
        assertThat(processed.get(0).body().path("formatFileId").asText()).isEqualTo("201211230");
        assertThat(processed.get(0).body().path("recordCount").asInt()).isEqualTo(11);
        assertThat(processed.get(0).body().path("totalAmount").path("amount").asLong()).isEqualTo(5_132_000);
        assertThat(ls(dir + "/processed")).containsExactly("MEPS_90002_201211230.txt");
        assertThat(ls(dir + "/inbound")).isEmpty();
        assertThat(fileStatus(source.sourceId(), sha)).isEqualTo("DONE");

        // o mesmo conteúdo com outro nome: nada de novo
        put(dir + "/inbound/MEPS_90002_201211230.resend1.txt", content);
        PollResult again = poller.poll(source);
        assertThat(again.ok()).isTrue();
        List<Received> afterResend = drain();
        assertThat(ofType(afterResend, "ReferencePaymentReceived")).isEmpty();
        assertThat(ofType(afterResend, "PaymentFileProcessed")).isEmpty();
        assertThat(ofType(afterResend, "PaymentFileRejected")).isEmpty();
        assertThat(ls(dir + "/processed")).contains("MEPS_90002_201211230.resend1.txt");
    }

    @Test
    void officialBmepsExampleIsPublishedWithTheEntityOfTheSource() {
        String dir = dir();
        FileSourceDescriptor source = bmepsSource(dir, true);
        byte[] content = resource("exemplo-ficheiro-bmeps.txt");
        put(dir + "/inbound/BMEPS_out80002508121122143100", content);

        PollResult result = poller.poll(source);

        assertThat(result.ok()).isTrue();
        List<Received> messages = drain();
        List<Received> payments = ofType(messages, "ReferencePaymentReceived");
        assertThat(payments).hasSize(14);
        assertThat(payments).allSatisfy(p -> {
            assertThat(p.body().path("fileFormat").asText()).isEqualTo("BMEPS");
            assertThat(p.body().path("entity").asText()).isEqualTo("90090");
            assertThat(p.body().path("formatFileId").asText()).isEqualTo("800-02-00001");
            assertThat(p.body().has("channel")).isFalse();
        });
        Received tenMeticais = payments.stream()
                .filter(p -> p.body().path("reference").asText().equals("02295721110")).findFirst().orElseThrow();
        assertThat(tenMeticais.body().path("paidAt").asText()).isEqualTo("2012-11-22T12:30:44Z");
        assertThat(tenMeticais.body().path("fee").path("amount").asLong()).isEqualTo(1_000);
        assertThat(payments.stream().mapToLong(p -> p.body().path("amount").path("amount").asLong()).sum())
                .isEqualTo(8_074_000);
        List<Received> processed = ofType(messages, "PaymentFileProcessed");
        assertThat(processed).hasSize(1);
        assertThat(processed.get(0).body().path("formatFileId").asText()).isEqualTo("800-02-00001");
        assertThat(ls(dir + "/processed")).containsExactly("BMEPS_out80002508121122143100");

        put(dir + "/inbound/BMEPS_out80002508121122143100.resend1", content);
        poller.poll(source);
        assertThat(ofType(drain(), "ReferencePaymentReceived")).isEmpty();
    }

    @Test
    void bmepsOfAnotherCompanyGoesToErrorWithEntityMismatch() {
        String dir = dir();
        FileSourceDescriptor source = bmepsSource(dir, true);
        put(dir + "/inbound/BMEPS_out80102508121122143100", resource("bmeps-err-company.txt"));

        PollResult result = poller.poll(source);

        assertThat(result.ok()).isTrue();
        assertThat(result.filesRejected()).isEqualTo(1);
        List<Received> messages = drain();
        assertThat(ofType(messages, "ReferencePaymentReceived")).isEmpty();
        List<Received> rejected = ofType(messages, "PaymentFileRejected");
        assertThat(rejected).hasSize(1);
        assertThat(rejected.get(0).body().path("reason").asText()).isEqualTo("ENTITY_MISMATCH");
        assertThat(ls(dir + "/error")).containsExactly("BMEPS_out80102508121122143100");
    }
}
