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

-- S2-04 ---------------------------------------------------------------------------------------
-- Dois produtos publicados da Seguradora Demo (valores fictícios), cada um com a versão 1 em
-- vigor desde 2026-12-01, as coberturas e os dois documentos legais em pt. Os PDFs estão em
-- db/dev/files/ (cópias de dev-placeholder.pdf; o DevSeedFilesRunner copia-os para o
-- armazenamento em dev-seed/<nome>). Sem logótipo: a Seguradora Demo aparece com as iniciais.

insert into catalog.product
  (id, created_at, created_by, updated_at, updated_by, insurer_id, code, name, type, description, status)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', o.id, v.code, v.name, v.type, v.description, 'PUBLISHED'
  from organization.organization o
  cross join (values
    ('01930000-0000-7000-8000-000000000201', 'RC-AUTO', 'RC Automóvel Demo', 'MTPL_ONLY',
     'Seguro obrigatório de responsabilidade civil automóvel (demonstração).'),
    ('01930000-0000-7000-8000-000000000202', 'AUTO-COMPLETO', 'Auto Completo Demo', 'COMPREHENSIVE',
     'Responsabilidade civil e danos próprios (demonstração).')
  ) as v(id, code, name, type, description)
 where o.id = '01930000-0000-7000-8000-000000000001'
on conflict do nothing;

insert into catalog.product_version
  (id, created_at, created_by, updated_at, updated_by, product_id, insurer_id, version_no, status,
   vehicle_categories, usages, change_notes, valid_from, published_at, published_by)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', p.id, p.insurer_id, 1, 'PUBLISHED',
       v.categories::text[], '{PRIVATE}', 'Versão inicial', date '2026-12-01',
       timestamptz '2026-12-01T00:00:00Z', 'dev-seed'
  from catalog.product p
  join (values
    ('01930000-0000-7000-8000-000000000211', '01930000-0000-7000-8000-000000000201',
     '{LIGHT_PASSENGER,LIGHT_MIXED,MOTORCYCLE}'),
    ('01930000-0000-7000-8000-000000000212', '01930000-0000-7000-8000-000000000202',
     '{LIGHT_PASSENGER,LIGHT_MIXED}')
  ) as v(id, product_id, categories) on p.id = v.product_id::uuid
on conflict do nothing;

insert into catalog.coverage
  (id, created_at, created_by, updated_at, updated_by, product_version_id, insurer_id, code, mandatory,
   requires_codes, capital_type, capital_amount, capital_options_amount, deductible_type, deductible_amount,
   deductible_percent, deductible_min_amount, sort_order)
select c.id::uuid, now(), 'dev-seed', now(), 'dev-seed', pv.id, pv.insurer_id, c.code, c.mandatory,
       c.requires::text[], c.capital_type, c.capital, c.options::bigint[], c.deductible_type, c.deductible,
       c.percent, c.min_amount, c.sort_order
  from catalog.product_version pv
  join (values
    -- RC Automóvel Demo, v1
    ('01930000-0000-7000-8000-000000000221', '01930000-0000-7000-8000-000000000211', 'MTPL', true, '{}',
     'FIXED', 300000000::bigint, null::text, 'NONE', null::bigint, null::numeric, null::bigint, 0),
    -- Auto Completo Demo, v1
    ('01930000-0000-7000-8000-000000000231', '01930000-0000-7000-8000-000000000212', 'MTPL', true, '{}',
     'FIXED', 300000000, null, 'NONE', null, null, null, 0),
    ('01930000-0000-7000-8000-000000000232', '01930000-0000-7000-8000-000000000212', 'VTPL', false, '{}',
     'OPTIONS', null, '{500000000,1000000000}', 'NONE', null, null, null, 1),
    ('01930000-0000-7000-8000-000000000233', '01930000-0000-7000-8000-000000000212', 'OD_COLLISION', false, '{}',
     'VEHICLE_VALUE', null, null, 'PERCENT', null, 10.00, 2500000, 2),
    ('01930000-0000-7000-8000-000000000234', '01930000-0000-7000-8000-000000000212', 'OD_FIRE', false, '{}',
     'VEHICLE_VALUE', null, null, 'PERCENT', null, 10.00, 2500000, 3),
    ('01930000-0000-7000-8000-000000000235', '01930000-0000-7000-8000-000000000212', 'OD_NATURE', false, '{}',
     'VEHICLE_VALUE', null, null, 'PERCENT', null, 10.00, 2500000, 4),
    ('01930000-0000-7000-8000-000000000236', '01930000-0000-7000-8000-000000000212', 'OD_GLASS', false, '{}',
     'VEHICLE_VALUE', null, null, 'FIXED', 500000, null, null, 5),
    ('01930000-0000-7000-8000-000000000237', '01930000-0000-7000-8000-000000000212', 'OD_THEFT', false,
     '{OD_COLLISION,OD_FIRE,OD_NATURE}', 'VEHICLE_VALUE', null, null, 'PERCENT', null, 20.00, 5000000, 6),
    ('01930000-0000-7000-8000-000000000238', '01930000-0000-7000-8000-000000000212', 'OCCUPANTS', false, '{}',
     'PER_PERSON', 50000000, null, 'NONE', null, null, null, 7),
    ('01930000-0000-7000-8000-000000000239', '01930000-0000-7000-8000-000000000212', 'ASSISTANCE', false, '{}',
     'FIXED', 2500000, null, 'NONE', null, null, null, 8)
  ) as c(id, version_id, code, mandatory, requires, capital_type, capital, options, deductible_type, deductible,
         percent, min_amount, sort_order) on pv.id = c.version_id::uuid
on conflict do nothing;

-- Ficheiros dos documentos: size_bytes e sha256 são os do PDF commitado (cópias de
-- dev-placeholder.pdf, 645 bytes).
insert into document.file
  (id, created_at, created_by, updated_at, updated_by, owner_type, owner_id, purpose, insurer_id,
   original_filename, content_type, size_bytes, sha256, storage_key, classification, linked_at)
select f.id::uuid, now(), 'dev-seed', now(), 'dev-seed', 'PRODUCT_VERSION', pv.id, f.purpose, pv.insurer_id,
       f.name, 'application/pdf', 645, 'be438c6ab2c994997e338413ffc2fb428ec0480be5b2a1dd1a0c2488372dbe4c',
       'dev-seed/' || f.name, 'PUBLIC', now()
  from catalog.product_version pv
  join (values
    ('01930000-0000-7000-8000-000000000241', '01930000-0000-7000-8000-000000000211', 'GENERAL_CONDITIONS',
     'rc-auto-condicoes-gerais.pdf'),
    ('01930000-0000-7000-8000-000000000242', '01930000-0000-7000-8000-000000000211', 'PRE_CONTRACT_INFO',
     'rc-auto-informacao-pre-contratual.pdf'),
    ('01930000-0000-7000-8000-000000000243', '01930000-0000-7000-8000-000000000212', 'GENERAL_CONDITIONS',
     'auto-completo-condicoes-gerais.pdf'),
    ('01930000-0000-7000-8000-000000000244', '01930000-0000-7000-8000-000000000212', 'PRE_CONTRACT_INFO',
     'auto-completo-informacao-pre-contratual.pdf')
  ) as f(id, version_id, purpose, name) on pv.id = f.version_id::uuid
on conflict do nothing;

insert into catalog.product_document
  (id, created_at, created_by, updated_at, updated_by, product_version_id, insurer_id, doc_type, language, file_id)
select d.id::uuid, now(), 'dev-seed', now(), 'dev-seed', f.owner_id, f.insurer_id, f.purpose, 'pt', f.id
  from document.file f
  join (values
    ('01930000-0000-7000-8000-000000000251', '01930000-0000-7000-8000-000000000241'),
    ('01930000-0000-7000-8000-000000000252', '01930000-0000-7000-8000-000000000242'),
    ('01930000-0000-7000-8000-000000000253', '01930000-0000-7000-8000-000000000243'),
    ('01930000-0000-7000-8000-000000000254', '01930000-0000-7000-8000-000000000244')
  ) as d(id, file_id) on f.id = d.file_id::uuid
on conflict do nothing;
