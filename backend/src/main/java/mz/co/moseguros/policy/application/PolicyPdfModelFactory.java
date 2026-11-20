package mz.co.moseguros.policy.application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.JsonNode;
import mz.co.moseguros.policy.PolicyCoverageView;
import mz.co.moseguros.policy.domain.Certificate;
import mz.co.moseguros.policy.domain.Instalment;
import mz.co.moseguros.policy.domain.Policy;
import mz.co.moseguros.shared.domain.Money;
import org.springframework.stereotype.Component;

/**
 * Modelos dos PDF da apólice (S6-02 §2 e §3): mapas simples com valores tipados ({@link Instant},
 * {@link Money}, códigos); a formatação faz-se no modelo Thymeleaf com o {@code fmt}.
 *
 * <p>Só o nome do tomador: nunca o documento, o NUIT, o telefone nem a morada. Do condutor só o
 * {@code licenceNumberMasked} do snapshot (o {@code licenceNumberHash} nunca entra). O código de
 * verificação só aparece dentro do URL de verificação ({@code verificationUrl}) e na imagem do QR. Os
 * modelos nunca vão para um log.
 */
@Component
public class PolicyPdfModelFactory {

    /** Tamanho máximo do logótipo da seguradora no cabeçalho (S6-02 §2). */
    static final BigDecimal LOGO_MAX_WIDTH_MM = new BigDecimal("40");
    static final BigDecimal LOGO_MAX_HEIGHT_MM = new BigDecimal("15");
    static final Set<String> LOGO_TYPES = Set.of("image/png", "image/jpeg");

    /** Logótipo em {@code data:} URI, com a largura e a altura (mm) que cabem em 40 × 15 mm. */
    public record Logo(String src, String width, String height) {
    }

    /** Cabeçalho comum: a seguradora (nome e logótipo opcional) e o momento da geração. */
    public record Header(String insurerName, Logo logo, Instant generatedAt) {
    }

    /**
     * Modelo do certificado ({@code policy-certificate.html}).
     *
     * @param territorialScope códigos ISO da cobertura {@code MTPL} da versão; vazio ou {@code null} se não houver
     * @param verificationUrl  {@code {public-base-url}/v/{código}}
     * @param qrCode           imagem do QR em {@code data:image/png;base64,…}
     */
    public Map<String, Object> certificate(Policy policy, Certificate certificate, Header header,
                                           List<String> territorialScope, String verificationUrl, String qrCode) {
        Map<String, Object> model = common(header);
        model.put("certificateNumber", certificate.getNumber());
        model.put("policyNumber", policy.getNumber());
        model.put("policyholderName", policy.getPolicyholderName());
        model.put("vehicle", vehicle(policy));
        model.put("validFrom", certificate.getValidFrom());
        model.put("validTo", certificate.getValidTo());
        model.put("coverageType", policy.getCoverageType().name());
        model.put("territorialScope", territorialScope == null || territorialScope.isEmpty() ? null
                : String.join(", ", territorialScope));
        model.put("verificationUrl", verificationUrl);
        model.put("qrCode", qrCode);
        return model;
    }

    /**
     * Modelo das condições particulares ({@code policy-schedule.html}).
     *
     * @param productVersionNo número da versão do produto; {@code null} se a versão não for legível
     */
    public Map<String, Object> schedule(Policy policy, Header header, List<PolicyCoverageView> coverages,
                                        List<Instalment> instalments, Integer productVersionNo) {
        Map<String, Object> model = common(header);
        model.put("policyNumber", policy.getNumber());
        model.put("productName", policy.getProductName());
        model.put("productVersionNo", productVersionNo);
        model.put("channel", policy.getChannel());
        model.put("validFrom", policy.getStartAt());
        model.put("validTo", policy.getEndAt());
        model.put("renewalType", policy.getRenewalType().name());
        model.put("policyholderName", policy.getPolicyholderName());
        model.put("vehicle", vehicle(policy));
        model.put("driver", driver(policy.getDriverSnapshot()));
        model.put("coverages", coverages.stream().map(PolicyPdfModelFactory::coverage).toList());
        model.put("total", Money.ofCentavos(policy.getTotalAmount()));
        model.put("paymentPlan", policy.getPaymentPlan().name());
        model.put("instalments", instalments.stream().map(PolicyPdfModelFactory::instalment).toList());
        return model;
    }

    /**
     * Logótipo PNG ou JPEG em {@code data:} URI, reduzido para caber em 40 × 15 mm (proporção mantida);
     * {@code null} se o tipo não for aceite ou a imagem não se ler.
     */
    public static Logo logo(byte[] bytes, String contentType) {
        if (bytes == null || bytes.length == 0 || contentType == null || !LOGO_TYPES.contains(contentType)) {
            return null;
        }
        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            return null;
        }
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
            return null;
        }
        BigDecimal ratio = BigDecimal.valueOf(image.getWidth()).divide(BigDecimal.valueOf(image.getHeight()), 6,
                RoundingMode.HALF_UP);
        BigDecimal width = LOGO_MAX_WIDTH_MM;
        BigDecimal height = width.divide(ratio, 2, RoundingMode.HALF_UP);
        if (height.compareTo(LOGO_MAX_HEIGHT_MM) > 0) {
            height = LOGO_MAX_HEIGHT_MM;
            width = height.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
        }
        return new Logo("data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(bytes),
                width.stripTrailingZeros().toPlainString() + "mm", height.stripTrailingZeros().toPlainString() + "mm");
    }

    private static Map<String, Object> common(Header header) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("insurerName", header.insurerName());
        model.put("logo", header.logo());
        model.put("generatedAt", header.generatedAt());
        return model;
    }

    private static Map<String, Object> vehicle(Policy policy) {
        JsonNode v = policy.getVehicleSnapshot();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("plate", policy.getPlate());
        m.put("make", text(v, "make"));
        m.put("model", text(v, "model"));
        JsonNode year = v == null ? null : v.get("year");
        m.put("year", year == null || year.isNull() ? null : year.asInt());
        m.put("chassisNumber", text(v, "chassisNumber"));
        m.put("category", text(v, "category"));
        m.put("usage", text(v, "usage"));
        return m;
    }

    /** {@code null} sem condutor habitual; só o número da carta mascarado. */
    private static Map<String, Object> driver(JsonNode d) {
        if (d == null || d.isNull() || d.isMissingNode()) {
            return null;
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fullName", text(d, "fullName"));
        m.put("licenceNumberMasked", text(d, "licenceNumberMasked"));
        List<String> categories = new ArrayList<>();
        d.path("licenceCategories").forEach(c -> categories.add(c.asText()));
        m.put("licenceCategories", categories.isEmpty() ? null : String.join(", ", categories));
        return m;
    }

    private static Map<String, Object> coverage(PolicyCoverageView c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", c.code());
        m.put("capital", c.capital());
        m.put("deductibleType", c.deductibleType());
        m.put("deductibleAmount", c.deductibleAmount());
        m.put("deductiblePercent", c.deductiblePercent());
        m.put("premium", c.premium());
        return m;
    }

    private static Map<String, Object> instalment(Instalment i) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("seq", i.getSeq());
        m.put("dueDate", i.getDueDate());
        m.put("amount", Money.ofCentavos(i.getAmount()));
        m.put("status", i.getStatus().name());
        return m;
    }

    private static String text(JsonNode node, String field) {
        if (node == null) {
            return null;
        }
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }
}
