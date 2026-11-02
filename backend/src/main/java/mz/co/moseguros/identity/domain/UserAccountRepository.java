package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import mz.co.moseguros.shared.domain.Msisdn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Contas visíveis no âmbito da transacção (Row Level Security).
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID>,
        JpaSpecificationExecutor<UserAccount> {

    Optional<UserAccount> findByTypeAndMsisdn(AccountType type, Msisdn msisdn);

    Optional<UserAccount> findByTypeAndEmail(AccountType type, String email);

    /** Lê a conta de staff com {@code select ... for update} (login: as falhas não se perdem). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from UserAccount a where a.type = mz.co.moseguros.identity.domain.AccountType.STAFF "
            + "and a.email = :email")
    Optional<UserAccount> findStaffByEmailForUpdate(@Param("email") String email);

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

    /**
     * Contas de staff activas da organização, excepto {@code excludedId}, com algum dos perfis
     * indicados. Uma conta {@code LOCKED} cujo bloqueio já passou conta como activa.
     */
    @Query("""
            select count(distinct a.id) from UserAccount a, UserRole r
             where r.userId = a.id and r.roleCode in :roleCodes and a.id <> :excludedId
               and a.type = mz.co.moseguros.identity.domain.AccountType.STAFF
               and a.organizationId = :organizationId
               and (a.status = mz.co.moseguros.identity.domain.AccountStatus.ACTIVE
                    or (a.status = mz.co.moseguros.identity.domain.AccountStatus.LOCKED and a.lockedUntil <= :now))""")
    long countActiveInOrganizationWithRoles(@Param("organizationId") UUID organizationId,
                                            @Param("roleCodes") Collection<String> roleCodes,
                                            @Param("excludedId") UUID excludedId,
                                            @Param("now") Instant now);

    /** Como {@link #countActiveInOrganizationWithRoles}, para o staff da plataforma. */
    @Query("""
            select count(distinct a.id) from UserAccount a, UserRole r
             where r.userId = a.id and r.roleCode in :roleCodes and a.id <> :excludedId
               and a.type = mz.co.moseguros.identity.domain.AccountType.STAFF
               and a.organizationId is null
               and (a.status = mz.co.moseguros.identity.domain.AccountStatus.ACTIVE
                    or (a.status = mz.co.moseguros.identity.domain.AccountStatus.LOCKED and a.lockedUntil <= :now))""")
    long countActiveInPlatformWithRoles(@Param("roleCodes") Collection<String> roleCodes,
                                        @Param("excludedId") UUID excludedId,
                                        @Param("now") Instant now);
}
