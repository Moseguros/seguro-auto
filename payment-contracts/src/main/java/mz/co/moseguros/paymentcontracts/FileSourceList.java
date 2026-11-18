package mz.co.moseguros.paymentcontracts;

import java.util.List;

/**
 * Resposta de {@code GET /internal/v1/payment-file-sources} (S5-09): todas as fontes de
 * ficheiros de movimentos. Schema: {@code schemas/file-source-list.v1.json}.
 */
public record FileSourceList(List<FileSourceDescriptor> sources) {

    public FileSourceList {
        sources = sources == null ? List.of() : List.copyOf(sources);
    }
}
