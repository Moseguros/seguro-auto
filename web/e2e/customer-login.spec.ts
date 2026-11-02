import { expect, test, type Page, type Route } from '@playwright/test';

test.use({ viewport: { width: 360, height: 740 } });

const options = {
  identifierTypes: ['MSISDN', 'EMAIL'],
  channels: { MSISDN: ['SMS', 'WHATSAPP'], EMAIL: ['EMAIL'] },
  defaultChannels: { MSISDN: 'WHATSAPP', EMAIL: 'EMAIL' },
  customerCanChoose: true,
  codeLength: 6,
  resendAfterSeconds: 60,
};

interface Scenario {
  /** Resposta do pedido de código. */
  otp?: Partial<{ channel: string; fallbackUsed: boolean }>;
  /** Código certo; outro dá OTP_INVALID. */
  code?: string;
}

/** API simulada com estado: perfil por completar até ao primeiro PUT. */
async function mockApi(page: Page, scenario: Scenario = {}) {
  const state = { profileComplete: false, attempts: 5, otpRequests: [] as unknown[] };
  const json = (route: Route, status: number, body: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: JSON.stringify(body),
    });
  const me = () => ({
    id: '0192f0c4-0000-7000-8000-000000000001',
    type: 'CUSTOMER',
    roles: ['CUSTOMER'],
    permissions: [],
    preferredLanguage: 'pt',
    msisdn: '+258841234567',
    msisdnVerified: true,
    emailVerified: false,
    profileComplete: state.profileComplete,
    ...(state.profileComplete ? { displayName: 'Ana Maria Mussa' } : {}),
  });

  await page.route('**/api/v1/**', async (route) => {
    const request = route.request();
    const { pathname } = new URL(request.url());
    const method = request.method();
    const key = `${method} ${pathname}`;
    if (key === 'GET /api/v1/auth/otp/options') return json(route, 200, options);
    if (key === 'POST /api/v1/auth/otp/request') {
      state.otpRequests.push(request.postDataJSON());
      return json(route, 202, {
        challengeId: '0192f0c4-0000-7000-8000-0000000000c1',
        channel: 'WHATSAPP',
        expiresAt: new Date(Date.now() + 300_000).toISOString(),
        fallbackUsed: false,
        resendAfterSeconds: 60,
        ...scenario.otp,
      });
    }
    if (pathname.startsWith('/api/v1/auth/otp/challenges/')) {
      return json(route, 200, {
        channel: scenario.otp?.channel ?? 'WHATSAPP',
        deliveryStatus: 'DELIVERED',
        expiresAt: new Date(Date.now() + 300_000).toISOString(),
        fallbackUsed: scenario.otp?.fallbackUsed ?? false,
      });
    }
    if (key === 'POST /api/v1/auth/otp/verify') {
      const body = request.postDataJSON() as { code: string };
      if (body.code !== (scenario.code ?? '123456')) {
        state.attempts -= 1;
        return json(route, 400, {
          type: 'https://moseguros.co.mz/problems/otp-invalid',
          title: 'Bad Request',
          status: 400,
          code: 'OTP_INVALID',
          attemptsRemaining: state.attempts,
        });
      }
      return json(route, 200, {
        accessToken: 'access-1',
        refreshToken: 'refresh-1',
        expiresIn: 900,
        isNewUser: true,
      });
    }
    if (key === 'GET /api/v1/me') {
      if (request.headers()['authorization'] !== 'Bearer access-1') {
        return json(route, 401, { status: 401, code: 'UNAUTHORIZED' });
      }
      return json(route, 200, me());
    }
    if (key === 'GET /api/v1/me/profile') return json(route, 200, { complete: false, version: 0 });
    if (key === 'PUT /api/v1/me/profile') {
      state.profileComplete = true;
      const body = request.postDataJSON() as Record<string, unknown>;
      return json(route, 200, {
        complete: true,
        version: 1,
        fullName: body.fullName,
        birthDate: body.birthDate,
        idDocType: body.idDocType,
        idDocNumberMasked: '*********345A',
        nuitMasked: '*****6789',
        province: body.province,
      });
    }
    if (key === 'GET /api/v1/me/contacts') {
      return json(route, 200, {
        contacts: [{ type: 'MSISDN', value: '+258841234567', verified: true }],
      });
    }
    return json(route, 404, { status: 404, code: 'NOT_FOUND' });
  });
  return state;
}

async function expectNoHorizontalScroll(page: Page) {
  const overflow = await page.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

async function requestCode(page: Page) {
  await page.goto('/cliente/entrar');
  await expect(page.getByRole('heading', { name: 'Entrar na área do cliente' })).toBeVisible();
  await page.getByLabel('Número de telemóvel').fill('841234567');
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Receber código' }).click();
}

async function typeCode(page: Page, code: string) {
  await page.getByLabel('Dígito 1 de 6').click();
  await page.keyboard.type(code);
}

test('login por telemóvel, completar o perfil e chegar ao início', async ({ page }) => {
  const state = await mockApi(page);
  await requestCode(page);
  expect(state.otpRequests).toEqual([
    { identifierType: 'MSISDN', identifier: '+258841234567', channel: 'WHATSAPP' },
  ]);
  await expect(
    page.getByText('Enviámos um código por WhatsApp para +258*****4567.'),
  ).toBeVisible();
  await expect(page.getByRole('button', { name: /Reenviar código \(\d+ s\)/ })).toBeDisabled();
  await expectNoHorizontalScroll(page);
  await typeCode(page, '123456');

  await expect(page).toHaveURL(/\/cliente\/perfil\?completar=1$/);
  await expect(
    page.getByText('Precisamos destes dados para emitir as suas apólices.'),
  ).toBeVisible();
  await page.getByLabel('Nome completo').fill('Ana Maria Mussa');
  await page.getByLabel('Data de nascimento').fill('1990-05-14');
  await page.getByLabel('Tipo de documento').selectOption('BI');
  await page.getByLabel('Número do documento').fill('110100123345A');
  await page.getByLabel('NUIT').fill('123456789');
  await page.getByLabel('Província').selectOption('MAPUTO_CITY');
  await expect(page.getByTestId('contact-MSISDN')).toContainText('Verificado');
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Guardar' }).click();

  await expect(page).toHaveURL(/\/cliente$/);
  await expect(page.getByRole('heading', { name: 'Olá, Ana' })).toBeVisible();
  await expect(page.getByRole('link', { name: 'Simular seguro' })).toBeVisible();
  await expect(page.getByText('Complete o seu perfil para poder comprar um seguro.')).toBeHidden();
  await expectNoHorizontalScroll(page);

  await page.getByRole('link', { name: 'Simular seguro' }).click();
  await expect(page).toHaveURL(/\/cliente\/simular$/);
  await expect(page.getByRole('heading', { name: 'Em construção' })).toBeVisible();
});

test('código enviado por outro canal mostra o aviso', async ({ page }) => {
  await mockApi(page, { otp: { channel: 'SMS', fallbackUsed: true } });
  await requestCode(page);
  await expect(page.getByText('Enviámos um código por SMS para +258*****4567.')).toBeVisible();
  await expect(
    page.getByText('Não conseguimos enviar por WhatsApp. Enviámos o código por SMS.'),
  ).toBeVisible();
});

test('código errado mostra as tentativas restantes', async ({ page }) => {
  await mockApi(page, { code: '654321' });
  await requestCode(page);
  await typeCode(page, '111111');
  await expect(page.getByRole('alert')).toHaveText('Código errado. Restam 4 tentativas.');
  await expect(page).toHaveURL(/\/cliente\/entrar$/);
});

test('/cliente/perfil sem sessão vai para a entrada', async ({ page }) => {
  await mockApi(page);
  await page.goto('/cliente/perfil');
  await expect(page).toHaveURL(/\/cliente\/entrar\?voltar=%2Fcliente%2Fperfil$/);
  await expect(page.getByRole('heading', { name: 'Entrar na área do cliente' })).toBeVisible();
});
