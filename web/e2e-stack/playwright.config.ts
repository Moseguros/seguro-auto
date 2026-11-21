import { defineConfig, devices } from '@playwright/test';

/**
 * Teste ponta a ponta da Release 1 contra o compose real (S6-08 §5): sem page.route, sem servidor do Vite.
 * Precisa do compose a correr com a base limpa (docker compose down -v && docker compose up -d --build).
 *
 *   STACK_BASE_URL    web do compose (por defeito http://localhost:3000)
 *   MAILPIT_URL       Mailpit (por defeito http://localhost:8025)
 *   PONTO24_MOCK_URL  simulador Ponto24 (por defeito http://localhost:8092)
 */
export default defineConfig({
  testDir: '.',
  testMatch: '*.spec.ts',
  fullyParallel: false,
  workers: 1,
  forbidOnly: !!process.env.CI,
  retries: 0,
  // O cenário é longo (convites, dois pagamentos, emissão automática e a leitura do ficheiro MEPS).
  timeout: 15 * 60_000,
  expect: { timeout: 15_000 },
  outputDir: '../test-results-stack',
  reporter: process.env.CI
    ? [['github'], ['html', { open: 'never', outputFolder: '../playwright-report-stack' }]]
    : [['list'], ['html', { open: 'never', outputFolder: '../playwright-report-stack' }]],
  use: {
    baseURL: process.env.STACK_BASE_URL ?? 'http://localhost:3000',
    locale: 'pt-MZ',
    timezoneId: 'Africa/Maputo',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    navigationTimeout: 60_000,
    actionTimeout: 20_000,
  },
  projects: [{ name: 'chromium', use: { ...devices['Desktop Chrome'] } }],
});
