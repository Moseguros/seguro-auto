package mz.co.moseguros.policy.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import mz.co.moseguros.shared.persistence.BaseEntity;

/** Prestação de uma apólice ({@code policy.instalment}, S6-01 §3 e regra 2). */
@Entity
@Table(schema = "policy", name = "instalment")
public class Instalment extends BaseEntity {

    @Column(name = "policy_id", nullable = false, updatable = false)
    private UUID policyId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "policyholder_user_id", nullable = false, updatable = false)
    private UUID policyholderUserId;

    @Column(name = "seq", nullable = false, updatable = false)
    private int seq;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private InstalmentStatus status;

    @Column(name = "payment_intent_id")
    private UUID paymentIntentId;

    @Column(name = "paid_at")
    private Instant paidAt;

    protected Instalment() {
    }

    /**
     * Prestações da emissão: a 1.ª fica {@code PAID} com o pagamento que levou à emissão; as outras
     * {@code DUE}.
     */
    public static Instalment of(Policy policy, InstalmentSchedule.Line line, UUID paymentIntentId, Instant paidAt) {
        Objects.requireNonNull(policy, "policy");
        Instalment i = new Instalment();
        i.policyId = policy.getId();
        i.insurerId = policy.getInsurerId();
        i.policyholderUserId = policy.getPolicyholderUserId();
        i.seq = line.seq();
        i.dueDate = line.dueDate();
        i.amount = line.amount();
        if (line.seq() == 1) {
            i.status = InstalmentStatus.PAID;
            i.paymentIntentId = Objects.requireNonNull(paymentIntentId, "paymentIntentId");
            i.paidAt = Objects.requireNonNull(paidAt, "paidAt");
        } else {
            i.status = InstalmentStatus.DUE;
        }
        return i;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public UUID getPolicyholderUserId() {
        return policyholderUserId;
    }

    public int getSeq() {
        return seq;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public long getAmount() {
        return amount;
    }

    public InstalmentStatus getStatus() {
        return status;
    }

    public UUID getPaymentIntentId() {
        return paymentIntentId;
    }

    public Instant getPaidAt() {
        return paidAt;
    }
}
