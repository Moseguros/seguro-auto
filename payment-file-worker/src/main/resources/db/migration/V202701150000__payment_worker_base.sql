-- Base de dados do payment-file-worker (S5-09 §3; modelo transversal §2 e §6). Corre como dono, com
-- histórico Flyway próprio em payment_worker.flyway_schema_history: o backend não toca neste schema
-- nem tem grants aqui. Sem RLS: só o worker usa este schema e o isolamento vem dos papéis de BD
-- (moseguros_worker_user, membro de moseguros_worker, criado pelo infra/postgres/init/02-worker-role.sql).

create schema if not exists payment_worker;

do $$ begin
  if not exists (select 1 from pg_roles where rolname = 'moseguros_worker') then
    create role moseguros_worker nologin;
  end if;
end $$;

grant usage on schema payment_worker to moseguros_worker;
alter default privileges in schema payment_worker grant select, insert, update on tables to moseguros_worker;

-- Um ficheiro lido de uma fonte (configuração BANK_REFERENCE de uma seguradora). Um ficheiro nunca é
-- processado duas vezes: pelo SHA-256 do conteúdo (qualquer que seja o nome) e pelo identificador do
-- ficheiro no formato (MEPS A005; BMEPS empresa-perfil-ID).
create table payment_worker.processed_file (
  id                       uuid primary key,
  created_at               timestamptz not null,
  created_by               varchar(64) not null,
  updated_at               timestamptz not null,
  updated_by               varchar(64) not null,
  version                  bigint not null default 0,
  source_id                uuid not null,
  insurer_id               uuid not null,
  file_name                varchar(255) not null,
  sha256                   varchar(64) not null,
  size_bytes               bigint not null,
  remote_mtime             timestamptz null,
  file_format              varchar(10) null,
  format_file_id           varchar(30) null,
  previous_format_file_id  varchar(30) null,
  record_count             int null,
  total_amount             bigint null,
  status                   varchar(12) not null check (status in ('RECEIVED','PARSED','PUBLISHED','DONE','ERROR')),
  error_code               varchar(30) null,
  error_message            varchar(500) null,
  attempts                 int not null default 1,
  received_at              timestamptz not null,
  completed_at             timestamptz null,
  constraint ux_processed_file_sha256 unique (source_id, sha256)
);

create unique index ux_processed_file_format_id on payment_worker.processed_file (source_id, format_file_id)
  where format_file_id is not null and status <> 'ERROR';
create index ix_processed_file_received on payment_worker.processed_file (source_id, received_at desc);
create index ix_processed_file_listing on payment_worker.processed_file (source_id, file_name, size_bytes, remote_mtime);

-- Um registo de detalhe (pagamento) de um ficheiro. raw_line: os layouts MEPS e BMEPS não têm dados
-- pessoais (referência, montantes, datas, terminal; S5-08 §2).
create table payment_worker.file_record (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  file_id              uuid not null references payment_worker.processed_file (id),
  line_no              int not null,
  entity               varchar(5) not null,
  reference            varchar(11) not null,
  amount               bigint not null,
  fee                  bigint null,
  paid_at              timestamptz not null,
  channel              varchar(20) null,
  bank_transaction_id  varchar(60) null,
  raw_line             varchar(200) not null,
  message_id           varchar(64) not null,
  published_at         timestamptz null,
  constraint ux_file_record_line unique (file_id, line_no)
);

create index ix_file_record_message on payment_worker.file_record (message_id);
create index ix_file_record_unpublished on payment_worker.file_record (file_id) where published_at is null;

-- Trincos do ShedLock (um por fonte: source:{sourceId}). Formato exigido pelo ShedLock: excepção às
-- colunas obrigatórias do AGENTS.md §5.
create table payment_worker.shedlock (
  name        varchar(64) primary key,
  lock_until  timestamptz not null,
  locked_at   timestamptz not null,
  locked_by   varchar(255) not null
);
