package mz.co.moseguros.rating;

import java.util.UUID;

import mz.co.moseguros.rating.application.QuoteQueryService;
import mz.co.moseguros.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface pública do módulo Tarifação (modelo transversal §3): leitura de uma cotação e o seu
 * uso numa proposta (S4). Os métodos correm no âmbito de quem chama (RLS). {@code rateRenewal} é
 * do S7 e {@code quoteForUssd} do S12.
 */
@Service
public class RatingApi {

    private final QuoteQueryService quotes;

    public RatingApi(QuoteQueryService quotes) {
        this.quotes = quotes;
    }

    /**
     * Cotação com os dados do pedido; {@code status} é o estado efectivo.
     *
     * @throws DomainException 404 {@code QUOTE_NOT_FOUND} se não for visível
     */
    public QuoteView quote(UUID quoteId) {
        return quotes.view(quoteId);
    }

    /**
     * Marca a cotação como usada ({@code USED}) na transacção de quem chama (a da proposta); sem
     * transacção lança {@code IllegalTransactionStateException}. Lê a linha com
     * {@code PESSIMISTIC_WRITE}: duas propostas com a mesma cotação não a usam as duas.
     *
     * @throws DomainException 404 {@code QUOTE_NOT_FOUND}; 409 {@code QUOTE_NOT_AVAILABLE},
     *                         {@code QUOTE_ALREADY_USED} ou {@code QUOTE_EXPIRED}
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void markUsed(UUID quoteId) {
        quotes.markUsed(quoteId);
    }
}
