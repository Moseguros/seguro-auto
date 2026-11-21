// Configuração comum dos testes de carga (S6-08 §4). Variáveis de ambiente (k6 -e NOME=valor ou -e no
// docker run):
//   BASE_URL     origem da web/API (por defeito http://localhost:3000, o nginx do compose)
//   TOKENS_FILE  tokens de acesso dos clientes de carga, um por linha (perf/get-tokens.sh)
//   QUOTES_FILE  pedidos de cotação gerados pelo perf/get-tokens.sh
//   DURATION     duração das corridas de carga (por defeito 5m)
//   QUOTE_START_DATE  data de início das cotações (AAAA-MM-DD); por defeito amanhã ou 2026-12-01 (início
//                     dos produtos de demonstração), o que for depois

export const BASE_URL = (__ENV.BASE_URL || 'http://localhost:3000').replace(/\/$/, '');
export const TOKENS_FILE = __ENV.TOKENS_FILE || '/perf/data/tokens.txt';
export const QUOTES_FILE = __ENV.QUOTES_FILE || '/perf/data/quote-requests.json';
export const DURATION = __ENV.DURATION || '5m';

/** Metas da R1 (plano): p95 < 1 s, menos de 1% de erros, mais de 99% de verificações certas. */
export function thresholds(scenario) {
  return {
    [`http_req_duration{scenario:${scenario}}`]: ['p(95)<1000'],
    http_req_failed: ['rate<0.01'],
    checks: ['rate>0.99'],
  };
}

/** Linhas não vazias de um ficheiro de texto (só no contexto de inicialização do k6). */
export function readLines(path) {
  return open(path)
    .split(/\r?\n/)
    .map((line) => line.trim())
    .filter((line) => line.length > 0 && !line.startsWith('#'));
}

/** Código de verificação de carga n.º i (R__dev_seed_s6.sql): PERF + 18 dígitos. */
export function perfToken(i) {
  return `PERF${String(i).padStart(18, '0')}`;
}

/** Matrícula de carga n.º i (R__dev_seed_s6.sql): PRF-000-MP … PRF-999-MP. */
export function perfPlate(i) {
  return `PRF-${String(i).padStart(3, '0')}-MP`;
}

/** Data de início das cotações: QUOTE_START_DATE, ou amanhã em UTC, nunca antes de 2026-12-01. */
export function quoteStartDate() {
  if (__ENV.QUOTE_START_DATE) {
    return __ENV.QUOTE_START_DATE;
  }
  const tomorrow = new Date(Date.now() + 24 * 3600 * 1000).toISOString().slice(0, 10);
  return tomorrow > '2026-12-01' ? tomorrow : '2026-12-01';
}
