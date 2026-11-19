package mz.co.moseguros.notification.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationRecipient;
import mz.co.moseguros.notification.domain.MessageChannel;

/**
 * Escolha dos canais de uma notificação (S6-04 §4), sem base de dados.
 *
 * <ul>
 *   <li><b>Disponível</b>: existe modelo activo para o canal e a língua (no WhatsApp com o nome do modelo
 *       aprovado) e: {@code PUSH} → pelo menos um dispositivo activo; {@code SMS}/{@code WHATSAPP} → telemóvel
 *       verificado; {@code EMAIL} → e-mail verificado.</li>
 *   <li><b>Permitido</b>: a preferência (categoria, canal) está activa (sem linha = activa).</li>
 *   <li>Não obrigatória: o primeiro disponível e permitido pela ordem; {@code PUSH} → uma linha por
 *       dispositivo.</li>
 *   <li>Obrigatória: {@code PUSH} (se disponível e permitido) <b>e</b> o primeiro disponível e permitido entre
 *       os outros; se nenhum for permitido, o primeiro disponível ignorando as preferências.</li>
 * </ul>
 */
public final class ChannelPlanner {

    private final List<MessageChannel> order;

    public ChannelPlanner(List<MessageChannel> order) {
        this.order = List.copyOf(order);
    }

    public List<MessageChannel> order() {
        return order;
    }

    /** Linhas a criar em {@code notify}. */
    public Plan plan(Context context) {
        List<Planned> rows = new ArrayList<>();
        if (!context.category().mandatory()) {
            for (MessageChannel channel : order) {
                if (context.available(channel) && context.allowed(channel)) {
                    addRows(context, channel, rows);
                    break;
                }
            }
            return new Plan(rows, false);
        }
        if (context.available(MessageChannel.PUSH) && context.allowed(MessageChannel.PUSH)) {
            addRows(context, MessageChannel.PUSH, rows);
        }
        Optional<MessageChannel> external = firstExternal(context, order, Set.of());
        external.ifPresent(channel -> rows.add(new Planned(channel, null)));
        return new Plan(rows, external.isEmpty());
    }

    /**
     * Canal seguinte depois de uma falha definitiva de {@code from} (S6-04 §4, passo 6): o próximo na ordem,
     * disponível e permitido (nas obrigatórias, ignorando as preferências se nenhum for permitido), ainda não
     * tentado nesta notificação. Nas obrigatórias nunca é o {@code PUSH} (corre em paralelo).
     */
    public Optional<MessageChannel> next(Context context, MessageChannel from, Collection<MessageChannel> tried) {
        int start = order.indexOf(from);
        List<MessageChannel> after = start < 0 ? order : order.subList(start + 1, order.size());
        if (context.category().mandatory()) {
            return firstExternal(context, after, Set.copyOf(tried));
        }
        for (MessageChannel channel : after) {
            if (!tried.contains(channel) && context.available(channel) && context.allowed(channel)) {
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }

    /** Primeiro canal sem push, disponível e permitido; senão o primeiro disponível (preferências ignoradas). */
    private static Optional<MessageChannel> firstExternal(Context context, List<MessageChannel> candidates,
                                                          Set<MessageChannel> tried) {
        List<MessageChannel> options = candidates.stream()
                .filter(c -> c != MessageChannel.PUSH && !tried.contains(c) && context.available(c))
                .toList();
        return options.stream().filter(context::allowed).findFirst().or(() -> options.stream().findFirst());
    }

    private static void addRows(Context context, MessageChannel channel, List<Planned> rows) {
        if (channel == MessageChannel.PUSH) {
            context.devices().forEach(device -> rows.add(new Planned(MessageChannel.PUSH, device)));
        } else {
            rows.add(new Planned(channel, null));
        }
    }

    /** Contactos e dispositivos disponíveis, sem olhar para os modelos ({@code available} das preferências). */
    public static boolean contactAvailable(MessageChannel channel, NotificationRecipient recipient,
                                           boolean hasDevices) {
        return switch (channel) {
            case PUSH -> hasDevices;
            case SMS, WHATSAPP -> recipient.msisdn() != null && recipient.msisdnVerified();
            case EMAIL -> recipient.email() != null && recipient.emailVerified();
            case INBOX -> false;
        };
    }

    /**
     * Dados para escolher os canais.
     *
     * @param templateChannels canais com modelo activo para o código e a língua (WhatsApp só com o nome do modelo)
     * @param devices          dispositivos activos do destinatário
     * @param preferences      preferências gravadas para a categoria (sem entrada = activa)
     */
    public record Context(NotificationCategory category, NotificationRecipient recipient,
                          Set<MessageChannel> templateChannels, List<UUID> devices,
                          Map<MessageChannel, Boolean> preferences) {

        public boolean available(MessageChannel channel) {
            return templateChannels.contains(channel) && contactAvailable(channel, recipient, !devices.isEmpty());
        }

        public boolean allowed(MessageChannel channel) {
            return preferences.getOrDefault(channel, Boolean.TRUE);
        }
    }

    /** Uma linha planeada ({@code deviceId} só no push). */
    public record Planned(MessageChannel channel, UUID deviceId) {
    }

    /**
     * Resultado.
     *
     * @param undeliverable categoria obrigatória sem nenhum canal externo disponível
     */
    public record Plan(List<Planned> rows, boolean undeliverable) {
    }
}
