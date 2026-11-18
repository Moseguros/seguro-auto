package mz.co.moseguros.payment.application.reference;

/**
 * Gerador das referências Ponto24 (contrato do S5-07 §1, criado no S5-02). Um só bean: o
 * {@code Ponto24ReferenceGenerator} do S5-07, igual em todos os ambientes, que segue
 * <b>exactamente</b> {@code docs/specs/interbancos/}. Até ele entrar, só os testes têm um gerador
 * ({@code TestReferenceGenerator}); sem bean, a entidade e referência não está disponível.
 */
public interface ReferenceGenerator {

    /** Consome um número de factura da entidade, na transacção de quem chama (MANDATORY). */
    GeneratedReference generate(ReferenceRequest request);

    /** Formato e dígitos de controlo para a entidade e o montante. */
    boolean isValid(String entity, String reference, long amountCentavos);
}
