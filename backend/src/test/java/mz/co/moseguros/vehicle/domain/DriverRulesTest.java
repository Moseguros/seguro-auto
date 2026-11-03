package mz.co.moseguros.vehicle.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import mz.co.moseguros.shared.reference.ReferenceValueLookup;
import mz.co.moseguros.vehicle.domain.DriverRules.DriverData;
import mz.co.moseguros.vehicle.domain.DriverRules.ValidDriver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Cada linha da tabela do pedido de condutor (S2-03 §2). */
class DriverRulesTest {

    static final LocalDate TODAY = LocalDate.of(2026, 12, 3);
    /** Categorias activas; {@code C1} existe mas está desactivada. */
    static final Set<String> ACTIVE = Set.of("A", "A1", "B", "C", "D", "D1", "BE", "CE", "DE");
    static final ReferenceValueLookup LOOKUP = (domain, code) ->
            ReferenceValueLookup.LICENCE_CATEGORY.equals(domain) && ACTIVE.contains(code);

    static DriverData valid() {
        return new DriverData("Ana Maria Mussa", LocalDate.of(1990, 5, 14), "MAP-1234567", List.of("B"),
                LocalDate.of(2010, 3, 1), null);
    }

    private static DriverData with(String fullName, LocalDate birthDate, String licenceNumber, List<String> categories,
                                   LocalDate issueDate) {
        return new DriverData(fullName, birthDate, licenceNumber, categories, issueDate, null);
    }

    private static List<String> codes(DriverData data, boolean licenceRequired) {
        try {
            DriverRules.validate(data, licenceRequired, LOOKUP, TODAY);
        } catch (DomainException e) {
            assertThat(e.getCode()).isEqualTo("VALIDATION_ERROR");
            @SuppressWarnings("unchecked")
            List<FieldError> errors = (List<FieldError>) e.getProperties().get("errors");
            return errors.stream().map(f -> f.field() + "/" + f.code()).toList();
        }
        throw new AssertionError("expected VALIDATION_ERROR");
    }

    private static List<String> codes(DriverData data) {
        return codes(data, true);
    }

    @Test
    void validRequestIsNormalised() {
        ValidDriver v = DriverRules.validate(new DriverData("  Ana   Maria  Mussa ", LocalDate.of(1990, 5, 14),
                " map 1234/567 ", List.of(" b ", "a1"), LocalDate.of(2010, 3, 1), true), true, LOOKUP, TODAY);
        assertThat(v.fullName()).isEqualTo("Ana Maria Mussa");
        assertThat(v.licenceNumber()).isEqualTo("MAP1234/567");
        assertThat(v.licenceCategories()).containsExactly("B", "A1");
        assertThat(v.isOwner()).isTrue();
        assertThat(DriverRules.validate(valid(), true, LOOKUP, TODAY).isOwner()).isFalse();
    }

    @Test
    void everyRequiredFieldMissingIsReportedTogether() {
        assertThat(codes(new DriverData(" ", null, null, null, null, null))).containsExactly("fullName/REQUIRED",
                "birthDate/REQUIRED", "licenceNumber/REQUIRED", "licenceCategories/REQUIRED",
                "licenceIssueDate/REQUIRED");
        assertThat(codes(with("Ana Mussa", LocalDate.of(1990, 1, 1), "MAP12345", List.of(), LocalDate.of(2010, 1, 1))))
                .containsExactly("licenceCategories/REQUIRED");
    }

    @ParameterizedTest
    @CsvSource({"Ana", "Ana 123", "Ana_Maria Mussa", "Ana -"})
    void fullNameFollowsTheProfileRules(String name) {
        assertThat(codes(with(name, LocalDate.of(1990, 5, 14), "MAP1234567", List.of("B"), LocalDate.of(2010, 3, 1))))
                .containsExactly("fullName/INVALID_NAME");
    }

    @Test
    void fullNameAcceptsAccentsApostropheAndHyphen() {
        assertThat(DriverRules.validate(with("João D'Ávila-Muianga", LocalDate.of(1990, 5, 14), "MAP1234567",
                List.of("B"), LocalDate.of(2010, 3, 1)), true, LOOKUP, TODAY).fullName())
                .isEqualTo("João D'Ávila-Muianga");
        assertThat(codes(with("A".repeat(60) + " " + "B".repeat(60), LocalDate.of(1990, 5, 14), "MAP1234567",
                List.of("B"), LocalDate.of(2010, 3, 1)))).containsExactly("fullName/INVALID_NAME");
    }

    @Test
    void birthDateInThePastAndAtMost100YearsAgo() {
        assertThat(codes(with("Ana Mussa", TODAY, "MAP1234567", List.of("B"), TODAY)))
                .containsExactly("birthDate/INVALID_DATE");
        assertThat(codes(with("Ana Mussa", TODAY.plusDays(1), "MAP1234567", List.of("B"), TODAY)))
                .containsExactly("birthDate/INVALID_DATE");
        assertThat(codes(with("Ana Mussa", TODAY.minusYears(101), "MAP1234567", List.of("B"), TODAY)))
                .containsExactly("birthDate/INVALID_DATE");
        LocalDate hundred = TODAY.minusYears(100);
        assertThat(DriverRules.validate(with("Ana Mussa", hundred, "MAP1234567", List.of("B"), TODAY), true, LOOKUP,
                TODAY).birthDate()).isEqualTo(hundred);
    }

    @ParameterizedTest
    @CsvSource({"ABCD", "ABCDEFGHIJKLMNOPQRSTU", "MAP_12345", "MAP.12345"})
    void licenceNumberInvalid(String number) {
        assertThat(codes(with("Ana Mussa", LocalDate.of(1990, 5, 14), number, List.of("B"), LocalDate.of(2010, 3, 1))))
                .containsExactly("licenceNumber/INVALID_LICENCE_NUMBER");
    }

    @Test
    void licenceNumberLimitsAndOptionalOnUpdate() {
        assertThat(DriverRules.validate(with("Ana Mussa", LocalDate.of(1990, 5, 14), "AB/-1", List.of("B"),
                LocalDate.of(2010, 3, 1)), true, LOOKUP, TODAY).licenceNumber()).isEqualTo("AB/-1");
        assertThat(DriverRules.validate(with("Ana Mussa", LocalDate.of(1990, 5, 14), "A".repeat(20), List.of("B"),
                LocalDate.of(2010, 3, 1)), true, LOOKUP, TODAY).licenceNumber()).hasSize(20);
        ValidDriver keep = DriverRules.validate(with("Ana Mussa", LocalDate.of(1990, 5, 14), null, List.of("B"),
                LocalDate.of(2010, 3, 1)), false, LOOKUP, TODAY);
        assertThat(keep.licenceNumber()).isNull();
    }

    @Test
    void categoriesMustBeActiveAndNotRepeated() {
        assertThat(codes(with("Ana Mussa", LocalDate.of(1990, 5, 14), "MAP1234567", List.of("B", "XYZ", "C1"),
                LocalDate.of(2010, 3, 1)))).containsExactly("licenceCategories[1]/LICENCE_CATEGORY_UNKNOWN",
                "licenceCategories[2]/LICENCE_CATEGORY_UNKNOWN");
        assertThat(codes(with("Ana Mussa", LocalDate.of(1990, 5, 14), "MAP1234567", List.of("B", "b"),
                LocalDate.of(2010, 3, 1)))).containsExactly("licenceCategories[1]/DUPLICATED");
        assertThat(codes(with("Ana Mussa", LocalDate.of(1990, 5, 14), "MAP1234567", Arrays.asList("B", null),
                LocalDate.of(2010, 3, 1)))).containsExactly("licenceCategories[1]/LICENCE_CATEGORY_UNKNOWN");
    }

    @Test
    void oneToTwelveCategories() {
        List<String> twelve = Collections.nCopies(12, "B");
        assertThat(codes(with("Ana Mussa", LocalDate.of(1990, 5, 14), "MAP1234567",
                Collections.nCopies(13, "B"), LocalDate.of(2010, 3, 1)))).containsExactly("licenceCategories/OUT_OF_RANGE");
        // 12 é o máximo (aqui todos repetidos, por isso só DUPLICATED, nunca OUT_OF_RANGE).
        assertThat(codes(with("Ana Mussa", LocalDate.of(1990, 5, 14), "MAP1234567", twelve,
                LocalDate.of(2010, 3, 1)))).doesNotContain("licenceCategories/OUT_OF_RANGE");
    }

    @Test
    void issueDateBetweenBirthAndToday() {
        LocalDate birth = LocalDate.of(1990, 5, 14);
        assertThat(codes(with("Ana Mussa", birth, "MAP1234567", List.of("B"), birth.minusDays(1))))
                .containsExactly("licenceIssueDate/LICENCE_ISSUE_DATE_INVALID");
        assertThat(codes(with("Ana Mussa", birth, "MAP1234567", List.of("B"), TODAY.plusDays(1))))
                .containsExactly("licenceIssueDate/LICENCE_ISSUE_DATE_INVALID");
        assertThat(DriverRules.validate(with("Ana Mussa", birth, "MAP1234567", List.of("B"), birth), true, LOOKUP,
                TODAY).licenceIssueDate()).isEqualTo(birth);
        assertThat(DriverRules.validate(with("Ana Mussa", birth, "MAP1234567", List.of("B"), TODAY), true, LOOKUP,
                TODAY).licenceIssueDate()).isEqualTo(TODAY);
    }

    @Test
    void maskHidesTheLength() {
        assertThat(DriverRules.mask(DriverRules.last4("MAP1234567"))).isEqualTo("****4567");
        assertThat(DriverRules.mask(DriverRules.last4("AB/-1"))).isEqualTo("****B/-1");
        assertThat(valid().toString()).doesNotContain("MAP-1234567");
    }
}
