package mz.co.moseguros.rating.domain.tariff;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.ProductVersionView;

/** Tipo de produto e coberturas de uma versão, pela ordem da versão. */
public record ProductDefinition(ProductType type, List<CoverageDefinition> coverages) {

    public ProductDefinition {
        Objects.requireNonNull(type, "type");
        coverages = List.copyOf(coverages);
    }

    public static ProductDefinition from(ProductVersionView version) {
        return new ProductDefinition(version.productType(),
                version.coverages().stream().map(CoverageDefinition::from).toList());
    }

    public Optional<CoverageDefinition> coverage(CoverageCode code) {
        return coverages.stream().filter(c -> c.code() == code).findFirst();
    }
}
