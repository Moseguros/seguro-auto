-- Definições de tarifação da seguradora (S3-01 §2; modelo transversal §2): modo de tarifação,
-- integração externa (escrita só pela S3-02) e encargos (impostos e taxas, charges-v1.json).
-- A política de RLS e os privilégios de insurer_profile (S1-01) não mudam.

alter table organization.insurer_profile
  add column rating_mode varchar(10) not null default 'INTERNAL'
      check (rating_mode in ('INTERNAL','EXTERNAL')),
  add column rating_endpoint_url varchar(500) null,
  add column rating_secret_ref varchar(200) null,
  add column charges jsonb not null default '[]'::jsonb,
  add constraint ck_insurer_profile_external
      check (rating_mode = 'INTERNAL' or (rating_endpoint_url is not null and rating_secret_ref is not null));
