-- Permissões do Sprint 2 (S2-01 §8; modelo transversal §5). As fichas S2-02 a S2-04 usam
-- estas permissões e não criam outras.

insert into identity.role_permission (role_code, permission, created_at, created_by)
select p.role_code, p.permission, now(), 'system'
  from (values
    ('PLATFORM_ADMIN',  'reference:manage'),
    ('INSURER_ADMIN',   'product:read'),
    ('INSURER_ADMIN',   'product:manage'),
    ('PRODUCT_MANAGER', 'product:read'),
    ('PRODUCT_MANAGER', 'product:manage'),
    ('UNDERWRITER',     'product:read'),
    ('CUSTOMER',        'self:vehicles')
  ) as p (role_code, permission);
