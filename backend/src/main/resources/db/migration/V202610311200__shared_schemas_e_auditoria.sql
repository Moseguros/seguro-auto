-- Schemas de todos os módulos (um por módulo, AGENTS.md §5) e tabela de auditoria.
create schema if not exists audit;
create schema if not exists identity;
create schema if not exists organization;
create schema if not exists vehicle;
create schema if not exists catalog;
create schema if not exists rating;
create schema if not exists proposal;
create schema if not exists payment;
create schema if not exists policy;
create schema if not exists document;
create schema if not exists notification;
create schema if not exists verification;
create schema if not exists claim;
create schema if not exists complaint;
create schema if not exists assistance;
create schema if not exists reporting;
create schema if not exists fraud;
create schema if not exists integration;

-- Só recebe inserções: excepção às colunas obrigatórias (AGENTS.md §5).
-- occurred_at e actor_id fazem o papel de created_at e created_by.
create table audit.audit_event (
  id           uuid primary key,
  occurred_at  timestamptz not null,
  actor_id     varchar(64) not null,
  actor_type   varchar(20) not null check (actor_type in ('CUSTOMER','STAFF','SYSTEM','SERVICE')),
  insurer_id   uuid null,
  action       varchar(80) not null,
  entity_type  varchar(60) not null,
  entity_id    varchar(64) not null,
  before_state jsonb null,
  after_state  jsonb null,
  ip           varchar(45) null,
  trace_id     varchar(64) null
);
create index ix_audit_entity on audit.audit_event (entity_type, entity_id, occurred_at desc);
create index ix_audit_insurer on audit.audit_event (insurer_id, occurred_at desc);
