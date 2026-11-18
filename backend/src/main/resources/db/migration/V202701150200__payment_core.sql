-- Núcleo de pagamentos (S5-02 §2; ADR-007): intenções de pagamento, histórico técnico, referências
-- Ponto24 (entidade e referência) e excepções para o financeiro. Corre como dono. Os grants ao papel
-- moseguros_app (select, insert, update; sem delete) vêm do "alter default privileges" do S5-01
-- (V202701150101). Sem apagamento físico (AGENTS.md §5).
--
-- O dinheiro vai sempre directamente para a conta da seguradora: estas tabelas só registam o que a
-- plataforma pediu ao fornecedor e o que ele (ou o ficheiro do banco, S5-10) confirmou.
--
-- RLS: s = app.scope, u = app.user_id, i = app.insurer_id (ADR-002).

create table payment.payment_intent (
  id                        uuid primary key,
  created_at                timestamptz not null,
  created_by                varchar(64) not null,
  updated_at                timestamptz not null,
  updated_by                varchar(64) not null,
  version                   bigint not null default 0,
  reference                 varchar(16) not null unique,      -- PG-AAAA-NNNNNNN (shared.business_sequence)
  insurer_id                uuid not null,
  payer_user_id             uuid not null,
  purpose                   varchar(20) not null
                            check (purpose in ('NEW_POLICY','INSTALMENT','RENEWAL','ENDORSEMENT','FLEET_INCLUSION')),
  target_type               varchar(20) not null
                            check (target_type in ('PROPOSAL','INSTALMENT','RENEWAL','ENDORSEMENT','FLEET_INCLUSION')),
  target_id                 uuid not null,                     -- noutro módulo: sem FK
  target_label              varchar(40) not null,              -- número de negócio do alvo (PR-…)
  amount                    bigint not null check (amount > 0),
  method                    varchar(20) not null check (method in ('MPESA','BANK_REFERENCE')),
  method_config_id          uuid not null references payment.insurer_payment_method (id),
  payer_msisdn              varchar(12) null,                  -- 258XXXXXXXXX
  status                    varchar(12) not null
                            check (status in ('CREATED','PENDING','UNKNOWN','SUCCEEDED','FAILED','EXPIRED',
                                              'CANCELLED','REFUNDED')),
  failure_code              varchar(40) null,
  provider_tx_id            varchar(100) null,                 -- M-Pesa output_TransactionID
  provider_conversation_id  varchar(100) null,                 -- M-Pesa output_ConversationID
  third_party_reference     varchar(40) null,
  idempotency_key           uuid not null,
  expires_at                timestamptz not null,
  initiated_by_user_id      uuid not null,
  initiated_via             varchar(12) not null check (initiated_via in ('APP','WEB','BROKER_LINK','USSD')),
  dispatched_at             timestamptz null,
  unknown_since             timestamptz null,
  next_query_at             timestamptz null,
  query_attempts            int not null default 0,
  succeeded_at              timestamptz null,
  failed_at                 timestamptz null,
  confirmed_by              varchar(20) null
                            check (confirmed_by in ('PROVIDER','STATUS_QUERY','REFERENCE_FILE','MANUAL')),
  constraint ck_payment_intent_mpesa_msisdn check (method <> 'MPESA' or payer_msisdn is not null)
);

create unique index ux_payment_intent_third_party_reference on payment.payment_intent (third_party_reference)
  where third_party_reference is not null;
create index ix_payment_intent_target on payment.payment_intent (target_type, target_id);
create index ix_payment_intent_insurer on payment.payment_intent (insurer_id, created_at desc);
create index ix_payment_intent_payer on payment.payment_intent (payer_user_id, created_at desc);
create index ix_payment_intent_query on payment.payment_intent (next_query_at) where status = 'UNKNOWN';
create index ix_payment_intent_expiry on payment.payment_intent (expires_at) where status in ('CREATED','PENDING');

alter table payment.payment_intent enable row level security;
create policy payment_intent_scope on payment.payment_intent
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and payer_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and payer_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

-- Histórico técnico de cada intenção (só inserções na prática). raw_payload já vem mascarado: sem
-- tokens, sem segredos, telemóvel com Msisdn.masked().
create table payment.payment_event (
  id            uuid primary key,
  created_at    timestamptz not null,
  created_by    varchar(64) not null,
  updated_at    timestamptz not null,
  updated_by    varchar(64) not null,
  version       bigint not null default 0,
  intent_id     uuid not null references payment.payment_intent (id),
  insurer_id    uuid not null,
  type          varchar(30) not null
                check (type in ('CREATED','DISPATCHED','PROVIDER_RESPONSE','STATUS_QUERY','STATUS_CHANGED',
                                'REFERENCE_PAID','MANUAL_RESOLUTION')),
  from_status   varchar(12) null,
  to_status     varchar(12) null,
  raw_payload   jsonb null,
  received_at   timestamptz not null
);

create index ix_payment_event_intent on payment.payment_event (intent_id, received_at);

alter table payment.payment_event enable row level security;
create policy payment_event_scope on payment.payment_event
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and exists (select 1 from payment.payment_intent p
                     where p.id = intent_id
                       and p.payer_user_id = nullif(current_setting('app.user_id', true), '')::uuid)))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and exists (select 1 from payment.payment_intent p
                     where p.id = intent_id
                       and p.payer_user_id = nullif(current_setting('app.user_id', true), '')::uuid)));

-- Uma linha por intenção BANK_REFERENCE (rede Ponto24 da InterBancos; docs/specs/interbancos/). A
-- referência e o número de factura nunca se repetem na entidade, em nenhum estado (S5-07).
create table payment.reference_payment (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  intent_id            uuid not null unique references payment.payment_intent (id),
  insurer_id           uuid not null,
  payer_user_id        uuid not null,
  entity               varchar(5) not null check (entity ~ '^[0-9]{5}$'),
  reference            varchar(11) not null check (reference ~ '^[0-9]{11}$'),
  invoice_number       int not null check (invoice_number between 1 and 9999999),
  reference_month      smallint not null check (reference_month between 1 and 12),
  amount               bigint not null check (amount > 0),
  valid_until          timestamptz not null,
  status               varchar(10) not null check (status in ('ACTIVE','PAID','EXPIRED','CANCELLED')),
  paid_at              timestamptz null,
  bank_transaction_id  varchar(60) null,
  message_id           varchar(64) null,
  document_file_id     uuid null,                               -- document.file, noutro schema: sem FK
  sms_sent_count       int not null default 0 check (sms_sent_count >= 0),
  constraint ux_reference_payment_reference unique (entity, reference),
  constraint ux_reference_payment_invoice unique (entity, invoice_number)
);

create index ix_reference_payment_insurer on payment.reference_payment (insurer_id, status);

alter table payment.reference_payment enable row level security;
create policy reference_payment_scope on payment.reference_payment
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and payer_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and payer_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

-- Casos para o financeiro (S5-02, S5-04, S5-10; ecrãs e resolução no S5-10 e S5-12). Uma devolução
-- decidida aqui faz-se sempre fora da plataforma: a MoSeguros não movimenta dinheiro.
create table payment.payment_exception (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  insurer_id           uuid not null,
  type                 varchar(25) not null
                       check (type in ('AMOUNT_MISMATCH','EXPIRED_REFERENCE','ALREADY_PAID','UNKNOWN_REFERENCE',
                                       'CANCELLED_REFERENCE','TARGET_NOT_PAYABLE','UNRESOLVED_STATUS')),
  intent_id            uuid null references payment.payment_intent (id),
  message_id           varchar(64) null unique,
  source_id            uuid null,
  file_name            varchar(200) null,
  line_no              int null,
  entity               varchar(5) null,
  reference            varchar(11) null,
  amount               bigint null,
  paid_at              timestamptz null,
  bank_transaction_id  varchar(60) null,
  detail               varchar(60) null,
  status               varchar(10) not null default 'OPEN' check (status in ('OPEN','RESOLVED')),
  resolution           varchar(15) null
                       check (resolution in ('LINKED','REFUND','IGNORED','MARK_SUCCEEDED','MARK_FAILED')),
  linked_intent_id     uuid null references payment.payment_intent (id),
  resolved_by          uuid null,
  resolved_at          timestamptz null,
  note                 varchar(500) null,
  constraint ck_payment_exception_resolution check (status <> 'RESOLVED' or resolution is not null)
);

create index ix_payment_exception_insurer on payment.payment_exception (insurer_id, status, created_at desc);
create unique index ux_exception_intent_type on payment.payment_exception (intent_id, type)
  where type in ('TARGET_NOT_PAYABLE','UNRESOLVED_STATUS');

alter table payment.payment_exception enable row level security;
create policy payment_exception_scope on payment.payment_exception
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid));
