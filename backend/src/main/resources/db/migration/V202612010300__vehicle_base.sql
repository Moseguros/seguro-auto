-- Veículos e condutores do cliente (S2-03 §1). Corre como dono. O papel moseguros_app lê, insere
-- e altera; só apaga em vehicle.vehicle_driver (a lista de condutores de um veículo é substituída
-- por inteiro). Veículos e condutores retirados ficam com status = 'REMOVED'.

grant usage on schema vehicle to moseguros_app;
alter default privileges in schema vehicle grant select, insert, update on tables to moseguros_app;

create table vehicle.vehicle (
  id                        uuid primary key,
  created_at                timestamptz not null,
  created_by                varchar(64) not null,
  updated_at                timestamptz not null,
  updated_by                varchar(64) not null,
  version                   bigint not null default 0,
  -- conta CUSTOMER (identity.user_account), noutro schema: sem FK
  owner_user_id             uuid not null,
  plate                     varchar(15) not null,   -- valor formatado de Matricula (ABC-123-MC)
  plate_format_code         varchar(20) not null,   -- Matricula.formatCode()
  chassis_number            varchar(30) not null check (chassis_number ~ '^[A-Z0-9]{5,30}$'),
  make                      varchar(60) not null,
  model                     varchar(60) not null,
  year                      int not null check (year >= 1900),
  engine_cc                 int null check (engine_cc between 1 and 30000),
  category                  varchar(20) not null
                            check (category in ('LIGHT_PASSENGER','LIGHT_MIXED','HEAVY','MOTORCYCLE','TRAILER','BUS')),
  seats                     int not null check (seats between 0 and 100),
  gross_weight_kg           int null check (gross_weight_kg between 1 and 100000),
  usage                     varchar(25) not null
                            check (usage in ('PRIVATE','RENTAL','PASSENGER_TRANSPORT','GOODS','TAXI','DRIVING_SCHOOL')),
  province_of_circulation   varchar(20) not null
                            check (province_of_circulation in ('MAPUTO_CITY','MAPUTO','GAZA','INHAMBANE','SOFALA',
                                   'MANICA','TETE','ZAMBEZIA','NAMPULA','CABO_DELGADO','NIASSA')),
  -- ficheiro actual do livrete (document.file), noutro schema: sem FK
  registration_doc_file_id  uuid null,
  commercial_value_amount   bigint null check (commercial_value_amount > 0),  -- centavos (MZN)
  has_tracker               boolean not null default false,
  inatro_verified_at        timestamptz null,
  status                    varchar(10) not null default 'ACTIVE' check (status in ('ACTIVE','REMOVED')),
  removed_at                timestamptz null,
  check ((status = 'REMOVED') = (removed_at is not null))
);

create unique index ux_vehicle_owner_plate on vehicle.vehicle (owner_user_id, plate) where status = 'ACTIVE';
create index ix_vehicle_owner_created on vehicle.vehicle (owner_user_id, created_at desc);

create table vehicle.driver (
  id                     uuid primary key,
  created_at             timestamptz not null,
  created_by             varchar(64) not null,
  updated_at             timestamptz not null,
  updated_by             varchar(64) not null,
  version                bigint not null default 0,
  owner_user_id          uuid not null,             -- cliente que registou o condutor
  full_name              varchar(120) not null,
  birth_date             date not null,
  licence_number_enc     bytea not null,            -- n.º da carta cifrado (CryptoService)
  licence_number_hash    varchar(64) not null,      -- BlindIndex("licence", valor normalizado)
  licence_number_last4   varchar(4) not null,       -- para a máscara ****1234
  licence_categories     text[] not null check (cardinality(licence_categories) between 1 and 12),
  licence_issue_date     date not null,
  is_owner               boolean not null default false,
  status                 varchar(10) not null default 'ACTIVE' check (status in ('ACTIVE','REMOVED')),
  removed_at             timestamptz null,
  check ((status = 'REMOVED') = (removed_at is not null))
);

create unique index ux_driver_owner_licence on vehicle.driver (owner_user_id, licence_number_hash)
  where status = 'ACTIVE';
create unique index ux_driver_owner_self on vehicle.driver (owner_user_id) where is_owner and status = 'ACTIVE';
create index ix_driver_licence_hash on vehicle.driver (licence_number_hash);
create index ix_driver_owner_created on vehicle.driver (owner_user_id, created_at desc);

-- Condutores de cada veículo: tem is_habitual, por isso não é uma tabela de associação pura.
create table vehicle.vehicle_driver (
  id           uuid primary key,
  created_at   timestamptz not null,
  created_by   varchar(64) not null,
  updated_at   timestamptz not null,
  updated_by   varchar(64) not null,
  version      bigint not null default 0,
  vehicle_id   uuid not null references vehicle.vehicle (id),
  driver_id    uuid not null references vehicle.driver (id),
  is_habitual  boolean not null,
  unique (vehicle_id, driver_id)
);

create unique index ux_vehicle_habitual on vehicle.vehicle_driver (vehicle_id) where is_habitual;
create index ix_vehicle_driver_driver on vehicle.vehicle_driver (driver_id);

-- Excepção justificada (S2-03 §1): a lista é substituída por inteiro; não é apólice, pagamento,
-- sinistro nem reclamação.
grant delete on vehicle.vehicle_driver to moseguros_app;

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda).
-- O âmbito ORG não vê estas tabelas: os sprints seguintes usam snapshots (VehicleApi.snapshot).
alter table vehicle.vehicle enable row level security;
create policy vehicle_scope on vehicle.vehicle
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and owner_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and owner_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

alter table vehicle.driver enable row level security;
create policy driver_scope on vehicle.driver
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and owner_user_id = nullif(current_setting('app.user_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and owner_user_id = nullif(current_setting('app.user_id', true), '')::uuid));

alter table vehicle.vehicle_driver enable row level security;
create policy vehicle_driver_scope on vehicle.vehicle_driver
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and exists (select 1 from vehicle.vehicle v
                     where v.id = vehicle_id
                       and v.owner_user_id = nullif(current_setting('app.user_id', true), '')::uuid)))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'CUSTOMER'
        and exists (select 1 from vehicle.vehicle v
                     where v.id = vehicle_id
                       and v.owner_user_id = nullif(current_setting('app.user_id', true), '')::uuid)));
