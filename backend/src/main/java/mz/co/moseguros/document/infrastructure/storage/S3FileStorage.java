package mz.co.moseguros.document.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Objects;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

/**
 * Armazenamento em S3 ou num serviço compatível (staging e produção; ADR-005), com o
 * {@link S3Client} síncrono do AWS SDK v2. As credenciais vêm da cadeia por defeito do SDK
 * ({@code AWS_ACCESS_KEY_ID}/{@code AWS_SECRET_ACCESS_KEY} ou papel da máquina), nunca da
 * configuração. Cada objecto é gravado com cifra do lado do servidor ({@code AES256}).
 *
 * <p>O construtor verifica o bucket ({@code headBucket}): sem acesso, a aplicação não arranca.
 */
public class S3FileStorage implements FileStorage {

    private final S3Client client;
    private final String bucket;

    public S3FileStorage(S3Client client, String bucket) {
        this.client = Objects.requireNonNull(client, "client");
        this.bucket = requireText(bucket, "moseguros.files.s3.bucket (MOSEGUROS_S3_BUCKET)");
        verifyBucket();
    }

    /** Cliente para as propriedades indicadas e as credenciais dadas. */
    public static S3Client client(StorageProperties.S3 properties, AwsCredentialsProvider credentials) {
        requireText(properties.region(), "moseguros.files.s3.region (MOSEGUROS_S3_REGION)");
        S3ClientBuilder builder = S3Client.builder()
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .region(Region.of(properties.region()))
                .credentialsProvider(credentials)
                .forcePathStyle(properties.pathStyleAccess())
                // checksums só quando a operação os exige: muitos serviços compatíveis com S3 ainda
                // não aceitam os checksums por defeito do SDK (CRC32 em aws-chunked)
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED);
        if (properties.endpoint() != null && !properties.endpoint().isBlank()) {
            builder.endpointOverride(URI.create(properties.endpoint()));
        }
        return builder.build();
    }

    @Override
    public void put(String key, InputStream content, long size, String contentType) {
        try (InputStream in = content) {
            client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(contentType)
                            .contentLength(size)
                            .serverSideEncryption(ServerSideEncryption.AES256)
                            .build(),
                    RequestBody.fromInputStream(in, size));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write the stored object", e);
        }
    }

    @Override
    public InputStream get(String key) {
        try {
            return client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (NoSuchKeyException e) {
            throw new StoredObjectMissingException(e);
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    @Override
    public void delete(String key) {
        // S3 não dá erro ao apagar uma chave inexistente.
        client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }

    private void verifyBucket() {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception e) {
            throw new IllegalStateException("S3 bucket '" + bucket + "' is not accessible (HTTP "
                    + e.statusCode() + "). Check MOSEGUROS_S3_BUCKET, the region/endpoint and the credentials.");
        } catch (SdkException e) {
            throw new IllegalStateException("S3 bucket '" + bucket + "' is not accessible ("
                    + e.getClass().getSimpleName() + "). Check MOSEGUROS_S3_ENDPOINT, MOSEGUROS_S3_REGION and"
                    + " the credentials.");
        }
    }

    private static String requireText(String value, String property) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(property + " is required when moseguros.files.storage=s3");
        }
        return value;
    }
}
