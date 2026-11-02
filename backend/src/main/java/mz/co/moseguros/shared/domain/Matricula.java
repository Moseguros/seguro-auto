package mz.co.moseguros.shared.domain;

import java.util.Objects;

/**
 * Matrícula de veículo já validada: valor formatado (ex.: {@code ABC-123-MC}) e o
 * código do formato que a aceitou.
 *
 * <p>Matrículas novas criam-se só com {@link PlateParser#parse(String)}; valores lidos
 * da base de dados com {@link #restore(String, String)}, sem nova validação (o formato
 * pode entretanto ter sido desactivado).
 *
 * <p>JSON: string formatada.
 */
public record Matricula(String value, String formatCode) {

    public Matricula {
        Objects.requireNonNull(value, "value");
    }

    /** Reconstrói uma matrícula guardada, sem validar de novo. */
    public static Matricula restore(String value, String formatCode) {
        return new Matricula(value, formatCode);
    }

    /** Substitui por {@code *} todos os dígitos do valor formatado (ex.: {@code ABC-***-MC}). */
    public String masked() {
        return value.replaceAll("\\d", "*");
    }

    @Override
    public String toString() {
        return value;
    }
}
