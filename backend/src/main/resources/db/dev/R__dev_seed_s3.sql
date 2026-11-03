-- Dados de demonstração do Sprint 3 (só perfil local: spring.flyway.locations inclui db/dev).
-- Mesmas regras do R__dev_seed_s2.sql: ids fixos, "on conflict do nothing", dev-seed, valores
-- fictícios. Corre depois do R__dev_seed_s2.sql (o Flyway corre os repetíveis por ordem do nome).
-- As tarifas e os encargos são os de docs/tasks/S3-rating-cases.md (provisórios).
-- Gerado a partir de S3-rating-cases.md; ao mudar as tarifas, mudar também esse documento.

-- S3-01 ---------------------------------------------------------------------------------------

-- 1. Encargos da Seguradora Demo (secção 3 de S3-rating-cases.md). Só se ainda não tiver
--    encargos: não apaga os que se configurem à mão em /seguradora.
update organization.insurer_profile
   set charges = '[{"code":"DEMO_STAMP","labelPt":"Imposto de selo (provisório)","labelEn":"Stamp duty (provisional)","type":"PERCENT","value":"2.5"},{"code":"DEMO_FEE","labelPt":"Taxa fixa (provisória)","labelEn":"Fixed fee (provisional)","type":"FIXED","value":5000}]'::jsonb
 where id = '01930000-0000-7000-8000-000000000002' and charges = '[]'::jsonb;

-- 2. Tarifas dos produtos do S2: RC-AUTO (MTPL de tariff-dp.json) e AUTO-COMPLETO (tariff-dp.json
--    com OD_GLASS, OCCUPANTS e ASSISTANCE da secção 7 de S3-rating-cases.md).
update catalog.product_version
   set rules = rules || jsonb_build_object('rating', '{"schemaVersion":"rating-v1","coverages":[{"coverageCode":"MTPL","base":{"type":"AMOUNT","rows":[{"label":"Ligeiro de passageiros, particular","when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["PRIVATE"]}},"value":450000},{"label":"Ligeiro de passageiros, táxi ou aluguer","when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["TAXI","RENTAL"]}},"value":900000},{"label":"Ligeiro misto","when":{"vehicleCategory":{"in":["LIGHT_MIXED"]}},"value":600000},{"label":"Motociclo","when":{"vehicleCategory":{"in":["MOTORCYCLE"]}},"value":250000},{"label":"Pesado","when":{"vehicleCategory":{"in":["HEAVY"]}},"value":1500000}]},"factors":[{"code":"DRIVER_AGE","label":"Idade do condutor","rows":[{"when":{"driverAgeYears":{"lt":25}},"value":"1.30"},{"when":{"driverAgeYears":{"gte":25,"lt":70}},"value":"1.00"},{"when":{"driverAgeYears":{"gte":70}},"value":"1.15"}]},{"code":"LICENCE_YEARS","label":"Antiguidade da carta","rows":[{"when":{"licenceYears":{"lt":2}},"value":"1.25"},{"when":{"licenceYears":{"gte":2}},"value":"1.00"}]},{"code":"PROVINCE","label":"Província de circulação","rows":[{"when":{"province":{"in":["MAPUTO_CITY","MAPUTO"]}},"value":"1.10"},{"when":{},"value":"1.00"}]}]}]}'::jsonb)
 where id = '01930000-0000-7000-8000-000000000211';

update catalog.product_version
   set rules = rules || jsonb_build_object('rating', '{"schemaVersion":"rating-v1","coverages":[{"coverageCode":"MTPL","base":{"type":"AMOUNT","rows":[{"label":"Ligeiro de passageiros, particular","when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["PRIVATE"]}},"value":450000},{"label":"Ligeiro de passageiros, táxi ou aluguer","when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["TAXI","RENTAL"]}},"value":900000},{"label":"Ligeiro misto","when":{"vehicleCategory":{"in":["LIGHT_MIXED"]}},"value":600000},{"label":"Motociclo","when":{"vehicleCategory":{"in":["MOTORCYCLE"]}},"value":250000},{"label":"Pesado","when":{"vehicleCategory":{"in":["HEAVY"]}},"value":1500000}]},"factors":[{"code":"DRIVER_AGE","label":"Idade do condutor","rows":[{"when":{"driverAgeYears":{"lt":25}},"value":"1.30"},{"when":{"driverAgeYears":{"gte":25,"lt":70}},"value":"1.00"},{"when":{"driverAgeYears":{"gte":70}},"value":"1.15"}]},{"code":"LICENCE_YEARS","label":"Antiguidade da carta","rows":[{"when":{"licenceYears":{"lt":2}},"value":"1.25"},{"when":{"licenceYears":{"gte":2}},"value":"1.00"}]},{"code":"PROVINCE","label":"Província de circulação","rows":[{"when":{"province":{"in":["MAPUTO_CITY","MAPUTO"]}},"value":"1.10"},{"when":{},"value":"1.00"}]}]},{"coverageCode":"VTPL","base":{"type":"RATE_OF_CAPITAL","rows":[{"when":{"capital":{"lt":600000000}},"value":"0.0004"},{"when":{},"value":"0.0003"}]}},{"coverageCode":"OD_COLLISION","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"label":"Até 4 anos","when":{"vehicleAgeYears":{"lt":5}},"value":"0.025"},{"label":"5 a 10 anos","when":{"vehicleAgeYears":{"gte":5,"lt":11}},"value":"0.030"}]},"factors":[{"code":"DRIVER_AGE","label":"Idade do condutor","rows":[{"when":{"driverAgeYears":{"lt":25}},"value":"1.30"},{"when":{},"value":"1.00"}]},{"code":"TRACKER","label":"Localizador","rows":[{"when":{"hasTracker":true},"value":"0.90"},{"when":{},"value":"1.00"}]},{"code":"PROVINCE","label":"Província de circulação","rows":[{"when":{"province":{"in":["MAPUTO_CITY"]}},"value":"1.15"},{"when":{},"value":"1.00"}]}]},{"coverageCode":"OD_FIRE","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"when":{},"value":"0.0015"}]}},{"coverageCode":"OD_NATURE","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"when":{},"value":"0.0020"}]}},{"coverageCode":"OD_THEFT","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"when":{},"value":"0.0050"}]},"factors":[{"code":"TRACKER","label":"Localizador","rows":[{"when":{"hasTracker":true},"value":"0.70"},{"when":{},"value":"1.00"}]}]},{"coverageCode":"OD_GLASS","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"when":{},"value":"0.0010"}]}},{"coverageCode":"OCCUPANTS","base":{"type":"AMOUNT","rows":[{"label":"Até 5 lugares","when":{"seats":{"lt":6}},"value":150000},{"when":{},"value":250000}]}},{"coverageCode":"ASSISTANCE","base":{"type":"AMOUNT","rows":[{"when":{},"value":120000}]}}]}'::jsonb)
 where id = '01930000-0000-7000-8000-000000000212';

-- 3. Produto temporário da Seguradora Demo: TEMP-AUTO, versão 1 publicada desde 2026-12-15, com
--    os documentos do RC-AUTO (os mesmos ficheiros, como no createProductVersion).
insert into catalog.product
  (id, created_at, created_by, updated_at, updated_by, insurer_id, code, name, type, description, status)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', o.id, v.code, v.name, v.type, v.description, 'PUBLISHED'
  from organization.organization o
  cross join (values
    ('01930000-0000-7000-8000-000000000301', 'TEMP-AUTO', 'Temporário Demo', 'TEMPORARY',
     'Seguro temporário de responsabilidade civil automóvel (demonstração).')
  ) as v(id, code, name, type, description)
 where o.id = '01930000-0000-7000-8000-000000000001'
on conflict do nothing;

insert into catalog.product_version
  (id, created_at, created_by, updated_at, updated_by, product_id, insurer_id, version_no, status,
   vehicle_categories, usages, change_notes, valid_from, published_at, published_by)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', p.id, p.insurer_id, 1, 'PUBLISHED',
       v.categories::text[], '{PRIVATE}', 'Versão inicial', v.valid_from, v.published_at, 'dev-seed'
  from catalog.product p
  join (values
    ('01930000-0000-7000-8000-000000000311', '01930000-0000-7000-8000-000000000301', '{LIGHT_PASSENGER,LIGHT_MIXED,MOTORCYCLE}', date '2026-12-15', timestamptz '2026-12-15T00:00:00Z')
  ) as v(id, product_id, categories, valid_from, published_at) on p.id = v.product_id::uuid
on conflict do nothing;

insert into catalog.coverage
  (id, created_at, created_by, updated_at, updated_by, product_version_id, insurer_id, code, mandatory,
   requires_codes, capital_type, capital_amount, deductible_type, territorial_scope, sort_order)
select '01930000-0000-7000-8000-000000000312', now(), 'dev-seed', now(), 'dev-seed', pv.id, pv.insurer_id, 'MTPL', true,
       '{}', 'FIXED', 300000000, 'NONE', '{MZ}', 0
  from catalog.product_version pv
 where pv.id = '01930000-0000-7000-8000-000000000311'
on conflict do nothing;

insert into catalog.product_document
  (id, created_at, created_by, updated_at, updated_by, product_version_id, insurer_id, doc_type, language, file_id)
select d.id::uuid, now(), 'dev-seed', now(), 'dev-seed', pv.id, pv.insurer_id, f.purpose, 'pt', f.id
  from (values
    ('01930000-0000-7000-8000-000000000313', '01930000-0000-7000-8000-000000000311', '01930000-0000-7000-8000-000000000241'),
    ('01930000-0000-7000-8000-000000000314', '01930000-0000-7000-8000-000000000311', '01930000-0000-7000-8000-000000000242')
  ) as d(id, version_id, file_id)
  join catalog.product_version pv on pv.id = d.version_id::uuid
  join document.file f on f.id = d.file_id::uuid
on conflict do nothing;

update catalog.product_version
   set rules = rules || jsonb_build_object('rating', '{"schemaVersion":"rating-v1","durationFactors":[{"days":7,"factor":"0.06"},{"days":14,"factor":"0.10"},{"days":30,"factor":"0.18"},{"days":60,"factor":"0.32"},{"days":365,"factor":"1"}],"coverages":[{"coverageCode":"MTPL","base":{"type":"AMOUNT","rows":[{"when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["PRIVATE"]}},"value":450000},{"when":{"vehicleCategory":{"in":["LIGHT_MIXED"]}},"value":600000},{"when":{"vehicleCategory":{"in":["MOTORCYCLE"]}},"value":250000}]},"factors":[{"code":"DRIVER_AGE","label":"Idade do condutor","rows":[{"when":{"driverAgeYears":{"lt":25}},"value":"1.30"},{"when":{"driverAgeYears":{"gte":25,"lt":70}},"value":"1.00"},{"when":{"driverAgeYears":{"gte":70}},"value":"1.15"}]}]}]}'::jsonb)
 where id = '01930000-0000-7000-8000-000000000311';

-- 4. Segunda seguradora, para haver o que comparar (sem utilizadores), com os mesmos encargos.
insert into organization.organization
  (id, created_at, created_by, updated_at, updated_by, type, legal_name, trade_name, nuit,
   license_number, status, contact_email, province)
values
  ('01930000-0000-7000-8000-000000000320', now(), 'dev-seed', now(), 'dev-seed', 'INSURER',
   'Seguradora Demo B, SA', 'Seguradora Demo B', '400000010', 'ASFPM-DEMO-010', 'ACTIVE',
   'demob@seguradora.local', 'MAPUTO_CITY')
on conflict do nothing;

insert into organization.insurer_profile
  (id, created_at, created_by, updated_at, updated_by, organization_id, short_code, complaints_email, charges)
select '01930000-0000-7000-8000-000000000321', now(), 'dev-seed', now(), 'dev-seed', o.id, 'DEMOB', 'reclamacoes@demob.local',
       '[{"code":"DEMO_STAMP","labelPt":"Imposto de selo (provisório)","labelEn":"Stamp duty (provisional)","type":"PERCENT","value":"2.5"},{"code":"DEMO_FEE","labelPt":"Taxa fixa (provisória)","labelEn":"Fixed fee (provisional)","type":"FIXED","value":5000}]'::jsonb
  from organization.organization o
 where o.id = '01930000-0000-7000-8000-000000000320'
on conflict do nothing;

-- 5. Produtos da Seguradora Demo B: cópias de RC-AUTO, AUTO-COMPLETO e TEMP-AUTO (mesmos dados,
--    coberturas, elegibilidade e documentos), com as mesmas tarifas e, em cada cobertura, mais o
--    factor COMMERCIAL (1,08) no fim.
insert into catalog.product
  (id, created_at, created_by, updated_at, updated_by, insurer_id, code, name, type, description, status)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', o.id, v.code, v.name, v.type, v.description, 'PUBLISHED'
  from organization.organization o
  cross join (values
    ('01930000-0000-7000-8000-000000000331', 'RC-AUTO', 'RC Automóvel Demo', 'MTPL_ONLY',
     'Seguro obrigatório de responsabilidade civil automóvel (demonstração).'),
    ('01930000-0000-7000-8000-000000000332', 'AUTO-COMPLETO', 'Auto Completo Demo', 'COMPREHENSIVE',
     'Responsabilidade civil e danos próprios (demonstração).'),
    ('01930000-0000-7000-8000-000000000333', 'TEMP-AUTO', 'Temporário Demo', 'TEMPORARY',
     'Seguro temporário de responsabilidade civil automóvel (demonstração).')
  ) as v(id, code, name, type, description)
 where o.id = '01930000-0000-7000-8000-000000000320'
on conflict do nothing;

insert into catalog.product_version
  (id, created_at, created_by, updated_at, updated_by, product_id, insurer_id, version_no, status,
   vehicle_categories, usages, change_notes, valid_from, published_at, published_by)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', p.id, p.insurer_id, 1, 'PUBLISHED',
       v.categories::text[], '{PRIVATE}', 'Versão inicial', v.valid_from, v.published_at, 'dev-seed'
  from catalog.product p
  join (values
    ('01930000-0000-7000-8000-000000000341', '01930000-0000-7000-8000-000000000331', '{LIGHT_PASSENGER,LIGHT_MIXED,MOTORCYCLE}', date '2026-12-01', timestamptz '2026-12-01T00:00:00Z'),
    ('01930000-0000-7000-8000-000000000342', '01930000-0000-7000-8000-000000000332', '{LIGHT_PASSENGER,LIGHT_MIXED}', date '2026-12-01', timestamptz '2026-12-01T00:00:00Z'),
    ('01930000-0000-7000-8000-000000000343', '01930000-0000-7000-8000-000000000333', '{LIGHT_PASSENGER,LIGHT_MIXED,MOTORCYCLE}', date '2026-12-15', timestamptz '2026-12-15T00:00:00Z')
  ) as v(id, product_id, categories, valid_from, published_at) on p.id = v.product_id::uuid
on conflict do nothing;

insert into catalog.coverage
  (id, created_at, created_by, updated_at, updated_by, product_version_id, insurer_id, code, mandatory,
   requires_codes, capital_type, capital_amount, capital_options_amount, deductible_type, deductible_amount,
   deductible_percent, deductible_min_amount, territorial_scope, description, exclusions, sort_order)
select m.id::uuid, now(), 'dev-seed', now(), 'dev-seed', pv.id, pv.insurer_id, c.code, c.mandatory,
       c.requires_codes, c.capital_type, c.capital_amount, c.capital_options_amount, c.deductible_type,
       c.deductible_amount, c.deductible_percent, c.deductible_min_amount, c.territorial_scope, c.description,
       c.exclusions, c.sort_order
  from (values
    ('01930000-0000-7000-8000-000000000350', '01930000-0000-7000-8000-000000000221', '01930000-0000-7000-8000-000000000341'),
    ('01930000-0000-7000-8000-000000000351', '01930000-0000-7000-8000-000000000231', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000352', '01930000-0000-7000-8000-000000000232', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000353', '01930000-0000-7000-8000-000000000233', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000354', '01930000-0000-7000-8000-000000000234', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000355', '01930000-0000-7000-8000-000000000235', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000356', '01930000-0000-7000-8000-000000000236', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000357', '01930000-0000-7000-8000-000000000237', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000358', '01930000-0000-7000-8000-000000000238', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000359', '01930000-0000-7000-8000-000000000239', '01930000-0000-7000-8000-000000000342'),
    ('01930000-0000-7000-8000-000000000360', '01930000-0000-7000-8000-000000000312', '01930000-0000-7000-8000-000000000343')
  ) as m(id, source_id, version_id)
  join catalog.coverage c on c.id = m.source_id::uuid
  join catalog.product_version pv on pv.id = m.version_id::uuid
on conflict do nothing;

insert into catalog.product_document
  (id, created_at, created_by, updated_at, updated_by, product_version_id, insurer_id, doc_type, language, file_id)
select d.id::uuid, now(), 'dev-seed', now(), 'dev-seed', pv.id, pv.insurer_id, f.purpose, 'pt', f.id
  from (values
    ('01930000-0000-7000-8000-000000000371', '01930000-0000-7000-8000-000000000341', '01930000-0000-7000-8000-000000000241'),
    ('01930000-0000-7000-8000-000000000372', '01930000-0000-7000-8000-000000000341', '01930000-0000-7000-8000-000000000242'),
    ('01930000-0000-7000-8000-000000000373', '01930000-0000-7000-8000-000000000342', '01930000-0000-7000-8000-000000000243'),
    ('01930000-0000-7000-8000-000000000374', '01930000-0000-7000-8000-000000000342', '01930000-0000-7000-8000-000000000244'),
    ('01930000-0000-7000-8000-000000000375', '01930000-0000-7000-8000-000000000343', '01930000-0000-7000-8000-000000000241'),
    ('01930000-0000-7000-8000-000000000376', '01930000-0000-7000-8000-000000000343', '01930000-0000-7000-8000-000000000242')
  ) as d(id, version_id, file_id)
  join catalog.product_version pv on pv.id = d.version_id::uuid
  join document.file f on f.id = d.file_id::uuid
on conflict do nothing;

update catalog.product_version
   set rules = rules || jsonb_build_object('rating', '{"schemaVersion":"rating-v1","coverages":[{"coverageCode":"MTPL","base":{"type":"AMOUNT","rows":[{"label":"Ligeiro de passageiros, particular","when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["PRIVATE"]}},"value":450000},{"label":"Ligeiro de passageiros, táxi ou aluguer","when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["TAXI","RENTAL"]}},"value":900000},{"label":"Ligeiro misto","when":{"vehicleCategory":{"in":["LIGHT_MIXED"]}},"value":600000},{"label":"Motociclo","when":{"vehicleCategory":{"in":["MOTORCYCLE"]}},"value":250000},{"label":"Pesado","when":{"vehicleCategory":{"in":["HEAVY"]}},"value":1500000}]},"factors":[{"code":"DRIVER_AGE","label":"Idade do condutor","rows":[{"when":{"driverAgeYears":{"lt":25}},"value":"1.30"},{"when":{"driverAgeYears":{"gte":25,"lt":70}},"value":"1.00"},{"when":{"driverAgeYears":{"gte":70}},"value":"1.15"}]},{"code":"LICENCE_YEARS","label":"Antiguidade da carta","rows":[{"when":{"licenceYears":{"lt":2}},"value":"1.25"},{"when":{"licenceYears":{"gte":2}},"value":"1.00"}]},{"code":"PROVINCE","label":"Província de circulação","rows":[{"when":{"province":{"in":["MAPUTO_CITY","MAPUTO"]}},"value":"1.10"},{"when":{},"value":"1.00"}]},{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]}]}'::jsonb)
 where id = '01930000-0000-7000-8000-000000000341';

update catalog.product_version
   set rules = rules || jsonb_build_object('rating', '{"schemaVersion":"rating-v1","coverages":[{"coverageCode":"MTPL","base":{"type":"AMOUNT","rows":[{"label":"Ligeiro de passageiros, particular","when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["PRIVATE"]}},"value":450000},{"label":"Ligeiro de passageiros, táxi ou aluguer","when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["TAXI","RENTAL"]}},"value":900000},{"label":"Ligeiro misto","when":{"vehicleCategory":{"in":["LIGHT_MIXED"]}},"value":600000},{"label":"Motociclo","when":{"vehicleCategory":{"in":["MOTORCYCLE"]}},"value":250000},{"label":"Pesado","when":{"vehicleCategory":{"in":["HEAVY"]}},"value":1500000}]},"factors":[{"code":"DRIVER_AGE","label":"Idade do condutor","rows":[{"when":{"driverAgeYears":{"lt":25}},"value":"1.30"},{"when":{"driverAgeYears":{"gte":25,"lt":70}},"value":"1.00"},{"when":{"driverAgeYears":{"gte":70}},"value":"1.15"}]},{"code":"LICENCE_YEARS","label":"Antiguidade da carta","rows":[{"when":{"licenceYears":{"lt":2}},"value":"1.25"},{"when":{"licenceYears":{"gte":2}},"value":"1.00"}]},{"code":"PROVINCE","label":"Província de circulação","rows":[{"when":{"province":{"in":["MAPUTO_CITY","MAPUTO"]}},"value":"1.10"},{"when":{},"value":"1.00"}]},{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]},{"coverageCode":"VTPL","base":{"type":"RATE_OF_CAPITAL","rows":[{"when":{"capital":{"lt":600000000}},"value":"0.0004"},{"when":{},"value":"0.0003"}]},"factors":[{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]},{"coverageCode":"OD_COLLISION","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"label":"Até 4 anos","when":{"vehicleAgeYears":{"lt":5}},"value":"0.025"},{"label":"5 a 10 anos","when":{"vehicleAgeYears":{"gte":5,"lt":11}},"value":"0.030"}]},"factors":[{"code":"DRIVER_AGE","label":"Idade do condutor","rows":[{"when":{"driverAgeYears":{"lt":25}},"value":"1.30"},{"when":{},"value":"1.00"}]},{"code":"TRACKER","label":"Localizador","rows":[{"when":{"hasTracker":true},"value":"0.90"},{"when":{},"value":"1.00"}]},{"code":"PROVINCE","label":"Província de circulação","rows":[{"when":{"province":{"in":["MAPUTO_CITY"]}},"value":"1.15"},{"when":{},"value":"1.00"}]},{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]},{"coverageCode":"OD_FIRE","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"when":{},"value":"0.0015"}]},"factors":[{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]},{"coverageCode":"OD_NATURE","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"when":{},"value":"0.0020"}]},"factors":[{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]},{"coverageCode":"OD_THEFT","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"when":{},"value":"0.0050"}]},"factors":[{"code":"TRACKER","label":"Localizador","rows":[{"when":{"hasTracker":true},"value":"0.70"},{"when":{},"value":"1.00"}]},{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]},{"coverageCode":"OD_GLASS","base":{"type":"RATE_OF_VEHICLE_VALUE","rows":[{"when":{},"value":"0.0010"}]},"factors":[{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]},{"coverageCode":"OCCUPANTS","base":{"type":"AMOUNT","rows":[{"label":"Até 5 lugares","when":{"seats":{"lt":6}},"value":150000},{"when":{},"value":250000}]},"factors":[{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]},{"coverageCode":"ASSISTANCE","base":{"type":"AMOUNT","rows":[{"when":{},"value":120000}]},"factors":[{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]}]}'::jsonb)
 where id = '01930000-0000-7000-8000-000000000342';

update catalog.product_version
   set rules = rules || jsonb_build_object('rating', '{"schemaVersion":"rating-v1","durationFactors":[{"days":7,"factor":"0.06"},{"days":14,"factor":"0.10"},{"days":30,"factor":"0.18"},{"days":60,"factor":"0.32"},{"days":365,"factor":"1"}],"coverages":[{"coverageCode":"MTPL","base":{"type":"AMOUNT","rows":[{"when":{"vehicleCategory":{"in":["LIGHT_PASSENGER"]},"vehicleUsage":{"in":["PRIVATE"]}},"value":450000},{"when":{"vehicleCategory":{"in":["LIGHT_MIXED"]}},"value":600000},{"when":{"vehicleCategory":{"in":["MOTORCYCLE"]}},"value":250000}]},"factors":[{"code":"DRIVER_AGE","label":"Idade do condutor","rows":[{"when":{"driverAgeYears":{"lt":25}},"value":"1.30"},{"when":{"driverAgeYears":{"gte":25,"lt":70}},"value":"1.00"},{"when":{"driverAgeYears":{"gte":70}},"value":"1.15"}]},{"code":"COMMERCIAL","label":"Ajuste comercial","rows":[{"when":{},"value":"1.08"}]}]}]}'::jsonb)
 where id = '01930000-0000-7000-8000-000000000343';
