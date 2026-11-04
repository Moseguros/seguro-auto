-- Numeração de negócio partilhada (S4-02 §1; modelo transversal §4). Corre como dono.
--
-- O schema shared não foi criado no S0-03 (V202610311200 só criou audit e os schemas dos
-- módulos), apesar da decisão D14: cria-o esta migração. As fichas seguintes que usam o schema
-- shared (idempotency_record no S5-02, shedlock no S6) dependem desta.
--
-- Tabela de infra-estrutura: sem as colunas obrigatórias (AGENTS.md §5, D14) e sem RLS
-- (contadores globais, sem dados de ninguém). Sem delete para a aplicação.

create schema if not exists shared;
grant usage on schema shared to moseguros_app;

create table shared.business_sequence (
  prefix     varchar(4) not null,   -- PR, PG, AP, CT, EN, SN, RC, AS, EX, SIMO
  year       int        not null,   -- ano em Africa/Maputo
  last_value bigint     not null,   -- último número atribuído
  primary key (prefix, year)
);

grant select, insert, update on shared.business_sequence to moseguros_app;
