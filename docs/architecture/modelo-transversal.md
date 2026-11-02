# Modelo transversal — Sprints 2 a 12

Este documento fixa o que tem de ser igual em todas as fichas dos Sprints 2 a 12: quem é dono de cada tabela, o sentido das dependências entre módulos, os contratos públicos (`<Modulo>Api` e eventos), as permissões, a numeração, os intervalos de migração e a lista de tarefas. As fichas detalham; este documento manda. Se uma ficha contradisser este documento, a ficha está errada: pare e escreva na secção "Questões" da ficha.

Regras gerais em `AGENTS.md`; isolamento em ADR-002 e adenda; fichas do Sprint 1 como exemplo do nível de detalhe esperado.

## 1. Dependências entre módulos

`A → B` quer dizer "A pode usar `BApi` e ouvir eventos de B". Nunca o contrário. O `ApplicationModules.verify()` rejeita ciclos.

```
shared        ← todos (OPEN)
organization  → shared
notification  → shared
document      → shared
identity      → organization, notification, document
vehicle       → identity, document
catalog       → organization, document
rating        → catalog, vehicle, organization, identity
payment       → organization, notification, document
proposal      → rating, catalog, vehicle, identity, organization, document, notification, payment
policy        → proposal, payment, catalog, vehicle, identity, organization, document, notification
verification  → policy, organization
claim         → policy, vehicle, identity, organization, document, notification, payment
complaint     → claim, policy, payment, organization, identity, document, notification
assistance    → policy, organization, identity, notification
fraud         → claim, policy, payment, identity, organization
reporting     → (só leitura SQL; ver §1.3)
integration   → rating, proposal, payment, policy, verification, identity, vehicle, claim, notification
```

### 1.1 Inversão de dependência (quando o sentido natural criaria um ciclo)

- **Pagamentos.** `payment` não conhece propostas nem apólices.
  - Define no seu pacote público a interface `PaymentTargetResolver` (`supports(targetType)`, `resolve(targetType, targetId) → PayableTarget(insurerId, payerUserId, amount, description, purpose, expiresAt)`).
  - `proposal` (`PROPOSAL`), `policy` (`INSTALMENT`, `RENEWAL`, `ENDORSEMENT`, `FLEET_INCLUSION`) e `claim` (`DEDUCTIBLE`, se vier a existir) implementam-na.
  - O `payment` encontra as implementações como beans Spring.
  - Os endpoints `POST /proposals/{id}/payment-intents` etc. ficam no módulo dono do alvo e chamam `PaymentApi.createIntent(...)`.
- **Ficheiros.** `document` guarda e serve ficheiros mas não decide quem os vê.
  - Define `FileAccessPolicy` (`supports(ownerType)`, `canRead(ownerType, ownerId, CurrentUser)`), implementada por cada módulo dono (`VEHICLE`, `PRODUCT_VERSION`, `KYC`, `PROPOSAL`, `INSPECTION`, `POLICY`, `CLAIM`, `COMPLAINT`…).
  - Sem política que diga "sim" → 404.
- **Histórico de sinistros (bónus/malus).** O `policy` não pode ouvir o `claim` (o `claim` depende do `policy`). Por isso o `claim`, ao pagar uma indemnização (S9-04), chama `PolicyApi.recordClaimOutcome(policyId, claimId, driverLicenceNumber, atFault, paidAmount, occurredAt)`, que grava em `policy.claims_history` e reduz o capital disponível da anuidade.
- **Pagamento da proposta → emissão.** O `proposal` ouve `PaymentSucceeded` (alvo `PROPOSAL`), passa a `PAID` e publica `ProposalPaid`; é este evento que o `policy` ouve para emitir (S6). Assim não há corrida entre os dois módulos.
- **Notificações.** Quem quer notificar chama `NotificationApi.notify(userId, templateCode, params, NotificationCategory)`. O `notification` nunca ouve eventos de outros módulos.

### 1.2 Eventos de domínio

Publicados com `ApplicationEventPublisher` + outbox do Spring Modulith (`event_publication`). Ouvidos com `@ApplicationModuleListener` (assíncrono, depois do commit, transacção própria).

Records imutáveis no pacote raiz do módulo que publica, com `UUID eventId`, `Instant occurredAt` e os ids necessários; nunca entidades JPA.

| Evento | Publicado por | Sprint | Ouvido por |
| --- | --- | --- | --- |
| `OrganizationSuspended`, `OrganizationReactivated` | organization | 1 | identity |
| `OtpDeliveryFailed` | notification | 1 | identity |
| `ProductVersionPublished` | catalog | 2 | — |
| `QuoteCreated` | rating | 3 | — |
| `ProposalSubmitted`, `ProposalAccepted`, `ProposalRejected`, `ProposalInfoRequested`, `ProposalExpired` | proposal | 4 | fraud (S11) |
| `ProposalPaid` | proposal | 5 | policy (S6: emite a apólice) |
| `PaymentSucceeded`, `PaymentFailed`, `PaymentRefunded` | payment | 5 | proposal (alvo `PROPOSAL` → `PAID` → publica `ProposalPaid`), policy (alvos `INSTALMENT`, `RENEWAL`, `ENDORSEMENT`, `FLEET_INCLUSION`, S7), fraud (S11) |
| `ReferenceGenerated` | payment | 5 | — (o `payment` notifica directamente) |
| `PolicyIssued`, `PolicyStatusChanged`, `PolicyRenewed`, `EndorsementApplied` | policy | 6–7 | verification (cache), fraud (S11), policy (comissões S11) |
| `ClaimReported`, `ClaimRegistered`, `ClaimRejected`, `ClaimDisputed`, `ClaimPaid`, `ClaimClosed` | claim | 8–9 | complaint (`ClaimDisputed`), fraud (S11) |
| `ComplaintSubmitted`, `ComplaintAnswered`, `ComplaintEscalated` | complaint | 10 | — |
| `AssistanceRequested` | assistance | 10 | — |

### 1.3 Excepção: `reporting`

O `reporting` (Sprint 11) não usa APIs Java de outros módulos: lê **vistas materializadas** no schema `reporting`, criadas pelas suas migrações sobre as tabelas dos outros schemas, renovadas de hora a hora por job, com um papel de BD só de leitura (`moseguros_reporting`). É a única excepção ao isolamento entre schemas e está registada no ADR-006.

## 2. Tabelas por módulo e sprint

Colunas obrigatórias e regras de `AGENTS.md` §5 em todas. Valores monetários em `bigint` (centavos) com sufixo `_amount`. As tabelas com dados de uma seguradora têm `insurer_id uuid not null` e RLS por `app.insurer_id` (ADR-002).

As tabelas de clientes têm RLS adicional pelo dono:

```sql
s in ('SYSTEM','PLATFORM')
or (s = 'ORG' and insurer_id = nullif(current_setting('app.insurer_id', true), '')::uuid)
or (s = 'CUSTOMER' and <coluna do cliente> = u)
```

| Sprint | Módulo | Tabelas novas (detalhe nas fichas) |
| --- | --- | --- |
| 2 | document | `document.file` |
| 2 | catalog | `catalog.reference_value`, `catalog.platform_setting`, `catalog.plate_format`, `catalog.product`, `catalog.product_version`, `catalog.coverage`, `catalog.product_document` |
| 2 | vehicle | `vehicle.vehicle`, `vehicle.driver`, `vehicle.vehicle_driver` |
| 3 | rating | `rating.quote_request`, `rating.quote`; `organization.insurer_profile` ganha `rating_mode`, `rating_endpoint_url`, `rating_secret_ref`, `charges jsonb` |
| 4 | identity | `identity.kyc_check` |
| 4 | proposal | `proposal.proposal`, `proposal.declaration`, `proposal.suitability_answer`, `proposal.inspection`, `proposal.inspection_photo`, `proposal.signature` |
| 4 | shared | `shared.business_sequence` (numeração) |
| 5 | payment | `payment.insurer_payment_method`, `payment.payment_intent`, `payment.payment_event`, `payment.reference_payment`, `payment.inbound_message`, `payment.payment_exception`, `payment.file_import`, `payment.idempotency_record` |
| 5 | (worker) | schema `payment_worker`: `processed_file`, `file_record`, `shedlock` |
| 6 | policy | `policy.policy`, `policy.instalment`, `policy.certificate`, `policy.policy_event` |
| 6 | verification | `verification.check_log` |
| 6 | notification | `notification.template`, `notification.message`, `notification.preference`, `notification.device` |
| 6 | shared | `shared.shedlock` (jobs agendados de todos os módulos) |
| 7 | policy | `policy.endorsement`, `policy.refund`, `policy.claims_history`, `policy.fleet`, `policy.policy_vehicle`, `policy.renewal` |
| 8 | claim | `claim.claim`, `claim.accident_statement`, `claim.statement_signature`, `claim.claim_party`, `claim.claim_document`, `claim.claim_event` |
| 9 | claim | `claim.assessment`, `claim.assessment_photo`, `claim.workshop_quote`, `claim.liability_decision`, `claim.settlement_offer`, `claim.indemnity_payment`, `claim.reserve` |
| 9 | organization | `organization.workshop_profile` (morada, província, coordenadas, especialidades), `organization.assistance_coverage` (prestador × província) |
| 10 | complaint | `complaint.complaint`, `complaint.complaint_message`, `complaint.ombudsman_case`, `complaint.external_proceeding`, `complaint.sla_config` |
| 10 | assistance | `assistance.request`, `assistance.request_event` |
| 11 | organization | `organization.broker_appointment` |
| 11 | policy | `policy.commission`, `policy.commission_statement` |
| 11 | fraud | `fraud.rule`, `fraud.alert` |
| 11 | reporting | `reporting.report_template`, `reporting.report_run`, vistas materializadas `reporting.mv_*` |
| 11 | audit | papel de BD só-de-inserção; `audit.audit_event` ganha `access_type` (`WRITE`/`READ_SENSITIVE`) |
| 12 | integration | `integration.ussd_session` |

## 3. Contratos públicos principais (`<Modulo>Api`)

Só os métodos que outros módulos usam. As fichas podem acrescentar métodos, nunca mudar estes.

| Módulo | Métodos |
| --- | --- |
| `DocumentApi` (S2) | `store(StoreFileCommand) → FileRef`, `open(UUID fileId) → FileContent`, `metadata(UUID) → FileRef`, `renderPdf(String templateCode, Map<String,Object> model, Locale) → FileRef` (S4), `delete` não existe (ficheiros não se apagam; ficam órfãos e são limpos por job ao fim de 30 dias se nunca tiverem sido ligados) |
| `VehicleApi` (S2) | `findOwned(UUID vehicleId, UUID ownerUserId) → Optional<VehicleView>`, `snapshot(UUID vehicleId) → VehicleSnapshot`, `driverSnapshot(UUID driverId) → DriverSnapshot` |
| `CatalogApi` (S2) | `findPublishedVersions(VehicleCategory, VehicleUsage, ProductType?) → List<ProductVersionView>`, `version(UUID) → ProductVersionView` (coberturas, regras, documentos), `platformSetting(String key) → String` |
| `RatingApi` (S3) | `quote(UUID quoteId) → QuoteView`, `markUsed(UUID quoteId)`, `rateRenewal(RenewalRatingRequest) → QuoteResult` (S7) |
| `IdentityApi` (S1/S4) | `customer(UUID userId) → CustomerView`, `kycStatus(UUID userId) → KycStatus`, `findOrCreateCustomerByMsisdn(Msisdn) → UUID` (S8 terceiro lesado, S12 USSD), `requestOtp/verifyOtp` para `SIGN` (S4) |
| `ProposalApi` (S4) | `view(UUID) → ProposalView`, `markConverted(UUID proposalId, UUID policyId)` (S6) |
| `PaymentApi` (S5) | `createIntent(CreateIntentCommand) → IntentView`, `intent(UUID) → IntentView`, `refund(RefundCommand) → RefundView` (S7) |
| `PolicyApi` (S6) | `findActiveByPlate(String plate, Instant at) → Optional<PolicyCoverView>`, `coverAt(UUID policyId, Instant at) → CoverStatus`, `view(UUID) → PolicyView`, `customerPolicies(UUID userId)`, `recordClaimOutcome(...)` (S7, usado pelo `claim` no S9) |
| `ClaimApi` (S8) | `view(UUID) → ClaimView` |
| `OrganizationApi` (S1+) | `findSummary(UUID)`, `insurerProfile(UUID) → InsurerProfileView` (S3), `activeAppointment(UUID brokerOrgId, UUID insurerId) → Optional<AppointmentView>` (S11), `workshopsByProvince(Province)` (S9), `assistanceProvider(UUID insurerId, Province)` (S10) |
| `NotificationApi` (S1+) | `sendOtp`, `sendEmail` (S1), `notify(UUID userId, String templateCode, Map<String,Object> params, NotificationCategory)` (S6) |

## 4. Numeração de negócio

- `shared.business_sequence (prefix varchar(4), year int, last_value bigint, primary key (prefix, year))`, criada no S4-02.
- `BusinessNumberGenerator.next(prefix)` faz `update … set last_value = last_value + 1 … returning` (ou `insert … on conflict`), na transacção de quem chama. O ano é o de `Africa/Maputo`.

| Prefixo | Formato | Sprint |
| --- | --- | --- |
| `PR` proposta | `PR-2027-000123` (6 dígitos) | 4 |
| `PG` pagamento | `PG-2027-0001234` (7 dígitos) | 5 |
| `AP` apólice | `AP-2027-000123` | 6 |
| `CT` certificado | `CT-2027-0001234` | 6 |
| `EN` endosso | `EN-2027-000123` | 7 |
| `SN` sinistro | `SN-2027-000123` | 8 |
| `RC` reclamação | `RC-2027-000123` | 10 |
| `AS` assistência | `AS-2027-000123` | 10 |
| `EX` extracto de comissões | `EX-2027-000123` | 11 |

## 5. Permissões

Cada sprint acrescenta permissões numa migração `…__identity_seed_permissions_s{n}.sql` (insere em `identity.role_permission`; nunca edita migrações anteriores). Nomes `recurso:acção`.

| Sprint | Permissão | Perfis |
| --- | --- | --- |
| 2 | `reference:manage` | PLATFORM_ADMIN |
| 2 | `product:read` | INSURER_ADMIN, PRODUCT_MANAGER, UNDERWRITER |
| 2 | `product:manage` | INSURER_ADMIN, PRODUCT_MANAGER |
| 2 | `self:vehicles` | CUSTOMER |
| 3 | `rating:manage` | INSURER_ADMIN, PRODUCT_MANAGER |
| 3 | `quote:create-for-client` | BROKER, AGENT |
| 3 | `self:quotes` | CUSTOMER |
| 4 | `proposal:read` | INSURER_ADMIN, UNDERWRITER |
| 4 | `proposal:decide` | UNDERWRITER, INSURER_ADMIN |
| 4 | `kyc:review` | UNDERWRITER, INSURER_ADMIN |
| 4 | `inspection:review` | UNDERWRITER |
| 4 | `proposal:create-for-client` | BROKER, AGENT |
| 4 | `self:proposals` | CUSTOMER |
| 5 | `payment:configure` | INSURER_ADMIN, FINANCE |
| 5 | `payment:read` | INSURER_ADMIN, FINANCE |
| 5 | `payment:resolve-exception` | FINANCE |
| 5 | `payment:link-for-client` | BROKER, AGENT |
| 5 | `self:payments` | CUSTOMER |
| 5 | `platform:operations` | PLATFORM_ADMIN (estado do worker e fontes) |
| 6 | `policy:read` | INSURER_ADMIN, UNDERWRITER, CLAIMS_HANDLER, FINANCE |
| 6 | `verification:police` | POLICE_OFFICER |
| 6 | `notification-template:manage` | PLATFORM_ADMIN |
| 6 | `self:policies` | CUSTOMER |
| 7 | `endorsement:decide` | UNDERWRITER |
| 7 | `refund:manage` | FINANCE |
| 7 | `fleet:manage` | FLEET_MANAGER |
| 8 | `claim:read` | CLAIMS_HANDLER, INSURER_ADMIN |
| 8 | `claim:triage` | CLAIMS_HANDLER |
| 8 | `self:claims` | CUSTOMER |
| 9 | `claim:decide` | CLAIMS_HANDLER |
| 9 | `indemnity:approve` | FINANCE (segundo aprovador: INSURER_ADMIN) |
| 9 | `assessment:work` | ASSESSOR |
| 9 | `workshop:work` | WORKSHOP |
| 10 | `complaint:read`, `complaint:handle` | COMPLAINTS_OFFICER, INSURER_ADMIN |
| 10 | `ombudsman:handle` | OMBUDSMAN |
| 10 | `assistance:work` | ASSISTANCE_PROVIDER |
| 10 | `self:complaints`, `self:assistance` | CUSTOMER |
| 11 | `broker:portfolio` | BROKER, AGENT |
| 11 | `commission:read` | BROKER, AGENT, FINANCE |
| 11 | `commission:manage` | FINANCE |
| 11 | `appointment:manage` | INSURER_ADMIN |
| 11 | `fraud:review` | CLAIMS_HANDLER, INSURER_ADMIN |
| 11 | `report:read` | INSURER_ADMIN, FINANCE |
| 11 | `report:template-manage` | PLATFORM_ADMIN |
| 11 | `regulator:read` | REGULATOR_VIEWER |
| 11 | `audit:read` | PLATFORM_ADMIN, INSURER_ADMIN |

## 6. Intervalos de carimbos de migração

As migrações de cada sprint usam carimbos dentro do intervalo do sprint, e cada tarefa usa o seu bloco de 10 minutos. Por exemplo, S2-03 usa `202612010300` a `202612010309`: dia `01`, hora `03`, que é o número da tarefa.

| Sprint | Intervalo |
| --- | --- |
| 2 | `2026120100xx` – `2026120123xx` (dia 01; hora = n.º da tarefa) |
| 3 | `20261215HHmm` |
| 4 | `20270101HHmm` |
| 5 | `20270115HHmm` |
| 6 | `20270201HHmm` |
| 7 | `20270215HHmm` |
| 8 | `20270301HHmm` |
| 9 | `20270315HHmm` |
| 10 | `20270401HHmm` |
| 11 | `20270415HHmm` |
| 12 | `20270501HHmm` |

O worker de ficheiros (S5) tem o seu próprio histórico Flyway, no schema `payment_worker`, começando em `V202701150000`.

## 7. Infra-estrutura comum introduzida ao longo dos sprints

| Sprint | O quê | Decisão |
| --- | --- | --- |
| 2 | Armazenamento de ficheiros | `FileStorage` com `FilesystemFileStorage` (local, testes, compose com volume `files`) e `S3FileStorage` (AWS SDK v2; staging e produção, qualquer serviço compatível com S3). ADR-005. Limite 10 MB por ficheiro; tipos permitidos `image/jpeg`, `image/png`, `image/heic`, `application/pdf`; SHA-256 calculado no upload |
| 2 | Upload e download | `POST` multipart no módulo dono (ex.: `/me/vehicles/{id}/registration-doc`); download `GET /api/v1/files/{id}` (módulo `document`, decide pela `FileAccessPolicy`) com `Content-Disposition` e `Cache-Control: private, no-store` |
| 4 | PDF | Modelos Thymeleaf (XHTML) em `backend/src/main/resources/templates/pdf/` → Flying Saucer (`org.xhtmlrenderer:flying-saucer-pdf-openpdf`). Fonte Inter embebida. Cada PDF guarda o SHA-256 |
| 3 | HTTP de saída | Resilience4j (`resilience4j-spring-boot3`): timeout, circuit breaker e bulkhead por seguradora/fornecedor. Clientes com `RestClient` |
| 5 | Cofre de segredos | `SecretStore` (`put`, `get`, `delete`, `exists`) com `VaultSecretStore` (HashiCorp Vault KV v2) e `InMemorySecretStore` (testes). Compose: Vault em modo dev. A BD guarda só a referência (`secret/…`) |
| 5 | Mensageria | RabbitMQ 3.13 (ADR-004) |
| 6 | Jobs | `@Scheduled` + ShedLock (`net.javacrumbs.shedlock`, JDBC, tabela `shared.shedlock`). Cron em `Africa/Maputo`. Cada job é idempotente e regista início, fim e contagens em log estruturado |
| 6 | Push | `PushSender` com `FcmPushSender` (Firebase Admin SDK) e `MockPushSender` |
| 6 | CAPTCHA | `CaptchaVerifier` com `TurnstileCaptchaVerifier` (Cloudflare Turnstile) e `AlwaysValidCaptchaVerifier` (local/testes). Decisão a confirmar pelo proprietário do produto |
| 6 | Observabilidade | Micrometer + Prometheus (`/api/v1/prometheus`, rede interna), Grafana no compose `observability` |
| 8 | Mapas | Web: Leaflet + OpenStreetMap; mobile: `flutter_map` + OpenStreetMap. Sem chave de API |
| 12 | USSD | `UssdAggregatorPort` + simulador próprio em `infra/mocks/ussd-mock` |

**Âmbito em listeners e jobs.** Os `@ApplicationModuleListener` e os jobs agendados correm sem utilizador autenticado (âmbito `NONE`, que não vê nada). Por isso as classes `…Listener` e `…Job` do pacote `application` usam `@RunAsSystem` (regra do S1-01). Têm de filtrar no código pela seguradora ou pelo cliente que vem no evento, e nunca devolvem dados a um utilizador.

**Dados de demonstração.** A partir do S2, `backend/src/main/resources/db/dev/` tem migrações `R__dev_seed_s{n}.sql` (repetíveis), aplicadas só no perfil `local` (`spring.flyway.locations: classpath:db/migration,classpath:db/dev`). Cada sprint acrescenta o seu ficheiro, com dados para testar à mão:
- seguradora "Seguradora Demo" (`DEMO`) e o seu gestor;
- um corretor;
- produtos publicados;
- um cliente de teste `+258840000001`.

Os dados de demonstração nunca vão para staging ou produção.

## 8. Lista de tarefas por sprint

As fichas usam exactamente estes IDs, títulos e dependências. "S{n-1}" quer dizer "todo o sprint anterior no `main`".

### Sprint 2 — Veículos, condutores e catálogo

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S2-01 | Módulo `document`: ficheiros, `FileStorage`, `FileAccessPolicy`, download (ADR-005) | S1 |
| S2-02 | Tabelas de referência, configuração da plataforma e formatos de matrícula na BD | S2-01 |
| S2-03 | Veículos e condutores do cliente (inclui `VehicleRegistryPort` com mock) | S2-02 |
| S2-04 | Catálogo: produtos, versões, coberturas, documentos, publicação, catálogo público | S2-02 |
| S2-05 | Web seguradora: editor de produtos; web admin: tabelas de referência | S2-04 |
| S2-06 | Web cliente: veículos e condutores | S2-03 |
| S2-07 | Mobile: veículos e condutores (câmara do livrete) | S2-03 |

### Sprint 3 — Motor tarifário e cotações

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S3-01 | Modelo de tarifa (`rating-v1.json`), avaliador interno e casos de referência | S2 |
| S3-02 | Cotações: pedido, avaliação em paralelo, tarifação externa, validade, simulador | S3-01 |
| S3-03 | Web seguradora: editor de tarifa, importação/exportação CSV, simulador | S3-02 |
| S3-04 | Web cliente `/cliente/cotar` e web mediador `/mediador/cotacoes` | S3-02 |
| S3-05 | Mobile: "Fazer seguro" até à comparação | S3-02 |

### Sprint 4 — Proposta, KYC, adequação e aceitação

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S4-01 | KYC do cliente (documento e selfie) e revisão pela seguradora | S3 |
| S4-02 | Propostas: numeração, estados, criação a partir da cotação, declarações, adequação, expiração | S4-01 |
| S4-03 | Vistoria de danos próprios (fotos por ângulo) e revisão | S4-02 |
| S4-04 | PDF da proposta e assinatura por OTP | S4-02 |
| S4-05 | Submissão e aceitação (regras automáticas, decisão manual, SLA) | S4-03, S4-04 |
| S4-06 | Proposta criada pelo mediador em nome do cliente | S4-05 |
| S4-07 | Web seguradora: fila de propostas, KYC e vistorias | S4-05 |
| S4-08 | Web cliente `/cliente/propostas` e web mediador `/mediador/propostas` | S4-06 |
| S4-09 | Mobile: proposta, vistoria guiada offline, assinatura | S4-05 |

### Sprint 5 — Pagamentos

IDs do plano, com uma tarefa nova (S5-12):

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S5-01 | Formas de pagamento por seguradora (BD, API, cofre de segredos) | S4 |
| S5-02 | Núcleo de pagamentos: `payment_intent`, idempotência, estados, `PaymentSucceeded` → proposta `PAID` | S5-01 |
| S5-03 | Simulador `mpesa-mock` fiel à API Vodacom | S4 |
| S5-04 | Adaptador M-Pesa (C2B, consulta, reversão) contra o simulador | S5-02, S5-03 |
| S5-05 | Adaptadores e-Mola e cartão: interface + mock | S5-02 |
| S5-06 | Infra: RabbitMQ, SFTP local, Vault, `payment-contracts` | S4 |
| S5-07 | **Algoritmo SIMO de referências — BLOQUEADA** | S5-02 |
| S5-08 | **Parser do ficheiro SIMO — BLOQUEADA** | S5-06 |
| S5-09 | `payment-file-worker` (SFTP, agendamento, publicação) | S5-06 (parser fake até S5-08) |
| S5-10 | Consumidor, reconciliação e excepções | S5-06, S5-02 (gerador fake até S5-07) |
| S5-11 | Ecrãs de pagamento do cliente (web e app) | S5-02, S5-04 |
| S5-12 | Web seguradora e admin: configuração, pagamentos, excepções, ficheiros, estado do worker | S5-01, S5-10 |

### Sprint 6 — Emissão, verificação, notificações, Release 1

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S6-01 | Emissão da apólice, prestações e certificado (dados) | S5 |
| S6-02 | PDF do certificado com QR e documentos da apólice | S6-01 |
| S6-03 | Verificação pública e área da polícia (API, CAPTCHA, registo) | S6-01 |
| S6-04 | Notificações: modelos, mensagens, preferências, dispositivos, push | S5 |
| S6-05 | Web: `/verificar` público, área PRM, `/seguradora/apolices`, `/admin/notificacoes` | S6-03, S6-04 |
| S6-06 | Web cliente: apólices, certificado, notificações e preferências | S6-02, S6-04 |
| S6-07 | Mobile: apólices offline, "mostrar à polícia", push e deep links | S6-02, S6-04 |
| S6-08 | Release 1: staging, observabilidade, carga (k6), ASVS nível 1, distribuição do APK | S6-05, S6-06, S6-07 |

### Sprint 7 — Ciclo de vida da apólice e frotas

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S7-01 | Jobs diários e máquina de estados da apólice (avisos, prestações em atraso, resolução, caducidade) | S6 |
| S7-02 | Pagamento de prestações | S7-01 |
| S7-03 | Renovação (proposta 30 dias antes, nova tarifa, bónus/malus) | S7-01 |
| S7-04 | Endossos (alterações com ou sem prémio) e decisão da seguradora | S7-01 |
| S7-05 | Venda, substituição de veículo, cancelamento e estornos | S7-04 |
| S7-06 | Frotas: apólice de frota, importação CSV, inclusões e exclusões pro rata, área `/frota` (backend) | S7-04 |
| S7-07 | Web cliente e seguradora: detalhe da apólice com linha temporal e acções; web `/frota` | S7-05, S7-06 |
| S7-08 | Mobile: acções na apólice e calendário de prestações | S7-05 |

### Sprint 8 — Sinistros I

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S8-01 | Sinistro: tabelas, numeração, estados até `REGISTERED`, linha temporal | S7 |
| S8-02 | Declaração amigável: sincronização idempotente, assinaturas (desenhada e OTP), assinatura por link | S8-01 |
| S8-03 | Participação: documentos, submissão, validação da cobertura, prazos, triagem, ligação automática | S8-02 |
| S8-04 | Terceiro lesado: participação por OTP contra a apólice de outro | S8-03 |
| S8-05 | Seguradora: fila, atribuição, registo e rejeição (API) | S8-03 |
| S8-06 | Web seguradora: fila e detalhe do sinistro | S8-05 |
| S8-07 | Web cliente: participação, terceiro lesado, assinatura por link | S8-04 |
| S8-08 | Mobile: "Tive um acidente" offline (passos, câmara, croquis, QR, assinatura, fila de envio) | S8-04 |

### Sprint 9 — Sinistros II e Release 2

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S9-01 | Peritagem: atribuição, agenda, relatório (API do perito) | S8 |
| S9-02 | Oficinas: perfil, escolha pelo cliente, orçamento, reparação, factura (API) | S8 |
| S9-03 | Responsabilidade e proposta de indemnização (cálculos), aceitação e contestação | S9-01, S9-02 |
| S9-04 | Pagamento da indemnização, dupla aprovação, provisão, encerramento, `ClaimPaid` | S9-03 |
| S9-05 | Web `/perito` e `/oficina` | S9-01, S9-02 |
| S9-06 | Web seguradora: separadores do sinistro e calculadora de perda total | S9-04 |
| S9-07 | Web cliente: linha temporal, oficina com mapa, proposta | S9-03 |
| S9-08 | Mobile: linha temporal, oficina com mapa, proposta | S9-03 |
| S9-09 | Release 2: ponta a ponta, perda total, indemnização em sandbox, manuais | S9-05 a S9-08 |

### Sprint 10 — Reclamações, provedor e assistência

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S10-01 | Reclamações: tabelas, estados, SLA configurável, mensagens e notas internas | S9 |
| S10-02 | Provedor do cliente: escalamento, recomendação, resposta da seguradora | S10-01 |
| S10-03 | Processos externos, reclamação automática a partir de `ClaimDisputed`, relatório mensal | S10-01 |
| S10-04 | Assistência: pedido, elegibilidade, despacho por província, estados, alerta | S9 |
| S10-05 | Web seguradora `/seguradora/reclamacoes` e web `/provedor` | S10-02, S10-03 |
| S10-06 | Web `/assistencia` (prestador) e web cliente (reclamações e assistência) | S10-03, S10-04 |
| S10-07 | Mobile: reclamar, conversar, provedor, assistência com localização | S10-03, S10-04 |

### Sprint 11 — Mediadores, antifraude, relatórios e auditoria

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S11-01 | Nomeações de mediadores e carteira (API); restrição de cotação e proposta | S10 |
| S11-02 | Comissões: cálculo, extractos mensais, estorno | S11-01 |
| S11-03 | Antifraude: motor de regras, 7 regras, alertas | S10 |
| S11-04 | Painéis da seguradora (vistas materializadas) | S10 |
| S11-05 | Exportações ASFPM configuráveis e área `/supervisao` (API) | S11-04 |
| S11-06 | Auditoria: só-de-inserção, leituras sensíveis, pesquisa e exportação (API) | S10 |
| S11-07 | Web: mediador, antifraude, painéis, supervisão, auditoria | S11-01 a S11-06 |
| S11-08 | Web cliente e mobile: mediador associado à apólice | S11-01 |

### Sprint 12 — USSD, endurecimento, piloto e Release 3

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S12-01 | USSD: porta do agregador, simulador, sessões e menus | S11 |
| S12-02 | USSD: compra de RC, pagamento, certificado por SMS, verificação | S12-01 |
| S12-03 | Mobile: OCR do livrete e do documento (ML Kit) | S11 |
| S12-04 | Mobile: login biométrico | S11 |
| S12-05 | Inglês completo na web, na app e nas mensagens | S11 |
| S12-06 | Segurança: preparação para ASVS nível 2 e correcções do pentest | S11 |
| S12-07 | Carga, cópias de segurança e recuperação (RPO/RTO), runbook | S11 |
| S12-08 | Observabilidade e alertas de negócio | S11 |
| S12-09 | Acessibilidade na app e na web | S11 |
| S12-10 | Publicação nas lojas (Google Play, App Store) | S12-03, S12-04, S12-09 |
| S12-11 | Release 3: critérios de entrada no piloto | todas |

## 9. Decisões que esperam pelo proprietário do produto

As fichas registam-nas em "Questões". Quando uma decisão bloqueia a tarefa, a ficha diz "BLOQUEADA" no estado e o que se pode adiantar.

| Tema | Ficha | Bloqueia? |
| --- | --- | --- |
| Capital mínimo da RC (`MTPL_MIN_CAPITAL`, valor inicial 3 000 000 MT) | S2-02 | não (parâmetro editável) |
| Casos de referência da tarifa com a seguradora piloto | S3-01 | não (casos provisórios marcados como tal) |
| Encargos e impostos reais (selo, taxas) | S3-01 | não (configuráveis) |
| Limiar do KYC simplificado e limites de aceitação automática | S4-05 | não (configuráveis) |
| Validação jurídica da assinatura electrónica simples | S4-04 | não para desenvolvimento; sim para produção |
| Algoritmo SIMO e layout do ficheiro | S5-07, S5-08 | **sim** |
| Documentação e credenciais e-Mola e adquirente de cartões | S5-05 | não (mock) |
| Fornecedor de CAPTCHA | S6-03 | não (porta + mock) |
| Alojamento de staging e produção, cofre de segredos gerido | S6-08 | **sim** para o deploy; não para preparar os ficheiros |
| Projecto Firebase (FCM, App Distribution) | S6-04, S6-07 | não (mock) |
| Textos legais (arbitragem, ASFPM, tribunais; política de privacidade; termos) | S10-07, S12-10 | não (texto provisório marcado) |
| Prazos de reclamação do regulamento do DL 1/2026 | S10-01 | não (configuráveis) |
| Formato oficial dos relatórios ASFPM | S11-05 | não (modelos configuráveis) |
| Agregador USSD e código curto | S12-01 | não para o simulador; sim para produção |
| Acessos INATRO e PRM | S12 (melhorias diferidas) | não (mocks) |
| Entidade de pentest externo | S12-06 | sim para a parte externa |
