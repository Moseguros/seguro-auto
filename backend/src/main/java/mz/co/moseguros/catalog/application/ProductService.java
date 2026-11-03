package mz.co.moseguros.catalog.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jakarta.persistence.criteria.Predicate;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.domain.Product;
import mz.co.moseguros.catalog.domain.ProductRepository;
import mz.co.moseguros.catalog.domain.ProductStatus;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.catalog.domain.ProductVersionRepository;
import mz.co.moseguros.catalog.domain.ProductVersionStatus;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Produtos da seguradora do utilizador (S2-04 §2 e §3). Leitura com {@code product:read}, escrita
 * com {@code product:manage}. Usa sempre {@code TenantContext.insurerId()}; um produto de outra
 * seguradora é invisível (RLS) e dá 404 {@code PRODUCT_NOT_FOUND}.
 */
@Service
public class ProductService {

    /** Fuso das datas de vigência. */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");

    private final ProductRepository products;
    private final ProductVersionRepository versions;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final Clock clock;

    public ProductService(ProductRepository products, ProductVersionRepository versions, AuditService audit,
                          CurrentUser currentUser, Clock clock) {
        this.products = products;
        this.versions = versions;
        this.audit = audit;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    /** Produto com as suas versões, da mais recente para a mais antiga. */
    public record ProductDetails(Product product, List<ProductVersion> versions) {
    }

    /** Linha da lista de produtos. */
    public record ProductSummary(Product product, Integer currentVersionNo, boolean hasDraft) {
    }

    /**
     * Produtos da seguradora.
     *
     * @param q procura no nome ou no código, sem diferenciar maiúsculas
     */
    @PreAuthorize("hasAuthority('product:read')")
    @Transactional(readOnly = true)
    public Page<ProductSummary> list(ProductStatus status, ProductType type, String q, Pageable pageable) {
        UUID insurerId = TenantContext.insurerId();
        Page<Product> page = products.findAll(filter(insurerId, status, type, q), pageable);
        List<UUID> ids = page.getContent().stream().map(Product::getId).toList();
        List<ProductVersion> relevant = ids.isEmpty() ? List.of() : versions.findByProductIdInAndStatusIn(ids,
                List.of(ProductVersionStatus.DRAFT, ProductVersionStatus.PUBLISHED));
        return page.map(p -> new ProductSummary(p,
                relevant.stream().filter(v -> v.getProductId().equals(p.getId())
                                && v.getStatus() == ProductVersionStatus.PUBLISHED)
                        .map(ProductVersion::getVersionNo).findFirst().orElse(null),
                relevant.stream().anyMatch(v -> v.getProductId().equals(p.getId()) && v.isDraft())));
    }

    /**
     * Cria o produto em {@code DRAFT} e a versão 1 {@code DRAFT} vazia.
     *
     * @throws DomainException {@code PRODUCT_CODE_ALREADY_EXISTS} (409)
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public ProductDetails create(String code, String name, ProductType type, String description) {
        UUID insurerId = TenantContext.insurerId();
        if (products.existsByInsurerIdAndCode(insurerId, code)) {
            throw DomainException.of(ErrorCodes.PRODUCT_CODE_ALREADY_EXISTS, "Product code already exists");
        }
        Product product = products.saveAndFlush(Product.create(insurerId, code, name, type, description));
        ProductVersion first = versions.saveAndFlush(ProductVersion.first(product));
        audit(insurerId, "PRODUCT.CREATE", "Product", product.getId(), null, ProductSnapshot.of(product));
        audit(insurerId, "PRODUCT_VERSION.CREATE", "ProductVersion", first.getId(), null,
                ProductVersionService.VersionSnapshot.of(first));
        return new ProductDetails(product, List.of(first));
    }

    @PreAuthorize("hasAuthority('product:read')")
    @Transactional(readOnly = true)
    public ProductDetails get(UUID id) {
        Product product = load(id);
        return new ProductDetails(product, versions.findByProductIdOrderByVersionNoDesc(product.getId()));
    }

    /**
     * Altera nome, tipo e descrição.
     *
     * @param version versão lida; diferente → {@code CONCURRENT_MODIFICATION} (409)
     * @throws DomainException {@code PRODUCT_RETIRED} ou {@code PRODUCT_TYPE_LOCKED} (409)
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public ProductDetails update(UUID id, String name, ProductType type, String description, long version) {
        Product product = load(id);
        product.requireNotRetired();
        if (product.getVersion() != version) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION, "The product was changed by another request");
        }
        ProductSnapshot before = ProductSnapshot.of(product);
        product.update(name, type, description);
        products.flush();
        audit(product.getInsurerId(), "PRODUCT.UPDATE", "Product", product.getId(), before, ProductSnapshot.of(product));
        return new ProductDetails(product, versions.findByProductIdOrderByVersionNoDesc(product.getId()));
    }

    /**
     * Retira o produto (irreversível); a versão em vigor passa a {@code RETIRED} com
     * {@code valid_to} = hoje. A versão {@code DRAFT} fica como está, mas não se publica.
     *
     * @throws DomainException {@code PRODUCT_RETIRED} (409)
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public ProductDetails retire(UUID id) {
        Product product = load(id);
        ProductSnapshot before = ProductSnapshot.of(product);
        Instant now = clock.instant();
        product.retire(now);
        versions.findByProductIdAndStatus(product.getId(), ProductVersionStatus.PUBLISHED)
                .ifPresent(v -> v.retire(LocalDate.ofInstant(now, ZONE)));
        products.flush();
        audit(product.getInsurerId(), "PRODUCT.RETIRE", "Product", product.getId(), before, ProductSnapshot.of(product));
        return new ProductDetails(product, versions.findByProductIdOrderByVersionNoDesc(product.getId()));
    }

    /** Produto da seguradora actual; {@code PRODUCT_NOT_FOUND} (404) se não existir ou não for visível. */
    public Product load(UUID id) {
        UUID insurerId = TenantContext.insurerId();
        return products.findById(id)
                .filter(p -> p.getInsurerId().equals(insurerId))
                .orElseThrow(() -> DomainException.of(ErrorCodes.PRODUCT_NOT_FOUND, "Product not found"));
    }

    private void audit(UUID insurerId, String action, String entityType, UUID entityId, Object before, Object after) {
        audit.record(new AuditRecord(ActorType.STAFF, currentUser.id().toString(), insurerId, action, entityType,
                entityId.toString(), before, after));
    }

    private static Specification<Product> filter(UUID insurerId, ProductStatus status, ProductType type, String q) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("insurerId"), insurerId));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase(Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%")
                        .replace("_", "\\_") + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("name")), like, '\\'),
                        cb.like(cb.lower(root.get("code")), like, '\\')));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    /** Estado auditado do produto. */
    public record ProductSnapshot(String code, String name, ProductType type, String description, ProductStatus status,
                                  Instant retiredAt) {

        static ProductSnapshot of(Product p) {
            return new ProductSnapshot(p.getCode(), p.getName(), p.getType(), p.getDescription(), p.getStatus(),
                    p.getRetiredAt());
        }
    }
}
