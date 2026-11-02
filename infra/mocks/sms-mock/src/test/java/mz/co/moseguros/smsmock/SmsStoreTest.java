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
    void clearRemovesEverything() {
        SmsStore store = new SmsStore();
        store.add("+258841234567", "teste");

        store.clear();

        assertThat(store.list()).isEmpty();
    }
}
