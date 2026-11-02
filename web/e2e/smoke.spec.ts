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

test('/seguradora mostra o menu da área', async ({ page }) => {
  await page.goto('/seguradora');
  await expect(page.getByRole('heading', { name: 'Em construção' })).toBeVisible();
  // A 360 px o menu é uma gaveta.
  await page.getByRole('button', { name: 'Abrir menu' }).click();
  const nav = page.getByRole('navigation', { name: 'Seguradora' });
  await expect(nav.getByRole('link', { name: 'Tarifas' })).toBeVisible();
  await nav.getByRole('link', { name: 'Propostas' }).click();
  await expect(page).toHaveURL(/\/seguradora\/propostas$/);
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
