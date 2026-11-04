import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { renderWithAuth } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { amountOf, UnderwritingRulesDialog } from './UnderwritingRulesDialog';

const manager = staffMe({
  roles: ['PRODUCT_MANAGER'],
  permissions: ['product:read', 'product:manage'],
  orgType: 'INSURER',
});

const defaults = {
  schemaVersion: 1,
  autoAccept: {
    enabled: true,
    simplifiedKycMaxPremium: { amount: 1000000, currency: 'MZN' },
    liability: { requireAllDeclarationsNegative: false },
    ownDamage: {
      maxVehicleValue: { amount: 150000000, currency: 'MZN' },
      requireAllDeclarationsNegative: true,
    },
  },
};

function api(bodies: unknown[] = [], putStatus = 200, putData?: unknown) {
  return signInStaff(manager, (c) => {
    if (is(c, 'GET', '/api/v1/insurer/product-versions/pv1/underwriting-rules')) {
      return { status: 200, data: { isDefault: true, rules: defaults, version: 4 } };
    }
    if (is(c, 'PUT', '/api/v1/insurer/product-versions/pv1/underwriting-rules')) {
      const body = JSON.parse(c.data as string) as { rules: unknown };
      bodies.push(body);
      return {
        status: putStatus,
        data: putData ?? { isDefault: false, rules: body.rules, version: 5 },
      };
    }
    return null;
  });
}

describe('UnderwritingRulesDialog', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('amountOf: centavos, zero e texto inválido', () => {
    expect(amountOf('10 000,50')).toBe(1000050);
    expect(amountOf('0')).toBe('POSITIVE');
    expect(amountOf('')).toBe('POSITIVE');
    expect(amountOf('10,505')).toBe('INVALID_MONEY');
  });

  it('converte 10 000,50 MT em 1000050 centavos e grava com a version', async () => {
    const bodies: unknown[] = [];
    restore = api(bodies);
    const onClose = vi.fn();
    renderWithAuth(<UnderwritingRulesDialog versionId="pv1" editable onClose={onClose} />);
    expect(
      await screen.findByText('Esta versão usa os valores por defeito da plataforma.'),
    ).toBeInTheDocument();
    const premium = screen.getByRole('textbox', {
      name: 'Prémio máximo com KYC simplificado (MT)',
    });
    await waitFor(() => expect((premium as HTMLInputElement).value).toMatch(/^10\s000,00$/));
    await userEvent.clear(premium);
    await userEvent.type(premium, '10 000,50');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(onClose).toHaveBeenCalled());
    expect(bodies).toEqual([
      {
        rules: {
          ...defaults,
          autoAccept: {
            ...defaults.autoAccept,
            simplifiedKycMaxPremium: { amount: 1000050, currency: 'MZN' },
          },
        },
        version: 4,
      },
    ]);
  });

  it('mais de 2 casas decimais é inválido e não grava', async () => {
    const bodies: unknown[] = [];
    restore = api(bodies);
    renderWithAuth(<UnderwritingRulesDialog versionId="pv1" editable onClose={() => {}} />);
    const value = await screen.findByRole('textbox', {
      name: 'Danos próprios: valor máximo do veículo (MT)',
    });
    await userEvent.clear(value);
    await userEvent.type(value, '1500,505');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(
      await screen.findByText('Valor inválido. Use no máximo 2 casas decimais.'),
    ).toBeInTheDocument();
    expect(bodies).toHaveLength(0);
  });

  it('desligar a aceitação automática esconde os outros campos', async () => {
    restore = api();
    renderWithAuth(<UnderwritingRulesDialog versionId="pv1" editable onClose={() => {}} />);
    await userEvent.click(await screen.findByRole('checkbox', { name: 'Aceitação automática' }));
    expect(
      screen.queryByRole('textbox', { name: 'Prémio máximo com KYC simplificado (MT)' }),
    ).not.toBeInTheDocument();
  });

  it('fica só de leitura numa versão publicada', async () => {
    restore = api();
    renderWithAuth(<UnderwritingRulesDialog versionId="pv1" editable={false} onClose={() => {}} />);
    expect(await screen.findByTestId('underwriting-read-only')).toHaveTextContent(
      'As versões publicadas não podem ser alteradas.',
    );
    expect(
      screen.getByRole('textbox', { name: 'Prémio máximo com KYC simplificado (MT)' }),
    ).toBeDisabled();
    expect(screen.getByRole('checkbox', { name: 'Aceitação automática' })).toBeDisabled();
    expect(screen.queryByRole('button', { name: 'Guardar' })).not.toBeInTheDocument();
  });

  it('409 CONCURRENT_MODIFICATION mostra "Outra pessoa alterou esta versão" com "Recarregar"', async () => {
    restore = api([], 409, { code: 'CONCURRENT_MODIFICATION' });
    renderWithAuth(<UnderwritingRulesDialog versionId="pv1" editable onClose={() => {}} />);
    await screen.findByRole('textbox', { name: 'Prémio máximo com KYC simplificado (MT)' });
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(await screen.findByText('Outra pessoa alterou esta versão')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Recarregar' })).toBeInTheDocument();
  });

  it('INVALID_RULES vai para o campo pelo caminho', async () => {
    restore = api([], 400, {
      code: 'VALIDATION_ERROR',
      errors: [
        { field: 'rules.autoAccept.ownDamage.maxVehicleValue.amount', code: 'INVALID_RULES' },
      ],
    });
    renderWithAuth(<UnderwritingRulesDialog versionId="pv1" editable onClose={() => {}} />);
    await screen.findByRole('textbox', { name: 'Prémio máximo com KYC simplificado (MT)' });
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(await screen.findByText('Regra inválida.')).toBeInTheDocument();
  });
});
