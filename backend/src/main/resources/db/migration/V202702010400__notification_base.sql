-- Notificações gerais (S6-04 §2; modelo transversal §1.1, §2): modelos por código, canal e língua, as
-- mensagens enviadas (uma linha por canal e destino, mais a linha INBOX do centro de notificações), as
-- preferências do cliente e os dispositivos para push. Corre como dono. O grant usage on schema
-- notification já existe (V202611031000, S1-03). Nenhuma migração insere textos de modelos (AGENTS.md
-- §4, decisão D7): o TemplateSeeder carrega-os de messages/notifications_{pt,en}.properties no arranque.

-- Modelos: o texto por defeito vem do ficheiro; o administrador pode sobrepô-lo (customized = true).
create table notification.template (
  id                      uuid primary key,
  created_at              timestamptz not null,
  created_by              varchar(64) not null,
  updated_at              timestamptz not null,
  updated_by              varchar(64) not null,
  version                 bigint not null default 0,
  code                    varchar(60) not null check (code ~ '^[A-Z][A-Z0-9_]{2,59}$'),
  channel                 varchar(10) not null check (channel in ('SMS','WHATSAPP','EMAIL','PUSH')),
  locale                  varchar(5) not null check (locale in ('pt','en')),
  subject                 varchar(200) null,
  body                    text not null check (char_length(body) <= 4000),
  whatsapp_template_name  varchar(100) null,
  whatsapp_params         text[] null,
  variables               text[] not null,
  sample_params           jsonb not null,
  active                  boolean not null default true,
  customized              boolean not null default false,
  default_hash            varchar(64) not null,
  constraint ck_template_subject check (channel not in ('EMAIL','PUSH') or subject is not null),
  constraint ck_template_whatsapp check (channel <> 'WHATSAPP' or whatsapp_template_name is not null),
  constraint ux_template_code_channel_locale unique (code, channel, locale)
);

-- Mensagens: o texto enviado por SMS, WhatsApp, e-mail ou push não se grava (gera-se de novo a partir de
-- params e do modelo); só a linha INBOX guarda o título e o texto mostrados no centro de notificações.
create table notification.message (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  notification_id      uuid not null,
  user_id              uuid not null,                 -- identity.user_account, noutro schema: sem FK
  template_code        varchar(60) not null,
  category             varchar(20) not null,          -- NotificationCategory (cresce nos sprints seguintes)
  channel              varchar(10) not null check (channel in ('SMS','WHATSAPP','EMAIL','PUSH','INBOX')),
  locale               varchar(5) not null,
  params               jsonb not null,
  title                varchar(200) null,
  body                 text null,
  data                 jsonb null,
  device_id            uuid null,
  destination_masked   varchar(254) null,
  destination_hash     varchar(64) null,
  status               varchar(10) not null
                       check (status in ('QUEUED','SENDING','SENT','DELIVERED','FAILED','SKIPPED')),
  attempts             int not null default 0 check (attempts >= 0),
  next_attempt_at      timestamptz null,
  last_error           varchar(200) null,
  provider             varchar(30) null,
  provider_message_id  varchar(200) null,
  sent_at              timestamptz null,
  delivered_at         timestamptz null,
  read_at              timestamptz null,
  constraint ck_message_inbox check (channel <> 'INBOX' or (title is not null and body is not null)),
  constraint ck_message_device check (channel <> 'PUSH' or device_id is not null),
  constraint ck_message_read check (read_at is null or channel = 'INBOX')
);

create index ix_message_queue on notification.message (next_attempt_at) where status = 'QUEUED';
create index ix_message_inbox on notification.message (user_id, created_at desc) where channel = 'INBOX';
create index ix_message_notification on notification.message (notification_id);
create index ix_message_provider_id on notification.message (provider, provider_message_id)
  where provider_message_id is not null;
create index ix_message_sending on notification.message (updated_at) where status = 'SENDING';

-- Preferências: sem linha = activo.
create table notification.preference (
  id          uuid primary key,
  created_at  timestamptz not null,
  created_by  varchar(64) not null,
  updated_at  timestamptz not null,
  updated_by  varchar(64) not null,
  version     bigint not null default 0,
  user_id     uuid not null,
  category    varchar(20) not null,
  channel     varchar(10) not null check (channel in ('SMS','WHATSAPP','EMAIL','PUSH')),
  enabled     boolean not null,
  constraint ux_preference_user_category_channel unique (user_id, category, channel)
);

-- Dispositivos para push: o token FCM nunca vai para logs nem para respostas. Um token só pode estar activo
-- num dispositivo (ux_device_token_active): quando outro cliente o regista, a linha antiga fica inactiva
-- (REASSIGNED) e cria-se uma nova (S6-04 §8; decisão nas Questões da ficha).
create table notification.device (
  id                  uuid primary key,
  created_at          timestamptz not null,
  created_by          varchar(64) not null,
  updated_at          timestamptz not null,
  updated_by          varchar(64) not null,
  version             bigint not null default 0,
  user_id             uuid not null,
  platform            varchar(10) not null check (platform in ('ANDROID','IOS')),
  token               varchar(512) not null,
  app_version         varchar(30) null,
  active              boolean not null default true,
  last_seen_at        timestamptz not null,
  deactivated_at      timestamptz null,
  deactivated_reason  varchar(20) null check (deactivated_reason in ('UNREGISTERED','INVALID_TOKEN','REASSIGNED')),
  constraint ck_device_deactivated check (active or deactivated_reason is not null)
);

create unique index ux_device_token_active on notification.device (token) where active;
create index ix_device_token on notification.device (token);
create index ix_device_user on notification.device (user_id) where active;

grant select, insert, update on notification.template, notification.message, notification.preference,
  notification.device to moseguros_app;

-- Row Level Security (sem "force"; ADR-002, adenda). Os modelos só em SYSTEM/PLATFORM; as mensagens, as
-- preferências e os dispositivos também do próprio cliente (app.user_id).
alter table notification.template enable row level security;
create policy template_scope on notification.template
  for all to moseguros_app
  using (current_setting('app.scope', true) in ('SYSTEM','PLATFORM'))
  with check (current_setting('app.scope', true) in ('SYSTEM','PLATFORM'));

alter table notification.message enable row level security;
create policy message_scope on notification.message
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid));

alter table notification.preference enable row level security;
create policy preference_scope on notification.preference
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid));

alter table notification.device enable row level security;
create policy device_scope on notification.device
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and user_id = nullif(current_setting('app.user_id', true), '')::uuid));
