-- S5-02 §10: o módulo de notificações passa a enviar as mensagens dos pagamentos (referência por SMS,
-- link de pagamento do mediador, aviso de pagamento recebido) com purpose = PAYMENT. A constraint é
-- recriada com todos os valores em uso até ao Sprint 5 (modelo transversal §6, linha notification):
-- assim a ordem em que as migrações chegam ao main não apaga valores.
alter table notification.outbound_message drop constraint outbound_message_purpose_check;
alter table notification.outbound_message
  add constraint outbound_message_purpose_check
  check (purpose in ('OTP','INVITATION','PASSWORD_RESET','KYC','SIGN_OTP','PROPOSAL','PAYMENT'));
