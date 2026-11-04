-- S4-04: contexto do código de assinatura (purpose = SIGN). A alternativa de canal (síncrona e por
-- recibo de entrega, OtpFallbackListener) gera um código novo, cujo hash tem de voltar a incluir o
-- hash do documento (HMAC-SHA256(pepper, challengeId:code:documentHash)), e reenvia a mensagem com
-- o número da proposta, a seguradora e o total. Sem dados pessoais: só o hash e o id do PDF, o
-- número da proposta, o nome da seguradora, o total em centavos e o nome do mediador (S4-06).
alter table identity.otp_challenge
  add column sign_context jsonb null,
  add constraint ck_otp_challenge_sign_context check ((purpose = 'SIGN') = (sign_context is not null));
