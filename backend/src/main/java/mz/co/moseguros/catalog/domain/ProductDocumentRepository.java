package mz.co.moseguros.catalog.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.catalog.ProductDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

/** Documentos de versões visíveis no âmbito da transacção (Row Level Security por {@code insurer_id}). */
public interface ProductDocumentRepository extends JpaRepository<ProductDocument, UUID> {

    List<ProductDocument> findByProductVersionIdOrderByCreatedAtAscIdAsc(UUID productVersionId);

    List<ProductDocument> findByProductVersionIdInOrderByCreatedAtAscIdAsc(Collection<UUID> productVersionIds);

    Optional<ProductDocument> findByProductVersionIdAndDocTypeAndLanguage(UUID productVersionId,
                                                                          ProductDocumentType docType,
                                                                          String language);

    Optional<ProductDocument> findByIdAndProductVersionId(UUID id, UUID productVersionId);

    long countByProductVersionId(UUID productVersionId);
}
