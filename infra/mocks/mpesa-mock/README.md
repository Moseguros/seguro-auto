# mpesa-mock — simulador da API M-Pesa (Vodacom Moçambique)

Simulador local das operações C2B, consulta de estado e reversão da API M-Pesa, com os mesmos caminhos, portas, campos e códigos (ficha `docs/tasks/S5-03.md`). Alvo do adaptador M-Pesa do backend (S5-04) em desenvolvimento e testes. **Não movimenta dinheiro e não aceita credenciais reais.**

## Correr

```bash
./mvnw -B verify                         # testes
./mvnw spring-boot:run                   # fora do Docker
cd ../.. && docker compose up -d --build mpesa-mock   # no compose (a partir de infra/)
```

| Operação | Método | Porta | Caminho |
| --- | --- | --- | --- |
| C2B | `POST` | 18352 | `/ipg/v1x/c2bPayment/singleStage/` |
| Consulta de estado | `GET` | 18353 | `/ipg/v1x/queryTransactionStatus/` |
| Reversão | `PUT` | 18354 | `/ipg/v1x/reversal/` |
| Apoio (só do simulador) | vários | 18352 | `/__mock/**`, página em `/` |
| Health | `GET` | 18350 | `/actuator/health` |

Cada caminho só responde na sua porta (noutra → 404); a barra final é opcional.

## Autenticação

`Authorization: Bearer <token>`, em que o token é o Base64 de `RSA/ECB/PKCS1Padding(apiKey)` cifrado com a chave pública do simulador (`src/main/resources/keys/mock-public.pem` ou `mock-public.b64`), e `Origin: developer.mpesa.vm.co.mz`. Token inválido → INS-2; `Origin` errado ou ausente → INS-26; `input_ServiceProviderCode` diferente de `171717` → INS-13.

Gerar um token à mão (Git Bash ou Linux):

```bash
printf '%s' dev-mpesa-api-key \
  | openssl pkeyutl -encrypt -pubin -inkey src/main/resources/keys/mock-public.pem -pkeyopt rsa_padding_mode:pkcs1 \
  | base64 -w0
```

Exemplo de C2B:

```bash
curl -s -X POST localhost:18352/ipg/v1x/c2bPayment/singleStage/ \
  -H 'Content-Type: application/json' -H "Authorization: Bearer $TOKEN" -H 'Origin: developer.mpesa.vm.co.mz' \
  -d '{"input_TransactionReference":"T12344C","input_CustomerMSISDN":"258841234567","input_Amount":"10",
       "input_ThirdPartyReference":"ABC123","input_ServiceProviderCode":"171717"}'
```

## Números de teste (três últimos dígitos de `input_CustomerMSISDN`)

| Final | Resposta do C2B | Estado registado |
| --- | --- | --- |
| `000` | INS-0 depois de `MOCK_DELAY_000_SECONDS` (5 s) | `Pending` → `Completed` |
| `001` | INS-5 (cancelado pelo cliente) depois de 2 s | `Cancelled` |
| `002` | INS-2006 (saldo insuficiente) depois de 2 s | `Failed` |
| `003` | INS-0 só depois de `MOCK_SLOW_DELAY_SECONDS` (120 s; força o timeout do cliente) | `Pending` → `Completed` |
| `004` | INS-10 imediatamente | `Cancelled` |
| `005` | INS-6 (transacção falhou) depois de 2 s | `Failed` |
| outro | INS-0 depois de `MOCK_DEFAULT_DELAY_SECONDS` (2 s) | `Pending` → `Completed` |

Só números `25884…` e `25885…` (`^258(84|85)[0-9]{7}$`); outros → INS-21. Uma `input_ThirdPartyReference` já usada num C2B → INS-10.

## Configuração

| Variável | Defeito |
| --- | --- |
| `MOCK_API_KEY` | `dev-mpesa-api-key` |
| `MOCK_ALLOWED_ORIGIN` | `developer.mpesa.vm.co.mz` |
| `MOCK_SERVICE_PROVIDER_CODE` | `171717` |
| `MOCK_INITIATOR_IDENTIFIER` | `dev-initiator` |
| `MOCK_SECURITY_CREDENTIAL` | `dev-security-credential` |
| `MOCK_DEFAULT_DELAY_SECONDS` | `2` |
| `MOCK_DELAY_000_SECONDS` | `5` |
| `MOCK_SLOW_DELAY_SECONDS` | `120` |
| `MOCK_FAILURE_DELAY_SECONDS` | `2` (finais `001`, `002` e `005`; extensão do simulador) |
| `MOCK_C2B_PORT`, `MOCK_QUERY_PORT`, `MOCK_REVERSAL_PORT`, `MOCK_MANAGEMENT_PORT` | `18352`, `18353`, `18354`, `18350` |

Endpoints de apoio (porta 18352, sem autenticação):

```bash
curl -s localhost:18352/__mock/transactions      # mais recente primeiro, últimas 500
curl -s -X POST localhost:18352/__mock/reset     # limpa transacções e ThirdPartyReference usadas
curl -s localhost:18352/__mock/config
curl -s -X PUT localhost:18352/__mock/config -H 'Content-Type: application/json' \
  -d '{"defaultDelaySeconds":0,"delay000Seconds":1,"slowDelaySeconds":3,"failureDelaySeconds":0}'
```

Os campos omitidos no `PUT /__mock/config` ficam como estavam. As transacções ficam só em memória.

## Chaves

**Chave só para o simulador. Nunca usar fora do desenvolvimento.** (Vale para os três ficheiros de `src/main/resources/keys/`; o `mock-public.b64` não tem comentário porque é o formato de uma linha em que a Vodacom entrega a chave.)

O par foi gerado uma vez assim (o comentário da primeira linha dos `.pem` foi acrescentado à mão; os leitores de PEM ignoram o texto fora das linhas `BEGIN`/`END`):

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out mock-private.pem      # PKCS#8 PEM
openssl pkey -in mock-private.pem -pubout -out mock-public.pem                          # pública, PEM
openssl pkey -in mock-private.pem -pubout -outform DER | base64 -w0 > mock-public.b64    # pública, Base64 DER
```

`backend/src/main/resources/dev/mpesa-mock-public.pem` é uma cópia exacta de `mock-public.pem` (verificada pelo `MpesaMockKeyConsistencyTest` do backend): se o par mudar, os dois mudam no mesmo PR.

## Registos

Cada pedido regista a operação, o código de resposta, as referências e o telemóvel mascarado (`258*****4567`). Nunca o token nem a chave decifrada.
