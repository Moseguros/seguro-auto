package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Convites visíveis no âmbito da transacção (Row Level Security).
 */
public interface InvitationRepository extends JpaRepository<Invitation, UUID>, JpaSpecificationExecutor<Invitation> {

    Optional<Invitation> findByTokenHash(String tokenHash);

    /** Lê o convite com {@code select ... for update} (activação: as tentativas não se perdem). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Invitation i where i.tokenHash = :tokenHash")
    Optional<Invitation> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    /** Convites por aceitar e não revogados para o e-mail (podem estar expirados). */
    List<Invitation> findByEmailAndRevokedAtIsNullAndAcceptedAtIsNull(String email);

    /** Há um convite da plataforma pendente (não revogado, não aceite, não expirado) para o e-mail. */
    @Query("""
            select count(i) > 0 from Invitation i
             where i.email = :email and i.organizationId is null
               and i.revokedAt is null and i.acceptedAt is null and i.expiresAt > :now""")
    boolean existsPendingPlatformInvitation(@Param("email") String email, @Param("now") Instant now);
}
