# payment-file-worker

Executável à parte do backend (S5-09, ADR-004, ADR-007) que lê os ficheiros de movimentos MEPS e BMEPS (`docs/specs/interbancos/`) que o banco deposita no SFTP de cada seguradora, valida-os e publica cada pagamento por entidade e referência no RabbitMQ (`payments.events`, contratos em `payment-contracts`). O backend consome-os no S5-10. O dinheiro nunca passa pela plataforma: o worker só confirma pagamentos que o banco já creditou na conta da seguradora.

## Correr

Na raiz do repositório:

```bash
./mvnw -B -pl payment-file-worker -am verify     # testes unitários e de integração (Docker)
./mvnw -B -pl payment-file-worker -am -DskipTests package
```

Em local, com a stack do `infra/docker-compose.yml` (serviço `payment-file-worker`, Actuator em <http://localhost:8081/actuator/health>), ou fora do Docker com `SPRING_PROFILES_ACTIVE=local` (backend em `localhost:8080`, Postgres, RabbitMQ e Vault do compose). Ver `infra/README.md`, "Worker de ficheiros de pagamento".

## Como funciona

| Peça | O que faz |
| --- | --- |
| `sources/ServiceTokenProvider` | token de serviço do backend (`client_credentials`), guardado até 60 s antes de expirar; um 401 pede outro e repete uma vez |
| `sources/BackendSourceClient` | `GET /internal/v1/payment-file-sources` (`FileSourceList`, sem segredos) |
| `sources/SourceRegistry` | agenda cada fonte com `scheduleWithFixedDelay(poll-interval)`; reagenda quando a `version` muda, cancela as que deixam de vir; também o indicador de saúde `sources` |
| `processing/SourcePoller` | uma leitura com o trinco `source:{sourceId}` (ShedLock): segredos do Vault, SFTP, listagem, ficheiros, `PaymentSourcePolled` |
| `processing/FileProcessor` | um ficheiro: deduplicação (nome+tamanho+data, SHA-256, identificador no formato), leitura, validação, registos, publicação, mudança de pasta |
| `processing/RecordPublisher` | publica com *publisher confirms* e `mandatory`; só `ack` sem devolução conta |
| `parser/` | contrato dos leitores (`PaymentFileParser`, `ParserRegistry`, `FileValidator`); os leitores MEPS e BMEPS entram no S5-08 |
| `sftp/` | ligações SFTP (Spring Integration / Apache MINA SSHD) e verificação da chave do servidor por impressão digital |
| `secrets/` | leitura dos segredos no Vault (KV v2) |
| `domain/` | `payment_worker.processed_file` e `payment_worker.file_record` (Spring Data JDBC) |

Estados de um ficheiro: `RECEIVED → PARSED → PUBLISHED → DONE`, ou `ERROR` (erro de conteúdo: não se tenta de novo com o mesmo conteúdo). Uma falha de publicação deixa o ficheiro em `PARSED` e a leitura seguinte publica só os registos que faltam.

## Configuração

`moseguros.worker.*` e `moseguros.vault.*` em `src/main/resources/application.yml`. Fora dos perfis `local` e `test` não arranca sem `WORKER_CLIENT_SECRET` e `VAULT_TOKEN`, nem com `allow-unknown-host-keys = true`; em todos os perfis `poll-interval` tem de ser pelo menos `PT10S`.

## Testes

- Unitários (`*Test`): `FileValidatorTest`, `SourceRegistryTest`, `ServiceTokenProviderTest`, `StartupChecksTest`.
- Integração (`*IT`, Testcontainers): `FileProcessingIT`, `SourceLockIT` (Postgres com `src/test/resources/db/init-roles.sql`, `rabbitmq:4.1-management`, `atmoz/sftp` com o `src/test/resources/sftp/sshd_config` e o `sftp-logged.sh`, que registam as aberturas de ficheiros em `/tmp/sftp.log`, backend simulado com WireMock) e `VaultSecretReaderIT` (`hashicorp/vault:1.20.4`).
- Até ao S5-08 usam o leitor `TestStubParser` (formato `TEST_STUB`, só em `src/test`).
