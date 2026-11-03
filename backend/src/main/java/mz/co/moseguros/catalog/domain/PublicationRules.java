package mz.co.moseguros.catalog.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;

/**
 * Regras de publicação de uma versão (S2-04 §4). Todas as regras são avaliadas e todas as falhas
 * vão juntas na lista devolvida (vazia = publicável). As coberturas já passaram por
 * {@link CoverageRules} ao gravar. {@code minCapital} ({@code M}) é o {@code MTPL_MIN_CAPITAL}
 * lido no momento da publicação, em centavos.
 */
public final class PublicationRules {

    /** Tipos que não admitem coberturas de danos próprios (regra 5). */
    static final Set<ProductType> NO_OWN_DAMAGE = EnumSet.of(ProductType.MTPL_ONLY, ProductType.TEMPORARY,
            ProductType.BORDER);
    /** Coberturas que o furto ou roubo exige (regra 8). */
    static final Set<CoverageCode> THEFT_REQUIRES = EnumSet.of(CoverageCode.OD_COLLISION, CoverageCode.OD_FIRE,
            CoverageCode.OD_NATURE);
    static final String PT = "pt";

    private PublicationRules() {
    }

    /** Documento presente na versão. */
    public record DocumentKey(ProductDocumentType docType, String language) {
    }

    /** Dados da versão que as regras avaliam. */
    public record Candidate(ProductType productType, List<CoverageSpec> coverages, Collection<DocumentKey> documents,
                            Collection<?> vehicleCategories, Collection<?> usages) {
    }

    public static List<FieldError> evaluate(Candidate v, long minCapital) {
        List<FieldError> errors = new ArrayList<>();
        List<CoverageSpec> coverages = v.coverages();
        Set<CoverageCode> codes = coverages.stream().map(CoverageSpec::code)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(CoverageCode.class)));

        // 1-3: RC obrigatória
        int mtpl = indexOf(coverages, CoverageCode.MTPL);
        if (mtpl < 0) {
            errors.add(error("coverages", FieldErrorCodes.MTPL_REQUIRED));
        } else {
            CoverageSpec c = coverages.get(mtpl);
            if (!Boolean.TRUE.equals(c.mandatory())) {
                errors.add(error(field(mtpl, "mandatory"), FieldErrorCodes.MTPL_MUST_BE_MANDATORY));
            }
            if (c.capitalType() != CapitalType.FIXED) {
                errors.add(error(field(mtpl, "capitalType"), FieldErrorCodes.INVALID_CAPITAL_TYPE));
            } else if (c.capital() == null || c.capital().amount() < minCapital) {
                errors.add(error(field(mtpl, "capital"), FieldErrorCodes.MTPL_CAPITAL_BELOW_MINIMUM));
            }
        }

        // 4: fronteira só com MTPL
        if (v.productType() == ProductType.BORDER && codes.stream().anyMatch(code -> code != CoverageCode.MTPL)) {
            errors.add(error("coverages", FieldErrorCodes.BORDER_ONLY_MTPL));
        }

        for (int i = 0; i < coverages.size(); i++) {
            CoverageSpec c = coverages.get(i);
            if (c.code().isOwnDamage()) {
                // 5: tipos sem danos próprios
                if (NO_OWN_DAMAGE.contains(v.productType())) {
                    errors.add(error(field(i, "code"), FieldErrorCodes.OD_NOT_ALLOWED_FOR_TYPE));
                }
                // 7: danos próprios sobre o valor do veículo
                if (c.capitalType() != CapitalType.VEHICLE_VALUE) {
                    errors.add(error(field(i, "capitalType"), FieldErrorCodes.OD_CAPITAL_MUST_BE_VEHICLE_VALUE));
                }
            }
            // 8: furto ou roubo
            if (c.code() == CoverageCode.OD_THEFT && !codes.containsAll(THEFT_REQUIRES)) {
                errors.add(error(field(i, "code"), FieldErrorCodes.OD_THEFT_REQUIRES_OD_COVERAGES));
            }
            // 9: coberturas exigidas presentes
            if (!codes.containsAll(c.requiresOrEmpty())) {
                errors.add(error(field(i, "requiresCodes"), FieldErrorCodes.REQUIRED_COVERAGE_MISSING));
            }
            // 10: RC facultativa acima do mínimo
            if (c.code() == CoverageCode.VTPL) {
                vtpl(i, c, minCapital, errors);
            }
            // 11: cobertura além-fronteiras
            if (c.code() == CoverageCode.CROSS_BORDER
                    && c.scopeOrEmpty().stream().noneMatch(country -> !CoverageRules.MOZAMBIQUE.equals(country))) {
                errors.add(error(field(i, "territorialScope"), FieldErrorCodes.CROSS_BORDER_SCOPE_REQUIRED));
            }
        }

        // 6: danos próprios precisa de pelo menos uma OD
        if (v.productType() == ProductType.COMPREHENSIVE && codes.stream().noneMatch(CoverageCode::isOwnDamage)) {
            errors.add(error("coverages", FieldErrorCodes.OD_REQUIRED_FOR_COMPREHENSIVE));
        }

        // 12-13: documentos em pt
        for (ProductDocumentType type : List.of(ProductDocumentType.GENERAL_CONDITIONS,
                ProductDocumentType.PRE_CONTRACT_INFO)) {
            if (!v.documents().contains(new DocumentKey(type, PT))) {
                errors.add(error("documents." + type.name(), FieldErrorCodes.DOCUMENT_REQUIRED));
            }
        }

        // 14-15: elegibilidade
        if (v.vehicleCategories().isEmpty()) {
            errors.add(error("vehicleCategories", FieldErrorCodes.REQUIRED));
        }
        if (v.usages().isEmpty()) {
            errors.add(error("usages", FieldErrorCodes.REQUIRED));
        }
        return errors;
    }

    private static void vtpl(int i, CoverageSpec c, long minCapital, List<FieldError> errors) {
        if (c.capitalType() == CapitalType.FIXED) {
            if (c.capital() == null || c.capital().amount() <= minCapital) {
                errors.add(error(field(i, "capital"), FieldErrorCodes.VTPL_CAPITAL_TOO_LOW));
            }
        } else if (c.capitalType() == CapitalType.OPTIONS) {
            List<Money> options = c.capitalOptions() == null ? List.of() : c.capitalOptions();
            if (options.isEmpty() || options.stream().anyMatch(o -> o.amount() <= minCapital)) {
                errors.add(error(field(i, "capitalOptions"), FieldErrorCodes.VTPL_CAPITAL_TOO_LOW));
            }
        } else {
            errors.add(error(field(i, "capitalType"), FieldErrorCodes.INVALID_CAPITAL_TYPE));
        }
    }

    private static int indexOf(List<CoverageSpec> coverages, CoverageCode code) {
        for (int i = 0; i < coverages.size(); i++) {
            if (coverages.get(i).code() == code) {
                return i;
            }
        }
        return -1;
    }

    private static String field(int index, String name) {
        return "coverages[" + index + "]." + name;
    }

    private static FieldError error(String field, String code) {
        return new FieldError(field, code, "Publication rule failed: " + code);
    }
}
