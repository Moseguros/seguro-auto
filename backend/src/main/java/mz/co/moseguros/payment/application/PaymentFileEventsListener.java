package mz.co.moseguros.payment.application;

import mz.co.moseguros.paymentcontracts.PaymentFileProcessed;
import mz.co.moseguros.paymentcontracts.PaymentFileRejected;
import mz.co.moseguros.paymentcontracts.PaymentSourcePolled;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor da fila {@code core.payment-file-events} (S5-10 §2, §4): ficheiros processados e rejeitados e
 * resultados das leituras das fontes, gravados pelo {@link PaymentFileEventsService}. Corre em {@code SYSTEM}
 * e filtra sempre pelo {@code insurerId} da mensagem. O conversor só deixa chegar os três tipos desta fila.
 */
@Component
@RabbitListener(id = "paymentFileEvents", queues = PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE,
        containerFactory = "paymentsListenerFactory")
public class PaymentFileEventsListener {

    private final PaymentFileEventsService service;

    public PaymentFileEventsListener(PaymentFileEventsService service) {
        this.service = service;
    }

    @RabbitHandler
    @RunAsSystem
    public void on(PaymentFileProcessed message) {
        service.processed(message);
    }

    @RabbitHandler
    @RunAsSystem
    public void on(PaymentFileRejected message) {
        service.rejected(message);
    }

    @RabbitHandler
    @RunAsSystem
    public void on(PaymentSourcePolled message) {
        service.polled(message);
    }
}
