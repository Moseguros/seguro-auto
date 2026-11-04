-- Vistoria de danos próprios e as suas fotografias (S4-03 §1; modelo transversal §2).
-- Corre como dono. Os privilégios do moseguros_app (select, insert, update; sem delete) vêm do
-- "alter default privileges" do V202701010201: as fotografias substituídas ou retiradas ficam com
-- removed_at, nunca se apagam.

create table proposal.inspection (
  id                uuid primary key,
  created_at        timestamptz not null,
  created_by        varchar(64) not null,
  updated_at        timestamptz not null,
  updated_by        varchar(64) not null,
  version           bigint not null default 0,
  insurer_id        uuid not null,                                   -- igual ao da proposta
  proposal_id       uuid not null unique references proposal.proposal (id),
  mode              varchar(12) not null default 'SELF_PHOTOS' check (mode in ('SELF_PHOTOS','ON_SITE')),
  status            varchar(10) not null default 'PENDING'
                    check (status in ('PENDING','SUBMITTED','APPROVED','REJECTED')),
  odometer_km       int null check (odometer_km between 0 and 2000000),
  customer_notes    varchar(500) null,                               -- pedido presencial: local e horas
  submitted_at      timestamptz null,
  reviewed_by       uuid null,
  reviewed_at       timestamptz null,
  review_notes      varchar(1000) null,
  rejection_reason  varchar(1000) null,
  retake_slots      text[] null,
  check ((status = 'REJECTED') = (rejection_reason is not null))
);

create index ix_inspection_insurer on proposal.inspection (insurer_id, status, submitted_at);

create table proposal.inspection_photo (
  id               uuid primary key,
  created_at       timestamptz not null,
  created_by       varchar(64) not null,
  updated_at       timestamptz not null,
  updated_by       varchar(64) not null,
  version          bigint not null default 0,
  insurer_id       uuid not null,                                    -- igual ao da vistoria
  inspection_id    uuid not null references proposal.inspection (id),
  slot             varchar(10) not null
                   check (slot in ('FRONT','REAR','LEFT','RIGHT','ODOMETER','CHASSIS','EXTRA')),
  file_id          uuid not null,                                    -- document.file, noutro schema: sem FK
  client_photo_id  uuid null,                                        -- gerado pelo cliente (idempotência)
  source           varchar(10) not null check (source in ('CAMERA','UPLOAD')),
  taken_at         timestamptz null,
  latitude         numeric(9,6) null check (latitude between -90 and 90),
  longitude        numeric(9,6) null check (longitude between -180 and 180),
  removed_at       timestamptz null,
  check ((latitude is null) = (longitude is null))
);

create index ix_photo_inspection on proposal.inspection_photo (inspection_id, created_at);
create unique index ux_photo_slot_active on proposal.inspection_photo (inspection_id, slot)
  where removed_at is null and slot <> 'EXTRA';
create unique index ux_photo_client_id on proposal.inspection_photo (inspection_id, client_photo_id)
  where client_photo_id is not null;

-- Row Level Security (padrão das filhas do S4-02): visível exactamente quando a proposta o é.
alter table proposal.inspection enable row level security;
create policy inspection_scope on proposal.inspection
  for all to moseguros_app
  using (exists (select 1 from proposal.proposal p where p.id = proposal_id))
  with check (exists (select 1 from proposal.proposal p where p.id = proposal_id));

alter table proposal.inspection_photo enable row level security;
create policy inspection_photo_scope on proposal.inspection_photo
  for all to moseguros_app
  using (exists (select 1 from proposal.inspection i where i.id = inspection_id))
  with check (exists (select 1 from proposal.inspection i where i.id = inspection_id));
