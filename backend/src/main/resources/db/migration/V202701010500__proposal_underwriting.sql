-- Submissão e aceitação da proposta (S4-05 §4; modelo transversal §2): o registo da última avaliação
-- das regras de aceitação, a decisão (automática ou do subscritor), o pedido de informação e o prazo
-- de decisão da seguradora (SLA). Corre como dono; o moseguros_app já lê e altera a tabela.

alter table proposal.proposal
  add column underwriting_result    jsonb null,                       -- última avaliação (S4-05 §3)
  add column acceptance_mode        varchar(10) null check (acceptance_mode in ('AUTOMATIC','MANUAL')),
  add column review_started_at      timestamptz null,                 -- entrada em UNDER_REVIEW
  add column sla_alert_at           timestamptz null,                 -- review_started_at + decision-alert-days
  add column sla_due_at             timestamptz null,                 -- review_started_at + decision-sla-days
  add column sla_alerted_at         timestamptz null,                 -- alerta enviado (ProposalSlaJob)
  add column decided_by             varchar(64) null,                 -- sub do subscritor ou 'system'
  add column decided_at             timestamptz null,
  add column rejection_reason_code  varchar(30) null check (rejection_reason_code in
    ('RISK_NOT_ACCEPTED','KYC_FAILED','INSPECTION_FAILED','FALSE_DECLARATION','DOCUMENTS_INVALID','OTHER')),
  add column decision_reason        varchar(1000) null,               -- motivo da recusa ou nota da aceitação
  add column info_request_message   varchar(1000) null,
  add column info_request_items     text[] null,                      -- valores de InfoRequestItem
  add column info_requested_at      timestamptz null;

-- Fila do alerta de SLA (ProposalSlaJob).
create index ix_proposal_sla on proposal.proposal (status, sla_alert_at) where status = 'UNDER_REVIEW';
