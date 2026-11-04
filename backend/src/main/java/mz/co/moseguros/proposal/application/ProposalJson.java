package mz.co.moseguros.proposal.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.proposal.CoverageLineView;
import mz.co.moseguros.proposal.domain.CoverageLine;
import mz.co.moseguros.proposal.domain.CoverageLine.Deductible;
import mz.co.moseguros.proposal.domain.PolicyholderSnapshot;
import mz.co.moseguros.proposal.domain.PremiumBreakdown;
import mz.co.moseguros.rating.QuoteView;
import mz.co.moseguros.rating.QuotedCoverage;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.vehicle.DriverSnapshot;
import mz.co.moseguros.vehicle.VehicleSnapshot;
import org.springframework.stereotype.Component;

/**
 * Conversão entre os records da proposta e o JSON gravado (S4-02 §5), com o {@code ObjectMapper}
 * da aplicação ({@code Money} como {@code { amount, currency }}, datas ISO 8601).
 */
@Component
public class ProposalJson {

    /** Campo do {@code DriverSnapshot} que nunca se guarda na proposta. */
    static final String LICENCE_NUMBER_HASH = "licenceNumberHash";

    private final ObjectMapper mapper;

    public ProposalJson(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /** Nesta fase o segurado é sempre o tomador. */
    public JsonNode insuredSameAsPolicyholder() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("sameAsPolicyholder", true);
        return node;
    }

    public JsonNode policyholder(PolicyholderSnapshot snapshot) {
        return mapper.valueToTree(snapshot);
    }

    public PolicyholderSnapshot policyholder(JsonNode json) {
        return read(json, PolicyholderSnapshot.class);
    }

    public JsonNode vehicle(VehicleSnapshot snapshot) {
        return mapper.valueToTree(snapshot);
    }

    /** O {@code DriverSnapshot} sem {@code licenceNumberHash} (só com a carta mascarada). */
    public JsonNode driver(DriverSnapshot snapshot) {
        ObjectNode node = mapper.valueToTree(snapshot);
        node.remove(LICENCE_NUMBER_HASH);
        return node;
    }

    public JsonNode coverages(List<CoverageLine> lines) {
        return mapper.valueToTree(lines);
    }

    public List<CoverageLine> coverages(JsonNode json) {
        try {
            return mapper.readerFor(new TypeReference<List<CoverageLine>>() { }).readValue(json);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Unreadable proposal coverages", e);
        }
    }

    public JsonNode premium(PremiumBreakdown breakdown) {
        return mapper.valueToTree(breakdown);
    }

    public PremiumBreakdown premium(JsonNode json) {
        return read(json, PremiumBreakdown.class);
    }

    /**
     * Coberturas da cotação como foram oferecidas, com o mínimo da franquia percentual lido da
     * versão do produto (o {@code QuotedCoverage} não o traz).
     *
     * @param catalogCoverages coberturas da versão cotada, por código
     */
    public static List<CoverageLine> coverageLines(List<QuotedCoverage> quoted, Map<String, CoverageView> catalogCoverages) {
        List<CoverageLine> lines = new ArrayList<>();
        for (QuotedCoverage c : quoted) {
            CoverageView catalog = catalogCoverages.get(c.code());
            lines.add(new CoverageLine(c.code(), c.mandatory(),
                    c.capitalAmount() == 0 ? null : Money.ofCentavos(c.capitalAmount()),
                    deductible(c, catalog), Money.ofCentavos(c.premiumAmount())));
        }
        return lines;
    }

    /** Decomposição do prémio da cotação. */
    public static PremiumBreakdown premiumOf(QuoteView quote) {
        List<PremiumBreakdown.Charge> charges = quote.charges().stream()
                .map(c -> new PremiumBreakdown.Charge(c.code(), c.labelPt(), c.labelEn(), Money.ofCentavos(c.amount())))
                .toList();
        return new PremiumBreakdown(Money.ofCentavos(nz(quote.commercialPremiumAmount())), charges,
                Money.ofCentavos(nz(quote.roundingAdjustmentAmount())), Money.ofCentavos(nz(quote.totalAmount())));
    }

    /** Vista de uma cobertura para outros módulos. */
    public static CoverageLineView view(CoverageLine line) {
        Deductible d = line.deductible();
        return new CoverageLineView(line.code(), line.mandatory(), line.capital(), d.type(), d.amount(),
                d.percentValue(), line.premium());
    }

    private static Deductible deductible(QuotedCoverage c, CoverageView catalog) {
        String type = c.deductibleType() == null ? Deductible.NONE : c.deductibleType();
        return switch (type) {
            case Deductible.FIXED -> new Deductible(Deductible.FIXED,
                    c.deductibleValue() == null ? null : Money.ofCentavos(Long.parseLong(c.deductibleValue())),
                    null, null);
            case Deductible.PERCENT -> new Deductible(Deductible.PERCENT, null, c.deductibleValue(),
                    catalog == null ? null : catalog.deductibleMinAmount());
            default -> Deductible.none();
        };
    }

    private static long nz(Long value) {
        return value == null ? 0 : value;
    }

    private <T> T read(JsonNode json, Class<T> type) {
        try {
            return mapper.treeToValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unreadable proposal data (" + type.getSimpleName() + ")", e);
        }
    }

}
