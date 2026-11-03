# Modelo transversal — Sprints 2 a 12

Este documento fixa o que tem de ser igual em todas as fichas dos Sprints 2 a 12: quem é dono de cada tabela, o sentido das dependências entre módulos, os contratos públicos (`<Modulo>Api` e eventos), as permissões, a numeração, os intervalos de migração e a lista de tarefas. As fichas detalham; este documento manda. Se uma ficha contradisser este documento, a ficha está errada: pare e escreva na secção "Questões" da ficha.

As decisões tomadas ao reconciliar as fichas escritas em paralelo estão em `decisoes-reconciliacao.md` (D1–D33) e já estão reflectidas aqui.

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
rating        → catalog, vehicle, organization, identity, notification
payment       → organization, notification, document
proposal      → rating, catalog, vehicle, identity, organization, document, notification, payment
policy        → proposal, payment, rating, catalog, vehicle, identity, organization, document, notification
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
  - `proposal` (`PROPOSAL`: `ProposalPaymentTargetResolver`, S5-02), `policy` (`PolicyPaymentTargetResolver`: `INSTALMENT` no S7-02, `RENEWAL` no S7-03, `ENDORSEMENT` no S7-04, `FLEET_INCLUSION` no S7-06) e `claim` (`DEDUCTIBLE`, se vier a existir; a cobrança da franquia está fora do âmbito do S9-04) implementam-na.
  - O `payment` encontra as implementações como beans Spring.
  - Os endpoints `POST /proposals/{id}/payment-intents` etc. ficam no módulo dono do alvo e chamam `PaymentApi.createIntent(...)`.
- **Ficheiros.** `document` guarda e serve ficheiros mas não decide quem os vê.
  - Define `FileAccessPolicy` (`ownerTypes()`, `supports(ownerType)`, `canRead(ownerType, ownerId, CurrentUser)`; S2-01), implementada por cada módulo dono. O `FileAccessPolicyRegistry` recusa arrancar com `ownerTypes()` vazio ou com dois beans para o mesmo tipo; o `store` e o `renderPdf` recusam um `ownerType` sem política.
  - Sem política que diga "sim" → 404.
  - Tipos de dono definidos nas fichas:

    | `ownerType` | Módulo (classe) | Ficha |
    | --- | --- | --- |
    | `VEHICLE` | vehicle (`VehicleFileAccessPolicy`) | S2-03 |
    | `PRODUCT_VERSION` | catalog (`ProductVersionFileAccessPolicy`) | S2-04 |
    | `INSURER_LOGO` | catalog (`InsurerLogoFileAccessPolicy`; leitura sempre permitida) | S2-04 |
    | `KYC` | identity (`KycFileAccessPolicy`) | S4-01 |
    | `INSPECTION` | proposal (`InspectionFileAccessPolicy`) | S4-03 |
    | `PROPOSAL` | proposal (`ProposalFileAccessPolicy`) | S4-04 |
    | `PAYMENT_REFERENCE` | payment (`PaymentFileAccessPolicy`) | S5-02 (S7-06: também o pagador em âmbito `ORG`) |
    | `POLICY` | policy (`PolicyFileAccessPolicy`) | S6-02 (S7-06: também o gestor de frota) |
    | `CLAIM` | claim (`ClaimFileAccessPolicy`) | S8-01 (S9-02: a oficina escolhida lê `PHOTO_DAMAGE` e `PHOTO_SCENE`) |
    | `ACCIDENT_STATEMENT` | claim (`StatementFileAccessPolicy`) | S8-02 |
    | `CLAIM_ASSESSMENT` | claim (`ClaimFileAccessPolicy`) | S9-01 |
    | `CLAIM_WORKSHOP_QUOTE` | claim (`ClaimFileAccessPolicy`) | S9-02 |
    | `CLAIM_SETTLEMENT` | claim (`ClaimFileAccessPolicy`; só a seguradora) | S9-04 |
    | `COMPLAINT`, `COMPLAINT_INTERNAL` | complaint (`ComplaintFileAccessPolicy`; `COMPLAINT_INTERNAL` só a seguradora com `complaint:read`) | S10-01 |
    | `COMMISSION_STATEMENT` | policy (`CommissionStatementFileAccessPolicy`) | S11-02 |

  - O `reporting` não usa o `DocumentApi`: os ficheiros das exportações ficam em `reporting.report_run` (S11-05).
- **Retirar um veículo.** O `vehicle` não pode depender do `policy`. Define no pacote raiz `VehicleRemovalGuard` (`boolean blocksRemoval(UUID vehicleId)`, no âmbito de quem chama, dentro da transacção do `DELETE`; S2-03 §7.1). O `VehicleService` recebe a lista de guards e o primeiro `true` dá 409 `VEHICLE_REMOVAL_BLOCKED`. O `policy` implementa-a no S6-01 (`PolicyVehicleRemovalGuard`: apólice `ACTIVE` ou `SUSPENDED` com o veículo). Até lá a remoção é permitida (D29).
- **Valores de referência e matrículas.** O `vehicle` valida dados sem depender do `catalog` através de interfaces do `shared`, cada uma com uma só implementação no `catalog`:
  - `shared.reference.ReferenceValueLookup` (`boolean isActive(String domain, String code)`), implementada por `ReferenceValueService` (S2-02);
  - `PlateFormatProvider` (S0-03), implementada por `DbPlateFormatProvider` (S2-02), que substitui o `ConfigPlateFormatProvider`.
- **Nomes de quem fez a acção na auditoria.** O `shared` não depende de nenhum módulo. Define `ActorNameResolver` (`Map<String, String> displayNames(Set<String> actorIds)`, S11-06), implementada pelo `identity` (`AuditActorNameResolver`, que delega em `IdentityApi.staffDisplayNames` no âmbito de quem pesquisa). Sem implementação, os nomes ficam `null`.
- **Histórico de sinistros (bónus/malus).** O `policy` não pode ouvir o `claim` (o `claim` depende do `policy`). Por isso o `claim`, ao pagar uma indemnização (S9-04), chama `PolicyApi.recordClaimOutcome(policyId, claimId, driverLicenceNumber, atFault, paidAmount, occurredAt)` (S7-03), que grava em `policy.claims_history` e reduz o capital disponível da anuidade. `driverLicenceNumber` pode ser `null` (sinistro sem declaração amigável); com valor, o `claim` lê-o com `AccidentStatement.driverLicenceNumber(VehicleKey)` do S8-02 (D9).
- **Pagamento da proposta → emissão.** O `proposal` ouve `PaymentSucceeded` (alvo `PROPOSAL`), passa a `PAID` e publica `ProposalPaid`; é este evento que o `policy` ouve para emitir (S6). Assim não há corrida entre os dois módulos.
- **Destinatários das notificações.** O `notification` não pode depender do `identity` (o `identity` já depende dele). Define a interface `NotificationRecipientResolver` (`Optional<NotificationRecipient> resolve(UUID userId)`, com `NotificationRecipient(userId, msisdn, msisdnVerified, email, emailVerified, language, active)`), implementada pelo `identity` (`IdentityRecipientResolver`, S6-04). Tem de existir exactamente um bean.
- **Notificações.** Quem quer notificar chama `NotificationApi.notify(userId, templateCode, params, NotificationCategory)`. O `notification` nunca ouve eventos de outros módulos: os avisos saem de listeners nos módulos donos dos eventos (`ProposalNotificationsListener`, `PaymentNotificationsListener`, S6-04) ou de jobs (`QuoteExpiryNotifierJob` do `rating`, S6-04, D16).

### 1.2 Eventos de domínio

Publicados com `ApplicationEventPublisher` + outbox do Spring Modulith (`event_publication`). Ouvidos com `@ApplicationModuleListener` (depois do commit, transacção própria). O código do S1 **não tem `@EnableAsync`**, por isso o ouvinte corre na thread de quem publicou, logo a seguir ao commit, e não em paralelo; quem precisar de trabalho em segundo plano usa um executor próprio (S5-04, S6-04). Os ouvintes não podem depender de correr noutra thread nem do `TenantContext` de quem publicou (usam `@RunAsSystem`, §7).

Records imutáveis no pacote raiz do módulo que publica, com `UUID eventId`, `Instant occurredAt` e os ids necessários; nunca entidades JPA. Os eventos do S1 (`OtpDeliveryFailed`, `OrganizationSuspended`, `OrganizationReactivated`) não têm `eventId`/`occurredAt`; a regra aplica-se aos novos.

| Evento | Publicado por | Sprint | Ouvido por |
| --- | --- | --- | --- |
| `OrganizationSuspended`, `OrganizationReactivated` | organization | 1 | identity |
| `OtpDeliveryFailed` | notification | 1 | identity |
| `ProductVersionPublished` | catalog | 2 (S2-04) | — |
| `QuoteCreated` | rating | 3 (S3-02) | — |
| `ProposalSubmitted` | proposal | 4 (S4-05) | — |
| `ProposalAccepted`, `ProposalRejected`, `ProposalInfoRequested` (S4-05), `ProposalExpired` (S4-02) | proposal | 4 | proposal (`ProposalNotificationsListener`, S6-04: avisos ao cliente). O `fraud` não ouve eventos de proposta (D17) |
| `ProposalPaid` | proposal | 5 (S5-02) | policy (`ProposalPaidListener`, S6-01: emite a apólice) |
| `PaymentSucceeded` | payment | 5 (S5-02) | proposal (`ProposalPaymentListener`, S5-02: alvo `PROPOSAL` → `PAID` → publica `ProposalPaid`, ou `raiseTargetNotPayable`), payment (`PaymentNotificationsListener`, S6-04), policy (`PolicyPaymentListener`, S7-02: `INSTALMENT`; `RENEWAL` S7-03, `ENDORSEMENT` S7-04, `FLEET_INCLUSION` S7-06), fraud (`FraudEventsListener`, S11-03: regras `PAYMENT`) |
| `PaymentFailed` | payment | 5 (S5-02) | payment (`PaymentNotificationsListener`, S6-04) |
| `ReferenceGenerated` | payment | 5 (S5-02) | payment (`PaymentNotificationsListener`, S6-04) |
| `PaymentRefunded` | payment | 5 (record S5-02); publicado a partir do S7-05, que lhe acrescenta `refundRequestId` | — |
| `PolicyIssued` | policy | 6 (S6-01; S7-06 nas apólices de frota) | policy (`PolicyDocumentsListener`, S6-02; `CommissionListener`, S11-02: linha `ISSUE`), verification (`PolicyCacheListener`, S6-03), integration (`UssdPolicyIssuedListener`, S12-02: certificado por SMS) |
| `PolicyStatusChanged` | policy | 7 (S7-01) | verification (`PolicyCacheListener`, S7-01), policy (`CommissionListener`, S11-02: `CANCELLATION` e `REINSTATEMENT`) |
| `PolicyRenewed` | policy | 7 (S7-03) | policy (`CommissionListener`, S11-02: linha `RENEWAL`) |
| `EndorsementApplied` | policy | 7 (S7-04) | policy (`RefundListener`, S7-05; `FleetCertificateListener`, S7-06, só `FLEET_INCLUSION`; `CommissionListener`, S11-02: linha `ENDORSEMENT`) |
| `ClaimReported` | claim | 8 (S8-03, S8-04; S12-01 pelo USSD) | claim (`ClaimReportedListener`, S8-03: cobertura, ligação, rejeição automática), fraud (`FraudEventsListener`, S11-03) |
| `ClaimRegistered` | claim | 8 (S8-05) | fraud (`FraudEventsListener`, S11-03) |
| `ClaimRejected` | claim | 8 (S8-03 automático, S8-05 manual) | — |
| `ClaimDisputed` | claim | 9 (S9-03) | complaint (`ClaimDisputedListener`, S10-03: reclamação automática) |
| `ClaimPaid` | claim | 9 (S9-04) | fraud (`FraudEventsListener`, S11-03) |
| `ClaimClosed` | claim | 9 (S9-04) | — |
| `ComplaintSubmitted`, `ComplaintAnswered` (S10-01), `ComplaintEscalated` (S10-02) | complaint | 10 | — |
| `AssistanceRequested` | assistance | 10 (S10-04) | — |

Eventos internos (no pacote `application` do módulo, ouvidos só pelo próprio módulo):

| Evento | Módulo | Ficha | Ouvido por |
| --- | --- | --- | --- |
| `RefundRequested(refundId)` | policy | S7-05 | `RefundDispatchListener` (chama `PaymentApi.refund`) |
| `ReportRunRequested(runId)` | reporting | S11-05 | `ReportRunListener` |

As mensagens RabbitMQ do `payment-contracts` (S5-06: `ReferencePaymentReceived`, `PaymentFileProcessed`, `PaymentFileRejected`, `PaymentSourcePolled`) não são eventos do Spring Modulith: o `payment-file-worker` publica-as (S5-09) e o `payment` consome-as com `@RabbitListener` (`ReferencePaymentListener` e `PaymentFileEventsListener`, S5-10).

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
| 2 | document | `document.file` (S2-01) |
| 2 | catalog | `catalog.reference_value`, `catalog.platform_setting`, `catalog.plate_format` (S2-02); `catalog.product`, `catalog.product_version`, `catalog.coverage`, `catalog.product_document` (S2-04). O S2-04 preenche `organization.insurer_profile.logo_file_id` (D27) |
| 2 | vehicle | `vehicle.vehicle`, `vehicle.driver`, `vehicle.vehicle_driver` (S2-03) |
| 3 | organization | `organization.insurer_profile` ganha `rating_mode`, `rating_endpoint_url`, `rating_secret_ref`, `charges jsonb` e `ck_insurer_profile_external` (S3-01) |
| 3 | rating | `rating.quote_request`, `rating.quote` (S3-02) |
| 4 | identity | `identity.kyc_check` (S4-01) |
| 4 | proposal | `proposal.proposal`, `proposal.declaration`, `proposal.suitability_answer` (S4-02; `ck_proposal_status` e `ck_proposal_channel` com nome fixo); `proposal.inspection`, `proposal.inspection_photo` (S4-03); `proposal.signature` (S4-04) |
| 4 | proposal | `proposal.proposal` ganha `sign_request_file_id`, `sign_request_hash`, `sign_request_challenge_id`, `sign_request_revision`, `sign_request_assisted_by_user_id`, `current_signature_id` (S4-04); `underwriting_result`, `acceptance_mode`, `review_started_at`, `sla_alert_at`, `sla_due_at`, `sla_alerted_at`, `decided_by`, `decided_at`, `rejection_reason_code`, `decision_reason`, `info_request_message`, `info_request_items`, `info_requested_at` (S4-05); `policyholder_source` (`PROFILE`/`BROKER`, S4-06) |
| 4 | shared | `shared.business_sequence` (numeração, S4-02). O schema `shared` **não existe** no S0/S1: a migração do S4-02 cria-o (`create schema if not exists`, como o S5-02) |
| 1–12 | notification | `notification.outbound_message` (S1-03). O `purpose` tem a constraint `outbound_message_purpose_check` e o enum `OutboundMessage.Purpose`: S1 `OTP`, `INVITATION`, `PASSWORD_RESET`; `KYC` (S4-01), `SIGN_OTP` (S4-04, reutilizado no S8-02), `PROPOSAL` (S4-05), `PAYMENT` (S5-02), `NOTIFICATION_TEST` (S6-04), `CLAIM` (S8-02, link de assinatura por SMS), `COMPLAINT` (S10-01), `ASSISTANCE` (S10-04), `ACCOUNT_DELETION` (S12-10). Cada valor novo entra por uma migração `…__notification_purpose_<x>.sql` que faz `drop constraint` e a recria com **todos** os valores desta lista até ao seu sprint (tarefas paralelas incluem os valores umas das outras) |
| 5 | payment | `payment.insurer_payment_method` (S5-01); `payment.payment_intent`, `payment.payment_event`, `payment.reference_payment`, `payment.payment_exception` (S5-02); `payment.inbound_message`, `payment.file_import` (S5-10) |
| 5 | shared | `shared.idempotency_record` (idempotência de todos os módulos, `IdempotencyService`, S5-02) |
| 5 | proposal | `proposal.proposal` ganha `paid_at`, `payment_intent_id` e `ck_proposal_paid`; `ck_proposal_status` recriada (S5-02, D4) |
| 5 | (worker) | schema `payment_worker`: `processed_file`, `file_record`, `shedlock` (S5-09) |
| 6 | policy | `policy.policy`, `policy.instalment`, `policy.certificate`, `policy.policy_event` (S6-01); o `policy` implementa `VehicleRemovalGuard` do `vehicle` (§1.1) |
| 6 | proposal | `proposal.proposal` ganha `policy_id` e `ck_proposal_converted` (S6-01) |
| 6 | verification | `verification.check_log` (S6-03) |
| 6 | notification | `notification.template`, `notification.message`, `notification.preference`, `notification.device` (S6-04) |
| 6 | rating | `rating.quote_request` ganha `expiry_notified_at` (S6-04, D16) |
| 6 | shared | `shared.shedlock` (jobs agendados de todos os módulos; S6-01, ou S6-04 se chegar primeiro) |
| 7 | policy | `policy.policy` ganha `inception_at`, `period_no`, `cancellation_reason`, `cancelled_effective_at`, `suspended_at`, `suspension_ends_on`, `bonus_malus_class`, `ck_policy_cancelled_fields`, `ck_policy_suspended_fields`; `policy.instalment` ganha `period_no`, `credited_amount`; `policy.policy_event` ganha `dedup_key` e `ck_policy_event_type` (S7-01). Parâmetros do ciclo de vida em `catalog.platform_setting` (S7-01) |
| 7 | policy | `policy.renewal`, `policy.claims_history` (com `driver_licence_enc` e `driver_licence_hash`, ambos `null` sem carta); `policy.policy` ganha `claims_paid_period_amount` (S7-03) |
| 7 | policy | `policy.endorsement`; `policy.policy` ganha `policyholder_address`, `policyholder_province` (S7-04) |
| 7 | policy | `policy.refund` (S7-05) |
| 7 | policy | `policy.fleet`, `policy.policy_vehicle`; `policy.policy` ganha `fleet_id`, `corporate_org_id` e `policyholder_user_id`, `vehicle_id`, `proposal_id` passam a `null` permitido (com `check`); `policy.certificate` ganha `policy_vehicle_id`; `policy.endorsement` e `policy.refund` ganham `corporate_org_id` (S7-06) |
| 7 | payment | `payment.payment_intent` ganha `refunded_amount`; `payment.payment_event` ganha `refund_request_id` e o tipo `REFUND` (S7-05); política RLS `payment_intent_org_payer` em `payment_intent` e `reference_payment` (S7-06) |
| 8 | claim | `claim.claim` (com `accident_statement_id`), `claim.claim_party`, `claim.claim_document`, `claim.claim_event` (S8-01); `claim.accident_statement`, `claim.statement_signature` e a FK `fk_claim_statement` (S8-02) |
| 9 | claim | `claim.assessment`, `claim.assessment_photo`; `claim.claim` ganha `assessor_org_id` (S9-01) |
| 9 | claim | `claim.workshop_quote`; `claim.claim` ganha `workshop_org_id`, `external_workshop_name`, `external_workshop_phone`, `external_workshop_address`, `workshop_chosen_at`, `workshop_chosen_by` (S9-02) |
| 9 | claim | `claim.liability_decision`, `claim.settlement_offer`; `claim.claim` ganha `vehicle_recovered_at` (S9-03) |
| 9 | claim | `claim.indemnity_payment`, `claim.reserve`; `claim.claim` ganha `closed_at`, `close_reason` (S9-04) |
| 9 | organization | `organization.workshop_profile` (morada, província, coordenadas, especialidades), `organization.assistance_coverage` (prestador × província) (S9-02); `organization.insurer_profile` ganha `indemnity_dual_approval_amount` (S9-04) |
| 10 | complaint | `complaint.complaint` (com `origin`, `source_event_id`, `claim_id`), `complaint.complaint_message`, `complaint.sla_config` (S10-01); `complaint.ombudsman_case` (S10-02); `complaint.external_proceeding` e `complaint.complaint_message.source_event_id` (S10-03) |
| 10 | assistance | `assistance.request`, `assistance.request_event` (S10-04) |
| 11 | organization | `organization.broker_appointment` (S11-01) |
| 11 | policy | `policy.renewal` ganha `broker_org_id`; política RLS só de leitura `policy_broker_read` em `policy.policy` (S11-01); `policy.commission`, `policy.commission_statement` (S11-02) |
| 11 | fraud | `fraud.rule`, `fraud.alert` (S11-03) |
| 11 | claim, identity | `claim.claim_document` ganha `photo_dhash`, `photo_dhash_computed_at`; `identity.refresh_token` ganha `device_id` (S11-03) |
| 11 | reporting | papel `moseguros_reporting`; vistas materializadas `reporting.mv_insurer`, `mv_policy`, `mv_renewal`, `mv_instalment`, `mv_payment`, `mv_claim`, `mv_complaint`, vistas `reporting.v_insurer_*`, função `reporting.refresh_all()` (S11-04); `reporting.report_template`, `reporting.report_run`, `mv_intermediary`, `mv_commission`, vistas `v_asfpm_*` e `v_regulator_insurer`, `v_regulator_insurer_month` (S11-05) |
| 11 | audit | `audit.audit_event` ganha `access_type` (`WRITE`/`READ_SENSITIVE`), *triggers* que recusam `update`/`delete`/`truncate`, índices e RLS; a aplicação fica só com `select` e `insert` (S11-06) |
| 12 | integration | `integration.ussd_session` (com `proposal_id` e `payment_intent_id`, preenchidos pelo S12-02) (S12-01) |
| 12 | proposal, policy | `ck_proposal_channel` e `ck_policy_channel` passam a aceitar `USSD`; `proposal.signature`: `ck_signature_method` (`OTP`, `USSD`), `otp_challenge_id`, `otp_channel` e `destination_masked` passam a `null` permitido com `ck_signature_otp` (S12-02) |

## 3. Contratos públicos principais (`<Modulo>Api`)

Só os métodos que outros módulos usam. As fichas podem acrescentar métodos, nunca mudar estes. As subtabelas seguem a forma da ficha que cria cada método (coluna "Ficha"); as diferenças entre fichas estão no §10.

Salvo indicação em contrário, os métodos correm no âmbito de quem chama (o RLS aplica-se). "`SYSTEM`" quer dizer que delegam numa classe `<Modulo>SystemLookups` com `@RunAsSystem`.

### `DocumentApi` (document)

| Método | Ficha | Notas |
| --- | --- | --- |
| `FileRef store(StoreFileCommand)` | S2-01 | `REQUIRES_NEW`; o `ownerType` tem de ter `FileAccessPolicy` |
| `void markLinked(UUID fileId)` | S2-01 | na transacção de quem chama; idempotente |
| `FileContent open(UUID fileId)` | S2-01 | não verifica a `FileAccessPolicy` (quem chama é o dono) |
| `FileRef metadata(UUID fileId)` | S2-01 | idem |
| `FileRef renderPdf(String templateCode, Map<String,Object> model, Locale locale, PdfTarget target)` | S4-04 | única forma (D5); `PdfTarget(ownerType, ownerId, purpose, insurerId, FileClassification classification, fileName)`; `MANDATORY` |

Não há `delete` na API: os ficheiros nunca ligados a um dono são limpos por job ao fim de 30 dias (o `FileStorage` tem `delete`, D26).

### `VehicleApi` (vehicle)

| Método | Ficha | Notas |
| --- | --- | --- |
| `Optional<VehicleView> findOwned(UUID vehicleId, UUID ownerUserId)` | S2-03 | vazio se não for do dono ou estiver `REMOVED` |
| `VehicleSnapshot snapshot(UUID vehicleId)` | S2-03 | `VEHICLE_NOT_FOUND` |
| `DriverSnapshot driverSnapshot(UUID driverId)` | S2-03 | `DRIVER_NOT_FOUND`; leva `licenceNumberMasked` e `licenceNumberHash`, nunca o número em claro |
| `RegisteredVehicle registerForCustomer(UUID ownerUserId, NewVehicle vehicle, NewDriver driver)` | S4-06 | `RegisteredVehicle(VehicleSnapshot, DriverSnapshot)` |
| `Optional<VehicleView> findOwnedByPlate(UUID ownerUserId, Matricula plate)` | S12-02 | `self:vehicles`; só `ACTIVE` |
| `UUID registerVehicle(UUID ownerUserId, NewVehicle vehicle)` | S12-02 | regras do `createMyVehicle` (S2-03) |
| `Optional<UUID> findOwnerDriver(UUID ownerUserId)` | S12-02 | condutor `is_owner` e `ACTIVE` |
| `UUID registerOwnerDriver(UUID ownerUserId, NewDriver driver, UUID vehicleId)` | S12-02 | regras do `createMyDriver` (S2-03) |

### `CatalogApi` (catalog)

| Método | Ficha | Notas |
| --- | --- | --- |
| `String platformSetting(String key)` | S2-02 | chave desconhecida → `IllegalArgumentException` |
| `List<ProductVersionView> findPublishedVersions(VehicleCategory category, VehicleUsage usage, ProductType type)` | S2-04 | `type` nulo = todos; `SYSTEM` (`CatalogSystemLookups`) |
| `ProductVersionView version(UUID productVersionId)` | S2-04 | só versões com `published_at`; senão `PRODUCT_VERSION_NOT_FOUND` |
| `ProductVersionView insurerVersion(UUID productVersionId)` | S3-01 | qualquer estado, só a seguradora dona (RLS) |
| `long updateRatingRules(UUID productVersionId, JsonNode rating, long expectedVersion)` | S3-01 | só `DRAFT` |

### `RatingApi` (rating)

| Método | Ficha | Notas |
| --- | --- | --- |
| `QuoteView quote(UUID quoteId)` | S3-02 | `QUOTE_NOT_FOUND` |
| `void markUsed(UUID quoteId)` | S3-02 | `MANDATORY` (transacção da proposta) |
| `QuoteResult rateRenewal(RenewalRatingRequest request)` | S7-03 | também usado pelo S7-04 (`purpose = ENDORSEMENT`); não grava `rating.quote` |
| `List<QuoteView> quoteForUssd(UssdQuoteCommand(UUID customerUserId, UUID vehicleId, UUID driverId, String productType, int durationDays, LocalDate startDate))` | S12-02 | só `OFFERED` |

### `IdentityApi` (identity)

| Método | Ficha | Notas |
| --- | --- | --- |
| `TokenPair issueTokens(UserAccount account, String deviceInfo)` | S1-02 | usado também nos testes (`SecurityTestConfiguration.IdentityFixtures`) |
| `UUID findOrCreateCustomerByMsisdn(Msisdn)` | S3-02 | `SYSTEM` (D2); usado no S4-06, S8 e S12 |
| `Optional<CustomerView> customer(UUID userId)` | S4-01 | `SYSTEM`; `CustomerView` com `idDocNumber` e `nuit` em claro, nunca serializado |
| `KycStatus kycStatus(UUID userId)` | S4-01 | `SYSTEM` |
| `SignOtpChallenge requestSignOtp(SignOtpRequest)` | S4-04 | `SYSTEM` |
| `SignOtpVerification verifySignOtp(UUID signerUserId, UUID challengeId, String code, String documentHash)` | S4-04 | `SYSTEM`; também verifica os códigos do S8-02 |
| `List<StaffContact> staffWithPermission(UUID orgId, String permission)` | S4-05 | `SYSTEM`; `StaffContact(UUID userId, String displayName, String email, String preferredLanguage)` (D10) |
| `SignOtpChallenge requestStatementSignOtp(StatementSignOtpRequest)` | S8-02 | `SYSTEM` |
| `Map<UUID,String> staffDisplayNames(Set<UUID> userIds)` | S8-05 | âmbito de quem chama |
| `Optional<StaffContact> staffMember(UUID userId)` | S10-01 | `SYSTEM`; qualquer estado da conta |
| `int accountsSharingDevices(UUID userId, Instant since)` | S11-03 | `SYSTEM` |
| `confirmMsisdnByNetwork(UUID userId)` | S12-01 | `SYSTEM`; marca o `msisdn` como verificado |
| `CustomerClaims customerClaims(UUID userId)` | S12-01 | `SYSTEM`; `CustomerClaims(userId, AccountStatus status, preferredLanguage, List<String> roles, List<String> permissions)` |
| `completeProfile(ProfileCompletionCommand(UUID userId, String fullName, LocalDate birthDate, String idDocType, String idDocNumber, Nuit nuit, Province province))` | S12-02 | `self:profile`; regras do `PUT /me/profile` (S1-06) |

### `ProposalApi` (proposal)

| Método | Ficha | Notas |
| --- | --- | --- |
| `Optional<ProposalView> view(UUID proposalId)` | S4-02 | `ProposalView` com `commercialPremiumAmount` (ver §10) e `signedDocumentFileId` (S4-04) |
| `void markConverted(UUID proposalId, UUID policyId)` | S6-01 | `PAID → CONVERTED` |
| `List<BrokerCustomerRef> brokerCustomers(UUID brokerOrgId)` | S11-01 | `BrokerCustomerRef(UUID customerUserId, int openProposals, Instant lastProposalAt)`; âmbito do mediador |
| `UssdProposalResult submitFromUssd(UssdProposalCommand)` | S12-02 | `self:proposals` |
| `List<ProposalView> findAwaitingPayment(UUID customerUserId)` | S12-02 | `ACCEPTED`, não expiradas |
| `Optional<ProposalView> findByQuoteId(UUID quoteId)` | S12-02 | |

### `PaymentApi` (payment)

| Método | Ficha | Notas |
| --- | --- | --- |
| `IntentView createIntent(CreateIntentCommand)` | S5-02 | idempotente pela chave (`shared.idempotency_record`) |
| `IntentView intent(UUID intentId)` | S5-02 | |
| `List<PaymentMethodOption> availableMethods(UUID insurerId, Money amount)` | S5-02 | |
| `Optional<IntentView> activeIntent(PaymentTargetType, UUID targetId)` | S5-02 | `CREATED`, `PENDING` ou `UNKNOWN` |
| `boolean hasBlockingIntent(PaymentTargetType, UUID targetId)` | S5-02 | M-Pesa ou e-Mola em curso |
| `boolean isPaid(PaymentTargetType, UUID targetId)` | S5-02 | |
| `void raiseTargetNotPayable(UUID intentId, String detail)` | S5-02 | cria `payment_exception` `TARGET_NOT_PAYABLE` |
| `RefundView refund(RefundCommand)` | S7-05 | `RefundCommand(UUID refundRequestId, UUID intentId, Money amount, String reason)`; idempotente por `refundRequestId` |

### `PolicyApi` (policy)

| Método | Ficha | Notas |
| --- | --- | --- |
| `Optional<PolicyCoverView> findActiveByPlate(String plate, Instant at)` | S6-01 | `SYSTEM` |
| `CoverStatus coverAt(UUID policyId, Instant at)` | S6-01 | `SYSTEM`; regras refeitas no S7-01 e no S7-03 (valores do S6-01, D8) |
| `PolicyView view(UUID policyId)` | S6-01 | `POLICY_NOT_FOUND` (D24); o S7-03 acrescenta `claimsPaidInPeriod` |
| `List<PolicySummaryView> customerPolicies(UUID userId)` | S6-01 | |
| `Optional<PolicyCoverView> findLatestByPlate(String plate, Instant at)` | S6-01 | `SYSTEM`; para o S6-03 |
| `Optional<PolicyCoverView> findByCertificateToken(String token)` | S6-01 | `SYSTEM`; para o S6-03 |
| `void recordClaimOutcome(UUID policyId, UUID claimId, String driverLicenceNumber, boolean atFault, Money paidAmount, Instant occurredAt)` | S7-03 | usado pelo `claim` no S9-04 (§1.1); idempotente por `claimId` |
| `PolicyClaimTerms claimTerms(UUID policyId, Instant at)` | S9-03 | anuidade que contém `at` |
| `Optional<AssistanceEntitlement> assistanceEntitlement(UUID policyId, Instant at)` | S10-04 | |
| `Set<UUID> brokerPolicyIds(UUID brokerOrgId)` | S11-01 | âmbito do mediador (`policy_broker_read`) |
| `Set<UUID> policyIdsByPlate(String plate)` | S11-03 | chamado em `SYSTEM` pelo `fraud` |
| `Optional<CertificateSummary> certificateSummary(UUID policyId)` | S12-02 | `CertificateSummary(certificateNumber, policyNumber, plate, insurerName, validFrom, validTo, verificationUrl)` |

### `ClaimApi` (claim)

| Método | Ficha | Notas |
| --- | --- | --- |
| `Optional<ClaimView> view(UUID claimId)` | S8-01 | |
| `ClaimFraudFacts fraudFacts(UUID claimId)` | S11-03 | chamado em `SYSTEM` pelo `fraud` |
| `ClaimMatchResult countClaimsMatching(ClaimMatchQuery q)` | S11-03 | idem |
| `List<SimilarPhoto> findSimilarPhotos(UUID claimId, int maxDistance, UUID insurerId)` | S11-03 | `insurerId` nulo = todas |
| `WorkshopAssessorStats workshopAssessorStats(UUID insurerId, UUID workshopOrgId, Instant from, Instant to)` | S11-03 | |
| `ClaimReference reportByUssd(UssdClaimReport(policyId, userId, at))` | S12-01 | `self:claims`; `ClaimReference(UUID claimId, String number)` |

### `OrganizationApi` (organization)

| Método | Ficha | Notas |
| --- | --- | --- |
| `Optional<OrganizationSummary> findSummary(UUID)` | S1-04 | `OrganizationSummary(id, type, status, displayName)`. Âmbito de quem chama: em `ORG` só vê a própria organização; vazio em `CUSTOMER` e `NONE`. Para o nome da seguradora visto por clientes, polícia ou público usa-se `insurerProfile` (S3-01) |
| `Map<UUID, OrganizationSummary> findSummaries(Set<UUID>)` | S2-04 | |
| `Optional<UUID> insurerLogoFileId(UUID insurerId)`, `void setInsurerLogo(UUID insurerId, UUID fileIdOrNull)` | S2-04 | |
| `Optional<InsurerProfileView> insurerProfile(UUID insurerId)` | S3-01 | `SYSTEM`; o S9-04 acrescenta `indemnityDualApprovalAmount` ao `InsurerProfileView` |
| `Map<UUID, InsurerProfileView> insurerProfiles(Collection<UUID> insurerIds)` | S3-01 | `SYSTEM` |
| `Optional<OrganizationSummary> findActiveCorporateClientByNuit(Nuit)` | S7-06 | `SYSTEM`; só `CORPORATE_CLIENT` `ACTIVE` |
| `List<OrganizationSummary> activeByType(OrganizationType)` | S9-01 | `SYSTEM` |
| `List<WorkshopView> workshopsByProvince(Province)` | S9-02 | `SYSTEM`; só `ACTIVE` e `listed` |
| `Optional<WorkshopView> workshop(UUID organizationId)` | S9-02 | `SYSTEM` |
| `List<OrganizationSummary> activeInsurers()` | S10-01 | `SYSTEM` |
| `Optional<OrganizationContact> findContact(UUID orgId)` | S10-01 | `SYSTEM`; `OrganizationContact(id, displayName, contactEmail, contactPhone)` |
| `Optional<AssistanceProviderView> assistanceProvider(UUID insurerId, Province province)` | S10-04 | `SYSTEM`; `AssistanceProviderView(orgId, displayName, contactPhone, contactEmail)` |
| `Optional<AppointmentView> activeAppointment(UUID brokerOrgId, UUID insurerId)` | S11-01 | `SYSTEM`; "activa" = hoje em `Africa/Maputo` |
| `Set<UUID> activeAppointedInsurers(UUID brokerOrgId)` | S11-01 | `SYSTEM` |
| `AppointmentView requireActiveAppointment(UUID brokerOrgId, UUID insurerId)` | S11-01 | 403 `BROKER_NOT_APPOINTED` |
| `Optional<IntermediaryContactView> intermediaryContact(UUID orgId)` | S11-01 | `SYSTEM` |
| `Optional<AppointmentView> latestAppointment(UUID brokerOrgId, UUID insurerId, LocalDate onOrBefore)` | S11-02 | `SYSTEM`; qualquer estado |

### `NotificationApi` (notification)

| Método | Ficha | Notas |
| --- | --- | --- |
| `DeliveryResult sendOtp(Channel channel, String destination, String code, int validityMinutes, String language, UUID correlationId)` | S1-03 | |
| `DeliveryResult sendEmail(String to, String templateKey, Object[] args, String language, String purpose, UUID correlationId)` | S1-04 | chaves `<templateKey>.subject`/`.body`; `purpose` tem de estar em `OutboundMessage.Purpose` (§2) |
| `DeliveryResult sendSignOtp(Channel channel, String destination, String code, int validityMinutes, SignOtpText text, String language, UUID correlationId)` | S4-04 | síncrono, nunca lança |
| `DeliveryResult sendSms(String toE164, String templateKey, Object[] args, String language, String purpose, UUID correlationId)` | S5-02 | só para números escritos pelo utilizador, links do mediador (S6-04 §9) e o link de assinatura da declaração amigável (S8-02) |
| `UUID notify(UUID userId, String templateCode, Map<String,Object> params, NotificationCategory category)` | S6-04 | `REQUIRED`; envio depois do *commit* |
| `DeliveryResult sendStatementSignOtp(Channel channel, String destination, String code, int validityMinutes, String occurredDate, String language, UUID correlationId)` | S8-02 | igual ao `sendSignOtp` |

### Outros módulos

| Módulo | Métodos | Ficha |
| --- | --- | --- |
| `VerificationApi` (verification) | `VerificationView verifyPlate(String rawPlate)` (`VerificationView(status, plateMasked, insurerName, validFrom, validTo, coverageType)`; grava `check_log` como `PUBLIC`) | S12-02 |
| `ComplaintApi` (complaint) | sem métodos (classe criada para os eventos) | S10-01 |
| `AssistanceApi` (assistance) | sem métodos | S10-04 |
| `fraud`, `reporting`, `integration` | não têm `<Modulo>Api` usado por outros módulos | — |

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

Já semeadas no S1 (`V202611020930`): `otp-settings:read`, `otp-settings:manage`, `organization:read`, `organization:manage` (PLATFORM_ADMIN); `org-user:read`, `org-user:manage` (PLATFORM_ADMIN, INSURER_ADMIN, ORG_ADMIN); `self:profile` (CUSTOMER). Na web, cada permissão verificada entra também no objecto `Permissions` de `web/src/auth/permissions.tsx`.

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

**Ordem de chegada ao `main`.** As tarefas correm em paralelo, por isso uma migração pode chegar depois de outra com carimbo maior. O Flyway corre com `spring.flyway.out-of-order: true` em todos os perfis e aplica-a na mesma. O S0-03 previa-o mas não chegou ao código: é o S2-01 que o acrescenta (com o `FlywayOutOfOrderIT`). Cada migração tem de funcionar sozinha sobre o estado das tarefas de que a sua tarefa depende ("Depende de"), e nunca sobre o de uma tarefa paralela.

O worker de ficheiros (S5) tem o seu próprio histórico Flyway, no schema `payment_worker`, começando em `V202701150000`.

## 7. Infra-estrutura comum introduzida ao longo dos sprints

| Sprint | O quê | Decisão |
| --- | --- | --- |
| 2 | Armazenamento de ficheiros | `FileStorage` com `FilesystemFileStorage` (local, testes, compose com volume `files`) e `S3FileStorage` (AWS SDK v2; staging e produção, qualquer serviço compatível com S3). ADR-005. Limite 10 MB por ficheiro; tipos permitidos `image/jpeg`, `image/png`, `image/heic`, `application/pdf`; SHA-256 calculado no upload |
| 2 | Upload e download | `POST` multipart no módulo dono (ex.: `/me/vehicles/{id}/registration-doc`); download `GET /api/v1/files/{id}` (módulo `document`, decide pela `FileAccessPolicy`) com `Content-Disposition` e `Cache-Control: private, no-store` |
| 4 | PDF | Modelos Thymeleaf (XHTML) em `backend/src/main/resources/templates/pdf/` → Flying Saucer (`org.xhtmlrenderer:flying-saucer-pdf-openpdf`). Fonte Inter embebida. Cada PDF guarda o SHA-256 |
| 3 | HTTP de saída | Resilience4j (`resilience4j-spring-boot3`): timeout, circuit breaker e bulkhead por seguradora/fornecedor. Clientes com `RestClient` |
| 5 | Cofre de segredos | `SecretStore` (`put`, `get`, `delete`, `exists`) com `VaultSecretStore` (HashiCorp Vault KV v2) e `InMemorySecretStore` (testes). Compose: Vault em modo dev. A BD guarda só a referência (`secret/…`) |
| 5 | Mensageria | RabbitMQ 4.1 (ADR-004 e adenda) |
| 5 | Idempotência | `shared.idempotency_record` + `IdempotencyService` (S5-02): mesma `Idempotency-Key` + mesmo corpo → mesma resposta durante 24 h; mesma chave com corpo diferente → 422 `IDEMPOTENCY_KEY_REUSED`; sem chave onde é obrigatória → 400 `IDEMPOTENCY_KEY_REQUIRED` |
| 5 | Build | Reactor Maven na raiz (`pom.xml` agregador: `payment-contracts`, `backend`, `payment-file-worker`), introduzido no S5-06 |
| 6 | Modelos de mensagem | `messages/notifications_{pt,en}.properties` com sobreposição opcional em `notification.template` (`AGENTS.md` §4). Nenhuma migração insere modelos |
| 2 | Upload no nginx | `web/nginx.conf` ganha `client_max_body_size 12m` (D28; previsto no S0-07, acrescentado no S2-01) |
| 4 | IP do cliente | `shared.web.ClientIpResolver` + `moseguros.web.trusted-proxies` (D15): `X-Forwarded-For` só vindo de um proxy de confiança. Previsto no S0-03 mas não implementado (o `AuditService` aceita hoje qualquer `X-Forwarded-For`). Criado no S4-04, a primeira ficha que o usa; o `AuditService` passa a usá-lo. S6-03, S11-06 e S12-06 reutilizam-no |
| 6 | Jobs | `SchedulingConfig` em `shared.scheduling` (S2-01); o S6-01 acrescenta-lhe o ShedLock. `@Scheduled` + ShedLock (`net.javacrumbs.shedlock`, JDBC, tabela `shared.shedlock`). Cron em `Africa/Maputo`. Cada job é idempotente e regista início, fim e contagens em log estruturado |
| 6 | Push | `PushSender` com `FcmPushSender` (Firebase Admin SDK) e `MockPushSender` |
| 6 | CAPTCHA | `CaptchaVerifier` com `TurnstileCaptchaVerifier` (Cloudflare Turnstile) e `AlwaysValidCaptchaVerifier` (local/testes). Decisão a confirmar pelo proprietário do produto |
| 6 | Observabilidade | Micrometer + Prometheus (`/api/v1/prometheus`, rede interna), Grafana no compose `observability` |
| 8 | Mapas | Web: Leaflet + OpenStreetMap; mobile: `flutter_map` + OpenStreetMap. Sem chave de API |
| 5 | SIMO em mock | `MOCK_SIMO_V1` (S5-07, S5-08) e simulador `infra/mocks/simo-mock` (porta 8092) que entrega ficheiros por SFTP; permitido em `local`, `test`, `staging`; proibido em `prod` |
| 12 | USSD | `UssdAggregatorPort` + simulador próprio em `infra/mocks/ussd-mock` |

**Âmbito em listeners e jobs.** Os `@ApplicationModuleListener` e os jobs agendados correm sem utilizador autenticado (âmbito `NONE`, que não vê nada). Por isso as classes `…Listener` e `…Job` do pacote `application` usam `@RunAsSystem` (regra do S1-01). Têm de filtrar no código pela seguradora ou pelo cliente que vem no evento, e nunca devolvem dados a um utilizador.

**Staff sem seguradora.** O staff de organizações que não são seguradoras (mediador, provedor, regulador…) tem âmbito `ORG` com `insurerId = null`. `TenantContext.insurerId()` lança `TENANT_REQUIRED`; onde pode faltar, usa-se `TenantContext.current().insurerId()`.

### 7.1 Convenções da web e da app (código do S1)

- **Web:**
  - datas com `formatDate`/`formatDateTime` de `web/src/i18n/format.ts` (não se cria outro `dates.ts` para formatar);
  - dinheiro com `web/src/format/money.ts` (S2-05, recebe `Money`);
  - ficheiros protegidos com `web/src/files/useFileBlob.ts` (S2-05);
  - guardas `withPermission`/`RequirePermission` (`web/src/auth/permissions.tsx`), sem permissão → `/sem-acesso`; os itens de `menus.ts` levam `permission:`;
  - o `organizationRoutes` do `router.tsx` é partilhado por todas as áreas de organização: páginas de uma só área precisam de uma lista própria.
- **App:**
  - cliente `dart-dio` gerado em `mobile/packages/moseguros_api`; tipos que o gerador não regista (ex.: `InnerEnum`) vão para `lib/core/api/api_serializers.dart`. Evitar enums dentro de mapas (`additionalProperties`) nos DTOs;
  - ficheiros com `core/files/file_download.dart`/`file_viewer.dart` e câmara em `core/camera/` (S2-07);
  - o destino pedido no login (`?from=`) e o `intent-filter` `moseguros` entram no S4-09; o S6-07 reutiliza-os.
- **Códigos de erro:** cada código novo leva a tradução na web (`errors.<CODE>`/`validation.<CODE>`, verificado por `web/src/i18n/errorCodes.test.ts`) e na app (chave ARB e caso no `switch` de `apiErrorMessage`/`fieldErrorMessage` em `mobile/lib/core/api/api_error.dart`, verificado por `mobile/test/error_codes_test.dart`). As traduções não têm parâmetros: tempos de espera saem de `retryAfterSeconds`.
- **`scripts/images.txt`:** a coluna "quando" só distingue `infra`; qualquer outro valor torna a imagem obrigatória no `doctor.sh`.

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
| S3-04 | Web cliente `/cliente/simular` e web mediador `/mediador/cotacoes` | S3-02 |
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
| S5-07 | Referências SIMO em mock: gerador `MOCK_SIMO_V1` e troca para o algoritmo real | S5-02 |
| S5-08 | Ficheiro de liquidação SIMO em mock: parser `MOCK_SIMO_V1` e simulador `simo-mock` | S5-07, S5-09 |
| S5-09 | `payment-file-worker` (SFTP, agendamento, publicação) | S5-06 |
| S5-10 | Consumidor, reconciliação e excepções | S5-02, S5-06, S5-07 |
| S5-11 | Ecrãs de pagamento do cliente (web e app) | S5-02, S5-04 |
| S5-12 | Web seguradora e admin: configuração, pagamentos, excepções, ficheiros, estado do worker | S5-01, S5-10 |
| S5-13 | **Algoritmo SIMO real de referências — BLOQUEADA** | S5-07 |
| S5-14 | **Layout SIMO real do ficheiro de liquidação — BLOQUEADA** | S5-08 |

### Sprint 6 — Emissão, verificação, notificações, Release 1

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S6-01 | Emissão da apólice, prestações e certificado (dados) | S5 |
| S6-02 | PDF do certificado com QR e documentos da apólice | S6-01, S6-04 |
| S6-03 | Verificação pública e área da polícia (API, CAPTCHA, registo) | S6-01 |
| S6-04 | Notificações: modelos, mensagens, preferências, dispositivos, push; aviso de cotação a expirar (`QuoteExpiryNotifierJob` no `rating`) | S5 |
| S6-05 | Web: `/verificar` público, área PRM, `/seguradora/apolices`, `/admin/notificacoes` | S6-02, S6-03, S6-04 |
| S6-06 | Web cliente: apólices, certificado, notificações e preferências | S6-02, S6-04 |
| S6-07 | Mobile: apólices offline, "mostrar à polícia", push e deep links | S6-02, S6-04 |
| S6-08 | Release 1: staging, observabilidade, carga (k6), ASVS nível 1, distribuição do APK | S6-05, S6-06, S6-07 |

### Sprint 7 — Ciclo de vida da apólice e frotas

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S7-01 | Jobs diários e máquina de estados da apólice (avisos, prestações em atraso, resolução, caducidade) | S6 |
| S7-02 | Pagamento de prestações | S7-01 |
| S7-03 | Renovação (proposta 30 dias antes, nova tarifa, bónus/malus) | S7-01, S7-02 |
| S7-04 | Endossos (alterações com ou sem prémio) e decisão da seguradora | S7-01, S7-02 |
| S7-05 | Venda, substituição de veículo, cancelamento e estornos | S7-04 |
| S7-06 | Frotas: apólice de frota, importação CSV, inclusões e exclusões pro rata, área `/frota` (backend) | S7-04, S7-05 |
| S7-07 | Web cliente e seguradora: detalhe da apólice com linha temporal e acções; web `/frota` | S7-03, S7-05, S7-06 |
| S7-08 | Mobile: acções na apólice e calendário de prestações | S7-03, S7-05 |

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
| S10-06 | Web `/assistencia` (prestador) e web cliente (reclamações e assistência) | S10-02, S10-03, S10-04 |
| S10-07 | Mobile: reclamar, conversar, provedor, assistência com localização | S10-02, S10-03, S10-04 |

### Sprint 11 — Mediadores, antifraude, relatórios e auditoria

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S11-01 | Nomeações de mediadores e carteira (API); restrição de cotação e proposta | S10 |
| S11-02 | Comissões: cálculo, extractos mensais, estorno | S11-01 |
| S11-03 | Antifraude: motor de regras, 7 regras, alertas | S10 |
| S11-04 | Painéis da seguradora (vistas materializadas) | S10 |
| S11-05 | Exportações ASFPM configuráveis e área `/supervisao` (API) | S11-02, S11-04 |
| S11-06 | Auditoria: só-de-inserção, leituras sensíveis, pesquisa e exportação (API) | S10 |
| S11-07 | Web: mediador, antifraude, painéis, supervisão, auditoria | S11-01 a S11-06 |
| S11-08 | Web cliente e mobile: mediador associado à apólice | S11-01 |

### Sprint 12 — USSD, endurecimento, piloto e Release 3

| ID | Tarefa | Depende de |
| --- | --- | --- |
| S12-01 | USSD: porta do agregador, simulador, sessões e menus | S11 |
| S12-02 | USSD: compra de RC, pagamento, certificado por SMS, verificação | S12-01 |
| S12-03 | Mobile: OCR do livrete e do documento (ML Kit) | S11 |
| S12-04 | Mobile: login biométrico e leitura automática do código por SMS (SMS Retriever) | S11 |
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
| Algoritmo SIMO e layout do ficheiro reais | S5-13, S5-14 | **sim** para produção; não para desenvolvimento e `staging` (mock `MOCK_SIMO_V1`, S5-07 e S5-08) |
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

## 10. Divergências encontradas ao actualizar o inventário

Resolvidas em 02/11/2026 nas fichas: `ProposalView.commercialPremiumAmount` existe desde o S4-02; `claims_history.driver_licence_enc` aceita `null` (S7-03, S9-04); a verificação pública não ouve `EndorsementApplied` (calcula o estado no pedido); `OrganizationSystemLookups` é criada no S3-01; `StaffContact` tem quatro campos, incluindo `preferredLanguage` (D10).

As secções §1.1, §1.2, §2 e §3 descrevem as fichas tal como estão; onde o texto antigo deste modelo divergia de uma ficha, foi actualizado para a ficha.
