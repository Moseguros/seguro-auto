# ADR-004 — Worker de ficheiros de pagamento separado, ligado por RabbitMQ

- Estado: aceite
- Data: 2026-10-31

## Contexto

Os pagamentos por entidade e referência (rede SIMO) são confirmados por ficheiros que cada seguradora recebe num servidor SFTP próprio. A plataforma tem de, para cada seguradora e com uma frequência parametrizada, ler esses ficheiros, validá-los e confirmar os pagamentos pela referência do cliente.

## Decisão

- Um executável à parte, `payment-file-worker` (Spring Boot, Java 17), faz todo o trabalho de SFTP e de leitura de ficheiros:
  - lê as fontes activas da aplicação principal (`GET /internal/v1/payment-file-sources`) e os segredos SFTP do cofre;
  - agenda cada fonte com o seu `pollCron`, com ShedLock para que só uma instância processe cada fonte;
  - garante idempotência por SHA-256 do ficheiro e guarda o registo dos ficheiros e linhas no seu próprio schema;
  - publica cada pagamento como mensagem `ReferencePaymentReceived` no RabbitMQ, com *publisher confirms*, antes de mover o ficheiro para `processedDir`.
- A aplicação principal consome a fila com confirmação manual, idempotência por `messageId`, e confirma o pagamento ou cria uma excepção para o financeiro.
- As mensagens estão definidas no módulo partilhado `payment-contracts`.
- **RabbitMQ** é o broker: exchange `payments.events` (topic), filas *quorum* com dead-letter.

## Consequências

- Credenciais SFTP e processamento de ficheiros ficam isolados da API pública; falhas ou ficheiros grandes não afectam a API.
- O worker pode ser implantado, reiniciado e escalado sozinho.
- Mais uma peça a operar (worker + RabbitMQ) e um contrato de mensagens a versionar.

## Alternativas consideradas

- Job agendado dentro do backend: mais simples, mas mistura credenciais SFTP e processamento pesado com a API, e um erro no parser pode afectar a aplicação.
- Kafka: pensado para grandes volumes e reprocessamento de fluxos; aqui o volume é de milhares de registos por dia e o que importa é a confirmação por mensagem, as filas de mensagens falhadas e a simplicidade de operação.

## Pendente

O algoritmo SIMO de geração de referências e o layout do ficheiro de pagamentos têm de ser fornecidos pelo proprietário do produto (tarefas S5-13 e S5-14, bloqueadas). Decisão de 02/11/2026: até lá, usa-se o mock `MOCK_SIMO_V1` (gerador com MOD 97, ficheiro de largura fixa) e o simulador `simo-mock`, que entrega os ficheiros por SFTP como faria o banco (tarefas S5-07 e S5-08). O mock é permitido em `local`, `test` e `staging` e proibido em `prod`.

## Adenda de 18/11/2026 — formatos reais e leitura contínua

O "Pendente" acima está resolvido e substituído pelo **ADR-007**: o algoritmo da referência e os layouts dos ficheiros (MEPS e BMEPS da InterBancos, rede Ponto24) estão em `docs/specs/interbancos/`; o mock `MOCK_SIMO_V1` e o `simo-mock` deixam de existir. O worker deixa de agendar cada fonte com um `pollCron` próprio: lê todas as fontes activas continuamente, com um intervalo curto global, e deduplica cada ficheiro pelo SHA-256 e pelo identificador do ficheiro no formato. O resto deste ADR mantém-se.

## Adenda de 02/11/2026 — versão do RabbitMQ

A versão passa de 3.13 para **4.1** (imagem `rabbitmq:4.1-management`), porque a 3.13 deixou de ter suporte da comunidade. As filas continuam *quorum*, com dead-letter. As ligações do exchange `payments.events` são explícitas por routing key (a ficha S5-06 tem o detalhe).

## Adenda de 18/11/2026 — topologia

Detalhe na ficha S5-06 §3 e em `PaymentsTopology` (`payment-contracts`).

- **Ligações explícitas.** O exchange `payments.events` (`topic`) liga `reference.paid.*` à fila `core.reference-payments` e `reference.file-processed.*`, `reference.file-rejected.*` e `reference.source-polled.*` à fila `core.payment-file-events`. O plano previa uma só ligação `reference.file-*`, mas num exchange `topic` o `*` substitui uma palavra inteira entre pontos: `file-*` seria lido à letra e não encaminharia nada. As chaves terminam no `insurerId`. As filas são *quorum* com `x-delivery-limit` 20 e *dead-letter* para `payments.dlx` (`direct`), que as entrega às filas `.dlq` correspondentes. O backend declara tudo; o worker declara só o exchange `payments.events` e publica com `mandatory = true`.
- **Mensagem nova `PaymentSourcePolled`.** Cada leitura de uma fonte publica o resultado (`OK`/`ERROR`, código do erro, ficheiros encontrados, processados e rejeitados), para a "Última leitura", o alerta de fonte parada e o ecrã de operações (S5-10, S5-12). Junta-se a `ReferencePaymentReceived`, `PaymentFileProcessed` e `PaymentFileRejected`; todas têm JSON Schema (`payment-contracts/src/main/resources/schemas/*.v1.json`).
- **Reactor Maven na raiz.** O `pom.xml` da raiz agrega `payment-contracts` e `backend` (o S5-09 acrescenta `payment-file-worker`), para as duas aplicações usarem os contratos sem publicar artefactos: `./mvnw -B -pl backend -am verify` na raiz. `cd backend && ./mvnw -B verify` continua a funcionar depois de `./mvnw -B -pl payment-contracts install` (decisão D18). A imagem do backend constrói-se com o contexto na raiz.
