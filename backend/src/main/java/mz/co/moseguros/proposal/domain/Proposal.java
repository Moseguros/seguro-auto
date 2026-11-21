package mz.co.moseguros.proposal.domain;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import mz.co.moseguros.catalog.ProductType;
import mz.co.moseguros.proposal.AcceptanceMode;
import mz.co.moseguros.proposal.ProposalStatus;
import mz.co.moseguros.shared.crypto.EncryptedStringConverter;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.persistence.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Proposta de seguro ({@code proposal.proposal}, S4-02 §3 e §4): os dados da cotação congelados
 * (coberturas, prémio, veículo e condutor), o tomador copiado do perfil e as respostas do cliente.
 * As transições de estado validam-se aqui ({@link #transitionTo}); o conteúdo só se altera em
 * {@code DRAFT} e {@code INFO_REQUESTED} ({@link #requireEditable}). Cada alteração de conteúdo
 * aumenta {@code content_revision} e renova o prazo de expiração ({@link #touchContent}). Não se apaga.
 *
 * <p>Assinatura (S4-04): o pedido em curso ({@code sign_request_*}, {@link #requestSignature}) e a
 * assinatura válida ({@code current_signature_id}, {@link #sign}). Qualquer alteração de conteúdo e o
 * pedido de informação da seguradora (S4-05) passam por {@link #contentChanged}, que invalida a
 * assinatura; o pedido em curso deixa de servir porque a revisão mudou.
 */
@Entity
@Table(schema = "proposal", name = "proposal")
public class Proposal extends BaseEntity {

    /** Fuso dos dias de início e de fim. */
    public static final ZoneId ZONE = ZoneId.of("Africa/Maputo");

    /** {@code decided_by} das decisões automáticas (S4-05 §6). */
    public static final String SYSTEM_DECIDER = "system";
    /** Limite de {@code decision_reason} e {@code info_request_message}. */
    public static final int DECISION_TEXT_MAX = 1000;

    private static final com.fasterxml.jackson.databind.ObjectMapper CANONICAL =
            new com.fasterxml.jackson.databind.ObjectMapper();

    @Column(name = "number", nullable = false, updatable = false, length = 16)
    private String number;

    @Column(name = "quote_id", nullable = false, updatable = false)
    private UUID quoteId;

    @Column(name = "insurer_id", nullable = false, updatable = false)
    private UUID insurerId;

    @Column(name = "insurer_name", nullable = false, updatable = false, length = 200)
    private String insurerName;

    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "product_version_id", nullable = false, updatable = false)
    private UUID productVersionId;

    @Column(name = "product_name", nullable = false, updatable = false, length = 120)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, updatable = false, length = 20)
    private ProductType productType;

    @Column(name = "customer_user_id", nullable = false, updatable = false)
    private UUID customerUserId;

    @Column(name = "created_by_user_id", nullable = false, updatable = false)
    private UUID createdByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, updatable = false, length = 10)
    private ProposalChannel channel;

    @Column(name = "broker_org_id", updatable = false)
    private UUID brokerOrgId;

    @Column(name = "broker_name", length = 200)
    private String brokerName;

    @Column(name = "vehicle_id", nullable = false, updatable = false)
    private UUID vehicleId;

    @Column(name = "driver_id", nullable = false, updatable = false)
    private UUID driverId;

    /** {@link PolicyholderSnapshot}, sem documento nem NUIT em claro. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "policyholder", nullable = false, columnDefinition = "jsonb")
    private JsonNode policyholder;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "policyholder_id_doc_number_enc", nullable = false)
    private String policyholderIdDocNumber;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "policyholder_nuit_enc", nullable = false)
    private String policyholderNuit;

    /** Origem do tomador (S4-06 §1): o perfil do cliente ou o mediador. */
    @Enumerated(EnumType.STRING)
    @Column(name = "policyholder_source", nullable = false, updatable = false, length = 10)
    private PolicyholderSource policyholderSource = PolicyholderSource.PROFILE;

    /** Nesta fase sempre {@code {"sameAsPolicyholder": true}}. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "insured", nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode insured;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "vehicle_snapshot", nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode vehicleSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "driver_snapshot", nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode driverSnapshot;

    /** Lista de {@link CoverageLine}. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "coverages", nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode coverages;

    /** {@link PremiumBreakdown}. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "premium_breakdown", nullable = false, updatable = false, columnDefinition = "jsonb")
    private JsonNode premiumBreakdown;

    @Column(name = "total_amount", nullable = false, updatable = false)
    private long totalAmount;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "duration_days", nullable = false, updatable = false)
    private int durationDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProposalStatus status;

    @Column(name = "content_revision", nullable = false)
    private int contentRevision;

    @Column(name = "inspection_required", nullable = false, updatable = false)
    private boolean inspectionRequired;

    @Column(name = "declarations_confirmed_at")
    private Instant declarationsConfirmedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "suitability_result", length = 10)
    private SuitabilityResult suitabilityResult;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "suitability_reasons", columnDefinition = "text[]")
    private List<String> suitabilityReasons;

    @Column(name = "suitability_answered_at")
    private Instant suitabilityAnsweredAt;

    @Column(name = "suitability_acknowledged_at")
    private Instant suitabilityAcknowledgedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    /** PDF do pedido de assinatura em curso (S4-04). */
    @Column(name = "sign_request_file_id")
    private UUID signRequestFileId;

    @Column(name = "sign_request_hash", length = 64)
    private String signRequestHash;

    /** Desafio OTP raiz do pedido em curso. */
    @Column(name = "sign_request_challenge_id")
    private UUID signRequestChallengeId;

    /** {@code content_revision} quando o pedido foi feito. */
    @Column(name = "sign_request_revision")
    private Integer signRequestRevision;

    /** Utilizador do mediador que pediu a assinatura (S4-06). */
    @Column(name = "sign_request_assisted_by_user_id")
    private UUID signRequestAssistedByUserId;

    /** Assinatura válida, se houver. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_signature_id")
    private Signature currentSignature;

    /** Última avaliação das regras de aceitação (S4-05 §3). */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "underwriting_result", columnDefinition = "jsonb")
    private JsonNode underwritingResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "acceptance_mode", length = 10)
    private AcceptanceMode acceptanceMode;

    @Column(name = "review_started_at")
    private Instant reviewStartedAt;

    @Column(name = "sla_alert_at")
    private Instant slaAlertAt;

    @Column(name = "sla_due_at")
    private Instant slaDueAt;

    @Column(name = "sla_alerted_at")
    private Instant slaAlertedAt;

    /** {@code sub} do subscritor ou {@value #SYSTEM_DECIDER}. */
    @Column(name = "decided_by", length = 64)
    private String decidedBy;

    @Column(name = "decided_at")
    private Instant decidedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "rejection_reason_code", length = 30)
    private RejectionReasonCode rejectionReasonCode;

    @Column(name = "decision_reason", length = 1000)
    private String decisionReason;

    @Column(name = "info_request_message", length = 1000)
    private String infoRequestMessage;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "info_request_items", columnDefinition = "text[]")
    private List<String> infoRequestItems;

    @Column(name = "info_requested_at")
    private Instant infoRequestedAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "payment_intent_id")
    private UUID paymentIntentId;

    @Column(name = "policy_id")
    private UUID policyId;

    protected Proposal() {
    }

    /**
     * Período coberto: de 00:00 de {@code startDate} a 00:00 do dia seguinte a {@code endDate}
     * (exclusivo), em {@code Africa/Maputo}.
     *
     * @param endDate último dia coberto (inclusivo)
     */
    public record CoverPeriod(LocalDate startDate, LocalDate endDate, Instant startAt, Instant endAt) {

        /**
         * Regra 4 do S3-02 §3: 365 dias → {@code startDate + 1 ano − 1 dia}; as outras durações →
         * {@code startDate + (dias − 1)}.
         */
        public static CoverPeriod of(LocalDate startDate, int durationDays) {
            Objects.requireNonNull(startDate, "startDate");
            LocalDate end = durationDays == 365 ? startDate.plusYears(1).minusDays(1)
                    : startDate.plusDays(durationDays - 1L);
            return of(startDate, end);
        }

        /** Com a data de fim já calculada (a da cotação). */
        public static CoverPeriod of(LocalDate startDate, LocalDate endDate) {
            return new CoverPeriod(startDate, endDate, startDate.atStartOfDay(ZONE).toInstant(),
                    endDate.plusDays(1).atStartOfDay(ZONE).toInstant());
        }
    }

    /**
     * Dados de uma proposta nova, já verificados e copiados da cotação, do perfil e dos snapshots.
     *
     * @param idDocNumber número do documento em claro (fica cifrado)
     * @param nuit        NUIT em claro (fica cifrado)
     */
    public record Draft(String number, UUID quoteId, UUID insurerId, String insurerName, UUID productId,
                        UUID productVersionId, String productName, ProductType productType, UUID customerUserId,
                        UUID createdByUserId, ProposalChannel channel, UUID brokerOrgId, String brokerName,
                        UUID vehicleId, UUID driverId, JsonNode policyholder, String idDocNumber, String nuit,
                        JsonNode insured, JsonNode vehicleSnapshot, JsonNode driverSnapshot,
                        List<CoverageLine> coverageLines, JsonNode coverages, JsonNode premiumBreakdown,
                        long totalAmount, CoverPeriod period, int durationDays) {
    }

    /** Proposta {@code DRAFT} com o tomador copiado do perfil ({@link PolicyholderSource#PROFILE}). */
    public static Proposal draft(Draft d, Instant now, Duration expiry) {
        return draft(d, PolicyholderSource.PROFILE, now, expiry);
    }

    /**
     * Proposta {@code DRAFT}, com {@code content_revision = 1} e a expiração em {@code now + expiry}. O
     * tomador preenchido pelo mediador ({@link PolicyholderSource#BROKER}) só existe no canal
     * {@code BROKER} (S4-06 §1).
     */
    public static Proposal draft(Draft d, PolicyholderSource policyholderSource, Instant now, Duration expiry) {
        if ((d.channel() == ProposalChannel.BROKER) != (d.brokerOrgId() != null)) {
            throw new IllegalArgumentException("brokerOrgId is required exactly with channel BROKER");
        }
        Objects.requireNonNull(policyholderSource, "policyholderSource");
        if (policyholderSource == PolicyholderSource.BROKER && d.channel() != ProposalChannel.BROKER) {
            throw new IllegalArgumentException("Only a broker proposal can have a policyholder filled by the broker");
        }
        if (d.totalAmount() <= 0) {
            throw new IllegalArgumentException("totalAmount must be positive");
        }
        Proposal p = new Proposal();
        p.number = Objects.requireNonNull(d.number(), "number");
        p.quoteId = Objects.requireNonNull(d.quoteId(), "quoteId");
        p.insurerId = Objects.requireNonNull(d.insurerId(), "insurerId");
        p.insurerName = Objects.requireNonNull(d.insurerName(), "insurerName");
        p.productId = Objects.requireNonNull(d.productId(), "productId");
        p.productVersionId = Objects.requireNonNull(d.productVersionId(), "productVersionId");
        p.productName = Objects.requireNonNull(d.productName(), "productName");
        p.productType = Objects.requireNonNull(d.productType(), "productType");
        p.customerUserId = Objects.requireNonNull(d.customerUserId(), "customerUserId");
        p.createdByUserId = Objects.requireNonNull(d.createdByUserId(), "createdByUserId");
        p.channel = d.channel();
        p.brokerOrgId = d.brokerOrgId();
        p.brokerName = d.brokerName();
        p.vehicleId = Objects.requireNonNull(d.vehicleId(), "vehicleId");
        p.driverId = Objects.requireNonNull(d.driverId(), "driverId");
        p.setPolicyholder(d.policyholder(), d.idDocNumber(), d.nuit());
        p.policyholderSource = policyholderSource;
        p.insured = canonical(Objects.requireNonNull(d.insured(), "insured"));
        p.vehicleSnapshot = canonical(Objects.requireNonNull(d.vehicleSnapshot(), "vehicleSnapshot"));
        p.driverSnapshot = canonical(Objects.requireNonNull(d.driverSnapshot(), "driverSnapshot"));
        p.coverages = canonical(Objects.requireNonNull(d.coverages(), "coverages"));
        p.premiumBreakdown = canonical(Objects.requireNonNull(d.premiumBreakdown(), "premiumBreakdown"));
        p.totalAmount = d.totalAmount();
        p.startAt = d.period().startAt();
        p.endAt = d.period().endAt();
        p.durationDays = d.durationDays();
        p.inspectionRequired = requiresInspection(d.coverageLines());
        p.status = ProposalStatus.DRAFT;
        p.contentRevision = 1;
        p.expiresAt = now.plus(expiry);
        return p;
    }

    /** {@code true} se alguma cobertura for de danos próprios ({@code OD_*}): vistoria (S4-03). */
    public static boolean requiresInspection(List<CoverageLine> coverages) {
        return coverages.stream().anyMatch(CoverageLine::isOwnDamage);
    }

    // --- Regras ---

    /** @throws DomainException 409 {@code PROPOSAL_NOT_EDITABLE} fora de {@code DRAFT}/{@code INFO_REQUESTED} */
    public void requireEditable() {
        if (!status.isEditable()) {
            throw DomainException.of(ErrorCodes.PROPOSAL_NOT_EDITABLE,
                    "The proposal can no longer be changed (status " + status + ")");
        }
    }

    /** @throws DomainException 409 {@code CONCURRENT_MODIFICATION} se a versão lida não for a gravada */
    public void requireVersion(long expected) {
        if (getVersion() != expected) {
            throw DomainException.of(ErrorCodes.CONCURRENT_MODIFICATION,
                    "The proposal was changed by another request (version " + getVersion() + ")");
        }
    }

    /**
     * Alteração de conteúdo (S4-02 §6): {@code content_revision + 1} e
     * {@code expires_at = now + expiry}. Só em estado editável.
     */
    public void touchContent(Instant now, Duration expiry) {
        requireEditable();
        contentChanged(Signature.InvalidationReason.CONTENT_CHANGED, now);
        contentRevision++;
        expiresAt = now.plus(expiry);
    }

    /**
     * A proposta mudou ({@code CONTENT_CHANGED}, chamado por {@link #touchContent}) ou a seguradora
     * pediu informação ({@code INFO_REQUESTED}, S4-05): a assinatura actual fica invalidada e deixa de
     * haver assinatura válida (S4-04 §7, "Invalidação"). Único ponto de invalidação; sem assinatura não
     * faz nada.
     *
     * <p>O pedido de assinatura em curso fica gravado com a revisão antiga: deixa de servir
     * ({@link #requireSignRequest} → 409 {@code PROPOSAL_CHANGED_SINCE_SIGN_REQUEST}, critério de
     * aceitação do S4-04) e o próximo pedido substitui-o. Limpá-lo aqui daria
     * {@code SIGN_REQUEST_NOT_FOUND} e o ecrã não saberia que a proposta mudou (decisão registada na ficha).
     */
    public void contentChanged(Signature.InvalidationReason reason, Instant now) {
        Objects.requireNonNull(reason, "reason");
        if (currentSignature != null) {
            currentSignature.invalidate(reason, now);
            currentSignature = null;
        }
    }

    /**
     * Novo pedido de assinatura da revisão actual (S4-04 §7): substitui o anterior; não altera
     * {@code content_revision}.
     *
     * @param assistedByUserId utilizador do mediador (S4-06) ou {@code null}
     */
    public void requestSignature(UUID documentFileId, String documentHash, UUID challengeId, UUID assistedByUserId) {
        requireEditable();
        this.signRequestFileId = Objects.requireNonNull(documentFileId, "documentFileId");
        this.signRequestHash = Objects.requireNonNull(documentHash, "documentHash");
        this.signRequestChallengeId = Objects.requireNonNull(challengeId, "challengeId");
        this.signRequestRevision = contentRevision;
        this.signRequestAssistedByUserId = assistedByUserId;
    }

    /**
     * O pedido de assinatura em curso é o do desafio indicado e a proposta não mudou desde então.
     *
     * @throws DomainException 409 {@code SIGN_REQUEST_NOT_FOUND} sem pedido ou com outro desafio; 409
     *                         {@code PROPOSAL_CHANGED_SINCE_SIGN_REQUEST} se a revisão mudou
     */
    public void requireSignRequest(UUID challengeId) {
        if (signRequestChallengeId == null || !signRequestChallengeId.equals(challengeId)) {
            throw DomainException.of(ErrorCodes.SIGN_REQUEST_NOT_FOUND, "There is no signature request for this code");
        }
        if (signRequestRevision == null || signRequestRevision != contentRevision) {
            throw DomainException.of(ErrorCodes.PROPOSAL_CHANGED_SINCE_SIGN_REQUEST,
                    "The proposal changed after the code was requested");
        }
    }

    /**
     * Assinatura confirmada da revisão actual: passa a ser a assinatura válida, o pedido fica
     * concluído e o prazo de expiração é renovado ({@code now + expiry}, S4-02).
     */
    public void sign(Signature signature, Instant now, Duration expiry) {
        requireEditable();
        Objects.requireNonNull(signature, "signature");
        if (!getId().equals(signature.getProposalId()) || signature.getContentRevision() != contentRevision) {
            throw new IllegalArgumentException("The signature is not of the current revision of this proposal");
        }
        this.currentSignature = signature;
        clearSignRequest();
        this.expiresAt = now.plus(expiry);
    }

    private void clearSignRequest() {
        this.signRequestFileId = null;
        this.signRequestHash = null;
        this.signRequestChallengeId = null;
        this.signRequestRevision = null;
        this.signRequestAssistedByUserId = null;
    }

    /** Nova data de início, com o tomador copiado de novo do perfil (S4-02 §9, {@code updateProposal}). */
    public void reschedule(CoverPeriod period, JsonNode policyholderJson, String idDocNumber, String nuit,
                           Instant now, Duration expiry) {
        requireEditable();
        this.startAt = period.startAt();
        this.endAt = period.endAt();
        setPolicyholder(policyholderJson, idDocNumber, nuit);
        touchContent(now, expiry);
    }

    /** Declarações gravadas e confirmadas pelo cliente. */
    public void confirmDeclarations(Instant now, Duration expiry) {
        touchContent(now, expiry);
        this.declarationsConfirmedAt = now;
    }

    /** Resultado do questionário de adequação; limpa uma confirmação anterior. */
    public void recordSuitability(SuitabilityEvaluator.Evaluation evaluation, Instant now, Duration expiry) {
        touchContent(now, expiry);
        this.suitabilityResult = evaluation.result();
        this.suitabilityReasons = new ArrayList<>(evaluation.reasons().stream().map(Enum::name).toList());
        this.suitabilityAnsweredAt = now;
        this.suitabilityAcknowledgedAt = null;
    }

    /** @throws DomainException 409 {@code SUITABILITY_NOT_WARNED} se o resultado não for {@code WARNED} */
    public void acknowledgeSuitability(Instant now, Duration expiry) {
        requireEditable();
        if (suitabilityResult != SuitabilityResult.WARNED) {
            throw DomainException.of(ErrorCodes.SUITABILITY_NOT_WARNED, "There are no suitability warnings");
        }
        touchContent(now, expiry);
        this.suitabilityAcknowledgedAt = now;
    }

    /**
     * Muda de estado segundo a tabela do S4-02 §3 e acerta o prazo: {@code expires_at = now + expiry}
     * ao entrar em {@code INFO_REQUESTED} ou {@code ACCEPTED}; {@code null} em {@code UNDER_REVIEW} e
     * nos estados finais.
     *
     * @throws DomainException 409 {@code INVALID_STATE_TRANSITION}
     */
    public void transitionTo(ProposalStatus target, Instant now, Duration expiry) {
        if (!status.canTransitionTo(target)) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                    "Cannot change a proposal from " + status + " to " + target);
        }
        this.status = target;
        this.expiresAt = target.expires() ? now.plus(expiry) : null;
    }

    // --- Submissão e decisão (S4-05) ---

    /**
     * Submissão (S4-05 §6, passos 1 e 4): só de {@code DRAFT} ou {@code INFO_REQUESTED};
     * {@code submitted_at} guarda a primeira submissão; o pedido de informação anterior é limpo. O
     * estado muda a seguir, com o resultado das regras ({@link #acceptAutomatically} ou
     * {@link #startReview}).
     *
     * @throws DomainException 409 {@code INVALID_STATE_TRANSITION}
     */
    public void submit(Instant now) {
        requireSubmittable();
        if (submittedAt == null) {
            submittedAt = now;
        }
        infoRequestMessage = null;
        infoRequestItems = null;
        infoRequestedAt = null;
    }

    /** @throws DomainException 409 {@code INVALID_STATE_TRANSITION} fora de {@code DRAFT}/{@code INFO_REQUESTED} */
    public void requireSubmittable() {
        if (!status.isEditable()) {
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                    "Only a DRAFT or INFO_REQUESTED proposal can be submitted (status " + status + ")");
        }
    }

    /** @throws DomainException 409 {@code PROPOSAL_NOT_UNDER_REVIEW} fora de {@code UNDER_REVIEW} */
    public void requireUnderReview() {
        if (status != ProposalStatus.UNDER_REVIEW) {
            throw DomainException.of(ErrorCodes.PROPOSAL_NOT_UNDER_REVIEW,
                    "The proposal is not under review (status " + status + ")");
        }
    }

    /** Grava o registo da última avaliação das regras de aceitação. */
    public void recordUnderwriting(JsonNode result) {
        this.underwritingResult = canonical(Objects.requireNonNull(result, "result"));
    }

    /**
     * Aceitação automática (S4-05 §6, passo 5): {@code ACCEPTED} com {@code acceptance_mode = AUTOMATIC},
     * {@code decided_by = system}, o prazo para pagar ({@code now + expiry}) e o SLA limpo. Vale na
     * submissão e na reavaliação ao aprovar a vistoria (§8).
     */
    public void acceptAutomatically(Instant now, Duration expiry) {
        transitionTo(ProposalStatus.ACCEPTED, now, expiry);
        this.acceptanceMode = AcceptanceMode.AUTOMATIC;
        decided(SYSTEM_DECIDER, now);
        this.rejectionReasonCode = null;
        this.decisionReason = null;
        clearSla();
    }

    /**
     * Entrada em análise (S4-05 §6, passo 5): {@code UNDER_REVIEW} sem prazo de expiração; o SLA
     * (re)começa agora, em dias de calendário contados em instantes.
     */
    public void startReview(Instant now, Duration alertAfter, Duration dueAfter) {
        transitionTo(ProposalStatus.UNDER_REVIEW, now, Duration.ZERO);
        this.reviewStartedAt = now;
        this.slaAlertAt = now.plus(alertAfter);
        this.slaDueAt = now.plus(dueAfter);
        this.slaAlertedAt = null;
        this.acceptanceMode = null;
        this.decidedBy = null;
        this.decidedAt = null;
        this.rejectionReasonCode = null;
        this.decisionReason = null;
    }

    /** Aceitação pelo subscritor (S4-05 §7): {@code note} opcional; prazo para pagar; SLA limpo. */
    public void acceptManually(String decider, String note, Instant now, Duration expiry) {
        requireUnderReview();
        transitionTo(ProposalStatus.ACCEPTED, now, expiry);
        this.acceptanceMode = AcceptanceMode.MANUAL;
        decided(decider, now);
        this.rejectionReasonCode = null;
        this.decisionReason = note;
        clearSla();
    }

    /** Recusa pelo subscritor (S4-05 §7): {@code REJECTED} (final, sem prazo) com o motivo; SLA limpo. */
    public void reject(String decider, RejectionReasonCode code, String reason, Instant now) {
        requireUnderReview();
        transitionTo(ProposalStatus.REJECTED, now, Duration.ZERO);
        this.acceptanceMode = null;
        decided(decider, now);
        this.rejectionReasonCode = Objects.requireNonNull(code, "code");
        this.decisionReason = Objects.requireNonNull(reason, "reason");
        clearSla();
    }

    /**
     * Pedido de informação (S4-05 §7): {@code INFO_REQUESTED} com o prazo do cliente
     * ({@code now + expiry}), a mensagem e os itens; SLA limpo. A assinatura deixa de valer
     * ({@link #contentChanged} com {@code INFO_REQUESTED}): o cliente assina de novo depois de corrigir.
     */
    public void requestInfo(String message, List<InfoRequestItem> items, Instant now, Duration expiry) {
        requireUnderReview();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("At least one item is required");
        }
        transitionTo(ProposalStatus.INFO_REQUESTED, now, expiry);
        this.infoRequestMessage = Objects.requireNonNull(message, "message");
        this.infoRequestItems = new ArrayList<>(items.stream().map(Enum::name).toList());
        this.infoRequestedAt = now;
        contentChanged(Signature.InvalidationReason.INFO_REQUESTED, now);
        clearSla();
    }

    /** O alerta de SLA foi enviado ({@code ProposalSlaJob}); não muda o estado. */
    public void markSlaAlerted(Instant now) {
        this.slaAlertedAt = Objects.requireNonNull(now, "now");
    }

    private void decided(String decider, Instant now) {
        this.decidedBy = Objects.requireNonNull(decider, "decider");
        this.decidedAt = now;
    }

    /** Fim da análise: deixa de haver prazo da seguradora ({@code review_started_at} fica como histórico). */
    private void clearSla() {
        this.slaAlertAt = null;
        this.slaDueAt = null;
        this.slaAlertedAt = null;
    }

    /** {@code ProposalExpiryJob}: passa a {@code EXPIRED} e limpa o prazo. */
    public ProposalStatus expire(Instant now) {
        ProposalStatus from = status;
        transitionTo(ProposalStatus.EXPIRED, now, Duration.ZERO);
        return from;
    }

    /**
     * Pagamento confirmado ({@code PaymentSucceeded}, S5-02 §9): {@code ACCEPTED → PAID} com a data e a
     * intenção; uma proposta paga deixa de expirar ({@code expires_at = null}).
     *
     * @throws DomainException 409 {@code INVALID_STATE_TRANSITION} fora de {@code ACCEPTED}
     */
    public void markPaid(UUID intentId, Instant paidAt) {
        Objects.requireNonNull(intentId, "intentId");
        Objects.requireNonNull(paidAt, "paidAt");
        transitionTo(ProposalStatus.PAID, paidAt, Duration.ZERO);
        this.paidAt = paidAt;
        this.paymentIntentId = intentId;
    }

    /**
     * Apólice emitida (S6-01, {@code ProposalApi.markConverted}): {@code PAID → CONVERTED} com a apólice;
     * {@code paid_at} e {@code payment_intent_id} mantêm-se. Já {@code CONVERTED} com a mesma apólice →
     * nada (devolve {@code false}).
     *
     * @return {@code true} se mudou de estado
     * @throws DomainException 409 {@code INVALID_STATE_TRANSITION} fora de {@code PAID}, ou já
     *                         {@code CONVERTED} com outra apólice
     */
    public boolean markConverted(UUID policyId, Instant now) {
        Objects.requireNonNull(policyId, "policyId");
        if (status == ProposalStatus.CONVERTED) {
            if (policyId.equals(this.policyId)) {
                return false;
            }
            throw DomainException.of(ErrorCodes.INVALID_STATE_TRANSITION,
                    "The proposal was already converted into another policy");
        }
        transitionTo(ProposalStatus.CONVERTED, now, Duration.ZERO);
        this.policyId = policyId;
        return true;
    }

    private void setPolicyholder(JsonNode json, String idDocNumber, String nuit) {
        this.policyholder = canonical(Objects.requireNonNull(json, "policyholder"));
        this.policyholderIdDocNumber = Objects.requireNonNull(idDocNumber, "idDocNumber");
        this.policyholderNuit = Objects.requireNonNull(nuit, "nuit");
    }

    /**
     * O JSON como o Hibernate o lê da coluna (texto analisado de novo): sem isto, um {@code LongNode}
     * em memória e o {@code IntNode} lido da BD seriam diferentes e cada {@code flush} gravaria a
     * linha outra vez (e aumentaria a {@code version}).
     */
    static JsonNode canonical(JsonNode node) {
        try {
            return CANONICAL.readTree(CANONICAL.writeValueAsString(node));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid proposal JSON", e);
        }
    }

    // --- Leitura ---

    /** Último dia coberto (inclusivo), em {@code Africa/Maputo}. */
    public LocalDate endDate() {
        return LocalDate.ofInstant(endAt, ZONE).minusDays(1);
    }

    /** Primeiro dia coberto, em {@code Africa/Maputo}. */
    public LocalDate startDate() {
        return LocalDate.ofInstant(startAt, ZONE);
    }

    public String getNumber() {
        return number;
    }

    public UUID getQuoteId() {
        return quoteId;
    }

    public UUID getInsurerId() {
        return insurerId;
    }

    public String getInsurerName() {
        return insurerName;
    }

    public UUID getProductId() {
        return productId;
    }

    public UUID getProductVersionId() {
        return productVersionId;
    }

    public String getProductName() {
        return productName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public UUID getCustomerUserId() {
        return customerUserId;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public ProposalChannel getChannel() {
        return channel;
    }

    public UUID getBrokerOrgId() {
        return brokerOrgId;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public JsonNode getPolicyholder() {
        return policyholder.deepCopy();
    }

    /** Em claro: só para o PDF e a seguradora (S4-04, S4-05); nunca para um log nem para a API do cliente. */
    public String getPolicyholderIdDocNumber() {
        return policyholderIdDocNumber;
    }

    /** Em claro: mesmas regras de {@link #getPolicyholderIdDocNumber}. */
    public String getPolicyholderNuit() {
        return policyholderNuit;
    }

    public PolicyholderSource getPolicyholderSource() {
        return policyholderSource;
    }

    public JsonNode getInsured() {
        return insured.deepCopy();
    }

    public JsonNode getVehicleSnapshot() {
        return vehicleSnapshot.deepCopy();
    }

    public JsonNode getDriverSnapshot() {
        return driverSnapshot.deepCopy();
    }

    public JsonNode getCoverages() {
        return coverages.deepCopy();
    }

    public JsonNode getPremiumBreakdown() {
        return premiumBreakdown.deepCopy();
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public ProposalStatus getStatus() {
        return status;
    }

    public int getContentRevision() {
        return contentRevision;
    }

    public boolean isInspectionRequired() {
        return inspectionRequired;
    }

    public Instant getDeclarationsConfirmedAt() {
        return declarationsConfirmedAt;
    }

    public SuitabilityResult getSuitabilityResult() {
        return suitabilityResult;
    }

    /** Códigos {@link SuitabilityReason}, pela ordem do S4-02 §8; vazio antes do questionário. */
    public List<SuitabilityReason> getSuitabilityReasons() {
        return suitabilityReasons == null ? List.of()
                : suitabilityReasons.stream().map(SuitabilityReason::valueOf).toList();
    }

    public Instant getSuitabilityAnsweredAt() {
        return suitabilityAnsweredAt;
    }

    public Instant getSuitabilityAcknowledgedAt() {
        return suitabilityAcknowledgedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public UUID getSignRequestFileId() {
        return signRequestFileId;
    }

    public String getSignRequestHash() {
        return signRequestHash;
    }

    public UUID getSignRequestChallengeId() {
        return signRequestChallengeId;
    }

    public Integer getSignRequestRevision() {
        return signRequestRevision;
    }

    public UUID getSignRequestAssistedByUserId() {
        return signRequestAssistedByUserId;
    }

    /** Assinatura válida, ou {@code null}. */
    public Signature getCurrentSignature() {
        return currentSignature;
    }

    /** {@code underwriting_result}, ou {@code null} antes da primeira submissão. */
    public JsonNode getUnderwritingResult() {
        return underwritingResult == null ? null : underwritingResult.deepCopy();
    }

    public AcceptanceMode getAcceptanceMode() {
        return acceptanceMode;
    }

    public Instant getReviewStartedAt() {
        return reviewStartedAt;
    }

    public Instant getSlaAlertAt() {
        return slaAlertAt;
    }

    public Instant getSlaDueAt() {
        return slaDueAt;
    }

    public Instant getSlaAlertedAt() {
        return slaAlertedAt;
    }

    /** {@code sub} de quem decidiu; nunca sai para o cliente nem para o mediador. */
    public String getDecidedBy() {
        return decidedBy;
    }

    public Instant getDecidedAt() {
        return decidedAt;
    }

    public RejectionReasonCode getRejectionReasonCode() {
        return rejectionReasonCode;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public String getInfoRequestMessage() {
        return infoRequestMessage;
    }

    /** Itens do pedido de informação, pela ordem pedida; vazio sem pedido. */
    public List<InfoRequestItem> getInfoRequestItems() {
        return infoRequestItems == null ? List.of()
                : infoRequestItems.stream().map(InfoRequestItem::valueOf).toList();
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public UUID getPaymentIntentId() {
        return paymentIntentId;
    }

    /** Apólice emitida a partir da proposta; só em {@code CONVERTED} (S6-01). */
    public UUID getPolicyId() {
        return policyId;
    }

    public Instant getInfoRequestedAt() {
        return infoRequestedAt;
    }
}
