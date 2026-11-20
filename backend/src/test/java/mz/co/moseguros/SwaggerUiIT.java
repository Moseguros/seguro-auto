package mz.co.moseguros;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/** Swagger UI ligado no perfil {@code local} e desligado em {@code prod}. */
class SwaggerUiIT {

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @Import(TestcontainersConfiguration.class)
    @ActiveProfiles({"test", "local"})
    class Local {

        @LocalServerPort
        int port;

        @BeforeEach
        void setUp() {
            RestAssured.port = port;
        }

        @Test
        void swaggerUiIsAvailable() {
            given().redirects().follow(true).get("/swagger-ui.html")
                    .then().statusCode(200)
                    .body(containsString("Swagger UI"));
        }
    }

    @Nested
    // o perfil prod exige S3 (S2-01); aqui basta o disco do perfil test. O prod exige o Turnstile (S6-03):
    // chave do site e anfitriões de teste
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
            properties = {"moseguros.files.storage=filesystem",
                "moseguros.captcha.turnstile.site-key=test-site-key",
                "moseguros.captcha.turnstile.allowed-hostnames=verificar.moseguros.test"})
    @Import(TestcontainersConfiguration.class)
    @ActiveProfiles({"test", "prod"})
    class Prod {

        @LocalServerPort
        int port;

        @BeforeEach
        void setUp() {
            RestAssured.port = port;
        }

        @Test
        void swaggerUiIsDisabled() {
            given().redirects().follow(false).get("/swagger-ui.html")
                    .then().statusCode(404);
        }
    }
}
