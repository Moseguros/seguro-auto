-- Idempotência partilhada (S5-02 §5; decisão D11): Idempotency-Key dos pagamentos (S5, S7),
-- emissões e indemnizações (S9-04). Não existe payment.idempotency_record. Corre como dono.
--
-- Tabela de infra-estrutura do schema shared (AGENTS.md §5): sem as colunas obrigatórias, só id e
-- created_at. Sem delete para a aplicação: um registo expirado (24 h) é substituído pelo serviço;
-- a limpeza dos expirados, se vier a ser precisa, é um job do S12-07.

-- Idempotentes: o schema já é criado pela V202701010200 (S4-02); repetidas para não depender da ordem.
create schema if not exists shared;
grant usage on schema shared to moseguros_app;

create table shared.idempotency_record (
  id               uuid primary key,
  created_at       timestamptz not null,
  user_id          uuid not null,                 -- quem enviou o pedido
  idempotency_key  uuid not null,
  operation        varchar(40) not null,          -- CREATE_PAYMENT_INTENT, …; sem check (cada ficha acrescenta)
  request_hash     varchar(64) not null,          -- SHA-256 hex de operation + "\n" + JSON canónico
  response_status  int null,                      -- null enquanto o pedido está a ser processado
  response_body    jsonb null,
  resource_id      uuid null,
  expires_at       timestamptz not null,          -- created_at + 24 h
  constraint ux_idempotency_record_key unique (user_id, idempotency_key)
);

create index ix_idempotency_record_expiry on shared.idempotency_record (expires_at);

grant select, insert, update on shared.idempotency_record to moseguros_app;

alter table shared.idempotency_record enable row level security;
create policy idempotency_record_scope on shared.idempotency_record
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or user_id = nullif(current_setting('app.user_id', true), '')::uuid)
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or user_id = nullif(current_setting('app.user_id', true), '')::uuid);
