package mz.co.moseguros.verification.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** {@code verification.check_log} (S6-03 §1): só se insere e lê (RLS: um agente só vê as suas). */
public interface CheckLogRepository extends JpaRepository<CheckLog, UUID> {

    /** Consultas do agente com {@code from <= checked_at < to}. */
    Page<CheckLog> findByCheckerUserIdAndCheckedAtGreaterThanEqualAndCheckedAtLessThan(UUID checkerUserId,
                                                                                        Instant from, Instant to,
                                                                                        Pageable pageable);
}
