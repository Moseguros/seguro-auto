-- Reconciliação dos pagamentos por referência (S5-10; ADR-004, ADR-007). Corre como dono. Os grants ao
-- papel moseguros_app (select, insert, update; sem delete) vêm do "alter default privileges" do S5-01
-- (V202701150101). Sem apagamento físico (AGENTS.md §5).
--
-- inbound_message: uma linha por mensagem RabbitMQ recebida do payment-file-worker (idempotência por
-- message_id). file_import: um ficheiro de movimentos MEPS ou BMEPS processado ou rejeitado pelo worker.
--
-- RLS: s = app.scope, i = app.insurer_id (ADR-002). Clientes não vêem nenhuma das duas.

create table payment.inbound_message (
  id            uuid primary key,
  created_at    timestamptz not null,
  created_by    varchar(64) not null,
  updated_at    timestamptz not null,
  updated_by    varchar(64) not null,
  version       bigint not null default 0,
  message_id    varchar(64) not null unique,
  message_type  varchar(40) not null
                check (message_type in ('ReferencePaymentReceived','PaymentFileProcessed','PaymentFileRejected',
                                        'PaymentSourcePolled')),
  insurer_id    uuid not null,
  source_id     uuid not null,                                  -- insurer_payment_method.id
  file_sha256   varchar(64) null,                               -- ficheiro de origem (ReferencePaymentReceived)
  received_at   timestamptz not null,
  outcome       varchar(12) not null check (outcome in ('CONFIRMED','EXCEPTION','RECORDED','IGNORED')),
  intent_id     uuid null,
  exception_id  uuid null references payment.payment_exception (id)
);

create index ix_inbound_message_file on payment.inbound_message (source_id, file_sha256);
create index ix_inbound_message_received on payment.inbound_message (received_at desc);

alter table payment.inbound_message enable row level security;
create policy inbound_message_scope on payment.inbound_message
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid));

create table payment.file_import (
  id              uuid primary key,
  created_at      timestamptz not null,
  created_by      varchar(64) not null,
  updated_at      timestamptz not null,
  updated_by      varchar(64) not null,
  version         bigint not null default 0,
  message_id      varchar(64) not null unique,
  insurer_id      uuid not null,
  source_id       uuid not null,                                -- insurer_payment_method.id
  file_name       varchar(255) not null,
  sha256          varchar(64) not null,
  file_format     varchar(10) not null check (file_format in ('MEPS','BMEPS')),
  format_file_id  varchar(30) null,                             -- identificador do ficheiro no formato (S5-08)
  status          varchar(10) not null check (status in ('PROCESSED','REJECTED')),
  record_count    int null,                                     -- só PROCESSED
  total_amount    bigint null,                                  -- só PROCESSED
  total_fees      bigint null,                                  -- tarifas (MEPS) ou comissões (BMEPS); informativo
  reason          varchar(30) null,                             -- FileRejectionReason; só REJECTED
  detail          varchar(200) null,
  line_no         int null,
  occurred_at     timestamptz not null,                         -- processedAt ou rejectedAt
  constraint ck_file_import_processed check (status <> 'PROCESSED' or (record_count is not null
                                                                       and total_amount is not null)),
  constraint ck_file_import_rejected check (status <> 'REJECTED' or reason is not null)
);

create index ix_file_import_insurer on payment.file_import (insurer_id, occurred_at desc);

alter table payment.file_import enable row level security;
create policy file_import_scope on payment.file_import
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid));

-- No contrato (payment-contracts) o nome do ficheiro vai até 255 caracteres e o identificador da
-- transacção do banco até 64: guardam-se inteiros.
alter table payment.payment_exception alter column file_name type varchar(255);
alter table payment.payment_exception alter column bank_transaction_id type varchar(64);
alter table payment.reference_payment alter column bank_transaction_id type varchar(64);
