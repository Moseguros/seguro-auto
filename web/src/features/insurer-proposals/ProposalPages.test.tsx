import { cleanup, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import {
  insurerProposalFixture,
  kycCheckFixture,
  proposalSummaryFixture,
} from '../../test/insurerProposals';
import { httpClient } from '../../api/http';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';

/** As páginas carregam-se à parte (`lazy`): o primeiro carregamento demora mais. */
const slow = { timeout: 15000 };

const underwriter = staffMe({
  roles: ['UNDERWRITER'],
  permissions: ['proposal:read', 'proposal:decide', 'kyc:review', 'inspection:review'],
  orgType: 'INSURER',
});

/** URL do pedido como o axios o escreve (com o `paramsSerializer` do pedido). */
function fullUrl(c: InternalAxiosRequestConfig): string {
  return httpClient.getUri({ url: c.url, params: c.params, paramsSerializer: c.paramsSerializer });
}

// Grelha, detalhe e páginas `lazy` no jsdom: com os testes em paralelo precisam de mais tempo.
describe('páginas das propostas da seguradora', { timeout: 45_000 }, () => {
  let restore: () => void = () => {};
  beforeEach(() => {
    URL.createObjectURL = vi.fn(() => 'blob:x');
    URL.revokeObjectURL = vi.fn();
  });
  afterEach(() => {
    cleanup();
    restore();
    Reflect.deleteProperty(URL, 'createObjectURL');
    Reflect.deleteProperty(URL, 'revokeObjectURL');
  });

  it('a fila pede "Em análise" pela mais antiga e abre o detalhe', async () => {
    const requests: InternalAxiosRequestConfig[] = [];
    restore = signInStaff(underwriter, (c) => {
      if (is(c, 'GET', '/api/v1/insurer/proposals')) {
        requests.push(c);
        return {
          status: 200,
          data: { content: [proposalSummaryFixture()], page: 0, size: 20, totalElements: 1 },
        };
      }
      if (is(c, 'GET', '/api/v1/insurer/proposals/p1')) {
        return { status: 200, data: insurerProposalFixture() };
      }
      if (c.url?.startsWith('/api/v1/files/')) {
        return { status: 200, data: new Blob(['x'], { type: 'image/jpeg' }) };
      }
      return null;
    });
    const { router } = renderRoute('/seguradora/propostas?sla=ALERT');
    // Sem matchMedia (jsdom) as consultas `max-width` falham: ecrã largo, com a grelha.
    const card = await screen.findByText('PR-2027-000123', undefined, slow);
    expect(within(screen.getByRole('grid')).getByText('Prazo a terminar')).toBeInTheDocument();
    const request = requests.at(-1)!;
    expect(request.params).toMatchObject({
      status: ['UNDER_REVIEW'],
      slaStatus: 'ALERT',
      sort: 'submittedAt,asc',
      page: 0,
      size: 20,
    });
    expect(fullUrl(request)).toContain('status=UNDER_REVIEW&slaStatus=ALERT');

    await userEvent.click(card);
    await waitFor(
      () => expect(router.state.location.pathname).toBe('/seguradora/propostas/p1'),
      slow,
    );
    expect(
      await screen.findByRole('heading', { level: 1, name: 'PR-2027-000123' }, slow),
    ).toBeVisible();
    expect(screen.getByRole('button', { name: 'Descarregar PDF' })).toBeInTheDocument();
    expect(screen.getByTestId('decision-bar')).toBeInTheDocument();

    await userEvent.click(screen.getByRole('link', { name: 'Voltar às propostas' }));
    await waitFor(() => expect(router.state.location.search).toBe('?sla=ALERT'));
  });

  it('o detalhe mostra as declarações, os avisos das regras e a assinatura', async () => {
    restore = signInStaff(underwriter, (c) => {
      if (is(c, 'GET', '/api/v1/insurer/proposals/p1')) {
        return { status: 200, data: insurerProposalFixture() };
      }
      if (c.url?.startsWith('/api/v1/files/')) {
        return { status: 200, data: new Blob(['x'], { type: 'image/jpeg' }) };
      }
      return null;
    });
    renderRoute('/seguradora/propostas/p1');
    await screen.findByRole('heading', { level: 1, name: 'PR-2027-000123' }, slow);
    // Em ecrã largo todas as secções estão abertas.
    expect(
      await screen.findByText(
        'Nos últimos 3 anos, teve algum acidente ou sinistro com um veículo, como condutor ou como proprietário?',
      ),
    ).toBeInTheDocument();
    expect(
      screen.getByText(/O veículo vai ser usado para outro fim além de «Particular»\?/),
    ).toBeInTheDocument();

    expect(await screen.findByText(/Prémio 15\s300,00 MT ≥ limite 10\s000,00 MT/)).toBeVisible();

    expect(await screen.findByText('9f2c…e1')).toBeInTheDocument();

    expect(await screen.findByRole('button', { name: 'Rever identidade' })).toBeInTheDocument();
  });

  it('sem proposal:read nem as outras permissões vai para /sem-acesso', async () => {
    restore = signInStaff(
      staffMe({ roles: ['FINANCE'], permissions: ['payment:read'], orgType: 'INSURER' }),
    );
    const { router } = renderRoute('/seguradora/propostas');
    await waitFor(() => expect(router.state.location.pathname).toBe('/sem-acesso'), slow);
  });

  it('só com inspection:review abre a fila das vistorias', async () => {
    restore = signInStaff(
      staffMe({ roles: ['UNDERWRITER'], permissions: ['inspection:review'], orgType: 'INSURER' }),
      (c) =>
        is(c, 'GET', '/api/v1/insurer/inspections')
          ? { status: 200, data: { content: [], page: 0, size: 20, totalElements: 0 } }
          : null,
    );
    const { router } = renderRoute('/seguradora/propostas');
    await waitFor(
      () => expect(router.state.location.pathname).toBe('/seguradora/propostas/vistorias'),
      slow,
    );
    expect(
      await screen.findByText('Não há vistorias com estes filtros.', undefined, slow),
    ).toBeInTheDocument();
    const tabs = screen.getByRole('tablist');
    expect(within(tabs).getAllByRole('tab')).toHaveLength(1);
  });

  it('a revisão do KYC mostra o número completo e confirma a identidade', async () => {
    const bodies: unknown[] = [];
    restore = signInStaff(underwriter, (c) => {
      if (is(c, 'GET', '/api/v1/insurer/kyc/k1')) {
        return { status: 200, data: kycCheckFixture() };
      }
      if (is(c, 'POST', '/api/v1/insurer/kyc/k1/verify')) {
        bodies.push(JSON.parse(c.data as string));
        return { status: 200, data: kycCheckFixture({ status: 'VERIFIED' }) };
      }
      if (is(c, 'GET', '/api/v1/insurer/kyc')) {
        return { status: 200, data: { content: [], page: 0, size: 20, totalElements: 0 } };
      }
      if (c.url?.startsWith('/api/v1/files/')) {
        return { status: 200, data: new Blob(['x'], { type: 'image/jpeg' }) };
      }
      return null;
    });
    const { router } = renderRoute('/seguradora/propostas/kyc/k1');
    expect(await screen.findByText('110100123456A', undefined, slow)).toBeInTheDocument();
    expect(screen.getByText('Compare estes dados com as fotografias.')).toBeInTheDocument();
    expect(await screen.findByRole('img', { name: 'Selfie' })).toBeInTheDocument();
    await userEvent.click(screen.getByRole('button', { name: 'Confirmar identidade' }));
    const dialog = await screen.findByRole('dialog');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Confirmar identidade' }));
    await waitFor(() => expect(router.state.location.pathname).toBe('/seguradora/propostas/kyc'));
    expect(bodies).toEqual([{ version: 0 }]);
    expect(await screen.findByText('Identidade confirmada', undefined, slow)).toBeInTheDocument();
  });
});
