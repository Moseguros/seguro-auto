-- Tabelas de referência, parâmetros da plataforma e formatos de matrícula (S2-02).
-- Corre como dono. Dados de referência da plataforma, sem dados de seguradora nem pessoais:
-- sem RLS (como identity.role). A escrita exige reference:manage no serviço. Sem delete.

grant usage on schema catalog to moseguros_app;
alter default privileges in schema catalog grant select, insert, update on tables to moseguros_app;

create table catalog.reference_value (
  id            uuid primary key,
  created_at    timestamptz not null,
  created_by    varchar(64) not null,
  updated_at    timestamptz not null,
  updated_by    varchar(64) not null,
  version       bigint not null default 0,
  domain        varchar(30) not null
                check (domain in ('VEHICLE_MAKE','VEHICLE_MODEL','LICENCE_CATEGORY')),
  -- não muda depois de criado
  code          varchar(40) not null check (code ~ '^[A-Z0-9_]{1,40}$'),
  label_pt      varchar(120) not null,
  label_en      varchar(120) null,  -- null: os ecrãs mostram label_pt
  parent_code   varchar(40) null,   -- só em VEHICLE_MODEL: código da marca
  sort_order    int not null default 100 check (sort_order between 0 and 9999),
  active        boolean not null default true,
  constraint uq_reference_value_domain_code unique (domain, code),
  constraint ck_reference_value_parent check ((domain = 'VEHICLE_MODEL') = (parent_code is not null))
);

create index ix_reference_value_parent on catalog.reference_value (domain, parent_code);

-- Chaves novas só por migração; a API só altera valores. Nunca há segredos nesta tabela.
create table catalog.platform_setting (
  id            uuid primary key,
  created_at    timestamptz not null,
  created_by    varchar(64) not null,
  updated_at    timestamptz not null,
  updated_by    varchar(64) not null,
  version       bigint not null default 0,
  key           varchar(60) not null unique check (key ~ '^[A-Z][A-Z0-9_]{1,59}$'),
  value_type    varchar(10) not null check (value_type in ('MONEY','INTEGER','BOOLEAN','STRING')),
  value         varchar(500) not null  -- MONEY em centavos, como texto
);

create table catalog.plate_format (
  id                uuid primary key,
  created_at        timestamptz not null,
  created_by        varchar(64) not null,
  updated_at        timestamptz not null,
  updated_by        varchar(64) not null,
  version           bigint not null default 0,
  code              varchar(20) not null unique check (code ~ '^[A-Z0-9_]{2,20}$'),  -- não muda
  label             varchar(80) not null,
  pattern           varchar(200) not null,   -- expressão regular Java, aplicada ao valor normalizado
  display_template  varchar(100) not null,   -- sintaxe {grupo} (S0-03)
  example           varchar(20) not null,    -- valor formatado de exemplo
  sort_order        int not null,            -- ordem de teste no PlateParser
  active            boolean not null default true
);
