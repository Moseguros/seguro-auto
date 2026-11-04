package mz.co.moseguros.identity.domain;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;

/**
 * Regras do perfil do cliente (S1-06 §2). {@link #validate} devolve um erro por campo, com o
 * código de {@link FieldErrorCodes}; a lista vazia quer dizer que os dados podem ser gravados.
 */
public final class CustomerProfileRules {

    /** Fuso em que se conta a idade. */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");
    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 120;
    public static final int NAME_MIN = 2;
    public static final int NAME_MAX = 120;
    public static final int ADDRESS_MAX = 300;

    /** Letras (incluindo acentuadas), espaços, apóstrofo e hífen. */
    private static final Pattern NAME = Pattern.compile("^[\\p{L}\\p{M}' -]+$");
    private static final Pattern HAS_LETTER = Pattern.compile("\\p{L}");
    private static final Pattern DOCUMENT_NUMBER = Pattern.compile("^[A-Z0-9]{5,20}$");
    private static final Pattern SPACES = Pattern.compile("\\s+");

    private CustomerProfileRules() {
    }

    /**
     * Dados pedidos pelo cliente. {@code idDocNumber} e {@code nuit} {@code null} mantêm os
     * actuais.
     */
    public record Changes(String fullName, LocalDate birthDate, IdDocType idDocType, String idDocNumber,
                          String nuit, String address, Province province) {

        /** Sem o documento nem o NUIT, para nunca irem para um log. */
        @Override
        public String toString() {
            return "Changes[idDocType=" + idDocType + ", province=" + province + "]";
        }
    }

    /**
     * Valida as alterações contra o perfil actual.
     *
     * @param today data de hoje em {@link #ZONE}
     */
    public static List<FieldError> validate(CustomerProfile current, Changes changes, LocalDate today) {
        return validate(current.getIdDocType(), current.getIdDocNumber() != null, current.getNuit() != null, changes,
                today);
    }

    /**
     * Valida as alterações contra os dados actuais (o perfil, ou o tomador preenchido pelo mediador
     * numa proposta, S4-06).
     *
     * @param currentIdDocType tipo de documento actual ou {@code null}
     * @param hasIdDocNumber   já existe um número de documento (omitido mantém-no)
     * @param hasNuit          já existe um NUIT (omitido mantém-no)
     * @param today            data de hoje em {@link #ZONE}
     */
    public static List<FieldError> validate(IdDocType currentIdDocType, boolean hasIdDocNumber, boolean hasNuit,
                                            Changes changes, LocalDate today) {
        List<FieldError> errors = new ArrayList<>();

        String fullName = normalizeName(changes.fullName());
        if (fullName == null) {
            errors.add(required("fullName"));
        } else if (!isValidName(fullName)) {
            errors.add(new FieldError("fullName", FieldErrorCodes.INVALID_NAME,
                    "Must have 2-120 letters, spaces, ' or -, and at least two words"));
        }

        LocalDate birthDate = changes.birthDate();
        if (birthDate == null) {
            errors.add(required("birthDate"));
        } else if (birthDate.isAfter(today) || Period.between(birthDate, today).getYears() < MIN_AGE) {
            errors.add(new FieldError("birthDate", FieldErrorCodes.UNDERAGE, "Must be at least " + MIN_AGE));
        } else if (Period.between(birthDate, today).getYears() > MAX_AGE) {
            errors.add(new FieldError("birthDate", FieldErrorCodes.INVALID_DATE, "Must be at most " + MAX_AGE));
        }

        if (changes.idDocType() == null) {
            errors.add(required("idDocType"));
        }

        String idDocNumber = normalizeDocumentNumber(changes.idDocNumber());
        if (idDocNumber == null) {
            boolean typeChanged = currentIdDocType != null && changes.idDocType() != null
                    && currentIdDocType != changes.idDocType();
            if (!hasIdDocNumber || typeChanged) {
                errors.add(required("idDocNumber"));
            }
        } else if (!DOCUMENT_NUMBER.matcher(idDocNumber).matches()) {
            errors.add(new FieldError("idDocNumber", FieldErrorCodes.INVALID_DOCUMENT_NUMBER,
                    "Must be 5-20 upper-case letters or digits"));
        }

        if (isBlank(changes.nuit())) {
            if (!hasNuit) {
                errors.add(required("nuit"));
            }
        } else {
            try {
                Nuit.of(changes.nuit());
            } catch (DomainException e) {
                errors.add(new FieldError("nuit", FieldErrorCodes.INVALID_NUIT, e.getMessage()));
            }
        }

        if (changes.address() != null && changes.address().trim().length() > ADDRESS_MAX) {
            errors.add(new FieldError("address", FieldErrorCodes.SIZE, "Must have at most " + ADDRESS_MAX
                    + " characters"));
        }

        if (changes.province() == null) {
            errors.add(required("province"));
        }
        return errors;
    }

    /** Sem espaços nas pontas e com um só espaço entre palavras; vazio → {@code null}. */
    public static String normalizeName(String raw) {
        if (isBlank(raw)) {
            return null;
        }
        return SPACES.matcher(raw.trim()).replaceAll(" ");
    }

    /** Maiúsculas, sem espaços; vazio → {@code null}. */
    public static String normalizeDocumentNumber(String raw) {
        if (isBlank(raw)) {
            return null;
        }
        return SPACES.matcher(raw).replaceAll("").toUpperCase(Locale.ROOT);
    }

    /** Todos os caracteres menos os últimos 4 trocados por {@code *}. */
    public static String maskDocumentNumber(String value) {
        if (value == null) {
            return null;
        }
        int keep = Math.min(4, value.length());
        return "*".repeat(value.length() - keep) + value.substring(value.length() - keep);
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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
