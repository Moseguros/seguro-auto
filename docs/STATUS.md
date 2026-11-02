# Estado do projecto

Actualizado em 02/11/2026.

## Onde estamos

### Sprint 0 — Fundações

| Tarefa | Estado | PR |
| --- | --- | --- |
| S0-01 Fichas e ADRs | concluída (merged) | #1 |
| S0-02 Esqueleto do backend | concluída (merged) | #2 |
| S0-03 Tipos partilhados e auditoria | em revisão (branch `task/S0-03-shared-e-auditoria`) | — |
| S0-04 Contrato OpenAPI | em revisão (branch `task/S0-04-openapi`) | — |
| S0-05 Web | em revisão | — |
| S0-06 Mobile | em revisão | — |
| S0-07 Infra local (`infra/docker-compose.yml`) | em revisão | — |
| S0-08 CI (`.github/workflows`) | em revisão | — |
| S0-09 Verificação do ambiente (`scripts/doctor.sh`) | em revisão | #5 |

### Sprint 1 — Identidade, organizações e perfis

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S1-01 Tabelas de identidade e organizações, RLS e cifra | S0-03, S0-04 | pronta |
| S1-02 Tokens JWT, sessões e permissões | S1-01 | pronta |
| S1-03 OTP do cliente e canais de envio | S1-02 | pronta |
| S1-04 Organizações, convites e activação de staff | S1-03 | pronta |
| S1-05 Login de staff e gestão de utilizadores | S1-04 | pronta |
| S1-06 Perfil e contactos do cliente | S1-03 | pronta — pode correr em paralelo com S1-04 |
| S1-07 Web: sessão e área do cliente | S0-05, S1-06 | pronta |
| S1-08 Web: staff, organizações e utilizadores | S1-07, S1-05 | pronta |
| S1-09 Mobile: apresentação, login e perfil | S0-06, S1-06 | pronta — pode correr em paralelo com S1-07 |

O Sprint 1 só começa depois de o Sprint 0 estar todo no `main`.

## Como continuar

1. Escolher a próxima tarefa na tabela (a primeira cujas dependências estão no `main`).
2. Ler a ficha `docs/tasks/S{n}-{nn}.md`.
3. Criar o branch indicado na ficha a partir do `main` actualizado.
4. Implementar, correr os comandos de verificação da ficha, abrir PR para revisão de `@edmilson-alar`.
5. Actualizar esta tabela no mesmo PR.

## Requisitos do ambiente

- Verificar tudo de uma vez: `./scripts/doctor.sh` (ou `/verificar-ambiente` no Claude Code).

- JDK 17 (Temurin), Docker em execução (Testcontainers), Node 22 LTS + pnpm 9, Flutter estável (versão fixada em `mobile/pubspec.yaml` a partir do S0-06).
- Backend: `cd backend && ./mvnw -B verify`.

## Notas e armadilhas conhecidas

- **Testcontainers** está fixado em 1.21.4 no `backend/pom.xml`: a 1.19.x do Spring Boot 3.3 é recusada pelo Docker 29+ ("client version 1.32 is too old").
- **Docker Hub com limite de pedidos (429):** se acontecer, puxar `mirror.gcr.io/library/postgres:16-alpine` e etiquetar como `postgres:16-alpine`; o código continua a usar a imagem oficial.
- **Spring Modulith JPA** precisa da tabela `event_publication` (migração `V202610311100`). Novas migrações têm carimbo posterior.
- O NUIT valida só 9 dígitos (sem dígito de controlo). Os formatos de matrícula são configuráveis (S0-03).
- **MinIO:** a imagem `minio/minio` deixou de estar no Docker Hub. O armazenamento S3 local escolhe-se no Sprint 3. O e-mail de desenvolvimento usa o **Mailpit** (o MailHog deixou de ser mantido).
- **A partir do S1-01** a aplicação liga-se à BD como `moseguros_app_user` (sem privilégios de dono) e o Flyway como dono; os testes precisam do `init-roles.sql`. Ver a adenda do ADR-002.
- **Fornecedores reais de SMS e WhatsApp** ainda não foram escolhidos: no Sprint 1 só há mocks (questão no S1-03).
- **Contrato OpenAPI** (S0-04): `api/openapi.yaml` é gerado, não se edita à mão. Regenerar com `cd backend && ./mvnw -B -Popenapi verify` (usa o `OpenApiExportIT`, precisa de Docker); `./scripts/check-openapi.sh` falha se estiver desactualizado.
- **Bloqueadas:** S5-07 e S5-08 (algoritmo SIMO e layout do ficheiro de pagamentos) — pedir ao proprietário do produto antes de implementar.

## Fonte de verdade

As fichas em `docs/tasks/`, o `AGENTS.md` e os ADRs são a fonte de verdade para implementar. Os documentos abaixo, fora do repositório, explicam o contexto e o porquê; se divergirem das fichas, valem as fichas.

- Levantamento de requisitos: Seguro Automóvel em Moçambique (claude.ai)
- Plano de Implementação por Sprints (claude.ai)
