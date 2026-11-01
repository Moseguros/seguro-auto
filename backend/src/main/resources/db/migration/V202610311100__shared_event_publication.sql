-- Registo de publicação de eventos do Spring Modulith (outbox persistente, ADR-001).
-- Esquema oficial do Spring Modulith 1.2 para PostgreSQL.
create table if not exists event_publication
(
    id               uuid                     not null,
    listener_id      text                     not null,
    event_type       text                     not null,
    serialized_event text                     not null,
    publication_date timestamp with time zone not null,
    completion_date  timestamp with time zone,
    primary key (id)
);

create index if not exists event_publication_serialized_event_hash_idx
    on event_publication using hash (serialized_event);

create index if not exists event_publication_by_completion_date_idx
    on event_publication (completion_date);
