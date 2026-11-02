# Estado do projecto

Actualizado em 02/11/2026.

## Onde estamos

### Sprint 0 — Fundações

| Tarefa | Estado | PR |
| --- | --- | --- |
| S0-01 Fichas e ADRs | concluída (merged) | #1 |
| S0-02 Esqueleto do backend | concluída (merged) | #2 |
| S0-03 Tipos partilhados e auditoria | pronta — começar depois do merge do #2 | — |
| S0-04 Contrato OpenAPI | pronta — depois do S0-03 (usa os value objects) | — |
| S0-05 Web | depende de S0-04 | — |
| S0-06 Mobile | depende de S0-04 | — |
| S0-07 Infra local | depende de S0-02 e S0-05 | — |
| S0-08 CI | depende de S0-03 a S0-07 | — |
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

### Sprint 2 — Veículos, condutores e catálogo

| Tarefa | Depende de | Estado |
| --- | --- | --- |
| S2-01 Módulo `document`: ficheiros, `FileStorage`, `FileAccessPolicy`, download (ADR-005) | S1 | pronta |
| S2-02 Tabelas de referência, configuração da plataforma e formatos de matrícula na BD | S2-01 | pronta |
| S2-03 Veículos e condutores do cliente (inclui `VehicleRegistryPort` com mock) | S2-02 | pronta |
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
| S5-07 Algoritmo SIMO de referências | S5-02 | bloqueada — pedir ao proprietário do produto (algoritmo SIMO) |
| S5-08 Parser do ficheiro SIMO | S5-06 | bloqueada — pedir ao proprietário do produto (layout do ficheiro SIMO) |
| S5-09 `payment-file-worker` (SFTP, agendamento, publicação) | S5-06 (parser fake até S5-08) | pronta |
| S5-10 Consumidor, reconciliação e excepções | S5-06, S5-02 (gerador fake até S5-07) | pronta |
| S5-11 Ecrãs de pagamento do cliente (web e app) | S5-02, S5-04 | pronta |
| S5-12 Web seguradora e admin: configuração, pagamentos, excepções, ficheiros, estado do worker | S5-01, S5-10 | pronta |

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
| S11-05 Exportações ASFPM configuráveis e área `/supervisao` (API) | S11-04 | pronta |
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
- O NUIT valida só 9 dígitos (sem dígito de controlo). Os formatos de matrícula são configuráveis (S0-03).
- **MinIO:** a imagem `minio/minio` deixou de estar no Docker Hub. O armazenamento de ficheiros usa disco local em desenvolvimento e S3 em produção (ADR-005, S2-01). O e-mail de desenvolvimento usa o **Mailpit** (o MailHog deixou de ser mantido).
- **A partir do S1-01** a aplicação liga-se à BD como `moseguros_app_user` (sem privilégios de dono) e o Flyway como dono; os testes precisam do `init-roles.sql`. Ver a adenda do ADR-002.
- **Fornecedores reais de SMS e WhatsApp** ainda não foram escolhidos: no Sprint 1 só há mocks (questão no S1-03).
- **Bloqueadas:** S5-07 e S5-08 (algoritmo SIMO e layout do ficheiro de pagamentos) — pedir ao proprietário do produto antes de implementar.

## Fonte de verdade

As fichas em `docs/tasks/`, o `AGENTS.md` e os ADRs são a fonte de verdade para implementar. Os documentos abaixo, fora do repositório, explicam o contexto e o porquê; se divergirem das fichas, valem as fichas.

- Levantamento de requisitos: Seguro Automóvel em Moçambique (claude.ai)
- Plano de Implementação por Sprints (claude.ai)
