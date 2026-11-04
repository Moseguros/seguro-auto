-- S4-04: o módulo de notificações passa a enviar o código de assinatura da proposta (SIGN_OTP),
-- como fizeram o S1-04 e o S1-05. A constraint é recriada com todos os valores até ao Sprint 4,
-- incluindo o PROPOSAL do S4-05 que o S4-01 (V202701010102) já tinha acrescentado: assim a ordem em
-- que as migrações do sprint chegam ao main não apaga valores.
alter table notification.outbound_message drop constraint outbound_message_purpose_check;
alter table notification.outbound_message
  add constraint outbound_message_purpose_check
  check (purpose in ('OTP','INVITATION','PASSWORD_RESET','KYC','SIGN_OTP','PROPOSAL'));
