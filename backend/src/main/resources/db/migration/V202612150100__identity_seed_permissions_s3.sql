-- Permissões do Sprint 3 (S3-01 §1; modelo transversal §5). As fichas S3-02 a S3-05 usam estas
-- permissões e não criam outras. Entram no token na renovação seguinte (S1-02).

insert into identity.role_permission (role_code, permission, created_at, created_by) values
  ('INSURER_ADMIN',   'rating:manage',           now(), 'system'),
  ('PRODUCT_MANAGER', 'rating:manage',           now(), 'system'),
  ('BROKER',          'quote:create-for-client', now(), 'system'),
  ('AGENT',           'quote:create-for-client', now(), 'system'),
  ('CUSTOMER',        'self:quotes',             now(), 'system');
