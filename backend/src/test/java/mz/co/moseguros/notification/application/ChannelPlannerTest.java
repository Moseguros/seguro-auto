package mz.co.moseguros.notification.application;

import static mz.co.moseguros.notification.domain.MessageChannel.EMAIL;
import static mz.co.moseguros.notification.domain.MessageChannel.PUSH;
import static mz.co.moseguros.notification.domain.MessageChannel.SMS;
import static mz.co.moseguros.notification.domain.MessageChannel.WHATSAPP;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.notification.NotificationRecipient;
import mz.co.moseguros.notification.domain.MessageChannel;
import mz.co.moseguros.notification.infrastructure.NotificationProperties;
import mz.co.moseguros.shared.domain.Msisdn;
import org.junit.jupiter.api.Test;

/** Escolha dos canais (S6-04 §4, tabela). */
class ChannelPlannerTest {

    static final Set<MessageChannel> ALL = EnumSet.of(PUSH, WHATSAPP, SMS, EMAIL);
    static final UUID D1 = UUID.randomUUID();
    static final UUID D2 = UUID.randomUUID();

    final ChannelPlanner planner = new ChannelPlanner(NotificationProperties.DEFAULT_ORDER);

    @Test
    void optionalCategoryUsesTheFirstAvailableAndAllowedChannel() {
        assertThat(channels(planner.plan(ctx(NotificationCategory.PROPOSAL, full(), ALL, List.of(), Map.of()))))
                .containsExactly(WHATSAPP);
        assertThat(planner.plan(ctx(NotificationCategory.PROPOSAL, full(), ALL, List.of(D1, D2), Map.of())).rows())
                .containsExactly(new ChannelPlanner.Planned(PUSH, D1), new ChannelPlanner.Planned(PUSH, D2));
        assertThat(channels(planner.plan(ctx(NotificationCategory.PROPOSAL, full(), ALL, List.of(D1),
                Map.of(PUSH, false, WHATSAPP, false))))).containsExactly(SMS);
        // sem modelo WhatsApp activo
        assertThat(channels(planner.plan(ctx(NotificationCategory.QUOTE, full(), EnumSet.of(PUSH, SMS, EMAIL),
                List.of(), Map.of())))).containsExactly(SMS);
        // só e-mail verificado
        assertThat(channels(planner.plan(ctx(NotificationCategory.QUOTE, emailOnly(), ALL, List.of(), Map.of()))))
                .containsExactly(EMAIL);
    }

    @Test
    void optionalCategoryWithNothingAllowedOnlyHasTheInbox() {
        ChannelPlanner.Plan plan = planner.plan(ctx(NotificationCategory.PROPOSAL, full(), ALL, List.of(D1),
                Map.of(PUSH, false, WHATSAPP, false, SMS, false, EMAIL, false)));
        assertThat(plan.rows()).isEmpty();
        assertThat(plan.undeliverable()).isFalse();
        assertThat(planner.plan(ctx(NotificationCategory.PROPOSAL, none(), ALL, List.of(), Map.of())).rows()).isEmpty();
    }

    @Test
    void mandatoryCategoryUsesPushAndOneExternalChannel() {
        ChannelPlanner.Plan plan = planner.plan(ctx(NotificationCategory.PAYMENT, full(), ALL, List.of(D1), Map.of()));
        assertThat(plan.rows()).containsExactly(new ChannelPlanner.Planned(PUSH, D1),
                new ChannelPlanner.Planned(WHATSAPP, null));
        assertThat(channels(planner.plan(ctx(NotificationCategory.POLICY, full(), ALL, List.of(), Map.of(WHATSAPP,
                false))))).containsExactly(SMS);
    }

    @Test
    void mandatoryCategoryIgnoresPreferencesWhenNothingIsAllowed() {
        ChannelPlanner.Plan plan = planner.plan(ctx(NotificationCategory.PAYMENT, full(), ALL, List.of(D1),
                Map.of(PUSH, false, WHATSAPP, false, SMS, false, EMAIL, false)));
        assertThat(channels(plan)).containsExactly(WHATSAPP);
        assertThat(plan.undeliverable()).isFalse();
    }

    @Test
    void mandatoryCategoryWithoutAnyContactIsUndeliverable() {
        ChannelPlanner.Plan plan = planner.plan(ctx(NotificationCategory.LEGAL, none(), ALL, List.of(), Map.of()));
        assertThat(plan.rows()).isEmpty();
        assertThat(plan.undeliverable()).isTrue();
        // com push mas sem contactos: o push sai e o canal externo falta
        ChannelPlanner.Plan withPush = planner.plan(ctx(NotificationCategory.LEGAL, none(), ALL, List.of(D1),
                Map.of()));
        assertThat(channels(withPush)).containsExactly(PUSH);
        assertThat(withPush.undeliverable()).isTrue();
    }

    @Test
    void fallbackGoesToTheNextChannelNotYetTried() {
        ChannelPlanner.Context optional = ctx(NotificationCategory.PROPOSAL, full(), ALL, List.of(D1),
                Map.of(WHATSAPP, false));
        assertThat(planner.next(optional, PUSH, List.of(PUSH))).contains(SMS);
        assertThat(planner.next(optional, SMS, List.of(PUSH, SMS))).contains(EMAIL);
        assertThat(planner.next(optional, EMAIL, List.of(PUSH, SMS, EMAIL))).isEmpty();
        assertThat(planner.next(ctx(NotificationCategory.PROPOSAL, phoneOnly(), ALL, List.of(), Map.of()), SMS,
                List.of(WHATSAPP, SMS))).isEmpty();

        ChannelPlanner.Context mandatory = ctx(NotificationCategory.PAYMENT, full(), ALL, List.of(D1),
                Map.of(SMS, false, EMAIL, false));
        assertThat(planner.next(mandatory, WHATSAPP, List.of(PUSH, WHATSAPP))).contains(SMS);
        assertThat(planner.next(mandatory, SMS, List.of(PUSH, WHATSAPP, SMS))).contains(EMAIL);
    }

    @Test
    void contactAvailabilityIgnoresTemplates() {
        assertThat(ChannelPlanner.contactAvailable(PUSH, full(), true)).isTrue();
        assertThat(ChannelPlanner.contactAvailable(PUSH, full(), false)).isFalse();
        assertThat(ChannelPlanner.contactAvailable(SMS, emailOnly(), false)).isFalse();
        assertThat(ChannelPlanner.contactAvailable(EMAIL, emailOnly(), false)).isTrue();
        assertThat(ChannelPlanner.contactAvailable(WHATSAPP, unverifiedPhone(), false)).isFalse();
    }

    private static List<MessageChannel> channels(ChannelPlanner.Plan plan) {
        return plan.rows().stream().map(ChannelPlanner.Planned::channel).toList();
    }

    private static ChannelPlanner.Context ctx(NotificationCategory category, NotificationRecipient recipient,
                                              Set<MessageChannel> templates, List<UUID> devices,
                                              Map<MessageChannel, Boolean> preferences) {
        return new ChannelPlanner.Context(category, recipient, templates, devices, preferences);
    }

    private static NotificationRecipient full() {
        return new NotificationRecipient(UUID.randomUUID(), Msisdn.of("841234567"), true, "ana@exemplo.co.mz", true,
                "pt", true);
    }

    private static NotificationRecipient phoneOnly() {
        return new NotificationRecipient(UUID.randomUUID(), Msisdn.of("841234567"), true, null, false, "pt", true);
    }

    private static NotificationRecipient unverifiedPhone() {
        return new NotificationRecipient(UUID.randomUUID(), Msisdn.of("841234567"), false, null, false, "pt", true);
    }

    private static NotificationRecipient emailOnly() {
        return new NotificationRecipient(UUID.randomUUID(), null, false, "ana@exemplo.co.mz", true, "pt", true);
    }

    private static NotificationRecipient none() {
        return new NotificationRecipient(UUID.randomUUID(), Msisdn.of("841234567"), false, "ana@exemplo.co.mz", false,
                "pt", true);
    }
}
