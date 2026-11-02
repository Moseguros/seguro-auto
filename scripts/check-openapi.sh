#!/usr/bin/env bash
# Gera api/openapi.yaml a partir do backend e falha se for diferente do versionado.
#
# Uso:
#   ./scripts/check-openapi.sh
#
# Precisa de JDK 17 e do Docker em execução (a aplicação arranca com Testcontainers,
# perfil Maven "openapi" -> OpenApiExportIT). Sai com 1 se o contrato estiver
# desactualizado: nesse caso corre o script, revê o diff e faz commit do api/openapi.yaml.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SPEC="api/openapi.yaml"

cd "$ROOT/backend"
echo "A gerar $SPEC (./mvnw -Popenapi verify)..."
if ! ./mvnw -B -q -Popenapi verify > "$ROOT/backend/target/check-openapi.log" 2>&1; then
  echo "ERRO: a geração do contrato falhou. Últimas linhas de backend/target/check-openapi.log:" >&2
  tail -n 40 "$ROOT/backend/target/check-openapi.log" >&2
  exit 1
fi

cd "$ROOT"
if [ ! -f "$SPEC" ]; then
  echo "ERRO: $SPEC não foi gerado." >&2
  exit 1
fi

if ! git ls-files --error-unmatch "$SPEC" > /dev/null 2>&1; then
  echo "ERRO: $SPEC não está versionado. Faz 'git add $SPEC' e commit." >&2
  exit 1
fi

if ! git diff --exit-code -- "$SPEC"; then
  echo >&2
  echo "ERRO: $SPEC está desactualizado ou foi alterado à mão." >&2
  echo "      O ficheiro é gerado pelo backend: revê o diff acima e faz commit do ficheiro gerado." >&2
  exit 1
fi

echo "OK: $SPEC corresponde ao contrato gerado pelo backend."
