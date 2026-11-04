package mz.co.moseguros.proposal.domain;

import java.util.Arrays;
import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * O que o subscritor pede ao cliente ({@code info_request_items}, S4-05 §7). Com {@link #INSPECTION}
 * a vistoria volta a {@code PENDING}. Textos em {@code proposal.infoRequestItem.<CODE>}.
 */
@Schema(enumAsRef = true)
public enum InfoRequestItem {
    DECLARATIONS,
    SUITABILITY,
    INSPECTION,
    KYC,
    POLICYHOLDER,
    OTHER;

    /** Valor do pedido; vazio se for desconhecido. */
    public static Optional<InfoRequestItem> parse(String value) {
        return value == null ? Optional.empty()
                : Arrays.stream(values()).filter(v -> v.name().equals(value.strip())).findFirst();
    }
}
