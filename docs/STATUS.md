# Estado do projecto

Actualizado em 03/11/2026.

## Onde estamos

### Sprint 0 — Fundações

| Tarefa | Estado | PR |
| --- | --- | --- |
| S0-01 Fichas e ADRs | concluída (merged) | #1 |
| S0-02 Esqueleto do backend | concluída (merged) | #2 |
| S0-03 Tipos partilhados e auditoria | concluída (no `main` pelo PR de integração) | #7–#12 |
| S0-04 Contrato OpenAPI | concluída (no `main` pelo PR de integração) | #7–#12 |
| S0-05 Web | concluída (no `main` pelo PR de integração) | #7–#12 |
| S0-06 Mobile | concluída (no `main` pelo PR de integração) | #7–#12 |
| S0-07 Infra local (`infra/docker-compose.yml`) | concluída (no `main` pelo PR de integração) | #7–#12 |
| S0-08 CI (`.github/workflows`) | concluída (no `main` pelo PR de integração) | #7–#12 |
| S0-09 Verificação do ambiente (`scripts/doctor.sh`) | concluída (merged) | #5 |

### Sprint 1 — Identidade, organizações e perfis

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S1-01 Tabelas de identidade e organizações, RLS e cifra | S0-03, S0-04 | concluída (no `main` pelo PR de integração) |
| S1-02 Tokens JWT, sessões e permissões | S1-01 | concluída (no `main` pelo PR de integração) |
| S1-03 OTP do cliente e canais de envio | S1-02 | concluída (no `main` pelo PR de integração) |
| S1-04 Organizações, convites e activação de staff | S1-03 | concluída (no `main` pelo PR de integração) |
| S1-05 Login de staff e gestão de utilizadores | S1-04 | concluída (no `main` pelo PR de integração) |
| S1-06 Perfil e contactos do cliente | S1-03 | concluída (no `main` pelo PR de integração) |
| S1-07 Web: sessão e área do cliente | S0-05, S1-06 | concluída (no `main` pelo PR de integração) |
| S1-08 Web: staff, organizações e utilizadores | S1-07, S1-05 | concluída (no `main` pelo PR de integração) |
| S1-09 Mobile: apresentação, login e perfil | S0-06, S1-06 | concluída (no `main` pelo PR de integração) |

O Sprint 1 só começa depois de o Sprint 0 estar todo no `main`.

### Sprint 2 — Veículos, condutores e catálogo

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S2-01 Módulo `document`: ficheiros, `FileStorage`, `FileAccessPolicy`, download (ADR-005) | S1 | em revisão (branch `task/S2-01-modulo-document`) |
| S2-02 Tabelas de referência, configuração da plataforma e formatos de matrícula na BD | S2-01 | em revisão (branch `task/S2-02-referencias-e-matriculas`) |
| S2-03 Veículos e condutores do cliente (inclui `VehicleRegistryPort` com mock) | S2-02 | em revisão (branch `task/S2-03-veiculos-e-condutores`) |
| S2-04 Catálogo: produtos, versões, coberturas, documentos, publicação, catálogo público | S2-02 | pronta |
| S2-05 Web seguradora: editor de produtos; web admin: tabelas de referência | S2-04 | pronta |
| S2-06 Web cliente: veículos e condutores | S2-03 | pronta |
| S2-07 Mobile: veículos e condutores (câmara do livrete) | S2-03 | pronta |

### Sprint 3 — Motor tarifário e cotações

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S3-01 Modelo de tarifa (`rating-v1.json`), avaliador interno e casos de referência | S2 | pronta |
| S3-02 Cotações: pedido, avaliação em paralelo, tarifação externa, validade, simulador | S3-01 | pronta |
| S3-03 Web seguradora: editor de tarifa, importação/exportação CSV, simulador | S3-02 | pronta |
| S3-04 Web cliente `/cliente/simular` e web mediador `/mediador/cotacoes` | S3-02 | pronta |
| S3-05 Mobile: "Fazer seguro" até à comparação | S3-02 | pronta |

### Sprint 4 — Proposta, KYC, adequação e aceitação

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S4-01 KYC do cliente (documento e selfie) e revisão pela seguradora | S3 | pronta |
| S4-02 Propostas: numeração, estados, criação a partir da cotação, declarações, adequação, expiração | S4-01 | pronta |
| S4-03 Vistoria de danos próprios (fotos por ângulo) e revisão | S4-02 | pronta |
| S4-04 PDF da proposta e assinatura por OTP | S4-02 | pronta |
| S4-05 Submissão e aceitação (regras automáticas, decisão manual, SLA) | S4-03, S4-04 | pronta |
| S4-06 Proposta criada pelo mediador em nome do cliente | S4-05 | pronta |
| S4-07 Web seguradora: fila de propostas, KYC e vistorias | S4-05 | pronta |
| S4-08 Web cliente `/cliente/propostas` e web mediador `/mediador/propostas` | S4-06 | pronta |
| S4-09 Mobile: proposta, vistoria guiada offline, assinatura | S4-05 | pronta |

### Sprint 5 — Pagamentos

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S5-01 Formas de pagamento por seguradora (BD, API, cofre de segredos) | S4 | pronta |
| S5-02 Núcleo de pagamentos: `payment_intent`, idempotência, estados, `PaymentSucceeded` → proposta `PAID` | S5-01 | pronta |
| S5-03 Simulador `mpesa-mock` fiel à API Vodacom | S4 | pronta |
| S5-04 Adaptador M-Pesa (C2B, consulta, reversão) contra o simulador | S5-02, S5-03 | pronta |
| S5-05 Adaptadores e-Mola e cartão: interface + mock | S5-02 | pronta |
| S5-06 Infra: RabbitMQ, SFTP local, Vault, `payment-contracts` | S4 | pronta |
| S5-07 Referências SIMO em mock: gerador `MOCK_SIMO_V1` e troca para o algoritmo real | S5-02 | pronta |
| S5-08 Ficheiro de liquidação SIMO em mock: parser `MOCK_SIMO_V1` e simulador `simo-mock` | S5-07, S5-09 | pronta |
| S5-09 `payment-file-worker` (SFTP, agendamento, publicação) | S5-06 | pronta |
| S5-10 Consumidor, reconciliação e excepções | S5-02, S5-06, S5-07 | pronta |
| S5-11 Ecrãs de pagamento do cliente (web e app) | S5-02, S5-04 | pronta |
| S5-12 Web seguradora e admin: configuração, pagamentos, excepções, ficheiros, estado do worker | S5-01, S5-10 | pronta |
| S5-13 Algoritmo SIMO real de referências | S5-07 | bloqueada — pedir ao proprietário do produto (algoritmo SIMO, via banco da seguradora) |
| S5-14 Layout SIMO real do ficheiro de liquidação | S5-08 | bloqueada — pedir ao proprietário do produto (layout do ficheiro, via banco da seguradora) |

### Sprint 6 — Emissão, verificação, notificações, Release 1

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S6-01 Emissão da apólice, prestações e certificado (dados) | S5 | pronta |
| S6-02 PDF do certificado com QR e documentos da apólice | S6-01, S6-04 | pronta |
| S6-03 Verificação pública e área da polícia (API, CAPTCHA, registo) | S6-01 | pronta |
| S6-04 Notificações: modelos, mensagens, preferências, dispositivos, push; aviso de cotação a expirar (`QuoteExpiryNotifierJob` no `rating`) | S5 | pronta |
| S6-05 Web: `/verificar` público, área PRM, `/seguradora/apolices`, `/admin/notificacoes` | S6-03, S6-04 | pronta |
| S6-06 Web cliente: apólices, certificado, notificações e preferências | S6-02, S6-04 | pronta |
| S6-07 Mobile: apólices offline, "mostrar à polícia", push e deep links | S6-02, S6-04 | pronta |
| S6-08 Release 1: staging, observabilidade, carga (k6), ASVS nível 1, distribuição do APK | S6-05, S6-06, S6-07 | pronta — deploy em staging bloqueado (alojamento por decidir) |

### Sprint 7 — Ciclo de vida da apólice e frotas

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S7-01 Jobs diários e máquina de estados da apólice (avisos, prestações em atraso, resolução, caducidade) | S6 | pronta |
| S7-02 Pagamento de prestações | S7-01 | pronta |
| S7-03 Renovação (proposta 30 dias antes, nova tarifa, bónus/malus) | S7-01 | pronta |
| S7-04 Endossos (alterações com ou sem prémio) e decisão da seguradora | S7-01 | pronta |
| S7-05 Venda, substituição de veículo, cancelamento e estornos | S7-04 | pronta |
| S7-06 Frotas: apólice de frota, importação CSV, inclusões e exclusões pro rata, área `/frota` (backend) | S7-04 | pronta |
| S7-07 Web cliente e seguradora: detalhe da apólice com linha temporal e acções; web `/frota` | S7-05, S7-06 | pronta |
| S7-08 Mobile: acções na apólice e calendário de prestações | S7-05 | pronta |

### Sprint 8 — Sinistros I

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S8-01 Sinistro: tabelas, numeração, estados até `REGISTERED`, linha temporal | S7 | pronta |
| S8-02 Declaração amigável: sincronização idempotente, assinaturas (desenhada e OTP), assinatura por link | S8-01 | pronta |
| S8-03 Participação: documentos, submissão, validação da cobertura, prazos, triagem, ligação automática | S8-02 | pronta |
| S8-04 Terceiro lesado: participação por OTP contra a apólice de outro | S8-03 | pronta |
| S8-05 Seguradora: fila, atribuição, registo e rejeição (API) | S8-03 | pronta |
| S8-06 Web seguradora: fila e detalhe do sinistro | S8-05 | pronta |
| S8-07 Web cliente: participação, terceiro lesado, assinatura por link | S8-04 | pronta |
| S8-08 Mobile: "Tive um acidente" offline (passos, câmara, croquis, QR, assinatura, fila de envio) | S8-04 | pronta |

### Sprint 9 — Sinistros II e Release 2

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S9-01 Peritagem: atribuição, agenda, relatório (API do perito) | S8 | pronta |
| S9-02 Oficinas: perfil, escolha pelo cliente, orçamento, reparação, factura (API) | S8 | pronta |
| S9-03 Responsabilidade e proposta de indemnização (cálculos), aceitação e contestação | S9-01, S9-02 | pronta |
| S9-04 Pagamento da indemnização, dupla aprovação, provisão, encerramento, `ClaimPaid` | S9-03 | pronta |
| S9-05 Web `/perito` e `/oficina` | S9-01, S9-02 | pronta |
| S9-06 Web seguradora: separadores do sinistro e calculadora de perda total | S9-04 | pronta |
| S9-07 Web cliente: linha temporal, oficina com mapa, proposta | S9-03 | pronta |
| S9-08 Mobile: linha temporal, oficina com mapa, proposta | S9-03 | pronta |
| S9-09 Release 2: ponta a ponta, perda total, indemnização em sandbox, manuais | S9-05 a S9-08 | pronta — parte em staging depende do alojamento |

### Sprint 10 — Reclamações, provedor e assistência

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S10-01 Reclamações: tabelas, estados, SLA configurável, mensagens e notas internas | S9 | pronta |
| S10-02 Provedor do cliente: escalamento, recomendação, resposta da seguradora | S10-01 | pronta |
| S10-03 Processos externos, reclamação automática a partir de `ClaimDisputed`, relatório mensal | S10-01 | pronta |
| S10-04 Assistência: pedido, elegibilidade, despacho por província, estados, alerta | S9 | pronta |
| S10-05 Web seguradora `/seguradora/reclamacoes` e web `/provedor` | S10-02, S10-03 | pronta |
| S10-06 Web `/assistencia` (prestador) e web cliente (reclamações e assistência) | S10-02, S10-03, S10-04 | pronta |
| S10-07 Mobile: reclamar, conversar, provedor, assistência com localização | S10-02, S10-03, S10-04 | pronta |

### Sprint 11 — Mediadores, antifraude, relatórios e auditoria

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S11-01 Nomeações de mediadores e carteira (API); restrição de cotação e proposta | S10 | pronta |
| S11-02 Comissões: cálculo, extractos mensais, estorno | S11-01 | pronta |
| S11-03 Antifraude: motor de regras, 7 regras, alertas | S10 | pronta |
| S11-04 Painéis da seguradora (vistas materializadas) | S10 | pronta |
| S11-05 Exportações ASFPM configuráveis e área `/supervisao` (API) | S11-02, S11-04 | pronta |
| S11-06 Auditoria: só-de-inserção, leituras sensíveis, pesquisa e exportação (API) | S10 | pronta |
| S11-07 Web: mediador, antifraude, painéis, supervisão, auditoria | S11-01 a S11-06 | pronta |
| S11-08 Web cliente e mobile: mediador associado à apólice | S11-01 | pronta |

### Sprint 12 — USSD, endurecimento, piloto e Release 3

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S12-01 USSD: porta do agregador, simulador, sessões e menus | S11 | pronta |
| S12-02 USSD: compra de RC, pagamento, certificado por SMS, verificação | S12-01 | pronta |
| S12-03 Mobile: OCR do livrete e do documento (ML Kit) | S11 | pronta |
| S12-04 Mobile: login biométrico e leitura automática do código por SMS (SMS Retriever) | S11 | pronta |
| S12-05 Inglês completo na web, na app e nas mensagens | S11 | pronta |
| S12-06 Segurança: preparação para ASVS nível 2 e correcções do pentest | S11 | pronta — pentest externo bloqueado (entidade por contratar) |
| S12-07 Carga, cópias de segurança e recuperação (RPO/RTO), runbook | S11 | pronta — execução em staging depende do alojamento |
| S12-08 Observabilidade e alertas de negócio | S11 | pronta |
| S12-09 Acessibilidade na app e na web | S11 | pronta |
| S12-10 Publicação nas lojas (Google Play, App Store) | S12-03, S12-04, S12-09 | pronta — publicação bloqueada (validação jurídica e contas das lojas) |
| S12-11 Release 3: critérios de entrada no piloto | todas | pronta — vários critérios dependem de decisões externas |

Cada sprint só começa depois de o anterior estar todo no `main`. As fichas dos Sprints 2 a 12 seguem o `docs/architecture/modelo-transversal.md` e as decisões em `docs/architecture/decisoes-reconciliacao.md`; vão ser revistas no início de cada sprint à luz do que foi implementado.

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
- O NUIT valida só 9 dígitos (sem dígito de controlo). Os formatos de matrícula estão na tabela `catalog.plate_format` (S2-02), editáveis pela API sem reiniciar.
- **MinIO:** a imagem `minio/minio` deixou de estar no Docker Hub. O armazenamento de ficheiros usa disco local em desenvolvimento e S3 em produção (ADR-005, S2-01). O e-mail de desenvolvimento usa o **Mailpit** (o MailHog deixou de ser mantido).
- **A partir do S1-01** a aplicação liga-se à BD como `moseguros_app_user` (sem privilégios de dono) e o Flyway como dono; os testes precisam do `init-roles.sql`. Ver a adenda do ADR-002.
- **Chave dos JWT (S1-02):** fora dos perfis `local` e `test` o backend não arranca sem `MOSEGUROS_JWT_PRIVATE_KEY` (PEM PKCS#8, RSA 2048) e `MOSEGUROS_JWT_KEY_ID`. Em `local`/`test` gera um par no arranque: os tokens deixam de valer quando o backend reinicia.
- **Códigos de erro (S1-02):** `api/error-codes.json` é gerado a partir de `ErrorCodes`/`FieldErrorCodes` (`./mvnw test -Dtest=ErrorCodesExportTest -Derror-codes.export=true`) e verificado pelo `scripts/check-openapi.sh`.
- **OTP (S1-03):** fora dos perfis `local` e `test` o backend não arranca sem `MOSEGUROS_OTP_PEPPER` (segredo do HMAC dos códigos); `MOSEGUROS_NOTIFICATION_WEBHOOK_SECRET` (recibos de entrega) vazio recusa todos os recibos. No perfil `local` o código vem no cabeçalho `X-Debug-Otp` (proibido em `prod`). Teste manual com o `sms-mock` em `infra/README.md`.
- **Primeiro administrador (S1-04):** com `MOSEGUROS_BOOTSTRAP_PLATFORM_ADMIN_EMAIL` definido, o arranque convida esse e-mail para `PLATFORM_ADMIN` se ainda não houver nenhum activo nem um convite pendente. No compose é `admin@moseguros.local`: o e-mail com o link aparece no Mailpit (`http://localhost:8025`). Os links usam `MOSEGUROS_WEB_BASE_URL` (5173 local, 3000 no compose); a activação é o ecrã `/convite/{token}` (S1-08).
- **Sessão da app (S1-09):** refresh token em `flutter_secure_storage` (`refresh_token`), token de acesso só em memória, flag da apresentação `onboarding_done` em `shared_preferences`. O `AuthInterceptor` (Dio) segue as regras da web. O cliente Dart gerado não regista `InnerEnum` (canais de `getOtpOptions`): a app acrescenta-o em `lib/core/api/api_serializers.dart`. Testes de widget com `http_mock_adapter`; os de integração correm também com `-d flutter-tester`.
- **Sessão da web (S1-07):** o token de acesso fica só em memória; o refresh token do cliente em `localStorage` (`moseguros.customer.rt`) e o do staff em `sessionStorage` (`moseguros.staff.rt`). Os interceptores de `web/src/api/http.ts` nunca mandam `Authorization` para `/api/v1/auth/**` e renovam uma única vez para vários 401 simultâneos. As áreas de staff exigem sessão de staff da própria área (S1-08); itens de menu e páginas sem a permissão (`me.permissions`) não aparecem ou vão para `/sem-acesso`.
- **Perfil e contactos do cliente (S1-06):** `/me/profile` e `/me/contacts` exigem `self:profile` (staff → 403). O documento e o NUIT só saem mascarados (resposta e auditoria). Os códigos de confirmação de contacto (`VERIFY_CONTACT`) contam para os limites de OTP do identificador.
- **Ficheiros (S2-01):** `moseguros.files.storage` (`filesystem` ou `s3`) não tem valor por defeito: sem ele o backend não arranca. Local em `backend/var/files` (`MOSEGUROS_FILES_DIR`), testes em `${java.io.tmpdir}/moseguros-test-files`, compose no volume `files`; staging/prod em S3 (`MOSEGUROS_S3_BUCKET`, `MOSEGUROS_S3_REGION`, opcionais `MOSEGUROS_S3_ENDPOINT` e `MOSEGUROS_S3_PATH_STYLE`; credenciais pela cadeia do AWS SDK). Cada `ownerType` precisa de uma `FileAccessPolicy` (bean no módulo dono); sem autorização o download dá 404. O `S3FileStorageIT` usa a imagem `adobe/s3mock:3.12.0`.
- **Flyway out-of-order (S2-01):** `spring.flyway.out-of-order: true` em todos os perfis: uma migração que chega ao `main` depois de outra com carimbo maior é aplicada na mesma.
- **Dados de demonstração (S2-01):** só no perfil `local` (`db/dev/R__dev_seed_s2.sql`): Seguradora Demo, Corretora Demo, convites `dev-convite-gestor-demo`/`dev-convite-corretor-demo` e o cliente `+258840000001`. Ver `infra/README.md`.
- **Jobs (S2-01):** `@Scheduled` só com `moseguros.jobs.enabled=true` (defeito; `false` nos testes). Sem ShedLock até ao S6: uma só instância.
- **Referências e parâmetros (S2-02):** marcas, modelos e categorias de carta em `catalog.reference_value`; parâmetros da plataforma (`MTPL_MIN_CAPITAL`, provisório) em `catalog.platform_setting` (chaves novas só por migração, todos públicos: nunca segredos). Os formatos de matrícula activos ficam em memória até 30 s (recarregam logo após uma alteração na mesma instância); `moseguros.plates` deixou de existir. Matrícula normalizada com mais de 15 caracteres é sempre `INVALID_PLATE`.
- **Veículos e condutores (S2-03):** `/me/vehicles` e `/me/drivers` exigem `self:vehicles` (staff → 403) e só mostram os do próprio cliente (RLS; o âmbito `ORG` não vê estas tabelas). O número da carta é cifrado e só sai mascarado (`****1234`); os repetidos procuram-se pelo índice cego (`BlindIndex`, HMAC-SHA256): fora dos perfis `local` e `test` o backend não arranca sem `MOSEGUROS_BLIND_INDEX_KEY` (32 bytes em Base64, diferente da chave de cifra; mudá-la invalida os hashes gravados). O registo automóvel (INATRO) é simulado em todos os perfis (`moseguros.vehicle.registry.provider: mock`; matrículas de teste em `infra/README.md`). Retirar um veículo é sempre permitido até o `policy` implementar o `VehicleRemovalGuard` (S6-01).
- **Fornecedores reais de SMS e WhatsApp** ainda não foram escolhidos: no Sprint 1 só há mocks (questão no S1-03).
- **Contrato OpenAPI** (S0-04): `api/openapi.yaml` é gerado, não se edita à mão. Regenerar com `cd backend && ./mvnw -B -Popenapi verify` (usa o `OpenApiExportIT`, precisa de Docker); `./scripts/check-openapi.sh` falha se estiver desactualizado.
- **SIMO:** até haver o algoritmo e o layout oficiais, o pagamento por referência funciona com o mock `MOCK_SIMO_V1` e o simulador `simo-mock` (S5-07, S5-08), em `local`, `test` e `staging`; em `prod` é proibido. **Bloqueadas:** S5-13 e S5-14 (algoritmo e layout reais) — pedir ao proprietário do produto, que os obtém do banco da seguradora ao aderir ao serviço de cobrança.

## Fonte de verdade

As fichas em `docs/tasks/`, o `AGENTS.md` e os ADRs são a fonte de verdade para implementar. Os documentos abaixo, fora do repositório, explicam o contexto e o porquê; se divergirem das fichas, valem as fichas.

- Levantamento de requisitos: Seguro Automóvel em Moçambique (claude.ai)
- Plano de Implementação por Sprints (claude.ai)
