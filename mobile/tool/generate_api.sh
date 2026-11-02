#!/usr/bin/env bash
# Gera o cliente Dart da API (packages/moseguros_api) a partir de ../api/openapi.yaml.
# O pacote gerado não se edita à mão: corre-se este script sempre que o contrato muda.
set -euo pipefail

cd "$(dirname "$0")/.."

SPEC="../api/openapi.yaml"
OUT="packages/moseguros_api"

rm -rf "$OUT"

npx --yes @openapitools/openapi-generator-cli@2.15.3 generate \
  -i "$SPEC" \
  -g dart-dio \
  -o "$OUT" \
  --additional-properties=pubName=moseguros_api,pubAuthor=MoSeguros,serializationLibrary=built_value \
  --global-property=apiTests=false,modelTests=false,apiDocs=false,modelDocs=false

# O dart-dio com built_value precisa de gerar os serializadores.
if grep -q "build_runner" "$OUT/pubspec.yaml"; then
  (cd "$OUT" && dart pub get && dart run build_runner build)
fi
