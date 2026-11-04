-- S4-05: o módulo de notificações passa a enviar os e-mails da proposta (submissão, decisão, pedido
-- de informação e alerta de SLA) com purpose = PROPOSAL, como fizeram o S1-04 e o S1-05. A
-- constraint é recriada com todos os valores até ao Sprint 4 (KYC do S4-01 e SIGN_OTP do S4-04, de
-- que esta tarefa depende): assim a ordem em que as migrações chegam ao main não apaga valores.
alter table notification.outbound_message drop constraint outbound_message_purpose_check;
alter table notification.outbound_message
  add constraint outbound_message_purpose_check
  check (purpose in ('OTP','INVITATION','PASSWORD_RESET','KYC','SIGN_OTP','PROPOSAL'));
