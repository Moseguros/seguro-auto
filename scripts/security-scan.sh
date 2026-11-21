#!/usr/bin/env bash
# Varrimento OWASP ZAP da Release 1 (S6-08 §6), com o compose a correr:
#   1. zap-baseline.py contra a web (nginx): passivo, cabeçalhos, cookies, divulgação de informação;
#   2. zap-api-scan.py contra a API do backend a partir do contrato OpenAPI (/v3/api-docs): activo.
# Os alertas aceites estão em security/zap/rules.tsv (IGNORE com justificação); os que contam como falha
# estão como FAIL. Relatórios em security/reports/ (fora do Git). Sai com 1 se houver alertas FAIL.
#
# Uso: ./scripts/security-scan.sh
#   COMPOSE_PROJECT  projecto do docker compose (por defeito moseguros); a rede é <projecto>_default
#   ZAP_NETWORK      rede Docker onde correm os alvos (por defeito <COMPOSE_PROJECT>_default)
#   WEB_TARGET       por defeito http://web:8080
#   API_TARGET       por defeito http://backend:8080/v3/api-docs
#   SKIP_API_SCAN=1  só o varrimento passivo da web (o activo da API demora vários minutos)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ZAP_IMAGE="ghcr.io/zaproxy/zaproxy:2.15.0"
COMPOSE_PROJECT="${COMPOSE_PROJECT:-moseguros}"
ZAP_NETWORK="${ZAP_NETWORK:-${COMPOSE_PROJECT}_default}"
WEB_TARGET="${WEB_TARGET:-http://web:8080}"
API_TARGET="${API_TARGET:-http://backend:8080/v3/api-docs}"
REPORTS="$ROOT/security/reports"
RULES="$ROOT/security/zap/rules.tsv"
mkdir -p "$REPORTS"
chmod 777 "$REPORTS" 2>/dev/null || true # o ZAP corre como o utilizador zap (uid 1000)

# Em Git Bash (Windows) os caminhos dos volumes têm de ir no formato do Windows e sem conversão.
host_path() {
  if command -v cygpath >/dev/null 2>&1; then cygpath -w "$1"; else printf '%s' "$1"; fi
}
export MSYS_NO_PATHCONV=1

zap() {
  docker run --rm --network "$ZAP_NETWORK" \
    -v "$(host_path "$REPORTS"):/zap/wrk:rw" \
    -v "$(host_path "$RULES"):/zap/rules.tsv:ro" \
    "$ZAP_IMAGE" "$@"
}

status=0
stamp="$(date -u +%Y%m%dT%H%M%SZ)"

echo "== ZAP baseline: $WEB_TARGET"
# -I: avisos (WARN) não falham; só os FAIL do rules.tsv. -j: AJAX spider (SPA React).
if zap zap-baseline.py -t "$WEB_TARGET" -c /zap/rules.tsv -I -j \
    -r "web-baseline-$stamp.html" -J "web-baseline-$stamp.json"; then
  echo "baseline: sem alertas FAIL"
else
  echo "baseline: alertas FAIL (ver security/reports/web-baseline-$stamp.html)" >&2
  status=1
fi

if [ "${SKIP_API_SCAN:-0}" != "1" ]; then
  echo "== ZAP API scan: $API_TARGET"
  if zap zap-api-scan.py -t "$API_TARGET" -f openapi -c /zap/rules.tsv -I \
      -r "api-scan-$stamp.html" -J "api-scan-$stamp.json"; then
    echo "api scan: sem alertas FAIL"
  else
    echo "api scan: alertas FAIL (ver security/reports/api-scan-$stamp.html)" >&2
    status=1
  fi
fi

exit "$status"
