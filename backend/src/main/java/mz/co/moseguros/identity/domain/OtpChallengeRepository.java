package mz.co.moseguros.identity.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import mz.co.moseguros.identity.domain.OtpChallenge.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Desafios OTP (só âmbito SYSTEM).
 */
public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {

    List<OtpChallenge> findByIdentifierOrderByCreatedAtDesc(String identifier);

    List<OtpChallenge> findByRootChallengeId(UUID rootChallengeId);

    /** Id do desafio raiz da cadeia do desafio indicado (sem carregar a entidade). */
    @Query("select c.rootChallengeId from OtpChallenge c where c.id = :id")
    Optional<UUID> findRootId(@Param("id") UUID id);

    /** A cadeia inteira (desafio raiz e alternativas), bloqueada para escrita. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from OtpChallenge c where c.rootChallengeId = :rootId")
    List<OtpChallenge> findChainForUpdate(@Param("rootId") UUID rootId);

    /** Pedidos (desafios raiz) do identificador desde {@code since}, para o limite por hora. */
    @Query("""
            select c.createdAt from OtpChallenge c
             where c.identifier = :identifier and c.fallbackFromChallengeId is null
               and c.purpose in :purposes and c.createdAt > :since
             order by c.createdAt asc""")
    List<Instant> findRequestTimesSince(@Param("identifier") String identifier,
                                        @Param("purposes") Collection<Purpose> purposes,
                                        @Param("since") Instant since);

    /** Momento do último pedido (desafio raiz) do identificador, ou {@code null}. */
    @Query("""
            select max(c.createdAt) from OtpChallenge c
             where c.identifier = :identifier and c.fallbackFromChallengeId is null
               and c.purpose in :purposes""")
    Instant findLastRequestTime(@Param("identifier") String identifier,
                                @Param("purposes") Collection<Purpose> purposes);

    /** Desafios ainda válidos do identificador e finalidade (para os marcar como substituídos). */
    List<OtpChallenge> findByIdentifierAndPurposeAndConsumedAtIsNullAndSupersededAtIsNull(String identifier,
                                                                                          Purpose purpose);
}
