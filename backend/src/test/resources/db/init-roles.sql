-- Papéis de base de dados da MoSeguros (ADR-002, adenda de 01/11/2026).
-- Corre antes das migrações: Testcontainers (withInitScript) e docker compose
-- (/docker-entrypoint-initdb.d, só com o volume vazio). Conteúdo igual em
-- backend/src/test/resources/db/init-roles.sql e infra/postgres/init/01-roles.sql.
-- A aplicação liga-se como moseguros_app_user: sem SUPERUSER, sem BYPASSRLS e sem ser
-- dona de nenhuma tabela, para que o Row Level Security se aplique a ela.
create role moseguros_app nologin;
create role moseguros_app_user login password 'moseguros_app' in role moseguros_app;
