-- Formas de pagamento por seguradora (S5-01 §3; ADR-007). Primeira migração do schema payment
-- (criado em V202610311200, sem grant). Corre como dono. O papel moseguros_app lê, insere e altera
-- (sem delete: uma forma de pagamento desactiva-se, não se apaga).
--
-- settings guarda só dados não secretos (schemas/payment-method-<método>.json); secret_refs guarda
-- o mapa campo → { path, updatedAt } com o caminho no cofre de segredos, nunca os valores
-- (AGENTS.md §6). As colunas last_poll_* e stale_since são preenchidas pelo S5-10.

grant usage on schema payment to moseguros_app;
alter default privileges in schema payment grant select, insert, update on tables to moseguros_app;

create table payment.insurer_payment_method (
  id                       uuid primary key,
  created_at               timestamptz not null,
  created_by               varchar(64) not null,
  updated_at               timestamptz not null,
  updated_by               varchar(64) not null,
  version                  bigint not null default 0,
  insurer_id               uuid not null,                    -- organization.organization (INSURER), noutro schema: sem FK
  method                   varchar(20) not null check (method in ('MPESA','BANK_REFERENCE')),
  enabled                  boolean not null default false,   -- só muda por enable/disable
  enabled_at               timestamptz null,                 -- última activação (alerta de fonte parada, S5-10)
  environment              varchar(12) not null default 'SANDBOX' check (environment in ('SANDBOX','PRODUCTION')),
  display_name             varchar(60) null,
  settings                 jsonb not null default '{}',
  secret_refs              jsonb not null default '{}',
  min_amount               bigint null check (min_amount is null or min_amount > 0),
  max_amount               bigint null check (max_amount is null or min_amount is null or max_amount >= min_amount),
  intent_validity_minutes  int not null default 30 check (intent_validity_minutes between 5 and 1440),
  sort_order               int not null default 0,
  last_test_at             timestamptz null,
  last_test_result         varchar(15) null check (last_test_result in ('OK','FAILED','NOT_SUPPORTED')),
  last_test_message        varchar(200) null,
  last_error_code          varchar(40) null,
  last_error_at            timestamptz null,
  last_poll_at             timestamptz null,
  last_poll_outcome        varchar(10) null check (last_poll_outcome in ('OK','ERROR')),
  last_poll_error          varchar(60) null,
  last_poll_success_at     timestamptz null,
  stale_since              timestamptz null,
  constraint ux_insurer_payment_method unique (insurer_id, method)
);

-- Duas seguradoras não podem ter a mesma entidade Ponto24 (o serviço verifica antes, ENTITY_IN_USE).
create unique index ux_bank_reference_entity on payment.insurer_payment_method ((settings->>'entity'))
  where method = 'BANK_REFERENCE' and settings ? 'entity';

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda).
-- Clientes e mediadores não lêem esta tabela: o que precisam passa por PaymentSystemLookups (SYSTEM).
alter table payment.insurer_payment_method enable row level security;
create policy insurer_payment_method_scope on payment.insurer_payment_method
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid));
