-- Dados de demonstração do Sprint 5 (só perfil local: spring.flyway.locations inclui db/dev).
-- Mesmas regras do R__dev_seed_s2.sql: ids fixos, dev-seed, valores fictícios. Corre depois do
-- R__dev_seed_s2.sql e do R__dev_seed_s3.sql (o Flyway corre os repetíveis por ordem do nome).

-- S5-01 ---------------------------------------------------------------------------------------

-- Formas de pagamento das seguradoras de demonstração (S5-01 §8). Só os caminhos dos segredos: os
-- valores de desenvolvimento são escritos no cofre pelo DevPaymentSecretsSeeder (perfil local).
-- As entidades e os IDs BMEPS são os dos exemplos oficiais (docs/specs/interbancos/README.md):
-- 90002 é a entidade do exemplo MEPS; 90090 valida as referências do exemplo BMEPS, com a empresa
-- 800 e o perfil 02 do cabeçalho. O sftp e o mpesa-mock são os serviços do compose (S5-06, S5-03).
insert into payment.insurer_payment_method
  (id, created_at, created_by, updated_at, updated_by, insurer_id, method, enabled, enabled_at,
   environment, display_name, settings, secret_refs, sort_order)
select v.id::uuid, now(), 'dev-seed', now(), 'dev-seed', o.id, v.method, true, now(), 'SANDBOX',
       v.display_name, v.settings::jsonb,
       (select jsonb_object_agg(f, jsonb_build_object(
                 'path', 'secret/insurers/' || o.id || '/payments/' || v.method || '/' || f,
                 'updatedAt', to_char(now() at time zone 'UTC', 'YYYY-MM-DD"T"HH24:MI:SS"Z"')))
          from unnest(v.secret_fields) as f),
       v.sort_order
  from (values
    ('01930000-0000-7000-8000-000000000501', '01930000-0000-7000-8000-000000000001', 'MPESA',
     'M-Pesa Seguradora Demo',
     '{"serviceProviderCode": "171717", "host": "http://mpesa-mock",
       "ports": {"c2b": 18352, "query": 18353, "reversal": 18354},
       "origin": "developer.mpesa.vm.co.mz", "c2bTimeoutSeconds": 90}',
     array['apiKey', 'publicKey'], 0),
    ('01930000-0000-7000-8000-000000000502', '01930000-0000-7000-8000-000000000001', 'BANK_REFERENCE',
     null,
     '{"entity": "90002", "fileFormat": "MEPS",
       "sftp": {"host": "sftp", "port": 22, "username": "demo", "authType": "PASSWORD", "inboundDir": "inbound",
                "moveAfterProcessing": true, "processedDir": "processed", "errorDir": "error", "filePattern": "*"}}',
     array['sftp.password'], 1),
    ('01930000-0000-7000-8000-000000000503', '01930000-0000-7000-8000-000000000320', 'BANK_REFERENCE',
     null,
     '{"entity": "90090", "fileFormat": "BMEPS", "bmepsCompanyId": "800", "bmepsProfileId": "02",
       "sftp": {"host": "sftp", "port": 22, "username": "demob", "authType": "PASSWORD", "inboundDir": "inbound",
                "moveAfterProcessing": true, "processedDir": "processed", "errorDir": "error", "filePattern": "*"}}',
     array['sftp.password'], 1)
  ) as v(id, insurer_id, method, display_name, settings, secret_fields, sort_order)
  join organization.organization o on o.id = v.insurer_id::uuid
on conflict (insurer_id, method) do nothing;
