package mz.co.moseguros.paymentworker.parser;

import java.io.InputStream;

/**
 * Leitor de um formato de ficheiro de movimentos (S5-08 §1). Os leitores reais {@code MEPS} e
 * {@code BMEPS} entram no S5-08 e existem em todos os ambientes (sem {@code @Profile}); não há leitores
 * simulados. Uma fonte cujo formato não tenha leitor dá {@code UNSUPPORTED_FORMAT}.
 *
 * <p>O leitor só lê: a validação comum (entidade, rodapé, montantes) é do {@link FileValidator}.
 */
public interface PaymentFileParser {

    /** Valor de {@code settings.fileFormat} ({@code MEPS} ou {@code BMEPS}). */
    String format();

    /**
     * Lê o ficheiro inteiro.
     *
     * @throws PaymentFileParseException códigos {@code PARSE_ERROR}, {@code ENCODING_ERROR},
     *     {@code MISSING_HEADER}, {@code MISSING_TRAILER}, {@code UNSUPPORTED_RECORD}
     */
    ParsedFile parse(InputStream in, ParseContext ctx) throws PaymentFileParseException;
}
