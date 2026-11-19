package mz.co.moseguros.paymentworker.parser;

import java.time.LocalDate;

/**
 * Cabeçalho de um ficheiro de movimentos (S5-08 §1).
 *
 * @param entity               MEPS: A033; BMEPS: a entidade da fonte ({@code ctx.sourceEntity})
 * @param formatFileId         identificador do ficheiro no formato: MEPS A005; BMEPS
 *                             {@code <empresa>-<perfil>-<ID do ficheiro>}
 * @param previousFormatFileId MEPS A007 ({@code null} se só zeros); BMEPS {@code null}
 * @param bmepsCompanyId       só BMEPS
 * @param bmepsProfileId       só BMEPS
 * @param bmepsFileSequence    só BMEPS: o ID do ficheiro (1, 2, …)
 * @param fileDate             data do ficheiro
 * @param rawHeader            a linha do cabeçalho sem o terminador
 */
public record FileHeader(String entity, String formatFileId, String previousFormatFileId, String bmepsCompanyId,
                         String bmepsProfileId, Integer bmepsFileSequence, LocalDate fileDate, String rawHeader) {
}
