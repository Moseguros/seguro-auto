#!/usr/bin/env bash
# Confirma que infra/staging/.env.staging.example tem todas as variáveis de ambiente lidas (${NOME…})
# pelo application.yml, application-staging.yml e application-prod.yml do backend e pelo application.yml
# do payment-file-worker (S6-08 §9). Os perfis local e test ficam de fora. Sai com 1 se faltar alguma.
#
# Uso: ./scripts/check-env-example.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
EXAMPLE="$ROOT/infra/staging/.env.staging.example"
FILES=(
  "$ROOT/backend/src/main/resources/application.yml"
  "$ROOT/backend/src/main/resources/application-staging.yml"
  "$ROOT/backend/src/main/resources/application-prod.yml"
  "$ROOT/payment-file-worker/src/main/resources/application.yml"
)

[ -f "$EXAMPLE" ] || { echo "Falta $EXAMPLE" >&2; exit 1; }

used="$(cat "${FILES[@]}" | tr -d '\r' | grep -oE '\$\{[A-Z][A-Z0-9_]*' | sed 's/^\${//' | sort -u)"
declared="$(tr -d '\r' < "$EXAMPLE" | grep -oE '^[A-Z][A-Z0-9_]*=' | sed 's/=$//' | sort -u)"

missing="$(comm -23 <(printf '%s\n' "$used") <(printf '%s\n' "$declared"))"
extra="$(comm -13 <(printf '%s\n' "$used") <(printf '%s\n' "$declared"))"

echo "Variáveis lidas pelos application*.yml: $(printf '%s\n' "$used" | grep -c .)"
echo "Variáveis no .env.staging.example:      $(printf '%s\n' "$declared" | grep -c .)"
if [ -n "$extra" ]; then
  echo "Só no exemplo (lidas pelo Spring por nome, ex.: SPRING_MAIL_HOST, ou por bibliotecas):"
  printf '  %s\n' $extra
fi
if [ -n "$missing" ]; then
  echo "FALTAM no infra/staging/.env.staging.example:" >&2
  printf '  %s\n' $missing >&2
  exit 1
fi
echo "OK: o exemplo tem todas as variáveis."
