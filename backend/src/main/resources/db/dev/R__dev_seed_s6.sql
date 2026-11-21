-- dados só para desenvolvimento
--
-- Dados de demonstração do Sprint 6 (só perfil local: spring.flyway.locations inclui db/dev). Mesmas
-- regras do R__dev_seed_s2.sql: ids fixos, dev-seed, "on conflict do nothing", valores fictícios. Corre
-- depois dos R__dev_seed_s2/s3/s5.sql (ordem do nome). Não mexe em shared.business_sequence: os números
-- …9000xx ficam fora da série normal. proposal_id, payment_intent_id e vehicle_id são ids fixos de
-- demonstração (sem FK entre schemas). O veículo AAA-100-MC do cliente (S2-03) não recebe apólice, para
-- continuar a poder ser retirado.

-- S6-01 ---------------------------------------------------------------------------------------

-- Apólices do cliente +258840000001 na Seguradora Demo: uma ACTIVE (de hoje − 10 dias a hoje + 355
-- dias) e uma EXPIRED (terminada há 30 dias). Datas às 00:00 de Maputo.
insert into policy.policy
  (id, created_at, created_by, updated_at, updated_by, number, insurer_id, proposal_id, payment_intent_id,
   product_version_id, product_name, product_type, coverage_type, policyholder_user_id, policyholder_name,
   vehicle_id, plate, vehicle_snapshot, driver_snapshot, coverages, start_at, end_at, renewal_type,
   payment_plan, total_amount, status, status_changed_at, channel, issued_at)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', v.number, ip.organization_id, v.proposal_id::uuid,
       v.intent_id::uuid, '01930000-0000-7000-8000-000000000211', 'RC Automóvel Demo', 'MTPL_ONLY', 'MTPL_ONLY',
       a.id, 'Cliente Teste', v.vehicle_id::uuid, v.plate,
       jsonb_build_object('vehicleId', v.vehicle_id, 'ownerUserId', a.id, 'plate', v.plate,
                          'make', v.make, 'model', v.model, 'year', v.year, 'category', 'LIGHT_PASSENGER',
                          'usage', 'PRIVATE', 'seats', 5),
       null,
       '[{"code": "MTPL", "mandatory": true, "capital": {"amount": 300000000, "currency": "MZN"},
          "deductibleType": "NONE", "deductibleAmount": null, "deductiblePercent": null,
          "premium": {"amount": 299900, "currency": "MZN"}}]'::jsonb,
       (((now() at time zone 'Africa/Maputo')::date + v.start_days)::timestamp at time zone 'Africa/Maputo'),
       (((now() at time zone 'Africa/Maputo')::date + v.end_days)::timestamp at time zone 'Africa/Maputo'),
       'ANNUAL_RENEWABLE', 'FULL', 299900, v.status,
       (((now() at time zone 'Africa/Maputo')::date
         + case when v.status = 'ACTIVE' then v.start_days else v.end_days end)::timestamp
         at time zone 'Africa/Maputo'),
       'APP',
       (((now() at time zone 'Africa/Maputo')::date + v.start_days)::timestamp at time zone 'Africa/Maputo')
  from (values
    ('01930000-0000-7000-8000-000000000601', 'AP-2027-900001', '01930000-0000-7000-8000-000000000611',
     '01930000-0000-7000-8000-000000000621', '01930000-0000-7000-8000-000000000631', 'ABC-123-MC',
     'Toyota', 'Corolla', 2018, -10, 355, 'ACTIVE'),
    ('01930000-0000-7000-8000-000000000602', 'AP-2027-900002', '01930000-0000-7000-8000-000000000612',
     '01930000-0000-7000-8000-000000000622', '01930000-0000-7000-8000-000000000632', 'XYZ-999-MP',
     'Nissan', 'Hardbody', 2015, -395, -30, 'EXPIRED')
  ) as v(id, number, proposal_id, intent_id, vehicle_id, plate, make, model, year, start_days, end_days, status)
  join identity.user_account a on a.msisdn = '258840000001' and a.type = 'CUSTOMER'
  join organization.insurer_profile ip on ip.short_code = 'DEMO'
on conflict do nothing;

-- Uma prestação paga (FULL) por apólice
insert into policy.instalment
  (id, created_at, created_by, updated_at, updated_by, policy_id, insurer_id, policyholder_user_id, seq,
   due_date, amount, status, payment_intent_id, paid_at)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', p.id, p.insurer_id, p.policyholder_user_id, 1,
       (p.start_at at time zone 'Africa/Maputo')::date, p.total_amount, 'PAID', p.payment_intent_id, p.issued_at
  from policy.policy p
  join (values
    ('01930000-0000-7000-8000-000000000651', '01930000-0000-7000-8000-000000000601'),
    ('01930000-0000-7000-8000-000000000652', '01930000-0000-7000-8000-000000000602')
  ) as v(id, policy_id) on p.id = v.policy_id::uuid
on conflict do nothing;

-- Certificados (códigos de verificação fixos, só para desenvolvimento: /v/{código})
insert into policy.certificate
  (id, created_at, created_by, updated_at, updated_by, policy_id, insurer_id, policyholder_user_id, number,
   valid_from, valid_to, verification_token, status)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', p.id, p.insurer_id, p.policyholder_user_id, v.number,
       p.start_at, p.end_at, v.token, 'VALID'
  from policy.policy p
  join (values
    ('01930000-0000-7000-8000-000000000641', '01930000-0000-7000-8000-000000000601', 'CT-2027-9000001',
     'DEMOvalid0000000000001'),
    ('01930000-0000-7000-8000-000000000642', '01930000-0000-7000-8000-000000000602', 'CT-2027-9000002',
     'DEMOexpired00000000002')
  ) as v(id, policy_id, number, token) on p.id = v.policy_id::uuid
on conflict do nothing;

-- Linha temporal: emitida e certificado emitido
insert into policy.policy_event
  (id, created_at, created_by, updated_at, updated_by, policy_id, insurer_id, policyholder_user_id, type,
   occurred_at, actor_type, actor_id)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', p.id, p.insurer_id, p.policyholder_user_id, v.type,
       p.issued_at, 'SYSTEM', 'policy-issuance'
  from policy.policy p
  join (values
    ('01930000-0000-7000-8000-000000000661', '01930000-0000-7000-8000-000000000601', 'ISSUED'),
    ('01930000-0000-7000-8000-000000000662', '01930000-0000-7000-8000-000000000601', 'CERTIFICATE_ISSUED'),
    ('01930000-0000-7000-8000-000000000663', '01930000-0000-7000-8000-000000000602', 'ISSUED'),
    ('01930000-0000-7000-8000-000000000664', '01930000-0000-7000-8000-000000000602', 'CERTIFICATE_ISSUED')
  ) as v(id, policy_id, type) on p.id = v.policy_id::uuid
on conflict do nothing;

-- S6-08 ---------------------------------------------------------------------------------------
-- Dados de carga (perf/, S6-08 §4). Ids fixos com o quarto grupo 80xx (sem colidir com os …06xx do
-- S6-01): 8006 clientes, 8007 perfis, 8008 veículos, 8001 apólices, 8002 propostas, 8003 intenções, 8004
-- veículos das apólices, 8005 certificados. Os condutores dos clientes de carga são criados pelo
-- perf/get-tokens.sh pela API (POST /me/drivers): o número da carta é cifrado (CryptoService) e não se
-- gera em SQL, como no R__dev_seed_s2.sql.

-- 20 clientes de carga +258841000000 … +258841000019 (entram por OTP com o X-Debug-Otp)
insert into identity.user_account
  (id, created_at, created_by, updated_at, updated_by, type, msisdn, msisdn_verified, status)
select ('01930000-0000-7000-8006-' || lpad(i::text, 12, '0'))::uuid, now(), 'dev-seed', now(), 'dev-seed',
       'CUSTOMER', '2588410000' || lpad(i::text, 2, '0'), true, 'ACTIVE'
  from generate_series(0, 19) as i
on conflict do nothing;

insert into identity.customer_profile
  (id, created_at, created_by, updated_at, updated_by, user_id, full_name, birth_date, province)
select ('01930000-0000-7000-8007-' || lpad(i::text, 12, '0'))::uuid, now(), 'dev-seed', now(), 'dev-seed',
       ('01930000-0000-7000-8006-' || lpad(i::text, 12, '0'))::uuid, 'Cliente Carga ' || chr(65 + i),
       date '1985-01-01', 'MAPUTO_CITY'
  from generate_series(0, 19) as i
on conflict do nothing;

insert into identity.user_role (user_id, role_code, created_at, created_by)
select ('01930000-0000-7000-8006-' || lpad(i::text, 12, '0'))::uuid, 'CUSTOMER', now(), 'dev-seed'
  from generate_series(0, 19) as i
on conflict do nothing;

-- Um veículo por cliente de carga (PRQ-000-MP … PRQ-019-MP, formato STANDARD), sem apólice: as cotações
-- do perf/quotes.js usam-nos.
insert into vehicle.vehicle
  (id, created_at, created_by, updated_at, updated_by, owner_user_id, plate, plate_format_code,
   chassis_number, make, model, year, engine_cc, category, seats, usage, province_of_circulation,
   commercial_value_amount, has_tracker, inatro_verified_at)
select ('01930000-0000-7000-8008-' || lpad(i::text, 12, '0'))::uuid, now(), 'dev-seed', now(), 'dev-seed',
       ('01930000-0000-7000-8006-' || lpad(i::text, 12, '0'))::uuid,
       'PRQ-' || lpad(i::text, 3, '0') || '-MP', 'STANDARD', 'PRFLOAD' || lpad(i::text, 10, '0'),
       'Toyota', 'Corolla', 2018, 1598, 'LIGHT_PASSENGER', 5, 'PRIVATE', 'MAPUTO_CITY', 85000000, false, now()
  from generate_series(0, 19) as i
on conflict do nothing;

-- 1 000 apólices ACTIVE da Seguradora Demo (PRF-000-MP … PRF-999-MP), de hoje − 10 dias a hoje + 355 dias,
-- do cliente de carga i % 20. proposal_id, payment_intent_id e vehicle_id são ids fixos de carga (sem FK).
insert into policy.policy
  (id, created_at, created_by, updated_at, updated_by, number, insurer_id, proposal_id, payment_intent_id,
   product_version_id, product_name, product_type, coverage_type, policyholder_user_id, policyholder_name,
   vehicle_id, plate, vehicle_snapshot, driver_snapshot, coverages, start_at, end_at, renewal_type,
   payment_plan, total_amount, status, status_changed_at, channel, issued_at)
select ('01930000-0000-7000-8001-' || lpad(g.i::text, 12, '0'))::uuid, now(), 'dev-seed', now(), 'dev-seed',
       'AP-2027-8' || lpad(g.i::text, 5, '0'), ip.organization_id,
       ('01930000-0000-7000-8002-' || lpad(g.i::text, 12, '0'))::uuid,
       ('01930000-0000-7000-8003-' || lpad(g.i::text, 12, '0'))::uuid,
       '01930000-0000-7000-8000-000000000211', 'RC Automóvel Demo', 'MTPL_ONLY', 'MTPL_ONLY',
       ('01930000-0000-7000-8006-' || lpad((g.i % 20)::text, 12, '0'))::uuid, 'Cliente Carga ' || chr(65 + g.i % 20),
       ('01930000-0000-7000-8004-' || lpad(g.i::text, 12, '0'))::uuid, g.plate,
       jsonb_build_object('vehicleId', '01930000-0000-7000-8004-' || lpad(g.i::text, 12, '0'),
                          'ownerUserId', '01930000-0000-7000-8006-' || lpad((g.i % 20)::text, 12, '0'),
                          'plate', g.plate, 'make', 'Toyota', 'model', 'Corolla', 'year', 2018,
                          'category', 'LIGHT_PASSENGER', 'usage', 'PRIVATE', 'seats', 5),
       null,
       '[{"code": "MTPL", "mandatory": true, "capital": {"amount": 300000000, "currency": "MZN"},
          "deductibleType": "NONE", "deductibleAmount": null, "deductiblePercent": null,
          "premium": {"amount": 299900, "currency": "MZN"}}]'::jsonb,
       g.start_at, g.start_at + interval '365 days', 'ANNUAL_RENEWABLE', 'FULL', 299900, 'ACTIVE', g.start_at,
       'WEB', g.start_at
  from (select i, 'PRF-' || lpad(i::text, 3, '0') || '-MP' as plate,
               (((now() at time zone 'Africa/Maputo')::date - 10)::timestamp at time zone 'Africa/Maputo') as start_at
          from generate_series(0, 999) as i) as g
  join organization.insurer_profile ip on ip.short_code = 'DEMO'
on conflict do nothing;

-- Certificados VALID com os códigos PERF000000000000000000 … PERF000000000000000999 (22 caracteres)
insert into policy.certificate
  (id, created_at, created_by, updated_at, updated_by, policy_id, insurer_id, policyholder_user_id, number,
   valid_from, valid_to, verification_token, status)
select ('01930000-0000-7000-8005-' || lpad(i::text, 12, '0'))::uuid, now(), 'dev-seed', now(), 'dev-seed',
       p.id, p.insurer_id, p.policyholder_user_id, 'CT-2027-8' || lpad(i::text, 6, '0'), p.start_at, p.end_at,
       'PERF' || lpad(i::text, 18, '0'), 'VALID'
  from generate_series(0, 999) as i
  join policy.policy p on p.id = ('01930000-0000-7000-8001-' || lpad(i::text, 12, '0'))::uuid
on conflict do nothing;
