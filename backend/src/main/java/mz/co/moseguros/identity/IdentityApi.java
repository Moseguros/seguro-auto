package mz.co.moseguros.identity;

import mz.co.moseguros.identity.application.TokenService;
import mz.co.moseguros.identity.domain.UserAccount;
import org.springframework.stereotype.Service;

/**
 * Interface pública do módulo Identidade. Nesta fase só serve os outros pacotes do próprio
 * módulo (login por OTP no S1-03, login de staff no S1-05); os outros módulos ainda não a usam.
 */
@Service
public class IdentityApi {

    private final TokenService tokenService;

    public IdentityApi(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Abre uma sessão nova para a conta e devolve o par de tokens.
     *
     * @param deviceInfo o {@code User-Agent} do pedido (pode ser {@code null})
     */
    public TokenPair issueTokens(UserAccount account, String deviceInfo) {
        return tokenService.issue(account, deviceInfo);
    }
}
