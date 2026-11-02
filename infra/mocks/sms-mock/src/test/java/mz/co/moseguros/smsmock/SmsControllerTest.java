package mz.co.moseguros.smsmock;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    MockMvc mvc;

    @BeforeEach
    void clear() throws Exception {
        mvc.perform(delete("/sms")).andExpect(status().isNoContent());
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
                .andExpect(jsonPath("$[0].to").value("+258841234567"))
                .andExpect(jsonPath("$[0].text").value("teste"))
                .andExpect(jsonPath("$[0].receivedAt", notNullValue()));
    }

    @Test
    void rejectsMessageWithoutRecipient() throws Exception {
        mvc.perform(post("/sms").contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"teste\"}"))
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
