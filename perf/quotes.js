// Carga das cotações (S6-08 §4): 50 pedidos/s de POST /api/v1/quotes (S3-02) com os veículos e condutores
// dos 20 clientes de carga e os tokens rotativos do perf/get-tokens.sh. Meta da R1: p95 < 1 s.
//
// ./perf/get-tokens.sh && docker run --rm --network host -v "$PWD/perf:/perf" grafana/k6:0.54.0 run /perf/quotes.js
//
// O backend tem de correr com MOSEGUROS_RATING_MAX_REQUESTS_PER_HOUR=1000000 (perf/README.md) e as
// seguradoras de carga só com tarifação INTERNAL. Os tokens de acesso valem 15 minutos.
import http from 'k6/http';
import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { BASE_URL, DURATION, QUOTES_FILE, TOKENS_FILE, quoteStartDate, readLines, thresholds } from './lib/config.js';

const SCENARIO = 'quotes';
const tokens = new SharedArray('tokens', () => readLines(TOKENS_FILE));
const requests = new SharedArray('quote-requests', () => JSON.parse(open(QUOTES_FILE)));
const startDate = quoteStartDate();

export const options = {
  scenarios: {
    [SCENARIO]: {
      executor: 'constant-arrival-rate',
      rate: Number(__ENV.RATE || 50),
      timeUnit: '1s',
      duration: DURATION,
      preAllocatedVUs: 100,
      maxVUs: 300,
    },
  },
  thresholds: thresholds(SCENARIO),
  summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

export function setup() {
  if (tokens.length === 0 || requests.length === 0) {
    throw new Error('Sem tokens ou pedidos de cotação: correr ./perf/get-tokens.sh antes.');
  }
  return {};
}

export default function () {
  const request = requests[Math.floor(Math.random() * requests.length)];
  const token = tokens[request.tokenIndex % tokens.length];
  const body = {
    productType: 'MTPL_ONLY',
    vehicleId: request.vehicleId,
    driverId: request.driverId,
    startDate,
    durationDays: 365,
    coverages: [],
  };
  const response = http.post(`${BASE_URL}/api/v1/quotes`, JSON.stringify(body), {
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
    tags: { name: 'POST /quotes' },
  });
  check(response, {
    'status 201': (r) => r.status === 201 || r.status === 200,
    'com cotações': (r) => {
      const quotes = r.status < 300 ? r.json('quotes') : null;
      return Array.isArray(quotes) && quotes.length > 0;
    },
  });
}
