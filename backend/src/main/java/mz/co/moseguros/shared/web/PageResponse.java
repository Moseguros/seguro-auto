package mz.co.moseguros.shared.web;

import java.util.List;
import java.util.function.Function;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

/**
 * Página de resultados (AGENTS.md §4): {@code { content, page, size, totalElements }}.
 *
 * @param page número da página, a começar em 0
 */
public record PageResponse<T>(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<T> content,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
        int page,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
        int size,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        long totalElements) {

    public static <S, T> PageResponse<T> of(Page<S> page, Function<? super S, ? extends T> mapper) {
        List<T> content = page.getContent().stream().<T>map(mapper).toList();
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
