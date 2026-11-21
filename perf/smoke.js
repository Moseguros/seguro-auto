// Fumo dos testes de carga (S6-08 §4): 1 utilizador virtual durante 30 s, um pedido de cada tipo por
// iteração (código do QR, matrícula, cotação). Serve para validar os scripts e os dados antes das corridas.
//
// ./perf/get-tokens.sh && docker run --rm --network host -v "$PWD/perf:/perf" grafana/k6:0.54.0 run /perf/smoke.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import {
  BASE_URL,
  QUOTES_FILE,
  TOKENS_FILE,
  perfPlate,
  perfToken,
  quoteStartDate,
  readLines,
} from './lib/config.js';

const tokens = new SharedArray('tokens', () => readLines(TOKENS_FILE));
const requests = new SharedArray('quote-requests', () => JSON.parse(open(QUOTES_FILE)));

export const options = {
  vus: 1,
  duration: __ENV.DURATION || '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    checks: ['rate>0.99'],
  },
};

export default function () {
  const i = Math.floor(Math.random() * 1000);

  const byToken = http.get(`${BASE_URL}/api/v1/public/verifications/${perfToken(i)}`, {
    tags: { name: 'GET /public/verifications/{token}' },
  });
  check(byToken, { 'código: VALID': (r) => r.status === 200 && r.json('status') === 'VALID' });

  const byPlate = http.get(
    `${BASE_URL}/api/v1/public/verifications?plate=${encodeURIComponent(perfPlate(i))}`,
    { headers: { 'X-Captcha-Token': 'perf' }, tags: { name: 'GET /public/verifications?plate' } },
  );
  check(byPlate, { 'matrícula: VALID': (r) => r.status === 200 && r.json('status') === 'VALID' });

  const request = requests[i % requests.length];
  const quote = http.post(
    `${BASE_URL}/api/v1/quotes`,
    JSON.stringify({
      productType: 'MTPL_ONLY',
      vehicleId: request.vehicleId,
      driverId: request.driverId,
      startDate: quoteStartDate(),
      durationDays: 365,
      coverages: [],
    }),
    {
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${tokens[request.tokenIndex % tokens.length]}`,
      },
      tags: { name: 'POST /quotes' },
    },
  );
  check(quote, {
    'cotação: 2xx': (r) => r.status === 201 || r.status === 200,
  });

  sleep(1);
}
