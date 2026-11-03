package mz.co.moseguros.identity.application;

import java.util.Set;

import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Preferências da conta autenticada ({@code PUT /me}, S1-06 §1): língua e, só para staff, o nome.
 * Corre no âmbito do próprio pedido (RLS).
 */
@Service
public class AccountService {

    static final Set<String> LANGUAGES = Set.of("pt", "en");
    static final int DISPLAY_NAME_MIN = 2;
    static final int DISPLAY_NAME_MAX = 120;

    private final CurrentUser currentUser;
    private final UserAccountRepository accounts;

    public AccountService(CurrentUser currentUser, UserAccountRepository accounts) {
        this.currentUser = currentUser;
        this.accounts = accounts;
    }

    /**
     * @param displayName só para staff ({@code null} mantém o actual); ignorado para clientes, cujo
     *                    nome está no perfil
     * @throws FieldValidationException {@code preferredLanguage} fora de {@code pt}/{@code en}
     *                                  ({@code PATTERN}) ou {@code displayName} fora de 2–120
     *                                  ({@code SIZE})
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void updateMe(String displayName, String preferredLanguage) {
        if (preferredLanguage == null || !LANGUAGES.contains(preferredLanguage)) {
            throw new FieldValidationException("preferredLanguage", FieldErrorCodes.PATTERN, "Must be pt or en");
        }
        UserAccount account = accounts.findById(currentUser.id())
                .orElseThrow(() -> new DomainException(ErrorCodes.UNAUTHORIZED, 401, "Account not found"));
        String name = null;
        if (account.isStaff() && displayName != null) {
            name = displayName.trim();
            if (name.length() < DISPLAY_NAME_MIN || name.length() > DISPLAY_NAME_MAX) {
                throw new FieldValidationException("displayName", FieldErrorCodes.SIZE,
                        "Must have " + DISPLAY_NAME_MIN + "-" + DISPLAY_NAME_MAX + " characters");
            }
        }
        account.updatePreferences(name, preferredLanguage);
    }
}
