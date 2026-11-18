package mz.co.moseguros.paymentworker.parser;

import java.util.List;
import java.util.Objects;

/** Um ficheiro lido: cabeçalho, registos de detalhe (por ordem) e rodapé (S5-08 §1). */
public record ParsedFile(FileHeader header, List<ParsedRecord> records, FileTrailer trailer) {

    public ParsedFile {
        Objects.requireNonNull(header, "header");
        Objects.requireNonNull(trailer, "trailer");
        records = records == null ? List.of() : List.copyOf(records);
    }
}
