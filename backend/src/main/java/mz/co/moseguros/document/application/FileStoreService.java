package mz.co.moseguros.document.application;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

import mz.co.moseguros.document.FileContent;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.document.domain.AllowedContentType;
import mz.co.moseguros.document.domain.ContentSniffer;
import mz.co.moseguros.document.domain.FilenameSanitizer;
import mz.co.moseguros.document.domain.StoredFile;
import mz.co.moseguros.document.domain.StoredFileRepository;
import mz.co.moseguros.document.infrastructure.storage.FileStorage;
import mz.co.moseguros.document.infrastructure.storage.StoredObjectMissingException;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Guarda e lê ficheiros no âmbito de quem chama (o RLS aplica-se). Usado pelo {@code DocumentApi};
 * não verifica a {@code FileAccessPolicy} (quem chama é o próprio módulo dono).
 *
 * <p>Os logs têm o {@code fileId}, o {@code ownerType}, o tipo e o tamanho; nunca o conteúdo, o
 * nome original nem a chave de armazenamento.
 */
@Service
public class FileStoreService {

    private static final Logger log = LoggerFactory.getLogger(FileStoreService.class);
    private static final String OCTET_STREAM = "application/octet-stream";

    private final StoredFileRepository files;
    private final FileStorage storage;
    private final FileAccessPolicyRegistry policies;
    private final Clock clock;

    public FileStoreService(StoredFileRepository files, FileStorage storage, FileAccessPolicyRegistry policies,
                            Clock clock) {
        this.files = files;
        this.storage = storage;
        this.policies = policies;
        this.clock = clock;
    }

    /**
     * Valida e guarda o ficheiro, numa transacção própria: a linha fica gravada mesmo que a
     * transacção de quem chama falhe depois (fica órfã e a limpeza trata dela).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public FileRef store(StoreFileCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        policies.require(cmd.ownerType());
        Objects.requireNonNull(cmd.ownerId(), "ownerId");
        Objects.requireNonNull(cmd.purpose(), "purpose");
        Objects.requireNonNull(cmd.classification(), "classification");
        byte[] content = Objects.requireNonNull(cmd.content(), "content");

        if (content.length == 0) {
            throw DomainException.of(ErrorCodes.FILE_EMPTY, "The file is empty");
        }
        if (content.length > StoredFile.MAX_SIZE_BYTES) {
            throw DomainException.of(ErrorCodes.FILE_TOO_LARGE, "The file is larger than 10 MB");
        }
        AllowedContentType type = ContentSniffer.sniff(content)
                .filter(t -> cmd.allowedContentTypes() == null || cmd.allowedContentTypes().contains(t.mimeType()))
                .orElseThrow(() -> DomainException.of(ErrorCodes.FILE_TYPE_NOT_ALLOWED,
                        "The file content is not one of the allowed types"));
        String declared = cmd.declaredContentType();
        if (declared != null && !declared.isBlank()) {
            String normalized = AllowedContentType.normalizeMimeType(declared);
            if (!normalized.equals(OCTET_STREAM) && !normalized.equals(type.mimeType())) {
                throw DomainException.of(ErrorCodes.FILE_TYPE_NOT_ALLOWED,
                        "The declared content type does not match the file content");
            }
        }

        TenantSnapshot tenant = TenantContext.current();
        StoredFile file = StoredFile.create(cmd.ownerType(), cmd.ownerId(), cmd.purpose(), cmd.insurerId(),
                tenant.userId(), tenant.orgId(), FilenameSanitizer.clean(cmd.originalFilename(), type),
                type, content.length, sha256(content), cmd.classification());
        String key = storageKey(file.getId(), type);
        file.assignStorageKey(key);

        storage.put(key, new ByteArrayInputStream(content), content.length, type.mimeType());
        try {
            files.saveAndFlush(file);
        } catch (RuntimeException e) {
            try {
                storage.delete(key);
            } catch (RuntimeException deleteFailure) {
                e.addSuppressed(deleteFailure);
            }
            throw e;
        }
        log.info("File stored fileId={} ownerType={} contentType={} sizeBytes={}",
                file.getId(), file.getOwnerType(), file.getContentType(), file.getSizeBytes());
        return toRef(file);
    }

    /**
     * Liga o ficheiro ao dono, na transacção de quem chama (a mesma em que grava a referência ao
     * ficheiro). Idempotente. Ficheiro não visível no âmbito actual → {@code FILE_NOT_FOUND}.
     */
    @Transactional
    public void markLinked(UUID fileId) {
        StoredFile file = visible(fileId);
        file.markLinked(clock.instant());
    }

    /** Conteúdo do ficheiro, no âmbito de quem chama. Quem recebe fecha o stream. */
    @Transactional(readOnly = true)
    public FileContent open(UUID fileId) {
        StoredFile file = visible(fileId);
        return new FileContent(toRef(file), read(file.getId(), file.getStorageKey()));
    }

    @Transactional(readOnly = true)
    public FileRef metadata(UUID fileId) {
        return toRef(visible(fileId));
    }

    /** Conteúdo de uma chave; em falta ou ilegível → {@code FILE_STORAGE_ERROR} (500). */
    InputStream read(UUID fileId, String storageKey) {
        try {
            return storage.get(storageKey);
        } catch (StoredObjectMissingException e) {
            log.error("Stored content missing for fileId={}", fileId);
            throw DomainException.of(ErrorCodes.FILE_STORAGE_ERROR, "The file content could not be read");
        } catch (UncheckedIOException | IllegalArgumentException e) {
            log.error("Stored content unreadable for fileId={} ({})", fileId, e.getClass().getSimpleName());
            throw DomainException.of(ErrorCodes.FILE_STORAGE_ERROR, "The file content could not be read");
        }
    }

    private StoredFile visible(UUID fileId) {
        return files.findById(Objects.requireNonNull(fileId, "fileId"))
                .filter(file -> !file.isPurged())
                .orElseThrow(FileStoreService::notFound);
    }

    static DomainException notFound() {
        return DomainException.of(ErrorCodes.FILE_NOT_FOUND, "File not found");
    }

    static FileRef toRef(StoredFile file) {
        return new FileRef(file.getId(), file.getOwnerType(), file.getOwnerId(), file.getPurpose(),
                file.getContentType(), file.getSizeBytes(), file.getSha256(), file.getOriginalFilename(),
                file.getClassification(), file.getInsurerId(),
                toMicros(file.getCreatedAt()), file.isLinked());
    }

    /**
     * A BD guarda microssegundos, arredondando (metade para o par, como o {@code rint} do
     * PostgreSQL): o {@code createdAt} devolvido pelo {@code store} é igual ao lido depois.
     */
    static Instant toMicros(Instant instant) {
        if (instant == null) {
            return null;
        }
        Instant truncated = instant.truncatedTo(ChronoUnit.MICROS);
        long remainder = instant.getNano() % 1_000;
        long micros = instant.getNano() / 1_000;
        boolean roundUp = remainder > 500 || (remainder == 500 && micros % 2 == 1);
        return roundUp ? truncated.plus(1, ChronoUnit.MICROS) : truncated;
    }

    /** {@code {aaaa}/{mm}/{dd}/{fileId}.{ext}}, com a data UTC do envio. */
    private String storageKey(UUID fileId, AllowedContentType type) {
        LocalDate today = LocalDate.ofInstant(clock.instant(), ZoneOffset.UTC);
        return String.format("%04d/%02d/%02d/%s.%s", today.getYear(), today.getMonthValue(), today.getDayOfMonth(),
                fileId, type.extension());
    }

    static String sha256(byte[] content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(content));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
