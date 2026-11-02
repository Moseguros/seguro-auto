-- Perfis e permissões iniciais (S1-01 §5). As permissões dos sprints seguintes entram
-- por migrações novas; esta não se edita.
-- O id dos perfis não é exposto (a API usa o code); gen_random_uuid() basta aqui.

insert into identity.role (id, created_at, created_by, updated_at, updated_by, code, org_types)
select gen_random_uuid(), now(), 'system', now(), 'system', r.code, r.org_types
  from (values
    ('PLATFORM_ADMIN',      null::text[]),
    ('INSURER_ADMIN',       '{INSURER}'::text[]),
    ('ORG_ADMIN',           '{BROKER,AGENT,WORKSHOP,ASSESSOR,ASSISTANCE,OMBUDSMAN,REGULATOR,POLICE,CORPORATE_CLIENT}'::text[]),
    ('PRODUCT_MANAGER',     '{INSURER}'::text[]),
    ('UNDERWRITER',         '{INSURER}'::text[]),
    ('CLAIMS_HANDLER',      '{INSURER}'::text[]),
    ('COMPLAINTS_OFFICER',  '{INSURER}'::text[]),
    ('FINANCE',             '{INSURER}'::text[]),
    ('BROKER',              '{BROKER}'::text[]),
    ('AGENT',               '{AGENT}'::text[]),
    ('WORKSHOP',            '{WORKSHOP}'::text[]),
    ('ASSESSOR',            '{ASSESSOR}'::text[]),
    ('ASSISTANCE_PROVIDER', '{ASSISTANCE}'::text[]),
    ('OMBUDSMAN',           '{OMBUDSMAN}'::text[]),
    ('REGULATOR_VIEWER',    '{REGULATOR}'::text[]),
    ('POLICE_OFFICER',      '{POLICE}'::text[]),
    ('FLEET_MANAGER',       '{CORPORATE_CLIENT}'::text[]),
    ('CUSTOMER',            null::text[])
  ) as r (code, org_types);

insert into identity.role_permission (role_code, permission, created_at, created_by)
select p.role_code, p.permission, now(), 'system'
  from (values
    ('PLATFORM_ADMIN', 'otp-settings:read'),
    ('PLATFORM_ADMIN', 'otp-settings:manage'),
    ('PLATFORM_ADMIN', 'organization:read'),
    ('PLATFORM_ADMIN', 'organization:manage'),
    ('PLATFORM_ADMIN', 'org-user:read'),
    ('PLATFORM_ADMIN', 'org-user:manage'),
    ('INSURER_ADMIN',  'org-user:read'),
    ('INSURER_ADMIN',  'org-user:manage'),
    ('ORG_ADMIN',      'org-user:read'),
    ('ORG_ADMIN',      'org-user:manage'),
    ('CUSTOMER',       'self:profile')
  ) as p (role_code, permission);
