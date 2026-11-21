package mz.co.moseguros.notification.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Normalização e tamanho dos SMS (S6-04 §5). */
class SmsTextTest {

    @Test
    void typographyAndAccentsAreFolded() {
        assertThat(SmsText.normalize("Apólice emitida — válida até 10/02/2028", true))
                .isEqualTo("Apolice emitida - valida ate 10/02/2028");
        assertThat(SmsText.normalize("“a” ‘b’ c–d e… 1 000 nº 1ª ção", true))
                .isEqualTo("\"a\" 'b' c-d e... 1 000 no 1a cao");
        assertThat(SmsText.normalize("Apólice", false)).isEqualTo("Apólice");
    }

    @Test
    void gsmTextUpTo160Fits() {
        String text = "a".repeat(160);
        SmsText.Measured measured = SmsText.fit(text, true);
        assertThat(measured.fits()).isTrue();
        assertThat(measured.text()).isEqualTo(text);
        assertThat(measured.limit()).isEqualTo(160);
        assertThat(measured.length()).isEqualTo(160);
    }

    @Test
    void longerTextIsCutTo157PlusEllipsis() {
        SmsText.Measured measured = SmsText.fit("b".repeat(161), true);
        assertThat(measured.fits()).isFalse();
        assertThat(measured.text()).isEqualTo("b".repeat(157) + "...");
        assertThat(measured.text()).hasSize(160);
    }

    @Test
    void extensionCharactersCountTwo() {
        assertThat(SmsText.length("a{b}€")).isEqualTo(8);
        assertThat(SmsText.limit("a{b}€")).isEqualTo(160);
        assertThat(SmsText.measure("[".repeat(80), true).fits()).isTrue();
        assertThat(SmsText.measure("[".repeat(81), true).fits()).isFalse();
    }

    @Test
    void withoutAsciiFoldANonGsmCharacterLimitsTo70() {
        SmsText.Measured measured = SmsText.measure("Confirmação", false);
        assertThat(measured.limit()).isEqualTo(70);
        assertThat(SmsText.measure("Confirmação", true).limit()).isEqualTo(160);
        assertThat(SmsText.fit("ç".repeat(71), false).text()).isEqualTo("ç".repeat(67) + "...");
    }
}
