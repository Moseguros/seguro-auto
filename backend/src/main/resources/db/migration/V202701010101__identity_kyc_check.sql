-- Verificações de identidade do cliente (KYC; S4-01 §2). Corre como dono. O papel moseguros_app
-- tem os privilégios por defeito do schema identity (select, insert, update; V202611020900):
-- sem delete. Os ficheiros (frente, verso, selfie) ficam no document.file (dono KYC), sem FK.

create table identity.kyc_check (
  id                     uuid primary key,
  created_at             timestamptz not null,
  created_by             varchar(64) not null,
  updated_at             timestamptz not null,
  updated_by             varchar(64) not null,
  version                bigint not null default 0,
  user_id                uuid not null references identity.user_account (id),  -- conta CUSTOMER
  -- seguradora a quem a verificação é enviada (organization), noutro schema: sem FK
  insurer_id             uuid not null,
  status                 varchar(10) not null check (status in ('PENDING','VERIFIED','REJECTED')),
  -- cópia do perfil no momento do envio
  full_name              varchar(120) not null,
  birth_date             date not null,
  id_doc_type            varchar(10) not null check (id_doc_type in ('BI','PASSPORT','DIRE')),
  id_doc_number_enc      bytea not null,       -- AES-GCM (CryptoService)
  profile_fingerprint    varchar(64) not null, -- SHA-256 (ProfileFingerprint); nunca sai na API
  doc_front_file_id      uuid not null,        -- document.file, noutro schema: sem FK
  doc_back_file_id       uuid null,            -- obrigatório para BI e DIRE (validado no serviço)
  selfie_file_id         uuid not null,
  submitted_at           timestamptz not null,
  reviewed_by            uuid null,            -- user_account.id do subscritor
  reviewed_at            timestamptz null,
  rejection_reason_code  varchar(30) null
                         check (rejection_reason_code in ('DOCUMENT_UNREADABLE','DOCUMENT_EXPIRED','DATA_MISMATCH',
                                                          'SELFIE_MISMATCH','OTHER')),
  rejection_reason       varchar(500) null,
  check ((status = 'REJECTED') = (rejection_reason_code is not null)),
  check (status = 'PENDING' or (reviewed_by is not null and reviewed_at is not null))
);

create unique index ux_kyc_pending_per_user on identity.kyc_check (user_id) where status = 'PENDING';
create index ix_kyc_user on identity.kyc_check (user_id, submitted_at desc);
create index ix_kyc_insurer_status on identity.kyc_check (insurer_id, status, submitted_at);

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda).
-- s = current_setting('app.scope', true)
-- u = nullif(current_setting('app.user_id', true), '')::uuid
-- i = nullif(current_setting('app.insurer_id', true), '')::uuid
alter table identity.kyc_check enable row level security;
create policy kyc_check_scope on identity.kyc_check
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid));
