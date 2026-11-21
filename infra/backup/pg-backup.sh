#!/usr/bin/env bash
# Cópia de segurança da base de dados (S6-08 §9): pg_dump em formato custom (-Fc) com a data no nome e
# retenção dos 14 ficheiros mais recentes. Corre o pg_dump dentro do contentor do PostgreSQL do compose
# (sem cliente local); em staging usar as cópias do serviço gerido ou este script com PG_CONTAINER vazio.
#
# Uso: ./infra/backup/pg-backup.sh
#   BACKUP_DIR       pasta das cópias (por defeito infra/backup/dumps, fora do Git)
#   COMPOSE_PROJECT  projecto do docker compose (por defeito moseguros)
#   PG_CONTAINER     contentor do PostgreSQL (por defeito <COMPOSE_PROJECT>-postgres-1); vazio = pg_dump local
#   PGUSER/PGDATABASE/PGHOST/PGPASSWORD  ligação (por defeito o dono moseguros/moseguros)
#   RETENTION        número de cópias a manter (por defeito 14)
set -euo pipefail

DIR="$(cd "$(dirname "$0")" && pwd)"
BACKUP_DIR="${BACKUP_DIR:-$DIR/dumps}"
COMPOSE_PROJECT="${COMPOSE_PROJECT:-moseguros}"
PG_CONTAINER="${PG_CONTAINER-${COMPOSE_PROJECT}-postgres-1}"
PGUSER="${PGUSER:-moseguros}"
PGDATABASE="${PGDATABASE:-moseguros}"
RETENTION="${RETENTION:-14}"

mkdir -p "$BACKUP_DIR"
stamp="$(date -u +%Y%m%dT%H%M%SZ)"
file="$BACKUP_DIR/${PGDATABASE}-${stamp}.dump"

if [ -n "$PG_CONTAINER" ]; then
  docker exec "$PG_CONTAINER" pg_dump -U "$PGUSER" -d "$PGDATABASE" -Fc --no-owner > "$file.part"
else
  pg_dump -U "$PGUSER" -d "$PGDATABASE" -Fc --no-owner > "$file.part"
fi
[ -s "$file.part" ] || { rm -f "$file.part"; echo "pg-backup: cópia vazia" >&2; exit 1; }
mv "$file.part" "$file"
echo "pg-backup: $file ($(wc -c < "$file") bytes)"

# Retenção: mantém as RETENTION cópias mais recentes desta base.
mapfile -t old < <(ls -1t "$BACKUP_DIR/${PGDATABASE}-"*.dump 2>/dev/null | tail -n +$((RETENTION + 1)))
for f in "${old[@]}"; do
  rm -f -- "$f"
  echo "pg-backup: apagada a cópia antiga $f"
done
