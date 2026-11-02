-- Papel da aplicação (ADR-002 e adenda de 01/11/2026). Corre como dono das tabelas.
-- A aplicação liga-se com moseguros_app_user (membro de moseguros_app), que não é dono
-- de nenhuma tabela: só assim o Row Level Security se aplica a ela.
do $$ begin
  if not exists (select 1 from pg_roles where rolname = 'moseguros_app') then
    create role moseguros_app nologin;
  end if;
end $$;

-- schemas já criados em V202610311200
grant usage on schema audit, identity, organization to moseguros_app;
grant select, insert on audit.audit_event to moseguros_app;
grant select, insert, update, delete on public.event_publication to moseguros_app; -- o Spring Modulith apaga publicações concluídas
alter default privileges in schema identity, organization grant select, insert, update on tables to moseguros_app;
