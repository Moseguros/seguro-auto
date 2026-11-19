package mz.co.moseguros.payment.application.reference;

/**
 * Gerador das referências Ponto24 (contrato do S5-07 §1, criado no S5-02). Um só bean: o
 * {@link Ponto24ReferenceGenerator}, igual em todos os ambientes (incluindo {@code prod}), que segue
 * <b>exactamente</b> {@code docs/specs/interbancos/}. Não há gerador mock.
 */
public interface ReferenceGenerator {

    /** Consome um número de factura da entidade, na transacção de quem chama (MANDATORY). */
    GeneratedReference generate(ReferenceRequest request);

    /** Formato e dígitos de controlo para a entidade e o montante. */
    boolean isValid(String entity, String reference, long amountCentavos);
}
