package mz.co.moseguros.catalog.domain;

import static mz.co.moseguros.catalog.domain.CoverageRulesTest.b;
import static mz.co.moseguros.catalog.domain.CoverageRulesTest.m;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mz.co.moseguros.catalog.CapitalType;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.DeductibleType;
import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.catalog.domain.PublicationRules.Candidate;
import mz.co.moseguros.catalog.domain.PublicationRules.DocumentKey;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import org.junit.jupiter.api.Test;

/** Regras de publicação (S2-04 §4): uma verificação por regra e os casos de referência ({@code M = 300000000}). */
class PublicationRulesTest {

    static final long M = 300_000_000L;
    static final Set<DocumentKey> DOCS = Set.of(new DocumentKey(ProductDocumentType.GENERAL_CONDITIONS, "pt"),
            new DocumentKey(ProductDocumentType.PRE_CONTRACT_INFO, "pt"));

    static CoverageSpec mtpl(long capital) {
        return b(CoverageCode.MTPL).capital(CapitalType.FIXED, m(capital), null).build();
    }

    static CoverageSpec od(CoverageCode code) {
        return b(code).mandatory(false).capital(CapitalType.VEHICLE_VALUE, null, null)
                .deductible(DeductibleType.PERCENT, null, new BigDecimal("10"), m(2_500_000)).build();
    }

    static CoverageSpec vtplOptions(long... amounts) {
        List<mz.co.moseguros.shared.domain.Money> options = new ArrayList<>();
        for (long a : amounts) {
            options.add(m(a));
        }
        return b(CoverageCode.VTPL).mandatory(false).capital(CapitalType.OPTIONS, null, options).build();
    }

    static List<String> errors(ProductType type, CoverageSpec... coverages) {
        return errors(new Candidate(type, List.of(coverages), DOCS, List.of(VehicleCategory.LIGHT_PASSENGER),
                List.of(VehicleUsage.PRIVATE)));
    }

    static List<String> errors(Candidate candidate) {
        return PublicationRules.evaluate(candidate, M).stream().map(e -> e.field() + ":" + e.code()).toList();
    }

    // Casos de referência

    @Test
    void referenceMtplOnlyAtTheMinimumIsPublishable() {
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(300_000_000))).isEmpty();
    }

    @Test
    void referenceMtplBelowTheMinimum() {
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(299_999_900)))
                .containsExactly("coverages[0].capital:MTPL_CAPITAL_BELOW_MINIMUM");
    }

    @Test
    void referenceVtplOptionEqualToTheMinimumIsTooLow() {
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), vtplOptions(300_000_000, 500_000_000)))
                .containsExactly("coverages[1].capitalOptions:VTPL_CAPITAL_TOO_LOW");
    }

    @Test
    void referenceVtplOptionsAboveTheMinimumArePublishable() {
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), vtplOptions(300_000_100, 500_000_000))).isEmpty();
    }

    @Test
    void referenceTheftWithoutTheOtherOwnDamageCoverages() {
        assertThat(errors(ProductType.COMPREHENSIVE, mtpl(M), od(CoverageCode.OD_THEFT)))
                .containsExactly("coverages[1].code:OD_THEFT_REQUIRES_OD_COVERAGES");
    }

    @Test
    void referenceFullComprehensiveIsPublishable() {
        assertThat(errors(ProductType.COMPREHENSIVE, mtpl(M), od(CoverageCode.OD_COLLISION), od(CoverageCode.OD_FIRE),
                od(CoverageCode.OD_NATURE), od(CoverageCode.OD_THEFT))).isEmpty();
    }

    @Test
    void referenceGlassWithFixedCapital() {
        CoverageSpec glass = b(CoverageCode.OD_GLASS).mandatory(false).capital(CapitalType.FIXED, m(1_000_000), null)
                .build();
        assertThat(errors(ProductType.COMPREHENSIVE, mtpl(M), glass))
                .containsExactly("coverages[1].capitalType:OD_CAPITAL_MUST_BE_VEHICLE_VALUE");
    }

    @Test
    void referenceBorderWithAssistance() {
        CoverageSpec assistance = b(CoverageCode.ASSISTANCE).mandatory(false).capital(CapitalType.FIXED,
                m(2_500_000), null).build();
        assertThat(errors(ProductType.BORDER, mtpl(M), assistance)).containsExactly("coverages:BORDER_ONLY_MTPL");
    }

    @Test
    void referenceTemporaryWithCollision() {
        assertThat(errors(ProductType.TEMPORARY, mtpl(M), od(CoverageCode.OD_COLLISION)))
                .containsExactly("coverages[1].code:OD_NOT_ALLOWED_FOR_TYPE");
    }

    @Test
    void referenceEmptyVersionGivesFiveErrorsTogether() {
        assertThat(errors(new Candidate(ProductType.MTPL_ONLY, List.of(), Set.of(), List.of(), List.of())))
                .containsExactly("coverages:MTPL_REQUIRED", "documents.GENERAL_CONDITIONS:DOCUMENT_REQUIRED",
                        "documents.PRE_CONTRACT_INFO:DOCUMENT_REQUIRED", "vehicleCategories:REQUIRED",
                        "usages:REQUIRED");
    }

    // Uma regra de cada vez

    @Test
    void rule1MtplIsRequired() {
        CoverageSpec assistance = b(CoverageCode.ASSISTANCE).capital(CapitalType.FIXED, m(1), null).build();
        assertThat(errors(ProductType.FLEET, assistance)).containsExactly("coverages:MTPL_REQUIRED");
    }

    @Test
    void rule2MtplMustBeMandatory() {
        assertThat(errors(ProductType.MTPL_ONLY, b(CoverageCode.MTPL).mandatory(false).build()))
                .containsExactly("coverages[0].mandatory:MTPL_MUST_BE_MANDATORY");
    }

    @Test
    void rule3MtplCapitalTypeAndMinimum() {
        CoverageSpec options = b(CoverageCode.MTPL).capital(CapitalType.OPTIONS, null,
                List.of(m(M), m(M + 1))).build();
        assertThat(errors(ProductType.MTPL_ONLY, options)).containsExactly("coverages[0].capitalType:INVALID_CAPITAL_TYPE");
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M - 1)))
                .containsExactly("coverages[0].capital:MTPL_CAPITAL_BELOW_MINIMUM");
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M + 1))).isEmpty();
    }

    @Test
    void rule4BorderOnlyMtpl() {
        assertThat(errors(ProductType.BORDER, mtpl(M))).isEmpty();
        CoverageSpec legal = b(CoverageCode.LEGAL).mandatory(false).capital(CapitalType.FIXED, m(1), null).build();
        assertThat(errors(ProductType.BORDER, mtpl(M), legal)).containsExactly("coverages:BORDER_ONLY_MTPL");
    }

    @Test
    void rule5NoOwnDamageInMtplOnlyTemporaryAndBorder() {
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), od(CoverageCode.OD_FIRE)))
                .containsExactly("coverages[1].code:OD_NOT_ALLOWED_FOR_TYPE");
        assertThat(errors(ProductType.TEMPORARY, mtpl(M), od(CoverageCode.OD_GLASS)))
                .containsExactly("coverages[1].code:OD_NOT_ALLOWED_FOR_TYPE");
        assertThat(errors(ProductType.BORDER, mtpl(M), od(CoverageCode.OD_GLASS)))
                .containsExactly("coverages:BORDER_ONLY_MTPL", "coverages[1].code:OD_NOT_ALLOWED_FOR_TYPE");
        // FLEET segue as regras gerais
        assertThat(errors(ProductType.FLEET, mtpl(M), od(CoverageCode.OD_GLASS))).isEmpty();
    }

    @Test
    void rule6ComprehensiveNeedsOwnDamage() {
        assertThat(errors(ProductType.COMPREHENSIVE, mtpl(M))).containsExactly("coverages:OD_REQUIRED_FOR_COMPREHENSIVE");
        assertThat(errors(ProductType.COMPREHENSIVE, mtpl(M), od(CoverageCode.OD_GLASS))).isEmpty();
    }

    @Test
    void rule7OwnDamageUsesVehicleValue() {
        CoverageSpec fire = b(CoverageCode.OD_FIRE).capital(CapitalType.PER_PERSON, m(1), null).build();
        assertThat(errors(ProductType.FLEET, mtpl(M), fire))
                .containsExactly("coverages[1].capitalType:OD_CAPITAL_MUST_BE_VEHICLE_VALUE");
    }

    @Test
    void rule8TheftNeedsCollisionFireAndNature() {
        assertThat(errors(ProductType.COMPREHENSIVE, mtpl(M), od(CoverageCode.OD_COLLISION), od(CoverageCode.OD_FIRE),
                od(CoverageCode.OD_THEFT))).containsExactly("coverages[3].code:OD_THEFT_REQUIRES_OD_COVERAGES");
    }

    @Test
    void rule9RequiredCodesMustExist() {
        CoverageSpec occupants = b(CoverageCode.OCCUPANTS).capital(CapitalType.PER_PERSON, m(1), null)
                .requires(CoverageCode.ASSISTANCE).build();
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), occupants))
                .containsExactly("coverages[1].requiresCodes:REQUIRED_COVERAGE_MISSING");
        CoverageSpec assistance = b(CoverageCode.ASSISTANCE).capital(CapitalType.FIXED, m(1), null).build();
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), occupants, assistance)).isEmpty();
    }

    @Test
    void rule10VtplAboveTheMinimum() {
        CoverageSpec fixedEqual = b(CoverageCode.VTPL).capital(CapitalType.FIXED, m(M), null).build();
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), fixedEqual))
                .containsExactly("coverages[1].capital:VTPL_CAPITAL_TOO_LOW");
        CoverageSpec fixedAbove = b(CoverageCode.VTPL).capital(CapitalType.FIXED, m(M + 1), null).build();
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), fixedAbove)).isEmpty();
        CoverageSpec perPerson = b(CoverageCode.VTPL).capital(CapitalType.PER_PERSON, m(M * 2), null).build();
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), perPerson))
                .containsExactly("coverages[1].capitalType:INVALID_CAPITAL_TYPE");
    }

    @Test
    void rule11CrossBorderNeedsAnotherCountry() {
        CoverageSpec onlyMz = b(CoverageCode.CROSS_BORDER).capital(CapitalType.FIXED, m(1), null).build();
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), onlyMz))
                .containsExactly("coverages[1].territorialScope:CROSS_BORDER_SCOPE_REQUIRED");
        CoverageSpec withZa = b(CoverageCode.CROSS_BORDER).capital(CapitalType.FIXED, m(1), null)
                .scope(List.of("MZ", "ZA")).build();
        assertThat(errors(ProductType.MTPL_ONLY, mtpl(M), withZa)).isEmpty();
    }

    @Test
    void rules12And13DocumentsInPortuguese() {
        List<VehicleCategory> categories = List.of(VehicleCategory.HEAVY);
        List<VehicleUsage> usages = List.of(VehicleUsage.GOODS);
        assertThat(errors(new Candidate(ProductType.MTPL_ONLY, List.of(mtpl(M)),
                Set.of(new DocumentKey(ProductDocumentType.GENERAL_CONDITIONS, "en"),
                        new DocumentKey(ProductDocumentType.PRE_CONTRACT_INFO, "pt")), categories, usages)))
                .containsExactly("documents.GENERAL_CONDITIONS:DOCUMENT_REQUIRED");
        assertThat(errors(new Candidate(ProductType.MTPL_ONLY, List.of(mtpl(M)),
                Set.of(new DocumentKey(ProductDocumentType.GENERAL_CONDITIONS, "pt"),
                        new DocumentKey(ProductDocumentType.SPECIAL_CONDITIONS, "pt")), categories, usages)))
                .containsExactly("documents.PRE_CONTRACT_INFO:DOCUMENT_REQUIRED");
    }

    @Test
    void rules14And15Eligibility() {
        assertThat(errors(new Candidate(ProductType.MTPL_ONLY, List.of(mtpl(M)), DOCS, List.of(),
                List.of(VehicleUsage.TAXI)))).containsExactly("vehicleCategories:REQUIRED");
        assertThat(errors(new Candidate(ProductType.MTPL_ONLY, List.of(mtpl(M)), DOCS,
                List.of(VehicleCategory.BUS), List.of()))).containsExactly("usages:REQUIRED");
    }

    @Test
    void theMinimumIsAParameter() {
        assertThat(PublicationRules.evaluate(new Candidate(ProductType.MTPL_ONLY, List.of(mtpl(M)), DOCS,
                List.of(VehicleCategory.HEAVY), List.of(VehicleUsage.GOODS)), 400_000_000L))
                .extracting(e -> e.code()).containsExactly("MTPL_CAPITAL_BELOW_MINIMUM");
    }
}
