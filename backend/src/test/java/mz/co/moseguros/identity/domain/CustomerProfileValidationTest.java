package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import mz.co.moseguros.identity.domain.CustomerProfileRules.Changes;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Regras do perfil do cliente (S1-06 §2): cada campo da tabela com casos válidos e inválidos.
 */
class CustomerProfileValidationTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 2);
    private static final Changes VALID = new Changes("Ana Maria Mussa", LocalDate.of(1990, 5, 14), IdDocType.BI,
            "110100123345A", "123456789", "Av. Julius Nyerere, 1234", Province.MAPUTO_CITY);

    private final UserAccount customer = UserAccount.newCustomer(Msisdn.of("841234567"));

    private CustomerProfile empty() {
        return CustomerProfile.emptyFor(customer);
    }

    private CustomerProfile filled() {
        CustomerProfile profile = empty();
        profile.apply(VALID, TODAY);
        return profile;
    }

    private static Changes with(String fullName, LocalDate birthDate, IdDocType type, String doc, String nuit,
                                String address, Province province) {
        return new Changes(fullName, birthDate, type, doc, nuit, address, province);
    }

    private static List<String> codesFor(List<FieldError> errors, String field) {
        return errors.stream().filter(e -> e.field().equals(field)).map(FieldError::code).toList();
    }

    @Test
    void validDataHasNoErrors() {
        assertThat(CustomerProfileRules.validate(empty(), VALID, TODAY)).isEmpty();
    }

    @Test
    void allMissingFieldsOfANewProfileAreReportedTogether() {
        List<FieldError> errors = CustomerProfileRules.validate(empty(),
                with(null, null, null, null, null, null, null), TODAY);
        assertThat(errors).extracting(FieldError::field)
                .containsExactly("fullName", "birthDate", "idDocType", "idDocNumber", "nuit", "province");
        assertThat(errors).extracting(FieldError::code).containsOnly("REQUIRED");
    }

    @Nested
    class FullName {

        @ParameterizedTest
        @ValueSource(strings = {"Ana Mussa", "José da Conceição", "Ângela Nhantumbo-Cossa", "Maria D'Almeida",
                "  Ana   Mussa  ", "Zé Bá"})
        void valid(String name) {
            assertThat(codesFor(CustomerProfileRules.validate(empty(),
                    with(name, VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(), null,
                            VALID.province()), TODAY), "fullName")).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"Ana", "Ana2 Mussa", "Ana_Mussa Silva", "Ana - ", "Ana Mussa!", "A"})
        void invalid(String name) {
            assertThat(codesFor(CustomerProfileRules.validate(empty(),
                    with(name, VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(), null,
                            VALID.province()), TODAY), "fullName")).containsExactly("INVALID_NAME");
        }

        @Test
        void longerThan120IsInvalid() {
            String name = "Ana " + "a".repeat(117);
            assertThat(name).hasSize(121);
            assertThat(codesFor(CustomerProfileRules.validate(empty(),
                    with(name, VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(), null,
                            VALID.province()), TODAY), "fullName")).containsExactly("INVALID_NAME");
            String max = "Ana " + "a".repeat(116);
            assertThat(codesFor(CustomerProfileRules.validate(empty(),
                    with(max, VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(), null,
                            VALID.province()), TODAY), "fullName")).isEmpty();
        }

        @Test
        void blankIsRequired() {
            assertThat(codesFor(CustomerProfileRules.validate(empty(),
                    with("  ", VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(), null,
                            VALID.province()), TODAY), "fullName")).containsExactly("REQUIRED");
        }

        @Test
        void isStoredWithSingleSpaces() {
            CustomerProfile profile = empty();
            profile.apply(with("  Ana   Maria  Mussa ", VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(),
                    VALID.nuit(), null, VALID.province()), TODAY);
            assertThat(profile.getFullName()).isEqualTo("Ana Maria Mussa");
        }
    }

    @Nested
    class BirthDate {

        private List<String> codes(LocalDate birthDate) {
            return codesFor(CustomerProfileRules.validate(empty(),
                    with(VALID.fullName(), birthDate, VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(), null,
                            VALID.province()), TODAY), "birthDate");
        }

        @Test
        void exactly18TodayIsValid() {
            assertThat(codes(TODAY.minusYears(18))).isEmpty();
        }

        @Test
        void oneDayShortOf18IsUnderage() {
            assertThat(codes(TODAY.minusYears(18).plusDays(1))).containsExactly("UNDERAGE");
        }

        @Test
        void futureDateIsUnderage() {
            assertThat(codes(TODAY.plusDays(1))).containsExactly("UNDERAGE");
        }

        @Test
        void age120IsValidAnd121IsInvalid() {
            assertThat(codes(TODAY.minusYears(120))).isEmpty();
            assertThat(codes(TODAY.minusYears(121))).containsExactly("INVALID_DATE");
        }

        @Test
        void missingIsRequired() {
            assertThat(codes(null)).containsExactly("REQUIRED");
        }
    }

    @Nested
    class IdDocTypeRule {

        @Test
        void eachTypeIsValidAndMissingIsRequired() {
            for (IdDocType type : IdDocType.values()) {
                assertThat(codesFor(CustomerProfileRules.validate(empty(),
                        with(VALID.fullName(), VALID.birthDate(), type, VALID.idDocNumber(), VALID.nuit(), null,
                                VALID.province()), TODAY), "idDocType")).isEmpty();
            }
            assertThat(codesFor(CustomerProfileRules.validate(empty(),
                    with(VALID.fullName(), VALID.birthDate(), null, VALID.idDocNumber(), VALID.nuit(), null,
                            VALID.province()), TODAY), "idDocType")).containsExactly("REQUIRED");
        }
    }

    @Nested
    class IdDocNumber {

        private List<String> codes(CustomerProfile current, IdDocType type, String number) {
            return codesFor(CustomerProfileRules.validate(current,
                    with(VALID.fullName(), VALID.birthDate(), type, number, VALID.nuit(), null, VALID.province()),
                    TODAY), "idDocNumber");
        }

        @ParameterizedTest
        @ValueSource(strings = {"AB123", "110100123345A", "12345678901234567890", "ab 12345", " n1234567 "})
        void valid(String number) {
            assertThat(codes(empty(), IdDocType.BI, number)).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"AB12", "123456789012345678901", "AB-12345", "AB/12345", "ÁB12345"})
        void invalid(String number) {
            assertThat(codes(empty(), IdDocType.BI, number)).containsExactly("INVALID_DOCUMENT_NUMBER");
        }

        @Test
        void storedInUpperCaseWithoutSpaces() {
            CustomerProfile profile = empty();
            profile.apply(with(VALID.fullName(), VALID.birthDate(), IdDocType.PASSPORT, " ab 12345 ", VALID.nuit(),
                    null, VALID.province()), TODAY);
            assertThat(profile.getIdDocNumber()).isEqualTo("AB12345");
        }

        @Test
        void requiredWhenTheProfileHasNone() {
            assertThat(codes(empty(), IdDocType.BI, null)).containsExactly("REQUIRED");
            assertThat(codes(empty(), IdDocType.BI, " ")).containsExactly("REQUIRED");
        }

        @Test
        void nullKeepsTheCurrentWhenTheTypeDoesNotChange() {
            CustomerProfile profile = filled();
            assertThat(codes(profile, IdDocType.BI, null)).isEmpty();
            profile.apply(with("Ana Mussa", VALID.birthDate(), IdDocType.BI, null, null, null, Province.GAZA), TODAY);
            assertThat(profile.getIdDocNumber()).isEqualTo("110100123345A");
            assertThat(profile.getNuit()).isEqualTo("123456789");
        }

        @Test
        void requiredWhenTheTypeChanges() {
            assertThat(codes(filled(), IdDocType.PASSPORT, null)).containsExactly("REQUIRED");
            assertThat(codes(filled(), IdDocType.PASSPORT, "N1234567")).isEmpty();
        }

        @Test
        void aValueReplacesTheCurrent() {
            CustomerProfile profile = filled();
            profile.apply(with(VALID.fullName(), VALID.birthDate(), IdDocType.BI, "999999999999Z", null, null,
                    VALID.province()), TODAY);
            assertThat(profile.getIdDocNumber()).isEqualTo("999999999999Z");
        }
    }

    @Nested
    class NuitRule {

        private List<String> codes(CustomerProfile current, String nuit) {
            return codesFor(CustomerProfileRules.validate(current,
                    with(VALID.fullName(), VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), nuit, null,
                            VALID.province()), TODAY), "nuit");
        }

        @ParameterizedTest
        @ValueSource(strings = {"123456789", "123 456 789"})
        void valid(String nuit) {
            assertThat(codes(empty(), nuit)).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = {"12345678", "1234567890", "12345678A"})
        void invalid(String nuit) {
            assertThat(codes(empty(), nuit)).containsExactly("INVALID_NUIT");
        }

        @Test
        void requiredOnlyWhenTheProfileHasNone() {
            assertThat(codes(empty(), null)).containsExactly("REQUIRED");
            assertThat(codes(filled(), null)).isEmpty();
        }

        @Test
        void theCustomerCanCorrectIt() {
            CustomerProfile profile = filled();
            profile.apply(with(VALID.fullName(), VALID.birthDate(), VALID.idDocType(), null, "987 654 321", null,
                    VALID.province()), TODAY);
            assertThat(profile.getNuit()).isEqualTo("987654321");
        }
    }

    @Nested
    class Address {

        private List<String> codes(String address) {
            return codesFor(CustomerProfileRules.validate(empty(),
                    with(VALID.fullName(), VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(),
                            address, VALID.province()), TODAY), "address");
        }

        @Test
        void optionalUpTo300() {
            assertThat(codes(null)).isEmpty();
            assertThat(codes("a".repeat(300))).isEmpty();
            assertThat(codes("a".repeat(301))).containsExactly("SIZE");
        }

        @Test
        void blankIsStoredAsNull() {
            CustomerProfile profile = empty();
            profile.apply(with(VALID.fullName(), VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(),
                    VALID.nuit(), "  ", VALID.province()), TODAY);
            assertThat(profile.getAddress()).isNull();
        }
    }

    @Nested
    class ProvinceRule {

        @Test
        void requiredAndAnyProvinceIsValid() {
            assertThat(codesFor(CustomerProfileRules.validate(empty(),
                    with(VALID.fullName(), VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(),
                            null, null), TODAY), "province")).containsExactly("REQUIRED");
            for (Province province : Province.values()) {
                assertThat(codesFor(CustomerProfileRules.validate(empty(),
                        with(VALID.fullName(), VALID.birthDate(), VALID.idDocType(), VALID.idDocNumber(), VALID.nuit(),
                                null, province), TODAY), "province")).isEmpty();
            }
        }
    }

    @Test
    void applyRejectsWithAllErrorsAndChangesNothing() {
        CustomerProfile profile = filled();
        assertThatThrownBy(() -> profile.apply(with("Ana", TODAY, IdDocType.BI, "x", "1", null, Province.GAZA), TODAY))
                .isInstanceOfSatisfying(DomainException.class, e -> {
                    assertThat(e.getCode()).isEqualTo("VALIDATION_ERROR");
                    assertThat(e.getHttpStatus()).isEqualTo(400);
                    @SuppressWarnings("unchecked")
                    List<FieldError> errors = (List<FieldError>) e.getProperties().get("errors");
                    assertThat(errors).extracting(FieldError::field)
                            .containsExactly("fullName", "birthDate", "idDocNumber", "nuit");
                });
        assertThat(profile.getFullName()).isEqualTo("Ana Maria Mussa");
        assertThat(profile.getProvince()).isEqualTo(Province.MAPUTO_CITY);
    }

    @Test
    void completeAndMasked() {
        CustomerProfile profile = filled();
        assertThat(profile.isComplete()).isTrue();
        assertThat(profile.idDocNumberMasked()).isEqualTo("*********345A");
        assertThat(profile.nuitMasked()).isEqualTo("*****6789");
        assertThat(empty().idDocNumberMasked()).isNull();
        assertThat(empty().nuitMasked()).isNull();
    }
}
