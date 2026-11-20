// Carga da verificação pública (S6-08 §4): 200 pedidos/s, 80% pelo código do QR e 20% pela matrícula,
// contra as 1 000 apólices de carga do R__dev_seed_s6.sql. Meta da R1: p95 < 1 s.
//
// docker run --rm --network host -v "$PWD/perf:/perf" grafana/k6:0.54.0 run /perf/verifications.js
//
// O backend tem de correr com MOSEGUROS_VERIFICATION_RATE_LIMIT_PUBLIC_PER_MINUTE=1000000 e
// MOSEGUROS_CAPTCHA_PROVIDER=none (perf/README.md): todos os pedidos vêm do mesmo IP.
import http from 'k6/http';
import { check } from 'k6';
import { BASE_URL, DURATION, perfPlate, perfToken, thresholds } from './lib/config.js';

const SCENARIO = 'verifications';
const POLICIES = 1000;

export const options = {
  scenarios: {
    [SCENARIO]: {
      executor: 'constant-arrival-rate',
      rate: Number(__ENV.RATE || 200),
      timeUnit: '1s',
      duration: DURATION,
      preAllocatedVUs: 200,
      maxVUs: 400,
    },
  },
  thresholds: thresholds(SCENARIO),
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  const i = Math.floor(Math.random() * POLICIES);
  let response;
  if (Math.random() < 0.8) {
    response = http.get(`${BASE_URL}/api/v1/public/verifications/${perfToken(i)}`, {
      tags: { name: 'GET /public/verifications/{token}' },
    });
  } else {
    response = http.get(
      `${BASE_URL}/api/v1/public/verifications?plate=${encodeURIComponent(perfPlate(i))}`,
      {
        headers: { 'X-Captcha-Token': 'perf' },
        tags: { name: 'GET /public/verifications?plate' },
      },
    );
  }
  check(response, {
    'status 200': (r) => r.status === 200,
    'seguro VALID': (r) => r.status === 200 && r.json('status') === 'VALID',
  });
}
