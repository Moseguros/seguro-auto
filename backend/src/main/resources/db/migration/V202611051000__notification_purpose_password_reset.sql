-- S1-05: o módulo de notificações passa a enviar também os e-mails de recuperação de
-- palavra-passe de staff.
alter table notification.outbound_message drop constraint outbound_message_purpose_check;
alter table notification.outbound_message
  add constraint outbound_message_purpose_check check (purpose in ('OTP','INVITATION','PASSWORD_RESET'));
