-- Ficheiros (S2-01, ADR-005): metadados de cada ficheiro guardado no FileStorage.
-- Corre como dono. O papel moseguros_app lê, insere e altera; nunca apaga (a limpeza de
-- órfãos marca purged_at e apaga só o conteúdo).

grant usage on schema document to moseguros_app;
alter default privileges in schema document grant select, insert, update on tables to moseguros_app;

create table document.file (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  -- tipo do dono (VEHICLE, PRODUCT_VERSION...): sem lista fechada; o código só aceita tipos
  -- com uma FileAccessPolicy registada
  owner_type           varchar(30) not null check (owner_type ~ '^[A-Z][A-Z_]{1,29}$'),
  -- entidade dona, noutro schema: sem FK
  owner_id             uuid not null,
  purpose              varchar(40) not null check (purpose ~ '^[A-Z][A-Z_]{1,39}$'),
  insurer_id           uuid null,
  uploaded_by_user_id  uuid null,  -- null = gerado pelo sistema
  uploaded_by_org_id   uuid null,
  original_filename    varchar(200) not null,
  content_type         varchar(40) not null
                       check (content_type in ('image/jpeg','image/png','image/heic','application/pdf')),
  size_bytes           bigint not null check (size_bytes between 1 and 10485760),
  sha256               varchar(64) not null check (sha256 ~ '^[0-9a-f]{64}$'),
  storage_key          varchar(300) not null unique,
  classification       varchar(10) not null check (classification in ('PUBLIC','PERSONAL','SENSITIVE')),
  linked_at            timestamptz null,  -- null = órfão (DocumentApi.markLinked)
  purged_at            timestamptz null   -- conteúdo apagado pela limpeza; a linha fica
);

create index ix_file_owner on document.file (owner_type, owner_id);
create index ix_file_orphans on document.file (created_at) where linked_at is null and purged_at is null;

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda).
-- s = current_setting('app.scope', true)
-- u = nullif(current_setting('app.user_id', true), '')::uuid
-- o = nullif(current_setting('app.org_id', true), '')::uuid
-- i = nullif(current_setting('app.insurer_id', true), '')::uuid
-- O download lê em SYSTEM (DocumentSystemLookups) e decide pela FileAccessPolicy do dono.
alter table document.file enable row level security;
create policy file_scope on document.file
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and (insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid
             or uploaded_by_org_id = nullif(current_setting('app.org_id', true), '')::uuid))
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and uploaded_by_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and (insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid
             or uploaded_by_org_id = nullif(current_setting('app.org_id', true), '')::uuid))
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and uploaded_by_user_id = nullif(current_setting('app.user_id', true), '')::uuid));
