-- Só para o FlywayOutOfOrderIT: migração com carimbo menor do que a última já aplicada
-- (V202612010101), como uma tarefa paralela que chega ao main depois de outra.
create table public.out_of_order_probe (id int primary key);
