# Decisões de reconciliação entre sprints (02/11/2026)

Tomadas pelo responsável do plano depois de os 11 agentes escreverem as fichas em paralelo. Aplicam-se a todas as fichas. Quando uma ficha contradiz uma decisão, a ficha é corrigida.

**D1. Rota da simulação do cliente.** É `/cliente/simular`, como no menu S0-05, no S1-07 e no S4-08. Não há `/cliente/cotar` nem redireccionamento. O modelo transversal (S3-04) passa a dizer `/cliente/simular`.

**D2. `IdentityApi.findOrCreateCustomerByMsisdn(Msisdn) → UUID`.** É criado no **S3-02**, o primeiro que o usa. O S4-06, o S8 e o S12 referem-no como existente ("criado no S3-02") e não o voltam a criar. A assinatura é a mesma em todo o lado. O método corre em `SYSTEM`, através de `IdentitySystemLookups`/`@RunAsSystem` no identity.

**D3. Campos do pedido de cotação feito pelo mediador.** Os nomes são os do S3-02, `requestedByOrgId` e `customerUserId`. O S4-06, o S11 e outros alinham com estes nomes.

**D4. Estados da proposta.** Mandam os do S4-02, sem estado `SUBMITTED`: a submissão leva directamente a `ACCEPTED` ou `UNDER_REVIEW`. O evento `ProposalSubmitted` continua a existir. O S5 alinha: uma proposta só é pagável em `ACCEPTED`, e o pagamento confirmado leva-a a `PAID`. O S5 também acrescenta `PAID` ao `check` nomeado `ck_proposal_status` do S4-02 (recria a constraint numa migração nova).

**D5. `DocumentApi.renderPdf`.** Manda a forma do S4-04, com `PdfTarget` (dono do ficheiro). O S5, o S6, o S9, o S10 e o S11 usam essa forma. O modelo §3 é actualizado.

**D6. `NotificationRecipientResolver`.** Interface definida no `notification` e implementada pelo `identity` (S6-04), para o `notification` obter os contactos sem depender do `identity`. Junta-se ao modelo §1.1.

**D7. Modelos de mensagem.** Estão em `backend/src/main/resources/messages/notifications_{pt,en}.properties`, como no S6-04. O `notification.template` na BD serve só para sobrepor um modelo editado em `/admin/notificacoes`. Nenhuma ficha insere modelos por migração: as fichas que o faziam (S7, S9, S10, S11, S12) passam a acrescentar as chaves aos `.properties`. Os textos dos PDF vão em `messages/pdf_{pt,en}.properties`.

**D8. `CoverStatus`.** Mandam os valores do S6-01. O S7-01, o S8 e o S10 alinham.

**D9. Número da carta de condução.** É cifrado e tem índice cego (S2-03, S8-02). O S9-04 lê-o com o método que o S8-02 define, nunca de um JSON em claro.

**D10. Consultas a staff no `IdentityApi`.** Fica um só conjunto de métodos, cada um criado pela primeira ficha que o usa:
- `staffWithPermission(UUID orgId, String permission) → List<StaffContact(userId, displayName, email, preferredLanguage)>`, criado no S4 (era o `staffContacts`);
- `staffDisplayNames(Set<UUID>) → Map<UUID,String>`, criado no S8;
- `staffMember(UUID) → Optional<StaffContact>`, criado no S10.

O `listActiveStaffWithPermission` (S8) passa a ser `staffWithPermission`. As fichas posteriores referem estes métodos sem os recriar.

**D11. Idempotência partilhada.** A tabela é `shared.idempotency_record` e o serviço `shared.idempotency.IdempotencyService`, criados no **S5-02**, com os códigos `IDEMPOTENCY_KEY_REQUIRED` (400) e `IDEMPOTENCY_KEY_REUSED` (422). `AGENTS.md` §4: mesma chave, mesma resposta durante 24 h. Não existe `payment.idempotency_record`. O S6, o S7 e o S9 (indemnizações) usam o serviço partilhado.

**D12. Migração das permissões de cada sprint.** Cria-a a primeira tarefa de backend do sprint que chegar ao `main`. As fichas que podem correr em paralelo dizem "se ainda não existir, cria-a com todas as permissões do sprint (modelo §5)".

**D13. Menus.** Uma ficha pode acrescentar itens ao `src/layout/menus.ts`, desde que diga exactamente quais (chave, caminho, ícone, texto pt/en). A frase do S0-05 que o proibia é corrigida.

**D14. Tabelas de infra-estrutura em `shared`.** O schema `shared` passa a ser criado na migração do S0-03. `shared.shedlock`, `shared.business_sequence` e `shared.idempotency_record` não têm as colunas obrigatórias: entram como excepções no `AGENTS.md` §5.

**D15. IP do cliente.** O S0-03 cria o `shared.web.ClientIpResolver`: usa o `X-Forwarded-For` só quando o endereço remoto está em `moseguros.web.trusted-proxies`; noutros casos usa o endereço remoto. É usado pelo `AuditService` e pelo S6-03, que não o volta a criar.

**D16. Aviso de cotação a expirar.** Um job diário `QuoteExpiryNotifierJob` no `rating`, criado no **S6-04**, avisa 2 dias antes de `valid_until`. Acrescenta-se `rating → notification` ao grafo. Não há evento `QuoteExpiring`.

**D17. Eventos de proposta e o `fraud`.** O `fraud` não ouve eventos de proposta: o grafo não tem `fraud → proposal`. O modelo §1.2 é corrigido.

**D18. Reactor Maven na raiz.** Introduzido no S5-06. A S5-06 actualiza todos os sítios que fazem `cd backend && ./mvnw`:
- `scripts/doctor.sh` (`--full`);
- `docs/STATUS.md`;
- `.github/workflows/*.yml`;
- `backend/Dockerfile`;
- fichas posteriores que citem o comando.

O comando `cd backend && ./mvnw -B verify` continua a funcionar, com `-am` se for preciso ou com os módulos instalados.

**D19. RabbitMQ.** Passa a `rabbitmq:4.1-management` (a 3.13 deixou de ter suporte da comunidade). Filas *quorum* como no ADR-004. O `AGENTS.md` §2 e o ADR-004 são actualizados.

**D20. Releases.**
- Pasta `docs/releases/`.
- Versões: R1 = `1.0.0`, R2 = `1.1.0`, R3 = `1.2.0`; candidatas `-rc.N`; etiquetas git `v1.0.0`, etc.
- O S6-08, o S9-09 e o S12-11 alinham.

**D21. Diferenças entre web e app.** `AGENTS.md` §8: as diferenças permitidas são o offline, a câmara guiada, o OCR, a biometria e a leitura automática do SMS.

**D22. Leitura automática do código por SMS** (SMS Retriever API, Android). Entra no **S12-04**, junto com a biometria.

**D23. USSD.** Fica a abordagem do S12-02: dados completos por blocos que se retomam. Os dados mínimos ficam em "Questões". O KYC simplificado do USSD segue a regra do S4: nome, documento e NUIT.

**D24.** O `PolicyApi.view` corre no âmbito de quem chama, como no S8.

**D25. Ordem das tarefas no modelo §8.**
- O S6-02 passa a depender do S6-04. A chamada `POLICY_ISSUED` fica no S6-02, e acaba o "quem chegar em segundo".
- O S10-06 e o S10-07 passam a depender do S10-02.

**D26.** O `FileStorage` ganha `delete` (ADR-005).

**D27. Logótipo da seguradora.** O `insurer_profile.logo_file_id` é preenchido a partir do **S2** (o S1-01 dizia "Sprint 3").

**D28.** O `nginx.conf` do S0-07 leva `client_max_body_size 12m`.

**D29. Retirar um veículo com apólice activa.** O `vehicle` define a interface `VehicleRemovalGuard`, que o `policy` implementa no **S6-01**. Até lá, a remoção é permitida.

**D30. Dados do veículo e do condutor.** As regras de validação são as do S2. O S3 e as fichas seguintes aceitam os dados do S2 tal como estão (ano a partir de 1900, `licenceCategories` em lista) e não acrescentam validações próprias.

**D31.** O modelo §2 e o §3 são actualizados a partir das fichas: tabelas, colunas e métodos novos.

**D32. Peritos internos.** Uma seguradora não tem peritos como staff próprio. Os peritos são sempre organizações `ASSESSOR`. Fica em "Questões".

**D33.** `ORGANIZATION_SUSPENDED` (403) é o código do login, como no S1-05.

**D34. Revisão contra o código do S0/S1 (03/11/2026).** As fichas S2–S12 foram revistas contra o código do S0/S1 (PR #22). O que estava especificado mas não chegou ao código é criado pela primeira ficha que precisa dele: `spring.flyway.out-of-order` e `client_max_body_size 12m` no S2-01; o schema `shared` no S4-02; o `ClientIpResolver` no S4-04. Cada finalidade nova de `notification.outbound_message` traz a sua migração (modelo §2). `findSummary` corre no âmbito de quem chama; para clientes, polícia e público usa-se `insurerProfile`. Convenções da web e da app no modelo §7.1.
