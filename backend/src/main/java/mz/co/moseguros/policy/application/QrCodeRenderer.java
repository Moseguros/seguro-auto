package mz.co.moseguros.policy.application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

/**
 * Código QR do certificado (S6-02 §4): ZXing {@link QRCodeWriter}, correcção de erros {@code M},
 * {@value #SIZE} × {@value #SIZE} px, margem de {@value #MARGIN} módulos, PNG. O conteúdo é o URL de
 * verificação ({@code {public-base-url}/v/{código}}), que nunca vai para logs.
 */
@Component
public class QrCodeRenderer {

    static final int SIZE = 512;
    static final int MARGIN = 2;
    static final String DATA_URI_PREFIX = "data:image/png;base64,";

    private static final Map<EncodeHintType, Object> HINTS = Map.of(
            EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN, MARGIN,
            EncodeHintType.CHARACTER_SET, "UTF-8");

    /** PNG do código QR com o {@code content}. */
    public byte[] png(String content) {
        Objects.requireNonNull(content, "content");
        try {
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE, HINTS);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4 * 1024);
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (WriterException e) {
            // Sem o conteúdo na mensagem: tem o código de verificação.
            throw new IllegalStateException("Could not encode the QR code", e);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write the QR code image", e);
        }
    }

    /** {@code data:image/png;base64,…} para o {@code <img>} do modelo do certificado. */
    public String dataUri(String content) {
        return DATA_URI_PREFIX + Base64.getEncoder().encodeToString(png(content));
    }
}
