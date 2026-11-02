# ADR-005 — Armazenamento de ficheiros atrás de uma porta, com disco local e S3

- Estado: aceite
- Data: 2026-11-02

## Contexto

A partir do Sprint 2 a plataforma guarda ficheiros: fotos do livrete, documentos de produtos, KYC, vistorias, certificados, fotos de sinistros. Muitos têm dados pessoais. A imagem `minio/minio` deixou de ser publicada no Docker Hub, e o alojamento de produção ainda não está decidido.

## Decisão

- Módulo `document` com a porta `FileStorage` (`put`, `get`, `exists`, `delete` — este só para a limpeza de ficheiros órfãos) e duas implementações:
  - `FilesystemFileStorage`: perfis `local` e `test`, e o docker-compose, com o volume `files`;
  - `S3FileStorage` (AWS SDK v2): staging e produção, com qualquer serviço compatível com S3, escolhido com o alojamento.
- Metadados em `document.file`: dono (`owner_type`, `owner_id`), seguradora, tipo de conteúdo, tamanho, SHA-256, chave de armazenamento, classificação (`PUBLIC`, `PERSONAL`, `SENSITIVE`).
- **Quem pode ler** é decidido pelo módulo dono do ficheiro, através da interface `FileAccessPolicy`, porque o `document` não conhece as regras de negócio. Sem política que autorize → 404.
- **Descarga** sempre pela API (`GET /api/v1/files/{id}`), nunca por URL directo do armazenamento, para a autorização e a auditoria de leituras sensíveis (Sprint 11) passarem sempre pelo backend.
- **Limites**: 10 MB por ficheiro; tipos `image/jpeg`, `image/png`, `image/heic` e `application/pdf`, confirmados pelo conteúdo e não só pela extensão.
- **Ficheiros cifrados?** Não na aplicação: a cifra é a do armazenamento (cifra do lado do servidor no S3, disco cifrado no servidor). Revê-se se o alojamento o exigir.

## Consequências

- Desenvolvimento e testes sem nenhum serviço extra.
- A mudança de fornecedor de S3 é só de configuração.
- O tráfego de descarga passa pelo backend. Aceitável para os volumes previstos; revê-se no Sprint 12 com os testes de carga.
