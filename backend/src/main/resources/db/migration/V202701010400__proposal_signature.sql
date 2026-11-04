-- Assinatura electrónica simples da proposta por OTP sobre o hash do PDF (S4-04 §5).
-- Corre como dono. O papel moseguros_app lê, insere e altera (privilégios por defeito do schema,
-- V202701010201); as assinaturas não se apagam: uma alteração posterior marca-as invalidadas.

create table proposal.signature (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  insurer_id           uuid not null,                                -- igual ao da proposta
  proposal_id          uuid not null references proposal.proposal (id),
  content_revision     int not null check (content_revision >= 1),  -- revisão assinada
  signer_user_id       uuid not null,                                -- sempre o cliente dono da proposta
  method               varchar(10) not null check (method in ('OTP')),
  document_file_id     uuid not null,                                -- PDF assinado (document.file, sem FK)
  document_hash        varchar(64) not null check (document_hash ~ '^[0-9a-f]{64}$'),
  otp_challenge_id     uuid not null,                                -- identity.otp_challenge, sem FK
  otp_channel          varchar(10) not null check (otp_channel in ('SMS','WHATSAPP','EMAIL')),
  destination_masked   varchar(254) not null,
  assisted_by_user_id  uuid null,                                    -- utilizador do mediador (S4-06)
  assisted_by_org_id   uuid null,
  signed_at            timestamptz not null,
  ip                   varchar(45) null,                             -- do pedido de confirmação
  user_agent           varchar(200) null,                            -- truncado
  invalidated_at       timestamptz null,
  invalidated_reason   varchar(20) null check (invalidated_reason in ('CONTENT_CHANGED','INFO_REQUESTED')),
  check ((invalidated_at is null) = (invalidated_reason is null))
);

create index ix_signature_proposal on proposal.signature (proposal_id, signed_at desc);

alter table proposal.proposal
  add column sign_request_file_id              uuid null,         -- PDF do pedido em curso
  add column sign_request_hash                 varchar(64) null check (sign_request_hash ~ '^[0-9a-f]{64}$'),
  add column sign_request_challenge_id         uuid null,
  add column sign_request_revision             int null,
  add column sign_request_assisted_by_user_id  uuid null,         -- S4-06
  add column current_signature_id              uuid null references proposal.signature (id),
  add constraint ck_proposal_sign_request check (
    (sign_request_file_id is null) = (sign_request_hash is null)
    and (sign_request_file_id is null) = (sign_request_challenge_id is null)
    and (sign_request_file_id is null) = (sign_request_revision is null));

-- As assinaturas são visíveis exactamente quando a proposta o é (a subconsulta fica sujeita ao
-- RLS de proposal.proposal).
alter table proposal.signature enable row level security;
create policy signature_scope on proposal.signature
  for all to moseguros_app
  using (exists (select 1 from proposal.proposal p where p.id = proposal_id))
  with check (exists (select 1 from proposal.proposal p where p.id = proposal_id));
