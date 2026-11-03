package mz.co.moseguros.identity;

import java.util.UUID;

import mz.co.moseguros.identity.application.CustomerAccountFactory;
import mz.co.moseguros.identity.application.TokenService;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.shared.domain.Msisdn;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Identidade (modelo transversal §3): sessões ({@link #issueTokens},
 * login por OTP e de staff) e contas de cliente pelo telemóvel ({@link #findOrCreateCustomerByMsisdn},
 * usado pelo {@code rating} desde o S3-02).
 */
@Service
public class IdentityApi {

    private final TokenService tokenService;
    private final CustomerAccountFactory customerAccounts;

    public IdentityApi(TokenService tokenService, CustomerAccountFactory customerAccounts) {
        this.tokenService = tokenService;
        this.customerAccounts = customerAccounts;
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
}
