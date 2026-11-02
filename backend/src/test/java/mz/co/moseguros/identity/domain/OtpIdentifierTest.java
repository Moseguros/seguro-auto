package mz.co.moseguros.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.error.FieldValidationException;
import org.junit.jupiter.api.Test;

class OtpIdentifierTest {

    @Test
    void msisdnIsNormalizedAndMasked() {
        OtpIdentifier id = OtpIdentifier.parse("+258 84 123 4567", IdentifierType.MSISDN, "identifier");

        assertThat(id.value()).isEqualTo("258841234567");
        assertThat(id.destination()).isEqualTo("+258841234567");
        assertThat(id.masked()).isEqualTo("+258*****4567");
        assertThat(id.toString()).doesNotContain("841234567");
        assertThat(id.email()).isNull();
        assertThat(id.accepts(Channel.SMS)).isTrue();
        assertThat(id.accepts(Channel.WHATSAPP)).isTrue();
        assertThat(id.accepts(Channel.EMAIL)).isFalse();
    }

    @Test
    void emailIsNormalizedAndMasked() {
        OtpIdentifier id = OtpIdentifier.parse(" Ana@Exemplo.co.mz ", IdentifierType.EMAIL, "identifier");

        assertThat(id.value()).isEqualTo("ana@exemplo.co.mz");
        assertThat(id.destination()).isEqualTo("ana@exemplo.co.mz");
        assertThat(id.masked()).isEqualTo("a***@exemplo.co.mz");
        assertThat(id.msisdn()).isNull();
        assertThat(id.accepts(Channel.EMAIL)).isTrue();
        assertThat(id.accepts(Channel.SMS)).isFalse();
    }

    @Test
    void invalidValuesAreFieldErrors() {
        assertThatThrownBy(() -> OtpIdentifier.parse("123", IdentifierType.MSISDN, "identifier"))
                .isInstanceOfSatisfying(FieldValidationException.class, e -> {
                    assertThat(e.getField()).isEqualTo("identifier");
                    assertThat(e.getFieldCode()).isEqualTo("INVALID_MSISDN");
                    assertThat(e.getCode()).isEqualTo("VALIDATION_ERROR");
                });
        assertThatThrownBy(() -> OtpIdentifier.parse("x@", IdentifierType.EMAIL, "identifier"))
                .isInstanceOfSatisfying(FieldValidationException.class,
                        e -> assertThat(e.getFieldCode()).isEqualTo("INVALID_EMAIL"));
    }

    @Test
    void settingsGiveCompatibleChannelsInFallbackOrder() {
        OtpSettings settings = new OtpSettings();
        settings.update(List.of(Channel.SMS, Channel.WHATSAPP, Channel.EMAIL), Channel.EMAIL, true,
                List.of(Channel.WHATSAPP, Channel.EMAIL, Channel.SMS), 6, 300, 3, 60, 5);

        assertThat(settings.channelsFor(IdentifierType.MSISDN)).containsExactly(Channel.WHATSAPP, Channel.SMS);
        assertThat(settings.channelsFor(IdentifierType.EMAIL)).containsExactly(Channel.EMAIL);
        assertThat(settings.defaultChannelFor(IdentifierType.MSISDN)).contains(Channel.WHATSAPP);
        assertThat(settings.defaultChannelFor(IdentifierType.EMAIL)).contains(Channel.EMAIL);

        settings.update(List.of(Channel.SMS), Channel.SMS, true, List.of(Channel.SMS), 6, 300, 3, 60, 5);
        assertThat(settings.channelsFor(IdentifierType.EMAIL)).isEmpty();
        assertThat(settings.defaultChannelFor(IdentifierType.EMAIL)).isEmpty();
    }
}
