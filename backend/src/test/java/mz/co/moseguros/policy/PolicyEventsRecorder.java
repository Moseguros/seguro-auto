package mz.co.moseguros.policy;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Regista os {@link PolicyIssued} publicados em qualquer thread (os listeners do Spring Modulith são
 * assíncronos). Perfil {@code test}: entra no contexto partilhado sem criar outro.
 */
@Component
@Profile("test")
public class PolicyEventsRecorder {

    private final List<PolicyIssued> issued = new CopyOnWriteArrayList<>();

    @EventListener
    void on(PolicyIssued event) {
        issued.add(event);
    }

    public List<PolicyIssued> issued(Predicate<PolicyIssued> filter) {
        return issued.stream().filter(filter).toList();
    }
}
