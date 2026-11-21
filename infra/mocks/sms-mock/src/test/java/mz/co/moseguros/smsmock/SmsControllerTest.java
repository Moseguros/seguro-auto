package mz.co.moseguros.smsmock;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SmsControllerTest {

    static final String WHATSAPP_BODY =
            "{\"to\":\"+258841234567\",\"template\":\"moseguros_otp\",\"params\":{\"code\":\"123456\"}}";

    @Autowired
    MockMvc mvc;

    @BeforeEach
    void clear() throws Exception {
        mvc.perform(delete("/sms")).andExpect(status().isNoContent());
        mvc.perform(put("/config").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sms\":\"OK\",\"whatsapp\":\"OK\",\"push\":\"OK\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void storesAndListsMessages() throws Exception {
        mvc.perform(post("/sms").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"+258841234567\",\"text\":\"teste\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", notNullValue()));

        mvc.perform(get("/sms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].channel").value("SMS"))
                .andExpect(jsonPath("$[0].to").value("+258841234567"))
                .andExpect(jsonPath("$[0].text").value("teste"))
                .andExpect(jsonPath("$[0].receivedAt", notNullValue()));
    }

    @Test
    void storesWhatsAppTemplateMessagesInTheSameList() throws Exception {
        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content(WHATSAPP_BODY))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", notNullValue()));
        mvc.perform(post("/sms").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"+258841234567\",\"text\":\"sms\"}"))
                .andExpect(status().isAccepted());

        mvc.perform(get("/sms"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].channel").value("WHATSAPP"))
                .andExpect(jsonPath("$[1].template").value("moseguros_otp"))
                .andExpect(jsonPath("$[1].params.code").value("123456"))
                .andExpect(jsonPath("$[1].text").value("moseguros_otp {code=123456}"));
    }

    @Test
    void acceptsTheProposalSigningTemplate() throws Exception {
        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content(
                        "{\"to\":\"+258841234567\",\"template\":\"moseguros_sign_otp\",\"params\":"
                                + "{\"code\":\"123456\",\"ref\":\"PR-2027-000123\",\"insurer\":\"Seguradora Demo\","
                                + "\"total\":\"4 663,00 MT\"}}"))
                .andExpect(status().isAccepted());

        mvc.perform(get("/sms"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].template").value("moseguros_sign_otp"))
                .andExpect(jsonPath("$[0].params.ref").value("PR-2027-000123"))
                .andExpect(jsonPath("$[0].params.total").value("4 663,00 MT"));
    }

    @Test
    void rejectsUnknownTemplatesAndMissingParameters() throws Exception {
        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content(
                        "{\"to\":\"+258841234567\",\"template\":\"other\",\"params\":{\"code\":\"1\"}}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content(
                        "{\"to\":\"+258841234567\",\"template\":\"moseguros_sign_otp\",\"params\":{\"code\":\"1\"}}"))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/sms")).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void failModeAnswers503AndStoresNothing() throws Exception {
        mvc.perform(put("/config").contentType(MediaType.APPLICATION_JSON).content("{\"sms\":\"FAIL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sms").value("FAIL"))
                .andExpect(jsonPath("$.whatsapp").value("OK"));

        mvc.perform(post("/sms").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"to\":\"+258841234567\",\"text\":\"teste\"}"))
                .andExpect(status().isServiceUnavailable());
        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content(WHATSAPP_BODY))
                .andExpect(status().isAccepted());

        mvc.perform(get("/sms")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/config")).andExpect(jsonPath("$.sms").value("FAIL"));
    }

    @Test
    void failReceiptModeStillAccepts() throws Exception {
        mvc.perform(put("/config").contentType(MediaType.APPLICATION_JSON).content("{\"whatsapp\":\"FAIL_RECEIPT\"}"))
                .andExpect(jsonPath("$.whatsapp").value("FAIL_RECEIPT"));

        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content(WHATSAPP_BODY))
                .andExpect(status().isAccepted());
    }

    @Test
    void acceptsTheNotificationTemplatesWithPositionalParameters() throws Exception {
        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content(
                        "{\"to\":\"+258841234567\",\"template\":\"moseguros_policy_issued\",\"params\":"
                                + "{\"p1\":\"AP-2027-000123\",\"p2\":\"ABC-123-MC\",\"p3\":\"10/02/2028 10:31\"}}"))
                .andExpect(status().isAccepted());
        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content(
                        "{\"to\":\"+258841234567\",\"template\":\"moseguros_payment_reference\",\"params\":"
                                + "{\"p1\":\"1\",\"p2\":\"2\",\"p3\":\"3\"}}"))
                .andExpect(status().isBadRequest());
        for (String template : WhatsAppTemplates.PARAMS.keySet()) {
            org.assertj.core.api.Assertions.assertThat(template).startsWith("moseguros_");
        }
        org.assertj.core.api.Assertions.assertThat(WhatsAppTemplates.PARAMS).hasSize(11)
                .containsEntry("moseguros_payment_reference", java.util.List.of("p1", "p2", "p3", "p4"))
                .containsEntry("moseguros_proposal_expired", java.util.List.of("p1"));

        mvc.perform(get("/sms")).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].params.p2").value("ABC-123-MC"));
    }

    @Test
    void storesPushInTheSameListWithoutReceipts() throws Exception {
        mvc.perform(post("/push").contentType(MediaType.APPLICATION_JSON).content(
                        "{\"token\":\"...abcd1234\",\"title\":\"Apolice emitida\",\"body\":\"A apolice AP-1 esta activa.\","
                                + "\"data\":{\"notificationId\":\"n-1\",\"deepLink\":\"moseguros://policy/1\"}}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", notNullValue()));

        mvc.perform(get("/sms"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].channel").value("PUSH"))
                .andExpect(jsonPath("$[0].to").value("...abcd1234"))
                .andExpect(jsonPath("$[0].text").value("Apolice emitida: A apolice AP-1 esta activa."))
                .andExpect(jsonPath("$[0].params.deepLink").value("moseguros://policy/1"));

        mvc.perform(put("/config").contentType(MediaType.APPLICATION_JSON).content("{\"push\":\"FAIL\"}"))
                .andExpect(jsonPath("$.push").value("FAIL")).andExpect(jsonPath("$.sms").value("OK"));
        mvc.perform(post("/push").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"t\",\"title\":\"x\",\"body\":\"y\"}"))
                .andExpect(status().isServiceUnavailable());
        mvc.perform(post("/push").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMessageWithoutRecipient() throws Exception {
        mvc.perform(post("/sms").contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"teste\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/whatsapp").contentType(MediaType.APPLICATION_JSON).content("{\"template\":\"x\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void servesThePageAtRoot() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(forwardedUrl("index.html"));
    }

    @Test
    void exposesHealth() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("UP")));
    }
}
