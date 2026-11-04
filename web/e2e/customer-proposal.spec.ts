import { expect, test, type Page } from '@playwright/test';
import {
  answerDeclarations,
  answerSuitability,
  expectNoHorizontalScroll,
  mockProposalApi,
  open,
  proposalFixture,
  proposalId,
  quoteId,
  quoteRequestFixture,
  requestId,
  signAndSubmit,
} from './proposalMocks';

test.use({ viewport: { width: 360, height: 740 } });

const me = {
  id: '0192f0c4-0000-7000-8000-000000000006',
  type: 'CUSTOMER',
  roles: ['CUSTOMER'],
  permissions: ['self:profile', 'self:vehicles', 'self:quotes', 'self:proposals'],
  preferredLanguage: 'pt',
  msisdn: '+258840000001',
  msisdnVerified: true,
  emailVerified: false,
  profileComplete: true,
  displayName: 'Ana Maria Mussa',
};

const slots = ['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS'];

const photo = (name: string) => ({
  name,
  mimeType: 'image/jpeg',
  buffer: Buffer.from('fotografia de teste'),
});

async function signIn(page: Page) {
  await page.addInitScript(() => {
    window.localStorage.setItem('moseguros.customer.rt', 'refresh-1');
  });
}

/** Da cotação ao primeiro passo da proposta. */
async function acquire(page: Page) {
  await open(page, `/cliente/simular/${requestId}/${quoteId}`);
  await expect(page.getByRole('heading', { name: 'Seguradora Demo' })).toBeVisible();
  await page.getByRole('link', { name: 'Adquirir apólice' }).click();
  await expect(page).toHaveURL(new RegExp(`/cliente/propostas/${proposalId}`));
  await expect(page.getByRole('heading', { name: 'Proposta PR-2026-000123' })).toBeVisible();
}

test('danos próprios: passos, 6 fotografias, aviso confirmado, "Agora não", assinatura e pedido de informação', async ({
  page,
}) => {
  // Fluxo longo (passos, 7 envios de fotografias e duas assinaturas).
  test.setTimeout(120_000);
  const api = await mockProposalApi(page, {
    me,
    quoteRequest: quoteRequestFixture(true, 'CUSTOMER'),
    proposal: proposalFixture({
      comprehensive: true,
      channel: 'WEB',
      policyholderSource: 'PROFILE',
      kycState: 'NONE',
    }),
    submitAs: 'UNDER_REVIEW',
    suitability: 'WARNED',
  });
  await signIn(page);
  await acquire(page);
  expect(api.bodies['POST /api/v1/proposals']).toEqual([{ quoteId, channel: 'WEB' }]);

  // Abre no primeiro passo por concluir (declarações); o tomador é só revisão.
  await answerDeclarations(page);

  // Adequação com aviso confirmado.
  await answerSuitability(page);
  const warnings = page.getByTestId('suitability-warnings');
  await expect(warnings.getByRole('alert')).toHaveCount(1);
  await expect(warnings).toContainText('não quer franquia');
  await expectNoHorizontalScroll(page);
  await page
    .getByRole('button', { name: 'Li os avisos e quero continuar com esta proposta' })
    .click();

  // Vistoria: 6 fotografias e a quilometragem.
  await expect(page.getByText(/· Vistoria$/)).toBeVisible();
  await expect(page.getByRole('button', { name: 'Enviar vistoria' })).toBeDisabled();
  await expect(page.getByRole('link', { name: 'Abrir na aplicação' })).toHaveAttribute(
    'href',
    `moseguros://proposal/${proposalId}/inspection`,
  );
  for (const slot of slots) {
    const card = page.getByTestId(`photo-slot-${slot}`);
    await card.locator('input[type=file]').setInputFiles(photo(`${slot}.jpg`));
    await expect(card.getByRole('img')).toBeVisible();
  }
  await expectNoHorizontalScroll(page);
  await page.getByRole('textbox', { name: 'Quilometragem actual' }).fill('45210');
  await page.getByRole('button', { name: 'Enviar vistoria' }).click();
  expect(api.bodies['POST /api/v1/proposals/' + proposalId + '/inspection/photos']).toHaveLength(6);

  // Identidade: "Agora não".
  await expect(page.getByText(/· Identidade$/)).toBeVisible();
  await expect(
    page.getByText('Verifique a sua identidade para a proposta poder ser aceite logo.', {
      exact: false,
    }),
  ).toBeVisible();
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Agora não' }).click();

  // Resumo, assinatura e envio.
  await signAndSubmit(page, 'Enviámos um código por SMS para +25884****002.');
  await expect(page.getByText('Em análise').first()).toBeVisible();
  await expect(
    page.getByText(/A Seguradora Demo está a analisar a sua proposta\. Tem até/),
  ).toBeVisible();
  await expectNoHorizontalScroll(page);
  expect(api.calls.filter((c) => c.endsWith('/submit') && !c.includes('inspection'))).toHaveLength(
    1,
  );

  // A seguradora pede para repetir a fotografia da frente.
  api.proposal.status = 'INFO_REQUESTED';
  api.proposal.decision = {
    infoRequest: {
      items: ['INSPECTION'],
      message: 'A fotografia da frente está desfocada. Envie outra, por favor.',
      requestedAt: '2026-11-05T09:00:00Z',
    },
  };
  api.proposal.inspection.status = 'REJECTED';
  api.proposal.inspection.review = {
    rejectionReason: 'Fotografia da frente desfocada.',
    retakeSlots: ['FRONT'],
    reviewedAt: '2026-11-05T09:00:00Z',
  };
  api.proposal.checklist.inspection = 'REJECTED';
  api.proposal.checklist.signature = 'MISSING';
  api.proposal.signature = null;
  await open(page, `/cliente/propostas/${proposalId}`);
  const banner = page.getByTestId('info-request');
  await expect(banner).toContainText('A fotografia da frente está desfocada.');
  await expectNoHorizontalScroll(page);
  await banner.getByRole('button', { name: 'Vistoria' }).click();
  await expect(page.getByText(/· Vistoria$/)).toBeVisible();
  await expect(page.getByText(/Fotografia da frente desfocada\./)).toBeVisible();
  const front = page.getByTestId('photo-slot-FRONT');
  await expect(front).toContainText('A seguradora pediu para repetir esta fotografia.');
  await front.locator('input[type=file]').setInputFiles(photo('FRONT-2.jpg'));
  await expect(front.getByRole('button', { name: 'Substituir' })).toBeEnabled();
  await page.getByRole('button', { name: 'Enviar vistoria' }).click();
  await expect(page.getByText(/· Identidade$/)).toBeVisible();
  await page.getByRole('button', { name: 'Continuar' }).click();
  await signAndSubmit(page, 'Enviámos um código por SMS');
  await expect(page.getByText('Em análise').first()).toBeVisible();
  await expect(page.getByTestId('info-request')).toHaveCount(0);
});

test('só RC aceite logo na submissão: "Proposta aceite"', async ({ page }) => {
  const proposal = proposalFixture({
    comprehensive: false,
    channel: 'WEB',
    policyholderSource: 'PROFILE',
    kycState: 'VERIFIED',
  });
  const api = await mockProposalApi(page, {
    me,
    quoteRequest: quoteRequestFixture(false, 'CUSTOMER'),
    proposal,
    submitAs: 'ACCEPTED',
    suitability: 'SUITABLE',
  });
  await signIn(page);
  await acquire(page);

  // Sem vistoria: 5 passos.
  await expect(page.getByText('Passo 2 de 5 · Declarações')).toBeVisible();
  await answerDeclarations(page);
  await answerSuitability(page);
  await expect(page.getByText(/· Identidade$/)).toBeVisible();
  await expect(page.getByText('Identidade verificada')).toBeVisible();
  await page.getByRole('button', { name: 'Continuar' }).click();

  await signAndSubmit(page, 'Enviámos um código por SMS para +25884****002.');
  await expect(page.getByText('Proposta aceite.')).toBeVisible();
  await expect(page.getByText('O pagamento fica disponível em breve.')).toBeVisible();
  await expect(page.getByText(/^Expira em /)).toBeVisible();
  await expect(
    page.getByRole('button', { name: 'Descarregar a proposta assinada (PDF)' }),
  ).toBeVisible();
  await expectNoHorizontalScroll(page);
  expect(api.proposal.status).toBe('ACCEPTED');
});
