# Testes de carga (k6)

Scripts da Release 1 (S6-08 §4). Metas do plano: **50 cotações/s** e **200 verificações/s** com
**p95 < 1 s**, menos de 1% de erros. A corrida local prova os scripts e dá uma referência; a **corrida
oficial é em staging** (bloqueada até haver alojamento, `infra/staging/README.md`). Resultados resumidos
em `docs/releases/R1.md`.

| Script | O que faz |
| --- | --- |
| `smoke.js` | 1 utilizador virtual, 30 s, um pedido de cada tipo; valida os scripts e os dados |
| `verifications.js` | `constant-arrival-rate` de 200/s: 80% `GET /api/v1/public/verifications/{código}`, 20% por matrícula |
| `quotes.js` | `constant-arrival-rate` de 50/s: `POST /api/v1/quotes` com os 20 clientes de carga |
| `get-tokens.sh` | tokens de acesso (OTP com `X-Debug-Otp`, só perfil `local`) e `data/quote-requests.json` |
| `lib/config.js` | `BASE_URL` (defeito `http://localhost:3000`), `TOKENS_FILE`, `QUOTES_FILE`, `DURATION` (defeito `5m`), `RATE`, `QUOTE_START_DATE` |

## Dados

O `R__dev_seed_s6.sql` (perfil `local`) traz:

- 1 000 apólices `ACTIVE` da Seguradora Demo com as matrículas `PRF-000-MP` … `PRF-999-MP` e os
  certificados `PERF000000000000000000` … `PERF000000000000000999` (os scripts geram estes códigos);
- 20 clientes `+258841000000` … `+258841000019`, cada um com o veículo `PRQ-0NN-MP`. O condutor de cada
  um é criado pelo `get-tokens.sh` pela API (o número da carta é cifrado e não se gera em SQL).

`data/` e `results/` ficam fora do Git.

## Ambiente de carga

Variáveis **só para estas corridas** (o compose lê-as do shell; nunca no `.env` de staging):

```bash
export MOSEGUROS_VERIFICATION_RATE_LIMIT_PUBLIC_PER_MINUTE=1000000   # todos os pedidos vêm do mesmo IP
export MOSEGUROS_RATING_MAX_REQUESTS_PER_HOUR=1000000                # 30 cotações/h por cliente por defeito
export MOSEGUROS_CAPTCHA_PROVIDER=none                                # já é o defeito do perfil local
cd infra && docker compose up -d --build && cd ..
```

As seguradoras de demonstração só têm tarifação `INTERNAL` (nenhuma chamada externa entra na medida).

## Correr

```bash
./perf/get-tokens.sh                         # tokens válidos 15 minutos; repetir a menos de 60 s dá OTP_RESEND_TOO_SOON
docker run --rm --network host -v "$PWD/perf:/perf" grafana/k6:0.54.0 run /perf/smoke.js
docker run --rm --network host -v "$PWD/perf:/perf" grafana/k6:0.54.0 run \
  --summary-export /perf/results/verifications-$(date +%F).json /perf/verifications.js
./perf/get-tokens.sh && docker run --rm --network host -v "$PWD/perf:/perf" grafana/k6:0.54.0 run \
  --summary-export /perf/results/quotes-$(date +%F).json /perf/quotes.js
```

- Em Git Bash (Windows) prefixar com `MSYS_NO_PATHCONV=1` e usar `-v "$(pwd -W)/perf:/perf"`.
- No Docker Desktop o `--network host` só existe com a opção *host networking* activa; a alternativa é
  `-e BASE_URL=http://host.docker.internal:3000`.
- Corrida mais curta: `-e DURATION=1m`; outro ritmo: `-e RATE=100`.
- Durante a corrida, os painéis "API" e "Negócio" do Grafana (`docker compose --profile observability up -d`)
  mostram o p95 por `uri` e as verificações por resultado.
