package mz.co.moseguros.shared.domain;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Formato de matrícula configurável.
 *
 * @param code            código estável (ex.: {@code STANDARD})
 * @param label           descrição para listas de administração
 * @param pattern         expressão aplicada ao valor normalizado (maiúsculas, sem espaços
 *                        nem hífens), com grupos nomeados
 * @param displayTemplate reconstrói o valor formatado a partir dos grupos, com a sintaxe
 *                        {@code {grupo}} (ex.: {@code {l}-{n}-{p}}); não se usa
 *                        {@code ${grupo}} porque o Spring o resolveria como propriedade
 * @param active          só os formatos activos aceitam matrículas novas
 */
public record PlateFormat(String code, String label, Pattern pattern, String displayTemplate, boolean active) {

    public PlateFormat {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(pattern, "pattern");
        Objects.requireNonNull(displayTemplate, "displayTemplate");
    }
}
