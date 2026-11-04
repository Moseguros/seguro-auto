-- Propostas, declarações de risco e questionário de adequação (S4-02 §4; modelo transversal §2).
-- Corre como dono. O papel moseguros_app lê, insere e altera (sem delete: as propostas não se
-- apagam e as respostas são substituídas actualizando as linhas existentes).
--
-- ck_proposal_status e ck_proposal_channel têm nome fixo: o S5-02 recria a primeira (D4) e o
-- S12-02 a segunda (com USSD). As colunas do pagamento (S5-02), da emissão (S6-01), da vistoria
-- (S4-03), da assinatura (S4-04), da decisão (S4-05) e do mediador (S4-06) entram nessas tarefas.

grant usage on schema proposal to moseguros_app;
alter default privileges in schema proposal grant select, insert, update on tables to moseguros_app;

create table proposal.proposal (
  id                              uuid primary key,
  created_at                      timestamptz not null,
  created_by                      varchar(64) not null,
  updated_at                      timestamptz not null,
  updated_by                      varchar(64) not null,
  version                         bigint not null default 0,
  number                          varchar(16) not null unique,      -- PR-AAAA-NNNNNN (shared.business_sequence)
  quote_id                        uuid not null unique,             -- rating.quote, noutro schema: sem FK
  insurer_id                      uuid not null,
  insurer_name                    varchar(200) not null,            -- displayName congelado
  product_id                      uuid not null,
  product_version_id              uuid not null,
  product_name                    varchar(120) not null,            -- congelado
  product_type                    varchar(20) not null
                                  check (product_type in ('MTPL_ONLY','COMPREHENSIVE','TEMPORARY','BORDER','FLEET')),
  customer_user_id                uuid not null,                    -- dono da proposta (identity.user_account)
  created_by_user_id              uuid not null,                    -- o cliente, ou o utilizador do mediador (S4-06)
  channel                         varchar(10) not null,
  broker_org_id                   uuid null,
  broker_name                     varchar(200) null,                -- congelado (S4-06)
  vehicle_id                      uuid not null,
  driver_id                       uuid not null,
  policyholder                    jsonb not null,                   -- PolicyholderSnapshot, sem documento nem NUIT em claro
  policyholder_id_doc_number_enc  bytea not null,                   -- AES-GCM (CryptoService)
  policyholder_nuit_enc           bytea not null,                   -- AES-GCM (CryptoService)
  insured                         jsonb not null,                   -- {"sameAsPolicyholder": true}
  vehicle_snapshot                jsonb not null,                   -- VehicleSnapshot
  driver_snapshot                 jsonb not null,                   -- DriverSnapshot sem licenceNumberHash
  coverages                       jsonb not null,                   -- [CoverageLine]
  premium_breakdown               jsonb not null,                   -- PremiumBreakdown
  total_amount                    bigint not null check (total_amount > 0),
  start_at                        timestamptz not null,             -- 00:00 de startDate em Africa/Maputo
  end_at                          timestamptz not null,             -- exclusivo: 00:00 do dia seguinte a endDate
  duration_days                   int not null check (duration_days in (7,14,30,60,365)),
  status                          varchar(20) not null,
  content_revision                int not null default 1,
  inspection_required             boolean not null,
  declarations_confirmed_at       timestamptz null,
  suitability_result              varchar(10) null check (suitability_result in ('SUITABLE','WARNED')),
  suitability_reasons             text[] null,
  suitability_answered_at         timestamptz null,
  suitability_acknowledged_at     timestamptz null,
  expires_at                      timestamptz null,
  submitted_at                    timestamptz null,
  constraint ck_proposal_status check (status in ('DRAFT','UNDER_REVIEW','INFO_REQUESTED','ACCEPTED','REJECTED',
                                                  'EXPIRED','PAID','CONVERTED')),
  constraint ck_proposal_channel check (channel in ('APP','WEB','BROKER')),
  check ((channel = 'BROKER') = (broker_org_id is not null)),
  check (end_at > start_at),
  check (content_revision >= 1),
  check (suitability_acknowledged_at is null or suitability_result = 'WARNED')
);

create index ix_proposal_customer on proposal.proposal (customer_user_id, created_at desc);
create index ix_proposal_insurer on proposal.proposal (insurer_id, status, submitted_at);
create index ix_proposal_broker on proposal.proposal (broker_org_id, created_at desc)
  where broker_org_id is not null;
create index ix_proposal_expiry on proposal.proposal (status, expires_at)
  where expires_at is not null;

create table proposal.declaration (
  id             uuid primary key,
  created_at     timestamptz not null,
  created_by     varchar(64) not null,
  updated_at     timestamptz not null,
  updated_by     varchar(64) not null,
  version        bigint not null default 0,
  insurer_id     uuid not null,                                     -- igual ao da proposta
  proposal_id    uuid not null references proposal.proposal (id),
  question_code  varchar(30) not null
                 check (question_code in ('CLAIMS_LAST_3_YEARS','PREVIOUS_REFUSAL','USAGE_DIFFERENT',
                                          'VEHICLE_MODIFIED','NOVICE_DRIVERS')),
  answer         boolean not null,
  claims_count   int null check (claims_count between 1 and 20),
  details        varchar(500) null,
  unique (proposal_id, question_code)
);

create table proposal.suitability_answer (
  id             uuid primary key,
  created_at     timestamptz not null,
  created_by     varchar(64) not null,
  updated_at     timestamptz not null,
  updated_by     varchar(64) not null,
  version        bigint not null default 0,
  insurer_id     uuid not null,                                     -- igual ao da proposta
  proposal_id    uuid not null references proposal.proposal (id),
  question_code  varchar(30) not null
                 check (question_code in ('SUIT_NEED','SUIT_FINANCED','SUIT_CAN_AFFORD_REPAIR','SUIT_CROSS_BORDER',
                                          'SUIT_DEDUCTIBLE')),
  answer         varchar(20) not null,
  unique (proposal_id, question_code)
);

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda).
alter table proposal.proposal enable row level security;
create policy proposal_scope on proposal.proposal
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'ORG'
        and broker_org_id = nullif(current_setting('app.org_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and customer_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'ORG'
        and broker_org_id = nullif(current_setting('app.org_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and customer_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

-- As linhas filhas são visíveis exactamente quando a proposta o é (a subconsulta fica sujeita ao
-- RLS de proposal.proposal).
alter table proposal.declaration enable row level security;
create policy declaration_scope on proposal.declaration
  for all to moseguros_app
  using (exists (select 1 from proposal.proposal p where p.id = proposal_id))
  with check (exists (select 1 from proposal.proposal p where p.id = proposal_id));

alter table proposal.suitability_answer enable row level security;
create policy suitability_answer_scope on proposal.suitability_answer
  for all to moseguros_app
  using (exists (select 1 from proposal.proposal p where p.id = proposal_id))
  with check (exists (select 1 from proposal.proposal p where p.id = proposal_id));
