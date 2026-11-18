package mz.co.moseguros.shared.error;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Lista oficial dos códigos de erro de topo (o {@code code} do {@code ProblemDetail}), cada um
 * com o estado HTTP com que é devolvido. É exportada para {@code api/error-codes.json}
 * ({@code ErrorCodesExportTest}); a web e a app têm de ter a tradução {@code errors.<CODE>} de
 * cada código, em pt e en (AGENTS.md §4).
 *
 * <p>Quem acrescenta uma constante acrescenta o estado em {@link #STATUS}. Os códigos de
 * {@code errors[].code} estão em {@link FieldErrorCodes}.
 */
public final class ErrorCodes {

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    // Value objects partilhados (400). Ao ler um pedido aparecem em errors[].code.
    public static final String INVALID_CURRENCY = "INVALID_CURRENCY";
    public static final String INVALID_MSISDN = "INVALID_MSISDN";
    public static final String INVALID_NUIT = "INVALID_NUIT";
    public static final String INVALID_PLATE = "INVALID_PLATE";
    public static final String INVALID_EMAIL = "INVALID_EMAIL";
    public static final String INVALID_PHONE = "INVALID_PHONE";

    // Infra-estrutura (500): erros de programação ou de configuração, nunca do utilizador.
    /** Operação que precisa de uma seguradora no {@code TenantContext} sem a ter. */
    public static final String TENANT_REQUIRED = "TENANT_REQUIRED";
    /** Valor cifrado alterado, chave errada ou versão de chave desconhecida. */
    public static final String DECRYPTION_FAILED = "DECRYPTION_FAILED";

    // Regras de domínio partilhadas
    /** Transição de estado não permitida a partir do estado actual (409). */
    public static final String INVALID_STATE_TRANSITION = "INVALID_STATE_TRANSITION";

    // Organizações (400)
    public static final String LICENSE_NUMBER_REQUIRED = "LICENSE_NUMBER_REQUIRED";

    // Sessões (401)
    /** Refresh token desconhecido, expirado, revogado, ou conta/organização que já não o permite. */
    public static final String REFRESH_TOKEN_INVALID = "REFRESH_TOKEN_INVALID";
    /** Refresh token já usado numa renovação: toda a sessão (família) foi revogada. */
    public static final String REFRESH_TOKEN_REUSED = "REFRESH_TOKEN_REUSED";

    // Concorrência
    /** O recurso foi alterado por outro pedido depois de ser lido ({@code version} diferente) (409). */
    public static final String CONCURRENT_MODIFICATION = "CONCURRENT_MODIFICATION";

    // OTP do cliente (S1-03). Os erros das definições são de campo (FieldErrorCodes).
    /** O cliente escolheu o canal, mas a escolha está desligada nas definições (400). */
    public static final String OTP_CHANNEL_CHOICE_DISABLED = "OTP_CHANNEL_CHOICE_DISABLED";
    /** Canal inactivo ou incompatível com o tipo de identificador (400). */
    public static final String OTP_CHANNEL_NOT_AVAILABLE = "OTP_CHANNEL_NOT_AVAILABLE";
    /** Demasiados pedidos na última hora (429, com {@code retryAfterSeconds}). */
    public static final String OTP_RATE_LIMITED = "OTP_RATE_LIMITED";
    /** Novo pedido antes do tempo de reenvio (429, com {@code retryAfterSeconds}). */
    public static final String OTP_RESEND_TOO_SOON = "OTP_RESEND_TOO_SOON";
    /** Nenhum canal conseguiu enviar o código (502). */
    public static final String OTP_DELIVERY_FAILED = "OTP_DELIVERY_FAILED";
    public static final String OTP_CHALLENGE_NOT_FOUND = "OTP_CHALLENGE_NOT_FOUND";
    public static final String OTP_ALREADY_USED = "OTP_ALREADY_USED";
    /** Há um código mais recente para o mesmo identificador (400). */
    public static final String OTP_SUPERSEDED = "OTP_SUPERSEDED";
    public static final String OTP_EXPIRED = "OTP_EXPIRED";
    /** Código errado (400, com {@code attemptsRemaining}). */
    public static final String OTP_INVALID = "OTP_INVALID";
    public static final String OTP_ATTEMPTS_EXCEEDED = "OTP_ATTEMPTS_EXCEEDED";
    /** Conta desactivada (403). */
    public static final String ACCOUNT_DISABLED = "ACCOUNT_DISABLED";

    // Organizações (S1-04)
    /** Organização inexistente ou fora do âmbito de quem pede (404). */
    public static final String ORGANIZATION_NOT_FOUND = "ORGANIZATION_NOT_FOUND";
    /** Organização suspensa (409). */
    public static final String ORGANIZATION_NOT_ACTIVE = "ORGANIZATION_NOT_ACTIVE";
    public static final String NUIT_ALREADY_IN_USE = "NUIT_ALREADY_IN_USE";
    public static final String SHORT_CODE_ALREADY_IN_USE = "SHORT_CODE_ALREADY_IN_USE";
    /** O provedor do cliente indicado não existe ou não é do tipo {@code OMBUDSMAN} (400). */
    public static final String OMBUDSMAN_INVALID = "OMBUDSMAN_INVALID";

    // Convites e activação de staff (S1-04)
    /** Perfil desconhecido ou que não pode ser atribuído no tipo de organização (400). */
    public static final String ROLE_NOT_ALLOWED_FOR_ORGANIZATION = "ROLE_NOT_ALLOWED_FOR_ORGANIZATION";
    /** O e-mail já pertence a outra conta de staff (409). */
    public static final String EMAIL_ALREADY_IN_USE = "EMAIL_ALREADY_IN_USE";
    /** Convite desconhecido, expirado, revogado ou já aceite (404). */
    public static final String INVITATION_INVALID = "INVITATION_INVALID";
    /** Activação antes de definir a palavra-passe ({@code accept}) (400). */
    public static final String INVITATION_NOT_ACCEPTED = "INVITATION_NOT_ACCEPTED";
    /** Código TOTP errado (400). */
    public static final String TOTP_INVALID = "TOTP_INVALID";
    /** Palavra-passe recusada pela política; os motivos vão em {@code errors[]} (400). */
    public static final String PASSWORD_TOO_WEAK = "PASSWORD_TOO_WEAK";

    // Login de staff, recuperação de palavra-passe e gestão de utilizadores (S1-05)
    /** E-mail, palavra-passe ou código TOTP errados, sem dizer qual (401). */
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    /** Conta bloqueada por falhas de login (403, com {@code retryAfterSeconds}). */
    public static final String ACCOUNT_LOCKED = "ACCOUNT_LOCKED";
    /** Login de staff de uma organização suspensa (403). */
    public static final String ORGANIZATION_SUSPENDED = "ORGANIZATION_SUSPENDED";
    /** Token de recuperação desconhecido, expirado, já usado ou invalidado (400). */
    public static final String RESET_TOKEN_INVALID = "RESET_TOKEN_INVALID";
    /** Utilizador de staff inexistente ou fora do âmbito de quem pede (404). */
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    /** O gestor não pode alterar, desactivar nem repor o acesso da própria conta (400). */
    public static final String CANNOT_MODIFY_SELF = "CANNOT_MODIFY_SELF";
    /** A organização (ou a plataforma) ficaria sem nenhum gestor activo (400). */
    public static final String LAST_MANAGER = "LAST_MANAGER";

    // Perfil e contactos do cliente (S1-06)
    /** O telemóvel ou e-mail já pertence a outra conta de cliente (409). */
    public static final String CONTACT_ALREADY_IN_USE = "CONTACT_ALREADY_IN_USE";
    /** O contacto já é o actual da conta e está verificado (409). */
    public static final String CONTACT_ALREADY_VERIFIED = "CONTACT_ALREADY_VERIFIED";
    /** A conta ficaria sem nenhum contacto verificado (400). */
    public static final String LAST_CONTACT = "LAST_CONTACT";

    // Ficheiros (S2-01)
    /** Ficheiro inexistente, limpo, ou que quem pede não pode ler (404; nunca 403). */
    public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";
    /** Ficheiro com 0 bytes (400). */
    public static final String FILE_EMPTY = "FILE_EMPTY";
    /** Ficheiro com mais de 10 MB (413). */
    public static final String FILE_TOO_LARGE = "FILE_TOO_LARGE";
    /** Conteúdo que não é JPEG, PNG, HEIC ou PDF, fora dos tipos aceites, ou diferente do declarado (415). */
    public static final String FILE_TYPE_NOT_ALLOWED = "FILE_TYPE_NOT_ALLOWED";
    /** O conteúdo de um ficheiro registado não pôde ser lido do armazenamento (500). */
    public static final String FILE_STORAGE_ERROR = "FILE_STORAGE_ERROR";

    // Tabelas de referência, parâmetros e formatos de matrícula (S2-02)
    /** Domínio de valores de referência desconhecido (404). */
    public static final String REFERENCE_DOMAIN_UNKNOWN = "REFERENCE_DOMAIN_UNKNOWN";
    /** Valor de referência inexistente ou de outro domínio (404). */
    public static final String REFERENCE_VALUE_NOT_FOUND = "REFERENCE_VALUE_NOT_FOUND";
    /** Código já usado no mesmo domínio (409). */
    public static final String REFERENCE_CODE_ALREADY_EXISTS = "REFERENCE_CODE_ALREADY_EXISTS";
    /** Parâmetro da plataforma inexistente (404). */
    public static final String PLATFORM_SETTING_NOT_FOUND = "PLATFORM_SETTING_NOT_FOUND";
    /** Formato de matrícula inexistente (404). */
    public static final String PLATE_FORMAT_NOT_FOUND = "PLATE_FORMAT_NOT_FOUND";
    /** Código de formato de matrícula já usado (409). */
    public static final String PLATE_FORMAT_CODE_ALREADY_EXISTS = "PLATE_FORMAT_CODE_ALREADY_EXISTS";
    /** Desactivar o último formato de matrícula activo (409). */
    public static final String LAST_ACTIVE_PLATE_FORMAT = "LAST_ACTIVE_PLATE_FORMAT";

    // Produtos e versões do catálogo (S2-04)
    /** Produto inexistente ou de outra seguradora (404). */
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    /** Versão inexistente, de outra seguradora, ou (no catálogo público) nunca publicada (404). */
    public static final String PRODUCT_VERSION_NOT_FOUND = "PRODUCT_VERSION_NOT_FOUND";
    /** Documento inexistente na versão (404). */
    public static final String PRODUCT_DOCUMENT_NOT_FOUND = "PRODUCT_DOCUMENT_NOT_FOUND";
    /** Código de produto já usado na mesma seguradora (409). */
    public static final String PRODUCT_CODE_ALREADY_EXISTS = "PRODUCT_CODE_ALREADY_EXISTS";
    /** Mudar o tipo de um produto que já foi publicado (409). */
    public static final String PRODUCT_TYPE_LOCKED = "PRODUCT_TYPE_LOCKED";
    /** Escrita num produto retirado (409). */
    public static final String PRODUCT_RETIRED = "PRODUCT_RETIRED";
    /** O produto já tem uma versão em preparação ({@code DRAFT}) (409). */
    public static final String PRODUCT_DRAFT_EXISTS = "PRODUCT_DRAFT_EXISTS";
    /** Alteração a uma versão que não está em preparação (409). */
    public static final String PRODUCT_VERSION_NOT_EDITABLE = "PRODUCT_VERSION_NOT_EDITABLE";
    /** Mais de 10 documentos numa versão (409). */
    public static final String PRODUCT_DOCUMENT_LIMIT_REACHED = "PRODUCT_DOCUMENT_LIMIT_REACHED";
    /** A versão não cumpre as regras de publicação; as falhas vão em {@code errors[]} (400). */
    public static final String PRODUCT_VERSION_NOT_PUBLISHABLE = "PRODUCT_VERSION_NOT_PUBLISHABLE";
    // Veículos e condutores do cliente (S2-03)
    /** Veículo inexistente, retirado ou de outro cliente (404). */
    public static final String VEHICLE_NOT_FOUND = "VEHICLE_NOT_FOUND";
    /** O cliente já tem um veículo activo com esta matrícula (409). */
    public static final String VEHICLE_ALREADY_REGISTERED = "VEHICLE_ALREADY_REGISTERED";
    /** Máximo de 20 veículos activos por cliente (409). */
    public static final String VEHICLE_LIMIT_REACHED = "VEHICLE_LIMIT_REACHED";
    /** O veículo não pode ser retirado agora (ex.: apólice em vigor) (409). */
    public static final String VEHICLE_REMOVAL_BLOCKED = "VEHICLE_REMOVAL_BLOCKED";
    /** Matrícula sem registo no registo automóvel (404). */
    public static final String VEHICLE_REGISTRY_NOT_FOUND = "VEHICLE_REGISTRY_NOT_FOUND";
    /** Registo automóvel indisponível (503). */
    public static final String VEHICLE_REGISTRY_UNAVAILABLE = "VEHICLE_REGISTRY_UNAVAILABLE";
    /** Condutor inexistente, retirado ou de outro cliente (404). */
    public static final String DRIVER_NOT_FOUND = "DRIVER_NOT_FOUND";
    /** O cliente já tem um condutor activo com esta carta (409). */
    public static final String DRIVER_ALREADY_REGISTERED = "DRIVER_ALREADY_REGISTERED";
    /** Máximo de 10 condutores activos por cliente (409). */
    public static final String DRIVER_LIMIT_REACHED = "DRIVER_LIMIT_REACHED";
    /** O cliente já se registou como condutor ({@code isOwner}) (409). */
    public static final String OWNER_DRIVER_ALREADY_EXISTS = "OWNER_DRIVER_ALREADY_EXISTS";
    /** O condutor é o habitual de um veículo activo (409). */
    public static final String DRIVER_IS_HABITUAL = "DRIVER_IS_HABITUAL";
    // Tarifa interna (S3-01)
    /** Tarifa (ou pedido com a tarifa) com mais de 262 144 bytes (400). */
    public static final String RATING_RULES_TOO_LARGE = "RATING_RULES_TOO_LARGE";

    // Cotações (S3-02)
    /** Pedido de cotação inexistente ou fora do âmbito de quem pede (404). */
    public static final String QUOTE_REQUEST_NOT_FOUND = "QUOTE_REQUEST_NOT_FOUND";
    /** Cotação inexistente ou fora do âmbito de quem pede (404). */
    public static final String QUOTE_NOT_FOUND = "QUOTE_NOT_FOUND";
    /** Cotação fora da validade ou com a data de início já passada (409). */
    public static final String QUOTE_EXPIRED = "QUOTE_EXPIRED";
    /** Cotação sem preço ({@code NOT_AVAILABLE}) (409). */
    public static final String QUOTE_NOT_AVAILABLE = "QUOTE_NOT_AVAILABLE";
    /** Cotação já usada numa proposta (409). */
    public static final String QUOTE_ALREADY_USED = "QUOTE_ALREADY_USED";
    /** Demasiados pedidos de cotação na última hora (429, com {@code retryAfterSeconds}). */
    public static final String QUOTE_RATE_LIMITED = "QUOTE_RATE_LIMITED";
    /** Danos próprios pedidos para um veículo sem valor comercial (400). */
    public static final String VEHICLE_VALUE_REQUIRED = "VEHICLE_VALUE_REQUIRED";
    /** A organização não é uma seguradora (400). */
    public static final String ORGANIZATION_NOT_INSURER = "ORGANIZATION_NOT_INSURER";

    // KYC do cliente (S4-01)
    /** O perfil do cliente está incompleto ({@code CustomerProfile.isComplete()}) (409). */
    public static final String PROFILE_INCOMPLETE = "PROFILE_INCOMPLETE";
    /** O cliente já tem uma verificação de identidade {@code PENDING} (409). */
    public static final String KYC_ALREADY_PENDING = "KYC_ALREADY_PENDING";
    /** A identidade do cliente já está verificada e o perfil não mudou (409). */
    public static final String KYC_ALREADY_VERIFIED = "KYC_ALREADY_VERIFIED";
    /** Verificação de identidade inexistente ou de outra seguradora (404). */
    public static final String KYC_NOT_FOUND = "KYC_NOT_FOUND";

    // Propostas (S4-02)
    /** Já existe uma proposta para esta cotação; propriedade {@code proposalId} (409). */
    public static final String PROPOSAL_ALREADY_EXISTS = "PROPOSAL_ALREADY_EXISTS";
    /** Proposta inexistente ou de outro cliente, mediador ou seguradora (404). */
    public static final String PROPOSAL_NOT_FOUND = "PROPOSAL_NOT_FOUND";
    /** A proposta não está em {@code DRAFT} nem {@code INFO_REQUESTED} (409). */
    public static final String PROPOSAL_NOT_EDITABLE = "PROPOSAL_NOT_EDITABLE";
    /** A seguradora da cotação não está activa (409). */
    public static final String INSURER_NOT_ACTIVE = "INSURER_NOT_ACTIVE";
    /** O questionário de adequação não deu {@code WARNED}: não há avisos para confirmar (409). */
    public static final String SUITABILITY_NOT_WARNED = "SUITABILITY_NOT_WARNED";
    /** Dados do veículo ou do condutor usados na tarifa mudaram depois da cotação; propriedade {@code fields} (409). */
    public static final String QUOTE_DATA_CHANGED = "QUOTE_DATA_CHANGED";

    // PDF e assinatura da proposta (S4-04)
    /** Modelo de PDF desconhecido (erro de programação) (500). */
    public static final String PDF_TEMPLATE_NOT_FOUND = "PDF_TEMPLATE_NOT_FOUND";
    /** O PDF não pôde ser gerado (modelo inválido, texto em falta ou erro do renderizador) (500). */
    public static final String PDF_RENDER_FAILED = "PDF_RENDER_FAILED";
    /** A conta do signatário não tem telemóvel nem e-mail verificado para receber o código (409). */
    public static final String SIGNER_CONTACT_MISSING = "SIGNER_CONTACT_MISSING";
    /** Faltam passos da proposta; propriedade {@code missing} (lista de itens) (409). */
    public static final String PROPOSAL_INCOMPLETE = "PROPOSAL_INCOMPLETE";
    /** Não há pedido de assinatura em curso, ou é de outro código (409). */
    public static final String SIGN_REQUEST_NOT_FOUND = "SIGN_REQUEST_NOT_FOUND";
    /** A proposta mudou depois de o código de assinatura ter sido pedido (409). */
    public static final String PROPOSAL_CHANGED_SINCE_SIGN_REQUEST = "PROPOSAL_CHANGED_SINCE_SIGN_REQUEST";
    // Vistoria de danos próprios (S4-03)
    /** A proposta não tem coberturas de danos próprios: não há vistoria (409). */
    public static final String INSPECTION_NOT_REQUIRED = "INSPECTION_NOT_REQUIRED";
    /** Vistoria inexistente, de outra seguradora ou de uma proposta ainda não submetida (404). */
    public static final String INSPECTION_NOT_FOUND = "INSPECTION_NOT_FOUND";
    /** A vistoria não está em {@code PENDING} nem {@code REJECTED} (409). */
    public static final String INSPECTION_NOT_EDITABLE = "INSPECTION_NOT_EDITABLE";
    /** Faltam ângulos obrigatórios; propriedade {@code missingSlots} (409). */
    public static final String INSPECTION_PHOTOS_MISSING = "INSPECTION_PHOTOS_MISSING";
    /** Fotografia inexistente, de outra vistoria ou já retirada (404). */
    public static final String PHOTO_NOT_FOUND = "PHOTO_NOT_FOUND";
    /** A proposta não está em {@code UNDER_REVIEW} (409). */
    public static final String PROPOSAL_NOT_UNDER_REVIEW = "PROPOSAL_NOT_UNDER_REVIEW";

    // Submissão e aceitação da proposta (S4-05)
    /** Aceitar sem o KYC do cliente verificado, com o total acima do limiar do KYC simplificado (409). */
    public static final String KYC_NOT_VERIFIED = "KYC_NOT_VERIFIED";
    /** Aceitar uma proposta com vistoria obrigatória ainda não aprovada (409). */
    public static final String INSPECTION_NOT_APPROVED = "INSPECTION_NOT_APPROVED";

    // Proposta criada pelo mediador (S4-06)
    /** A conta do cliente da cotação do mediador está desactivada ({@code DISABLED}) (409). */
    public static final String CLIENT_ACCOUNT_DISABLED = "CLIENT_ACCOUNT_DISABLED";

    // Formas de pagamento por seguradora (S5-01)
    /** Método de pagamento desconhecido ({@code MPESA}, {@code BANK_REFERENCE}) ou ainda sem configuração (404). */
    public static final String PAYMENT_METHOD_NOT_FOUND = "PAYMENT_METHOD_NOT_FOUND";
    /** Activar um método com dados em falta ou inválidos; {@code errors[]} com os campos (409). */
    public static final String PAYMENT_METHOD_INCOMPLETE = "PAYMENT_METHOD_INCOMPLETE";
    /** O cofre de segredos não respondeu; nada foi gravado (503). */
    public static final String SECRET_STORE_UNAVAILABLE = "SECRET_STORE_UNAVAILABLE";

    /** Estado HTTP de cada código. */
    private static final Map<String, Integer> STATUS = Map.ofEntries(
            Map.entry(VALIDATION_ERROR, 400),
            Map.entry(NOT_FOUND, 404),
            Map.entry(UNAUTHORIZED, 401),
            Map.entry(FORBIDDEN, 403),
            Map.entry(INTERNAL_ERROR, 500),
            Map.entry(INVALID_CURRENCY, 400),
            Map.entry(INVALID_MSISDN, 400),
            Map.entry(INVALID_NUIT, 400),
            Map.entry(INVALID_PLATE, 400),
            Map.entry(INVALID_EMAIL, 400),
            Map.entry(INVALID_PHONE, 400),
            Map.entry(TENANT_REQUIRED, 500),
            Map.entry(DECRYPTION_FAILED, 500),
            Map.entry(INVALID_STATE_TRANSITION, 409),
            Map.entry(LICENSE_NUMBER_REQUIRED, 400),
            Map.entry(REFRESH_TOKEN_INVALID, 401),
            Map.entry(REFRESH_TOKEN_REUSED, 401),
            Map.entry(CONCURRENT_MODIFICATION, 409),
            Map.entry(OTP_CHANNEL_CHOICE_DISABLED, 400),
            Map.entry(OTP_CHANNEL_NOT_AVAILABLE, 400),
            Map.entry(OTP_RATE_LIMITED, 429),
            Map.entry(OTP_RESEND_TOO_SOON, 429),
            Map.entry(OTP_DELIVERY_FAILED, 502),
            Map.entry(OTP_CHALLENGE_NOT_FOUND, 404),
            Map.entry(OTP_ALREADY_USED, 400),
            Map.entry(OTP_SUPERSEDED, 400),
            Map.entry(OTP_EXPIRED, 400),
            Map.entry(OTP_INVALID, 400),
            Map.entry(OTP_ATTEMPTS_EXCEEDED, 400),
            Map.entry(ACCOUNT_DISABLED, 403),
            Map.entry(ORGANIZATION_NOT_FOUND, 404),
            Map.entry(ORGANIZATION_NOT_ACTIVE, 409),
            Map.entry(NUIT_ALREADY_IN_USE, 409),
            Map.entry(SHORT_CODE_ALREADY_IN_USE, 409),
            Map.entry(OMBUDSMAN_INVALID, 400),
            Map.entry(ROLE_NOT_ALLOWED_FOR_ORGANIZATION, 400),
            Map.entry(EMAIL_ALREADY_IN_USE, 409),
            Map.entry(INVITATION_INVALID, 404),
            Map.entry(INVITATION_NOT_ACCEPTED, 400),
            Map.entry(TOTP_INVALID, 400),
            Map.entry(PASSWORD_TOO_WEAK, 400),
            Map.entry(INVALID_CREDENTIALS, 401),
            Map.entry(ACCOUNT_LOCKED, 403),
            Map.entry(ORGANIZATION_SUSPENDED, 403),
            Map.entry(RESET_TOKEN_INVALID, 400),
            Map.entry(USER_NOT_FOUND, 404),
            Map.entry(CANNOT_MODIFY_SELF, 400),
            Map.entry(LAST_MANAGER, 400),
            Map.entry(CONTACT_ALREADY_IN_USE, 409),
            Map.entry(CONTACT_ALREADY_VERIFIED, 409),
            Map.entry(LAST_CONTACT, 400),
            Map.entry(FILE_NOT_FOUND, 404),
            Map.entry(FILE_EMPTY, 400),
            Map.entry(FILE_TOO_LARGE, 413),
            Map.entry(FILE_TYPE_NOT_ALLOWED, 415),
            Map.entry(FILE_STORAGE_ERROR, 500),
            Map.entry(REFERENCE_DOMAIN_UNKNOWN, 404),
            Map.entry(REFERENCE_VALUE_NOT_FOUND, 404),
            Map.entry(REFERENCE_CODE_ALREADY_EXISTS, 409),
            Map.entry(PLATFORM_SETTING_NOT_FOUND, 404),
            Map.entry(PLATE_FORMAT_NOT_FOUND, 404),
            Map.entry(PLATE_FORMAT_CODE_ALREADY_EXISTS, 409),
            Map.entry(LAST_ACTIVE_PLATE_FORMAT, 409),
            Map.entry(PRODUCT_NOT_FOUND, 404),
            Map.entry(PRODUCT_VERSION_NOT_FOUND, 404),
            Map.entry(PRODUCT_DOCUMENT_NOT_FOUND, 404),
            Map.entry(PRODUCT_CODE_ALREADY_EXISTS, 409),
            Map.entry(PRODUCT_TYPE_LOCKED, 409),
            Map.entry(PRODUCT_RETIRED, 409),
            Map.entry(PRODUCT_DRAFT_EXISTS, 409),
            Map.entry(PRODUCT_VERSION_NOT_EDITABLE, 409),
            Map.entry(PRODUCT_DOCUMENT_LIMIT_REACHED, 409),
            Map.entry(PRODUCT_VERSION_NOT_PUBLISHABLE, 400),
            Map.entry(VEHICLE_NOT_FOUND, 404),
            Map.entry(VEHICLE_ALREADY_REGISTERED, 409),
            Map.entry(VEHICLE_LIMIT_REACHED, 409),
            Map.entry(VEHICLE_REMOVAL_BLOCKED, 409),
            Map.entry(VEHICLE_REGISTRY_NOT_FOUND, 404),
            Map.entry(VEHICLE_REGISTRY_UNAVAILABLE, 503),
            Map.entry(DRIVER_NOT_FOUND, 404),
            Map.entry(DRIVER_ALREADY_REGISTERED, 409),
            Map.entry(DRIVER_LIMIT_REACHED, 409),
            Map.entry(OWNER_DRIVER_ALREADY_EXISTS, 409),
            Map.entry(DRIVER_IS_HABITUAL, 409),
            Map.entry(RATING_RULES_TOO_LARGE, 400),
            Map.entry(QUOTE_REQUEST_NOT_FOUND, 404),
            Map.entry(QUOTE_NOT_FOUND, 404),
            Map.entry(QUOTE_EXPIRED, 409),
            Map.entry(QUOTE_NOT_AVAILABLE, 409),
            Map.entry(QUOTE_ALREADY_USED, 409),
            Map.entry(QUOTE_RATE_LIMITED, 429),
            Map.entry(VEHICLE_VALUE_REQUIRED, 400),
            Map.entry(ORGANIZATION_NOT_INSURER, 400),
            Map.entry(PROFILE_INCOMPLETE, 409),
            Map.entry(KYC_ALREADY_PENDING, 409),
            Map.entry(KYC_ALREADY_VERIFIED, 409),
            Map.entry(KYC_NOT_FOUND, 404),
            Map.entry(PROPOSAL_ALREADY_EXISTS, 409),
            Map.entry(PROPOSAL_NOT_FOUND, 404),
            Map.entry(PROPOSAL_NOT_EDITABLE, 409),
            Map.entry(INSURER_NOT_ACTIVE, 409),
            Map.entry(SUITABILITY_NOT_WARNED, 409),
            Map.entry(QUOTE_DATA_CHANGED, 409),
            Map.entry(PDF_TEMPLATE_NOT_FOUND, 500),
            Map.entry(PDF_RENDER_FAILED, 500),
            Map.entry(SIGNER_CONTACT_MISSING, 409),
            Map.entry(PROPOSAL_INCOMPLETE, 409),
            Map.entry(SIGN_REQUEST_NOT_FOUND, 409),
            Map.entry(PROPOSAL_CHANGED_SINCE_SIGN_REQUEST, 409),
            Map.entry(INSPECTION_NOT_REQUIRED, 409),
            Map.entry(INSPECTION_NOT_FOUND, 404),
            Map.entry(INSPECTION_NOT_EDITABLE, 409),
            Map.entry(INSPECTION_PHOTOS_MISSING, 409),
            Map.entry(PHOTO_NOT_FOUND, 404),
            Map.entry(PROPOSAL_NOT_UNDER_REVIEW, 409),
            Map.entry(KYC_NOT_VERIFIED, 409),
            Map.entry(INSPECTION_NOT_APPROVED, 409),
            Map.entry(CLIENT_ACCOUNT_DISABLED, 409),
            Map.entry(PAYMENT_METHOD_NOT_FOUND, 404),
            Map.entry(PAYMENT_METHOD_INCOMPLETE, 409),
            Map.entry(SECRET_STORE_UNAVAILABLE, 503));

    private ErrorCodes() {
    }

    /** Estado HTTP de um código desta lista. */
    public static int status(String code) {
        Integer status = STATUS.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Unknown error code: " + code);
        }
        return status;
    }

    /**
     * Todos os códigos (as constantes desta classe) com o seu estado, por ordem alfabética.
     * Falha se alguma constante não tiver estado.
     */
    public static Map<String, Integer> all() {
        Map<String, Integer> all = new TreeMap<>();
        for (String code : constants(ErrorCodes.class)) {
            all.put(code, status(code));
        }
        return all;
    }

    /** Valores das constantes {@code public static final String} de {@code type}. */
    static List<String> constants(Class<?> type) {
        List<String> values = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                    && field.getType() == String.class) {
                try {
                    values.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return values;
    }
}
