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
| `sms-mock` | <http://localhost:8090> | lista dos SMS recebidos, actualizada a cada 3 s |

API do `sms-mock` (alvo do `SmsSender` mock a partir do Sprint 1):

```bash
curl -X POST localhost:8090/sms -H 'content-type: application/json' \
  -d '{"to":"+258841234567","text":"teste"}'   # 202 {"id": "..."}
curl localhost:8090/sms                        # lista, mais recente primeiro (últimas 500)
curl -X DELETE localhost:8090/sms              # limpa
```

As mensagens ficam só em memória: perdem-se quando o contentor reinicia.

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

## Comandos úteis

```bash
docker compose logs -f backend            # registos de um serviço
docker compose up -d --build backend      # reconstruir só o backend depois de mudar código
docker compose stop                       # parar sem apagar nada
docker compose down                       # remover os contentores (os dados da BD ficam)
docker compose down -v                    # remover também o volume pgdata (BD vazia no próximo arranque)
```

Para correr o backend ou a web fora do Docker (ex.: no IDE), suba só o que precisa, por exemplo `docker compose up -d postgres mailpit sms-mock`; o backend com o perfil `local` liga-se a `localhost:5432` e a web (`pnpm dev`) faz proxy de `/api` para `localhost:8080`.

## Notas

- As imagens têm versão fixada; quem mudar uma imagem aqui ou num `Dockerfile` actualiza `scripts/images.txt` no mesmo PR.
- Os contentores do backend e do `sms-mock` correm como `app` (uid 10001) e o da web como `nginx` (uid 101).
- O nginx escuta em IPv4 e IPv6 dentro do contentor (o healthcheck usa `localhost`, que resolve para `::1`).
- Se o Docker Hub responder 429 (limite de pedidos), ver as notas em `docs/STATUS.md`.
- Armazenamento S3 (Sprint 3), RabbitMQ, SFTP, cofre de segredos e `mpesa-mock` (Sprint 5) ainda não fazem parte do compose.
