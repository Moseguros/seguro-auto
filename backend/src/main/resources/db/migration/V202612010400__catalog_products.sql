-- Produtos, versões, coberturas e documentos legais das seguradoras (S2-04).
-- Corre como dono. select/insert/update vêm dos "default privileges" do schema catalog (S2-02).
-- insurer_id em todas as tabelas (também nas filhas) para o RLS não precisar de join; é o id da
-- organização seguradora (organization.organization), sem FK por ser outro schema.

create table catalog.product (
  id           uuid primary key,
  created_at   timestamptz not null,
  created_by   varchar(64) not null,
  updated_at   timestamptz not null,
  updated_by   varchar(64) not null,
  version      bigint not null default 0,
  insurer_id   uuid not null,
  -- não muda depois de criado
  code         varchar(30) not null check (code ~ '^[A-Z0-9][A-Z0-9_-]{1,29}$'),
  name         varchar(120) not null,
  type         varchar(20) not null
               check (type in ('MTPL_ONLY','COMPREHENSIVE','TEMPORARY','BORDER','FLEET')),
  description  varchar(1000) null,
  status       varchar(10) not null default 'DRAFT' check (status in ('DRAFT','PUBLISHED','RETIRED')),
  retired_at   timestamptz null,
  constraint uq_product_insurer_code unique (insurer_id, code),
  constraint ck_product_retired check ((status = 'RETIRED') = (retired_at is not null))
);

create table catalog.product_version (
  id                   uuid primary key,
  created_at           timestamptz not null,
  created_by           varchar(64) not null,
  updated_at           timestamptz not null,
  updated_by           varchar(64) not null,
  version              bigint not null default 0,
  product_id           uuid not null references catalog.product (id),
  insurer_id           uuid not null,  -- igual ao do produto
  version_no           int not null check (version_no >= 1),
  status               varchar(12) not null default 'DRAFT'
                       check (status in ('DRAFT','PUBLISHED','SUPERSEDED','RETIRED')),
  vehicle_categories   text[] not null default '{}',  -- valores de VehicleCategory
  usages               text[] not null default '{}',  -- valores de VehicleUsage
  -- chaves reservadas: rating (S3), underwriting (S4), bonusMalus (S7); o S2-04 só guarda e copia
  rules                jsonb not null default '{}' check (jsonb_typeof(rules) = 'object'),
  change_notes         varchar(1000) null,
  based_on_version_id  uuid null references catalog.product_version (id),
  valid_from           date null,   -- preenchida na publicação
  valid_to             date null,   -- inclusiva; preenchida quando deixa de estar em vigor
  published_at         timestamptz null,
  published_by         varchar(64) null,  -- sub de quem publicou
  constraint uq_version_product_no unique (product_id, version_no),
  constraint ck_version_published check ((status = 'DRAFT') = (published_at is null))
);

create unique index ux_version_one_draft on catalog.product_version (product_id) where status = 'DRAFT';
create unique index ux_version_one_published on catalog.product_version (product_id) where status = 'PUBLISHED';
create index ix_version_published on catalog.product_version using gin (vehicle_categories) where status = 'PUBLISHED';

create table catalog.coverage (
  id                      uuid primary key,
  created_at              timestamptz not null,
  created_by              varchar(64) not null,
  updated_at              timestamptz not null,
  updated_by              varchar(64) not null,
  version                 bigint not null default 0,
  product_version_id      uuid not null references catalog.product_version (id),
  insurer_id              uuid not null,
  code                    varchar(20) not null
                          check (code in ('MTPL','VTPL','OD_COLLISION','OD_GLASS','OD_FIRE','OD_NATURE','OD_THEFT',
                                          'OCCUPANTS','ASSISTANCE','LEGAL','SRCC','CROSS_BORDER')),
  mandatory               boolean not null,  -- o cliente não pode retirar esta cobertura
  requires_codes          text[] not null default '{}',
  capital_type            varchar(15) not null check (capital_type in ('FIXED','VEHICLE_VALUE','OPTIONS','PER_PERSON')),
  capital_amount          bigint null,     -- FIXED (capital) e PER_PERSON (capital por pessoa), em centavos
  capital_options_amount  bigint[] null,   -- OPTIONS: valores que o cliente escolhe, em centavos
  deductible_type         varchar(10) not null check (deductible_type in ('NONE','FIXED','PERCENT')),
  deductible_amount       bigint null,     -- FIXED, em centavos
  deductible_percent      numeric(5,2) null,  -- PERCENT: percentagem do valor do prejuízo
  deductible_min_amount   bigint null,     -- PERCENT: mínimo da franquia (opcional), em centavos
  territorial_scope       text[] not null default '{MZ}',  -- ISO 3166-1 alfa-2
  description             varchar(1000) null,
  exclusions              varchar(4000) null,
  sort_order              int not null,
  constraint uq_coverage_version_code unique (product_version_id, code),
  check ((capital_type in ('FIXED','PER_PERSON')) = (capital_amount is not null)),
  check ((capital_type = 'OPTIONS') = (capital_options_amount is not null)),
  check ((deductible_type = 'FIXED') = (deductible_amount is not null)),
  check ((deductible_type = 'PERCENT') = (deductible_percent is not null)),
  check (deductible_type = 'PERCENT' or deductible_min_amount is null),
  check (deductible_percent is null or deductible_percent > 0 and deductible_percent <= 100)
);

create index ix_coverage_version on catalog.coverage (product_version_id);

create table catalog.product_document (
  id                  uuid primary key,
  created_at          timestamptz not null,
  created_by          varchar(64) not null,
  updated_at          timestamptz not null,
  updated_by          varchar(64) not null,
  version             bigint not null default 0,
  product_version_id  uuid not null references catalog.product_version (id),
  insurer_id          uuid not null,
  doc_type            varchar(20) not null
                      check (doc_type in ('GENERAL_CONDITIONS','PRE_CONTRACT_INFO','SPECIAL_CONDITIONS','OTHER')),
  language            varchar(2) not null default 'pt' check (language in ('pt','en')),
  title               varchar(120) null,  -- null: os ecrãs mostram o nome do tipo
  file_id             uuid not null       -- document.file, sem FK (outro schema)
);

create unique index ux_document_main on catalog.product_document (product_version_id, doc_type, language)
  where doc_type in ('GENERAL_CONDITIONS','PRE_CONTRACT_INFO');
create index ix_document_version on catalog.product_document (product_version_id);

-- Excepção ao "sem delete": só se apagam coberturas e documentos de versões DRAFT (configuração
-- em edição); o serviço garante-o. Produtos e versões nunca se apagam.
grant delete on catalog.coverage, catalog.product_document to moseguros_app;

-- Row Level Security (sem "force": o dono não é afectado; ver ADR-002, adenda).
-- s = current_setting('app.scope', true); i = app.insurer_id.
-- O catálogo público e o CatalogApi lêem em SYSTEM (CatalogSystemLookups) e filtram no código
-- pelas versões publicadas.
alter table catalog.product enable row level security;
create policy product_scope on catalog.product
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid));

alter table catalog.product_version enable row level security;
create policy product_version_scope on catalog.product_version
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid));

alter table catalog.coverage enable row level security;
create policy coverage_scope on catalog.coverage
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid));

alter table catalog.product_document enable row level security;
create policy product_document_scope on catalog.product_document
  for all to moseguros_app
  using (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid))
  with check (
    current_setting('app.scope', true) in ('SYSTEM','PLATFORM')
    or (current_setting('app.scope', true) = 'ORG'
        and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid));
