-- Apólices, prestações, certificados e linha temporal (S6-01 §3; modelo transversal §2, §4). Corre como
-- dono. O schema policy já existe (V202610311200). O papel moseguros_app lê, insere e altera (as tabelas
-- do S7 em diante herdam os mesmos direitos); nenhum delete (sem apagamento físico, AGENTS.md §5).
--
-- RLS (ADR-002; tabela de cliente, modelo §2): SYSTEM/PLATFORM vêem tudo; ORG só a sua seguradora
-- (app.insurer_id: um mediador, sem app.insurer_id, não vê apólices neste sprint); CUSTOMER só as suas
-- (policyholder_user_id, copiado da apólice para as outras três tabelas).

grant usage on schema policy to moseguros_app;
alter default privileges in schema policy grant select, insert, update on tables to moseguros_app;

create table policy.policy (
  id                    uuid primary key,
  created_at            timestamptz not null,
  created_by            varchar(64) not null,
  updated_at            timestamptz not null,
  updated_by            varchar(64) not null,
  version               bigint not null default 0,
  number                varchar(14) not null unique,          -- AP-AAAA-NNNNNN
  insurer_id            uuid not null,
  proposal_id           uuid not null,                        -- proposal.proposal, noutro schema: sem FK
  payment_intent_id     uuid not null,                        -- payment.payment_intent: sem FK
  product_version_id    uuid not null,
  product_name          varchar(120) not null,
  product_type          varchar(15) not null
                        check (product_type in ('MTPL_ONLY','COMPREHENSIVE','TEMPORARY','BORDER','FLEET')),
  coverage_type         varchar(15) not null
                        check (coverage_type in ('MTPL_ONLY','COMPREHENSIVE','TEMPORARY','BORDER')),
  policyholder_user_id  uuid not null,
  policyholder_name     varchar(120) not null,
  vehicle_id            uuid not null,
  plate                 varchar(20) not null,                 -- matrícula formatada (Matricula)
  vehicle_snapshot      jsonb not null,
  driver_snapshot       jsonb null,
  coverages             jsonb not null,
  start_at              timestamptz not null,
  end_at                timestamptz not null,
  renewal_type          varchar(20) not null check (renewal_type in ('ANNUAL_RENEWABLE','FIXED_TERM')),
  payment_plan          varchar(12) not null check (payment_plan in ('FULL','SEMIANNUAL','QUARTERLY','MONTHLY')),
  total_amount          bigint not null check (total_amount > 0),
  status                varchar(12) not null default 'ACTIVE'
                        check (status in ('ACTIVE','SUSPENDED','CANCELLED','EXPIRED','LAPSED')),
  status_reason         varchar(30) null,
  status_changed_at     timestamptz not null,
  channel               varchar(10) not null check (channel in ('APP','WEB','BROKER')),
  broker_org_id         uuid null,
  schedule_file_id      uuid null,                            -- condições particulares (S6-02)
  issued_at             timestamptz not null,
  constraint ck_policy_period check (end_at > start_at)
);

create unique index ux_policy_proposal on policy.policy (proposal_id);
create index ix_policy_insurer on policy.policy (insurer_id, issued_at desc);
create index ix_policy_holder on policy.policy (policyholder_user_id, issued_at desc);
create index ix_policy_plate on policy.policy (plate, end_at desc);
create index ix_policy_status on policy.policy (status);

alter table policy.policy enable row level security;
create policy policy_scope on policy.policy
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and policyholder_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and policyholder_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

create table policy.instalment (
  id                    uuid primary key,
  created_at            timestamptz not null,
  created_by            varchar(64) not null,
  updated_at            timestamptz not null,
  updated_by            varchar(64) not null,
  version               bigint not null default 0,
  policy_id             uuid not null references policy.policy (id),
  insurer_id            uuid not null,
  policyholder_user_id  uuid not null,
  seq                   int not null check (seq >= 1),
  due_date              date not null,                        -- data de negócio em Africa/Maputo
  amount                bigint not null check (amount > 0),
  status                varchar(10) not null check (status in ('DUE','PAID','OVERDUE','CANCELLED')),
  payment_intent_id     uuid null,
  paid_at               timestamptz null,
  constraint ux_instalment_policy_seq unique (policy_id, seq)
);

create index ix_instalment_status_due on policy.instalment (status, due_date);

alter table policy.instalment enable row level security;
create policy instalment_scope on policy.instalment
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and policyholder_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and policyholder_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

create table policy.certificate (
  id                    uuid primary key,
  created_at            timestamptz not null,
  created_by            varchar(64) not null,
  updated_at            timestamptz not null,
  updated_by            varchar(64) not null,
  version               bigint not null default 0,
  policy_id             uuid not null references policy.policy (id),
  insurer_id            uuid not null,
  policyholder_user_id  uuid not null,
  number                varchar(15) not null unique,          -- CT-AAAA-NNNNNNN
  valid_from            timestamptz not null,
  valid_to              timestamptz not null,
  verification_token    varchar(22) not null unique,          -- 128 bits em Base64 URL (S6-01 regra 6)
  pdf_file_id           uuid null,                            -- S6-02
  status                varchar(10) not null default 'VALID' check (status in ('VALID','REVOKED')),
  revoked_at            timestamptz null,                     -- S7
  revoked_reason        varchar(30) null                      -- S7
);

-- No máximo um certificado válido por apólice.
create unique index ux_certificate_valid on policy.certificate (policy_id) where status = 'VALID';

alter table policy.certificate enable row level security;
create policy certificate_scope on policy.certificate
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and policyholder_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and policyholder_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

-- Linha temporal da apólice. O S7 acrescenta tipos com uma migração nova (drop + add constraint).
create table policy.policy_event (
  id                    uuid primary key,
  created_at            timestamptz not null,
  created_by            varchar(64) not null,
  updated_at            timestamptz not null,
  updated_by            varchar(64) not null,
  version               bigint not null default 0,
  policy_id             uuid not null references policy.policy (id),
  insurer_id            uuid not null,
  policyholder_user_id  uuid not null,
  type                  varchar(30) not null,
  occurred_at           timestamptz not null,
  actor_type            varchar(10) not null check (actor_type in ('CUSTOMER','STAFF','SYSTEM')),
  actor_id              varchar(64) not null,                 -- sub ou nome do job/listener
  details               jsonb null,                           -- sem dados pessoais
  constraint ck_policy_event_type check (type in ('ISSUED','CERTIFICATE_ISSUED','DOCUMENTS_READY'))
);

create index ix_policy_event_policy on policy.policy_event (policy_id, occurred_at);

alter table policy.policy_event enable row level security;
create policy policy_event_scope on policy.policy_event
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and policyholder_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and policyholder_user_id = nullif(current_setting('app.user_id', true), '')::uuid));
