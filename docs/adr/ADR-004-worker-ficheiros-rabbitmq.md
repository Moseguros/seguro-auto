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

## Adenda de 02/11/2026 — versão do RabbitMQ

A versão passa de 3.13 para **4.1** (imagem `rabbitmq:4.1-management`), porque a 3.13 deixou de ter suporte da comunidade. As filas continuam *quorum*, com dead-letter. As ligações do exchange `payments.events` são explícitas por routing key (a ficha S5-06 tem o detalhe).
