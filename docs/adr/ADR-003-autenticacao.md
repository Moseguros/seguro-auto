# ADR-003 — Autenticação própria com OTP multicanal e JWT

- Estado: aceite
- Data: 2026-10-31

## Contexto

Os clientes finais entram com o número de telemóvel ou e-mail, muitos sem palavra-passe habitual, e o canal de OTP (SMS, WhatsApp ou e-mail) tem de ser configurável no painel de administração. O staff das organizações (seguradoras, mediadores, peritos, oficinas…) precisa de autenticação forte. A plataforma tem de funcionar em app, web e USSD.

## Decisão

- Módulo `identity` próprio, sobre Spring Security, sem fornecedor de identidade externo nesta fase.
- **Clientes:** OTP enviado pelo canal configurado em `identity.otp_settings` (canais activos, canal por defeito, escolha pelo cliente, ordem de alternativa). Telemóvel → SMS ou WhatsApp; e-mail → e-mail. Código com hash, validade e tentativas configuráveis.
- **Staff:** e-mail + palavra-passe (Argon2id) + TOTP obrigatório.
- **Tokens:** JWT de acesso assinado com RS256, validade de 15 minutos, com `sub`, `type`, `orgId`, `orgType`, `roles`, `permissions`; refresh token opaco, guardado com hash e rotativo em cada uso.
- O backend é também *resource server* que valida os seus próprios JWT; as chaves públicas ficam em `/.well-known/jwks.json`.
- Canais de entrega atrás da interface `OtpDeliveryChannel`, reutilizados pelas notificações.

## Consequências

- Controlo total do fluxo de OTP, incluindo alternativa automática de canal e regras locais.
- A plataforma é responsável pela segurança do módulo: limites de pedidos, bloqueios, rotação de chaves e auditoria têm de ser implementados e testados.
- Possível migração futura para um fornecedor de identidade (ex.: Keycloak) sem mudar os clientes, mantendo o formato dos JWT.

## Alternativas consideradas

- Keycloak desde o início: mais uma peça para operar, e o OTP multicanal com alternativa exigiria extensões próprias.
- Fornecedor de identidade na nuvem: custo por utilizador e dependência externa para um mercado com canais (WhatsApp, SMS locais) pouco suportados.
