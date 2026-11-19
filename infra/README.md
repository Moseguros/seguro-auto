# Infraestrutura local

`docker compose` sobe a base de dados, os serviços de apoio, o backend e a web, tudo construído a partir deste repositório.

Requisitos: Docker em execução (com Compose v2). As imagens base estão em [`scripts/images.txt`](../scripts/images.txt) e podem ser descarregadas antes com `./scripts/doctor.sh`.

## Subir

```bash
cd infra
cp .env.example .env        # opcional: sem .env usam-se os mesmos valores por defeito
docker compose up -d --build
docker compose ps           # todos os serviços devem ficar "healthy"
```

O primeiro build demora alguns minutos (dependências Maven e pnpm); os seguintes usam a cache do Docker.

Verificar:

```bash
curl -fsS localhost:8080/api/v1/meta/version   # backend directo
curl -fsS localhost:3000/api/v1/meta/version   # pela web (nginx → backend)
```

## Serviços e URLs

| Serviço | URL | Notas |
| --- | --- | --- |
| `web` | <http://localhost:3000> | nginx sem root: build da web e proxy de `/api/` para o backend |
| `backend` | <http://localhost:8080/api/v1/meta/version> | perfil `local`; Swagger UI em <http://localhost:8080/swagger-ui.html>; health em `/api/v1/health` |
| `postgres` | `localhost:5432` | BD, utilizador e palavra-passe `moseguros` (dono; ver `.env.example`); a aplicação usa `moseguros_app_user` (ver "Base de dados — papéis"); dados no volume `pgdata` |
| `mailpit` | <http://localhost:8025> (web), `localhost:1025` (SMTP) | caixa de correio de teste: todo o e-mail enviado fica aqui |
| `sms-mock` | <http://localhost:8090> | SMS e WhatsApp recebidos (coluna "canal"), actualizados a cada 3 s; botões para pôr cada canal em `OK`, `FAIL` ou `FAIL_RECEIPT` |
| `rabbitmq` | <http://localhost:15672> (consola), `localhost:5672` (AMQP) | RabbitMQ 4.1 (ADR-004); utilizador e palavra-passe `moseguros` (`RABBITMQ_DEFAULT_USER`/`PASS` do `.env`); o backend declara a topologia dos pagamentos no arranque (ver "RabbitMQ — topologia"); dados no volume `rabbitdata` |
| `sftp` | `localhost:2222` | SFTP onde o "banco" deposita os ficheiros MEPS/BMEPS (S5-09 lê-os); utilizadores `demo` e `demob` (ver [`mocks/sftp/README.md`](mocks/sftp/README.md)) |
| `vault` | <http://localhost:8200> | cofre de segredos em modo dev (em memória); entrar na interface com o token `dev-root-token` (`VAULT_DEV_ROOT_TOKEN` do `.env`) |
| `payment-file-worker` | <http://localhost:8081/actuator/health> | lê os ficheiros de movimentos do SFTP de cada seguradora e publica os pagamentos no RabbitMQ (ver "Worker de ficheiros de pagamento") |
| `ponto24-mock` | <http://localhost:8092> | simulador da rede Ponto24 e do banco (S5-08): paga referências e deposita ficheiros MEPS/BMEPS no `sftp` de minuto a minuto (ver "Pagar uma referência em local" e [`mocks/ponto24-mock/README.md`](mocks/ponto24-mock/README.md)) |

API do `sms-mock` (alvo do `MockSmsSender` e do `MockWhatsAppSender` do backend, S1-03):

```bash
curl -X POST localhost:8090/sms -H 'content-type: application/json' \
  -d '{"to":"+258841234567","text":"teste"}'   # 202 {"id": "..."}
curl -X POST localhost:8090/whatsapp -H 'content-type: application/json' \
  -d '{"to":"+258841234567","template":"moseguros_otp","params":{"code":"123456"}}'   # 202 {"id": "..."}
curl localhost:8090/sms                        # lista (SMS e WhatsApp), mais recente primeiro (últimas 500)
curl -X DELETE localhost:8090/sms              # limpa
curl localhost:8090/config                     # modos: {"sms":"OK","whatsapp":"OK"}
curl -X PUT localhost:8090/config -H 'content-type: application/json' -d '{"sms":"FAIL"}'
```

Modos de cada canal: `OK` (202 e, 2 s depois, recibo `DELIVERED`), `FAIL` (503, nada é guardado) e `FAIL_RECEIPT` (202 e, 2 s depois, recibo `FAILED`). Os recibos vão para `RECEIPT_CALLBACK_URL/mock-sms` ou `/mock-whatsapp` (no compose, o backend) com o cabeçalho `X-Webhook-Secret` (`WEBHOOK_SECRET` do `.env`); sem `RECEIPT_CALLBACK_URL` não há recibos.

As mensagens e os modos ficam só em memória: perdem-se quando o contentor reinicia.

### Login por código (OTP) à mão

Com a stack a correr, pedir um código na Swagger UI (<http://localhost:8080/swagger-ui.html>, `POST /api/v1/auth/otp/request`) ou com curl. No perfil `local` a resposta traz o código no cabeçalho `X-Debug-Otp`:

```bash
curl -s -D - -X POST localhost:8080/api/v1/auth/otp/request -H 'content-type: application/json' \
  -d '{"identifier":"841234567","identifierType":"MSISDN"}'
```

O SMS aparece em <http://localhost:8090>. Com o SMS em `FAIL` (botão na página), o pedido seguinte (passados 60 s) chega por WhatsApp (`"channel":"WHATSAPP","fallbackUsed":true`); com `FAIL_RECEIPT`, o SMS é aceite e, 2 s depois, o recibo de falha faz o backend reenviar por WhatsApp (`GET /api/v1/auth/otp/challenges/{challengeId}` passa a mostrar `WHATSAPP`). Os e-mails chegam ao Mailpit (<http://localhost:8025>).

## RabbitMQ — topologia

O backend declara, no arranque, a topologia dos pagamentos por referência (`PaymentsTopology` no `payment-contracts`, S5-06; ADR-004 e adendas):

| Elemento | Nome | Notas |
| --- | --- | --- |
| Exchange | `payments.events` | `topic`, durável; o `payment-file-worker` (S5-09) publica aqui |
| Exchange | `payments.dlx` | `direct`, durável; mensagens mortas |
| Fila | `core.reference-payments` | quorum; ligada a `reference.paid.*`; dead-letter para `payments.dlx` ao fim de 20 entregas |
| Fila | `core.payment-file-events` | quorum; ligada a `reference.file-processed.*`, `reference.file-rejected.*` e `reference.source-polled.*` |
| Fila | `core.reference-payments.dlq`, `core.payment-file-events.dlq` | quorum; mensagens rejeitadas ou entregues demasiadas vezes |

Ver as filas:

```bash
curl -s -u moseguros:moseguros localhost:15672/api/queues | grep -o '"name":"core[^"]*"'
```

Os consumidores das filas são do S5-10 (`ReferencePaymentListener` e `PaymentFileEventsListener`). Sem RabbitMQ o backend arranca na mesma (aviso no registo e `/api/v1/health` em `DOWN`) e declara a topologia quando conseguir ligar.

### Mensagens nas `.dlq` (S5-10)

Uma mensagem vai para a `.dlq` quando é inválida (JSON, `type`, `schemaVersion`, schema ou `insurerId` diferente da chave: logo, sem repetições) ou quando o backend falhou 6 vezes seguidas por um erro técnico (BD em baixo, *deadlock*). O registo do backend tem uma linha `ERROR` com o `messageId`; o `PaymentQueueMonitorJob` regista `alert=PAYMENT_DLQ_NOT_EMPTY` quando uma `.dlq` cresce. Não há reprocessamento por ecrã: corrigida a causa, as mensagens movem-se na consola do RabbitMQ (<http://localhost:15672>, fila `core.reference-payments.dlq` ou `core.payment-file-events.dlq`, "Move messages" para `core.reference-payments`/`core.payment-file-events`; precisa do *plugin* `rabbitmq_shovel_management`) ou com uma *shovel* dinâmica:

```bash
rabbitmqctl set_parameter shovel dlq-replay '{"src-protocol":"amqp091","src-uri":"amqp://","src-queue":"core.reference-payments.dlq","src-delete-after":"queue-length","dest-protocol":"amqp091","dest-uri":"amqp://","dest-queue":"core.reference-payments"}'
```

Reprocessar é seguro: o backend deduplica pelo `messageId` (`payment.inbound_message`), e uma mensagem inválida volta à `.dlq`.

## RabbitMQ — utilizadores

Em `local` há um só utilizador (`RABBITMQ_DEFAULT_USER`/`RABBITMQ_DEFAULT_PASS`), usado pelo backend (`RABBITMQ_USERNAME`/`RABBITMQ_PASSWORD`) e, a partir do S5-09, pelo worker.

**Staging e produção** (criados pela equipa de operações; palavras-passe no cofre de segredos; o utilizador `guest` é apagado):

| Utilizador | `configure` | `write` | `read` | Quem |
| --- | --- | --- | --- | --- |
| `moseguros_backend` | `^(payments\..*\|core\..*)$` | `^(payments\..*\|core\..*)$` | `^(payments\..*\|core\..*)$` | backend: declara a topologia e consome as filas `core.*` |
| `moseguros_worker` | `^payments\.events$` | `^payments\.events$` | (vazio) | worker: declara o exchange e só publica |

```bash
rabbitmqctl add_user moseguros_backend '<do cofre>'
rabbitmqctl set_permissions -p / moseguros_backend '^(payments\..*|core\..*)$' '^(payments\..*|core\..*)$' '^(payments\..*|core\..*)$'
rabbitmqctl add_user moseguros_worker '<do cofre>'
rabbitmqctl set_permissions -p / moseguros_worker '^payments\.events$' '^payments\.events$' ''
```

## Dados de pagamento de desenvolvimento

- **Vault** (modo dev, <http://localhost:8200>, token `dev-root-token`): *mount* KV v2 `secret/`. Os segredos ficam em `secret/insurers/{insurerId}/payments/{METHOD}/{campo}` (S5-01); a BD guarda só a referência. Em modo dev o Vault perde tudo quando o contentor reinicia; no perfil `local` o backend volta a escrever os segredos de desenvolvimento das seguradoras de demonstração no arranque (`DevPaymentSecretsSeeder`, S5-01), por exemplo `BANK_REFERENCE/sftp.password` = `demo-sftp-pass` (Seguradora Demo) e `demob-sftp-pass` (Seguradora Demo B).
- **SFTP**: utilizadores `demo` (MEPS, entidade `90002`) e `demob` (BMEPS, entidade `90090`, empresa `800`, perfil `02`), pastas `inbound`, `processed`, `error` ([`mocks/sftp/README.md`](mocks/sftp/README.md)). O `ponto24-mock` deposita lá os ficheiros (S5-08); os exemplos oficiais (`docs/specs/interbancos/exemplo-ficheiro-*.txt`) também se enviam pela página dele.
- Ler um segredo à mão: `docker compose exec -e VAULT_ADDR=http://127.0.0.1:8200 -e VAULT_TOKEN=dev-root-token vault vault kv get secret/insurers/01930000-0000-7000-8000-000000000001/payments/BANK_REFERENCE/sftp.password`.

## Base de dados — papéis

A partir do S1-01 a aplicação **não** se liga à base de dados como dona das tabelas (ADR-002 e a adenda de 01/11/2026):

| Papel | Para quê | Local (`docker compose`) |
| --- | --- | --- |
| dono (`POSTGRES_USER` do `.env`, por defeito `moseguros`) | corre as migrações Flyway; dono de todas as tabelas | `DB_OWNER_USERNAME` / `DB_OWNER_PASSWORD` do backend |
| `moseguros_app` (sem login) | recebe os privilégios (`select`, `insert`, `update`; `delete` só em `identity.user_role` e `public.event_publication`) | criado por `postgres/init/01-roles.sql` |
| `moseguros_app_user` (login, membro de `moseguros_app`) | a aplicação; sem `SUPERUSER`, sem `BYPASSRLS` e sem ser dona de nenhuma tabela, para que o Row Level Security se aplique a ela | `DB_USERNAME=moseguros_app_user`, `DB_PASSWORD=moseguros_app` |
| `moseguros_worker` (sem login) | privilégios (`select`, `insert`, `update`) só no schema `payment_worker` do `payment-file-worker` (S5-09); nenhum nos schemas do backend | criado por `postgres/init/02-worker-role.sql` (e pela migração do worker, se faltar) |
| `moseguros_worker_user` (login, membro de `moseguros_worker`) | o `payment-file-worker` | `DB_USERNAME=moseguros_worker_user`, `DB_PASSWORD=moseguros_worker` |

O Postgres só corre os scripts de `postgres/init` (`/docker-entrypoint-initdb.d`) quando o volume está **vazio**. Quem já tinha o volume `pgdata` de antes do S1-01 apaga-o uma vez:

```bash
docker compose down -v && docker compose up -d --build
```

Sem isto o backend não arranca (`password authentication failed for user "moseguros_app_user"`).

**Staging e produção:** os papéis são criados pela equipa de operações antes do primeiro deploy, com o mesmo conteúdo de `postgres/init/01-roles.sql` (a palavra-passe de `moseguros_app_user` vem do cofre de segredos, não a do ficheiro). O dono das tabelas não precisa de ser superutilizador: basta ser dono da base de dados. O backend recebe `DB_URL`, `DB_USERNAME`/`DB_PASSWORD` (`moseguros_app_user`), `DB_OWNER_USERNAME`/`DB_OWNER_PASSWORD` (dono, só para o Flyway) e `MOSEGUROS_DATA_KEY_1` (chave de cifra das colunas, 32 bytes em Base64; sem ela a aplicação não arranca), e `MOSEGUROS_BLIND_INDEX_KEY` (chave do índice cego, 32 bytes em Base64, diferente da anterior; sem ela a aplicação não arranca).

## Worker de ficheiros de pagamento

O `payment-file-worker` (S5-09, ADR-004 e ADR-007) é um executável à parte do backend: isola as credenciais SFTP e o processamento dos ficheiros da API pública. Tem BD própria (schema `payment_worker`, histórico Flyway em `payment_worker.flyway_schema_history`) e só fala com o backend por HTTP interno e pelo RabbitMQ.

1. No arranque e de minuto a minuto pede um **token de serviço** ao backend (`POST /internal/v1/auth/token`, `client_credentials` com Basic `payment-file-worker` / `SERVICE_CLIENT_PAYMENT_FILE_WORKER_SECRET`) e as **fontes** (`GET /internal/v1/payment-file-sources`): as configurações `BANK_REFERENCE` activas de todas as seguradoras e as inactivas que ainda têm referências por pagar. Nunca recebe segredos, só os caminhos no Vault.
2. Lê cada fonte continuamente: `poll-interval` (60 s; `WORKER_POLL_INTERVAL`, mínimo 10 s) depois do fim da leitura anterior. Duas instâncias nunca leem a mesma fonte ao mesmo tempo (ShedLock, `payment_worker.shedlock`).
3. Em cada leitura: palavra-passe ou chave SFTP do Vault, ligação ao SFTP (chave do servidor pela `hostKeyFingerprint`; em `local` aceita chaves desconhecidas), ficheiros de `inboundDir` (sem `.part` nem os que começam por `.`), validação, um `ReferencePaymentReceived` por registo com *publisher confirms*, mudança para `processedDir` (ou `errorDir`), `PaymentFileProcessed` / `PaymentFileRejected` e, no fim, `PaymentSourcePolled`.
4. Um ficheiro **nunca é processado duas vezes**: nem com o mesmo nome, nem com outro, nem deixado em `inbound` (SFTP só de leitura): pelo SHA-256 do conteúdo e pelo identificador do ficheiro no formato (`DUPLICATE_FILE_ID`, alerta `PAYMENT_FILE_DUPLICATE_ID`).

O papel `moseguros_worker_user` só é criado com o volume `pgdata` vazio: quem já tinha a stack de antes do S5-09 corre uma vez `docker compose down -v && docker compose up -d --build` (senão o worker falha com `password authentication failed for user "moseguros_worker_user"`).

O worker tem os leitores reais `MEPS` e `BMEPS` (S5-08), em todos os perfis. Ver o estado:

```bash
docker compose ps payment-file-worker                      # (healthy)
curl -s localhost:8081/actuator/health                     # db, rabbit e sources (fontes agendadas)
docker compose logs payment-file-worker | grep "Source "   # fontes agendadas no arranque
curl -s localhost:8081/actuator/metrics/worker.polls
```

**Staging e produção:** `/internal/**` **não** pode ser encaminhado pelo *proxy* de entrada (o nginx da web só encaminha `/api/`; S6-08): o worker chama o backend pela rede interna. O worker recebe `WORKER_CLIENT_SECRET` (igual a `MOSEGUROS_SERVICE_CLIENT_PAYMENT_FILE_WORKER_SECRET` do backend, pelo menos 32 caracteres, do cofre de segredos), `VAULT_URI`/`VAULT_TOKEN` (só leitura de `secret/insurers/*`), `DB_*` (`moseguros_worker_user`, criado como em `postgres/init/02-worker-role.sql` com a palavra-passe do cofre) e o utilizador RabbitMQ `moseguros_worker`. Sem `WORKER_CLIENT_SECRET` ou `VAULT_TOKEN`, ou com `allow-unknown-host-keys`, não arranca.

## Pagar uma referência em local

O `ponto24-mock` (S5-08) faz de rede Ponto24 e de banco: aceita o pagamento de uma referência (como a rede, recusa com `INVALID_REFERENCE` um montante diferente do da referência) e, de minuto a minuto, deposita no SFTP da seguradora um ficheiro no formato dela; o worker lê-o e o backend confirma o pagamento. Seguradora Demo: MEPS, entidade `90002`, utilizador SFTP `demo`; Seguradora Demo B: BMEPS, entidade `90090` (empresa `800`, perfil `02`), utilizador `demob`.

1. Na web, como cliente, pagar uma proposta aceite por referência e anotar a entidade, a referência e o montante.
2. Em <http://localhost:8092>, escolher a entidade, introduzir a referência e o montante e carregar em **Pagar** (ou pela API):
   ```bash
   curl -s -X POST localhost:8092/payments -H 'content-type: application/json' \
     -d '{"entity":"90002","reference":"<referência mostrada na web>","amountCentavos":299900}'
   ```
3. No minuto seguinte (ou com **Enviar ficheiros agora** / `curl -s -X POST localhost:8092/settlements/run`) o ficheiro aparece em `processed` no SFTP e a proposta passa a `PAID`:
   ```bash
   sftp -P 2222 demo@localhost      # palavra-passe demo-sftp-pass; ls processed error
   docker compose logs payment-file-worker | grep "parsed"
   ```
4. Outros casos na página: **Reenviar o último ficheiro** (o worker não o processa de novo), pagar a mesma referência outra vez (excepção `ALREADY_PAID`, S5-10), **Forçar** com outro montante (`AMOUNT_MISMATCH`, S5-10), **Enviar ficheiro com rodapé errado** (vai para `error`), **Enviar ficheiro vazio**, **Enviar exemplo oficial** e **Limpar**.

O simulador guarda tudo em memória: depois de o reiniciar, as sequências dos ficheiros recomeçam e o worker pode recusar um ficheiro com um identificador já visto (`DUPLICATE_FILE_ID`); `docker compose down -v` limpa tudo.

## Ficheiros

Os ficheiros enviados (fotos do livrete, documentos dos produtos…; S2-01, ADR-005) ficam em disco no volume `files`, montado no backend em `/var/lib/moseguros/files` (`MOSEGUROS_FILES_DIR`). Cada ficheiro tem a chave `{aaaa}/{mm}/{dd}/{id}.{ext}`; os metadados estão em `document.file`. Os ficheiros dos dados de demonstração ficam em `dev-seed/` e são copiados no arranque do backend se faltarem.

- Ver o que lá está: `docker compose exec backend ls -R /var/lib/moseguros/files`.
- Descarregar: `GET /api/v1/files/{id}` (o módulo dono decide quem pode ler; sem autorização → 404).
- Limite de 10 MB por ficheiro; o nginx da web aceita pedidos até 12 MB (`client_max_body_size` em `web/nginx.conf`).
- Apagar tudo: `docker compose down -v` (apaga também a BD, para os metadados não ficarem sem conteúdo).

Fora do Docker (backend no IDE com o perfil `local`) os ficheiros ficam em `backend/var/files` (ignorada pelo git). Staging e produção usam S3 (`MOSEGUROS_S3_*`).

## Dados de demonstração

No perfil `local` o Flyway aplica também `backend/src/main/resources/db/dev/` (scripts repetíveis `R__dev_seed_s{n}.sql`; nunca em staging nem produção):

| O quê | Valores |
| --- | --- |
| Seguradora | "Seguradora Demo, SA" (`DEMO`), NUIT `400000001`, id `01930000-0000-7000-8000-000000000001` |
| Corretora | "Corretora Demo, Lda", NUIT `400000002`, id `01930000-0000-7000-8000-000000000003` |
| Gestor da seguradora | convite para `gestor@demo.local` (`INSURER_ADMIN`, `PRODUCT_MANAGER`) |
| Corretor | convite para `corretor@demo.local` (`ORG_ADMIN`, `BROKER`) |
| Cliente de teste | `+258840000001` ("Cliente Teste"), entra por OTP com o código no cabeçalho `X-Debug-Otp` |
| Veículo do cliente de teste | `AAA-100-MC` (Toyota Corolla 2018, `PRIVATE`, `MAPUTO_CITY`, 850 000 MZN), verificado no registo simulado; id `01930000-0000-7000-8000-000000000101`; sem livrete nem condutores |
| Segunda seguradora (S3-01) | "Seguradora Demo B, SA" (`DEMOB`), NUIT `400000010`, id `01930000-0000-7000-8000-000000000320`; sem utilizadores |
| Produtos publicados | nas duas seguradoras: `RC-AUTO` (`MTPL_ONLY`), `AUTO-COMPLETO` (`COMPREHENSIVE`) e `TEMP-AUTO` "Temporário Demo" (`TEMPORARY`, desde 2026-12-15; só `MTPL`); ids da Demo B `…0331`–`…0333` (versões `…0341`–`…0343`) |
| Tarifas e encargos (S3-01) | tarifas de `docs/tasks/S3-rating-cases.md` em todos os produtos (`validateRatingRules` sem erros); a Demo B tem mais o factor `COMMERCIAL` (1,08) em cada cobertura. Encargos das duas: `DEMO_STAMP` 2,5 % e `DEMO_FEE` 50 MT (provisórios) |

Activar as contas de staff (palavra-passe e TOTP) abrindo os convites, que não expiram:

- <http://localhost:5173/convite/dev-convite-gestor-demo> (no compose: <http://localhost:3000/convite/dev-convite-gestor-demo>)
- <http://localhost:5173/convite/dev-convite-corretor-demo> (no compose: <http://localhost:3000/convite/dev-convite-corretor-demo>)

Os ids são fixos (`01930000-0000-7000-8000-00000000000N`, N = 1 a 7, `…0101` para o veículo, `…02NN` para os produtos do S2 e `…03NN` para os do S3) e os scripts não duplicam nada se correrem outra vez.

### Registo automóvel simulado (INATRO)

Até haver acesso ao INATRO, o backend usa um registo simulado (`moseguros.vehicle.registry.provider: mock`, em todos os perfis; em `staging` e `prod` regista um aviso no arranque). Dados fictícios e fixos, usados pelo `GET /api/v1/me/vehicles/registry-lookup?plate=` e para marcar `inatroVerified` ao registar um veículo (matrícula e chassis iguais):

| Matrícula | Chassis | Marca / modelo | Ano | cc | Categoria | Lugares | Peso (kg) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `AAA-100-MC` | `JTDBR32E720012345` | Toyota / Corolla | 2018 | 1598 | `LIGHT_PASSENGER` | 5 | — |
| `AAA-200-MC` | `AHTFR22G106012345` | Toyota / Hilux | 2015 | 2982 | `LIGHT_MIXED` | 5 | — |
| `AAA-300-MC` | `WDB9340321L123456` | Mercedes-Benz / Actros | 2012 | 11946 | `HEAVY` | 2 | 40000 |
| `AAA-400-MC` | `LTMKD0791L5123456` | Honda / CG 125 | 2020 | 124 | `MOTORCYCLE` | 2 | — |
| `AAA-500-MC` | `JTFSS22P900123456` | Toyota / Quantum | 2017 | 2494 | `BUS` | 15 | 3300 |
| `MLA-12-34` | `ADNJ720000E123456` | Nissan / Hardbody | 2004 | 2389 | `LIGHT_MIXED` | 5 | — |
| `AAA-999-MC` | — | — | — | — | — | — | — |

`AAA-999-MC` simula o registo indisponível (503 `VEHICLE_REGISTRY_UNAVAILABLE`; ao registar o veículo grava-o sem verificação). Qualquer outra matrícula não tem registo (404 `VEHICLE_REGISTRY_NOT_FOUND`).

## Comandos úteis

```bash
docker compose logs -f backend            # registos de um serviço
docker compose up -d --build backend      # reconstruir só o backend depois de mudar código
docker compose stop                       # parar sem apagar nada
docker compose down                       # remover os contentores (os dados da BD ficam)
docker compose down -v                    # remover também os volumes (BD, ficheiros, RabbitMQ e SFTP vazios)
```

Para correr o backend ou a web fora do Docker (ex.: no IDE), suba só o que precisa, por exemplo `docker compose up -d postgres mailpit sms-mock rabbitmq vault`; o backend com o perfil `local` liga-se a `localhost:5432` e `localhost:5672` e a web (`pnpm dev`) faz proxy de `/api` para `localhost:8080`.

O backend é um módulo do *reactor* Maven da raiz (S5-06, decisão D18). Na raiz do repositório:

```bash
./mvnw -B verify                          # tudo: payment-contracts e backend
./mvnw -B -pl backend -am verify          # só o backend e o que ele usa (sem instalar nada)
./mvnw -B -pl payment-contracts install   # instala os contratos no ~/.m2 (uma vez, e sempre que mudarem)
cd backend && ./mvnw -B verify            # também funciona, depois do install acima
```

A imagem do backend constrói-se com o contexto na raiz: `docker build -f backend/Dockerfile .` (o compose já o faz).

## Notas

- As imagens têm versão fixada; quem mudar uma imagem aqui ou num `Dockerfile` actualiza `scripts/images.txt` no mesmo PR.
- Os contentores do backend, do `sms-mock` e do `ponto24-mock` correm como `app` (uid 10001) e o da web como `nginx` (uid 101).
- O nginx escuta em IPv4 e IPv6 dentro do contentor (o healthcheck usa `localhost`, que resolve para `::1`).
- Se o Docker Hub responder 429 (limite de pedidos), ver as notas em `docs/STATUS.md`.
- O compose guarda os ficheiros em disco (volume `files`); S3 só em staging e produção (fornecedor escolhido com o alojamento, S6-08). RabbitMQ, SFTP e o cofre de segredos (Vault em modo dev) entraram no S5-06; o `mpesa-mock` entra no S5-03, o `payment-file-worker` no S5-09 e o `ponto24-mock` no S5-08.
- O `atmoz/sftp` só tem imagem `amd64`: em `arm64` corre por emulação.
