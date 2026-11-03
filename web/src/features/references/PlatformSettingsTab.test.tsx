import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { renderWithProviders } from '../../test/render';
import { PlatformSettingsTab } from './PlatformSettingsTab';

describe('PlatformSettingsTab', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('envia "300000000" ao gravar 3 000 000 MT', async () => {
    const bodies: unknown[] = [];
    restore = mockApi((config) => {
      if (config.method === 'get' && config.url === '/api/v1/admin/platform-settings') {
        return {
          status: 200,
          data: {
            settings: [
              {
                key: 'MTPL_MIN_CAPITAL',
                value: '250000000',
                valueType: 'MONEY',
                version: 3,
                updatedAt: '2026-11-01T08:00:00Z',
                updatedBy: 'admin@moseguros.co.mz',
              },
            ],
          },
        };
      }
      if (config.url === '/api/v1/admin/platform-settings/MTPL_MIN_CAPITAL') {
        bodies.push(JSON.parse(config.data as string));
        return { status: 200, data: {} };
      }
      return { status: 404, data: { code: 'NOT_FOUND' } };
    });
    renderWithProviders(<PlatformSettingsTab />);
    const card = await screen.findByRole('region', {
      name: 'Capital mínimo da responsabilidade civil obrigatória',
    });
    expect(card).toHaveTextContent(
      'Usado para validar a publicação de produtos. Os produtos já publicados não mudam.',
    );
    const field = within(card).getByLabelText('Valor');
    expect((field as HTMLInputElement).value.replace(/\s/g, ' ')).toBe('2 500 000,00');

    await userEvent.clear(field);
    await userEvent.type(field, '3 000 000');
    await userEvent.click(within(card).getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(bodies).toEqual([{ value: '300000000', version: 3 }]));
  });

  it('um valor com 3 casas decimais não se grava', async () => {
    const writes: unknown[] = [];
    restore = mockApi((config) => {
      if (config.method === 'get') {
        return {
          status: 200,
          data: {
            settings: [
              {
                key: 'MTPL_MIN_CAPITAL',
                value: '250000000',
                valueType: 'MONEY',
                version: 3,
                updatedAt: '2026-11-01T08:00:00Z',
                updatedBy: 'admin',
              },
            ],
          },
        };
      }
      writes.push(config.data);
      return { status: 200, data: {} };
    });
    renderWithProviders(<PlatformSettingsTab />);
    const field = await screen.findByLabelText('Valor');
    await userEvent.clear(field);
    await userEvent.type(field, '1,234');
    expect(screen.getByText('Valor inválido. Use no máximo 2 casas decimais.')).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(writes).toEqual([]);
  });
});
