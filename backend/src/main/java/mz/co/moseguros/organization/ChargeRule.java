package mz.co.moseguros.organization;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Encargo (imposto ou taxa) que a seguradora acrescenta ao prémio comercial (S3-01 §3 e §4).
 * Os nomes são dados da seguradora, por isso vêm em pt e en.
 *
 * @param percent     só {@code PERCENT}: {@code 2.5} quer dizer 2,5 %; senão {@code null}
 * @param fixedAmount só {@code FIXED}: centavos; senão {@code null}
 */
public record ChargeRule(String code, String labelPt, String labelEn, ChargeType type,
                         BigDecimal percent,
                         Long fixedAmount) {

    public ChargeRule {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(type, "type");
        if (type == ChargeType.PERCENT && percent == null) {
            throw new IllegalArgumentException("A PERCENT charge needs percent");
        }
        if (type == ChargeType.FIXED && fixedAmount == null) {
            throw new IllegalArgumentException("A FIXED charge needs fixedAmount");
        }
    }

    public static ChargeRule percent(String code, String labelPt, String labelEn, BigDecimal percent) {
        return new ChargeRule(code, labelPt, labelEn, ChargeType.PERCENT, percent, null);
    }

    public static ChargeRule fixed(String code, String labelPt, String labelEn, long amount) {
        return new ChargeRule(code, labelPt, labelEn, ChargeType.FIXED, null, amount);
    }
}
