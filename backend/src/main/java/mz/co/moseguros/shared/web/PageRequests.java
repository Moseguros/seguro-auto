package mz.co.moseguros.shared.web;

import java.util.Locale;
import java.util.Set;

import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Lê os parâmetros de paginação de AGENTS.md §4 ({@code ?page=0&size=20&sort=createdAt,desc}),
 * aceitando só as propriedades de ordenação indicadas pelo recurso.
 */
public final class PageRequests {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PageRequests() {
    }

    /**
     * @param sort         {@code propriedade} ou {@code propriedade,asc|desc}; {@code null} = {@code defaultSort}
     * @param allowedSorts propriedades aceites
     * @throws FieldValidationException {@code OUT_OF_RANGE} em {@code page}/{@code size} ou
     *                                  {@code INVALID_SORT} em {@code sort}
     */
    public static Pageable of(Integer page, Integer size, String sort, Set<String> allowedSorts, Sort defaultSort) {
        int p = page == null ? 0 : page;
        int s = size == null ? DEFAULT_SIZE : size;
        if (p < 0) {
            throw new FieldValidationException("page", FieldErrorCodes.OUT_OF_RANGE, "page must be 0 or more");
        }
        if (s < 1 || s > MAX_SIZE) {
            throw new FieldValidationException("size", FieldErrorCodes.OUT_OF_RANGE,
                    "size must be between 1 and " + MAX_SIZE);
        }
        return PageRequest.of(p, s, sort == null || sort.isBlank() ? defaultSort : parseSort(sort, allowedSorts));
    }

    static Sort parseSort(String raw, Set<String> allowed) {
        String[] parts = raw.split(",");
        String property = parts[0].trim();
        if (!allowed.contains(property) || parts.length > 2) {
            throw invalidSort();
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length == 2) {
            String value = parts[1].trim().toLowerCase(Locale.ROOT);
            if (value.equals("desc")) {
                direction = Sort.Direction.DESC;
            } else if (!value.equals("asc")) {
                throw invalidSort();
            }
        }
        return Sort.by(direction, property);
    }

    private static FieldValidationException invalidSort() {
        return new FieldValidationException("sort", FieldErrorCodes.INVALID_SORT, "Unsupported sort");
    }
}
