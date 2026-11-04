import { expect, test } from '@playwright/test';
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

const broker = {
  id: '0192f0c4-0000-7000-8000-000000000010',
  type: 'STAFF',
  roles: ['BROKER'],
  permissions: ['quote:create-for-client', 'proposal:create-for-client'],
  preferredLanguage: 'pt',
  emailVerified: true,
  msisdnVerified: false,
  displayName: 'Carlos Corretor',
  email: 'carlos@corretor.co.mz',
  organization: { id: 'o-broker', displayName: 'Corretora Demo', type: 'BROKER' },
};

test('mediador: veículo e condutor, perfil incompleto, tomador, passos e assinatura assistida', async ({
  page,
}) => {
  test.setTimeout(90_000);
  const proposal = proposalFixture({
    comprehensive: false,
    channel: 'BROKER',
    policyholderSource: 'BROKER',
    kycState: 'NONE',
  });
  const api = await mockProposalApi(page, {
    me: broker,
    quoteRequest: quoteRequestFixture(false, 'BROKER'),
    proposal,
    submitAs: 'UNDER_REVIEW',
    suitability: 'SUITABLE',
    createForClient: (body) =>
      body.policyholder
        ? { status: 201, body: proposal }
        : {
            status: 400,
            body: {
              type: 'about:blank',
              title: 'VALIDATION_ERROR',
              status: 400,
              code: 'VALIDATION_ERROR',
              errors: [{ field: 'policyholder', code: 'REQUIRED' }],
            },
          },
  });
  await page.addInitScript(() => sessionStorage.setItem('moseguros.staff.rt', 'refresh-1'));

  await open(page, `/mediador/cotacoes/${requestId}/${quoteId}`);
  await expect(page.getByRole('heading', { name: 'Seguradora Demo' })).toBeVisible();
  await page.getByRole('link', { name: 'Adquirir apólice' }).click();
  await expect(page).toHaveURL(
    new RegExp(`/mediador/propostas/nova\\?pedido=${requestId}&cotacao=${quoteId}$`),
  );

  // Os campos da simulação ficam só de leitura.
  await expect(
    page.getByText(
      'Estes dados foram usados na simulação. Para os mudar, faça uma nova simulação.',
    ),
  ).toBeVisible();
  for (const name of ['Matrícula', 'Ano', 'Categoria', 'Uso', 'Cilindrada (cm³)']) {
    await expect(page.getByRole('textbox', { name, exact: true })).toHaveAttribute('readonly', '');
  }
  await expect(page.getByRole('textbox', { name: 'Matrícula', exact: true })).toHaveValue(
    'AAA-100-MC',
  );
  await expectNoHorizontalScroll(page);
  await page.getByRole('textbox', { name: 'N.º de chassis' }).fill('JTDBR32E720012345');
  await page.getByRole('textbox', { name: 'Marca' }).fill('Toyota');
  await page.getByRole('textbox', { name: 'Modelo' }).fill('Corolla');
  await page.getByRole('checkbox', { name: 'O condutor é o tomador' }).check();
  await page.getByRole('textbox', { name: 'Nome completo' }).fill('João Carlos Sitoe');
  await page.getByRole('textbox', { name: 'N.º da carta' }).fill('MAP-123456');
  await page.getByRole('button', { name: 'Continuar' }).click();

  // Perfil incompleto: o tomador, com o nome e a data de nascimento do condutor sugeridos.
  await expect(
    page.getByText('O perfil deste cliente está incompleto. Preencha os dados do tomador.'),
  ).toBeVisible();
  const holder = page.locator('form#broker-policyholder');
  await expect(holder.getByRole('textbox', { name: 'Nome completo' })).toHaveValue(
    'João Carlos Sitoe',
  );
  await expect(holder.getByLabel('Data de nascimento')).toHaveValue('1991-09-09');
  await holder.getByRole('combobox', { name: 'Tipo de documento' }).selectOption('BI');
  await holder.getByRole('textbox', { name: 'Número do documento' }).fill('110100765432B');
  await holder.getByRole('textbox', { name: 'NUIT' }).fill('123456789');
  await holder.getByRole('combobox', { name: 'Província' }).selectOption('MAPUTO');
  await expectNoHorizontalScroll(page);
  await page.getByRole('button', { name: 'Continuar' }).click();

  await expect(page).toHaveURL(new RegExp(`/mediador/propostas/${proposalId}`));
  const created = api.bodies['POST /api/v1/broker/proposals']!;
  expect(created).toHaveLength(2);
  expect(created[0].policyholder).toBeUndefined();
  expect(created[1]).toMatchObject({
    quoteId,
    vehicle: { plate: 'AAA-100-MC', chassisNumber: 'JTDBR32E720012345', year: 2019 },
    driver: { fullName: 'João Carlos Sitoe', licenceNumber: 'MAP-123456', isOwner: true },
    policyholder: { idDocType: 'BI', nuit: '123456789', province: 'MAPUTO' },
  });

  // Passos em nome do cliente.
  await answerDeclarations(page);
  await answerSuitability(page);
  await expect(page.getByText(/· Identidade$/)).toBeVisible();
  await expect(
    page.getByText(
      'O cliente envia as fotografias na aplicação MoSeguros ou em /cliente/identidade.',
    ),
  ).toBeVisible();
  await page.getByRole('button', { name: 'Continuar' }).click();

  await signAndSubmit(page, 'Enviámos um código para o telemóvel do cliente');
  await expect(page.getByText('Em análise').first()).toBeVisible();
  await expectNoHorizontalScroll(page);
  expect(api.proposal.signature.assisted).toBe(true);
});
