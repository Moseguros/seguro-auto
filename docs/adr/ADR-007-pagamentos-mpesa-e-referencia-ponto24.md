# ADR-007 — Pagamentos só por M-Pesa e por entidade e referência (Ponto24), com o algoritmo e os ficheiros reais

- Estado: aceite
- Data: 2026-11-18
- Substitui: a secção "Pendente" do ADR-004 (mock `MOCK_SIMO_V1`) e o agendamento por `pollCron` do ADR-004

## Contexto

O plano do Sprint 5 previa quatro formas de pagamento (M-Pesa, e-Mola, cartão, entidade e referência) e, por falta do algoritmo de referências e do layout dos ficheiros bancários, um mock inventado (`MOCK_SIMO_V1`, simulador `simo-mock`) com duas tarefas bloqueadas (S5-13, S5-14).

Em 18/11/2026 o proprietário do produto decidiu:

1. Só há duas formas de pagamento: **M-Pesa** pela API oficial da Vodacom Moçambique (`developer.mpesa.vm.co.mz`), com credenciais por seguradora; e **entidade e referência** na rede Ponto24 da InterBancos (e-banking/app, ATM, `*124#`, POS, balcão).
2. O e-Mola e o cartão saem do âmbito (S5-05 cancelada).
3. O algoritmo da referência e os layouts dos ficheiros (MEPS e BMEPS) são conhecidos e estão em `docs/specs/interbancos/`. O mock deixa de ser necessário (S5-13 e S5-14 canceladas, absorvidas pelo S5-07 e S5-08).
4. Cada seguradora indica o seu servidor SFTP e as credenciais; a frequência dos ficheiros é combinada entre a seguradora e o banco; a plataforma fica sempre em escuta e nunca processa um ficheiro duas vezes.
5. Ao receber o pagamento, a proposta passa a `PAID` e a apólice é emitida automaticamente (S6-01); os actores da aquisição recebem um e-mail.

## Decisão

- `PaymentMethod { MPESA, BANK_REFERENCE }`. Não há callbacks de fornecedores nem páginas de pagamento externas.
- **Referência** (S5-07): `NúmeroFactura(7) + Mês(2) + CD(2)`, com o `CD` MOD 97 sobre `Entidade(5) + NúmeroFactura(7) + Mês(2) + Montante(centavos sem zeros à esquerda)` (algoritmo em `docs/specs/interbancos/README.md`). Número de factura sequencial **por entidade**, a partir de `0000001`, nunca reutilizado; mês da emissão em `Africa/Maputo`. Uma referência = um montante.
- **Ficheiros** (S5-08): leitores reais `MEPS` e `BMEPS` no `payment-file-worker`, tolerantes às diferenças documentadas entre os exemplos e a especificação. Formato, entidade (5 dígitos) e, no BMEPS, ID da empresa (3) e ID do perfil (2) configuráveis por seguradora (S5-01). Um simulador `ponto24-mock` (local, QA, testes) gera ficheiros nestes formatos e deposita-os no SFTP local.
- **Leitura contínua** (S5-09): o worker lê cada fonte activa com um intervalo curto global (`moseguros.worker.poll-interval`, defeito 60 s), em vez de um `pollCron` por fonte. ShedLock por fonte, como antes.
- **Nunca duas vezes** (S5-09): um ficheiro é identificado pelo SHA-256 do conteúdo (por fonte) **e** pelo identificador do ficheiro no formato (MEPS `A005`; BMEPS empresa + perfil + ID do ficheiro). Repetido por qualquer dos dois → não é processado. O padrão do nome é opcional (defeito `*`). Mover o ficheiro para `processedDir` é opcional (há SFTP de banco só de leitura): a deduplicação não depende disso.
- **Mensagens** (S5-06): a idempotência de cada registo passa a ser por ficheiro e linha (`sha256(sourceId|fileSha256|lineNo)`), porque os exemplos trazem os identificadores de transacção a zeros e o MEPS só tem precisão ao minuto (dois pagamentos iguais no mesmo minuto não podem ser confundidos).
- **Excepções** (S5-10): pagamento depois de a proposta expirar, referência paga duas vezes, referência desconhecida e montante diferente ficam numa fila da seguradora. A devolução do dinheiro é sempre feita **fora** da plataforma; a plataforma só regista a decisão (`AGENTS.md` §9 regra 10).
- **M-Pesa** (S5-01, S5-04): credenciais por seguradora (API Key, Public Key, Service Provider Code, Initiator Identifier e Security Credential para reversões, ambiente) no cofre; a BD guarda as referências. Todas as seguradoras usam o mesmo IP de origem da plataforma no registo da Vodacom: o alojamento tem de ter **IP público de saída fixo** (S6-08).

## Consequências

- Desaparecem o mock `MOCK_SIMO_V1`, o simulador `simo-mock`, a regra "proibido em `prod`" e o `BankReferenceReadinessCheck`; o pagamento por referência pode ser activado em produção.
- Menos código: sem adaptadores e-Mola e cartão, sem endpoint de callbacks, sem `nextAction` `REDIRECT`.
- O worker mantém a estrutura do ADR-004 (SFTP → parser → RabbitMQ → backend). Muda o agendamento e a deduplicação.
- Uma seguradora com mais de uma conta bancária (MEPS) ou mais de um perfil (BMEPS) recebe vários ficheiros; no S5 cada seguradora configura uma só fonte (ver "Questões" do S5-01).

## Alternativas consideradas

- **Manter o `pollCron` por fonte:** a frequência real é combinada com o banco e pode mudar sem aviso; ler de minuto a minuto custa pouco e reflecte o pagamento mais depressa.
- **`messageId` por identificador de transacção do banco:** os exemplos trazem-no a zeros; com a regra alternativa (entidade, referência, data, montante), dois pagamentos iguais no mesmo minuto num ficheiro MEPS seriam fundidos e um deles perdido.
