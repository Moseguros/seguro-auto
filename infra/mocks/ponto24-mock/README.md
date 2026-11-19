# ponto24-mock — simulador da rede Ponto24 e do banco (S5-08)

**Só para `local`, testes e QA.** Não é a rede Ponto24 e nenhum dinheiro real é movimentado. O compose de produção não o tem.

Faz de rede e de banco: aceita pagamentos de referências (valida os dígitos de controlo com a entidade **e o montante**, como a rede) e, de minuto a minuto, deposita no SFTP da seguradora um ficheiro de movimentos no formato dela (MEPS ou BMEPS, `docs/specs/interbancos/`). Não conhece o backend: o `payment-file-worker` lê o ficheiro e o backend confirma o pagamento.

- Página: `http://localhost:8092` (pagar, enviar ficheiros, reenviar, exemplos oficiais, limpar).
- Testes: `./mvnw -B verify` nesta pasta (o `SftpDeliveryIT` precisa de Docker). Projecto autónomo, fora do *reactor* da raiz.
- `Ponto24CheckDigits` é uma cópia exacta da classe do backend (S5-07); o `Ponto24CheckDigitsTest` compara o código-fonte e corre os casos de `backend/src/test/resources/ponto24/reference-cases.csv`.
- Os ficheiros `src/test/resources/golden/` são os que os *writers* produzem com entradas fixas; os testes do worker também os lêem. Depois de uma mudança intencional: `./mvnw -B test -Dtest=FileWritersTest -Dgolden.update=true`.

## Configuração

| Variável | Defeito (compose) | Significado |
| --- | --- | --- |
| `PONTO24_MOCK_SOURCES` | `90002=MEPS@sftp://demo@sftp:22/inbound,90090=BMEPS:800:02@sftp://demob@sftp:22/inbound` | entidade → formato (no BMEPS com empresa e perfil) e destino SFTP (pasta relativa à pasta inicial do utilizador) |
| `PONTO24_MOCK_SFTP_PASSWORD_<entidade>` | `demo-sftp-pass` (`90002`), `demob-sftp-pass` (`90090`) | palavra-passe SFTP por entidade (nunca nos logs nem na API) |
| `PONTO24_MOCK_SETTLEMENT_CRON` | `0 * * * * *` | quando gera e envia os ficheiros pendentes (`-` desliga) |
| `PONTO24_MOCK_ZONE` | `Africa/Maputo` | fuso das datas no ficheiro |
| `PONTO24_MOCK_FILE_STYLE` | `EXAMPLE` | `EXAMPLE` reproduz os exemplos oficiais; `SPEC` segue a letra da especificação (MEPS: A003/A004 `50000000`/`00000100`, A006 `508`, A031 `00`, A035 com 10 dígitos e 2 espaços, A008 `0001` e A014 com um contador, A023 com 12 zeros; BMEPS: "ID da transacção" com um contador) |

Fora do compose (`./mvnw spring-boot:run`) os destinos por defeito são o SFTP do compose em `localhost:2222`; as palavras-passe têm de vir das variáveis `PONTO24_MOCK_SFTP_PASSWORD_90002` e `PONTO24_MOCK_SFTP_PASSWORD_90090`.

## API

| Método e caminho | Resposta |
| --- | --- |
| `POST /payments` `{ "entity", "reference", "amountCentavos", "paidAt"?, "channel"? }` | 201 `{ "id", "status": "PENDING_SETTLEMENT" }`; entidade sem destino → 404 `UNKNOWN_ENTITY`; referência sem 11 dígitos ou com controlo errado para a entidade e o montante → 422 `INVALID_REFERENCE`; montante ≤ 0 → 422 `INVALID_AMOUNT` |
| `POST /payments?force=true` | aceita sem validar os dígitos de controlo (para gerar `UNKNOWN_REFERENCE` e `AMOUNT_MISMATCH` no backend); a referência continua a precisar de 11 dígitos e o montante de ser > 0 (senão não cabe no ficheiro) |
| `GET /payments` | pagamentos, mais recentes primeiro, com o estado (`PENDING_SETTLEMENT`, `SETTLED` + `fileName`) |
| `POST /settlements/run[?entity=][&corrupt=TRAILER_COUNT\|TRAILER_TOTAL\|LINE_LENGTH\|ENTITY]` | `{ "files": [...], "errors"?: [...] }`: um ficheiro por entidade com pagamentos pendentes; com `corrupt`, o ficheiro leva o defeito, os pagamentos continuam pendentes e, sem pagamentos pendentes, envia um ficheiro vazio com o defeito |
| `POST /settlements/empty?entity=` | envia um ficheiro sem registos |
| `POST /settlements/resend?file=` | envia outra vez o mesmo conteúdo com outro nome (`….resend<n>.txt`) |
| `POST /settlements/official-example?format=MEPS\|BMEPS` | envia o exemplo oficial tal como está (MEPS para `90002`, BMEPS para `90090`) |
| `GET /settlements` | ficheiros enviados: nome, entidade, formato, `formatFileId`, registos, total, destino, data |
| `GET /sources` | entidades configuradas (sem palavras-passe) |
| `POST /__mock/reset` | apaga pagamentos, ficheiros e sequências |

## Ficheiros

- **MEPS:** `MEPS_<entidade>_<A005>.txt`; A005 = data de hoje + sequência do dia `1`–`9` por entidade (o 10.º → 409 `DAILY_SEQUENCE_EXHAUSTED`); A007 = A005 do último ficheiro sem defeito da entidade (zeros no primeiro); tarifa 10,00 por registo.
- **BMEPS:** `BMEPS_out<EEE><PP>508<AAMMDDhhmmss>`; ID do ficheiro sequencial por empresa e perfil a partir de `00001`; comissão 5,00 por registo.
- Linhas com `\r\n`. Envio por SFTP com o sufixo `.part` e depois *rename* (o worker ignora `*.part`); só depois de um envio sem erro os pagamentos passam a `SETTLED`. Falha no SFTP → ficam pendentes e as sequências não avançam; tenta-se no cron seguinte.
- Tudo em memória: um reinício perde o histórico e as sequências (o worker deduplica pelo conteúdo e pelo `formatFileId`; `docker compose down -v` limpa também o worker e o SFTP).
