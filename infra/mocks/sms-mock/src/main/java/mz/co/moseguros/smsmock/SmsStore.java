package mz.co.moseguros.smsmock;

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/** Guarda em memória as últimas {@value #CAPACITY} mensagens (SMS, WhatsApp e push), a mais recente primeiro. */
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

    /** SMS recebido. */
    public SmsMessage add(String to, String text) {
        return store(new SmsMessage(UUID.randomUUID().toString(), "SMS", to, text, null, null, clock.instant()));
    }

    /** Mensagem de WhatsApp com modelo; o texto mostra o modelo e os parâmetros. */
    public SmsMessage addWhatsApp(String to, String template, Map<String, String> params) {
        Map<String, String> copy = params == null ? Map.of() : Map.copyOf(params);
        String text = template + " " + copy.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
        return store(new SmsMessage(UUID.randomUUID().toString(), "WHATSAPP", to, text, template, copy,
                clock.instant()));
    }

    /** Push (S6-04): o texto é o título e o corpo; os parâmetros são os dados. */
    public SmsMessage addPush(String token, String title, String body, Map<String, String> data) {
        Map<String, String> copy = data == null ? Map.of() : Map.copyOf(data);
        return store(new SmsMessage(UUID.randomUUID().toString(), "PUSH", token, title + ": " + body, null, copy,
                clock.instant()));
    }

    private synchronized SmsMessage store(SmsMessage message) {
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
