# ADR-001 — Monólito modular com Spring Modulith

- Estado: aceite
- Data: 2026-10-31

## Contexto

A plataforma tem cerca de 18 áreas de negócio (identidade, catálogo, tarifação, propostas, pagamentos, apólices, sinistros, reclamações…), é desenvolvida por uma equipa humana mínima com agentes de IA e precisa de chegar a um piloto em cerca de 26 semanas. O volume esperado no piloto é baixo (centenas a poucos milhares de apólices).

## Decisão

O backend é uma única aplicação Spring Boot (`backend/`), organizada em módulos Spring Modulith, um por área de negócio, no pacote `mz.co.moseguros.<modulo>`.

- Cada módulo expõe uma única classe pública `<Modulo>Api` e eventos de domínio; o resto é interno.
- Comunicação entre módulos: chamada síncrona à `<Modulo>Api` ou evento de domínio publicado com outbox persistente (Spring Modulith event publication registry).
- Cada módulo tem o seu schema PostgreSQL.
- `ApplicationModules.verify()` corre nos testes e falha o build se um módulo aceder ao interior de outro.
- A única excepção ao deployable único é o `payment-file-worker` (ver ADR-004).

## Consequências

- Um só deploy, uma só base de dados e transacções locais simples.
- Fronteiras claras desde o início, verificadas automaticamente; um módulo pode ser extraído para um serviço mais tarde se o volume o justificar.
- Escalar uma parte obriga a escalar a aplicação toda; aceitável até depois do piloto.

## Alternativas consideradas

- Microserviços desde o início: custo de operação, contratos e testes desproporcionado para a equipa e o volume.
- Monólito sem módulos: rápido no início, mas as fronteiras degradam-se, sobretudo com código gerado por vários agentes.
