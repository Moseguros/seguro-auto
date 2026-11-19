package mz.co.moseguros.payment.application;

import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.paymentcontracts.ReferencePaymentReceived;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor da fila {@code core.reference-payments} (S5-10 §2): cada pagamento lido num ficheiro de movimentos
 * MEPS ou BMEPS vai para o {@link ReferenceReconciliationService}. Corre em {@code SYSTEM} (não há utilizador)
 * e a reconciliação filtra sempre pelo {@code insurerId} da mensagem (que o conversor já comparou com a chave de
 * encaminhamento). Os resultados de negócio não lançam: a mensagem é confirmada; um erro técnico é repetido
 * pelo {@code paymentsListenerFactory} e, à sexta falha, vai para a {@code .dlq}.
 */
@Component
public class ReferencePaymentListener {

    private final ReferenceReconciliationService reconciliation;

    public ReferencePaymentListener(ReferenceReconciliationService reconciliation) {
        this.reconciliation = reconciliation;
    }

    @RabbitListener(id = "referencePayments", queues = PaymentsTopology.REFERENCE_PAYMENTS_QUEUE,
            containerFactory = "paymentsListenerFactory")
    @RunAsSystem
    public void on(ReferencePaymentReceived message) {
        reconciliation.handle(message);
    }
}
