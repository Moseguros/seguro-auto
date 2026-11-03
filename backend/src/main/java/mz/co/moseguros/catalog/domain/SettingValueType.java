package mz.co.moseguros.catalog.domain;

import java.util.regex.Pattern;

/**
 * Tipo do valor de um parâmetro da plataforma (S2-02 §6.6). O valor é sempre guardado em texto.
 */
public enum SettingValueType {

    /** Centavos, maior que zero: {@code ^[1-9]\d{0,14}$}. */
    MONEY(Pattern.compile("^[1-9]\\d{0,14}$")),
    INTEGER(Pattern.compile("^-?\\d{1,9}$")),
    BOOLEAN(Pattern.compile("^(true|false)$")),
    /** 1 a 500 caracteres. */
    STRING(Pattern.compile("^[\\s\\S]{1,500}$"));

    private final Pattern format;

    SettingValueType(Pattern format) {
        this.format = format;
    }

    /** {@code true} se {@code value} é um valor válido deste tipo. */
    public boolean accepts(String value) {
        return value != null && format.matcher(value).matches();
    }
}
