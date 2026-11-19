-- Papéis do worker para os testes (S5-09 §3), como o infra/postgres/init/02-worker-role.sql: o Flyway
-- corre como dono (o utilizador do contentor) e a aplicação liga-se como moseguros_worker_user.
create role moseguros_worker nologin;
create role moseguros_worker_user login password 'moseguros_worker' in role moseguros_worker;
