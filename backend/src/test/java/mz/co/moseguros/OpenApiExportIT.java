package mz.co.moseguros;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Descarrega o contrato {@code /v3/api-docs.yaml}. Com {@code -Dopenapi.export=true}
 * (perfil Maven {@code openapi}) escreve-o em {@code ../api/openapi.yaml}
 * (ou em {@code -Dopenapi.output=...}).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class OpenApiExportIT {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void exportsTheContract() throws IOException {
        String yaml = new String(given().get("/v3/api-docs.yaml")
                .then().statusCode(200)
                .extract().asByteArray(), StandardCharsets.UTF_8);

        assertThat(yaml)
                .startsWith("openapi: 3.0.")
                .contains("title: MoSeguros API")
                .contains("/api/v1/meta/version")
                .contains("operationId: getVersion")
                .contains("bearerAuth")
                .contains("ProblemDetail")
                .doesNotContain("localhost");

        if (Boolean.getBoolean("openapi.export")) {
            Path output = Path.of(System.getProperty("openapi.output", "../api/openapi.yaml")).toAbsolutePath().normalize();
            Files.createDirectories(output.getParent());
            String normalized = yaml.replace("\r\n", "\n");
            if (!normalized.endsWith("\n")) {
                normalized += "\n";
            }
            Files.writeString(output, normalized, StandardCharsets.UTF_8);
        }
    }
}
