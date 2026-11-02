-- Organizações (seguradoras, mediadores, oficinas...) e perfil de seguradora (S1-01).
-- Privilégios do papel moseguros_app: os por defeito de V202611020900 (select, insert, update).

create table organization.organization (
  id              uuid primary key,
  created_at      timestamptz not null,
  created_by      varchar(64) not null,
  updated_at      timestamptz not null,
  updated_by      varchar(64) not null,
  version         bigint not null default 0,
  type            varchar(20) not null
                  check (type in ('INSURER','BROKER','AGENT','WORKSHOP','ASSESSOR','ASSISTANCE',
                                  'OMBUDSMAN','REGULATOR','POLICE','CORPORATE_CLIENT')),
  legal_name      varchar(200) not null,
  trade_name      varchar(120) null,
  -- NUIT de pessoa colectiva: público, não é cifrado (AGENTS.md §6)
  nuit            varchar(9) not null unique check (nuit ~ '^[0-9]{9}$'),
  license_number  varchar(40) null,
  status          varchar(20) not null default 'ACTIVE' check (status in ('ACTIVE','SUSPENDED')),
  contact_email   varchar(254) not null check (contact_email = lower(contact_email)),
  contact_phone   varchar(20) null check (contact_phone ~ '^\+?[0-9]{9,15}$'),
  address         varchar(300) null,
  province        varchar(20) null
                  check (province in ('MAPUTO_CITY','MAPUTO','GAZA','INHAMBANE','SOFALA','MANICA',
                                      'TETE','ZAMBEZIA','NAMPULA','CABO_DELGADO','NIASSA'))
);

create table organization.insurer_profile (
  id                uuid primary key,
  created_at        timestamptz not null,
  created_by        varchar(64) not null,
  updated_at        timestamptz not null,
  updated_by        varchar(64) not null,
  version           bigint not null default 0,
  organization_id   uuid not null unique references organization.organization (id),
  short_code        varchar(10) not null unique check (short_code ~ '^[A-Z0-9]{2,10}$'),
  logo_file_id      uuid null,
  complaints_email  varchar(254) not null check (complaints_email = lower(complaints_email)),
  ombudsman_org_id  uuid null references organization.organization (id)
);

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda)
alter table organization.organization enable row level security;
create policy organization_scope on organization.organization
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and id = nullif(current_setting('app.org_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and id = nullif(current_setting('app.org_id', true), '')::uuid));

alter table organization.insurer_profile enable row level security;
create policy insurer_profile_scope on organization.insurer_profile
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and organization_id = nullif(current_setting('app.org_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and organization_id = nullif(current_setting('app.org_id', true), '')::uuid));
