-- Permissões do Sprint 6 (S6-01 §1; modelo transversal §5). As fichas S6-02 a S6-07 usam estas
-- permissões e não criam outras. Entram no token na renovação seguinte (S1-02).

insert into identity.role_permission (role_code, permission, created_at, created_by) values
  ('INSURER_ADMIN',  'policy:read',                  now(), 'system'),
  ('UNDERWRITER',    'policy:read',                  now(), 'system'),
  ('CLAIMS_HANDLER', 'policy:read',                  now(), 'system'),
  ('FINANCE',        'policy:read',                  now(), 'system'),
  ('POLICE_OFFICER', 'verification:police',          now(), 'system'),
  ('PLATFORM_ADMIN', 'notification-template:manage', now(), 'system'),
  ('CUSTOMER',       'self:policies',                now(), 'system');
