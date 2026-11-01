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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
        mvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
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

    @RestController
    static class TestController {

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
