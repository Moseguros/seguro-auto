-- Numeração das facturas das referências Ponto24 (S5-07 §3; docs/specs/interbancos/README.md §1.3).
-- Corre como dono.
--
-- Um contador por entidade (seguradora), a partir de 0000001, sem ano e nunca reutilizado: o número
-- da factura entra na referência (factura 7 + mês 2 + controlo 2). Não usa o BusinessNumberGenerator
-- (que reinicia por ano e tem prefixo).
--
-- Tabela de infra-estrutura (AGENTS.md §5): sem as colunas obrigatórias e sem RLS (só a entidade, sem
-- dados pessoais nem de seguradora). Sem delete para a aplicação. O check do last_value faz falhar a
-- geração quando a entidade esgota os 9 999 999 números (503 REFERENCE_GENERATION_FAILED).

create table payment.reference_invoice_sequence (
  entity     varchar(5) primary key check (entity ~ '^[0-9]{5}$'),
  last_value int        not null check (last_value between 1 and 9999999)
);

grant select, insert, update on payment.reference_invoice_sequence to moseguros_app;
