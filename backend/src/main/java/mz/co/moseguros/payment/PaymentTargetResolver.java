package mz.co.moseguros.payment;

import java.util.UUID;

import mz.co.moseguros.shared.error.ErrorCodes;

/**
 * Implementada pelo módulo dono de cada tipo de alvo (modelo transversal §1.1): o {@code proposal}
 * para {@link PaymentTargetType#PROPOSAL} (S5-02), o {@code policy} para prestações, renovações,
 * endossos e inclusões de frota (S7). O {@code payment} encontra as implementações como beans Spring.
 */
public interface PaymentTargetResolver {

    boolean supports(PaymentTargetType targetType);

    /**
     * O alvo, lido no âmbito de quem chama.
     *
     * @throws mz.co.moseguros.shared.error.DomainException se não existir ou não for visível (404 do
     *         módulo dono, ex.: {@code PROPOSAL_NOT_FOUND}) ou não for pagável (409
     *         {@code PAYMENT_TARGET_NOT_PAYABLE})
     */
    PayableTarget resolve(PaymentTargetType targetType, UUID targetId);

    /**
     * Código de "não encontrado" do alvo (ex.: {@code PROPOSAL_NOT_FOUND}), devolvido quando quem pede
     * não é o pagador do alvo (S5-02 §4, passo 4).
     */
    default String notFoundCode() {
        return ErrorCodes.NOT_FOUND;
    }
}
