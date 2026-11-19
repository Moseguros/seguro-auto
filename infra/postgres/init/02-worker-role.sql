-- Papéis do payment-file-worker (S5-09 §3): moseguros_worker (sem login) recebe os grants do schema
-- payment_worker pela migração do worker; moseguros_worker_user é o utilizador com que o worker se liga.
-- O Flyway do worker corre como dono. Só corre com o volume vazio (docker compose down -v).
-- A palavra-passe é só de desenvolvimento.
create role moseguros_worker nologin;
create role moseguros_worker_user login password 'moseguros_worker' in role moseguros_worker;
