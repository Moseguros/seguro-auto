-- Registo das consultas de verificação (S6-03 §1; modelo transversal §2). Corre como dono. O schema
-- verification já existe (V202610311200). O registo só cresce: moseguros_app só lê e insere (sem update
-- nem delete). Nunca guarda a matrícula nem o código em claro: subject_hash é o HMAC-SHA256 do valor
-- (chave moseguros.verification.hash-key), plate_masked tem os dígitos trocados por '*' e ip_prefix o IP
-- truncado (/24 em IPv4, /48 em IPv6).
--
-- RLS: a consulta pública grava sem utilizador (âmbito NONE) e não pode ler; SYSTEM/PLATFORM lêem tudo;
-- um agente da polícia (ORG) só as suas consultas.

grant usage on schema verification to moseguros_app;

create table verification.check_log (
  id               uuid primary key,
  created_at       timestamptz not null,
  created_by       varchar(64) not null,
  updated_at       timestamptz not null,
  updated_by       varchar(64) not null,
  version          bigint not null default 0,
  subject_type     varchar(5) not null check (subject_type in ('PLATE','TOKEN')),
  subject_hash     varchar(64) not null,
  plate_masked     varchar(20) null,
  result           varchar(15) not null
                   check (result in ('VALID','NOT_YET_VALID','EXPIRED','SUSPENDED','CANCELLED','NOT_FOUND')),
  policy_id        uuid null,                 -- policy.policy, noutro schema: sem FK
  insurer_id       uuid null,
  checker_type     varchar(10) not null check (checker_type in ('PUBLIC','POLICE')),
  checker_user_id  uuid null,
  checker_org_id   uuid null,
  ip_prefix        varchar(49) null,
  checked_at       timestamptz not null,
  constraint ck_check_log_checker check ((checker_type = 'POLICE') = (checker_user_id is not null))
);

create index ix_check_log_checker on verification.check_log (checker_user_id, checked_at desc)
  where checker_user_id is not null;
create index ix_check_log_subject on verification.check_log (subject_hash, checked_at desc);
create index ix_check_log_checked_at on verification.check_log (checked_at);

grant select, insert on verification.check_log to moseguros_app;

alter table verification.check_log enable row level security;
create policy check_log_insert on verification.check_log for insert to moseguros_app with check (true);
create policy check_log_select on verification.check_log for select to moseguros_app using (
  current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
  or (current_setting('app.scope', true) = 'ORG'
      and checker_user_id = nullif(current_setting('app.user_id', true), '')::uuid)
);
