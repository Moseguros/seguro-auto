package mz.co.moseguros.paymentworker.parser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import mz.co.moseguros.paymentcontracts.FileRejectionReason;
import mz.co.moseguros.paymentcontracts.FileSourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validação comum a todos os formatos, depois do {@code parse} (S5-08 §1). Pela ordem:
 *
 * <ol>
 *   <li>BMEPS: ID da empresa e do perfil do cabeçalho iguais aos da fonte; outros formatos: entidade do
 *       cabeçalho igual à da fonte → senão {@code ENTITY_MISMATCH};</li>
 *   <li>montantes maiores que 0 → senão {@code INVALID_AMOUNT};</li>
 *   <li>{@code lineNo} sem repetições → senão {@code PARSE_ERROR};</li>
 *   <li>número de registos e total do rodapé iguais aos dos detalhes → senão
 *       {@code TRAILER_COUNT_MISMATCH} / {@code TRAILER_TOTAL_MISMATCH};</li>
 *   <li>total das tarifas/comissões do rodapé diferente da soma → <b>não rejeita</b>: {@code WARN}
 *       {@code alert=PAYMENT_FILE_FEES_MISMATCH} (as tarifas não servem para confirmar pagamentos).</li>
 * </ol>
 */
@Component
public class FileValidator {

    private static final Logger log = LoggerFactory.getLogger(FileValidator.class);

    /** Um ficheiro rejeitado: motivo, linha (ou {@code null}) e explicação sem conteúdo do ficheiro. */
    public record Failure(FileRejectionReason reason, Integer lineNo, String detail) {
    }

    /** Vazio = válido. */
    public Optional<Failure> validate(ParsedFile file, ParseContext ctx, String format) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(ctx, "ctx");
        FileHeader header = file.header();
        if (FileSourceDescriptor.FORMAT_BMEPS.equals(format)) {
            if (!Objects.equals(header.bmepsCompanyId(), ctx.bmepsCompanyId())
                    || !Objects.equals(header.bmepsProfileId(), ctx.bmepsProfileId())) {
                return Optional.of(new Failure(FileRejectionReason.ENTITY_MISMATCH, 1,
                        "BMEPS company/profile of the header differ from the source"));
            }
        } else if (!Objects.equals(header.entity(), ctx.sourceEntity())) {
            return Optional.of(new Failure(FileRejectionReason.ENTITY_MISMATCH, 1,
                    "Entity of the header differs from the source"));
        }
        long total = 0;
        long fees = 0;
        Set<Integer> lines = new HashSet<>();
        for (ParsedRecord r : file.records()) {
            if (r.amountCentavos() <= 0) {
                return Optional.of(new Failure(FileRejectionReason.INVALID_AMOUNT, r.lineNo(),
                        "Amount must be greater than zero"));
            }
            if (!lines.add(r.lineNo())) {
                return Optional.of(new Failure(FileRejectionReason.PARSE_ERROR, r.lineNo(), "Repeated line number"));
            }
            total = Math.addExact(total, r.amountCentavos());
            fees = Math.addExact(fees, r.feeCentavos() == null ? 0 : r.feeCentavos());
        }
        FileTrailer trailer = file.trailer();
        if (trailer.recordCount() != file.records().size()) {
            return Optional.of(new Failure(FileRejectionReason.TRAILER_COUNT_MISMATCH, null,
                    "Trailer record count " + trailer.recordCount() + " differs from " + file.records().size()
                            + " detail records"));
        }
        if (trailer.totalAmountCentavos() != total) {
            return Optional.of(new Failure(FileRejectionReason.TRAILER_TOTAL_MISMATCH, null,
                    "Trailer total differs from the sum of the detail records"));
        }
        if (trailer.totalFeesCentavos() != null && trailer.totalFeesCentavos() != fees) {
            log.warn("alert=PAYMENT_FILE_FEES_MISMATCH file={} trailerFees={} sumOfFees={}", ctx.fileName(),
                    trailer.totalFeesCentavos(), fees);
        }
        return Optional.empty();
    }
}
