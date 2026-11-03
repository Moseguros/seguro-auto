import { screen } from '@testing-library/react';
import { afterEach, describe, expect, it } from 'vitest';
import i18n from '../i18n';
import { mockApi } from '../test/mockApi';
import { renderWithProviders } from '../test/render';
import { SystemStatusCard } from './SystemStatusCard';

describe('SystemStatusCard', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('mostra a versão e o commit da API', async () => {
    const urls: string[] = [];
    restore = mockApi((config) => {
      urls.push(`${config.baseURL ?? ''}${config.url ?? ''}`);
      return { status: 200, data: { name: 'moseguros', version: '0.1.0', commit: 'abc1234' } };
    });

    renderWithProviders(<SystemStatusCard />);

    expect(screen.getByText('Estado do sistema')).toBeInTheDocument();
    expect(await screen.findByText('0.1.0')).toBeInTheDocument();
    expect(screen.getByText('abc1234')).toBeInTheDocument();
    expect(urls).toEqual(['/api/v1/meta/version']);
  });

  it('mostra o erro traduzido a partir do código do ProblemDetail', async () => {
    restore = mockApi(() => ({
      status: 500,
      data: {
        type: 'about:blank',
        title: 'Internal Server Error',
        status: 500,
        code: 'INTERNAL_ERROR',
      },
    }));

    renderWithProviders(<SystemStatusCard />);

    const alert = await screen.findByRole('alert');
    expect(alert).toHaveTextContent('Não foi possível obter o estado da API.');
    expect(alert).toHaveTextContent('Erro interno do servidor. Tente mais tarde.');
  });

  it('mostra o erro de rede traduzido em inglês', async () => {
    await i18n.changeLanguage('en');
    restore = mockApi(() => 'network-error');

    renderWithProviders(<SystemStatusCard />);

    const alert = await screen.findByRole('alert');
    expect(alert).toHaveTextContent('Could not get the API status.');
    expect(alert).toHaveTextContent('No connection to the server.');
  });
});
