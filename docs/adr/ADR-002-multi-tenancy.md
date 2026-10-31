# ADR-002 — Multi-tenancy por `insurer_id` com Row Level Security

- Estado: aceite
- Data: 2026-10-31

## Contexto

A MoSeguros é um agregador: várias seguradoras usam a mesma plataforma e nenhuma pode ver dados de outra (produtos, tarifas, propostas, apólices, pagamentos, sinistros, reclamações). Os clientes finais compram a várias seguradoras e vêem todas as suas apólices.

## Decisão

- Uma única base de dados e um único conjunto de schemas para todas as seguradoras.
- Todas as tabelas com dados de uma seguradora têm a coluna `insurer_id uuid not null`.
- Isolamento em duas camadas:
  1. **Aplicação:** o `TenantContext` é preenchido a partir do token (`orgId` quando `orgType = INSURER`); os serviços filtram sempre por ele.
  2. **Base de dados:** Row Level Security nessas tabelas. Em cada transacção, a aplicação executa `select set_config('app.insurer_id', :id, true)`. A política é `insurer_id = current_setting('app.insurer_id', true)::uuid`.
- O utilizador de BD da aplicação não é dono das tabelas (para que o RLS se aplique a ele). Acessos de clientes finais, mediadores e da plataforma usam políticas próprias definidas por tarefa (ex.: por `customer_user_id`), nunca desligam o RLS.
- Cada endpoint com dados de seguradora tem um teste de integração que prova que a seguradora A não lê nem altera dados da seguradora B.

## Consequências

- Uma falha de filtro na aplicação não expõe dados de outra seguradora, porque o RLS bloqueia.
- Consultas transversais legítimas (relatórios do regulador, administração) usam um papel de BD separado e auditado.
- Pequeno custo de desempenho do RLS; aceitável.

## Alternativas consideradas

- Schema ou base de dados por seguradora: isolamento forte, mas migrações e operação multiplicadas pelo número de seguradoras, e o cliente final precisa de ver apólices de várias.
- Só filtro na aplicação: um erro num agente ou numa consulta expõe dados de outra seguradora.

## Adenda de 01/11/2026 — âmbitos além da seguradora

O Sprint 1 precisa de isolar também utilizadores e organizações que não são seguradoras (mediadores, oficinas…) e os próprios clientes. A decisão generaliza o mecanismo acima, sem o substituir:

- O `TenantContext` passa a ter um **âmbito** (`NONE`, `SYSTEM`, `PLATFORM`, `ORG`, `CUSTOMER`), além do utilizador, da organização e da seguradora.
- Em cada transacção a aplicação define `app.scope`, `app.user_id`, `app.org_id` e `app.insurer_id` com `set_config(..., true)` (o `TenantJpaTransactionManager` faz isto ao começar a transacção).
- As políticas das tabelas com `insurer_id` continuam a usar `app.insurer_id`. As tabelas de identidade e organizações usam `app.scope`, `app.org_id` e `app.user_id` (políticas exactas na ficha S1-01).
- Sem âmbito (`NONE`) não se vê nenhuma linha.
- `SYSTEM` é usado só pelos fluxos que ainda não têm utilizador autenticado (pedir e validar OTP, login, convites, recuperação de palavra-passe) e por jobs, através de `@RunAsSystem`, cujo uso está limitado por um teste de arquitectura.
- A aplicação liga-se à BD com um utilizador sem privilégios de dono (`moseguros_app_user`, membro de `moseguros_app`); o Flyway usa o dono. As tabelas têm `force row level security`.
