-- Dados de demonstração do Sprint 2 (só perfil local: spring.flyway.locations inclui db/dev).
-- Repetível e idempotente: ids fixos e "on conflict do nothing". Corre como dono (o RLS não se
-- aplica). Uma secção por tarefa; cada tarefa só acrescenta a sua. Nunca vai para staging/prod.
-- "on conflict do nothing" sem alvo também cobre um NUIT, código ou telemóvel já criado à mão
-- numa BD local; as linhas que dependem de outra só entram se essa existir com o id fixo.

-- S2-01 ---------------------------------------------------------------------------------------

-- Seguradora Demo (DEMO) e o seu perfil de seguradora
insert into organization.organization
  (id, created_at, created_by, updated_at, updated_by, type, legal_name, trade_name, nuit,
   license_number, status, contact_email, province)
values
  ('01930000-0000-7000-8000-000000000001', now(), 'dev-seed', now(), 'dev-seed', 'INSURER',
   'Seguradora Demo, SA', 'Seguradora Demo', '400000001', 'ASFPM-DEMO-001', 'ACTIVE',
   'demo@seguradora.local', 'MAPUTO_CITY')
on conflict do nothing;

insert into organization.insurer_profile
  (id, created_at, created_by, updated_at, updated_by, organization_id, short_code, complaints_email)
select '01930000-0000-7000-8000-000000000002', now(), 'dev-seed', now(), 'dev-seed',
       o.id, 'DEMO', 'reclamacoes@seguradora.local'
  from organization.organization o
 where o.id = '01930000-0000-7000-8000-000000000001'
on conflict do nothing;

-- Corretora Demo
insert into organization.organization
  (id, created_at, created_by, updated_at, updated_by, type, legal_name, nuit, license_number,
   status, contact_email)
values
  ('01930000-0000-7000-8000-000000000003', now(), 'dev-seed', now(), 'dev-seed', 'BROKER',
   'Corretora Demo, Lda', '400000002', 'ASFPM-DEMO-002', 'ACTIVE', 'demo@corretora.local')
on conflict do nothing;

-- Convites: activar em http://localhost:5173/convite/dev-convite-gestor-demo
--                   e http://localhost:5173/convite/dev-convite-corretor-demo
insert into identity.invitation
  (id, created_at, created_by, updated_at, updated_by, email, full_name, organization_id,
   role_codes, token_hash, expires_at)
values
  ('01930000-0000-7000-8000-000000000004', now(), 'dev-seed', now(), 'dev-seed',
   'gestor@demo.local', 'Gestor Demo', '01930000-0000-7000-8000-000000000001',
   '{INSURER_ADMIN,PRODUCT_MANAGER}', encode(sha256('dev-convite-gestor-demo'::bytea), 'hex'),
   '2099-12-31T00:00:00Z'),
  ('01930000-0000-7000-8000-000000000005', now(), 'dev-seed', now(), 'dev-seed',
   'corretor@demo.local', 'Corretor Demo', '01930000-0000-7000-8000-000000000003',
   '{ORG_ADMIN,BROKER}', encode(sha256('dev-convite-corretor-demo'::bytea), 'hex'),
   '2099-12-31T00:00:00Z')
on conflict do nothing;

-- Cliente de teste +258840000001 (entra por OTP; código no cabeçalho X-Debug-Otp). Sem
-- documento nem NUIT: são cifrados (CryptoService) e não se geram em SQL.
insert into identity.user_account
  (id, created_at, created_by, updated_at, updated_by, type, msisdn, msisdn_verified, status)
values
  ('01930000-0000-7000-8000-000000000006', now(), 'dev-seed', now(), 'dev-seed', 'CUSTOMER',
   '258840000001', true, 'ACTIVE')
on conflict do nothing;

insert into identity.customer_profile
  (id, created_at, created_by, updated_at, updated_by, user_id, full_name, birth_date, province)
select '01930000-0000-7000-8000-000000000007', now(), 'dev-seed', now(), 'dev-seed',
       a.id, 'Cliente Teste', date '1990-01-01', 'MAPUTO_CITY'
  from identity.user_account a
 where a.id = '01930000-0000-7000-8000-000000000006'
on conflict do nothing;

insert into identity.user_role (user_id, role_code, created_at, created_by)
select a.id, 'CUSTOMER', now(), 'dev-seed'
  from identity.user_account a
 where a.id = '01930000-0000-7000-8000-000000000006'
on conflict do nothing;

-- S2-03 ---------------------------------------------------------------------------------------

-- Veículo do cliente de teste: AAA-100-MC com os dados do registo simulado (MockVehicleRegistry).
-- Sem livrete nem condutores (o número da carta é cifrado e não se gera em SQL).
insert into vehicle.vehicle
  (id, created_at, created_by, updated_at, updated_by, owner_user_id, plate, plate_format_code,
   chassis_number, make, model, year, engine_cc, category, seats, usage, province_of_circulation,
   commercial_value_amount, has_tracker, inatro_verified_at)
select '01930000-0000-7000-8000-000000000101', now(), 'dev-seed', now(), 'dev-seed', a.id,
       'AAA-100-MC', 'STANDARD', 'JTDBR32E720012345', 'Toyota', 'Corolla', 2018, 1598,
       'LIGHT_PASSENGER', 5, 'PRIVATE', 'MAPUTO_CITY', 85000000, false, now()
  from identity.user_account a
 where a.id = '01930000-0000-7000-8000-000000000006'
on conflict do nothing;
