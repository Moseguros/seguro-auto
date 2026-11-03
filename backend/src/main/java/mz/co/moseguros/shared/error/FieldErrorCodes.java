package mz.co.moseguros.shared.error;

import java.util.List;
import java.util.TreeSet;

/**
 * Lista oficial dos códigos de {@code errors[].code} (erros por campo de um
 * {@code VALIDATION_ERROR}). É exportada para {@code api/error-codes.json}
 * ({@code ErrorCodesExportTest}); a web e a app têm de ter a tradução {@code validation.<CODE>}
 * de cada código, em pt e en (AGENTS.md §4).
 *
 * <p>Inclui os códigos dos value objects (lidos de um pedido são erros de campo) e os das
 * restrições de Bean Validation usadas nos DTOs ({@code @NotBlank} → {@code NOT_BLANK}, ver
 * {@code GlobalExceptionHandler.codeOf}).
 */
public final class FieldErrorCodes {

    // Value objects partilhados
    public static final String INVALID_CURRENCY = ErrorCodes.INVALID_CURRENCY;
    public static final String INVALID_MSISDN = ErrorCodes.INVALID_MSISDN;
    public static final String INVALID_NUIT = ErrorCodes.INVALID_NUIT;
    public static final String INVALID_PLATE = ErrorCodes.INVALID_PLATE;
    public static final String INVALID_EMAIL = ErrorCodes.INVALID_EMAIL;
    public static final String INVALID_PHONE = ErrorCodes.INVALID_PHONE;

    // Definições de OTP (S1-03)
    public static final String CHANNELS_EMPTY = "CHANNELS_EMPTY";
    public static final String CHANNELS_DUPLICATED = "CHANNELS_DUPLICATED";
    public static final String DEFAULT_CHANNEL_NOT_ENABLED = "DEFAULT_CHANNEL_NOT_ENABLED";
    public static final String FALLBACK_ORDER_MISMATCH = "FALLBACK_ORDER_MISMATCH";
    public static final String OUT_OF_RANGE = "OUT_OF_RANGE";

    // Organizações e convites (S1-04)
    /** Campo que não pode ser enviado neste caso (ex.: {@code insurerProfile} fora de uma seguradora). */
    public static final String NOT_ALLOWED = "NOT_ALLOWED";
    /** Campo obrigatório neste caso (ex.: {@code insurerProfile} numa seguradora). */
    public static final String REQUIRED = "REQUIRED";
    /** Ordenação pedida não suportada pelo recurso. */
    public static final String INVALID_SORT = "INVALID_SORT";

    // Política de palavras-passe (S1-04): errors[] de PASSWORD_TOO_WEAK
    public static final String TOO_SHORT = "TOO_SHORT";
    public static final String TOO_LONG = "TOO_LONG";
    public static final String CONTAINS_EMAIL = "CONTAINS_EMAIL";
    public static final String TOO_COMMON = "TOO_COMMON";

    // Perfil do cliente (S1-06)
    /** Nome completo com caracteres não permitidos, fora de 2–120 ou com menos de duas palavras. */
    public static final String INVALID_NAME = "INVALID_NAME";
    /** Menos de 18 anos à data de hoje (em {@code Africa/Maputo}). */
    public static final String UNDERAGE = "UNDERAGE";
    /** Data impossível para o campo (ex.: nascimento há mais de 120 anos). */
    public static final String INVALID_DATE = "INVALID_DATE";
    /** Número de documento fora de {@code ^[A-Z0-9]{5,20}$}. */
    public static final String INVALID_DOCUMENT_NUMBER = "INVALID_DOCUMENT_NUMBER";

    // Tabelas de referência, parâmetros e formatos de matrícula (S2-02)
    /** Código fora do formato permitido (maiúsculas, algarismos e {@code _}). */
    public static final String INVALID_CODE = "INVALID_CODE";
    /** A marca indicada em {@code parentCode} não existe. */
    public static final String PARENT_NOT_FOUND = "PARENT_NOT_FOUND";
    /** Valor de um parâmetro da plataforma inválido para o seu tipo. */
    public static final String INVALID_VALUE = "INVALID_VALUE";
    /** Expressão regular que não compila. */
    public static final String INVALID_PATTERN = "INVALID_PATTERN";
    /** Expressão regular que não começa por {@code ^} ou não acaba em {@code $}. */
    public static final String PATTERN_NOT_ANCHORED = "PATTERN_NOT_ANCHORED";
    /** Expressão regular sem nenhum grupo nomeado. */
    public static final String PATTERN_WITHOUT_GROUPS = "PATTERN_WITHOUT_GROUPS";
    /** Modelo de apresentação com um grupo que não existe na expressão, ou com caracteres não permitidos. */
    public static final String TEMPLATE_GROUP_UNKNOWN = "TEMPLATE_GROUP_UNKNOWN";
    /** O exemplo não corresponde ao formato, ou formatado dá outro valor. */
    public static final String EXAMPLE_DOES_NOT_MATCH = "EXAMPLE_DOES_NOT_MATCH";

    // Bean Validation
    /** {@code @NotBlank}: campo obrigatório vazio. */
    public static final String NOT_BLANK = "NOT_BLANK";
    /** {@code @NotNull}: campo obrigatório em falta. */
    public static final String NOT_NULL = "NOT_NULL";
    /** {@code @Size}: comprimento ou número de elementos fora dos limites. */
    public static final String SIZE = "SIZE";
    /** {@code @Pattern}: formato inválido. */
    public static final String PATTERN = "PATTERN";
    /** Parâmetro do pedido (caminho ou query) com um valor que não é do tipo esperado. */
    public static final String TYPE_MISMATCH = "TYPE_MISMATCH";

    private FieldErrorCodes() {
    }

    /** Todos os códigos, por ordem alfabética. */
    public static List<String> all() {
        return List.copyOf(new TreeSet<>(ErrorCodes.constants(FieldErrorCodes.class)));
    }
}
