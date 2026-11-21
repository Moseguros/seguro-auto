package mz.co.moseguros.policy.domain;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.policy.PolicyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Apólices visíveis no âmbito da transacção (Row Level Security). */
public interface PolicyRepository extends JpaRepository<Policy, UUID>, JpaSpecificationExecutor<Policy> {

    Optional<Policy> findByProposalId(UUID proposalId);

    Page<Policy> findByPolicyholderUserId(UUID policyholderUserId, Pageable pageable);

    Page<Policy> findByPolicyholderUserIdAndStatusIn(UUID policyholderUserId, Collection<PolicyStatus> statuses,
                                                     Pageable pageable);

    List<Policy> findByPolicyholderUserIdOrderByIssuedAtDesc(UUID policyholderUserId);

    /** {@code ACTIVE} com {@code start_at <= at < end_at} para a matrícula; a de {@code end_at} maior primeiro. */
    @Query("""
            select p from Policy p
             where p.plate = :plate and p.status = mz.co.moseguros.policy.PolicyStatus.ACTIVE
               and p.startAt <= :at and p.endAt > :at
             order by p.endAt desc, p.id desc""")
    List<Policy> findCovering(@Param("plate") String plate, @Param("at") Instant at, Pageable pageable);

    /** {@code ACTIVE} que ainda não começou ({@code start_at > at}); a que começa primeiro primeiro. */
    @Query("""
            select p from Policy p
             where p.plate = :plate and p.status = mz.co.moseguros.policy.PolicyStatus.ACTIVE
               and p.startAt > :at
             order by p.startAt asc, p.id asc""")
    List<Policy> findUpcoming(@Param("plate") String plate, @Param("at") Instant at, Pageable pageable);

    /** Qualquer estado; a de {@code end_at} maior primeiro. */
    @Query("select p from Policy p where p.plate = :plate order by p.endAt desc, p.id desc")
    List<Policy> findLatest(@Param("plate") String plate, Pageable pageable);

    /**
     * Apólices com certificado {@code VALID} sem PDF ou sem as condições particulares, emitidas antes de
     * {@code issuedBefore}, as mais antigas primeiro (S6-02 §1, {@code PolicyDocumentsJob}).
     */
    @Query("""
            select p.id from Policy p, Certificate c
             where c.policyId = p.id and c.status = mz.co.moseguros.policy.CertificateStatus.VALID
               and (c.pdfFileId is null or p.scheduleFileId is null)
               and p.issuedAt < :issuedBefore
             order by p.issuedAt asc, p.id asc""")
    List<UUID> findMissingDocuments(@Param("issuedBefore") Instant issuedBefore, Pageable pageable);

    /** Existe apólice do veículo num dos estados indicados ainda em vigor ({@code end_at > now}). */
    @Query("""
            select count(p) > 0 from Policy p
             where p.vehicleId = :vehicleId and p.status in :statuses and p.endAt > :now""")
    boolean existsInForce(@Param("vehicleId") UUID vehicleId, @Param("statuses") Collection<PolicyStatus> statuses,
                          @Param("now") Instant now);
}
