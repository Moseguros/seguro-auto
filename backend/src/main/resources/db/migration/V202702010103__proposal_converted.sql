-- Proposta convertida em apólice (S6-01; ProposalApi.markConverted). Corre como dono.
--
-- policy_id é a apólice emitida a partir da proposta (policy.policy, noutro schema: sem FK). paid_at e
-- payment_intent_id (S5-02) mantêm-se em CONVERTED (ck_proposal_paid já aceita os dois estados).

alter table proposal.proposal
  add column policy_id uuid null;

alter table proposal.proposal
  add constraint ck_proposal_converted check ((status = 'CONVERTED') = (policy_id is not null));
