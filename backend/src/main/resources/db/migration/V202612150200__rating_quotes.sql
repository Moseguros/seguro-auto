-- Pedidos de cotação e cotações (S3-02 §1; modelo transversal §2). Corre como dono. O papel
-- moseguros_app lê, insere e altera (sem delete). Sem FK para outros schemas.
--
-- rating.quote_request guarda os snapshots do veículo e do condutor (dados pessoais): só o cliente,
-- o mediador que pediu e a plataforma o vêem. rating.quote tem só preços: a seguradora vê as suas
-- linhas (insurer_id), sem o pedido.

grant usage on schema rating to moseguros_app;

create table rating.quote_request (
  id                    uuid primary key,
  created_at            timestamptz not null,
  created_by            varchar(64) not null,
  updated_at            timestamptz not null,
  updated_by            varchar(64) not null,
  version               bigint not null default 0,
  origin                varchar(10) not null check (origin in ('CUSTOMER','BROKER')),
  -- cliente que pediu, ou o cliente do mediador (identity.user_account)
  customer_user_id      uuid not null,
  -- 258XXXXXXXXX; obrigatório quando origin = BROKER
  client_msisdn         varchar(12) null check (client_msisdn ~ '^258[0-9]{9}$'),
  requested_by_user_id  uuid not null,
  requested_by_org_id   uuid null,
  product_type          varchar(20) not null check (product_type in ('MTPL_ONLY','COMPREHENSIVE','TEMPORARY')),
  vehicle_snapshot      jsonb not null,             -- RatedVehicle
  driver_snapshot       jsonb not null,             -- RatedDriver
  requested_coverages   jsonb not null,             -- [{ "code": "VTPL", "capital": 500000000 }]
  start_date            date not null,
  duration_days         int not null check (duration_days in (7,14,30,60,365)),
  end_date              date not null,
  evaluation_ms         int not null check (evaluation_ms >= 0),
  check ((origin = 'CUSTOMER' and requested_by_org_id is null and client_msisdn is null)
      or (origin = 'BROKER' and requested_by_org_id is not null and client_msisdn is not null)),
  check (end_date >= start_date)
);

create index ix_quote_request_customer on rating.quote_request (customer_user_id, created_at desc);
create index ix_quote_request_org on rating.quote_request (requested_by_org_id, created_at desc)
  where requested_by_org_id is not null;
create index ix_quote_request_requester on rating.quote_request (requested_by_user_id, created_at desc);

-- Uma linha por versão de produto avaliada.
create table rating.quote (
  id                         uuid primary key,
  created_at                 timestamptz not null,
  created_by                 varchar(64) not null,
  updated_at                 timestamptz not null,
  updated_by                 varchar(64) not null,
  version                    bigint not null default 0,
  quote_request_id           uuid not null references rating.quote_request (id),
  insurer_id                 uuid not null,
  customer_user_id           uuid not null,          -- cópia do pedido (RLS)
  requested_by_org_id        uuid null,              -- cópia do pedido (RLS)
  product_id                 uuid not null,
  product_version_id         uuid not null,
  provider                   varchar(10) not null check (provider in ('INTERNAL','EXTERNAL')),
  status                     varchar(15) not null check (status in ('OFFERED','NOT_AVAILABLE','EXPIRED','USED')),
  unavailable_reason         varchar(40) null
                             check (unavailable_reason in ('TARIFF_MISSING','TARIFF_INVALID','COVERAGE_NOT_OFFERED',
                                    'COVERAGE_REQUIREMENTS_NOT_MET','CAPITAL_NOT_OFFERED','VEHICLE_VALUE_REQUIRED',
                                    'DURATION_NOT_RATED','NO_MATCHING_ROW','INSURER_TIMEOUT','INSURER_UNAVAILABLE',
                                    'INSURER_DECLINED','EXTERNAL_INVALID_RESPONSE','EVALUATION_ERROR')),
  unavailable_detail         jsonb null,
  premium_breakdown          jsonb null,             -- PremiumBreakdown (S3-01 §10) ou o do externo
  commercial_premium_amount  bigint null,
  charges_amount             bigint null,
  total_amount               bigint null,
  valid_until                timestamptz null,
  used_at                    timestamptz null,
  external_reference         varchar(100) null,
  rating_ms                  int not null check (rating_ms >= 0),
  check ((status in ('OFFERED','EXPIRED','USED') and total_amount is not null and premium_breakdown is not null
          and valid_until is not null and unavailable_reason is null)
      or (status = 'NOT_AVAILABLE' and unavailable_reason is not null and total_amount is null)),
  check (status <> 'USED' or used_at is not null)
);

create unique index ux_quote_request_version on rating.quote (quote_request_id, product_version_id);
create index ix_quote_insurer on rating.quote (insurer_id, created_at desc);

grant select, insert, update on rating.quote_request, rating.quote to moseguros_app;

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda).
alter table rating.quote_request enable row level security;
create policy quote_request_scope on rating.quote_request
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and requested_by_org_id = nullif(current_setting('app.org_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and customer_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and requested_by_org_id = nullif(current_setting('app.org_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and customer_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

alter table rating.quote enable row level security;
create policy quote_scope on rating.quote
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and (insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid
             or requested_by_org_id = nullif(current_setting('app.org_id', true), '')::uuid))
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and customer_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and (insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid
             or requested_by_org_id = nullif(current_setting('app.org_id', true), '')::uuid))
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and customer_user_id = nullif(current_setting('app.user_id', true), '')::uuid));
