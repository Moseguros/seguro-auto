-- S1-04: convites procurados por e-mail (substituição de convites pendentes, arranque do
-- primeiro administrador) e listados por organização.
create index ix_invitation_email on identity.invitation (email);
create index ix_invitation_org on identity.invitation (organization_id, created_at desc);
