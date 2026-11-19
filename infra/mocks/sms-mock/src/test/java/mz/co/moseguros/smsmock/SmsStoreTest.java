package mz.co.moseguros.smsmock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class SmsStoreTest {

    @Test
    void listsMostRecentFirst() {
        SmsStore store = new SmsStore();
        store.add("+258841111111", "primeiro");
        store.add("+258842222222", "segundo");

        assertThat(store.list()).extracting(SmsMessage::text).containsExactly("segundo", "primeiro");
    }

    @Test
    void keepsOnlyTheLastFiveHundred() {
        SmsStore store = new SmsStore();
        for (int i = 0; i < SmsStore.CAPACITY + 10; i++) {
            store.add("+258841234567", "sms " + i);
        }

        List<SmsMessage> messages = store.list();
        assertThat(messages).hasSize(SmsStore.CAPACITY);
        assertThat(messages.get(0).text()).isEqualTo("sms 509");
        assertThat(messages.get(messages.size() - 1).text()).isEqualTo("sms 10");
    }

    @Test
    void whatsAppMessagesShowTheTemplateAndParams() {
        SmsStore store = new SmsStore();
        SmsMessage message = store.addWhatsApp("+258841234567", "moseguros_otp", java.util.Map.of("code", "012345"));

        assertThat(message.channel()).isEqualTo("WHATSAPP");
        assertThat(message.text()).isEqualTo("moseguros_otp {code=012345}");
        assertThat(store.list()).containsExactly(message);
        assertThat(store.add("+258841234567", "x").channel()).isEqualTo("SMS");
    }

    @Test
    void pushMessagesShowTitleAndBody() {
        SmsStore store = new SmsStore();
        SmsMessage message = store.addPush("…abcd1234", "Pagamento recebido", "Recebemos o pagamento.",
                java.util.Map.of("notificationId", "n-1"));

        assertThat(message.channel()).isEqualTo("PUSH");
        assertThat(message.text()).isEqualTo("Pagamento recebido: Recebemos o pagamento.");
        assertThat(message.params()).containsEntry("notificationId", "n-1");
        assertThat(message.template()).isNull();
    }

    @Test
    void clearRemovesEverything() {
        SmsStore store = new SmsStore();
        store.add("+258841234567", "teste");

        store.clear();

        assertThat(store.list()).isEmpty();
    }
}
