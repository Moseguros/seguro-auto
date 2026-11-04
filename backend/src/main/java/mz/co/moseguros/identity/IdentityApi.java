package mz.co.moseguros.identity;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.application.CustomerAccountFactory;
import mz.co.moseguros.identity.application.CustomerViewService;
import mz.co.moseguros.identity.application.IdentitySystemLookups;
import mz.co.moseguros.identity.application.OtpService;
import mz.co.moseguros.identity.application.TokenService;
import mz.co.moseguros.identity.domain.CustomerProfileRules;
import mz.co.moseguros.identity.domain.IdDocType;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.Problems.FieldError;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Identidade (modelo transversal §3): sessões ({@link #issueTokens},
 * login por OTP e de staff) e contas de cliente pelo telemóvel ({@link #findOrCreateCustomerByMsisdn},
 * usado pelo {@code rating} desde o S3-02), dados e estado KYC de um cliente ({@link #customer},
 * {@link #kycStatus}, S4-01) e códigos de assinatura de documentos ({@link #requestSignOtp},
 * {@link #verifySignOtp}, S4-04) e contactos do staff por permissão ({@link #staffWithPermission},
 * S4-05).
 */
@Service
public class IdentityApi {

    private final TokenService tokenService;
    private final CustomerAccountFactory customerAccounts;
    private final CustomerViewService customerViews;
    private final OtpService otpService;
    private final IdentitySystemLookups lookups;
    private final Clock clock;

    public IdentityApi(TokenService tokenService, CustomerAccountFactory customerAccounts,
                       CustomerViewService customerViews, OtpService otpService, IdentitySystemLookups lookups,
                       Clock clock) {
        this.tokenService = tokenService;
        this.customerAccounts = customerAccounts;
        this.customerViews = customerViews;
        this.otpService = otpService;
        this.lookups = lookups;
        this.clock = clock;
    }

    /**
     * Valida e normaliza dados pessoais com as regras do perfil do cliente (S1-06 §2): {@code REQUIRED},
     * {@code INVALID_NAME}, {@code UNDERAGE}, {@code INVALID_DATE}, {@code INVALID_DOCUMENT_NUMBER},
     * {@code INVALID_NUIT}, {@code SIZE} (morada) e {@code INVALID_VALUE} (tipo de documento desconhecido).
     * Não lê nem grava nada. Usado pelo {@code proposal} para o tomador preenchido pelo mediador (S4-06).
     *
     * @param currentIdDocType tipo de documento actual ou {@code null} (sem dados anteriores)
     * @param hasIdDocNumber   já existe um número de documento (omitido mantém-no, se o tipo não mudar)
     * @param hasNuit          já existe um NUIT (omitido mantém-no)
     * @param fieldPrefix      prefixo de {@code errors[].field} (ex.: {@code policyholder.})
     * @throws DomainException 400 {@code VALIDATION_ERROR} com um erro por campo em {@code errors}
     */
    public ValidPersonalData validatePersonalData(PersonalData data, String currentIdDocType, boolean hasIdDocNumber,
                                                  boolean hasNuit, String fieldPrefix) {
        String prefix = fieldPrefix == null ? "" : fieldPrefix;
        IdDocType type = null;
        FieldError typeError = null;
        if (data.idDocType() != null) {
            try {
                type = IdDocType.valueOf(data.idDocType().strip().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                typeError = new FieldError("idDocType", FieldErrorCodes.INVALID_VALUE, "Must be BI, PASSPORT or DIRE");
            }
        }
        IdDocType current = currentIdDocType == null ? null : IdDocType.valueOf(currentIdDocType);
        CustomerProfileRules.Changes changes = new CustomerProfileRules.Changes(data.fullName(), data.birthDate(),
                type, data.idDocNumber(), data.nuit(), data.address(), data.province());
        LocalDate today = LocalDate.ofInstant(clock.instant(), CustomerProfileRules.ZONE);
        List<FieldError> errors = new ArrayList<>(CustomerProfileRules.validate(current, hasIdDocNumber,
                hasNuit, changes, today));
        if (typeError != null) {
            errors.removeIf(e -> e.field().equals("idDocType"));
            errors.add(typeError);
        }
        if (!errors.isEmpty()) {
            throw DomainException.of(ErrorCodes.VALIDATION_ERROR, "Request validation failed")
                    .withProperty("errors", errors.stream()
                            .map(e -> new FieldError(prefix + e.field(), e.code(), e.message())).toList());
        }
        String address = data.address() == null || data.address().isBlank() ? null : data.address().trim();
        String nuit = data.nuit() == null || data.nuit().isBlank() ? null : Nuit.of(data.nuit()).value();
        return new ValidPersonalData(CustomerProfileRules.normalizeName(data.fullName()), data.birthDate(),
                type.name(), CustomerProfileRules.normalizeDocumentNumber(data.idDocNumber()), nuit, address,
                data.province());
    }

    /**
     * Contas {@code STAFF} {@code ACTIVE} da organização com algum perfil que tenha a permissão,
     * ordenadas por {@code displayName} (S4-05 §10). É o método único de consulta de staff por
     * permissão (decisão D10): o S8 e o S10 acrescentam {@code staffDisplayNames} e
     * {@code staffMember} com o mesmo {@link StaffContact}. Corre em {@code SYSTEM}.
     */
    public List<StaffContact> staffWithPermission(UUID orgId, String permission) {
        return lookups.staffWithPermission(orgId, permission);
    }

    /**
     * Envia ao signatário um código que assina o documento com {@code documentHash} (S4-04 §4):
     * telemóvel da conta (verificado ou não) ou, sem ele, o e-mail verificado; canal, alternativa e
     * limites do login (S1-03), contando também os códigos de assinatura. Os códigos de assinatura
     * anteriores do utilizador ficam substituídos. Corre em {@code SYSTEM}.
     *
     * @throws mz.co.moseguros.shared.error.DomainException 409 {@code SIGNER_CONTACT_MISSING}; 429
     *         {@code OTP_RATE_LIMITED} / {@code OTP_RESEND_TOO_SOON} (com {@code Retry-After}); os outros
     *         códigos {@code OTP_*} do pedido de login
     */
    public SignOtpChallenge requestSignOtp(SignOtpRequest request) {
        return otpService.requestSign(request);
    }

    /**
     * Valida o código de assinatura do documento com {@code documentHash} (S4-04 §4): as regras e os
     * códigos do login ({@code OTP_CHALLENGE_NOT_FOUND}, {@code OTP_ALREADY_USED}, {@code OTP_SUPERSEDED},
     * {@code OTP_EXPIRED}, {@code OTP_ATTEMPTS_EXCEEDED}, {@code OTP_INVALID} com
     * {@code attemptsRemaining}); desafio que não é de assinatura ou de outro utilizador → 404. As
     * tentativas erradas ficam gravadas mesmo com erro: quem chama não pode fazer rollback por causa de
     * uma {@code DomainException}. Corre em {@code SYSTEM}.
     */
    public SignOtpVerification verifySignOtp(UUID signerUserId, UUID challengeId, String code, String documentHash) {
        return otpService.verifySign(signerUserId, challengeId, code, documentHash);
    }

    /**
     * Abre uma sessão nova para a conta e devolve o par de tokens.
     *
     * @param deviceInfo o {@code User-Agent} do pedido (pode ser {@code null})
     */
    public TokenPair issueTokens(UserAccount account, String deviceInfo) {
        return tokenService.issue(account, deviceInfo);
    }

    /**
     * Id da conta {@code CUSTOMER} com este telemóvel, em qualquer estado (uma conta {@code DISABLED}
     * também é devolvida: quem chama decide). Se não existir, cria-a {@code ACTIVE}, com o telemóvel
     * por verificar, perfil vazio e o perfil {@code CUSTOMER} (auditoria {@code CUSTOMER.REGISTER},
     * {@code source = FIND_OR_CREATE}); o telemóvel fica verificado no primeiro login por OTP.
     *
     * <p>Corre em {@code SYSTEM} (quem chama não precisa de âmbito especial) e numa transacção
     * própria; duas chamadas simultâneas para o mesmo número devolvem a mesma conta (S3-02 §6.1,
     * decisão D2). Usado no S3-02 (cotação do mediador), S4-06, S8 e S12.
     */
    public UUID findOrCreateCustomerByMsisdn(Msisdn msisdn) {
        return customerAccounts.findOrCreateByMsisdn(msisdn);
    }

    /**
     * Dados do cliente (conta {@code CUSTOMER}; outra conta ou inexistente → vazio), com o número do
     * documento e o NUIT em claro: nunca serializar nem registar em log.
     *
     * <p>Corre em {@code SYSTEM}: quem chama pode estar em âmbito {@code ORG}, em que o RLS esconde o
     * perfil. <strong>Quem chama é responsável por só pedir clientes com quem tem uma relação
     * legítima</strong> (ex.: o dono da proposta). Usado a partir do S4-02.
     */
    public Optional<CustomerView> customer(UUID userId) {
        return customerViews.customer(userId);
    }

    /**
     * Estado KYC actual do cliente (S4-01 §5), incluindo {@link KycState#OUTDATED} se o perfil mudou
     * depois da verificação. Corre em {@code SYSTEM}; mesma responsabilidade de {@link #customer}.
     */
    public KycStatus kycStatus(UUID userId) {
        return customerViews.kycStatus(userId);
    }
}
