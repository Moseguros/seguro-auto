package mz.co.moseguros.paymentcontracts;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Objects;
import java.util.UUID;

/**
 * Identificadores das mensagens ({@code messageId}, também a propriedade AMQP {@code
 * message-id}). O backend deduplica por eles (S5-10). Os determinísticos são o SHA-256, em
 * hexadecimal minúsculo, do texto UTF-8 indicado em cada método.
 */
public final class MessageIds {

    private MessageIds() {}

    /**
     * {@code sha256(sourceId + "|" + fileSha256 + "|" + lineNo)}: um registo de um ficheiro.
     *
     * <p>Não usa o identificador do banco nem os dados do pagamento: nos exemplos oficiais os
     * identificadores de transacção vêm a zeros e o MEPS só tem a hora ao minuto, por isso dois
     * pagamentos iguais da mesma referência no mesmo minuto teriam o mesmo identificador e um deles
     * perder-se-ia. Um ficheiro nunca é processado duas vezes (S5-09); se a mesma transacção
     * aparecer noutro ficheiro, o backend trata-a como um segundo pagamento (S5-10).
     */
    public static String referencePaymentReceived(UUID sourceId, String fileSha256, int lineNo) {
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(fileSha256, "fileSha256");
        return sha256Hex(sourceId + "|" + fileSha256 + "|" + lineNo);
    }

    /** {@code sha256("processed|" + sourceId + "|" + sha256)}. */
    public static String paymentFileProcessed(UUID sourceId, String fileSha256) {
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(fileSha256, "fileSha256");
        return sha256Hex("processed|" + sourceId + "|" + fileSha256);
    }

    /**
     * {@code sha256("rejected|" + sourceId + "|" + sha256 + "|" + rejectedAt)}, com {@code
     * rejectedAt} em ISO 8601 ({@link DateTimeFormatter#ISO_INSTANT}, o mesmo texto que vai no
     * JSON).
     */
    public static String paymentFileRejected(UUID sourceId, String fileSha256, Instant rejectedAt) {
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(fileSha256, "fileSha256");
        Objects.requireNonNull(rejectedAt, "rejectedAt");
        return sha256Hex("rejected|" + sourceId + "|" + fileSha256 + "|"
                + DateTimeFormatter.ISO_INSTANT.format(rejectedAt));
    }

    /** UUID aleatório em texto: cada leitura de uma fonte é um acontecimento novo. */
    public static String paymentSourcePolled() {
        return UUID.randomUUID().toString();
    }

    /** SHA-256 de {@code text} em UTF-8, em hexadecimal minúsculo. */
    public static String sha256Hex(String text) {
        return sha256Hex(text.getBytes(StandardCharsets.UTF_8));
    }

    /** SHA-256 de {@code bytes} em hexadecimal minúsculo (ex.: o conteúdo de um ficheiro). */
    public static String sha256Hex(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
