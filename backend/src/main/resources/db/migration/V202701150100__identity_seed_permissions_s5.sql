-- Permissões do Sprint 5 (S5-01 §1; modelo transversal §5). As fichas S5-02 a S5-12 usam estas
-- permissões e não criam outras. Entram no token na renovação seguinte (S1-02). O âmbito de
-- serviço payments:file-sources do payment-file-worker não é uma permissão de perfil (S5-09).

insert into identity.role_permission (role_code, permission, created_at, created_by) values
  ('INSURER_ADMIN',  'payment:configure',         now(), 'system'),
  ('FINANCE',        'payment:configure',         now(), 'system'),
  ('INSURER_ADMIN',  'payment:read',              now(), 'system'),
  ('FINANCE',        'payment:read',              now(), 'system'),
  ('FINANCE',        'payment:resolve-exception', now(), 'system'),
  ('BROKER',         'payment:link-for-client',   now(), 'system'),
  ('AGENT',          'payment:link-for-client',   now(), 'system'),
  ('CUSTOMER',       'self:payments',             now(), 'system'),
  ('PLATFORM_ADMIN', 'platform:operations',       now(), 'system');
