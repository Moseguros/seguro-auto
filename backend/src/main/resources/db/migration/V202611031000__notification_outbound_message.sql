-- Mensagens enviadas pelo módulo de notificações (S1-03: só OTP). Nunca se grava o texto
-- nem o código; o destino fica mascarado e com hash (SHA-256 do destino normalizado).

create table notification.outbound_message (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  channel              varchar(10) not null check (channel in ('SMS','WHATSAPP','EMAIL')),
  purpose              varchar(30) not null check (purpose in ('OTP')),
  destination_masked   varchar(254) not null,
  destination_hash     varchar(64) not null,
  -- id do otp_challenge (sem FK: outro módulo)
  correlation_id       uuid null,
  provider             varchar(30) not null,
  provider_message_id  varchar(100) null,
  status               varchar(12) not null check (status in ('PENDING','SENT','DELIVERED','FAILED')),
  failure_reason       varchar(200) null,
  sent_at              timestamptz null,
  delivered_at         timestamptz null
);
create index ix_outbound_message_provider_id on notification.outbound_message (provider, provider_message_id)
  where provider_message_id is not null;
create index ix_outbound_message_correlation on notification.outbound_message (correlation_id)
  where correlation_id is not null;
create index ix_outbound_message_destination on notification.outbound_message (destination_hash, created_at desc);

grant usage on schema notification to moseguros_app;
grant select, insert, update on notification.outbound_message to moseguros_app;

-- Row Level Security (sem "force"; ver ADR-002, adenda): só o âmbito SYSTEM.
alter table notification.outbound_message enable row level security;
create policy outbound_message_scope on notification.outbound_message
  for all to moseguros_app
  using (current_setting('app.scope', true) = 'SYSTEM')
  with check (current_setting('app.scope', true) = 'SYSTEM');
