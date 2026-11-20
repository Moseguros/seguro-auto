# OWASP ASVS 4.0.3 nível 1 — Release 1

Revisão do S6-08 §6. Uma linha por requisito de **nível 1** dos capítulos V2, V3, V4, V5, V7, V8, V9, V12,
V13 e V14, tirados do CSV oficial da versão 4.0.3 (`OWASP/ASVS`, `4.0/docs_en/OWASP Application Security
Verification Standard 4.0.3-en.csv`, coluna L1): 118 linhas.

**Âmbito:** endpoints públicos (`PUBLIC_PATHS` do `SecurityConfig`: `/api/v1/auth/**`, `/api/v1/health/**`,
`/api/v1/meta/**`, listas de referência e formatos de matrícula, `/api/v1/catalog/**`,
`/.well-known/jwks.json`, `/v3/api-docs/**`, Swagger UI, `/api/v1/public/**`), *callbacks*
(`/api/v1/integrations/delivery-receipts/**`; não há *callbacks* de pagamento: o M-Pesa é síncrono com
consulta de estado e a referência chega por ficheiro, ADR-007), downloads (`/api/v1/files/**`), API interna
do worker (`/internal/v1/**`, `InternalSecurityConfig`) e a web (nginx). O inventário exacto dos endpoints
sem token está em `backend/src/test/resources/security/public-endpoints.txt` (`PublicEndpointsInventoryTest`).

**Controlos automáticos desta tarefa:** `PublicEndpointsInventoryTest`, `SecurityHeadersIT`,
`PrometheusEndpointIT`, cabeçalhos do nginx (`web/security-headers.inc`), `scripts/security-scan.sh` (ZAP
baseline da web e API scan do backend; alertas aceites em `security/zap/rules.tsv`) e
`.github/workflows/security.yml` (OSV-Scanner, excepções em `osv-scanner.toml`).

**Estados:** `OK`, `FALHA` (com questão em `docs/releases/R1.md` se não for corrigida neste PR), `N/A` (com
justificação), `BLOQUEADO` (com o motivo). Revisão manual: **agente (Claude), 19/11/2026, a validar pelo
revisor do PR (@edmilson-alar)**.

Achado corrigido neste PR: um método HTTP não suportado (ex.: `DELETE /api/v1/meta/version`) e um
`Content-Type` não aceite davam **500** `INTERNAL_ERROR`; passam a 405 `METHOD_NOT_ALLOWED` e 415
`UNSUPPORTED_MEDIA_TYPE` (`GlobalExceptionHandler`, `SecurityHeadersIT`).

## V2 — Autenticação

Clientes entram por código de uso único (OTP por SMS/WhatsApp/e-mail, S1-03); o staff por e-mail,
palavra-passe e TOTP (S1-05). Os requisitos de palavra-passe aplicam-se ao staff.

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 2.1.1 | Palavras-passe com pelo menos 12 caracteres | staff | `PasswordPolicy` (`MIN_LENGTH = 12`), testes do S1-04/S1-05 | OK | |
| 2.1.2 | Palavras-passe de 64 caracteres ou mais permitidas, até 128 | staff | `PasswordPolicy` (`MAX_LENGTH = 128`) | OK | |
| 2.1.3 | Sem truncar a palavra-passe | staff | revisão manual (agente): o Argon2id recebe o valor completo | OK | |
| 2.1.4 | Qualquer carácter Unicode imprimível, incluindo espaços e emojis | staff | `PasswordPolicy` conta *code points*, sem lista de caracteres | OK | |
| 2.1.5 | O utilizador pode mudar a palavra-passe | staff | reposição por e-mail (`/auth/password/forgot` e `/reset`, S1-05) | OK | Mudar com sessão iniciada: S12-06 |
| 2.1.6 | Mudar a palavra-passe pede a actual e a nova | staff | revisão manual (agente) | N/A | Não há mudança com sessão iniciada na R1; só a reposição por token de uso único (S12-06) |
| 2.1.7 | Palavras-passe verificadas contra uma lista de violadas | staff | `PasswordPolicy`: lista das 10 000 mais comuns (`identity/common-passwords.txt`) | OK | Lista local (sem chamada externa) |
| 2.1.8 | Medidor de força da palavra-passe | web `/convite/:token` | revisão manual (agente): a web mostra as regras e os motivos do `PASSWORD_TOO_WEAK` | FALHA | Sem medidor de força; questão Q-ASVS-1 em `docs/releases/R1.md` |
| 2.1.9 | Sem regras de composição (maiúsculas, símbolos…) | staff | `PasswordPolicy`: só comprimento, parte do e-mail e lista comum | OK | |
| 2.1.10 | Sem rotação periódica obrigatória nem histórico | staff | revisão manual (agente) | OK | |
| 2.1.11 | Colar, gestores de palavras-passe e preenchimento automático permitidos | web | revisão manual (agente): campos `type=password` com `autocomplete` | OK | |
| 2.1.12 | Ver a palavra-passe escrita (mostrar/esconder) | web | revisão manual (agente) | FALHA | Sem botão mostrar/esconder no login e na activação; Q-ASVS-1 |
| 2.2.1 | Controlos anti-automatização contra força bruta e *credential stuffing* | `/auth/**`, `/public/**` | staff: bloqueio de 15 min à 5.ª falha (S1-05); OTP: limites por identificador e reenvio de 60 s (S1-03); verificação: 60/min por IP e CAPTCHA (S6-03, `VerificationRateLimitIT`) | OK | Sem limite global por IP no login de staff: S12-06 |
| 2.2.2 | Autenticadores fracos (SMS/e-mail) só como secundários ou transacções | clientes | revisão manual (agente) | OK | O OTP é o fator de posse do cliente (telemóvel verificado); decisão de produto do S1-03 |
| 2.2.3 | Avisos seguros depois de mudanças de credenciais | staff | revisão manual (agente) | FALHA | Sem e-mail "a sua palavra-passe foi alterada"; Q-ASVS-2 |
| 2.3.1 | Palavras-passe iniciais/códigos de activação aleatórios e de curta duração | convites | convite com token aleatório válido 7 dias e de uso único (S1-04); OTP de 6 dígitos com expiração | OK | |
| 2.5.1 | Segredo de recuperação nunca enviado em claro | staff | o e-mail leva um link com token de uso único, nunca a palavra-passe (S1-05) | OK | |
| 2.5.2 | Sem perguntas de segurança | todos | revisão manual (agente) | OK | |
| 2.5.3 | A recuperação não revela a palavra-passe actual | staff | revisão manual (agente) | OK | |
| 2.5.4 | Sem contas partilhadas ou por defeito (admin/root) | todos | primeiro administrador por convite (`MOSEGUROS_BOOTSTRAP_PLATFORM_ADMIN_EMAIL`, S1-04), sem palavra-passe por defeito | OK | |
| 2.5.5 | Aviso ao utilizador quando um fator muda | staff | revisão manual (agente) | FALHA | Sem aviso de mudança de palavra-passe/TOTP; Q-ASVS-2 |
| 2.5.6 | Recuperação segura (token de uso único, TOTP…) | staff | reposição com token de uso único e TOTP mantido (S1-05) | OK | |
| 2.7.1 | SMS/telefone em claro só como fator secundário restrito | clientes | revisão manual (agente) | OK | Mesmo que 2.2.2; WhatsApp tem cifra ponta a ponta |
| 2.7.2 | Código fora de banda expira em 10 minutos | OTP | `otp_challenge.expires_at` (S1-03) | OK | |
| 2.7.3 | Código fora de banda de uso único e para o pedido original | OTP | desafio por `challengeId`, invalidado na verificação (S1-03); assinatura com hash do PDF (S4-04) | OK | |
| 2.7.4 | Canal secundário seguro | OTP | revisão manual (agente) | OK | Fornecedores reais de SMS/WhatsApp por escolher (S1-03); só mocks na R1 |
| 2.8.1 | Códigos TOTP com tempo de vida definido | staff | TOTP de 30 s com janela e sem repetir o passo (S1-05) | OK | |

## V3 — Gestão de sessões

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 3.1.1 | Tokens de sessão nunca no URL | API, web | revisão manual (agente): `Authorization: Bearer` e refresh no corpo; o ZAP não encontrou tokens em URL | OK | Os tokens de convite/reposição/verificação no caminho não são de sessão |
| 3.2.1 | Novo token de sessão na autenticação | API | cada login/verificação emite par novo (S1-02) | OK | |
| 3.2.2 | Tokens com pelo menos 64 bits de entropia | API | JWT RS256; refresh token aleatório (S1-02) | OK | |
| 3.2.3 | Tokens no browser guardados de forma segura | web | acesso só em memória; refresh em `localStorage`/`sessionStorage` (S1-07) | OK | CSP sem `unsafe-inline` em scripts reduz o risco de XSS ler o refresh |
| 3.3.1 | Terminar a sessão invalida o token | API | `POST /auth/logout` revoga o refresh; o acesso expira em 15 min | OK | O JWT de acesso continua válido até expirar (stateless, ADR-003) |
| 3.4.1 | Cookies com `Secure` | web | revisão manual (agente) | N/A | A sessão não usa cookies |
| 3.4.2 | Cookies com `HttpOnly` | web | idem | N/A | Sem cookies |
| 3.4.3 | Cookies com `SameSite` | web | idem | N/A | Sem cookies |
| 3.4.4 | Cookies com prefixo `__Host-` | web | idem | N/A | Sem cookies |
| 3.4.5 | Caminho do cookie restrito | web | idem | N/A | Sem cookies |
| 3.7.1 | Reautenticação antes de operações sensíveis | API | assinatura da proposta com OTP próprio (S4-04); pagamento com `Idempotency-Key` | OK | |

## V4 — Controlo de acessos

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 4.1.1 | Regras de acesso aplicadas no servidor | API | `@PreAuthorize` no serviço (AGENTS.md §6); `PublicEndpointsInventoryTest` lista o que é anónimo | OK | |
| 4.1.2 | Atributos de autorização não manipuláveis pelo utilizador | API | permissões e âmbito do JWT assinado (`TenantContext`), RLS (ADR-002) | OK | |
| 4.1.3 | Menor privilégio | API, BD | utilizador da BD sem privilégios de dono; permissões por perfil; worker só no schema `payment_worker` | OK | |
| 4.1.5 | Falha segura quando há excepção | API | `anyRequest().authenticated()`; erros RFC 7807 sem dados (`SecurityHeadersIT`) | OK | |
| 4.2.1 | Protecção contra IDOR | API | RLS por cliente/seguradora e testes de isolamento entre seguradoras em cada endpoint (AGENTS.md §7); downloads pela `FileAccessPolicy` (404) | OK | |
| 4.2.2 | Protecção CSRF | API | sem cookies de sessão: `Authorization` explícito; CSRF desligado de propósito | OK | |
| 4.3.1 | Interfaces de administração com MFA | staff | login de staff exige TOTP (S1-05) | OK | |
| 4.3.2 | Sem listagem de directórios nem ficheiros de metadados expostos | nginx, API | ZAP baseline (sem alertas de listagem); `try_files` sem `autoindex`; `/internal/` e `/api/v1/prometheus` com 404 no nginx | OK | O Swagger UI está desligado em `prod` |

## V5 — Validação, sanitização e codificação

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 5.1.1 | Defesa contra poluição de parâmetros HTTP | API | Spring MVC com DTOs; parâmetros repetidos só onde são listas (`status`) | OK | |
| 5.1.2 | Protecção contra atribuição em massa | API | DTOs *records* por pedido, sem entidades na API (AGENTS.md §3) | OK | |
| 5.1.3 | Validação de todas as entradas por lista permitida | API | Bean Validation e value objects (`Msisdn`, `Matricula`, `Nuit`…) → `VALIDATION_ERROR` | OK | |
| 5.1.4 | Dados estruturados com tipo e formato validados | API | JSON Schema (`rating-v1`, `underwriting-v1`), enums e formatos no contrato | OK | |
| 5.1.5 | Redirecionamentos só para destinos permitidos | web | `?voltar=` só aceita caminhos internos `/cliente/…` ou da área (S1-07, S3-04) | OK | |
| 5.2.1 | HTML de editores WYSIWYG sanitizado | web | revisão manual (agente) | N/A | Sem HTML introduzido pelo utilizador; os modelos de notificação são texto com variáveis (S6-04) |
| 5.2.2 | Dados não estruturados sanitizados | API | campos livres com tamanho máximo e escapados na saída (React) | OK | |
| 5.2.3 | Entrada sanitizada antes de ir para e-mail (injecção SMTP) | notificações | revisão manual (agente): destinatários validados (`Email`), assuntos dos modelos | OK | |
| 5.2.4 | Sem `eval()` nem código dinâmico | web, API | revisão manual (agente); CSP sem `unsafe-eval` | OK | |
| 5.2.5 | Protecção contra injecção de modelos | notificações, PDF | revisão manual (agente): `MessageFormat`/Thymeleaf com textos do servidor; parâmetros escapados | OK | |
| 5.2.6 | Protecção contra SSRF | tarifação externa | URL externo só `https://` (S3-02, `allow-http` só local/test) e configurado por administrador | OK | |
| 5.2.7 | SVG enviado pelo utilizador sanitizado | ficheiros | uploads só JPEG/PNG/HEIC/PDF (S2-01, S4-01) | N/A | SVG não é aceite |
| 5.2.8 | Linguagens de *template* do utilizador sanitizadas | — | revisão manual (agente) | N/A | O utilizador não escreve modelos (só o administrador, com variáveis fixas) |
| 5.3.1 | Codificação da saída para o contexto | web | React escapa por defeito; sem `dangerouslySetInnerHTML` com dados do utilizador | OK | |
| 5.3.2 | Codificação preserva o conjunto de caracteres | API | UTF-8 em toda a API e nos PDF | OK | |
| 5.3.3 | Protecção contra XSS reflectido, armazenado e DOM | web | React + CSP (`script-src 'self'`); ZAP baseline sem alertas de XSS | OK | |
| 5.3.4 | Consultas parametrizadas (SQL injection) | BD | JPA/JdbcTemplate com parâmetros; ZAP API scan | OK | |
| 5.3.5 | Protecção contra outras injecções (LDAP, OS…) | — | revisão manual (agente) | N/A | Sem LDAP nem comandos do sistema |
| 5.3.6 | Protecção contra injecção em JSON/JavaScript | API | Jackson serializa; sem JSONP | OK | |
| 5.3.7 | Protecção contra injecção LDAP | — | revisão manual (agente) | N/A | Sem LDAP |
| 5.3.8 | Protecção contra injecção de comandos do SO | — | revisão manual (agente) | N/A | O backend não chama comandos do sistema |
| 5.3.9 | Protecção contra LFI/RFI | ficheiros | `FileStorage` com chaves geradas pelo servidor, nunca caminhos do utilizador (ADR-005) | OK | |
| 5.3.10 | Protecção contra injecção XPath/XML | ficheiros MEPS/BMEPS | layouts de largura fixa (S5-08), sem XML do utilizador | N/A | O XHTML dos PDF é gerado pelo servidor |
| 5.5.1 | Objectos serializados com integridade/cifra | API | revisão manual (agente) | N/A | Sem serialização nativa Java; só JSON |
| 5.5.2 | XML sem entidades externas (XXE) | — | revisão manual (agente) | N/A | Não se lê XML do utilizador |
| 5.5.3 | Desserialização de dados não confiáveis evitada | API | Jackson sem *default typing* | OK | |
| 5.5.4 | JSON no browser lido com `JSON.parse` | web | revisão manual (agente) | OK | |

## V7 — Erros e registos

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 7.1.1 | Sem credenciais nem dados de pagamento nos registos | logs | `SensitiveDataMasker`, regra do AGENTS.md §6; `SecretLeakIT` (S5) | OK | |
| 7.1.2 | Sem outros dados sensíveis nos registos | logs | telefone, documentos, códigos OTP e tokens mascarados (`SensitiveDataMasker`); código de verificação nunca em logs (S6-01) | OK | |
| 7.4.1 | Mensagem de erro genérica, com id para o suporte | API | RFC 7807 com `code` e `instance`, sem *stack traces* (`SecurityHeadersIT`); 405/415 deixaram de dar 500 | OK | Corrigido neste PR |

## V8 — Protecção de dados

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 8.2.1 | Cabeçalhos anti-cache nas respostas sensíveis | API | `Cache-Control: no-store` em `/me/**`, `/public/**`, `/files/**`, `/auth/**` (`SecurityHeadersIT`) | OK | |
| 8.2.2 | Dados sensíveis não guardados no browser (excepto tokens) | web | só a máscara do n.º da carta na cache (S2-06); rascunhos sem documentos | OK | O rascunho da cotação fica em `sessionStorage` (sem dados pessoais sensíveis) |
| 8.2.3 | Dados sensíveis apagados do browser ao sair | web | logout apaga tokens e caches do TanStack Query (S1-07) | OK | |
| 8.3.1 | Dados sensíveis no corpo ou cabeçalhos, nunca no URL | API | OTP, palavras-passe e tokens no corpo; a matrícula da verificação vai em `?plate=` (dado público, S6-03) | OK | O telefone do mediador vai em `?clientMsisdn=` (área autenticada, S3-02) |
| 8.3.2 | O utilizador pode exportar/apagar os seus dados | clientes | revisão manual (agente) | BLOQUEADO | Exportação e apagamento de dados pessoais dependem da validação jurídica (S12) |
| 8.3.3 | Informação clara sobre a recolha e o uso dos dados | web, app | revisão manual (agente) | BLOQUEADO | Política de privacidade e termos por validar juridicamente (S12-10) |
| 8.3.4 | Dados sensíveis identificados e com política | todos | documentos e NUIT de pessoas singulares cifrados (AES-GCM, `CryptoService`), carta com índice cego (S2-03), RLS | OK | |

## V9 — Comunicações

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 9.1.1 | TLS em todas as ligações do cliente, sem cair para HTTP | staging | `infra/staging/README.md` (TLS ≥ 1.2, HSTS, redirecção) | BLOQUEADO | Alojamento por decidir; o compose local é HTTP |
| 9.1.2 | Só suites de cifra fortes | staging | idem | BLOQUEADO | Alojamento por decidir |
| 9.1.3 | Só TLS 1.2 e 1.3 | staging | idem | BLOQUEADO | Alojamento por decidir |

## V12 — Ficheiros e recursos

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 12.1.1 | Ficheiros grandes recusados (limite) | API, nginx | 10 MB por ficheiro, 11 MB por pedido (Spring), 12 MB no nginx (S2-01) | OK | |
| 12.3.1 | Nome do ficheiro do utilizador não usado em caminhos | ficheiros | chaves geradas pelo `FileStorage` (ADR-005) | OK | |
| 12.3.2 | Metadados do ficheiro validados (sem *path traversal*) | ficheiros | nome original só guardado como metadado e sanitizado no `Content-Disposition` | OK | |
| 12.3.3 | Sem RFI/SSRF por nomes de ficheiros | ficheiros | idem | OK | |
| 12.3.4 | Sem RFD (nome e tipo controlados no download) | `/files/**` | `Content-Type` gravado no envio e `nosniff`; `Content-Disposition` do servidor | OK | |
| 12.3.5 | Nomes de ficheiros não chegam a comandos do SO | ficheiros | revisão manual (agente) | OK | Sem comandos do sistema |
| 12.4.1 | Ficheiros guardados fora da raiz da web | ficheiros | disco do backend ou S3 (ADR-005), nunca no nginx | OK | |
| 12.4.2 | Ficheiros verificados por antivírus | ficheiros | revisão manual (agente) | FALHA | Sem antivírus nos envios (KYC, vistoria, livrete); Q-ASVS-3 |
| 12.5.1 | O servidor só serve extensões permitidas | nginx | só o `dist/` da web; `/api/` pelo proxy | OK | |
| 12.5.2 | Ficheiros enviados não são executados como HTML/JS | `/files/**` | `nosniff`, tipos permitidos e `Content-Disposition` | OK | |
| 12.6.1 | Lista permitida de destinos de pedidos do servidor (SSRF) | backend | destinos fixos por configuração (M-Pesa por seguradora, tarifação externa `https://` configurada pelo administrador, S3/Vault/SMTP) | OK | |

## V13 — API e serviços web

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 13.1.1 | Mesma codificação e *parsers* em todos os componentes | API | JSON UTF-8 em todo o lado; contrato OpenAPI gerado (S0-04) | OK | |
| 13.1.3 | Sem dados sensíveis nos URL da API (chaves, tokens) | API | revisão manual (agente); `PublicEndpointsInventoryTest` | OK | Os códigos de verificação no caminho são públicos por natureza (QR) |
| 13.2.1 | Métodos HTTP permitidos explícitos | API | mapeamentos por método; outros → 405 (`SecurityHeadersIT`) | OK | Corrigido neste PR (antes 500) |
| 13.2.2 | Validação de esquema JSON nos pedidos | API | DTOs com Bean Validation; `JsonSchemas` nas regras (S3-01) | OK | |
| 13.2.3 | Protecção CSRF nos serviços que usam cookies | API | sem cookies de sessão | N/A | Autenticação por `Authorization` |
| 13.3.1 | Validação de XSD/SOAP | — | revisão manual (agente) | N/A | Sem SOAP |

## V14 — Configuração

| ID | Requisito (resumo) | Onde | Como se verifica | Estado | Notas |
| --- | --- | --- | --- | --- | --- |
| 14.2.1 | Componentes actualizados, sem vulnerabilidades conhecidas | dependências | `.github/workflows/security.yml` (OSV-Scanner; HIGH/CRITICAL falham) | OK | Excepções em `osv-scanner.toml` (nenhuma na R1) |
| 14.2.2 | Funcionalidades, documentação e amostras desnecessárias removidas | API, imagens | Swagger UI desligado em `prod`; imagens de execução JRE/nginx sem ferramentas de build; *mocks* não publicados (`release.yml`) | OK | |
| 14.2.3 | Recursos externos com SRI | web | revisão manual (agente) | OK | Só a folha do Google Fonts (dinâmica, sem SRI possível; regra 90003 aceite) e o script do Turnstile (versão sem SRI publicado); ambos limitados pela CSP |
| 14.3.2 | Modo de depuração desligado em produção | API | `X-Debug-Otp` só no perfil `local` (não arranca com `prod`); `show-details: never` no health | OK | |
| 14.3.3 | Cabeçalhos sem versões de componentes | nginx, API | `server_tokens off`; ZAP baseline sem alertas de divulgação de versão | OK | |
| 14.4.1 | `Content-Type` com charset em todas as respostas | API, nginx | JSON e `problem+json` com UTF-8; nginx com tipos por extensão | OK | |
| 14.4.2 | Respostas da API com `Content-Disposition: attachment` quando adequado | `/files/**` | downloads com `Content-Disposition` | OK | |
| 14.4.3 | Content-Security-Policy | web | `web/security-headers.inc` (`script-src 'self' https://challenges.cloudflare.com`, `frame-ancestors 'none'`) | OK | `style-src 'unsafe-inline'` exigido pelo MUI/Emotion (regra 10055 aceite) |
| 14.4.4 | `X-Content-Type-Options: nosniff` | web, API | nginx e Spring Security (`SecurityHeadersIT`) | OK | |
| 14.4.5 | HSTS | staging | `infra/staging/README.md` (`max-age=31536000` no terminador TLS) | BLOQUEADO | Alojamento por decidir; o compose é HTTP |
| 14.4.6 | `Referrer-Policy` adequada | web | `strict-origin-when-cross-origin` no nginx | OK | |
| 14.4.7 | Conteúdo não pode ser incorporado por terceiros | web, API | `X-Frame-Options: DENY` e `frame-ancestors 'none'` | OK | |
| 14.5.1 | Só os métodos HTTP necessários | API | 405 para os outros (`SecurityHeadersIT`) | OK | |
| 14.5.2 | `Origin` não usado para autenticação | API | revisão manual (agente): sem CORS com credenciais; a web é da mesma origem | OK | |
| 14.5.3 | CORS com lista restrita de origens | API | revisão manual (agente): sem configuração CORS (a web e a API partilham a origem pelo nginx) | OK | A app móvel não usa CORS |

## Resumo

| Estado | Linhas |
| --- | --- |
| OK | 90 |
| N/A | 17 (com justificação) |
| FALHA | 5 (2.1.8, 2.1.12, 2.2.3, 2.5.5, 12.4.2): questões Q-ASVS-1 a Q-ASVS-3 com prazo em `docs/releases/R1.md` |
| BLOQUEADO | 6 (8.3.2, 8.3.3, 9.1.1, 9.1.2, 9.1.3, 14.4.5) |

Total: 118 linhas, todas com estado (contagem conferida com o CSV oficial: V2 27, V3 11, V4 8, V5 27, V7 3,
V8 7, V9 3, V12 11, V13 6, V14 15).
