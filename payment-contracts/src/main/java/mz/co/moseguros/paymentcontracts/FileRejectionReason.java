package mz.co.moseguros.paymentcontracts;

/** Motivo da rejeição de um ficheiro inteiro ({@link PaymentFileRejected}, S5-08, S5-09). */
public enum FileRejectionReason {
    /** Entidade do MEPS (A033), ou empresa/perfil do BMEPS, diferentes dos da fonte. */
    ENTITY_MISMATCH,
    /** Número de registos do rodapé diferente do número de detalhes lidos. */
    TRAILER_COUNT_MISMATCH,
    /** Total do rodapé diferente da soma dos detalhes. */
    TRAILER_TOTAL_MISMATCH,
    /** Montante não numérico, nulo ou fora do intervalo. */
    INVALID_AMOUNT,
    /** Linha com comprimento ou campos inválidos. */
    PARSE_ERROR,
    /** Bytes que não se lêem na codificação do formato. */
    ENCODING_ERROR,
    /** Sem registo de cabeçalho. */
    MISSING_HEADER,
    /** Sem registo de rodapé. */
    MISSING_TRAILER,
    /** Tipo de registo desconhecido. */
    UNSUPPORTED_RECORD,
    /** Ficheiro acima do tamanho máximo aceite. */
    FILE_TOO_LARGE,
    /** O mesmo identificador do ficheiro no formato já foi processado com outro conteúdo (S5-09). */
    DUPLICATE_FILE_ID
}
