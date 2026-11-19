-- ShedLock (S6-01 §2; modelo transversal §7): uma só execução de cada job agendado entre instâncias.
-- Tabela de infra-estrutura da biblioteca (como a event_publication): sem as colunas obrigatórias nem
-- RLS (AGENTS.md §5). Corre como dono.

-- Idempotentes: o schema já é criado pela V202701010200 (S4-02); repetidas para não depender da ordem.
create schema if not exists shared;
grant usage on schema shared to moseguros_app;

create table if not exists shared.shedlock (
  name       varchar(64)  primary key,
  lock_until timestamptz  not null,
  locked_at  timestamptz  not null,
  locked_by  varchar(255) not null
);

grant select, insert, update on shared.shedlock to moseguros_app;
