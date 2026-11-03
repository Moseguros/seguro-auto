package mz.co.moseguros.catalog.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.catalog.ProductDocumentView;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.catalog.domain.Coverage;
import mz.co.moseguros.catalog.domain.CoverageSpec;
import mz.co.moseguros.catalog.domain.Product;
import mz.co.moseguros.catalog.domain.ProductDocument;
import mz.co.moseguros.catalog.domain.ProductVersion;

/** Converte as entidades do catálogo nas vistas públicas do módulo ({@code ProductVersionView}…). */
public final class CatalogViews {

    private CatalogViews() {
    }

    public static ProductVersionView version(ProductVersion v, Product p, List<Coverage> coverages,
                                             List<ProductDocument> documents) {
        List<ProductDocumentView> docs = documents.stream().map(CatalogViews::document).toList();
        return new ProductVersionView(v.getId(), p.getId(), p.getCode(), p.getName(), p.getType(), v.getInsurerId(),
                v.getVersionNo(), v.getStatus().name(), v.getValidFrom(), v.getValidTo(), v.getVehicleCategories(),
                v.getUsages(), coverages.stream().map(CatalogViews::coverage).toList(), docs,
                mainFile(docs, ProductDocumentType.GENERAL_CONDITIONS),
                mainFile(docs, ProductDocumentType.PRE_CONTRACT_INFO), v.getRules(), v.getPublishedAt(),
                v.getVersion());
    }

    public static CoverageView coverage(Coverage c) {
        CoverageSpec s = c.toSpec();
        return new CoverageView(s.code(), Boolean.TRUE.equals(s.mandatory()), s.requiresOrEmpty(), s.capitalType(),
                s.capital(), s.capitalOptions(), s.deductibleType(), s.deductibleAmount(), s.deductiblePercent(),
                s.deductibleMinAmount(), s.scopeOrEmpty(), s.description(), s.exclusions());
    }

    public static ProductDocumentView document(ProductDocument d) {
        return new ProductDocumentView(d.getId(), d.getDocType(), d.getLanguage(), d.getTitle(), d.getFileId());
    }

    private static UUID mainFile(List<ProductDocumentView> docs, ProductDocumentType type) {
        return docs.stream()
                .filter(d -> d.docType() == type && Objects.equals(d.language(), "pt"))
                .map(ProductDocumentView::fileId)
                .findFirst()
                .orElse(null);
    }
}
