package mz.co.moseguros.document.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Nome original limpo (S2-01 §6). */
class FilenameSanitizerTest {

    @Test
    void keepsOnlyTheBaseNameOfAPath() {
        assertThat(FilenameSanitizer.clean("../../etc/passwd.pdf", "application/pdf")).isEqualTo("passwd.pdf");
        assertThat(FilenameSanitizer.clean("C:\\Users\\ana\\Documentos\\livrete.jpg", "image/jpeg"))
                .isEqualTo("livrete.jpg");
        assertThat(FilenameSanitizer.clean("..", "application/pdf")).isEqualTo("ficheiro.pdf");
        assertThat(FilenameSanitizer.clean("pasta/../", "application/pdf")).isEqualTo("ficheiro.pdf");
    }

    @Test
    void replacesControlAndOtherCharacters() {
        assertThat(FilenameSanitizer.clean("li\u0000vre\nte\t1.pdf", "application/pdf")).isEqualTo("li_vre_te_1.pdf");
        assertThat(FilenameSanitizer.clean("a<b>c:d\"e|f?g*.png", "image/png")).isEqualTo("a_b_c_d_e_f_g_.png");
        assertThat(FilenameSanitizer.clean("nome   com    espaços.pdf", "application/pdf"))
                .isEqualTo("nome com espaços.pdf");
    }

    @Test
    void keepsAccentsLettersDigitsAndAllowedPunctuation() {
        assertThat(FilenameSanitizer.clean("Condições Gerais (versão 2)_final-ç.pdf", "application/pdf"))
                .isEqualTo("Condições Gerais (versão 2)_final-ç.pdf");
        // NFD (acento combinado) passa a NFC e fica
        assertThat(FilenameSanitizer.clean("Seguro\u0301.pdf", "application/pdf")).isEqualTo("Seguró.pdf");
    }

    @Test
    void cutsTo200CharactersKeepingTheExtension() {
        String cleaned = FilenameSanitizer.clean("a".repeat(300) + ".pdf", "application/pdf");
        assertThat(cleaned).hasSize(200).endsWith("a.pdf").startsWith("aaaa");
        String accented = FilenameSanitizer.clean("ç".repeat(300) + ".jpg", "image/jpeg");
        assertThat(accented.codePointCount(0, accented.length())).isEqualTo(200);
        assertThat(accented).endsWith("ç.jpg");
    }

    @Test
    void emptyNameBecomesFicheiro() {
        assertThat(FilenameSanitizer.clean(null, "image/png")).isEqualTo("ficheiro.png");
        assertThat(FilenameSanitizer.clean("", "image/png")).isEqualTo("ficheiro.png");
        assertThat(FilenameSanitizer.clean("   ", "image/png")).isEqualTo("ficheiro.png");
        assertThat(FilenameSanitizer.clean(".pdf", "application/pdf")).isEqualTo("ficheiro.pdf");
    }

    @Test
    void extensionIsAlwaysTheOneOfTheRealType() {
        assertThat(FilenameSanitizer.clean("foto.png", "image/jpeg")).isEqualTo("foto.jpg");
        assertThat(FilenameSanitizer.clean("foto.JPEG", "image/jpeg")).isEqualTo("foto.jpg");
        assertThat(FilenameSanitizer.clean("foto", "image/heic")).isEqualTo("foto.heic");
        assertThat(FilenameSanitizer.clean("relatorio.final", "application/pdf")).isEqualTo("relatorio.final.pdf");
        assertThat(FilenameSanitizer.clean("virus.pdf.exe", "application/pdf")).isEqualTo("virus.pdf.pdf");
    }

    @Test
    void unknownContentTypeIsAProgrammingError() {
        assertThatThrownBy(() -> FilenameSanitizer.clean("x.gif", "image/gif"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
