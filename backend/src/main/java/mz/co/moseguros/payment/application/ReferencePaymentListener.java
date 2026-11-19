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
 *
 * <p>Um só consumidor ({@code concurrency = "1-1"}: mínimo e máximo, por cima dos 2 a 4 da fábrica; só
 * {@code "1"} deixava o máximo da fábrica, 4, e o contentor podia crescer): os registos de um ficheiro tratam-se pela ordem de {@code lineNo} (S5-10 §3), que é a ordem da fila; com dois consumidores, dois
 * registos da mesma referência podiam ser tratados em paralelo e o {@code for update} só garantia que um deles
 * confirmava, não qual. A repetição sem estado de um erro técnico corre na mesma thread, por isso também não
 * troca a ordem.
 */
@Component
public class ReferencePaymentListener {

    /** Um consumidor só (mínimo e máximo), para manter a ordem de {@code lineNo}. */
    static final String REFERENCE_PAYMENTS_CONCURRENCY = "1-1";

    private final ReferenceReconciliationService reconciliation;

    public ReferencePaymentListener(ReferenceReconciliationService reconciliation) {
        this.reconciliation = reconciliation;
    }

    @RabbitListener(id = "referencePayments", queues = PaymentsTopology.REFERENCE_PAYMENTS_QUEUE,
            containerFactory = "paymentsListenerFactory", concurrency = REFERENCE_PAYMENTS_CONCURRENCY)
    @RunAsSystem
    public void on(ReferencePaymentReceived message) {
        reconciliation.handle(message);
    }
}
