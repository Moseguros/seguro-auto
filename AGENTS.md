# AGENTS.md — regras para quem desenvolve na MoSeguros (pessoas e agentes de IA)

Este ficheiro é incluído no contexto de todas as tarefas. Se uma ficha de tarefa contradisser este ficheiro, pare e pergunte na secção "Questões" da ficha.

## 1. Produto e identidade

- Nome comercial: **MoSeguros**.
- Pacote Java base: `mz.co.moseguros` (ex.: `mz.co.moseguros.policy`).
- Grupo Maven: `mz.co.moseguros`; artefactos `backend`, `payment-file-worker`, `payment-contracts`.
- App móvel: application id / bundle id `mz.co.moseguros.app`; deep links `moseguros://`.
- Repositório: `Moseguros/seguro-auto` (monorepo).
- Revisor obrigatório dos pull requests: `@edmilson-alar`.
- Marca: azul profundo `#142D4E` (primária), laranja `#F28C28` (acção), fundo `#E8EFF6`. O laranja só destaca acções de conversão (simular, comparar, adquirir, renovar). Detalhes em `docs/brand/README.md`.

## 2. Stack

| Camada | Tecnologia |
| --- | --- |
| Backend | Java 17 (Temurin), Spring Boot 3.3.x, Maven, Spring Modulith, PostgreSQL 16, Flyway |
| Worker de ficheiros | Java 17, Spring Boot, Spring Integration SFTP, Spring AMQP |
| Mensageria | RabbitMQ 3.13 (worker ↔ backend); eventos internos com Spring Modulith + outbox |
| Web | React 18, TypeScript 5 (strict), Vite, MUI, TanStack Query, React Router, i18next, orval; Node 22 LTS, pnpm 9 |
| Mobile | Flutter estável ≥ 3.24, Riverpod, go_router, Dio, drift |
| Infra | Docker, docker-compose, GitHub Actions |

## 3. Estrutura do monorepo

```
backend/               aplicação principal
payment-file-worker/   SFTP → parser → RabbitMQ
payment-contracts/     mensagens RabbitMQ partilhadas
web/                   React (todos os actores, incluindo /cliente)
mobile/                Flutter (só clientes finais)
api/openapi.yaml       contrato gerado pelo backend e versionado
infra/                 docker-compose, Dockerfiles, mocks (mpesa-mock, sftp, sms)
docs/adr/              decisões de arquitectura
docs/tasks/            fichas de tarefa (S{sprint}-{nn}.md)
```

Módulos do backend (um pacote por módulo em `mz.co.moseguros`): `shared`, `identity`, `organization`, `vehicle`, `catalog`, `rating`, `proposal`, `payment`, `policy`, `document`, `notification`, `verification`, `claim`, `complaint`, `assistance`, `reporting`, `fraud`, `integration`.

Dentro de cada módulo: `api/` (controladores e DTOs records), `application/` (casos de uso, transacções, `@PreAuthorize`), `domain/` (entidades, estados, regras, repositórios), `infrastructure/` (adaptadores). Um módulo só fala com outro pela classe pública `<Modulo>Api` ou por eventos; nunca usa repositórios de outro módulo. `ApplicationModules.verify()` tem de passar.

## 4. API REST

- Prefixo `/api/v1`; recursos no plural em inglês; JSON em camelCase.
- Contrato primeiro: DTO + anotações OpenAPI antes da implementação; regenerar `api/openapi.yaml` e os clientes web e mobile no mesmo PR.
- Erros RFC 7807 (`application/problem+json`) com `type`, `title`, `status`, `detail`, `code` (ex.: `POLICY_NOT_ACTIVE`) e `errors[]` por campo.
- Os códigos de erro estão em `ErrorCodes` (topo) e `FieldErrorCodes` (`errors[].code`) e exportados para `api/error-codes.json`. Erros de value objects ao ler um pedido são erros de campo (`VALIDATION_ERROR`). Quem acrescenta um código acrescenta as traduções `errors.<CODE>` / `validation.<CODE>` na web e na app (pt e en) no mesmo PR.
- Paginação `?page=0&size=20&sort=createdAt,desc` → `{ content, page, size, totalElements }`.
- Datas ISO 8601 em UTC; apresentação em `Africa/Maputo`.
- Dinheiro `{ "amount": 299900, "currency": "MZN" }` em centavos.
- Pagamento, emissão e indemnização exigem `Idempotency-Key` (UUID); mesma chave = mesma resposta durante 24 h.
- Transições de estado são acções: `POST /policies/{id}/cancel`, nunca `PATCH status`.
- IDs públicos UUID v7; números de negócio com prefixo (`AP-2026-000123`, `SN-…`, `RC-…`, `PR-…`, `PG-…`).
- Todo o endpoint tem `@Operation(operationId = "verboRecurso")` explícito (`getVersion`, `listOrganizations`, `cancelPolicy`) e o controlador tem `@Tag` com o nome do módulo. Os clientes web e mobile são gerados a partir destes nomes; mudá-los é uma alteração de contrato.
- Textos para o utilizador vêm do i18n do frontend, não da API. Excepção: mensagens que o backend envia por SMS, WhatsApp, e-mail ou push, que vêm de `backend/src/main/resources/messages/*.properties` (pt e en).

## 5. Base de dados

- Um schema PostgreSQL por módulo; snake_case em inglês.
- Colunas obrigatórias: `id uuid pk`, `created_at`, `created_by`, `updated_at`, `updated_by`, `version`. Excepções: `audit.audit_event`, que só recebe inserções; tabelas de associação (só chaves estrangeiras, ex.: `identity.user_role`), que têm chave primária composta e apenas `created_at` e `created_by`.
- A aplicação liga-se à BD com um utilizador que não é dono das tabelas; o Flyway usa o dono. Isolamento por âmbito (`TenantContext`: sistema, plataforma, organização, cliente) com RLS — ver ADR-002 e a adenda.
- Dados de seguradora têm `insurer_id`; isolamento por `TenantContext` no serviço **e** Row Level Security.
- Migrações Flyway `V{AAAAMMDDHHmm}__{modulo}_{descricao}.sql`; nunca editar uma migração aplicada.
- Estados em `varchar` com `CHECK`; transições validadas no domínio.
- Sem apagamento físico em apólices, pagamentos, sinistros e reclamações.

## 6. Segurança e dados pessoais

- Autorização por permissão (`policy:issue`, `claim:decide`…) com `@PreAuthorize` no serviço, nunca só no frontend.
- Documentos de identificação e NUIT de pessoas singulares cifrados na coluna (AES-GCM, chave fora da BD, `CryptoService`). O NUIT de uma organização (pessoa colectiva) é público e fica em claro.
- Segredos (chaves M-Pesa, SFTP, cartão) só no cofre de segredos; a BD guarda a referência; a API nunca os devolve.
- Logs sem telefone completo, documentos, tokens, códigos OTP ou segredos.
- Escritas sobre apólice, pagamento, sinistro ou reclamação geram evento em `audit.audit_event`.

## 7. Testes

- Backend: unitários de domínio; integração com Testcontainers (Postgres e RabbitMQ reais) por endpoint, incluindo acesso negado e isolamento entre seguradoras; cobertura ≥ 80% em `domain` e `application`.
- Web: Vitest para lógica; Playwright para o fluxo principal do sprint.
- Mobile: testes de widget dos ecrãs novos; teste de integração do fluxo principal em emulador Android.

## 8. Definição de Pronto

- [ ] Critérios de aceitação da ficha cumpridos.
- [ ] `api/openapi.yaml` e clientes regenerados (se a API mudou).
- [ ] CI verde; sem avisos novos do analisador.
- [ ] Migração Flyway incluída (se houver mudança de BD).
- [ ] Textos em i18n (pt-MZ); ecrãs utilizáveis a 360 px.
- [ ] Funcionalidades de cliente entregues na web (`/cliente`) **e** na app, salvo diferenças permitidas (offline e câmara guiada só na app).
- [ ] PR revisto e aprovado por um humano.

## 9. Como trabalhar (agentes de IA)

1. Comece pela ficha `docs/tasks/S{n}-{nn}.md`. Faça só o que ela pede.
2. Ordem: migração → domínio → serviço → API + testes → OpenAPI → web → mobile.
3. Um branch por tarefa: `task/S{n}-{nn}-descricao-curta`, a partir do `main` actualizado.
4. Commits pequenos, mensagem em português no imperativo, prefixada com o ID: `S0-03: cria value object Msisdn`.
5. Abra um pull request por tarefa com: o que foi feito, como testar, critérios de aceitação marcados, e o que ficou de fora.
6. Nunca faça push para o `main`, nunca reescreva histórico partilhado, nunca altere migrações aplicadas nem contratos de outro módulo sem ficha explícita.
7. Dúvida ou contradição → escreva na secção "Questões" da ficha e pare essa parte; não invente.
8. **Tarefas bloqueadas:** S5-07 (algoritmo SIMO de referências) e S5-08 (layout do ficheiro de pagamentos SIMO). Quem lá chegar pára e pede ao proprietário do produto o algoritmo, o layout e exemplos. Não se inventa algoritmo nem layout.
9. A API M-Pesa segue a documentação oficial da Vodacom Moçambique (`developer.mpesa.vm.co.mz`); o simulador em `infra/mocks/mpesa-mock` tem de se comportar como ela.
10. O dinheiro vai sempre directamente para a conta da seguradora. A plataforma e os mediadores nunca recebem fundos.
