package mz.co.moseguros.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import mz.co.moseguros.proposal.ProposalPaid;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Regista os eventos dos pagamentos publicados em qualquer thread (pedidos REST, envio assíncrono,
 * listeners do Spring Modulith), que o {@code PublishedEvents} do teste não vê. Perfil {@code test}:
 * entra no contexto partilhado sem criar outro.
 */
@Component
@Profile("test")
public class PaymentEventsRecorder {

    private final List<Object> published = new CopyOnWriteArrayList<>();

    @EventListener
    void on(PaymentSucceeded event) {
        published.add(event);
    }

    @EventListener
    void on(PaymentFailed event) {
        published.add(event);
    }

    @EventListener
    void on(ReferenceGenerated event) {
        published.add(event);
    }

    @EventListener
    void on(ProposalPaid event) {
        published.add(event);
    }

    public <T> List<T> of(Class<T> type) {
        List<T> list = new ArrayList<>();
        published.stream().filter(type::isInstance).map(type::cast).forEach(list::add);
        return list;
    }

    public <T> List<T> of(Class<T> type, Predicate<T> filter) {
        return of(type).stream().filter(filter).toList();
    }
}
