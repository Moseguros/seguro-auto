# Staging — requisitos do alojamento

**Estado: BLOQUEADO.** O alojamento de staging e produção e o cofre de segredos gerido ainda não foram
decididos pelo proprietário do produto (modelo transversal §9; Questões do S6-08). Esta pasta deixa
preparado o que não depende dessa decisão. Quando o alojamento for escolhido, uma ficha nova (criada pelo
proprietário do produto ou pelo tech lead) faz o deploy, a corrida oficial do k6 (`perf/`) e o varrimento
ZAP (`scripts/security-scan.sh`) contra staging.

- `.env.staging.example` — todas as variáveis de que o backend, o worker e a web precisam, sem valores
  (confirmado pelo `scripts/check-env-example.sh`). As credenciais M-Pesa e SFTP de cada seguradora não
  entram aqui: ficam no cofre (S5-01, `AGENTS.md` §6).
- Imagens: `ghcr.io/moseguros/backend`, `ghcr.io/moseguros/payment-file-worker` e `ghcr.io/moseguros/web`
  com a *tag* da versão (`v1.0.0-rc.1`, …) e `sha-<curto>`, publicadas pelo `.github/workflows/release.yml`.
- Cópias de segurança: `infra/backup/pg-backup.sh` e `pg-restore-test.sh` (testados contra o compose local).
- Observabilidade: `infra/observability/` (Prometheus, Alertmanager, Grafana, alertas e painéis).

## Lista para marcar (o alojamento escolhido tem de cumprir)

### Rede e HTTPS

- [ ] HTTPS com TLS ≥ 1.2 no terminador; HTTP redirecciona para HTTPS.
- [ ] HSTS no terminador: `Strict-Transport-Security: max-age=31536000` (o nginx da web não o envia).
- [ ] **IP público de saída fixo**: todas as seguradoras registam o mesmo IP da plataforma na Vodacom para o
      M-Pesa (decisão do proprietário do produto de 18/11/2026, ADR-007).
- [ ] O terminador substitui o `X-Forwarded-For` (não acrescenta) e o seu endereço entra em
      `MOSEGUROS_WEB_TRUSTED_PROXIES` (D15); só o nginx da web e o terminador são de confiança.
- [ ] `/api/v1/prometheus`, `/internal/**` e as portas de gestão (worker 8081, RabbitMQ 15672/15692,
      Vault, PostgreSQL) só na rede interna; `MOSEGUROS_OBSERVABILITY_ALLOWED_CIDRS` = rede do Prometheus.

### Dados

- [ ] PostgreSQL 16 com os papéis da adenda do ADR-002: `moseguros_app_user` sem privilégios de dono, o
      dono só para o Flyway (`DB_OWNER_*`), `moseguros_worker_user` para o worker.
- [ ] Cópias de segurança diárias com retenção de 14 dias (`pg-backup.sh` ou as do serviço gerido) e **um
      teste de restauro antes da R1** (`pg-restore-test.sh` numa base temporária).
- [ ] RabbitMQ 4.1 com filas *quorum* (ADR-004 e adenda), utilizadores `moseguros_backend` e
      `moseguros_worker` (`infra/README.md`), plugin `rabbitmq_prometheus`.
- [ ] Cofre de segredos gerido (Vault ou equivalente) para as chaves da aplicação, o FCM, o CAPTCHA e as
      credenciais M-Pesa/SFTP de cada seguradora.
- [ ] Armazenamento compatível com S3 com cifra do lado do servidor (ADR-005).
- [ ] SMTP autenticado para os e-mails da plataforma.

### Observabilidade

- [ ] Prometheus, Alertmanager e Grafana com acesso autenticado (sem acesso anónimo).
- [ ] Destinatários reais dos alertas (e-mail da equipa) no `alertmanager.yml` e horário de resposta
      (Questões do S6-08).
- [ ] Retenção das métricas de pelo menos 15 dias.

### Deploy

- [ ] Imagens da secção 7 por *tag* (nunca `latest`); os *mocks* não são publicados.
- [ ] Migrações Flyway com o utilizador dono no arranque do backend (e do worker, schema `payment_worker`).
- [ ] Perfil `staging` com CAPTCHA, push e SMS/WhatsApp conforme as decisões em aberto (mocks enquanto não
      houver fornecedor: `MOSEGUROS_CAPTCHA_PROVIDER=none` regista um WARN; o push `mock` também).
- [ ] `ponto24-mock` a depositar os ficheiros MEPS/BMEPS no SFTP de teste (só staging/QA, nunca produção).

## Depois do deploy (ficha nova)

1. `docker run grafana/k6:0.54.0 run /perf/verifications.js` e `quotes.js` contra staging (metas: p95 < 1 s).
2. `scripts/security-scan.sh` com `WEB_TARGET`/`API_TARGET` de staging; sem alertas `FAIL` por tratar.
3. Teste ponta a ponta da R1 (`web/e2e-stack/`) com `STACK_BASE_URL` de staging.
4. `mobile-distribute.yml` com `STAGING_API_BASE_URL` e o projecto Firebase.
5. Actualizar `docs/releases/R1.md`.
