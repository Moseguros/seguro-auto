# MoSeguros

Plataforma de seguro automóvel para Moçambique: os motoristas cotam, compram, pagam e gerem as suas apólices, participam sinistros e reclamam. Seguradoras, mediadores, peritos, oficinas, prestadores de assistência, provedores do cliente, PRM e ASFPM gerem a sua parte de cada processo.

## Aplicações

| Pasta | O quê | Tecnologia |
| --- | --- | --- |
| `backend/` | API REST principal (monólito modular) | Java 17, Spring Boot 3.3, PostgreSQL 16 |
| `payment-file-worker/` | Leitura de ficheiros de pagamento por SFTP e publicação no RabbitMQ | Java 17, Spring Boot |
| `payment-contracts/` | Mensagens partilhadas entre o worker e o backend | Java 17 |
| `web/` | Aplicação web para todos os actores, incluindo o cliente final | React 18, TypeScript, Vite |
| `mobile/` | Aplicação móvel para clientes finais | Flutter |
| `infra/` | docker-compose, Dockerfiles, simuladores | Docker |
| `docs/` | ADRs e fichas de tarefas | Markdown |

As pastas são criadas ao longo do Sprint 0.

## Como contribuir

- Leia o [`AGENTS.md`](AGENTS.md): as convenções valem para pessoas e agentes de IA.
- Nada entra no `main` sem pull request aprovado.
- Cada tarefa tem uma ficha em `docs/tasks/`.

## Documentos de referência

- Levantamento de requisitos: Seguro Automóvel em Moçambique
- Plano de Implementação por Sprints
