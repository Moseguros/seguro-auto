#!/usr/bin/env bash
# Teste de restauro (S6-08 §9): restaura uma cópia do pg-backup.sh numa base temporária e confirma que o
# número de linhas de policy.policy e payment.payment_intent é igual ao da base de origem (contagem feita
# agora: correr logo a seguir à cópia, ou com COMPARE=0 para só confirmar que o restauro funciona).
# Apaga a base temporária no fim, passe ou falhe.
#
# Uso: ./infra/backup/pg-restore-test.sh [ficheiro.dump]   (sem argumento: a cópia mais recente)
#   mesmas variáveis do pg-backup.sh (BACKUP_DIR, COMPOSE_PROJECT, PG_CONTAINER, PGUSER, PGDATABASE)
set -euo pipefail

DIR="$(cd "$(dirname "$0")" && pwd)"
BACKUP_DIR="${BACKUP_DIR:-$DIR/dumps}"
COMPOSE_PROJECT="${COMPOSE_PROJECT:-moseguros}"
PG_CONTAINER="${PG_CONTAINER-${COMPOSE_PROJECT}-postgres-1}"
PGUSER="${PGUSER:-moseguros}"
PGDATABASE="${PGDATABASE:-moseguros}"
COMPARE="${COMPARE:-1}"
TARGET="restore_test_$(date -u +%Y%m%d%H%M%S)"
TABLES=(policy.policy payment.payment_intent)

file="${1:-$(ls -1t "$BACKUP_DIR/${PGDATABASE}-"*.dump 2>/dev/null | head -n 1 || true)}"
[ -n "$file" ] && [ -f "$file" ] || { echo "pg-restore-test: sem cópia em $BACKUP_DIR" >&2; exit 1; }

run() {
  if [ -n "$PG_CONTAINER" ]; then
    docker exec -i "$PG_CONTAINER" "$@"
  else
    "$@"
  fi
}

count() {
  run psql -U "$PGUSER" -d "$1" -tAc "select count(*) from $2" | tr -d '[:space:]'
}

cleanup() {
  run psql -U "$PGUSER" -d postgres -qc "drop database if exists $TARGET" >/dev/null 2>&1 || true
}
trap cleanup EXIT

echo "pg-restore-test: a restaurar $file em $TARGET"
run psql -U "$PGUSER" -d postgres -qc "create database $TARGET"
# --no-owner/--no-privileges: os papéis já existem no servidor; os GRANT e o RLS ficam como estão no dump.
run pg_restore -U "$PGUSER" -d "$TARGET" --no-owner --no-privileges --exit-on-error < "$file"

status=0
for table in "${TABLES[@]}"; do
  restored="$(count "$TARGET" "$table")"
  if [ "$COMPARE" = "1" ]; then
    source_count="$(count "$PGDATABASE" "$table")"
    if [ "$restored" = "$source_count" ]; then
      echo "pg-restore-test: $table OK ($restored linhas)"
    else
      echo "pg-restore-test: $table DIFERENTE: restaurada $restored, origem $source_count" >&2
      status=1
    fi
  else
    echo "pg-restore-test: $table restaurada com $restored linhas"
  fi
done

[ "$status" = "0" ] && echo "pg-restore-test: restauro OK" || echo "pg-restore-test: restauro com diferenças" >&2
exit "$status"
