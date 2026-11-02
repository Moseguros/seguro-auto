package mz.co.moseguros.identity.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Contas visíveis no âmbito da transacção (Row Level Security).
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByTypeAndMsisdn(AccountType type, Msisdn msisdn);

    Optional<UserAccount> findByTypeAndEmail(AccountType type, String email);

    List<UserAccount> findByOrganizationId(UUID organizationId);

    /** Ids das contas da organização (ex.: para revogar as sessões quando é suspensa). */
    @Query("select a.id from UserAccount a where a.organizationId = :organizationId")
    List<UUID> findIdsByOrganizationId(@Param("organizationId") UUID organizationId);

    /** Há alguma conta {@code ACTIVE} com o perfil indicado. */
    @Query("""
            select count(a) > 0 from UserAccount a, UserRole r
             where r.userId = a.id and r.roleCode = :roleCode
               and a.status = mz.co.moseguros.identity.domain.AccountStatus.ACTIVE""")
    boolean existsActiveWithRole(@Param("roleCode") String roleCode);
}
