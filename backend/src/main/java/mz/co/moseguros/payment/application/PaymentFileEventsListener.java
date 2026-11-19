package mz.co.moseguros.payment.application;

import mz.co.moseguros.paymentcontracts.PaymentFileProcessed;
import mz.co.moseguros.paymentcontracts.PaymentFileRejected;
import mz.co.moseguros.paymentcontracts.PaymentSourcePolled;
import mz.co.moseguros.paymentcontracts.PaymentsTopology;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumidor da fila {@code core.payment-file-events} (S5-10 §2, §4): ficheiros processados e rejeitados e
 * resultados das leituras das fontes, gravados pelo {@link PaymentFileEventsService}. Corre em {@code SYSTEM}
 * e filtra sempre pelo {@code insurerId} da mensagem.
 */
@Component
public class PaymentFileEventsListener {

    private final PaymentFileEventsService service;

    public PaymentFileEventsListener(PaymentFileEventsService service) {
        this.service = service;
    }

    @RabbitListener(id = "paymentFileEvents", queues = PaymentsTopology.PAYMENT_FILE_EVENTS_QUEUE,
            containerFactory = "paymentsListenerFactory")
    @RunAsSystem
    public void on(@Payload Object message) {
        if (message instanceof PaymentFileProcessed processed) {
            service.processed(processed);
        } else if (message instanceof PaymentFileRejected rejected) {
            service.rejected(rejected);
        } else if (message instanceof PaymentSourcePolled polled) {
            service.polled(polled);
        } else {
            // O conversor só deixa passar os três tipos desta fila.
            throw new AmqpRejectAndDontRequeueException("Unexpected payment file event "
                    + (message == null ? null : message.getClass().getSimpleName()));
        }
    }
}
