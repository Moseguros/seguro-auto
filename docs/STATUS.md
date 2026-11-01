# Estado do projecto

Actualizado em 31/10/2026.

## Onde estamos

| Tarefa | Estado | PR |
| --- | --- | --- |
| S0-01 Fichas e ADRs | concluída (merged) | #1 |
| S0-02 Esqueleto do backend | em revisão | #2 |
| S0-03 Tipos partilhados e auditoria | pronta — começar depois do merge do #2 | — |
| S0-04 Contrato OpenAPI | pronta — pode correr em paralelo com S0-03 | — |
| S0-05 Web | depende de S0-04 | — |
| S0-06 Mobile | depende de S0-04 | — |
| S0-07 Infra local | depende de S0-02 e S0-05 | — |
| S0-08 CI | depende de S0-03 a S0-07 | — |

## Como continuar

1. Ler a ficha `docs/tasks/S0-0X.md` da próxima tarefa.
2. Criar o branch `task/S0-0X-descricao` a partir do `main` actualizado.
3. Implementar, correr os comandos de verificação da ficha, abrir PR para revisão de `@edmilson-alar`.
4. Actualizar esta tabela no mesmo PR.

## Requisitos do ambiente

- JDK 17 (Temurin), Docker em execução (Testcontainers), Node 20 + pnpm 9, Flutter estável ≥ 3.24.
- Backend: `cd backend && ./mvnw -B verify`.

## Notas e armadilhas conhecidas

- **Testcontainers** está fixado em 1.21.4 no `backend/pom.xml`: a 1.19.x do Spring Boot 3.3 é recusada pelo Docker 29+ ("client version 1.32 is too old").
- **Docker Hub com limite de pedidos (429):** se acontecer, puxar `mirror.gcr.io/library/postgres:16-alpine` e etiquetar como `postgres:16-alpine`; o código continua a usar a imagem oficial.
- **Spring Modulith JPA** precisa da tabela `event_publication` (migração `V202610311100`). Novas migrações têm carimbo posterior.
- O NUIT valida só 9 dígitos (sem dígito de controlo). Os formatos de matrícula são configuráveis (S0-03).
- **Bloqueadas:** S5-07 e S5-08 (algoritmo SIMO e layout do ficheiro de pagamentos) — pedir ao proprietário do produto antes de implementar.

## Documentos de referência (fora do repositório)

- Levantamento de requisitos: Seguro Automóvel em Moçambique (claude.ai)
- Plano de Implementação por Sprints (claude.ai)
