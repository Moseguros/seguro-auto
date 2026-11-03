import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { renderWithProviders } from '../../test/render';
import { PlateFormatDialog } from './PlateFormatDialog';

/** Cola o texto no campo (`type` interpretaria `[` e `{` como teclas especiais). */
async function paste(field: HTMLElement, text: string) {
  await userEvent.click(field);
  await userEvent.paste(text);
}

describe('PlateFormatDialog', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('chama testPlateFormat uma só vez depois de várias teclas seguidas', async () => {
    const bodies: unknown[] = [];
    restore = mockApi((config) => {
      if (config.url === '/api/v1/admin/plate-formats/test') {
        bodies.push(JSON.parse(config.data as string));
        return { status: 200, data: { matches: true, formatted: 'AB-1234' } };
      }
      return { status: 404, data: { code: 'NOT_FOUND' } };
    });
    renderWithProviders(<PlateFormatDialog onClose={() => {}} onSaved={() => {}} />);
    await paste(screen.getByLabelText(/Padrão/), '^(?<l>[A-Z]{2})(?<n>\\d{4})$');
    await paste(screen.getByLabelText(/Modelo de apresentação/), '{l}-{n}');
    await userEvent.type(screen.getByLabelText('Matrícula'), 'ab 1234');

    expect(await screen.findByText('Corresponde: AB-1234')).toBeInTheDocument();
    // Espera mais do que o atraso: não há segundo pedido.
    await new Promise((resolve) => setTimeout(resolve, 600));
    expect(bodies).toEqual([
      { pattern: '^(?<l>[A-Z]{2})(?<n>\\d{4})$', displayTemplate: '{l}-{n}', value: 'ab 1234' },
    ]);
  });

  it('mostra "Não corresponde" e os erros de campo do servidor', async () => {
    restore = mockApi((config) => {
      if (config.url === '/api/v1/admin/plate-formats/test') {
        return { status: 200, data: { matches: false } };
      }
      if (config.url === '/api/v1/admin/plate-formats') {
        return {
          status: 400,
          data: {
            code: 'VALIDATION_ERROR',
            errors: [{ field: 'pattern', code: 'PATTERN_NOT_ANCHORED' }],
          },
        };
      }
      return { status: 404, data: { code: 'NOT_FOUND' } };
    });
    renderWithProviders(<PlateFormatDialog onClose={() => {}} onSaved={() => {}} />);
    await userEvent.type(screen.getByLabelText(/Código/), 'test2');
    await userEvent.type(screen.getByLabelText(/Nome/), 'Teste');
    await paste(screen.getByLabelText(/Padrão/), '(?<l>[A-Z]{2})');
    await paste(screen.getByLabelText(/Modelo de apresentação/), '{l}');
    await userEvent.type(screen.getByLabelText(/Exemplo/), 'AB');
    await userEvent.type(screen.getByLabelText('Matrícula'), '12');
    expect(await screen.findByText('Não corresponde')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    await waitFor(() =>
      expect(
        screen.getByText('A expressão tem de começar por ^ e acabar em $.'),
      ).toBeInTheDocument(),
    );
  });
});
