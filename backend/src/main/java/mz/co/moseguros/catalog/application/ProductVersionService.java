package mz.co.moseguros.catalog.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import mz.co.moseguros.catalog.domain.Coverage;
import mz.co.moseguros.catalog.domain.CoverageRepository;
import mz.co.moseguros.catalog.domain.CoverageRules;
import mz.co.moseguros.catalog.domain.CoverageSpec;
import mz.co.moseguros.catalog.domain.Product;
import mz.co.moseguros.catalog.domain.ProductDocument;
import mz.co.moseguros.catalog.domain.ProductDocumentRepository;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.catalog.domain.ProductVersionRepository;
import mz.co.moseguros.catalog.domain.ProductVersionStatus;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.hibernate.StaleStateException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Versões dos produtos da seguradora do utilizador (S2-04 §2 e §3): criar (copiando a última
 * publicada), consultar, alterar a elegibilidade e substituir as coberturas. Só a versão
 * {@code DRAFT} se altera; produto retirado não se altera. Toda a escrita incrementa a
 * {@code version} da linha {@code product_version}.
 */
@Service
public class ProductVersionService {

    private final ProductService productService;
    private final ProductVersionRepository versions;
    private final CoverageRepository coverages;
    private final ProductDocumentRepository documents;
    private final AuditService audit;
    private final CurrentUser currentUser;

    @PersistenceContext
    private EntityManager entityManager;

    public ProductVersionService(ProductService productService, ProductVersionRepository versions,
                                 CoverageRepository coverages, ProductDocumentRepository documents,
                                 AuditService audit, CurrentUser currentUser) {
        this.productService = productService;
        this.versions = versions;
        this.coverages = coverages;
        this.documents = documents;
        this.audit = audit;
        this.currentUser = currentUser;
    }

    /** Versão com o produto, as coberturas (por ordem) e os documentos. */
    public record VersionDetails(ProductVersion version, Product product, List<Coverage> coverages,
                                 List<ProductDocument> documents) {

        /** {@code DRAFT} e produto não retirado. */
        public boolean editable() {
            return version.isDraft() && !product.isRetired();
        }
    }

    /**
     * Nova versão {@code DRAFT}: copia a versão não-{@code DRAFT} com o número mais alto
     * (categorias, usos, regras, coberturas e documentos, que apontam para os mesmos ficheiros).
     *
     * @throws DomainException {@code PRODUCT_RETIRED} ou {@code PRODUCT_DRAFT_EXISTS} (409)
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public VersionDetails create(UUID productId) {
        Product product = productService.load(productId);
        product.requireNotRetired();
        if (versions.existsByProductIdAndStatus(productId, ProductVersionStatus.DRAFT)) {
            throw DomainException.of(ErrorCodes.PRODUCT_DRAFT_EXISTS, "The product already has a DRAFT version");
        }
        ProductVersion base = versions.findFirstByProductIdAndStatusNotOrderByVersionNoDesc(productId,
                ProductVersionStatus.DRAFT).orElse(null);
        ProductVersion draft = versions.saveAndFlush(ProductVersion.draftFrom(product,
                versions.maxVersionNo(productId) + 1, base));
        if (base != null) {
            coverages.saveAll(coverages.findByProductVersionIdOrderBySortOrderAsc(base.getId()).stream()
                    .map(c -> c.copyTo(draft)).toList());
            documents.saveAll(documents.findByProductVersionIdOrderByCreatedAtAscIdAsc(base.getId()).stream()
                    .map(d -> d.copyTo(draft)).toList());
            coverages.flush();
        }
        audit(draft, "PRODUCT_VERSION.CREATE", null, VersionSnapshot.of(draft));
        return details(draft, product);
    }

    @PreAuthorize("hasAuthority('product:read')")
    @Transactional(readOnly = true)
    public VersionDetails get(UUID versionId) {
        ProductVersion version = load(versionId);
        return details(version, productService.load(version.getProductId()));
    }

    /**
     * Categorias, usos e notas da versão {@code DRAFT}.
     *
     * @throws DomainException {@code PRODUCT_VERSION_NOT_EDITABLE}, {@code PRODUCT_RETIRED} ou
     *                         {@code CONCURRENT_MODIFICATION} (409)
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public VersionDetails update(UUID versionId, List<VehicleCategory> categories, List<VehicleUsage> usages,
                                 String changeNotes, long expectedVersion) {
        Editing editing = beginEdit(versionId, expectedVersion, false);
        ProductVersion version = editing.version();
        VersionSnapshot before = VersionSnapshot.of(version);
        version.updateEligibility(categories, usages, changeNotes);
        versions.flush();
        if (version.getVersion() == expectedVersion) {
            forceIncrement(version); // pedido sem alterações: a escrita conta na mesma
        }
        audit(version, "PRODUCT_VERSION.UPDATE", before, VersionSnapshot.of(version));
        return details(version, editing.product());
    }

    /**
     * Substitui as coberturas da versão {@code DRAFT} (validadas por {@link CoverageRules}); a
     * ordem da lista é o {@code sort_order}.
     *
     * @throws DomainException 400 {@code VALIDATION_ERROR} com {@code errors[]}, ou 409 como em
     *                         {@link #update}
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public VersionDetails replaceCoverages(UUID versionId, List<CoverageSpec> specs, long expectedVersion) {
        Editing editing = beginEdit(versionId, expectedVersion, false);
        CoverageRules.require(specs);
        ProductVersion version = editing.version();
        List<Coverage> current = coverages.findByProductVersionIdOrderBySortOrderAsc(versionId);
        List<CoverageSpec> before = current.stream().map(Coverage::toSpec).toList();
        coverages.deleteAll(current);
        coverages.flush();
        for (int i = 0; i < specs.size(); i++) {
            coverages.save(Coverage.create(version, specs.get(i), i));
        }
        coverages.flush();
        forceIncrement(version);
        audit(version, "PRODUCT_VERSION.COVERAGES_UPDATE", new CoveragesSnapshot(before), new CoveragesSnapshot(
                coverages.findByProductVersionIdOrderBySortOrderAsc(versionId).stream().map(Coverage::toSpec).toList()));
        return details(version, editing.product());
    }

    /** Versão e produto prontos para alterar. */
    public record Editing(ProductVersion version, Product product) {
    }

    /**
     * Carrega a versão para a alterar: {@code DRAFT} ({@code PRODUCT_VERSION_NOT_EDITABLE}), produto
     * não retirado ({@code PRODUCT_RETIRED}) e, se {@code expectedVersion} não for {@code null},
     * a mesma {@code version} ({@code CONCURRENT_MODIFICATION}).
     */
    public Editing beginEdit(UUID versionId, Long expectedVersion, boolean lock) {
        ProductVersion version = load(versionId);
        version.requireEditable();
        Product product = productService.load(version.getProductId());
        product.requireNotRetired();
        if (expectedVersion != null && version.getVersion() != expectedVersion) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The product version was changed by another request");
        }
        if (lock) {
            forceIncrement(version);
        }
        return new Editing(version, product);
    }

    /**
     * Incrementa já a {@code version} da linha (as coberturas e os documentos são linhas próprias e
     * não a mudam). Com outro pedido a escrever ao mesmo tempo → {@code CONCURRENT_MODIFICATION}.
     */
    public void forceIncrement(ProductVersion version) {
        try {
            entityManager.lock(version, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        } catch (OptimisticLockException | StaleStateException e) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The product version was changed by another request");
        }
    }

    /** Versão da seguradora actual; {@code PRODUCT_VERSION_NOT_FOUND} (404) se não existir ou não for visível. */
    public ProductVersion load(UUID versionId) {
        UUID insurerId = TenantContext.insurerId();
        return versions.findById(versionId)
                .filter(v -> v.getInsurerId().equals(insurerId))
                .orElseThrow(() -> DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_FOUND,
                        "Product version not found"));
    }

    public VersionDetails details(ProductVersion version, Product product) {
        return new VersionDetails(version, product,
                coverages.findByProductVersionIdOrderBySortOrderAsc(version.getId()),
                documents.findByProductVersionIdOrderByCreatedAtAscIdAsc(version.getId()));
    }

    public void audit(ProductVersion version, String action, Object before, Object after) {
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), version.getInsurerId(), action,
                "ProductVersion", version.getId().toString(), before, after));
    }

    /** Estado auditado da versão (sem coberturas nem documentos). */
    public record VersionSnapshot(UUID productId, int versionNo, ProductVersionStatus status,
                                  List<VehicleCategory> vehicleCategories, List<VehicleUsage> usages,
                                  String changeNotes, LocalDate validFrom, LocalDate validTo) {

        static VersionSnapshot of(ProductVersion v) {
            return new VersionSnapshot(v.getProductId(), v.getVersionNo(), v.getStatus(), v.getVehicleCategories(),
                    v.getUsages(), v.getChangeNotes(), v.getValidFrom(), v.getValidTo());
        }
    }

    /** Lista de coberturas auditada (antes e depois). */
    public record CoveragesSnapshot(List<CoverageSpec> coverages) {
    }
}
