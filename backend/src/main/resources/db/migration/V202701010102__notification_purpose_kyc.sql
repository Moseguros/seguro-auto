-- S4-01: o módulo de notificações passa a enviar os e-mails do resultado do KYC. Como manda o
-- modelo transversal (§ numeração, linha notification), a constraint é recriada com todos os
-- valores até ao Sprint 4, incluindo os das tarefas paralelas (SIGN_OTP do S4-04, PROPOSAL do
-- S4-05): assim a ordem em que as migrações do sprint chegam ao main não apaga valores.
alter table notification.outbound_message drop constraint outbound_message_purpose_check;
alter table notification.outbound_message
  add constraint outbound_message_purpose_check
  check (purpose in ('OTP','INVITATION','PASSWORD_RESET','KYC','SIGN_OTP','PROPOSAL'));
