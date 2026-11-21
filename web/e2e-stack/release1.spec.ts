import { expect, test, type Browser, type BrowserContext, type Page } from '@playwright/test';
import { invitationToken, waitForMail } from './helpers/mailpit';
import { requestDebugOtp, typeSegmentedCode, waitForDebugOtp } from './helpers/otp';
import { StaffTotp } from './helpers/totp';

/**
 * Release 1 de ponta a ponta contra o compose real (S6-08 §5), em série e sem page.route:
 *  1. activa o administrador da plataforma pelo convite do Mailpit (TOTP calculado com o otpauth);
 *  2. cria uma organização POLICE e activa o agente convidado;
 *  3. um cliente novo entra, completa o perfil, confirma o e-mail, regista um veículo e o condutor, simula RC
 *     na Seguradora Demo, cria e assina a proposta (aceite automaticamente);
 *  4. paga por M-Pesa (mpesa-mock, número terminado em 000) → apólice emitida automaticamente (S6-01) com o
 *     certificado pronto, e o e-mail "Pagamento recebido" no Mailpit;
 *  5. abre /v/{código} do QR → "O seguro ainda não começou" (a apólice começa amanhã, ver `startDate`);
 *  6. o agente da polícia entra e verifica a matrícula → o mesmo resultado, com o n.º da apólice;
 *  7. repete 3–4 com outro veículo e pagamento por referência (Ponto24, entidade 90002) pago no ponto24-mock,
 *     lido pelo payment-file-worker do SFTP → apólice emitida.
 *
 * Precisa da base limpa (docker compose down -v): o convite do primeiro administrador só sai se ainda não
 * houver nenhum activo (S1-04).
 */

const PASSWORD = 'palavra-passe-e2e-release1';
const ADMIN_EMAIL = 'admin@moseguros.local';
const POLICE_EMAIL = 'agente.e2e@moseguros.local';
const CUSTOMER_MSISDN = '849000001';
const CUSTOMER_EMAIL = 'cliente.e2e@moseguros.local';
const MPESA_PAYER = '849000000'; // final 000: o mpesa-mock confirma (INS-0) ao fim de 5 s
const PONTO24_MOCK_URL = (process.env.PONTO24_MOCK_URL ?? 'http://localhost:8092').replace(
  /\/$/,
  '',
);

/** Início das apólices: amanhã, mas nunca antes de 01/12/2026 (início dos produtos de demonstração). */
function startDate(): string {
  const tomorrow = new Date(Date.now() + 24 * 3600 * 1000).toLocaleDateString('en-CA', {
    timeZone: 'Africa/Maputo',
  });
  return tomorrow > '2026-12-01' ? tomorrow : '2026-12-01';
}

/**
 * Resultado da verificação da apólice acabada de emitir: o início é sempre amanhã ou depois (`startDate`), por
 * isso a verificação dá `NOT_YET_VALID` e não "Seguro válido".
 */
const NEW_POLICY_COVER = 'O seguro ainda não começou';

async function newPage(browser: Browser): Promise<{ context: BrowserContext; page: Page }> {
  const context = await browser.newContext();
  return { context, page: await context.newPage() };
}

/** Activa um convite de staff (S1-04/S1-08) e devolve o gerador TOTP da conta. */
async function activateInvitation(page: Page, email: string): Promise<StaffTotp> {
  const mail = await waitForMail(page.request, email, /Convite para a MoSeguros/);
  await page.goto(`/convite/${invitationToken(mail)}`);
  await expect(page.getByRole('heading', { name: 'Activar a conta' })).toBeVisible();
  await page.getByLabel('Nova palavra-passe').fill(PASSWORD);
  await page.getByLabel('Confirmar a palavra-passe').fill(PASSWORD);
  await page.getByRole('button', { name: 'Continuar' }).click();
  const secret = (await page.getByTestId('totp-secret').textContent()) ?? '';
  expect(secret.replace(/\s+/g, '')).toMatch(/^[A-Z2-7]+=*$/);
  const totp = new StaffTotp(secret);
  await page.getByLabel('Código de 6 dígitos').fill(await totp.nextCode());
  await page.getByRole('button', { name: 'Activar a conta' }).click();
  await expect(page).not.toHaveURL(/\/convite\//);
  return totp;
}

/** Regista um veículo pelo registo simulado (S2-03) e associa o condutor habitual. */
async function addVehicle(page: Page, plate: string, driverName: string, createDriver: boolean) {
  await page.goto('/cliente/veiculos/novo');
  await page.getByLabel('Matrícula').fill(plate);
  await page.getByRole('button', { name: 'Procurar no registo' }).click();
  await expect(
    page.getByText(
      'Preenchemos os dados a partir do registo automóvel. Confirme-os com o seu livrete.',
    ),
  ).toBeVisible();
  await page.getByLabel('Uso').selectOption('PRIVATE');
  await page.getByLabel('Província de circulação').selectOption('MAPUTO_CITY');
  await page.getByLabel('Valor comercial').fill('650 000');
  await page.getByRole('button', { name: 'Guardar' }).click();
  await expect(page).toHaveURL(/\/cliente\/veiculos\/[0-9a-f-]{36}$/);

  await page.getByRole('button', { name: 'Gerir condutores' }).click();
  if (createDriver) {
    await page.getByRole('link', { name: 'Registar condutor' }).click();
    await page.getByLabel('Sou eu').check();
    await page.getByLabel('N.º da carta').fill('MAP 765 4321');
    await page.getByLabel('B — Ligeiros').check();
    await page.getByLabel('Data de emissão da carta').fill('2010-06-01');
    await page.getByRole('button', { name: 'Guardar' }).click();
  }
  const dialog = page.getByRole('dialog', { name: 'Condutores do veículo' });
  await expect(dialog).toBeVisible();
  await dialog.getByLabel(driverName, { exact: true }).check();
  await dialog.getByRole('radio', { name: `${driverName} é o condutor habitual` }).check();
  await dialog.getByRole('button', { name: 'Guardar' }).click();
  await expect(dialog).toBeHidden();
}

/** Simula RC na Seguradora Demo e abre a proposta (S3-04, S4-08). */
async function simulateAndPropose(page: Page, plate: string): Promise<string> {
  await page.goto('/cliente/simular');
  await expect(page.getByRole('heading', { level: 1, name: 'Simular seguro' })).toBeVisible();
  await page.getByRole('radio', { name: new RegExp(plate) }).click();
  await page.getByRole('button', { name: 'Continuar' }).click();
  await page.getByRole('radio', { name: /^Só responsabilidade civil/ }).click();
  await page.getByRole('button', { name: 'Continuar' }).click();
  // Passos seguintes até ao resumo (as coberturas só aparecem com danos próprios).
  for (let step = 0; step < 4; step++) {
    const start = page.getByLabel('Data de início');
    if (await start.isVisible()) {
      await start.fill(startDate());
    }
    const simulate = page.getByRole('button', { name: 'Simular seguro' });
    if (await simulate.isVisible()) {
      await simulate.click();
      break;
    }
    await page.getByRole('button', { name: 'Continuar' }).click();
  }
  await expect(page).toHaveURL(/\/cliente\/simular\/[0-9a-f-]{36}$/);

  const card = page
    .getByTestId('quote-card')
    .filter({ hasText: 'Seguradora Demo' })
    .filter({ hasNotText: 'Seguradora Demo B' })
    .first();
  await expect(card).toBeVisible({ timeout: 30_000 });
  await card.getByRole('link', { name: 'Ver detalhe' }).click();
  await page.getByRole('link', { name: 'Adquirir apólice' }).click();
  await expect(page).toHaveURL(/\/cliente\/propostas\/[0-9a-f-]{36}/);
  const heading = page.getByRole('heading', { name: /^Proposta PR-\d{4}-\d{6}$/ });
  await expect(heading).toBeVisible();
  return ((await heading.textContent()) ?? '').replace('Proposta ', '').trim();
}

/**
 * Passo actual do assistente da proposta: em ecrãs estreitos "Passo n de m · <passo>", nos largos o passo
 * activo do Stepper (o compose corre em Desktop Chrome, 1280 px).
 */
function stepIndicator(page: Page, name: string) {
  const narrow = page.getByText(new RegExp(`· ${name}$`));
  const wide = page.locator('.MuiStepLabel-label.Mui-active').filter({ hasText: name });
  return narrow.or(wide);
}

async function expectStep(page: Page, name: string) {
  await expect(stepIndicator(page, name)).toBeVisible();
}

/** Declarações, adequação, identidade ("Agora não") e assinatura com o código do X-Debug-Otp (S4-04/S4-05). */
async function completeAndSign(page: Page) {
  await expectStep(page, 'Declarações');
  for (const group of await page.getByRole('radiogroup').all()) {
    await group.getByRole('radio', { name: 'Não' }).check();
  }
  await page.getByRole('checkbox', { name: /Declaro que as respostas/ }).check();
  await page.getByRole('button', { name: 'Continuar' }).click();

  await expectStep(page, 'Adequação');
  for (const group of await page.getByRole('radiogroup').all()) {
    await group.getByRole('radio').first().check();
  }
  await page.getByRole('button', { name: 'Continuar' }).click();
  const warnings = page.getByRole('button', {
    name: 'Li os avisos e quero continuar com esta proposta',
  });
  // Os avisos da adequação aparecem depois da gravação: esperar por eles ou pelo passo seguinte.
  await expect(warnings.or(stepIndicator(page, 'Identidade'))).toBeVisible();
  if (await warnings.isVisible()) {
    await warnings.click();
  }

  await expectStep(page, 'Identidade');
  const notNow = page.getByRole('button', { name: 'Agora não' });
  await expect(notNow.or(page.getByRole('button', { name: 'Continuar' })).first()).toBeVisible();
  if (await notNow.isVisible()) {
    await notNow.click();
  } else {
    await page.getByRole('button', { name: 'Continuar' }).click();
  }

  await expectStep(page, 'Resumo e assinatura');
  await page
    .getByRole('checkbox', {
      name: 'Recebi e li as Condições Gerais e a Informação Pré-contratual.',
    })
    .check();
  const code = await requestDebugOtp(page, /\/sign\/request$/, () =>
    page.getByRole('button', { name: 'Assinar e enviar à seguradora' }).click(),
  );
  const dialog = page.getByRole('dialog');
  await dialog.locator('input[autocomplete="one-time-code"]').fill(code);
  // Regras por defeito (underwriting-v1-defaults.json): RC até 10 000 MT é aceite logo.
  await expect(page.getByText('Proposta aceite.')).toBeVisible({ timeout: 30_000 });
}

/** Do detalhe da proposta aceite ao ecrã de pagamento com a forma escolhida (S5-11). */
async function choosePayment(page: Page, method: 'M-Pesa' | 'Entidade e referência') {
  await page.getByRole('link', { name: 'Adquirir apólice' }).click();
  await expect(page).toHaveURL(/\/pagar$/);
  await expect(
    page.getByText(
      'O pagamento é feito directamente à Seguradora Demo. A MoSeguros não recebe dinheiro.',
    ),
  ).toBeVisible();
  await page.getByRole('radio', { name: method }).check();
}

/** Espera pela apólice emitida automaticamente e pelo certificado pronto; devolve o n.º e o URL do QR. */
async function waitForPolicy(page: Page, plate: string, timeoutMs: number) {
  const card = page.getByTestId('policy-card').filter({ hasText: plate });
  await expect
    .poll(
      async () => {
        await page.goto('/cliente/apolices?estado=todas');
        await page.getByRole('heading', { name: 'As minhas apólices' }).waitFor();
        return card.count();
      },
      { timeout: timeoutMs, intervals: [5_000], message: `apólice de ${plate}` },
    )
    .toBeGreaterThan(0);
  const number = /AP-\d{4}-\d{6}/.exec((await card.first().textContent()) ?? '')?.[0];
  expect(number, 'n.º da apólice no cartão').toBeTruthy();
  await card.first().click();
  await expect(page.getByRole('heading', { name: plate })).toBeVisible();
  // O detalhe volta a ler de 5 em 5 s enquanto os documentos estão PENDING (S6-06).
  await expect(page.getByText('A preparar o certificado…')).toBeHidden({ timeout: 120_000 });
  const qr = page.getByTestId('certificate-qr');
  await expect(qr).toBeVisible();
  const verificationUrl = (await qr.getAttribute('data-value')) ?? '';
  expect(verificationUrl).toMatch(/\/v\/[A-Za-z0-9_-]{22}$/);
  return { number: number!, verificationPath: new URL(verificationUrl).pathname };
}

test('Release 1: convites, compra de RC por M-Pesa e por referência, verificação pública e da polícia', async ({
  browser,
}) => {
  // 1. Administrador da plataforma.
  const admin = await newPage(browser);
  await activateInvitation(admin.page, ADMIN_EMAIL);
  await expect(admin.page).toHaveURL(/\/admin$/);

  // 2. Organização POLICE com o agente como primeiro gestor (ORG_ADMIN + POLICE_OFFICER).
  await admin.page.goto('/admin/organizacoes/nova');
  await expect(admin.page.getByRole('heading', { name: 'Nova organização' })).toBeVisible();
  await admin.page.getByLabel('Tipo').selectOption('POLICE');
  await admin.page.getByLabel('NUIT').fill('500600700');
  await admin.page.getByLabel('Nome legal').fill('Polícia de Trânsito de Teste');
  await admin.page.getByLabel('E-mail de contacto').fill('transito.e2e@moseguros.local');
  const manager = admin.page.getByRole('region', { name: 'Primeiro gestor' });
  await manager.getByLabel('Nome').fill('Agente Teste');
  await manager.getByLabel('E-mail').fill(POLICE_EMAIL);
  await admin.page.getByRole('button', { name: 'Criar organização' }).click();
  await expect(admin.page).toHaveURL(/\/admin\/organizacoes\/[0-9a-f-]{36}$/);
  await expect(admin.page.getByTestId(`invitation-row-${POLICE_EMAIL}`)).toContainText('Pendente');

  const police = await newPage(browser);
  const policeTotp = await activateInvitation(police.page, POLICE_EMAIL);
  await police.context.clearCookies();

  // 3. Cliente novo: login por código, perfil, e-mail confirmado, veículo e condutor.
  const customer = await newPage(browser);
  const page = customer.page;
  await page.goto('/cliente/entrar');
  await expect(page.getByRole('heading', { name: 'Entrar na área do cliente' })).toBeVisible();
  await page.getByLabel('Número de telemóvel').fill(CUSTOMER_MSISDN);
  const loginCode = waitForDebugOtp(page, '/api/v1/auth/otp/request');
  await page.getByRole('button', { name: 'Receber código' }).click();
  await typeSegmentedCode(page, await loginCode);

  await expect(
    page.getByText('Precisamos destes dados para emitir as suas apólices.'),
  ).toBeVisible();
  await page.getByLabel('Nome completo').fill('Teresa Release Uno');
  await page.getByLabel('Data de nascimento').fill('1988-03-21');
  await page.getByLabel('Tipo de documento').selectOption('BI');
  await page.getByLabel('Número do documento').fill('110100765432B');
  await page.getByLabel('NUIT').fill('187654321');
  await page.getByLabel('Província').selectOption('MAPUTO_CITY');
  await page.getByRole('button', { name: 'Guardar' }).click();
  await expect(page.getByRole('heading', { name: 'Olá, Teresa' })).toBeVisible();

  // O e-mail confirmado recebe o "Pagamento recebido" (D36).
  await page.goto('/cliente/perfil');
  await page.getByRole('button', { name: 'Acrescentar e-mail' }).click();
  const contactDialog = page.getByRole('dialog');
  await contactDialog.getByLabel('E-mail').fill(CUSTOMER_EMAIL);
  const contactCode = waitForDebugOtp(page, '/api/v1/me/contacts');
  await contactDialog.getByRole('button', { name: 'Receber código' }).click();
  await typeSegmentedCode(page, await contactCode);
  await expect(page.getByText('Contacto confirmado')).toBeVisible();

  await addVehicle(page, 'AAA-100-MC', 'Teresa Release Uno', true);

  // Simulação, proposta e assinatura.
  const proposalNumber = await simulateAndPropose(page, 'AAA-100-MC');
  await completeAndSign(page);

  // 4. M-Pesa → PaymentSucceeded → proposta PAID → apólice emitida (S6-01, D36).
  await choosePayment(page, 'M-Pesa');
  await page.getByRole('textbox', { name: /Número M-Pesa/ }).fill(MPESA_PAYER);
  await page.getByRole('button', { name: 'Adquirir apólice' }).click();
  await expect(page).toHaveURL(/\/cliente\/pagamentos\/[0-9a-f-]{36}$/);
  await expect(page.getByText('Pagamento confirmado')).toBeVisible({ timeout: 60_000 });

  const mpesaPolicy = await waitForPolicy(page, 'AAA-100-MC', 60_000);
  await waitForMail(
    page.request,
    CUSTOMER_EMAIL,
    new RegExp(`Pagamento recebido — proposta ${proposalNumber}`),
  );

  // 5. Verificação pública pelo código do QR.
  const visitor = await newPage(browser);
  await visitor.page.goto(mpesaPolicy.verificationPath);
  await expect(visitor.page.getByRole('heading', { name: NEW_POLICY_COVER })).toBeVisible();

  // 6. Agente da polícia: entra e verifica a matrícula.
  await police.page.goto(`/entrar?voltar=${encodeURIComponent('/verificar')}`);
  await expect(police.page.getByRole('heading', { name: 'Entrar na MoSeguros' })).toBeVisible();
  await police.page.getByLabel('E-mail').fill(POLICE_EMAIL);
  await police.page.getByLabel('Palavra-passe', { exact: true }).fill(PASSWORD);
  await police.page
    .getByLabel('Código da aplicação de autenticação')
    .fill(await policeTotp.nextCode());
  await police.page.getByRole('button', { name: 'Entrar' }).click();
  await expect(police.page.getByRole('button', { name: 'Menu da conta' })).toBeVisible();
  await police.page.goto('/verificar');
  await police.page.getByLabel('Matrícula').fill('AAA-100-MC');
  await police.page.getByRole('button', { name: 'Verificar' }).click();
  await expect(police.page.getByRole('heading', { name: NEW_POLICY_COVER })).toBeVisible();
  await expect(police.page.getByText(mpesaPolicy.number)).toBeVisible();

  // 7. Outro veículo, pagamento por entidade e referência (Ponto24) pago no ponto24-mock.
  await addVehicle(page, 'MLA-12-34', 'Teresa Release Uno', false);
  await simulateAndPropose(page, 'MLA-12-34');
  await completeAndSign(page);
  await choosePayment(page, 'Entidade e referência');
  await page.getByRole('button', { name: 'Adquirir apólice' }).click();
  await expect(page.getByTestId('reference-entity')).toHaveText('90002', { timeout: 30_000 });
  const reference = ((await page.getByTestId('reference-reference').textContent()) ?? '').replace(
    /\D/g,
    '',
  );
  const amountCentavos = Number(
    ((await page.getByTestId('reference-amount').textContent()) ?? '').replace(/\D/g, ''),
  );
  expect(reference).toMatch(/^\d{11}$/);
  expect(amountCentavos).toBeGreaterThan(0);

  const paid = await page.request.post(`${PONTO24_MOCK_URL}/payments`, {
    data: { entity: '90002', reference, amountCentavos },
  });
  expect(paid.status(), await paid.text()).toBe(201);
  const settled = await page.request.post(`${PONTO24_MOCK_URL}/settlements/run?entity=90002`);
  expect(settled.ok(), await settled.text()).toBeTruthy();

  // O worker lê o SFTP de minuto a minuto e publica no RabbitMQ; o backend confirma e emite a apólice.
  const referencePolicy = await waitForPolicy(page, 'MLA-12-34', 180_000);
  expect(referencePolicy.number).not.toBe(mpesaPolicy.number);

  await Promise.all([admin, police, customer, visitor].map(({ context }) => context.close()));
});
