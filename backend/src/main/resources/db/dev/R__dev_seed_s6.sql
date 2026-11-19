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
