package mz.co.moseguros.vehicle.domain;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;

/**
 * Regras do pedido de condutor (S2-03 §2). {@link #validate} junta um erro por campo, com os
 * códigos de {@link FieldErrorCodes}, e devolve os dados já normalizados.
 */
public final class DriverRules {

    /** Fuso em que se conta "hoje". */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");
    public static final int MAX_AGE = 100;
    public static final int NAME_MIN = 2;
    public static final int NAME_MAX = 120;
    public static final int MAX_CATEGORIES = 12;
    /** Máximo de condutores {@code ACTIVE} por cliente. */
    public static final int MAX_ACTIVE_DRIVERS = 10;
    /** Finalidade do {@code BlindIndex} do número da carta. */
    public static final String LICENCE_HASH_PURPOSE = "licence";

    /** As mesmas regras do nome do perfil do cliente (S1-06): letras, espaços, apóstrofo e hífen. */
    private static final Pattern NAME = Pattern.compile("^[\\p{L}\\p{M}' -]+$");
    private static final Pattern HAS_LETTER = Pattern.compile("\\p{L}");
    private static final Pattern LICENCE_NUMBER = Pattern.compile("^[A-Z0-9/-]{5,20}$");
    private static final Pattern SPACES = Pattern.compile("\\s+");

    private DriverRules() {
    }

    /** Dados pedidos pelo cliente, ainda por validar. {@code licenceNumber} {@code null} no {@code PUT} mantém o actual. */
    public record DriverData(String fullName, LocalDate birthDate, String licenceNumber,
                             List<String> licenceCategories, LocalDate licenceIssueDate, Boolean isOwner) {

        /** Sem o número da carta, para nunca ir para um log. */
        @Override
        public String toString() {
            return "DriverData[licenceCategories=" + licenceCategories + ", isOwner=" + isOwner + "]";
        }
    }

    /** Dados válidos e normalizados; {@code licenceNumber} {@code null} = manter o actual. */
    public record ValidDriver(String fullName, LocalDate birthDate, String licenceNumber,
                              List<String> licenceCategories, LocalDate licenceIssueDate, boolean isOwner) {

        @Override
        public String toString() {
            return "ValidDriver[licenceCategories=" + licenceCategories + ", isOwner=" + isOwner + "]";
        }
    }

    /**
     * Valida e normaliza.
     *
     * @param licenceRequired {@code true} no {@code POST}; no {@code PUT} a falta mantém o número actual
     * @param today           data de hoje em {@link #ZONE}
     * @throws DomainException 400 {@code VALIDATION_ERROR} com um erro por campo em {@code errors}
     */
    public static ValidDriver validate(DriverData data, boolean licenceRequired, ReferenceValueLookup references,
                                       LocalDate today) {
        List<FieldError> errors = new ArrayList<>();

        String fullName = normalizeName(data.fullName());
        if (fullName == null) {
            errors.add(required("fullName"));
        } else if (!isValidName(fullName)) {
            errors.add(new FieldError("fullName", FieldErrorCodes.INVALID_NAME,
                    "Must have 2-120 letters, spaces, ' or -, and at least two words"));
        }

        LocalDate birthDate = data.birthDate();
        boolean birthDateValid = false;
        if (birthDate == null) {
            errors.add(required("birthDate"));
        } else if (!birthDate.isBefore(today) || Period.between(birthDate, today).getYears() > MAX_AGE) {
            errors.add(new FieldError("birthDate", FieldErrorCodes.INVALID_DATE,
                    "Must be in the past and at most " + MAX_AGE + " years ago"));
        } else {
            birthDateValid = true;
        }

        String licenceNumber = normalizeLicenceNumber(data.licenceNumber());
        if (licenceNumber == null) {
            if (licenceRequired) {
                errors.add(required("licenceNumber"));
            }
        } else if (!LICENCE_NUMBER.matcher(licenceNumber).matches()) {
            errors.add(new FieldError("licenceNumber", FieldErrorCodes.INVALID_LICENCE_NUMBER,
                    "Must be 5-20 upper-case letters, digits, / or -"));
        }

        List<String> categories = new ArrayList<>();
        List<String> rawCategories = data.licenceCategories();
        if (rawCategories == null || rawCategories.isEmpty()) {
            errors.add(required("licenceCategories"));
        } else if (rawCategories.size() > MAX_CATEGORIES) {
            errors.add(new FieldError("licenceCategories", FieldErrorCodes.OUT_OF_RANGE,
                    "Must have between 1 and " + MAX_CATEGORIES + " categories"));
        } else {
            Set<String> seen = new HashSet<>();
            for (int i = 0; i < rawCategories.size(); i++) {
                String field = "licenceCategories[" + i + "]";
                String code = rawCategories.get(i) == null ? "" : rawCategories.get(i).trim().toUpperCase(Locale.ROOT);
                if (!seen.add(code)) {
                    errors.add(new FieldError(field, FieldErrorCodes.DUPLICATED, "Repeated category"));
                } else if (code.isEmpty() || !references.isActive(ReferenceValueLookup.LICENCE_CATEGORY, code)) {
                    errors.add(new FieldError(field, FieldErrorCodes.LICENCE_CATEGORY_UNKNOWN,
                            "Unknown or inactive licence category"));
                }
                categories.add(code);
            }
        }

        LocalDate issueDate = data.licenceIssueDate();
        if (issueDate == null) {
            errors.add(required("licenceIssueDate"));
        } else if (issueDate.isAfter(today) || (birthDateValid && issueDate.isBefore(birthDate))) {
            errors.add(new FieldError("licenceIssueDate", FieldErrorCodes.LICENCE_ISSUE_DATE_INVALID,
                    "Must be on or after the birth date and not after today"));
        }

        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors);
        }
        return new ValidDriver(fullName, birthDate, licenceNumber, List.copyOf(categories), issueDate,
                Boolean.TRUE.equals(data.isOwner()));
    }

    /** Maiúsculas, sem espaços; vazio → {@code null}. */
    public static String normalizeLicenceNumber(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return SPACES.matcher(raw).replaceAll("").toUpperCase(Locale.ROOT);
    }

    /** Quatro {@code *} fixos e os últimos 4 caracteres (não revela o comprimento). */
    public static String mask(String last4) {
        return "****" + last4;
    }

    /** Últimos 4 caracteres do número normalizado. */
    public static String last4(String normalizedLicenceNumber) {
        return normalizedLicenceNumber.substring(Math.max(0, normalizedLicenceNumber.length() - 4));
    }

    /** Sem espaços nas pontas e com um só espaço entre palavras; vazio → {@code null}. */
    static String normalizeName(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return SPACES.matcher(raw.trim()).replaceAll(" ");
    }

    private static boolean isValidName(String name) {
        if (name.length() < NAME_MIN || name.length() > NAME_MAX || !NAME.matcher(name).matches()) {
            return false;
        }
        String[] words = name.split(" ");
        if (words.length < 2) {
            return false;
        }
        for (String word : words) {
            if (!HAS_LETTER.matcher(word).find()) {
                return false;
            }
        }
        return true;
    }

    private static FieldError required(String field) {
        return new FieldError(field, FieldErrorCodes.REQUIRED, "Required");
    }
}
