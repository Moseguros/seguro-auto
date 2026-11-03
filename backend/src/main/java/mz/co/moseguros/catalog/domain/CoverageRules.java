package mz.co.moseguros.catalog.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;

/**
 * Validação das coberturas ao gravar ({@code replaceProductVersionCoverages}, S2-04 §3, tabela
 * {@code CoverageRequest}). Devolve no máximo um erro por campo, com
 * {@code field = "coverages[i].<campo>"}; a lista vazia quer dizer que se pode gravar. As regras
 * que olham para a versão inteira (existência dos códigos exigidos, capital mínimo…) são de
 * {@link PublicationRules}.
 */
public final class CoverageRules {

    public static final int MAX_COVERAGES = 12;
    public static final int MIN_OPTIONS = 2;
    public static final int MAX_OPTIONS = 10;
    public static final int MAX_SCOPE = 20;
    public static final int DESCRIPTION_MAX = 1000;
    public static final int EXCLUSIONS_MAX = 4000;
    public static final String MOZAMBIQUE = "MZ";
    private static final Pattern COUNTRY = Pattern.compile("^[A-Z]{2}$");
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private CoverageRules() {
    }

    /**
     * @throws DomainException 400 {@code VALIDATION_ERROR} com os erros em {@code errors[]}
     */
    public static void require(List<CoverageSpec> coverages) {
        List<FieldError> errors = validate(coverages);
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
    }

    public static List<FieldError> validate(List<CoverageSpec> coverages) {
        List<FieldError> errors = new ArrayList<>();
        if (coverages == null) {
            errors.add(error("coverages", FieldErrorCodes.REQUIRED, "The list of coverages is required"));
            return errors;
        }
        if (coverages.size() > MAX_COVERAGES) {
            errors.add(error("coverages", FieldErrorCodes.SIZE, "At most " + MAX_COVERAGES + " coverages"));
            return errors;
        }
        Set<CoverageCode> seen = new HashSet<>();
        for (int i = 0; i < coverages.size(); i++) {
            String prefix = "coverages[" + i + "]";
            CoverageSpec c = coverages.get(i);
            if (c == null) {
                errors.add(error(prefix, FieldErrorCodes.REQUIRED, "Coverage is required"));
                continue;
            }
            validateOne(prefix + ".", c, seen, errors);
        }
        return errors;
    }

    private static void validateOne(String p, CoverageSpec c, Set<CoverageCode> seen, List<FieldError> errors) {
        if (c.code() == null) {
            errors.add(error(p + "code", FieldErrorCodes.REQUIRED, "Code is required"));
        } else if (!seen.add(c.code())) {
            errors.add(error(p + "code", FieldErrorCodes.DUPLICATED, "Coverage code repeated in the list"));
        }

        if (c.mandatory() == null) {
            errors.add(error(p + "mandatory", FieldErrorCodes.REQUIRED, "mandatory is required"));
        }

        List<CoverageCode> requires = c.requiresOrEmpty();
        if (requires.stream().anyMatch(Objects::isNull) || new HashSet<>(requires).size() != requires.size()) {
            errors.add(error(p + "requiresCodes", FieldErrorCodes.DUPLICATED, "Repeated or empty required codes"));
        } else if (c.code() != null && requires.contains(c.code())) {
            errors.add(error(p + "requiresCodes", FieldErrorCodes.INVALID_REQUIREMENT,
                    "A coverage cannot require itself"));
        }

        validateCapital(p, c, errors);
        validateDeductible(p, c, errors);
        validateScope(p, c.territorialScope(), errors);

        if (c.description() != null && c.description().trim().length() > DESCRIPTION_MAX) {
            errors.add(error(p + "description", FieldErrorCodes.SIZE, "At most " + DESCRIPTION_MAX + " characters"));
        }
        if (c.exclusions() != null && c.exclusions().trim().length() > EXCLUSIONS_MAX) {
            errors.add(error(p + "exclusions", FieldErrorCodes.SIZE, "At most " + EXCLUSIONS_MAX + " characters"));
        }
    }

    private static void validateCapital(String p, CoverageSpec c, List<FieldError> errors) {
        CapitalType type = c.capitalType();
        if (type == null) {
            errors.add(error(p + "capitalType", FieldErrorCodes.REQUIRED, "capitalType is required"));
            return;
        }
        boolean amountType = type == CapitalType.FIXED || type == CapitalType.PER_PERSON;
        if (amountType) {
            positive(p + "capital", c.capital(), true, errors);
        } else if (c.capital() != null) {
            errors.add(error(p + "capital", FieldErrorCodes.NOT_ALLOWED, "capital is only for FIXED or PER_PERSON"));
        }

        List<Money> options = c.capitalOptions();
        if (type == CapitalType.OPTIONS) {
            if (options == null) {
                errors.add(error(p + "capitalOptions", FieldErrorCodes.REQUIRED, "capitalOptions is required"));
            } else if (!validOptions(options)) {
                errors.add(error(p + "capitalOptions", FieldErrorCodes.CAPITAL_OPTIONS_INVALID,
                        "2 to 10 positive values in strictly increasing order"));
            }
        } else if (options != null && !options.isEmpty()) {
            errors.add(error(p + "capitalOptions", FieldErrorCodes.NOT_ALLOWED, "capitalOptions is only for OPTIONS"));
        }
    }

    static boolean validOptions(List<Money> options) {
        if (options.size() < MIN_OPTIONS || options.size() > MAX_OPTIONS) {
            return false;
        }
        long previous = 0;
        for (Money option : options) {
            if (option == null || option.amount() <= previous) {
                return false;
            }
            previous = option.amount();
        }
        return true;
    }

    private static void validateDeductible(String p, CoverageSpec c, List<FieldError> errors) {
        DeductibleType type = c.deductibleType();
        if (type == null) {
            errors.add(error(p + "deductibleType", FieldErrorCodes.REQUIRED, "deductibleType is required"));
            return;
        }
        if (type == DeductibleType.FIXED) {
            positive(p + "deductibleAmount", c.deductibleAmount(), true, errors);
        } else if (c.deductibleAmount() != null) {
            errors.add(error(p + "deductibleAmount", FieldErrorCodes.NOT_ALLOWED, "deductibleAmount is only for FIXED"));
        }

        if (type == DeductibleType.PERCENT) {
            BigDecimal percent = c.deductiblePercent();
            if (percent == null) {
                errors.add(error(p + "deductiblePercent", FieldErrorCodes.REQUIRED, "deductiblePercent is required"));
            } else if (percent.signum() <= 0 || percent.compareTo(HUNDRED) > 0
                    || percent.stripTrailingZeros().scale() > 2) {
                errors.add(error(p + "deductiblePercent", FieldErrorCodes.OUT_OF_RANGE,
                        "Must be > 0 and <= 100, with at most 2 decimal places"));
            }
            positive(p + "deductibleMinAmount", c.deductibleMinAmount(), false, errors);
        } else {
            if (c.deductiblePercent() != null) {
                errors.add(error(p + "deductiblePercent", FieldErrorCodes.NOT_ALLOWED,
                        "deductiblePercent is only for PERCENT"));
            }
            if (c.deductibleMinAmount() != null) {
                errors.add(error(p + "deductibleMinAmount", FieldErrorCodes.NOT_ALLOWED,
                        "deductibleMinAmount is only for PERCENT"));
            }
        }
    }

    private static void validateScope(String p, List<String> scope, List<FieldError> errors) {
        String field = p + "territorialScope";
        if (scope == null) {
            errors.add(error(field, FieldErrorCodes.REQUIRED, "territorialScope is required"));
        } else if (scope.isEmpty() || scope.size() > MAX_SCOPE) {
            errors.add(error(field, FieldErrorCodes.SIZE, "1 to " + MAX_SCOPE + " countries"));
        } else if (scope.stream().anyMatch(code -> code == null || !COUNTRY.matcher(code).matches())) {
            errors.add(error(field, FieldErrorCodes.INVALID_COUNTRY_CODE, "Country codes are ISO 3166-1 alpha-2"));
        } else if (new HashSet<>(scope).size() != scope.size()) {
            errors.add(error(field, FieldErrorCodes.DUPLICATED, "Repeated country code"));
        } else if (!scope.contains(MOZAMBIQUE)) {
            errors.add(error(field, FieldErrorCodes.MZ_SCOPE_REQUIRED, "The scope must include MZ"));
        }
    }

    /** Obrigatório (ou opcional) e, se presente, maior que zero. */
    private static void positive(String field, Money value, boolean required, List<FieldError> errors) {
        if (value == null) {
            if (required) {
                errors.add(error(field, FieldErrorCodes.REQUIRED, field + " is required"));
            }
        } else if (value.amount() <= 0) {
            errors.add(error(field, FieldErrorCodes.OUT_OF_RANGE, "Must be greater than zero"));
        }
    }

    static FieldError error(String field, String code, String message) {
        return new FieldError(field, code, message);
    }
}
