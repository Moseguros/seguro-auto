package mz.co.moseguros.smsmock;

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

/** Guarda em memória as últimas {@value #CAPACITY} mensagens, a mais recente primeiro. */
@Component
public class SmsStore {

    static final int CAPACITY = 500;

    private final Deque<SmsMessage> messages = new ArrayDeque<>();
    private final Clock clock;

    public SmsStore() {
        this(Clock.systemUTC());
    }

    SmsStore(Clock clock) {
        this.clock = clock;
    }

    public synchronized SmsMessage add(String to, String text) {
        SmsMessage message = new SmsMessage(UUID.randomUUID().toString(), to, text, clock.instant());
        messages.addFirst(message);
        while (messages.size() > CAPACITY) {
            messages.removeLast();
        }
        return message;
    }

    public synchronized List<SmsMessage> list() {
        return List.copyOf(messages);
    }

    public synchronized void clear() {
        messages.clear();
    }
}
