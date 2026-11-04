package mz.co.moseguros.rating.infrastructure;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Contrato fixo {@code moseguros-rating-1} entre a plataforma e o sistema de tarifação de uma
 * seguradora (S3-02 §8): o corpo do pedido e a leitura estrita da resposta. Não leva matrícula,
 * nome, telemóvel, número da carta nem ids da plataforma além do {@code requestId}.
 */
public final class ExternalRatingContract {

    public static final String VERSION = "moseguros-rating-1";
    public static final String MZN = "MZN";
    public static final int EXTERNAL_REFERENCE_MAX = 100;
    public static final int DECLINE_REASON_MAX = 200;
    public static final int MAX_CHARGES = 10;
    public static final int LABEL_MAX = 80;
    private static final Pattern CHARGE_CODE = Pattern.compile("^[A-Z][A-Z0-9_]{1,29}$");

    private ExternalRatingContract() {
    }

    // --- Pedido ---

    /** Valor em centavos, sempre {@code MZN}. */
    public record Amount(long amount, String currency) {

        public static Amount of(Long centavos) {
            return centavos == null ? null : new Amount(centavos, MZN);
        }
    }

    public record Product(String code, int versionNo, String type) {
    }

    public record Vehicle(String category, String usage, String make, String model, int year, Integer engineCc,
                          Integer seats, Integer grossWeightKg, String provinceOfCirculation, Amount commercialValue,
                          boolean hasTracker) {
    }

    public record Driver(LocalDate birthDate, LocalDate licenceIssueDate, List<String> licenceCategories) {
    }

    /** Cobertura com o capital já resolvido pelo {@code CoverageSelector}. */
    public record Coverage(String code, Amount capital) {
    }

    /** Corpo do {@code POST {ratingEndpointUrl}}. */
    public record Request(String contractVersion, String requestId, String insurerShortCode, Product product,
                          LocalDate startDate, LocalDate endDate, int durationDays, Vehicle vehicle, Driver driver,
                          List<Coverage> coverages) {
    }

    // --- Resposta ---

    /** Resposta que não cumpre o contrato ({@code EXTERNAL_INVALID_RESPONSE}). */
    public static class InvalidResponseException extends RuntimeException {

        public InvalidResponseException(String message) {
            super(message);
        }
    }

    public record CoveragePremium(String code, long premiumAmount) {
    }

    public record Charge(String code, String labelPt, String labelEn, long amount) {
    }

    /** Resposta {@code OFFERED} ou {@code DECLINED} já validada. */
    public sealed interface Response permits Offered, Declined {
    }

    public record Offered(String externalReference, List<CoveragePremium> coverages, long commercialPremiumAmount,
                          List<Charge> charges, long totalAmount) implements Response {
    }

    public record Declined(String declineReason) implements Response {
    }

    /**
     * Valida a resposta (S3-02 §8, "Validação da resposta").
     *
     * @param requestId     o {@code requestId} enviado
     * @param coverageCodes os códigos das coberturas enviadas
     * @throws InvalidResponseException à primeira falha
     */
    public static Response read(JsonNode body, String requestId, Set<String> coverageCodes) {
        if (body == null || !body.isObject()) {
            throw invalid("The body is not a JSON object");
        }
        if (!VERSION.equals(text(body, "contractVersion"))) {
            throw invalid("contractVersion does not match");
        }
        if (!requestId.equals(text(body, "requestId"))) {
            throw invalid("requestId does not match");
        }
        String status = text(body, "status");
        if ("DECLINED".equals(status)) {
            String reason = optionalText(body, "declineReason");
            if (reason != null && reason.length() > DECLINE_REASON_MAX) {
                throw invalid("declineReason is too long");
            }
            return new Declined(reason);
        }
        if (!"OFFERED".equals(status)) {
            throw invalid("Unknown status");
        }
        String reference = optionalText(body, "externalReference");
        if (reference != null && reference.length() > EXTERNAL_REFERENCE_MAX) {
            throw invalid("externalReference is too long");
        }

        JsonNode coveragesNode = body.get("coverages");
        if (coveragesNode == null || !coveragesNode.isArray()) {
            throw invalid("coverages must be an array");
        }
        List<CoveragePremium> coverages = new ArrayList<>();
        Set<String> codes = new HashSet<>();
        long sum = 0;
        for (JsonNode c : coveragesNode) {
            String code = text(c, "code");
            if (code == null || !codes.add(code)) {
                throw invalid("Coverage code missing or repeated");
            }
            long premium = money(c.get("premium"));
            sum = Math.addExact(sum, premium);
            coverages.add(new CoveragePremium(code, premium));
        }
        if (!codes.equals(coverageCodes)) {
            throw invalid("The coverages differ from the ones sent");
        }
        long commercial = money(body.get("commercialPremium"));
        if (sum != commercial) {
            throw invalid("The coverage premiums do not add up to commercialPremium");
        }

        JsonNode chargesNode = body.get("charges");
        List<Charge> charges = new ArrayList<>();
        long total = commercial;
        if (chargesNode != null && !chargesNode.isNull()) {
            if (!chargesNode.isArray() || chargesNode.size() > MAX_CHARGES) {
                throw invalid("charges must be an array with at most " + MAX_CHARGES + " items");
            }
            for (JsonNode c : chargesNode) {
                String code = text(c, "code");
                if (code == null || !CHARGE_CODE.matcher(code).matches()) {
                    throw invalid("Invalid charge code");
                }
                String labelPt = label(c, "labelPt");
                String labelEn = label(c, "labelEn");
                long amount = money(c.get("amount"));
                total = Math.addExact(total, amount);
                charges.add(new Charge(code, labelPt, labelEn, amount));
            }
        }
        long totalAmount = money(body.get("totalAmount"));
        if (total != totalAmount) {
            throw invalid("commercialPremium plus charges does not add up to totalAmount");
        }
        return new Offered(reference, List.copyOf(coverages), commercial, List.copyOf(charges), totalAmount);
    }

    /** {@code { "amount": <inteiro ≥ 0>, "currency": "MZN" }}. */
    private static long money(JsonNode node) {
        if (node == null || !node.isObject()) {
            throw invalid("Money must be an object");
        }
        if (!MZN.equals(text(node, "currency"))) {
            throw invalid("Currency must be MZN");
        }
        JsonNode amount = node.get("amount");
        if (amount == null || !amount.isIntegralNumber() || !amount.canConvertToLong() || amount.longValue() < 0) {
            throw invalid("Amounts must be integers ≥ 0");
        }
        return amount.longValue();
    }

    private static String label(JsonNode node, String field) {
        String value = text(node, field);
        if (value == null || value.isBlank() || value.length() > LABEL_MAX) {
            throw invalid(field + " must have 1 to " + LABEL_MAX + " characters");
        }
        return value;
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && value.isTextual() ? value.asText() : null;
    }

    private static String optionalText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isTextual()) {
            throw invalid(field + " must be a string");
        }
        return value.asText();
    }

    private static InvalidResponseException invalid(String message) {
        return new InvalidResponseException(message);
    }
}
