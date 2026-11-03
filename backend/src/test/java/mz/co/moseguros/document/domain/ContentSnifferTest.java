package mz.co.moseguros.document.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import mz.co.moseguros.document.TestFiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tipo real pelo conteúdo (S2-01 §6), com ficheiros reais em {@code src/test/resources/files/}.
 */
class ContentSnifferTest {

    @ParameterizedTest
    @CsvSource({
        "sample.jpg, JPEG",
        "sample.png, PNG",
        "sample.heic, HEIC",
        "sample.pdf, PDF"
    })
    void recognisesTheFourAllowedTypes(String file, AllowedContentType expected) throws IOException {
        assertThat(ContentSniffer.sniff(resource(file))).contains(expected);
    }

    @Test
    void gifIsUnknown() throws IOException {
        assertThat(ContentSniffer.sniff(resource("sample.gif"))).isEmpty();
    }

    @Test
    void textWithPdfExtensionIsUnknown() throws IOException {
        assertThat(ContentSniffer.sniff(resource("not-a-pdf.pdf"))).isEmpty();
    }

    @Test
    void windowsExecutableIsUnknown() {
        // cabeçalho MZ + PE de um .exe
        byte[] exe = new byte[128];
        exe[0] = 'M';
        exe[1] = 'Z';
        exe[0x3C] = 0x40;
        exe[0x40] = 'P';
        exe[0x41] = 'E';
        assertThat(ContentSniffer.sniff(exe)).isEmpty();
    }

    @Test
    void otherIsoMediaBrandsAreUnknown() {
        // MP4 (ftyp isom): mesma estrutura do HEIC, outra marca
        byte[] mp4 = "\0\0\0\u0018ftypisom\0\0\0\0isomiso2".getBytes(StandardCharsets.ISO_8859_1);
        assertThat(ContentSniffer.sniff(mp4)).isEmpty();
    }

    @Test
    void heifBrandsAreRecognised() {
        for (String brand : new String[] {"heic", "heix", "hevc", "hevx", "mif1", "msf1"}) {
            byte[] content = ("\0\0\0\u0018ftyp" + brand + "\0\0\0\0").getBytes(StandardCharsets.ISO_8859_1);
            assertThat(ContentSniffer.sniff(content)).as(brand).contains(AllowedContentType.HEIC);
        }
    }

    @Test
    void emptyAndShortContentIsUnknown() {
        assertThat(ContentSniffer.sniff(new byte[0])).isEmpty();
        assertThat(ContentSniffer.sniff(new byte[] {(byte) 0xFF, (byte) 0xD8})).isEmpty();
        assertThat(ContentSniffer.sniff("%PDF".getBytes(StandardCharsets.US_ASCII))).isEmpty();
        assertThat(ContentSniffer.sniff(null)).isEmpty();
    }

    private static byte[] resource(String name) throws IOException {
        return TestFiles.bytes(name);
    }
}
