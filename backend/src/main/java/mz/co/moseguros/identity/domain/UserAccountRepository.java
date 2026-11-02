package mz.co.moseguros.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Contas visíveis no âmbito da transacção (Row Level Security).
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByTypeAndMsisdn(AccountType type, Msisdn msisdn);

    Optional<UserAccount> findByTypeAndEmail(AccountType type, String email);

    List<UserAccount> findByOrganizationId(UUID organizationId);
}
