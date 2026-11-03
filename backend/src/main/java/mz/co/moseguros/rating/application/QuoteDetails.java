package mz.co.moseguros.rating.application;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.rating.QuoteStatus;
import mz.co.moseguros.rating.domain.Quote;
import mz.co.moseguros.rating.domain.QuoteRequest;
import mz.co.moseguros.rating.domain.StoredBreakdown;
import mz.co.moseguros.rating.domain.tariff.RatedDriver;
import mz.co.moseguros.rating.domain.tariff.RatedVehicle;
import mz.co.moseguros.rating.domain.tariff.RatingInput.RequestedCoverage;

/**
 * Cotação com o que a API mostra: o estado efectivo, a seguradora, a versão do produto e a
 * decomposição gravada.
 *
 * @param insurer   {@code null} se a seguradora já não existir (não acontece com dados íntegros)
 * @param version   {@code null} se a versão não for legível
 * @param breakdown {@code null} em {@code NOT_AVAILABLE}
 */
public record QuoteDetails(Quote quote, QuoteStatus status, InsurerProfileView insurer, ProductVersionView version,
                           StoredBreakdown breakdown) {

    private static final Collator COLLATOR = Collator.getInstance(Locale.forLanguageTag("pt-MZ"));

    /**
     * Ordem da resposta (S3-02 §4): primeiro as com preço por {@code totalAmount} crescente (empate:
     * nome da seguradora, depois do produto); depois as {@code NOT_AVAILABLE} por nome da seguradora.
     */
    public static final Comparator<QuoteDetails> ORDER = Comparator
            .comparingInt(QuoteDetails::rank)
            .thenComparing(d -> d.quote().getTotalAmount(), Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(QuoteDetails::insurerName, COLLATOR)
            .thenComparing(QuoteDetails::productName, COLLATOR);

    public QuoteDetails {
        Objects.requireNonNull(quote, "quote");
        Objects.requireNonNull(status, "status");
    }

    private int rank() {
        return switch (status) {
            case OFFERED -> 0;
            case USED, EXPIRED -> 1;
            case NOT_AVAILABLE -> 2;
        };
    }

    public String insurerName() {
        return insurer == null || insurer.displayName() == null ? "" : insurer.displayName();
    }

    public String productName() {
        return version == null || version.productName() == null ? "" : version.productName();
    }

    /**
     * Pedido de cotações com as cotações, pela ordem da resposta.
     *
     * @param requestedCoverages tal como pedidas
     */
    public record QuoteRequestDetails(QuoteRequest request, RatedVehicle vehicle, RatedDriver driver,
                                      List<RequestedCoverage> requestedCoverages, List<QuoteDetails> quotes) {

        public QuoteRequestDetails {
            quotes = quotes.stream().sorted(ORDER).toList();
            requestedCoverages = List.copyOf(requestedCoverages);
        }
    }
}
