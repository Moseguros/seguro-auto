package mz.co.moseguros.identity.domain;

import static mz.co.moseguros.shared.domain.Channel.EMAIL;
import static mz.co.moseguros.shared.domain.Channel.SMS;
import static mz.co.moseguros.shared.domain.Channel.WHATSAPP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OtpSettingsTest {

    private final OtpSettings settings = new OtpSettings();

    @Test
    void validUpdateIsApplied() {
        settings.update(List.of(SMS, EMAIL), EMAIL, false, List.of(EMAIL, SMS), 8, 1800, 10, 15, 50);

        assertThat(settings.getEnabledChannels()).containsExactly(SMS, EMAIL);
        assertThat(settings.getDefaultChannel()).isEqualTo(EMAIL);
        assertThat(settings.isCustomerCanChoose()).isFalse();
        assertThat(settings.getFallbackOrder()).containsExactly(EMAIL, SMS);
        assertThat(settings.isEnabled(WHATSAPP)).isFalse();
        assertThat(settings.getCodeLength()).isEqualTo(8);
        assertThat(settings.getValiditySeconds()).isEqualTo(1800);
        assertThat(settings.getMaxAttempts()).isEqualTo(10);
        assertThat(settings.getResendAfterSeconds()).isEqualTo(15);
        assertThat(settings.getMaxRequestsPerHour()).isEqualTo(50);
    }

    @Test
    void emptyChannelsAreRejected() {
        assertCode(() -> update(List.of(), SMS, List.of()), "CHANNELS_EMPTY");
        assertCode(() -> update(null, SMS, List.of(SMS)), "CHANNELS_EMPTY");
    }

    @Test
    void duplicatedChannelsAreRejected() {
        assertCode(() -> update(List.of(SMS, SMS), SMS, List.of(SMS, SMS)), "CHANNELS_DUPLICATED");
    }

    @Test
    void defaultChannelMustBeEnabled() {
        assertCode(() -> update(List.of(SMS), EMAIL, List.of(SMS)), "DEFAULT_CHANNEL_NOT_ENABLED");
        assertCode(() -> update(List.of(SMS), null, List.of(SMS)), "DEFAULT_CHANNEL_NOT_ENABLED");
    }

    @Test
    void fallbackOrderMustHaveExactlyTheEnabledChannels() {
        assertCode(() -> update(List.of(SMS, EMAIL), SMS, List.of(SMS)), "FALLBACK_ORDER_MISMATCH");
        assertCode(() -> update(List.of(SMS, EMAIL), SMS, List.of(SMS, WHATSAPP)), "FALLBACK_ORDER_MISMATCH");
        assertCode(() -> update(List.of(SMS, EMAIL), SMS, List.of(SMS, SMS)), "FALLBACK_ORDER_MISMATCH");
        assertCode(() -> update(List.of(SMS), SMS, null), "FALLBACK_ORDER_MISMATCH");
    }

    @ParameterizedTest
    @CsvSource({
            "3,300,3,60,5", "9,300,3,60,5",
            "6,59,3,60,5", "6,1801,3,60,5",
            "6,300,0,60,5", "6,300,11,60,5",
            "6,300,3,14,5", "6,300,3,601,5",
            "6,300,3,60,0", "6,300,3,60,51"})
    void numbersOutOfRangeAreRejected(int codeLength, int validity, int attempts, int resend, int perHour) {
        assertCode(() -> settings.update(List.of(SMS), SMS, true, List.of(SMS), codeLength, validity, attempts,
                resend, perHour), "OUT_OF_RANGE");
    }

    @Test
    void failedUpdateLeavesSettingsUnchanged() {
        settings.update(List.of(SMS), SMS, true, List.of(SMS), 6, 300, 3, 60, 5);
        assertThatThrownBy(() -> settings.update(List.of(EMAIL), EMAIL, true, List.of(EMAIL), 6, 300, 3, 60, 99));
        assertThat(settings.getEnabledChannels()).containsExactly(SMS);
    }

    private void update(List<Channel> enabled, Channel defaultChannel, List<Channel> fallback) {
        settings.update(enabled, defaultChannel, true, fallback, 6, 300, 3, 60, 5);
    }

    private static void assertCode(Runnable action, String code) {
        assertThatThrownBy(action::run).isInstanceOfSatisfying(DomainException.class, e -> {
            assertThat(e.getCode()).isEqualTo(code);
            assertThat(e.getHttpStatus()).isEqualTo(400);
        });
    }
}
