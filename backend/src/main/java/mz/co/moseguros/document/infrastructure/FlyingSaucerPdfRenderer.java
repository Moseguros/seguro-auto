package mz.co.moseguros.document.infrastructure;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.pdf.BaseFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.util.XRLog;

/**
 * XHTML → PDF com o Flying Saucer sobre o OpenPDF (S4-04 §1). Um {@link ITextRenderer} novo por
 * documento (não é thread-safe), com as fontes de {@code classpath:fonts/} embebidas (Identity-H, para
 * os acentos). Os recursos relativos dos modelos ({@code templates/pdf/logo.png},
 * {@code fonts/Inter-Regular.ttf}) resolvem-se a partir da raiz do classpath.
 *
 * <p>Os registos do Flying Saucer ficam desligados: podiam conter partes do documento. Os erros chegam
 * como excepção a quem chama, que regista só a classe e a mensagem do erro.
 */
@Component
public class FlyingSaucerPdfRenderer {

    private static final Logger log = LoggerFactory.getLogger(FlyingSaucerPdfRenderer.class);

    /** Fontes embebidas em todos os PDF (licença em {@code fonts/OFL.txt}). */
    static final List<String> FONTS = List.of("fonts/Inter-Regular.ttf", "fonts/Inter-SemiBold.ttf");

    private final List<Path> fontFiles;
    private final String baseUrl;

    public FlyingSaucerPdfRenderer() {
        XRLog.setLoggingEnabled(false);
        this.fontFiles = copyFonts();
        URL root = FlyingSaucerPdfRenderer.class.getClassLoader().getResource(FONTS.get(0));
        if (root == null) {
            throw new IllegalStateException("Missing PDF font " + FONTS.get(0));
        }
        String url = root.toExternalForm();
        this.baseUrl = url.substring(0, url.length() - FONTS.get(0).length());
    }

    /**
     * Renderiza o XHTML (bem formado) e devolve os bytes do PDF.
     *
     * @throws RuntimeException se o documento não for válido ou o renderizador falhar
     */
    public byte[] render(String xhtml) {
        ITextRenderer renderer = new ITextRenderer();
        try {
            for (Path font : fontFiles) {
                renderer.getFontResolver().addFont(font.toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load the PDF fonts", e);
        }
        renderer.setDocumentFromString(xhtml, baseUrl);
        renderer.layout();
        ByteArrayOutputStream out = new ByteArrayOutputStream(64 * 1024);
        renderer.createPDF(out);
        renderer.finishPDF();
        return out.toByteArray();
    }

    /**
     * O OpenPDF regista fontes a partir de um caminho no disco: copia-as do classpath (pode ser um
     * JAR) para uma pasta temporária, uma vez por arranque.
     */
    private static List<Path> copyFonts() {
        try {
            Path dir = Files.createTempDirectory("moseguros-pdf-fonts");
            dir.toFile().deleteOnExit();
            List<Path> files = new ArrayList<>();
            for (String resource : FONTS) {
                try (InputStream in = FlyingSaucerPdfRenderer.class.getClassLoader().getResourceAsStream(resource)) {
                    if (in == null) {
                        throw new IllegalStateException("Missing PDF font " + resource);
                    }
                    Path target = dir.resolve(resource.substring(resource.lastIndexOf('/') + 1));
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                    target.toFile().deleteOnExit();
                    files.add(target);
                }
            }
            log.debug("PDF fonts ready: {}", FONTS);
            return List.copyOf(files);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not prepare the PDF fonts", e);
        }
    }
}
