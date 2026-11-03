#!/usr/bin/env bash
# Gera api/openapi.yaml e api/error-codes.json a partir do backend e falha se algum for
# diferente do versionado.
#
# Uso:
#   ./scripts/check-openapi.sh
#
# Precisa de JDK 17 e do Docker em execução (a aplicação arranca com Testcontainers,
# perfil Maven "openapi" -> OpenApiExportIT). A lista de códigos de erro vem do
# ErrorCodesExportTest (ErrorCodes e FieldErrorCodes). Sai com 1 se algum ficheiro estiver
# desactualizado: nesse caso corre o script, revê o diff e faz commit dos ficheiros gerados.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SPEC="api/openapi.yaml"
CODES="api/error-codes.json"

cd "$ROOT"
# Uma alteração local por gravar seria apagada pela geração sem ser detectada.
for file in "$SPEC" "$CODES"; do
  if [ -f "$file" ] && ! git diff --quiet -- "$file"; then
    echo "ERRO: $file tem alterações locais por gravar (o ficheiro é gerado, não se edita à mão)." >&2
    echo "      Descarta-as com 'git checkout -- $file' ou faz commit do ficheiro gerado." >&2
    exit 1
  fi
done

cd "$ROOT/backend"
mkdir -p target
echo "A gerar $SPEC (./mvnw -Popenapi verify)..."
if ! ./mvnw -B -q -Popenapi verify > "$ROOT/backend/target/check-openapi.log" 2>&1; then
  echo "ERRO: a geração do contrato falhou. Últimas linhas de backend/target/check-openapi.log:" >&2
  tail -n 40 "$ROOT/backend/target/check-openapi.log" >&2
  exit 1
fi

echo "A gerar $CODES (ErrorCodesExportTest)..."
if ! ./mvnw -B -q test -Dtest=ErrorCodesExportTest -Derror-codes.export=true     > "$ROOT/backend/target/check-error-codes.log" 2>&1; then
  echo "ERRO: a geração dos códigos de erro falhou. Últimas linhas de backend/target/check-error-codes.log:" >&2
  tail -n 40 "$ROOT/backend/target/check-error-codes.log" >&2
  exit 1
fi

cd "$ROOT"
for file in "$SPEC" "$CODES"; do
  if [ ! -f "$file" ]; then
    echo "ERRO: $file não foi gerado." >&2
    exit 1
  fi

  if ! git ls-files --error-unmatch "$file" > /dev/null 2>&1; then
    echo "ERRO: $file não está versionado. Faz 'git add $file' e commit." >&2
    exit 1
  fi

  if ! git diff --exit-code -- "$file"; then
    echo >&2
    echo "ERRO: $file está desactualizado ou foi alterado à mão." >&2
    echo "      O ficheiro é gerado pelo backend: revê o diff acima e faz commit do ficheiro gerado." >&2
    exit 1
  fi
done

echo "OK: $SPEC e $CODES correspondem ao que o backend gera."
