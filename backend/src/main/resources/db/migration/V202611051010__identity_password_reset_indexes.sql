-- S1-05: pedidos de recuperação procurados por conta (limite de 3 por hora e invalidação dos
-- anteriores).
create index ix_password_reset_user on identity.password_reset (user_id, created_at desc);
