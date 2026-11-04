import { screen, waitFor, within } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { customerMe, signInCustomer } from '../../test/customer';
import { ctaButtons, requestFixture, money } from '../../test/quotes';
import { renderRoute } from '../../test/render';
import { is } from '../../test/staff';

function mockMatchMedia(matches: boolean) {
  vi.stubGlobal(
    'matchMedia',
    vi.fn().mockImplementation((query: string) => ({
      matches,
      media: query,
      onchange: null,
      addListener: vi.fn(),
      removeListener: vi.fn(),
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
      dispatchEvent: vi.fn(),
    })),
  );
}

describe('QuoteComparePage', () => {
  let restore: () => void = () => {};
  beforeEach(() => {
    const request = requestFixture();
    restore = signInCustomer(customerMe(), (config) =>
      is(config, 'GET', '/api/v1/quotes/r1') ? { status: 200, data: request } : undefined,
    );
  });
  afterEach(() => {
    restore();
    vi.unstubAllGlobals();
  });

  it('abaixo de 600 px: cartões empilhados com as mesmas linhas e as diferenças marcadas', async () => {
    mockMatchMedia(false);
    renderRoute('/cliente/simular/r1/comparar?ids=q1,q2');
    const cards = await screen.findAllByTestId('compare-card');
    expect(cards).toHaveLength(2);
    expect(screen.queryByTestId('compare-table')).not.toBeInTheDocument();
    expect(cards[0]).toHaveTextContent('Mais barata');
    expect(cards[1]).not.toHaveTextContent('Mais barata');
    expect(cards[0]).toHaveTextContent(money(5226200));
    // O encargo só existe numa das seguradoras.
    expect(cards[1]).toHaveTextContent('Imposto de selo (provisório)—');
    const different = cards[0]!.querySelectorAll('[data-different]');
    const labels = Array.from(different).map((row) => row.firstChild?.textContent);
    expect(labels).toContain('Prémio total');
    expect(labels).toContain('Choque, colisão e capotamento');
    expect(labels).not.toContain('Responsabilidade civil obrigatória');
    expect(labels).not.toContain('Válida até');
    expect(within(cards[0]!).getAllByRole('link', { name: 'Ver detalhe' })).toHaveLength(1);
    expect(ctaButtons()).toHaveLength(0);
  });

  it('a partir de 600 px: tabela com as linhas diferentes a negrito', async () => {
    mockMatchMedia(true);
    renderRoute('/cliente/simular/r1/comparar?ids=q1,q2');
    const table = await screen.findByTestId('compare-table');
    const header = within(table).getByRole('rowheader', { name: 'Prémio total' });
    expect(header.closest('tr')).toHaveAttribute('data-different', 'true');
    const same = within(table).getByRole('rowheader', {
      name: 'Responsabilidade civil obrigatória',
    });
    expect(same.closest('tr')).not.toHaveAttribute('data-different');
    expect(
      within(table).getAllByRole('button', { name: 'Informação pré-contratual' }),
    ).toHaveLength(2);
    expect(ctaButtons()).toHaveLength(0);
  });

  it('ids inválidos voltam aos resultados', async () => {
    mockMatchMedia(false);
    const { router } = renderRoute('/cliente/simular/r1/comparar?ids=q1');
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/simular/r1'));
    await router.navigate('/cliente/simular/r1/comparar?ids=q1,q3');
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/simular/r1'));
  });
});
