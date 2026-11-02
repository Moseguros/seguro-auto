-- Contas, perfis de cliente, perfis/permissões, OTP, sessões, convites e recuperação de
-- palavra-passe (S1-01). Privilégios do papel moseguros_app: os por defeito de
-- V202611020900 (select, insert, update), com as excepções indicadas em cada tabela.

create table identity.user_account (
  id                  uuid primary key,
  created_at          timestamptz not null,
  created_by          varchar(64) not null,
  updated_at          timestamptz not null,
  updated_by          varchar(64) not null,
  version             bigint not null default 0,
  type                varchar(10) not null check (type in ('CUSTOMER','STAFF')),
  msisdn              varchar(12) null check (msisdn ~ '^258[0-9]{9}$'),
  msisdn_verified     boolean not null default false,
  email               varchar(254) null check (email = lower(email)),
  email_verified      boolean not null default false,
  display_name        varchar(120) null,
  preferred_language  varchar(5) not null default 'pt' check (preferred_language in ('pt','en')),
  password_hash       varchar(200) null,
  totp_secret_enc     bytea null,
  last_totp_step      bigint null,
  status              varchar(20) not null
                      check (status in ('PENDING_ACTIVATION','ACTIVE','LOCKED','DISABLED')),
  locked_until        timestamptz null,
  failed_attempts     int not null default 0,
  -- sem FK: outro módulo (organization); a consistência vem do módulo
  organization_id     uuid null,
  last_login_at       timestamptz null,
  check (type = 'STAFF' or (msisdn is not null or email is not null)),
  check (type = 'CUSTOMER' or (email is not null and msisdn is null)),
  check (type = 'STAFF' or (password_hash is null and totp_secret_enc is null and organization_id is null))
);
create unique index ux_user_customer_msisdn on identity.user_account (msisdn) where type = 'CUSTOMER' and msisdn is not null;
create unique index ux_user_customer_email  on identity.user_account (email)  where type = 'CUSTOMER' and email is not null;
create unique index ux_user_staff_email     on identity.user_account (email)  where type = 'STAFF';
create index ix_user_org on identity.user_account (organization_id) where organization_id is not null;

create table identity.customer_profile (
  id                 uuid primary key,
  created_at         timestamptz not null,
  created_by         varchar(64) not null,
  updated_at         timestamptz not null,
  updated_by         varchar(64) not null,
  version            bigint not null default 0,
  user_id            uuid not null unique references identity.user_account (id),
  full_name          varchar(120) null,
  birth_date         date null,
  id_doc_type        varchar(10) null check (id_doc_type in ('BI','PASSPORT','DIRE')),
  id_doc_number_enc  bytea null,  -- AES-GCM (CryptoService)
  nuit_enc           bytea null,  -- AES-GCM (CryptoService)
  address            varchar(300) null,
  province           varchar(20) null
                     check (province in ('MAPUTO_CITY','MAPUTO','GAZA','INHAMBANE','SOFALA','MANICA',
                                         'TETE','ZAMBEZIA','NAMPULA','CABO_DELGADO','NIASSA'))
);

-- Tabelas de referência: só leitura para a aplicação; os perfis mudam por migração.
create table identity.role (
  id          uuid primary key,
  created_at  timestamptz not null,
  created_by  varchar(64) not null,
  updated_at  timestamptz not null,
  updated_by  varchar(64) not null,
  version     bigint not null default 0,
  code        varchar(30) not null unique,
  org_types   text[] null  -- null = só fora de organizações
);
revoke insert, update on identity.role from moseguros_app;

-- Associação: chave composta e só created_at/created_by (AGENTS.md §5).
create table identity.role_permission (
  role_code   varchar(30) not null references identity.role (code),
  permission  varchar(60) not null,
  created_at  timestamptz not null,
  created_by  varchar(64) not null,
  primary key (role_code, permission)
);
revoke insert, update on identity.role_permission from moseguros_app;

create table identity.user_role (
  user_id     uuid not null references identity.user_account (id),
  role_code   varchar(30) not null references identity.role (code),
  created_at  timestamptz not null,
  created_by  varchar(64) not null,
  primary key (user_id, role_code)
);
-- Única tabela em que a aplicação apaga linhas.
grant delete on identity.user_role to moseguros_app;

create table identity.otp_settings (
  id                     uuid primary key,
  created_at             timestamptz not null,
  created_by             varchar(64) not null,
  updated_at             timestamptz not null,
  updated_by             varchar(64) not null,
  version                bigint not null default 0,
  singleton              boolean not null default true unique check (singleton),
  enabled_channels       text[] not null
                         check (cardinality(enabled_channels) > 0
                                and enabled_channels <@ array['SMS','WHATSAPP','EMAIL']::text[]),
  default_channel        varchar(10) not null,
  customer_can_choose    boolean not null,
  fallback_order         text[] not null,
  code_length            int not null default 6 check (code_length between 4 and 8),
  validity_seconds       int not null default 300 check (validity_seconds between 60 and 1800),
  max_attempts           int not null default 3 check (max_attempts between 1 and 10),
  resend_after_seconds   int not null default 60 check (resend_after_seconds between 15 and 600),
  max_requests_per_hour  int not null default 5 check (max_requests_per_hour between 1 and 50),
  check (default_channel = any (enabled_channels)),
  check (fallback_order <@ enabled_channels and enabled_channels <@ fallback_order
         and cardinality(fallback_order) = cardinality(enabled_channels))
);

-- Linha inicial (antes de activar o RLS, por clareza).
insert into identity.otp_settings
  (id, created_at, created_by, updated_at, updated_by, enabled_channels, default_channel,
   customer_can_choose, fallback_order)
values
  ('019a4300-0000-7000-8000-000000000001', now(), 'system', now(), 'system',
   '{SMS,WHATSAPP,EMAIL}', 'SMS', true, '{SMS,WHATSAPP,EMAIL}');

create table identity.otp_challenge (
  id                          uuid primary key,
  created_at                  timestamptz not null,
  created_by                  varchar(64) not null,
  updated_at                  timestamptz not null,
  updated_by                  varchar(64) not null,
  version                     bigint not null default 0,
  identifier                  varchar(254) not null,
  identifier_type             varchar(10) not null check (identifier_type in ('MSISDN','EMAIL')),
  channel                     varchar(10) not null check (channel in ('SMS','WHATSAPP','EMAIL')),
  purpose                     varchar(20) not null check (purpose in ('LOGIN','VERIFY_CONTACT','SIGN','TEST')),
  user_id                     uuid null,
  code_hash                   varchar(64) not null,
  expires_at                  timestamptz not null,
  attempts                    int not null default 0,
  consumed_at                 timestamptz null,
  superseded_at               timestamptz null,
  delivery_status             varchar(12) not null
                              check (delivery_status in ('PENDING','SENT','DELIVERED','FAILED')),
  sent_at                     timestamptz null,
  fallback_from_challenge_id  uuid null references identity.otp_challenge (id),
  root_challenge_id           uuid not null
);
create index ix_otp_challenge_identifier on identity.otp_challenge (identifier, created_at desc);
create index ix_otp_challenge_root on identity.otp_challenge (root_challenge_id);

create table identity.refresh_token (
  id                  uuid primary key,
  created_at          timestamptz not null,
  created_by          varchar(64) not null,
  updated_at          timestamptz not null,
  updated_by          varchar(64) not null,
  version             bigint not null default 0,
  user_id             uuid not null references identity.user_account (id),
  family_id           uuid not null,
  token_hash          varchar(64) not null unique,
  expires_at          timestamptz not null,
  session_expires_at  timestamptz null,
  revoked_at          timestamptz null,
  revoked_reason      varchar(20) null
                      check (revoked_reason in ('ROTATED','LOGOUT','REUSE_DETECTED','PASSWORD_RESET',
                                                'ACCESS_RESET','DISABLED','ORG_SUSPENDED')),
  replaced_by_id      uuid null,
  device_info         varchar(200) null
);
create index ix_refresh_token_user_active on identity.refresh_token (user_id) where revoked_at is null;
create index ix_refresh_token_family on identity.refresh_token (family_id);

create table identity.invitation (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  email                varchar(254) not null check (email = lower(email)),
  full_name            varchar(120) not null,
  organization_id      uuid null,  -- null = staff da plataforma
  role_codes           text[] not null check (cardinality(role_codes) > 0),
  token_hash           varchar(64) not null unique,
  expires_at           timestamptz not null,
  accepted_at          timestamptz null,
  revoked_at           timestamptz null,
  invited_by_user_id   uuid null,
  user_id              uuid null,
  activation_attempts  int not null default 0
);

create table identity.password_reset (
  id              uuid primary key,
  created_at      timestamptz not null,
  created_by      varchar(64) not null,
  updated_at      timestamptz not null,
  updated_by      varchar(64) not null,
  version         bigint not null default 0,
  user_id         uuid not null references identity.user_account (id),
  token_hash      varchar(64) not null unique,
  expires_at      timestamptz not null,
  used_at         timestamptz null,
  invalidated_at  timestamptz null
);

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda).
-- s = current_setting('app.scope', true)
-- u = nullif(current_setting('app.user_id', true), '')::uuid
-- o = nullif(current_setting('app.org_id', true), '')::uuid

alter table identity.user_account enable row level security;
create policy user_account_scope on identity.user_account
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and organization_id = nullif(current_setting('app.org_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and organization_id = nullif(current_setting('app.org_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and id = nullif(current_setting('app.user_id', true), '')::uuid));

alter table identity.customer_profile enable row level security;
create policy customer_profile_scope on identity.customer_profile
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid));

alter table identity.user_role enable row level security;
create policy user_role_scope on identity.user_role
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and exists (select 1 from identity.user_account a
                     where a.id = user_id
                       and a.organization_id = nullif(current_setting('app.org_id', true), '')::uuid))
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and exists (select 1 from identity.user_account a
                     where a.id = user_id
                       and a.organization_id = nullif(current_setting('app.org_id', true), '')::uuid))
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid));

alter table identity.invitation enable row level security;
create policy invitation_scope on identity.invitation
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and organization_id = nullif(current_setting('app.org_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and organization_id = nullif(current_setting('app.org_id', true), '')::uuid));

alter table identity.otp_settings enable row level security;
create policy otp_settings_scope on identity.otp_settings
  for all to moseguros_app
  using (current_setting('app.scope', true) in ('SYSTEM','PLATFORM'))
  with check (current_setting('app.scope', true) in ('SYSTEM','PLATFORM'));

alter table identity.otp_challenge enable row level security;
create policy otp_challenge_scope on identity.otp_challenge
  for all to moseguros_app
  using (current_setting('app.scope', true) = 'SYSTEM')
  with check (current_setting('app.scope', true) = 'SYSTEM');

alter table identity.refresh_token enable row level security;
create policy refresh_token_scope on identity.refresh_token
  for all to moseguros_app
  using (current_setting('app.scope', true) = 'SYSTEM')
  with check (current_setting('app.scope', true) = 'SYSTEM');

alter table identity.password_reset enable row level security;
create policy password_reset_scope on identity.password_reset
  for all to moseguros_app
  using (current_setting('app.scope', true) = 'SYSTEM')
  with check (current_setting('app.scope', true) = 'SYSTEM');
