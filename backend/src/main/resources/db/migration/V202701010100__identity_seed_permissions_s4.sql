-- Permissões do Sprint 4 (S4-01 §1; modelo transversal §5). As fichas S4-02 a S4-09 usam estas
-- permissões e não criam outras. Entram no token na renovação seguinte (S1-02). O cliente envia e
-- consulta o seu KYC com self:profile (S1-06), que já tem.

insert into identity.role_permission (role_code, permission, created_at, created_by) values
  ('INSURER_ADMIN', 'proposal:read',              now(), 'system'),
  ('UNDERWRITER',   'proposal:read',              now(), 'system'),
  ('UNDERWRITER',   'proposal:decide',            now(), 'system'),
  ('INSURER_ADMIN', 'proposal:decide',            now(), 'system'),
  ('UNDERWRITER',   'kyc:review',                 now(), 'system'),
  ('INSURER_ADMIN', 'kyc:review',                 now(), 'system'),
  ('UNDERWRITER',   'inspection:review',          now(), 'system'),
  ('BROKER',        'proposal:create-for-client', now(), 'system'),
  ('AGENT',         'proposal:create-for-client', now(), 'system'),
  ('CUSTOMER',      'self:proposals',             now(), 'system');
