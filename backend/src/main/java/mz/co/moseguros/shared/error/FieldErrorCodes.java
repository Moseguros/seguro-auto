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

    // Coberturas e publicação de produtos (S2-04)
    /** Valor repetido numa lista (ex.: o mesmo código de cobertura duas vezes). Também usado no S2-03. */
    public static final String DUPLICATED = "DUPLICATED";
    /** Uma cobertura que se exige a si própria em {@code requiresCodes}. */
    public static final String INVALID_REQUIREMENT = "INVALID_REQUIREMENT";
    /** {@code capitalOptions} sem 2 a 10 valores positivos em ordem estritamente crescente. */
    public static final String CAPITAL_OPTIONS_INVALID = "CAPITAL_OPTIONS_INVALID";
    /** Código de país fora de {@code ^[A-Z]{2}$}. */
    public static final String INVALID_COUNTRY_CODE = "INVALID_COUNTRY_CODE";
    /** Âmbito territorial sem {@code MZ}. */
    public static final String MZ_SCOPE_REQUIRED = "MZ_SCOPE_REQUIRED";
    /** Tipo de capital não permitido para a cobertura. */
    public static final String INVALID_CAPITAL_TYPE = "INVALID_CAPITAL_TYPE";
    public static final String MTPL_REQUIRED = "MTPL_REQUIRED";
    public static final String MTPL_MUST_BE_MANDATORY = "MTPL_MUST_BE_MANDATORY";
    /** Capital da RC obrigatória abaixo de {@code MTPL_MIN_CAPITAL}. */
    public static final String MTPL_CAPITAL_BELOW_MINIMUM = "MTPL_CAPITAL_BELOW_MINIMUM";
    public static final String BORDER_ONLY_MTPL = "BORDER_ONLY_MTPL";
    public static final String OD_NOT_ALLOWED_FOR_TYPE = "OD_NOT_ALLOWED_FOR_TYPE";
    public static final String OD_REQUIRED_FOR_COMPREHENSIVE = "OD_REQUIRED_FOR_COMPREHENSIVE";
    public static final String OD_CAPITAL_MUST_BE_VEHICLE_VALUE = "OD_CAPITAL_MUST_BE_VEHICLE_VALUE";
    public static final String OD_THEFT_REQUIRES_OD_COVERAGES = "OD_THEFT_REQUIRES_OD_COVERAGES";
    /** Um código de {@code requiresCodes} que não existe na versão. */
    public static final String REQUIRED_COVERAGE_MISSING = "REQUIRED_COVERAGE_MISSING";
    /** Capital (ou uma opção) da RC facultativa não superior a {@code MTPL_MIN_CAPITAL}. */
    public static final String VTPL_CAPITAL_TOO_LOW = "VTPL_CAPITAL_TOO_LOW";
    public static final String CROSS_BORDER_SCOPE_REQUIRED = "CROSS_BORDER_SCOPE_REQUIRED";
    /** Falta um documento legal obrigatório em português. */
    public static final String DOCUMENT_REQUIRED = "DOCUMENT_REQUIRED";
    // Veículos e condutores do cliente (S2-03)
    /** N.º de chassis fora de {@code ^[A-Z0-9]{5,30}$} (depois de normalizado). */
    public static final String INVALID_CHASSIS = "INVALID_CHASSIS";
    /** Ano antes de 1900 ou depois do próximo ano (em {@code Africa/Maputo}). */
    public static final String INVALID_YEAR = "INVALID_YEAR";
    /** N.º da carta fora de {@code ^[A-Z0-9/-]{5,20}$} (depois de normalizado). */
    public static final String INVALID_LICENCE_NUMBER = "INVALID_LICENCE_NUMBER";
    /** Categoria de carta inexistente ou inactiva em {@code LICENCE_CATEGORY}. */
    public static final String LICENCE_CATEGORY_UNKNOWN = "LICENCE_CATEGORY_UNKNOWN";
    /** Data de emissão da carta antes do nascimento ou depois de hoje. */
    public static final String LICENCE_ISSUE_DATE_INVALID = "LICENCE_ISSUE_DATE_INVALID";
    /** Lista de condutores de um veículo sem nenhum habitual. */
    public static final String HABITUAL_DRIVER_REQUIRED = "HABITUAL_DRIVER_REQUIRED";
    /** Lista de condutores de um veículo com mais de um habitual. */
    public static final String MULTIPLE_HABITUAL_DRIVERS = "MULTIPLE_HABITUAL_DRIVERS";

    // Tarifa interna e encargos da seguradora (S3-01); field = ponteiro JSON dentro de rating/charges
    /** O documento não cumpre o JSON Schema ({@code rating-v1.json}, {@code charges-v1.json}). */
    public static final String SCHEMA_VIOLATION = "SCHEMA_VIOLATION";
    /** Uma cobertura da versão sem entrada em {@code coverages} da tarifa. */
    public static final String COVERAGE_TARIFF_MISSING = "COVERAGE_TARIFF_MISSING";
    /** Entrada da tarifa para uma cobertura que a versão não tem. */
    public static final String COVERAGE_NOT_IN_PRODUCT = "COVERAGE_NOT_IN_PRODUCT";
    /** Duas entradas da tarifa para a mesma cobertura. */
    public static final String DUPLICATE_COVERAGE = "DUPLICATE_COVERAGE";
    /** Tipo da base que não serve para o tipo de capital da cobertura. */
    public static final String BASE_TYPE_NOT_ALLOWED = "BASE_TYPE_NOT_ALLOWED";
    /** Taxa, factor ou percentagem fora dos limites permitidos. */
    public static final String VALUE_OUT_OF_RANGE = "VALUE_OUT_OF_RANGE";
    /** Intervalo com {@code gte} maior ou igual a {@code lt}. */
    public static final String INVALID_RANGE = "INVALID_RANGE";
    /** Código de factor repetido na mesma cobertura. */
    public static final String DUPLICATE_FACTOR = "DUPLICATE_FACTOR";
    /** Código de factor reservado ao sistema ({@code BONUS_MALUS}, S7). */
    public static final String RESERVED_FACTOR_CODE = "RESERVED_FACTOR_CODE";
    /** Duração ({@code days}) repetida em {@code durationFactors}. */
    public static final String DUPLICATE_DURATION = "DUPLICATE_DURATION";
    /** Factor de 365 dias diferente de 1. */
    public static final String DURATION_365_NOT_ONE = "DURATION_365_NOT_ONE";
    /** Produto {@code TEMPORARY} sem {@code durationFactors}. */
    public static final String DURATION_FACTORS_REQUIRED = "DURATION_FACTORS_REQUIRED";
    /** Duração diferente de 365 dias num produto que não é {@code TEMPORARY}. */
    public static final String DURATION_NOT_ALLOWED = "DURATION_NOT_ALLOWED";
    /** Código de encargo repetido. */
    public static final String DUPLICATE_CHARGE = "DUPLICATE_CHARGE";

    // Cotações e ligação à tarifação externa (S3-02)
    /** Data de início antes de hoje ou depois do limite ({@code max-start-days-ahead}). */
    public static final String START_DATE_OUT_OF_RANGE = "START_DATE_OUT_OF_RANGE";
    /** Cobertura que não existe no tipo de seguro pedido (ex.: danos próprios fora de {@code COMPREHENSIVE}). */
    public static final String COVERAGE_NOT_ALLOWED_FOR_TYPE = "COVERAGE_NOT_ALLOWED_FOR_TYPE";
    /** Pedido {@code COMPREHENSIVE} sem nenhuma cobertura de danos próprios. */
    public static final String OWN_DAMAGE_REQUIRED = "OWN_DAMAGE_REQUIRED";
    /** Cobertura pedida sem as coberturas que ela exige ({@code OD_THEFT}). */
    public static final String COVERAGE_REQUIRES = "COVERAGE_REQUIRES";
    /** Endereço que não é uma URL absoluta {@code https://} (ou {@code http://} onde é permitido). */
    public static final String INVALID_URL = "INVALID_URL";
    /** Referência de segredo fora do formato {@code config:<chave>}. */
    public static final String INVALID_SECRET_REF = "INVALID_SECRET_REF";

    // KYC do cliente (S4-01)
    /** Seguradora inexistente, que não é seguradora ou que não está activa. */
    public static final String INVALID_INSURER = "INVALID_INSURER";

    // Propostas: declarações e adequação (S4-02)
    /** Falta resposta a alguma das perguntas fixas. */
    public static final String ANSWERS_INCOMPLETE = "ANSWERS_INCOMPLETE";
    /** A mesma pergunta respondida mais de uma vez. */
    public static final String ANSWER_DUPLICATED = "ANSWER_DUPLICATED";
    /** Código de pergunta desconhecido. */
    public static final String UNKNOWN_QUESTION = "UNKNOWN_QUESTION";
    /** Resposta fora das permitidas para o tipo da pergunta. */
    public static final String INVALID_ANSWER = "INVALID_ANSWER";
    /** Confirmação obrigatória ({@code true}). */
    public static final String MUST_BE_TRUE = "MUST_BE_TRUE";

    // Vistoria (S4-03)
    /** Ângulo que não é um {@code PhotoSlot}. */
    public static final String INVALID_SLOT = "INVALID_SLOT";
    /** Já há {@code max-extra-photos} fotografias extra activas. */
    public static final String EXTRA_PHOTOS_LIMIT = "EXTRA_PHOTOS_LIMIT";
    /** {@code takenAt} anterior a agora − {@code max-photo-age-days}. */
    public static final String PHOTO_TOO_OLD = "PHOTO_TOO_OLD";
    /** {@code takenAt} posterior a agora + 5 minutos. */
    public static final String PHOTO_IN_FUTURE = "PHOTO_IN_FUTURE";
    /** Só uma das coordenadas, ou fora dos limites. */
    public static final String INVALID_COORDINATES = "INVALID_COORDINATES";

    // Regras de aceitação da versão do produto (S4-05)
    /** As regras de aceitação não cumprem o {@code underwriting-v1.json} (um erro por violação). */
    public static final String INVALID_RULES = "INVALID_RULES";

    // Formas de pagamento por seguradora (S5-01); field = settings.<caminho>, secrets.<campo> ou o campo do pedido
    /** Valor que não cumpre o formato do campo ({@code pattern}, {@code enum}, tipo errado). */
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    /** Pastas do SFTP repetidas (entrada, processados e erros têm de ser diferentes). */
    public static final String DIRECTORY_CONFLICT = "DIRECTORY_CONFLICT";
    /** Chave pública que não é uma chave RSA em Base64 DER ou PEM. */
    public static final String INVALID_PUBLIC_KEY = "INVALID_PUBLIC_KEY";
    /** Valor mínimo maior que o máximo. */
    public static final String MIN_GREATER_THAN_MAX = "MIN_GREATER_THAN_MAX";
    /** Entidade de referências já usada por outra seguradora. */
    public static final String ENTITY_IN_USE = "ENTITY_IN_USE";

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
