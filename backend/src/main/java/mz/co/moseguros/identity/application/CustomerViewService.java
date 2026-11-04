package mz.co.moseguros.identity.application;

import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.KycStatus;
import mz.co.moseguros.identity.domain.AccountStatus;
import mz.co.moseguros.identity.domain.CustomerProfile;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.KycCheck;
import mz.co.moseguros.identity.domain.KycCheckRepository;
import mz.co.moseguros.identity.domain.ProfileFingerprint;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dados e estado KYC de um cliente para os outros módulos ({@code IdentityApi.customer} e
 * {@code IdentityApi.kycStatus}; S4-01 §6). Corre em {@code SYSTEM}: quem chama pode estar em âmbito
 * {@code ORG} (mediador, seguradora), em que o RLS esconde o perfil do cliente. Quem chama é
 * responsável por só pedir clientes com quem tem uma relação legítima (ex.: o dono da proposta).
 */
@Service
public class CustomerViewService {

    private final UserAccountRepository accounts;
    private final CustomerProfileRepository profiles;
    private final KycCheckRepository checks;

    public CustomerViewService(UserAccountRepository accounts, CustomerProfileRepository profiles,
                               KycCheckRepository checks) {
        this.accounts = accounts;
        this.profiles = profiles;
        this.checks = checks;
    }

    /** Conta {@code CUSTOMER} com o perfil; outra conta ou inexistente → vazio. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public Optional<CustomerView> customer(UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return accounts.findById(userId)
                .filter(UserAccount::isCustomer)
                .map(account -> view(account, profiles.findByUserId(userId).orElse(null)));
    }

    /** Estado KYC actual (S4-01 §5); conta inexistente → {@code NONE}. */
    @RunAsSystem
    @Transactional(readOnly = true)
    public KycStatus kycStatus(UUID userId) {
        return statusOf(userId);
    }

    /** Estado KYC no âmbito da transacção actual (o RLS tem de deixar ver o perfil e as verificações). */
    KycStatus statusOf(UUID userId) {
        KycCheck latest = checks.findFirstByUserIdOrderBySubmittedAtDesc(userId).orElse(null);
        if (latest == null) {
            return KycStatus.none();
        }
        String current = profiles.findByUserId(userId).map(ProfileFingerprint::of).orElse(null);
        return KycCheck.statusOf(latest, current);
    }

    private static CustomerView view(UserAccount account, CustomerProfile profile) {
        boolean hasProfile = profile != null;
        return new CustomerView(account.getId(),
                hasProfile ? profile.getFullName() : null,
                hasProfile ? profile.getBirthDate() : null,
                hasProfile && profile.getIdDocType() != null ? profile.getIdDocType().name() : null,
                hasProfile ? profile.getIdDocNumber() : null,
                hasProfile ? profile.getNuit() : null,
                hasProfile ? profile.getAddress() : null,
                hasProfile && profile.getProvince() != null ? profile.getProvince().name() : null,
                account.getMsisdn(), account.isMsisdnVerified(),
                account.getEmail(), account.isEmailVerified(), account.getPreferredLanguage(),
                hasProfile && profile.isComplete(),
                account.getStatus() == AccountStatus.ACTIVE);
    }
}
