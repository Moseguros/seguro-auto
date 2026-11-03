package mz.co.moseguros.shared.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.domain.StaticPlateFormatProvider;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.json.ValueObjectsModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
                .modulesToInstall(new ValueObjectsModule(
                        new PlateParser(StaticPlateFormatProvider.of(StaticPlateFormatProvider.STANDARD))))
                .build();
        mvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new StringHttpMessageConverter(), new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    @Test
    void invalidValueObjectInBodyIsAFieldError() throws Exception {
        mvc.perform(post("/test/contact").contentType(MediaType.APPLICATION_JSON).content("{\"msisdn\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code", equalTo("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", equalTo("msisdn")))
                .andExpect(jsonPath("$.errors[0].code", equalTo("INVALID_MSISDN")))
                .andExpect(content().string(not(containsString("\"123\""))));
    }

    @Test
    void nestedFieldPathIsReported() throws Exception {
        mvc.perform(post("/test/vehicles").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehicles\":[{\"plate\":\"ABC-123-MC\"},{\"plate\":\"XX\"}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.errors[0].field", equalTo("vehicles[1].plate")))
                .andExpect(jsonPath("$.errors[0].code", equalTo("INVALID_PLATE")));
    }

    @Test
    void validValueObjectInBodyIsAccepted() throws Exception {
        mvc.perform(post("/test/contact").contentType(MediaType.APPLICATION_JSON).content("{\"msisdn\":\"84 123 4567\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("+258841234567"));
    }

    @Test
    void malformedBodyIsA400NotA500() throws Exception {
        mvc.perform(post("/test/contact").contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.errors", hasSize(0)));
    }

    @Test
    void valueObjectCreatedOutsideTheRequestKeepsTopLevelCode() throws Exception {
        mvc.perform(get("/test/msisdn"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", equalTo("INVALID_MSISDN")));
    }

    @Test
    void invalidBodyReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/test/body").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code", equalTo("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", equalTo("name")))
                .andExpect(jsonPath("$.errors[0].code", equalTo("NOT_BLANK")));
    }

    @Test
    void domainExceptionKeepsItsCodeAndStatus() throws Exception {
        mvc.perform(get("/test/domain"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", equalTo("POLICY_NOT_ACTIVE")))
                .andExpect(jsonPath("$.type", equalTo("https://moseguros.co.mz/problems/policy-not-active")));
    }

    @Test
    void unexpectedErrorReturns500WithoutInternals() throws Exception {
        mvc.perform(get("/test/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", equalTo("INTERNAL_ERROR")))
                .andExpect(content().string(not(containsString("secret-internal-detail"))))
                .andExpect(content().string(not(containsString("IllegalStateException"))));
    }

    @Test
    void missingResourceReturns404() {
        var problem = new GlobalExceptionHandler()
                .handleNotFound(new NoResourceFoundException(HttpMethod.GET, "does-not-exist"));
        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getProperties()).containsEntry("code", "NOT_FOUND");
    }

    @Test
    void constraintNamesBecomeUpperSnakeCodes() {
        assertThat(GlobalExceptionHandler.codeOf("NotBlank")).isEqualTo("NOT_BLANK");
        assertThat(GlobalExceptionHandler.codeOf("Size")).isEqualTo("SIZE");
        assertThat(GlobalExceptionHandler.codeOf(null)).isEqualTo("INVALID");
    }

    record Body(@NotBlank String name) {
    }

    record Contact(Msisdn msisdn) {
    }

    record Vehicle(Matricula plate) {
    }

    record Fleet(List<Vehicle> vehicles) {
    }

    @RestController
    static class TestController {

        @PostMapping("/test/contact")
        String contact(@RequestBody Contact contact) {
            return contact.msisdn().e164();
        }

        @PostMapping("/test/vehicles")
        int vehicles(@RequestBody Fleet fleet) {
            return fleet.vehicles().size();
        }

        @GetMapping("/test/msisdn")
        String msisdn() {
            return Msisdn.of("123").e164();
        }

        @PostMapping("/test/body")
        String body(@Valid @RequestBody Body body) {
            return body.name();
        }

        @GetMapping("/test/domain")
        String domain() {
            throw new DomainException("POLICY_NOT_ACTIVE", 409, "Policy is not active");
        }

        @GetMapping("/test/boom")
        String boom() {
            throw new IllegalStateException("secret-internal-detail");
        }
    }
}
