import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { customerMe, signInCustomer } from '../../test/customer';
import { proposalFixture } from '../../test/proposals';
import { renderRoute } from '../../test/render';
import { is } from '../../test/staff';

let restore: () => void = () => {};
afterEach(() => restore());

const me = customerMe({
  permissions: ['self:profile', 'self:vehicles', 'self:quotes', 'self:proposals'],
});
const profile = { complete: false, version: 1 };

describe('NewProposalPage', () => {
  it('PROFILE_INCOMPLETE → perfil com ?completar=1&voltar=<esta URL>', async () => {
    restore = signInCustomer(
      me,
      (c) => (is(c, 'GET', '/api/v1/me/profile') ? { status: 200, data: profile } : null),
      (c) =>
        is(c, 'POST', '/api/v1/proposals')
          ? { status: 409, data: { code: 'PROFILE_INCOMPLETE' } }
          : null,
    );
    const { router } = renderRoute('/cliente/propostas/nova?cotacao=q1');
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/perfil'));
    const params = new URLSearchParams(router.state.location.search);
    expect(params.get('completar')).toBe('1');
    expect(params.get('voltar')).toBe('/cliente/propostas/nova?cotacao=q1');
  });

  it('PROPOSAL_ALREADY_EXISTS → abre a proposta existente', async () => {
    const existing = proposalFixture({ id: 'p9' });
    restore = signInCustomer(
      me,
      (c) =>
        is(c, 'POST', '/api/v1/proposals')
          ? { status: 409, data: { code: 'PROPOSAL_ALREADY_EXISTS', proposalId: 'p9' } }
          : null,
      (c) => (is(c, 'GET', '/api/v1/proposals/p9') ? { status: 200, data: existing } : null),
    );
    const { router } = renderRoute('/cliente/propostas/nova?cotacao=q1');
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/propostas/p9'));
  });

  it('cria com o canal WEB e abre a proposta nova; cotação expirada → "Simular seguro"', async () => {
    const created = proposalFixture({ id: 'p2' });
    let body: unknown;
    restore = signInCustomer(
      me,
      (c) => {
        if (!is(c, 'POST', '/api/v1/proposals')) return null;
        body = JSON.parse(c.data as string);
        return { status: 201, data: created };
      },
      (c) => (is(c, 'GET', '/api/v1/proposals/p2') ? { status: 200, data: created } : null),
    );
    const { router } = renderRoute('/cliente/propostas/nova?cotacao=q1');
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/propostas/p2'));
    expect(body).toEqual({ quoteId: 'q1', channel: 'WEB' });
    restore();

    restore = signInCustomer(me, (c) =>
      is(c, 'POST', '/api/v1/proposals') ? { status: 409, data: { code: 'QUOTE_EXPIRED' } } : null,
    );
    renderRoute('/cliente/propostas/nova?cotacao=q2');
    const simulate = await screen.findByRole('link', { name: 'Simular seguro' });
    expect(simulate).toHaveAttribute('href', '/cliente/simular');
    expect(simulate).toHaveClass('MuiButton-cta');
  });
});

describe('MyProposalsPage', () => {
  it('a lista vazia tem o botão "Simular seguro" com variant="cta"', async () => {
    let url = '';
    restore = signInCustomer(me, (c) => {
      if (!is(c, 'GET', '/api/v1/me/proposals')) return null;
      url = c.url ?? '';
      return { status: 200, data: { content: [], page: 0, size: 20, totalElements: 0 } };
    });
    renderRoute('/cliente/propostas');
    expect(await screen.findByText('Não tem propostas em curso.')).toBeInTheDocument();
    const simulate = screen.getByRole('link', { name: 'Simular seguro' });
    expect(simulate).toHaveClass('MuiButton-cta');
    expect(simulate).toHaveAttribute('href', '/cliente/simular');
    expect(url).toBe('/api/v1/me/proposals');
    // O único laranja do ecrã.
    expect(document.querySelectorAll('.MuiButton-cta')).toHaveLength(1);
  });

  it('mostra os cartões com "Continuar" nas editáveis e "Ver" nas outras', async () => {
    const summary = (id: string, status: string) => ({
      id,
      number: `PR-2027-00000${id}`,
      status,
      insurer: { id: 'i1', displayName: 'Seguradora Demo' },
      productName: 'Auto Completo',
      productType: 'COMPREHENSIVE',
      vehiclePlate: 'AAA-100-MC',
      total: { amount: 5226200, currency: 'MZN' },
      createdAt: '2027-01-05T09:30:00Z',
      startAt: '2027-01-14T22:00:00Z',
      expiresAt: status === 'DRAFT' ? '2027-01-20T09:30:00Z' : null,
    });
    restore = signInCustomer(me, (c) =>
      is(c, 'GET', '/api/v1/me/proposals')
        ? {
            status: 200,
            data: {
              content: [summary('1', 'DRAFT'), summary('2', 'UNDER_REVIEW')],
              page: 0,
              size: 20,
              totalElements: 2,
            },
          }
        : null,
    );
    renderRoute('/cliente/propostas');
    const cards = await screen.findAllByTestId('proposal-card');
    expect(cards).toHaveLength(2);
    expect(cards[0]).toHaveTextContent('Rascunho');
    expect(cards[0]).toHaveTextContent('Expira em 20/01/27');
    expect(screen.getByRole('link', { name: 'Continuar' })).toHaveAttribute(
      'href',
      '/cliente/propostas/1',
    );
    expect(screen.getByRole('link', { name: 'Ver' })).toHaveAttribute(
      'href',
      '/cliente/propostas/2',
    );
  });
});

describe('ProfilePage ?voltar=', () => {
  const complete = {
    complete: true,
    fullName: 'Ana Maria Mussa',
    birthDate: '1991-09-09',
    idDocType: 'BI',
    idDocNumberMasked: '********123A',
    nuitMasked: '*****6789',
    province: 'MAPUTO_CITY',
    version: 2,
  };

  it.each([
    ['/cliente/propostas/p1?passo=tomador', '/cliente/propostas/p1'],
    ['https://exemplo.com/', '/cliente/perfil'],
  ])('depois de gravar com voltar=%s fica em %s', async (back, expected) => {
    const user = userEvent.setup();
    restore = signInCustomer(me, (c) => {
      if (is(c, 'GET', '/api/v1/me/profile')) return { status: 200, data: complete };
      if (is(c, 'PUT', '/api/v1/me/profile')) return { status: 200, data: complete };
      if (is(c, 'GET', '/api/v1/proposals/p1')) return { status: 200, data: proposalFixture() };
      if (is(c, 'GET', '/api/v1/me/contacts')) return { status: 200, data: { contacts: [] } };
      return null;
    });
    const { router } = renderRoute(`/cliente/perfil?voltar=${encodeURIComponent(back)}`);
    await user.click(await screen.findByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(router.state.location.pathname).toBe(expected));
    if (expected === '/cliente/perfil') {
      expect(await screen.findByText('Perfil guardado')).toBeInTheDocument();
    }
  });
});
