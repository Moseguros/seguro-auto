package mz.co.moseguros.paymentworker.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import mz.co.moseguros.paymentcontracts.FileRejectionReason;
import org.junit.jupiter.api.Test;

/** Validação comum (S5-08 §1): cada regra com um caso válido e um inválido. */
class FileValidatorTest {

    static final Instant PAID = Instant.parse("2026-11-18T08:00:00Z");
    static final ParseContext MEPS = new ParseContext("90002", null, null, ParseContext.MAPUTO, "f.txt");
    static final ParseContext BMEPS = new ParseContext("90090", "800", "02", ParseContext.MAPUTO, "b.txt");

    final FileValidator validator = new FileValidator();

    static ParsedRecord record(int lineNo, long amount, Long fee) {
        return new ParsedRecord(lineNo, "90002", "00548781108", amount, fee, PAID, null, null, "linha");
    }

    static FileHeader header(String entity) {
        return new FileHeader(entity, "201211230", "201211220", null, null, null, null, "cabecalho");
    }

    static FileHeader bmepsHeader(String company, String profile) {
        return new FileHeader("90090", company + "-" + profile + "-00001", null, company, profile, 1, null, "c");
    }

    static ParsedFile file(FileHeader header, List<ParsedRecord> records, int count, long total, Long fees) {
        return new ParsedFile(header, records, new FileTrailer(count, total, fees));
    }

    static Optional<FileRejectionReason> reason(Optional<FileValidator.Failure> failure) {
        return failure.map(FileValidator.Failure::reason);
    }

    @Test
    void entityOfTheHeaderMustBeTheSourceEntity() {
        List<ParsedRecord> records = List.of(record(2, 300_000, null));
        assertThat(validator.validate(file(header("90002"), records, 1, 300_000, null), MEPS, "MEPS")).isEmpty();
        assertThat(reason(validator.validate(file(header("90003"), records, 1, 300_000, null), MEPS, "MEPS")))
                .contains(FileRejectionReason.ENTITY_MISMATCH);
    }

    @Test
    void bmepsCompanyAndProfileMustBeTheSourceOnes() {
        List<ParsedRecord> records = List.of(record(2, 100, null));
        assertThat(validator.validate(file(bmepsHeader("800", "02"), records, 1, 100, null), BMEPS, "BMEPS"))
                .isEmpty();
        assertThat(reason(validator.validate(file(bmepsHeader("801", "02"), records, 1, 100, null), BMEPS, "BMEPS")))
                .contains(FileRejectionReason.ENTITY_MISMATCH);
        assertThat(reason(validator.validate(file(bmepsHeader("800", "03"), records, 1, 100, null), BMEPS, "BMEPS")))
                .contains(FileRejectionReason.ENTITY_MISMATCH);
    }

    @Test
    void trailerCountMustMatchTheDetailRecords() {
        List<ParsedRecord> records = List.of(record(2, 100, null), record(3, 200, null));
        assertThat(validator.validate(file(header("90002"), records, 2, 300, null), MEPS, "MEPS")).isEmpty();
        assertThat(reason(validator.validate(file(header("90002"), records, 3, 300, null), MEPS, "MEPS")))
                .contains(FileRejectionReason.TRAILER_COUNT_MISMATCH);
    }

    @Test
    void trailerTotalMustMatchTheSumOfTheDetailRecords() {
        List<ParsedRecord> records = List.of(record(2, 100, null), record(3, 200, null));
        assertThat(validator.validate(file(header("90002"), records, 2, 300, null), MEPS, "MEPS")).isEmpty();
        Optional<FileValidator.Failure> failure =
                validator.validate(file(header("90002"), records, 2, 301, null), MEPS, "MEPS");
        assertThat(reason(failure)).contains(FileRejectionReason.TRAILER_TOTAL_MISMATCH);
        assertThat(failure.get().detail()).doesNotContain("00548781108");
    }

    @Test
    void differentFeesTotalOnlyWarns() {
        List<ParsedRecord> records = List.of(record(2, 100, 10L), record(3, 200, 10L));
        assertThat(validator.validate(file(header("90002"), records, 2, 300, 20L), MEPS, "MEPS")).isEmpty();
        assertThat(validator.validate(file(header("90002"), records, 2, 300, 99L), MEPS, "MEPS")).isEmpty();
    }

    @Test
    void amountsMustBeGreaterThanZero() {
        assertThat(validator.validate(file(header("90002"), List.of(record(2, 1, null)), 1, 1, null), MEPS, "MEPS"))
                .isEmpty();
        Optional<FileValidator.Failure> failure =
                validator.validate(file(header("90002"), List.of(record(2, 0, null)), 1, 0, null), MEPS, "MEPS");
        assertThat(reason(failure)).contains(FileRejectionReason.INVALID_AMOUNT);
        assertThat(failure.get().lineNo()).isEqualTo(2);
    }

    @Test
    void lineNumbersMustNotRepeat() {
        assertThat(validator.validate(file(header("90002"), List.of(record(2, 1, null), record(3, 1, null)), 2, 2,
                null), MEPS, "MEPS")).isEmpty();
        Optional<FileValidator.Failure> failure = validator.validate(file(header("90002"),
                List.of(record(2, 1, null), record(2, 1, null)), 2, 2, null), MEPS, "MEPS");
        assertThat(reason(failure)).contains(FileRejectionReason.PARSE_ERROR);
    }

    @Test
    void emptyFileWithZeroTrailerIsValid() {
        assertThat(validator.validate(file(header("90002"), List.of(), 0, 0, 0L), MEPS, "MEPS")).isEmpty();
    }
}
