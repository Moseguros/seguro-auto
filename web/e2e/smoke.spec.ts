import { expect, test, type Page } from '@playwright/test';

test.use({ viewport: { width: 360, height: 740 } });

test.beforeEach(async ({ page }) => {
  await page.route('**/api/v1/meta/version', (route) =>
    route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ name: 'moseguros', version: '0.1.0-e2e', commit: 'e2ecafe' }),
    }),
  );
});

async function expectNoHorizontalScroll(page: Page) {
  const overflow = await page.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

test('/ mostra a versão da API', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('heading', { level: 1, name: 'MoSeguros' })).toBeVisible();
  await expect(
    page.getByText('Compare, compre e gira o seu seguro automóvel num só lugar.'),
  ).toBeVisible();
  await expect(page.getByTestId('api-version')).toHaveText('0.1.0-e2e');
  await expect(page.getByTestId('api-commit')).toHaveText('e2ecafe');
  await expectNoHorizontalScroll(page);
});

test('/seguradora mostra o menu da área (com sessão de staff)', async ({ page }) => {
  // Sessão de staff de uma seguradora: o refresh token guardado é renovado ao arrancar.
  await page.addInitScript(() => sessionStorage.setItem('moseguros.staff.rt', 'refresh-smoke'));
  const json = (body: unknown) => ({
    status: 200,
    contentType: 'application/json',
    body: JSON.stringify(body),
  });
  await page.route('**/api/v1/auth/refresh', (route) =>
    route.fulfill(
      json({ accessToken: 'access-smoke', refreshToken: 'refresh-smoke', expiresIn: 900 }),
    ),
  );
  await page.route('**/api/v1/me', (route) =>
    route.fulfill(
      json({
        id: '0192f0c4-0000-7000-8000-000000000002',
        type: 'STAFF',
        roles: ['UNDERWRITER'],
        permissions: ['product:read'],
        preferredLanguage: 'pt',
        emailVerified: true,
        msisdnVerified: false,
        displayName: 'Subscritor',
        organization: { id: 'o1', displayName: 'Índico Seguros', type: 'INSURER' },
      }),
    ),
  );
  await page.goto('/seguradora');
  await expect(page.getByRole('heading', { name: 'Em construção' })).toBeVisible();
  // A 360 px o menu é uma gaveta.
  await page.getByRole('button', { name: 'Abrir menu' }).click();
  const nav = page.getByRole('navigation', { name: 'Seguradora' });
  await expect(nav.getByRole('link', { name: 'Tarifas' })).toBeVisible();
  // Sem proposal:read o item "Propostas" não aparece (S4-07).
  await expect(nav.getByRole('link', { name: 'Propostas' })).toHaveCount(0);
  await nav.getByRole('link', { name: 'Apólices' }).click();
  await expect(page).toHaveURL(/\/seguradora\/apolices$/);
  await expect(nav).toBeHidden();
  await expectNoHorizontalScroll(page);
});

test('/xyz mostra 404', async ({ page }) => {
  await page.goto('/xyz');
  await expect(page.getByRole('heading', { name: 'Página não encontrada' })).toBeVisible();
  await expectNoHorizontalScroll(page);
});

test('muda a língua para inglês', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('combobox', { name: 'Língua' }).click();
  await page.getByRole('option', { name: 'English' }).click();
  await expect(
    page.getByText('Compare, buy and manage your car insurance in one place.'),
  ).toBeVisible();
  await expect(page.locator('html')).toHaveAttribute('lang', 'en');
});
