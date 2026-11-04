import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import type { MeResponse, ProductVersionResponse, RatingRulesResponse } from '../../api/generated';
import { renderRoute, renderWithAuth } from '../../test/render';
import { is, signInStaff, type StaffHandler } from '../../test/staff';
import { TariffMissingWarning } from './TariffMissingWarning';
import {
  draftRules,
  draftVersion,
  productSummary,
  productWith,
  publishedRules,
  publishedVersion,
  settings,
  tariffManager,
  tariffReader,
} from './testData';

function api(
  {
    me = tariffManager,
    version = draftVersion,
    rules = draftRules,
    ratingSettings = settings(),
  }: {
    me?: MeResponse;
    version?: ProductVersionResponse;
    rules?: RatingRulesResponse;
    ratingSettings?: ReturnType<typeof settings>;
  },
  ...extra: StaffHandler[]
) {
  return signInStaff(me, ...extra, (c) => {
    if (is(c, 'GET', '/api/v1/insurer/rating-settings')) {
      return { status: 200, data: ratingSettings };
    }
    if (is(c, 'GET', `/api/v1/insurer/product-versions/${version.id}`)) {
      return { status: 200, data: version };
    }
    if (is(c, 'GET', `/api/v1/insurer/product-versions/${version.id}/rating`)) {
      return { status: 200, data: rules };
    }
    if (is(c, 'GET', '/api/v1/insurer/products')) {
      return {
        status: 200,
        data: { content: [productSummary], page: 0, size: 100, totalElements: 1 },
      };
    }
    if (is(c, 'GET', `/api/v1/insurer/products/${version.productId}`)) {
      return { status: 200, data: productWith(version) };
    }
    return null;
  });
}

const ctaButtons = () => document.querySelectorAll('.MuiButton-cta').length;

describe('TariffsPage', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('lista as versões com o estado da tarifa e a acção certa', async () => {
    restore = api({});
    renderRoute('/seguradora/tarifas');
    await userEvent.click(await screen.findByRole('button', { name: /RC Teste/ }));
    const row = await screen.findByTestId(`version-${draftVersion.id}`);
    expect(await within(row).findByText('Sem tarifa')).toBeInTheDocument();
    expect(within(row).getByRole('link', { name: 'Editar tarifa' })).toHaveAttribute(
      'href',
      `/seguradora/tarifas/${draftVersion.id}`,
    );
    expect(ctaButtons()).toBe(0);
  });

  it('seguradora com tarifação externa: aviso no topo', async () => {
    restore = api({ ratingSettings: settings({ ratingMode: 'EXTERNAL' }) });
    renderRoute('/seguradora/tarifas');
    expect(await screen.findByTestId('external-notice')).toHaveTextContent(
      'As cotações desta seguradora são calculadas pelo seu próprio sistema.',
    );
  });

  it('sem rating:manage: "Ver tarifa"', async () => {
    restore = api({ me: tariffReader });
    renderRoute('/seguradora/tarifas');
    await userEvent.click(await screen.findByRole('button', { name: /RC Teste/ }));
    expect(await screen.findByRole('link', { name: 'Ver tarifa' })).toBeInTheDocument();
  });
});

describe('ChargesPanel', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('código repetido e percentagem acima de 100 são recusados', async () => {
    const puts: unknown[] = [];
    restore = api({}, (c) => {
      if (is(c, 'PUT', '/api/v1/insurer/rating-settings')) {
        puts.push(JSON.parse(c.data as string));
        return { status: 200, data: settings({ version: 2 }) };
      }
      return null;
    });
    renderRoute('/seguradora/tarifas?separador=encargos');
    const add = await screen.findByRole('button', { name: 'Acrescentar encargo' });
    await userEvent.click(add);
    await userEvent.click(add);
    for (const index of [0, 1]) {
      const charge = screen.getByTestId(`charge-${index}`);
      await userEvent.type(within(charge).getByLabelText('Código'), 'SELO');
      await userEvent.type(within(charge).getByLabelText('Nome (pt)'), 'Selo');
      await userEvent.type(within(charge).getByLabelText('Nome (en)'), 'Stamp');
      await userEvent.type(within(charge).getByLabelText('Valor'), index === 0 ? '150' : '2,5');
    }
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(
      within(screen.getByTestId('charge-1')).getByText('Encargo repetido.'),
    ).toBeInTheDocument();
    expect(
      within(screen.getByTestId('charge-0')).getByText('Valor fora dos limites permitidos.'),
    ).toBeInTheDocument();
    expect(puts).toHaveLength(0);
  });

  it('valor fixo em MT é enviado em centavos e a percentagem com ponto', async () => {
    const puts: unknown[] = [];
    restore = api({}, (c) => {
      if (is(c, 'PUT', '/api/v1/insurer/rating-settings')) {
        puts.push(JSON.parse(c.data as string));
        return { status: 200, data: settings({ version: 2 }) };
      }
      return null;
    });
    renderRoute('/seguradora/tarifas?separador=encargos');
    const add = await screen.findByRole('button', { name: 'Acrescentar encargo' });
    await userEvent.click(add);
    await userEvent.click(add);
    const [first, second] = [screen.getByTestId('charge-0'), screen.getByTestId('charge-1')];
    await userEvent.type(within(first).getByLabelText('Código'), 'selo');
    await userEvent.type(within(first).getByLabelText('Nome (pt)'), 'Selo');
    await userEvent.type(within(first).getByLabelText('Nome (en)'), 'Stamp');
    await userEvent.type(within(first).getByLabelText('Valor'), '2,5');
    await userEvent.type(within(second).getByLabelText('Código'), 'TAXA');
    await userEvent.type(within(second).getByLabelText('Nome (pt)'), 'Taxa');
    await userEvent.type(within(second).getByLabelText('Nome (en)'), 'Fee');
    await userEvent.click(within(second).getByLabelText('Tipo'));
    await userEvent.click(await screen.findByRole('option', { name: 'Valor fixo' }));
    await userEvent.type(within(second).getByLabelText('Valor'), '50');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(puts).toHaveLength(1));
    expect(puts[0]).toEqual({
      charges: [
        { code: 'SELO', labelPt: 'Selo', labelEn: 'Stamp', type: 'PERCENT', value: '2.5' },
        { code: 'TAXA', labelPt: 'Taxa', labelEn: 'Fee', type: 'FIXED', value: 5000 },
      ],
      version: 1,
    });
    expect(await screen.findByText('Encargos guardados')).toBeInTheDocument();
  });

  it('sem rating:manage os encargos são só de leitura', async () => {
    restore = api({
      me: tariffReader,
      ratingSettings: settings({
        charges: [
          { code: 'SELO', labelPt: 'Selo', labelEn: 'Stamp', type: 'PERCENT', value: '2.5' },
        ],
      }),
    });
    renderRoute('/seguradora/tarifas?separador=encargos');
    expect(await screen.findByLabelText('Código')).toBeDisabled();
    expect(screen.getByLabelText('Valor')).toHaveValue('2,5');
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Acrescentar encargo' })).not.toBeInTheDocument();
  });
});

describe('TariffEditorPage', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('versão PUBLISHED: só de leitura, com exportar e simular', async () => {
    restore = api({ version: publishedVersion, rules: publishedRules });
    renderRoute(`/seguradora/tarifas/${publishedVersion.id}`);
    expect(await screen.findByTestId('read-only-notice')).toBeInTheDocument();
    expect(screen.getByLabelText('Tipo de base')).toHaveAttribute('aria-disabled', 'true');
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Acrescentar linha' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Importar CSV' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Acrescentar factor' })).not.toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Exportar CSV' })).toBeInTheDocument();
    expect(screen.getByRole('tab', { name: 'Simulador' })).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Mais' }));
    expect(screen.getByRole('menuitem', { name: 'Exportar JSON' })).toBeInTheDocument();
    expect(screen.queryByRole('menuitem', { name: 'Importar JSON' })).not.toBeInTheDocument();
  });

  it('sem rating:manage: só de leitura e sem simulador', async () => {
    restore = api({ me: tariffReader });
    renderRoute(`/seguradora/tarifas/${draftVersion.id}`);
    expect(await screen.findByTestId('read-only-notice')).toHaveTextContent(
      'Não tem permissão para alterar tarifas',
    );
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Validar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('tab', { name: 'Simulador' })).not.toBeInTheDocument();
    expect(screen.getByLabelText('Tipo de base')).toHaveAttribute('aria-disabled', 'true');
  });

  it('rascunho sem tarifa: uma linha sem valor; guardar mostra o erro sem chamar a API', async () => {
    const puts: unknown[] = [];
    restore = api({}, (c) => {
      if (c.method?.toUpperCase() === 'PUT') puts.push(c.url);
      return null;
    });
    renderRoute(`/seguradora/tarifas/${draftVersion.id}`);
    expect(await screen.findByText('Sem valor')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(await screen.findByTestId('tariff-errors')).toHaveTextContent(
      'Campo obrigatório. (MTPL › Base › linha 1)',
    );
    expect(screen.getByTestId('row-0-base-0')).toHaveAttribute('data-error', 'true');
    expect(puts).toHaveLength(0);
  });

  it('nenhum botão laranja (cta) no editor nem no simulador', async () => {
    restore = api({ version: publishedVersion, rules: publishedRules });
    renderRoute(`/seguradora/tarifas/${publishedVersion.id}`);
    await screen.findByTestId('read-only-notice');
    expect(ctaButtons()).toBe(0);
    await userEvent.click(screen.getByRole('tab', { name: 'Simulador' }));
    expect(await screen.findByRole('button', { name: 'Calcular' })).toBeInTheDocument();
    expect(ctaButtons()).toBe(0);
    await userEvent.click(screen.getByRole('button', { name: 'Mais' }));
    expect(ctaButtons()).toBe(0);
  });
});

describe('TariffMissingWarning', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  const cases: [string, RatingRulesResponse, 'INTERNAL' | 'EXTERNAL', boolean][] = [
    ['sem tarifa e INTERNAL', draftRules, 'INTERNAL', true],
    ['com tarifa e INTERNAL', { ...draftRules, configured: true }, 'INTERNAL', false],
    ['sem tarifa e EXTERNAL', draftRules, 'EXTERNAL', false],
  ];

  it.each(cases)('%s', async (_, rules, ratingMode, shown) => {
    restore = api({ rules, ratingSettings: settings({ ratingMode }) });
    renderWithAuth(<TariffMissingWarning versionId={draftVersion.id} />);
    if (shown) {
      const alert = await screen.findByTestId('tariff-missing-warning');
      expect(alert).toHaveTextContent('Esta versão ainda não tem tarifa.');
      expect(within(alert).getByRole('link', { name: 'Configurar tarifa' })).toHaveAttribute(
        'href',
        `/seguradora/tarifas/${draftVersion.id}`,
      );
    } else {
      await new Promise((resolve) => setTimeout(resolve, 50));
      expect(screen.queryByTestId('tariff-missing-warning')).not.toBeInTheDocument();
    }
  });
});
