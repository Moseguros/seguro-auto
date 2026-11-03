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

## Base de dados — papéis

A partir do S1-01 a aplicação **não** se liga à base de dados como dona das tabelas (ADR-002 e a adenda de 01/11/2026):

| Papel | Para quê | Local (`docker compose`) |
| --- | --- | --- |
| dono (`POSTGRES_USER` do `.env`, por defeito `moseguros`) | corre as migrações Flyway; dono de todas as tabelas | `DB_OWNER_USERNAME` / `DB_OWNER_PASSWORD` do backend |
| `moseguros_app` (sem login) | recebe os privilégios (`select`, `insert`, `update`; `delete` só em `identity.user_role` e `public.event_publication`) | criado por `postgres/init/01-roles.sql` |
| `moseguros_app_user` (login, membro de `moseguros_app`) | a aplicação; sem `SUPERUSER`, sem `BYPASSRLS` e sem ser dona de nenhuma tabela, para que o Row Level Security se aplique a ela | `DB_USERNAME=moseguros_app_user`, `DB_PASSWORD=moseguros_app` |

O Postgres só corre os scripts de `postgres/init` (`/docker-entrypoint-initdb.d`) quando o volume está **vazio**. Quem já tinha o volume `pgdata` de antes do S1-01 apaga-o uma vez:

```bash
docker compose down -v && docker compose up -d --build
```

Sem isto o backend não arranca (`password authentication failed for user "moseguros_app_user"`).

**Staging e produção:** os papéis são criados pela equipa de operações antes do primeiro deploy, com o mesmo conteúdo de `postgres/init/01-roles.sql` (a palavra-passe de `moseguros_app_user` vem do cofre de segredos, não a do ficheiro). O dono das tabelas não precisa de ser superutilizador: basta ser dono da base de dados. O backend recebe `DB_URL`, `DB_USERNAME`/`DB_PASSWORD` (`moseguros_app_user`), `DB_OWNER_USERNAME`/`DB_OWNER_PASSWORD` (dono, só para o Flyway) e `MOSEGUROS_DATA_KEY_1` (chave de cifra das colunas, 32 bytes em Base64; sem ela a aplicação não arranca).

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

Activar as contas de staff (palavra-passe e TOTP) abrindo os convites, que não expiram:

- <http://localhost:5173/convite/dev-convite-gestor-demo> (no compose: <http://localhost:3000/convite/dev-convite-gestor-demo>)
- <http://localhost:5173/convite/dev-convite-corretor-demo> (no compose: <http://localhost:3000/convite/dev-convite-corretor-demo>)

Os ids são fixos (`01930000-0000-7000-8000-00000000000N`, N = 1 a 7) e os scripts não duplicam nada se correrem outra vez.

## Comandos úteis

```bash
docker compose logs -f backend            # registos de um serviço
docker compose up -d --build backend      # reconstruir só o backend depois de mudar código
docker compose stop                       # parar sem apagar nada
docker compose down                       # remover os contentores (os dados da BD ficam)
docker compose down -v                    # remover também os volumes pgdata e files (BD e ficheiros vazios)
```

Para correr o backend ou a web fora do Docker (ex.: no IDE), suba só o que precisa, por exemplo `docker compose up -d postgres mailpit sms-mock`; o backend com o perfil `local` liga-se a `localhost:5432` e a web (`pnpm dev`) faz proxy de `/api` para `localhost:8080`.

## Notas

- As imagens têm versão fixada; quem mudar uma imagem aqui ou num `Dockerfile` actualiza `scripts/images.txt` no mesmo PR.
- Os contentores do backend e do `sms-mock` correm como `app` (uid 10001) e o da web como `nginx` (uid 101).
- O nginx escuta em IPv4 e IPv6 dentro do contentor (o healthcheck usa `localhost`, que resolve para `::1`).
- Se o Docker Hub responder 429 (limite de pedidos), ver as notas em `docs/STATUS.md`.
- O compose guarda os ficheiros em disco (volume `files`); S3 só em staging e produção (fornecedor escolhido com o alojamento, S6-08). RabbitMQ, SFTP, cofre de segredos e `mpesa-mock` (Sprint 5) ainda não fazem parte do compose.
