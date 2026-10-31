package mz.co.moseguros;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class MetaControllerIT {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void healthIsUp() {
        given().get("/api/v1/health")
                .then().statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    void versionIsPublic() {
        given().get("/api/v1/meta/version")
                .then().statusCode(200)
                .body("name", equalTo("moseguros-backend"))
                .body("version", notNullValue())
                .body("commit", notNullValue());
    }

    @Test
    void anyOtherPathRequiresAuthenticationAsProblemJson() {
        given().get("/api/v1/qualquer-coisa")
                .then().statusCode(401)
                .contentType("application/problem+json")
                .body("status", equalTo(401))
                .body("code", equalTo("UNAUTHORIZED"));
    }

    @Test
    void openApiDocumentIsPublic() {
        given().get("/v3/api-docs")
                .then().statusCode(200)
                .body("paths", notNullValue());
    }
}
