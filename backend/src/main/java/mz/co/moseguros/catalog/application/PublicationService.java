package mz.co.moseguros.catalog.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.ProductVersionPublished;
import mz.co.moseguros.catalog.application.ProductVersionService.Editing;
import mz.co.moseguros.catalog.application.ProductVersionService.VersionDetails;
import mz.co.moseguros.catalog.application.ProductVersionService.VersionSnapshot;
import mz.co.moseguros.catalog.domain.Coverage;
import mz.co.moseguros.catalog.domain.Product;
import mz.co.moseguros.catalog.domain.ProductVersion;
import mz.co.moseguros.catalog.domain.ProductVersionRepository;
import mz.co.moseguros.catalog.domain.ProductVersionStatus;
import mz.co.moseguros.catalog.domain.PublicationRules;
import mz.co.moseguros.catalog.domain.PublicationRules.Candidate;
import mz.co.moseguros.catalog.domain.PublicationRules.DocumentKey;
import mz.co.moseguros.shared.domain.Ids;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Validação e publicação de versões (S2-04 §4 e §5). As regras são as de {@link PublicationRules},
 * com o {@code MTPL_MIN_CAPITAL} lido no momento ({@link CatalogApi#platformSetting}). Publicar
 * substitui a versão em vigor, publica o produto na primeira vez e emite
 * {@link ProductVersionPublished}, tudo numa transacção.
 */
@Service
public class PublicationService {

    static final String MTPL_MIN_CAPITAL = "MTPL_MIN_CAPITAL";

    private final ProductVersionService versionService;
    private final ProductService productService;
    private final ProductVersionRepository versions;
    private final CatalogApi catalogApi;
    private final ApplicationEventPublisher events;
    private final CurrentUser currentUser;
    private final Clock clock;

    public PublicationService(ProductVersionService versionService, ProductService productService,
                              ProductVersionRepository versions, CatalogApi catalogApi,
                              ApplicationEventPublisher events, CurrentUser currentUser, Clock clock) {
        this.versionService = versionService;
        this.productService = productService;
        this.versions = versions;
        this.catalogApi = catalogApi;
        this.events = events;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    /** As falhas das regras de publicação, sem gravar nada (vazia = publicável). */
    @PreAuthorize("hasAuthority('product:read')")
    @Transactional(readOnly = true)
    public List<FieldError> validate(UUID versionId) {
        ProductVersion version = versionService.load(versionId);
        return evaluate(versionService.details(version, productService.load(version.getProductId())));
    }

    /**
     * Publica a versão {@code DRAFT}: em vigor desde hoje ({@code Africa/Maputo}); a anterior em
     * vigor passa a {@code SUPERSEDED} com {@code valid_to} = hoje; o produto {@code DRAFT} passa a
     * {@code PUBLISHED}.
     *
     * @throws DomainException 409 {@code PRODUCT_VERSION_NOT_EDITABLE}, {@code PRODUCT_RETIRED},
     *                         {@code CONCURRENT_MODIFICATION}; 400 {@code PRODUCT_VERSION_NOT_PUBLISHABLE}
     *                         com todas as falhas em {@code errors[]}
     */
    @PreAuthorize("hasAuthority('product:manage')")
    @Transactional
    public VersionDetails publish(UUID versionId, long expectedVersion) {
        Editing editing = versionService.beginEdit(versionId, expectedVersion, false);
        ProductVersion version = editing.version();
        Product product = editing.product();
        List<FieldError> errors = evaluate(versionService.details(version, product));
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.PRODUCT_VERSION_NOT_PUBLISHABLE,
                    "The version does not follow the publication rules").withProperty("errors", errors);
        }
        VersionSnapshot before = VersionSnapshot.of(version);
        Instant now = clock.instant();
        LocalDate today = LocalDate.ofInstant(now, ProductService.ZONE);
        versions.findByProductIdAndStatus(product.getId(), ProductVersionStatus.PUBLISHED)
                .ifPresent(previous -> previous.supersede(today));
        versions.flush(); // a anterior deixa de estar PUBLISHED antes de esta o ficar (ux_version_one_published)
        version.publish(today, now, currentUser.id().toString());
        product.markPublished();
        versions.flush();
        events.publishEvent(new ProductVersionPublished(Ids.newId(), now, version.getInsurerId(), product.getId(),
                version.getId(), version.getVersionNo(), product.getType()));
        versionService.audit(version, "PRODUCT_VERSION.PUBLISH", before, VersionSnapshot.of(version));
        return versionService.details(version, product);
    }

    private List<FieldError> evaluate(VersionDetails details) {
        long minCapital = Long.parseLong(catalogApi.platformSetting(MTPL_MIN_CAPITAL));
        Candidate candidate = new Candidate(details.product().getType(),
                details.coverages().stream().map(Coverage::toSpec).toList(),
                details.documents().stream().map(d -> new DocumentKey(d.getDocType(), d.getLanguage()))
                        .collect(Collectors.toSet()),
                details.version().getVehicleCategories(), details.version().getUsages());
        return PublicationRules.evaluate(candidate, minCapital);
    }
}
