package mz.co.moseguros.payment.application;

import java.util.UUID;

import mz.co.moseguros.payment.domain.PaymentExceptionCase;
import mz.co.moseguros.payment.domain.PaymentExceptionRepository;
import mz.co.moseguros.payment.domain.PaymentExceptionType;
import mz.co.moseguros.payment.domain.PaymentIntent;
import mz.co.moseguros.payment.domain.PaymentIntentRepository;
import mz.co.moseguros.payment.domain.ReferencePaymentRepository;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos para o financeiro criados pelo núcleo (S5-02 §9): {@code TARGET_NOT_PAYABLE} quando o dono do
 * alvo recebeu um pagamento que já não pode aceitar. Nunca emite nada nem movimenta dinheiro: o
 * financeiro resolve no S5-10/S5-12 (uma devolução faz-se fora da plataforma).
 */
@Service
public class PaymentExceptionService {

    private static final Logger log = LoggerFactory.getLogger(PaymentExceptionService.class);

    private final PaymentIntentRepository intents;
    private final ReferencePaymentRepository references;
    private final PaymentExceptionRepository exceptions;

    public PaymentExceptionService(PaymentIntentRepository intents, ReferencePaymentRepository references,
                                   PaymentExceptionRepository exceptions) {
        this.intents = intents;
        this.references = references;
        this.exceptions = exceptions;
    }

    /** Ver {@code PaymentApi.raiseTargetNotPayable}; repetido para a mesma intenção não cria outro caso. */
    @Transactional
    public void raiseTargetNotPayable(UUID intentId, String detail) {
        PaymentIntent intent = intents.lock(intentId).orElseThrow(() -> DomainException.of(
                ErrorCodes.PAYMENT_INTENT_NOT_FOUND, "Payment intent not found"));
        if (exceptions.existsByIntentIdAndType(intentId, PaymentExceptionType.TARGET_NOT_PAYABLE)) {
            return;
        }
        exceptions.save(PaymentExceptionCase.targetNotPayable(intent,
                references.findByIntentId(intentId).orElse(null), detail));
        log.warn("Payment received for a target that is no longer payable intentId={} reference={} detail={}",
                intentId, intent.getReference(), detail);
    }
}
