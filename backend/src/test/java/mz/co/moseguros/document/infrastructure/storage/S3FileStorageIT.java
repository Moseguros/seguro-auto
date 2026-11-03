package mz.co.moseguros.document.infrastructure.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

/** {@link S3FileStorage} contra o S3Mock (S2-01 §3). */
class S3FileStorageIT {

    static final String BUCKET = "moseguros-test";

    private static GenericContainer<?> s3mock;
    private static S3Client client;

    @BeforeAll
    static void start() {
        s3mock = new GenericContainer<>(DockerImageName.parse("adobe/s3mock:3.12.0"))
                .withExposedPorts(9090)
                .withEnv("initialBuckets", BUCKET)
                .withEnv("COM_ADOBE_TESTING_S3MOCK_STORE_INITIAL_BUCKETS", BUCKET)
                .waitingFor(Wait.forHttp("/").forPort(9090).forStatusCodeMatching(code -> code < 500));
        s3mock.start();
        client = S3FileStorage.client(
                new StorageProperties.S3(BUCKET, "us-east-1",
                        "http://" + s3mock.getHost() + ":" + s3mock.getMappedPort(9090), true),
                StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")));
    }

    @AfterAll
    static void stop() {
        if (client != null) {
            client.close();
        }
        if (s3mock != null) {
            s3mock.stop();
        }
    }

    @Test
    void putGetExistsAndDelete() throws IOException {
        S3FileStorage storage = new S3FileStorage(client, BUCKET);
        String key = "2026/12/01/" + UUID.randomUUID() + ".pdf";
        byte[] content = "%PDF-1.4 conteudo de teste".getBytes(StandardCharsets.US_ASCII);

        assertThat(storage.exists(key)).isFalse();
        storage.put(key, new ByteArrayInputStream(content), content.length, "application/pdf");
        assertThat(storage.exists(key)).isTrue();
        try (InputStream in = storage.get(key)) {
            assertThat(in.readAllBytes()).isEqualTo(content);
        }
        HeadObjectResponse head = client.headObject(HeadObjectRequest.builder().bucket(BUCKET).key(key).build());
        assertThat(head.contentType()).isEqualTo("application/pdf");

        storage.delete(key);
        assertThat(storage.exists(key)).isFalse();
        assertThatCode(() -> storage.delete(key)).doesNotThrowAnyException();
        assertThatThrownBy(() -> storage.get(key)).isInstanceOf(StoredObjectMissingException.class);
    }

    @Test
    void missingBucketFailsTheComponentStartup() {
        assertThatThrownBy(() -> new S3FileStorage(client, "nao-existe"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("nao-existe")
                .hasMessageNotContaining("test:test");
    }

    @Test
    void bucketAndRegionAreRequired() {
        assertThatThrownBy(() -> new S3FileStorage(client, " ")).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MOSEGUROS_S3_BUCKET");
        assertThatThrownBy(() -> S3FileStorage.client(new StorageProperties.S3(BUCKET, null, null, false),
                StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("MOSEGUROS_S3_REGION");
    }
}
