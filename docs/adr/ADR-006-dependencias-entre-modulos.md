# ADR-006 — Sentido das dependências entre módulos e excepções

- Estado: aceite
- Data: 2026-11-02

## Contexto

O Spring Modulith (`ApplicationModules.verify()`) rejeita ciclos entre módulos. Vários fluxos naturais criariam ciclos: o pagamento confirma uma proposta que depois o usa; o sinistro consulta a apólice e a apólice precisa do histórico de sinistros; muitos módulos querem enviar notificações.

## Decisão

- O grafo de dependências oficial está no `docs/architecture/modelo-transversal.md` §1. Uma tarefa que precise de uma dependência fora do grafo pára e pergunta.
- Quando o sentido natural criaria um ciclo, inverte-se com uma interface no módulo de baixo, implementada pelo de cima:
  - `PaymentTargetResolver` (pagamentos de propostas, prestações, renovações, endossos);
  - `FileAccessPolicy` (quem pode ler cada ficheiro).
- O `claim` chama `PolicyApi.recordClaimOutcome(...)` em vez de o `policy` ouvir eventos do `claim`.
- O `proposal` publica `ProposalPaid` para o `policy` emitir; o `policy` não ouve directamente o pagamento da proposta.
- As notificações são chamadas pelo módulo que quer notificar (`NotificationApi.notify`); o `notification` não ouve eventos.
- **Excepção ao isolamento entre schemas:** o `reporting` lê vistas materializadas no seu schema, construídas sobre tabelas de outros schemas, com um papel de BD só de leitura. Não escreve fora do seu schema e não usa APIs Java de outros módulos.

## Consequências

- O grafo é verificável em cada build.
- Algumas interfaces a mais (SPIs), em troca de módulos que podem evoluir sem se conhecerem mutuamente.
- O `reporting` depende da estrutura das tabelas dos outros módulos. Uma alteração de tabela que parta uma vista materializada parte a migração do `reporting`, e o build detecta-o.
