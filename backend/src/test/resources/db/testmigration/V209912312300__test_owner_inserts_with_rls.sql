-- Migração só de teste (OwnerMigrationIT): o dono, sem ser superutilizador, insere dados
-- iniciais em tabelas com RLS activo (sem "force row level security").
insert into organization.organization
  (id, created_at, created_by, updated_at, updated_by, type, legal_name, nuit, license_number, contact_email)
values
  ('019a4300-0000-7000-8000-00000000a001', now(), 'system', now(), 'system', 'REGULATOR',
   'Regulador de Teste', '999000111', null, 'geral@regulador.mz');

insert into identity.user_account
  (id, created_at, created_by, updated_at, updated_by, type, email, display_name, status, organization_id)
values
  ('019a4300-0000-7000-8000-00000000a002', now(), 'system', now(), 'system', 'STAFF',
   'admin@regulador.mz', 'Admin', 'PENDING_ACTIVATION', '019a4300-0000-7000-8000-00000000a001');
