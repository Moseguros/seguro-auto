-- Proposta paga (S5-02 §9; decisão D4). Corre como dono.
--
-- paid_at e payment_intent_id ficam preenchidos quando o pagamento é confirmado (PaymentSucceeded) e
-- mantêm-se em CONVERTED (S6-01). ck_proposal_status é recriada com o mesmo nome e os 8 estados do
-- S4-02 (sem SUBMITTED), para a BD aceitar PAID qualquer que seja a versão aplicada, sem editar a
-- migração do S4-02 (AGENTS.md §5).

alter table proposal.proposal
  add column paid_at timestamptz null,
  add column payment_intent_id uuid null;            -- payment.payment_intent, noutro schema: sem FK

alter table proposal.proposal
  drop constraint ck_proposal_status,
  add constraint ck_proposal_status check (status in ('DRAFT','UNDER_REVIEW','INFO_REQUESTED','ACCEPTED','REJECTED',
                                                      'EXPIRED','PAID','CONVERTED'));

alter table proposal.proposal
  add constraint ck_proposal_paid
  check ((status in ('PAID','CONVERTED')) = (paid_at is not null and payment_intent_id is not null));
