import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { mockApi } from './test/mockApi';
import { renderRoute } from './test/render';

describe('rotas', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('/ mostra o nome, a frase e o estado do sistema', async () => {
    restore = mockApi(() => ({ status: 200, data: { version: '9.9.9', commit: 'deadbee' } }));
    renderRoute('/');
    expect(screen.getByRole('heading', { level: 1, name: 'MoSeguros' })).toBeInTheDocument();
    expect(
      screen.getByText('Compare, compre e gira o seu seguro automóvel num só lugar.'),
    ).toBeInTheDocument();
    expect(await screen.findByText('9.9.9')).toBeInTheDocument();
  });

  it('/seguradora mostra o menu da área e a página em construção', async () => {
    renderRoute('/seguradora/tarifas');
    expect(screen.getByRole('heading', { name: 'Em construção' })).toBeInTheDocument();
    // Sem matchMedia (jsdom) o layout é o de ecrã estreito: o menu abre-se como gaveta.
    await userEvent.click(screen.getByRole('button', { name: 'Abrir menu' }));
    const nav = await screen.findByRole('navigation', { name: 'Seguradora' });
    expect(nav).toHaveTextContent('Tarifas');
    expect(nav).toHaveTextContent('Configuração');
  });

  it.each(['/cliente', '/admin/organizacoes', '/frota/x/y', '/verificar', '/v/abc'])(
    '%s mostra a página em construção',
    (path) => {
      renderRoute(path);
      expect(screen.getByRole('heading', { name: 'Em construção' })).toBeInTheDocument();
    },
  );

  it('um caminho desconhecido mostra 404', () => {
    renderRoute('/xyz');
    expect(screen.getByRole('heading', { name: 'Página não encontrada' })).toBeInTheDocument();
  });
});
