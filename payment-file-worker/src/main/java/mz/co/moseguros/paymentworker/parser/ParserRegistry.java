package mz.co.moseguros.paymentworker.parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * Leitores por formato (S5-08 §1): todos os beans {@link PaymentFileParser}. Dois leitores com o mesmo
 * formato impedem o arranque.
 */
@Component
public class ParserRegistry {

    private final Map<String, PaymentFileParser> parsers = new LinkedHashMap<>();

    public ParserRegistry(List<PaymentFileParser> parsers) {
        for (PaymentFileParser parser : parsers) {
            PaymentFileParser previous = this.parsers.putIfAbsent(parser.format(), parser);
            if (previous != null) {
                throw new IllegalStateException("Two parsers for the file format " + parser.format());
            }
        }
    }

    /** Leitor do formato, ou vazio ({@code UNSUPPORTED_FORMAT}). */
    public Optional<PaymentFileParser> find(String format) {
        return format == null ? Optional.empty() : Optional.ofNullable(parsers.get(format));
    }

    /** Formatos com leitor. */
    public List<String> formats() {
        return List.copyOf(parsers.keySet());
    }
}
