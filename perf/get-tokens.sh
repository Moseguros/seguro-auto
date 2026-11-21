#!/usr/bin/env bash
# Tokens e pedidos de cotação dos clientes de carga (S6-08 §4). Só contra o perfil local (o código do
# OTP vem no cabeçalho X-Debug-Otp, proibido em prod).
#
# Para cada cliente de carga (+258841000000 … +258841000019, R__dev_seed_s6.sql): requestOtp + verifyOtp,
# garante um condutor (POST /me/drivers: o número da carta é cifrado e não se gera em SQL) e lê o veículo
# (GET /me/vehicles). Escreve:
#   perf/data/tokens.txt           um token de acesso por linha (vale 15 minutos)
#   perf/data/quote-requests.json  [{ tokenIndex, vehicleId, driverId }]
# Os dois ficam fora do Git (.gitignore). Repetir a menos de 60 s dá OTP_RESEND_TOO_SOON (S1-03).
#
# Uso: ./perf/get-tokens.sh            (BASE_URL=http://localhost:3000 por defeito)
#      BASE_URL=http://localhost:8080 COUNT=5 ./perf/get-tokens.sh
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:3000}"
BASE_URL="${BASE_URL%/}"
COUNT="${COUNT:-20}"
DIR="$(cd "$(dirname "$0")" && pwd)"
OUT="$DIR/data"
mkdir -p "$OUT"

TOKENS="$OUT/tokens.txt"
QUOTES="$OUT/quote-requests.json"
: > "$TOKENS.tmp"
printf '[' > "$QUOTES.tmp"

# Valor de um campo de texto de um JSON plano (sem jq): o primeiro "campo":"valor".
json_field() {
  { grep -o "\"$1\"[[:space:]]*:[[:space:]]*\"[^\"]*\"" || true; } | head -n 1 | sed 's/.*:[[:space:]]*"\([^"]*\)"$/\1/'
}

fail() {
  echo "get-tokens: $*" >&2
  rm -f "$TOKENS.tmp" "$QUOTES.tmp"
  exit 1
}

for i in $(seq 0 $((COUNT - 1))); do
  msisdn="+2588410000$(printf '%02d' "$i")"
  headers="$(mktemp)"
  body="$(curl -sS -D "$headers" -X POST "$BASE_URL/api/v1/auth/otp/request" \
    -H 'Content-Type: application/json' \
    -d "{\"identifier\":\"$msisdn\",\"identifierType\":\"MSISDN\"}")" || fail "requestOtp falhou para o cliente $i"
  code="$(tr -d '\r' < "$headers" | sed -n 's/^[Xx]-[Dd]ebug-[Oo]tp:[[:space:]]*//p' | head -n 1)"
  rm -f "$headers"
  challenge="$(printf '%s' "$body" | json_field challengeId)"
  [ -n "$code" ] && [ -n "$challenge" ] \
    || fail "sem X-Debug-Otp ou challengeId para o cliente $i (perfil local? OTP pedido há menos de 60 s?): $body"

  token="$(curl -sS -X POST "$BASE_URL/api/v1/auth/otp/verify" -H 'Content-Type: application/json' \
    -d "{\"challengeId\":\"$challenge\",\"code\":\"$code\"}" | json_field accessToken)"
  [ -n "$token" ] || fail "verifyOtp sem accessToken para o cliente $i"
  auth="Authorization: Bearer $token"

  driver="$(curl -sS "$BASE_URL/api/v1/me/drivers" -H "$auth" | json_field id)"
  if [ -z "$driver" ]; then
    letter="$(printf "\\$(printf '%03o' $((65 + i)))")"
    driver="$(curl -sS -X POST "$BASE_URL/api/v1/me/drivers" -H "$auth" -H 'Content-Type: application/json' \
      -d "{\"fullName\":\"Cliente Carga $letter\",\"birthDate\":\"1985-01-01\",\"licenceNumber\":\"PRF-$(printf '%07d' "$i")\",\"licenceCategories\":[\"B\"],\"licenceIssueDate\":\"2005-01-01\",\"isOwner\":true}" \
      | json_field id)"
  fi
  [ -n "$driver" ] || fail "sem condutor para o cliente $i"

  vehicle="$(curl -sS "$BASE_URL/api/v1/me/vehicles" -H "$auth" | json_field id)"
  [ -n "$vehicle" ] || fail "sem veículo para o cliente $i (R__dev_seed_s6.sql aplicado?)"

  printf '%s\n' "$token" >> "$TOKENS.tmp"
  [ "$i" -gt 0 ] && printf ',' >> "$QUOTES.tmp"
  printf '\n  {"tokenIndex": %d, "vehicleId": "%s", "driverId": "%s"}' "$i" "$vehicle" "$driver" >> "$QUOTES.tmp"
  echo "cliente $i ($msisdn): token, condutor e veículo prontos"
done

printf '\n]\n' >> "$QUOTES.tmp"
mv "$TOKENS.tmp" "$TOKENS"
mv "$QUOTES.tmp" "$QUOTES"
echo "Escrito: $TOKENS ($COUNT tokens) e $QUOTES"
