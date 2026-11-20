package mz.co.moseguros.policy.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.jupiter.api.Test;

/** Código QR do certificado (S6-02 §4): lido de volta com o ZXing devolve exactamente o URL. */
class QrCodeRendererTest {

    static final String URL = "https://verificar.moseguros.test/v/Qm9hIHRhcmRlIHNyLg_-Ab";

    final QrCodeRenderer renderer = new QrCodeRenderer();

    @Test
    void thePngDecodesToTheExactUrl() throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(renderer.png(URL)));

        assertThat(image.getWidth()).isEqualTo(QrCodeRenderer.SIZE);
        assertThat(image.getHeight()).isEqualTo(QrCodeRenderer.SIZE);
        Result result = decode(image);
        assertThat(result.getText()).isEqualTo(URL);
        assertThat(result.getResultMetadata().get(ResultMetadataType.ERROR_CORRECTION_LEVEL))
                .isEqualTo(ErrorCorrectionLevel.M.name());
    }

    @Test
    void theDataUriCarriesThePng() throws Exception {
        String uri = renderer.dataUri(URL);

        assertThat(uri).startsWith("data:image/png;base64,");
        byte[] png = Base64.getDecoder().decode(uri.substring("data:image/png;base64,".length()));
        assertThat(decode(ImageIO.read(new ByteArrayInputStream(png))).getText()).isEqualTo(URL);
    }

    /** Lê o QR de uma imagem (também usado no teste do PDF). */
    static Result decode(BufferedImage image) throws NotFoundException, ChecksumException, FormatException {
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        return new QRCodeReader().decode(bitmap, Map.of(DecodeHintType.TRY_HARDER, Boolean.TRUE));
    }

    static Result decode(byte[] png) throws IOException, NotFoundException, ChecksumException, FormatException {
        return decode(ImageIO.read(new ByteArrayInputStream(png)));
    }
}
