-- Proposta criada pelo mediador em nome do cliente (S4-06 §1): origem do tomador. PROFILE = copiado
-- do perfil do cliente (S4-02); BROKER = preenchido pelo mediador porque o perfil estava incompleto
-- (os dados ficam só na proposta; identity.customer_profile não muda). Corre como dono; o
-- moseguros_app já lê e altera a tabela.

alter table proposal.proposal
  add column policyholder_source varchar(10) not null default 'PROFILE'
    check (policyholder_source in ('PROFILE','BROKER'));
