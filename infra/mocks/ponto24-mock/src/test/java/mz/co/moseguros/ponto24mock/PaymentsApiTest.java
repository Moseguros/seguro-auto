package mz.co.moseguros.ponto24mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import mz.co.moseguros.ponto24mock.MockProperties.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** API do simulador (S5-08 §6.2 e §6.3), com o envio SFTP simulado e o relógio fixo em 19/11/2026. */
@SpringBootTest(properties = {
    "ponto24mock.settlement-cron=-",
    "ponto24mock.sources=90002=MEPS@sftp://demo@sftp:22/inbound,90090=BMEPS:800:02@sftp://demob@sftp:22/inbound",
    "PONTO24_MOCK_SFTP_PASSWORD_90002=demo-pass",
    "PONTO24_MOCK_SFTP_PASSWORD_90090=demob-pass"
})
@AutoConfigureMockMvc
@Import(PaymentsApiTest.FixedClock.class)
class PaymentsApiTest {

    static final Instant NOW = Instant.parse("2026-11-19T10:00:00Z");

    @TestConfiguration
    static class FixedClock {
        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(NOW, ZoneOffset.UTC);
        }
    }

    @Autowired
    MockMvc mvc;

    @MockBean
    SftpDelivery delivery;

    static final String REF = Ponto24CheckDigits.reference("90002", 123, 11, 299_900);
    static final String BMEPS_REF = Ponto24CheckDigits.reference("90090", 77, 11, 572_000);

    @BeforeEach
    void clean() throws Exception {
        reset(delivery);
        mvc.perform(post("/__mock/reset")).andExpect(status().isNoContent());
    }

    ResultActions pay(String entity, String reference, long amount, String query) throws Exception {
        return mvc.perform(post("/payments" + query).contentType(MediaType.APPLICATION_JSON)
                .content("{\"entity\":\"" + entity + "\",\"reference\":\"" + reference + "\",\"amountCentavos\":"
                        + amount + "}"));
    }

    @Test
    void validReferenceIsAccepted() throws Exception {
        pay("90002", REF, 299_900, "").andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("PENDING_SETTLEMENT"));
        mvc.perform(get("/payments")).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].paidAt").value(NOW.toString()));
    }

    @Test
    void wrongCheckDigitsAreRefused() throws Exception {
        String wrong = REF.substring(0, 9) + (REF.endsWith("00") ? "01" : "00");
        pay("90002", wrong, 299_900, "").andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INVALID_REFERENCE"));
        pay("90002", "1234", 299_900, "").andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INVALID_REFERENCE"));
    }

    @Test
    void otherAmountThanTheReferenceIsRefused() throws Exception {
        pay("90002", REF, 300_000, "").andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INVALID_REFERENCE"));
    }

    @Test
    void forceAcceptsWithoutValidating() throws Exception {
        pay("90002", REF, 300_000, "?force=true").andExpect(status().isCreated());
        pay("90002", "00000000000", 100, "?force=true").andExpect(status().isCreated());
        mvc.perform(get("/payments")).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].forced").value(true));
    }

    @Test
    void unknownEntityIs404() throws Exception {
        pay("90003", REF, 299_900, "").andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("UNKNOWN_ENTITY"));
    }

    @Test
    void zeroAmountIsRefused() throws Exception {
        pay("90002", REF, 0, "").andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("INVALID_AMOUNT"));
        pay("90002", REF, 0, "?force=true").andExpect(status().isUnprocessableEntity());
    }

    @Test
    void settlementSendsOneMepsFilePerEntityAndSettlesThePayments() throws Exception {
        pay("90002", REF, 299_900, "").andExpect(status().isCreated());
        pay("90090", BMEPS_REF, 572_000, "").andExpect(status().isCreated());

        mvc.perform(post("/settlements/run")).andExpect(status().isOk())
                .andExpect(jsonPath("$.files", hasSize(2)))
                .andExpect(jsonPath("$.files[0].name").value("MEPS_90002_202611191.txt"))
                .andExpect(jsonPath("$.files[0].formatFileId").value("202611191"))
                .andExpect(jsonPath("$.files[1].name").value("BMEPS_out80002508261119120000"))
                .andExpect(jsonPath("$.files[1].formatFileId").value("800-02-00001"));

        ArgumentCaptor<byte[]> content = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<Source> source = ArgumentCaptor.forClass(Source.class);
        verify(delivery, org.mockito.Mockito.times(2)).deliver(source.capture(), anyString(), anyString(),
                content.capture());
        assertThat(source.getAllValues()).extracting(Source::username).containsExactly("demo", "demob");
        String meps = new String(content.getAllValues().get(0), StandardCharsets.ISO_8859_1);
        assertThat(meps).startsWith("0MEPS0000000800090002202611191000000000" + "90002943XX0000");
        assertThat(meps).contains(REF).contains("0000000299900000001000");
        String bmeps = new String(content.getAllValues().get(1), StandardCharsets.ISO_8859_1);
        assertThat(bmeps).startsWith("0800021911202600001\r\n1" + BMEPS_REF + "0000000000572000" + "0000000000000500"
                + "19112026120000");
        mvc.perform(get("/payments")).andExpect(jsonPath("$[0].status").value("SETTLED"))
                .andExpect(jsonPath("$[1].status").value("SETTLED"));

        // nada pendente: nenhum ficheiro
        mvc.perform(post("/settlements/run")).andExpect(jsonPath("$.files", hasSize(0)));

        // o segundo ficheiro MEPS do dia aponta para o primeiro (A007)
        pay("90002", REF, 299_900, "").andExpect(status().isCreated());
        mvc.perform(post("/settlements/run?entity=90002")).andExpect(jsonPath("$.files[0].formatFileId").value("202611192"));
        String second = new String(lastContent(), StandardCharsets.ISO_8859_1);
        assertThat(second.substring(21, 39)).isEqualTo("202611192202611191");
        mvc.perform(get("/settlements")).andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void sftpFailureKeepsThePaymentsPendingAndTheSequence() throws Exception {
        pay("90002", REF, 299_900, "").andExpect(status().isCreated());
        doThrow(new SftpDelivery.DeliveryException("SFTP down", null)).when(delivery)
                .deliver(any(), anyString(), anyString(), any());

        mvc.perform(post("/settlements/run")).andExpect(status().isOk())
                .andExpect(jsonPath("$.files", hasSize(0)))
                .andExpect(jsonPath("$.errors[0].error").value("SFTP_DELIVERY_FAILED"));
        mvc.perform(get("/payments")).andExpect(jsonPath("$[0].status").value("PENDING_SETTLEMENT"));

        reset(delivery);
        mvc.perform(post("/settlements/run")).andExpect(jsonPath("$.files[0].formatFileId").value("202611191"));
        mvc.perform(get("/payments")).andExpect(jsonPath("$[0].status").value("SETTLED"))
                .andExpect(jsonPath("$[0].fileName").value("MEPS_90002_202611191.txt"));
    }

    @Test
    void tenthMepsFileOfTheDayIsRefused() throws Exception {
        for (int i = 1; i <= 9; i++) {
            mvc.perform(post("/settlements/empty?entity=90002")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.files[0].formatFileId").value("20261119" + i));
        }
        mvc.perform(post("/settlements/empty?entity=90002")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DAILY_SEQUENCE_EXHAUSTED"));
    }

    @Test
    void corruptFilesDoNotSettleThePayments() throws Exception {
        pay("90002", REF, 299_900, "").andExpect(status().isCreated());

        mvc.perform(post("/settlements/run?corrupt=TRAILER_TOTAL")).andExpect(status().isOk())
                .andExpect(jsonPath("$.files[0].corruption").value("TRAILER_TOTAL"));
        String corrupt = new String(lastContent(), StandardCharsets.ISO_8859_1);
        String trailer = corrupt.split("\r\n")[2];
        assertThat(trailer.substring(9, 26)).isEqualTo("00000000000299901");
        mvc.perform(get("/payments")).andExpect(jsonPath("$[0].status").value("PENDING_SETTLEMENT"));

        // o ficheiro certo seguinte usa outra sequência e aponta para o último sem defeito (nenhum)
        mvc.perform(post("/settlements/run")).andExpect(jsonPath("$.files[0].formatFileId").value("202611192"));
        String good = new String(lastContent(), StandardCharsets.ISO_8859_1);
        assertThat(good.substring(30, 39)).isEqualTo("000000000");
    }

    @Test
    void corruptionsWithoutPendingPaymentsSendAnEmptyFileWithTheDefect() throws Exception {
        mvc.perform(post("/settlements/run?corrupt=TRAILER_COUNT&entity=90090")).andExpect(status().isOk())
                .andExpect(jsonPath("$.files", hasSize(1)));
        assertThat(new String(lastContent(), StandardCharsets.ISO_8859_1).split("\r\n")[1]).startsWith("90000001");

        mvc.perform(post("/settlements/run?corrupt=ENTITY&entity=90002")).andExpect(status().isOk());
        assertThat(new String(lastContent(), StandardCharsets.ISO_8859_1).substring(39, 44)).isEqualTo("90003");

        mvc.perform(post("/settlements/run?corrupt=LINE_LENGTH&entity=90002")).andExpect(status().isOk());
        assertThat(new String(lastContent(), StandardCharsets.ISO_8859_1).split("\r\n")[0]).hasSize(108);

        mvc.perform(post("/settlements/run?corrupt=ENTITY&entity=90090")).andExpect(status().isOk());
        assertThat(new String(lastContent(), StandardCharsets.ISO_8859_1)).startsWith("080102");
    }

    @Test
    void resendSendsTheSameContentWithAnotherName() throws Exception {
        mvc.perform(post("/settlements/empty?entity=90002")).andExpect(status().isOk());
        byte[] first = lastContent();

        mvc.perform(post("/settlements/resend?file=MEPS_90002_202611191.txt")).andExpect(status().isOk())
                .andExpect(jsonPath("$.files[0].name").value("MEPS_90002_202611191.resend1.txt"))
                .andExpect(jsonPath("$.files[0].resendOf").value("MEPS_90002_202611191.txt"));
        assertThat(lastContent()).isEqualTo(first);
        mvc.perform(post("/settlements/resend?file=nao-existe")).andExpect(status().isNotFound());
    }

    @Test
    void officialExamplesAreSentAsTheyAre() throws Exception {
        mvc.perform(post("/settlements/official-example?format=MEPS")).andExpect(status().isOk())
                .andExpect(jsonPath("$.files[0].entity").value("90002"))
                .andExpect(jsonPath("$.files[0].records").value(11));
        verify(delivery).deliver(any(), eq("demo-pass"), eq("MEPS_90002_201211230.txt"),
                eq(SettlementService.example(MockProperties.Format.MEPS)));
        mvc.perform(post("/settlements/official-example?format=BMEPS")).andExpect(status().isOk())
                .andExpect(jsonPath("$.files[0].entity").value("90090"))
                .andExpect(jsonPath("$.files[0].formatFileId").value("800-02-00001"));
    }

    @Test
    void sourcesListHasNoPasswordsAndThePageIsServed() throws Exception {
        mvc.perform(get("/sources")).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].destination").value("sftp://demo@sftp:22/inbound"))
                .andExpect(jsonPath("$[1].formatLabel").value("BMEPS 800/02"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
        mvc.perform(get("/")).andExpect(forwardedUrl("index.html"));
    }

    byte[] lastContent() {
        ArgumentCaptor<byte[]> content = ArgumentCaptor.forClass(byte[].class);
        verify(delivery, org.mockito.Mockito.atLeastOnce()).deliver(any(), anyString(), anyString(), content.capture());
        List<byte[]> all = content.getAllValues();
        return all.get(all.size() - 1);
    }
}
