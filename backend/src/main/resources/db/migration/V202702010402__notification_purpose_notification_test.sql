-- S6-04 §8: o envio de teste de um modelo (POST /admin/notification-templates/{id}/test) fica em
-- outbound_message com purpose = NOTIFICATION_TEST. A constraint é recriada com todos os valores em uso até
-- ao Sprint 6 (modelo transversal §6, linha notification).
alter table notification.outbound_message drop constraint outbound_message_purpose_check;
alter table notification.outbound_message
  add constraint outbound_message_purpose_check
  check (purpose in ('OTP','INVITATION','PASSWORD_RESET','KYC','SIGN_OTP','PROPOSAL','PAYMENT',
                     'NOTIFICATION_TEST'));
