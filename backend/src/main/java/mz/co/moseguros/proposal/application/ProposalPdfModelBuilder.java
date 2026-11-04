package mz.co.moseguros.proposal.application;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.proposal.domain.CoverageLine;
import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.PolicyholderSnapshot;
import mz.co.moseguros.proposal.domain.PremiumBreakdown;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.SuitabilityAnswer;
import mz.co.moseguros.shared.domain.Money;
import org.springframework.stereotype.Component;

/**
 * Modelo do PDF da proposta ({@code templates/pdf/proposal.html}, S4-04 §3): mapas simples com os
 * dados da proposta pela ordem das secções. O documento e o NUIT do tomador saem decifrados <em>só</em>
 * para o modelo (o PDF é {@code SENSITIVE}); o modelo nunca vai para um log. A carta do condutor sai
 * mascarada (o número em claro não sai do {@code vehicle}).
 */
@Component
public class ProposalPdfModelBuilder {

    private final ProposalJson json;

    public ProposalPdfModelBuilder(ProposalJson json) {
        this.json = json;
    }

    /**
     * Dados do PDF.
     *
     * @param inspection           secção da vistoria (S4-03: {@code mode}, {@code status},
     *                             {@code photoCount}, {@code odometerKm}, {@code notes},
     *                             {@code submittedAt}) ou {@code null} sem vistoria
     * @param signatureDestination destino mascarado do código ({@code +258*****4567})
     */
    public Map<String, Object> build(Proposal p, List<Declaration> declarations, List<SuitabilityAnswer> suitability,
                                     List<ProductDocuments.Selected> documents, Map<String, Object> inspection,
                                     String signatureDestination, Instant generatedAt) {
        PolicyholderSnapshot holder = json.policyholder(p.getPolicyholder());
        String language = language(holder);
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("number", p.getNumber());
        model.put("revision", p.getContentRevision());
        model.put("generatedAt", generatedAt);
        model.put("insurerName", p.getInsurerName());
        model.put("product", map("name", p.getProductName(), "versionId", p.getProductVersionId().toString(),
                "type", p.getProductType().name()));
        model.put("channel", p.getChannel().name());
        model.put("brokerName", p.getBrokerName());
        model.put("holder", holder(holder, p));
        model.put("vehicle", vehicle(p.getVehicleSnapshot()));
        model.put("driver", driver(p.getDriverSnapshot()));
        model.put("period", map("startDate", p.startDate(), "endDate", p.endDate(), "durationDays", p.getDurationDays()));
        model.put("coverages", json.coverages(p.getCoverages()).stream().map(ProposalPdfModelBuilder::coverage).toList());
        model.put("premium", premium(json.premium(p.getPremiumBreakdown()), language));
        model.put("declarations", declarations(p, declarations));
        model.put("suitability", suitability(p, suitability));
        model.put("inspection", p.isInspectionRequired() ? inspection : null);
        model.put("documents", documents.stream()
                .map(d -> map("type", d.type().name(), "fileId", d.fileId().toString())).toList());
        model.put("signatureDestination", signatureDestination);
        return model;
    }

    /** Língua do PDF: a do tomador ({@code pt} ou {@code en}). */
    public Locale locale(Proposal p) {
        return "en".equals(language(json.policyholder(p.getPolicyholder()))) ? Locale.ENGLISH
                : Locale.forLanguageTag("pt");
    }

    static String language(PolicyholderSnapshot holder) {
        return "en".equalsIgnoreCase(holder.preferredLanguage()) ? "en" : "pt";
    }

    private static Map<String, Object> holder(PolicyholderSnapshot h, Proposal p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fullName", h.fullName());
        m.put("birthDate", h.birthDate());
        m.put("idDocType", h.idDocType());
        m.put("idDocNumber", p.getPolicyholderIdDocNumber());
        m.put("nuit", p.getPolicyholderNuit());
        m.put("address", h.address());
        m.put("province", h.province());
        m.put("msisdn", h.msisdn());
        m.put("email", h.email());
        return m;
    }

    private static Map<String, Object> vehicle(JsonNode v) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("plate", text(v, "plate"));
        m.put("make", text(v, "make"));
        m.put("model", text(v, "model"));
        m.put("year", v.path("year").asInt());
        m.put("chassisNumber", text(v, "chassisNumber"));
        m.put("category", text(v, "category"));
        m.put("usage", text(v, "usage"));
        m.put("seats", v.path("seats").asInt());
        JsonNode value = v.get("commercialValue");
        m.put("commercialValue", value == null || value.isNull() ? null : Money.ofCentavos(value.get("amount").asLong()));
        return m;
    }

    private static Map<String, Object> driver(JsonNode d) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fullName", text(d, "fullName"));
        m.put("birthDate", date(d, "birthDate"));
        m.put("licenceNumberMasked", text(d, "licenceNumberMasked"));
        List<String> categories = new ArrayList<>();
        d.path("licenceCategories").forEach(c -> categories.add(c.asText()));
        m.put("licenceCategories", categories.isEmpty() ? null : String.join(", ", categories));
        m.put("licenceIssueDate", date(d, "licenceIssueDate"));
        return m;
    }

    private static Map<String, Object> coverage(CoverageLine c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", c.code());
        m.put("capital", c.capital());
        CoverageLine.Deductible d = c.deductible();
        m.put("deductibleType", d.type());
        m.put("deductibleAmount", d.amount());
        m.put("deductiblePercent", d.percentValue());
        m.put("deductibleMinAmount", d.minAmount());
        m.put("premium", c.premium());
        return m;
    }

    private static Map<String, Object> premium(PremiumBreakdown premium, String language) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("commercial", premium.commercial());
        m.put("charges", premium.charges().stream()
                .map(c -> map("label", "en".equals(language) ? c.labelEn() : c.labelPt(), "amount", c.amount()))
                .toList());
        m.put("roundingAdjustment", premium.roundingAdjustment().amount() == 0 ? null : premium.roundingAdjustment());
        m.put("total", premium.total());
        return m;
    }

    private static Map<String, Object> declarations(Proposal p, List<Declaration> answers) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Declaration a : answers) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("code", a.getQuestionCode().name());
            m.put("answer", a.getAnswer());
            m.put("claimsCount", a.getClaimsCount());
            m.put("details", a.getDetails());
            list.add(m);
        }
        return map("confirmedAt", p.getDeclarationsConfirmedAt(), "answers", list);
    }

    private static Map<String, Object> suitability(Proposal p, List<SuitabilityAnswer> answers) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("result", p.getSuitabilityResult() == null ? null : p.getSuitabilityResult().name());
        m.put("answers", answers.stream()
                .map(a -> map("code", a.getQuestionCode().name(), "answer", a.getAnswer())).toList());
        m.put("reasons", p.getSuitabilityReasons().stream().map(Enum::name).toList());
        m.put("acknowledgedAt", p.getSuitabilityAcknowledgedAt());
        return m;
    }

    private static Map<String, Object> map(Object... keyValues) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            m.put((String) keyValues[i], keyValues[i + 1]);
        }
        return m;
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private static LocalDate date(JsonNode node, String field) {
        String value = text(node, field);
        return value == null ? null : LocalDate.parse(value);
    }
}
