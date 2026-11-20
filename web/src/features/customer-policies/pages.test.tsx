import { act, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import type { PolicyDetailResponse } from '../../api/generated';
import { menus } from '../../layout/menus';
import { customerMe, signInCustomer } from '../../test/customer';
import {
  documentsFixture,
  policyDetailFixture,
  policySummaryFixture,
  verificationUrl,
} from '../../test/policies';
import { renderRoute, renderWithProviders } from '../../test/render';
import { is } from '../../test/staff';
import { CertificateCard } from './CertificateCard';

// O QR real é um SVG de módulos: no teste lê-se o valor que recebe.
vi.mock('qrcode.react', () => ({
  QRCodeSVG: (props: { value: string; 'aria-label'?: string }) => (
    <svg data-testid="qr" data-qr-value={props.value} aria-label={props['aria-label']} />
  ),
}));

let restore: () => void = () => {};
afterEach(() => {
  restore();
  vi.useRealTimers();
});

const me = customerMe({ permissions: ['self:profile', 'self:policies'] });
const ctaButtons = () => document.querySelectorAll('.MuiButton-cta');

const page = (content: unknown[], totalElements = content.length) => ({
  content,
  page: 0,
  size: 20,
  totalElements,
});

describe('MyPolicyListPage', () => {
  it('vazio mostra o único botão laranja "Simular seguro" com o caminho do menu', async () => {
    restore = signInCustomer(me, (c) =>
      is(c, 'GET', '/api/v1/me/policies') ? { status: 200, data: page([]) } : null,
    );
    renderRoute('/cliente/apolices');
    const cta = await screen.findByRole('link', { name: 'Simular seguro' }, { timeout: 10_000 });
    expect(cta).toHaveClass('MuiButton-cta');
    expect(cta).toHaveAttribute('href', menus.cliente.find((i) => i.key === 'quote')!.path);
    expect(ctaButtons()).toHaveLength(1);
    expect(screen.getByText('Não tem apólices em vigor.')).toBeVisible();
  });

  it('mostra os cartões, filtra por "Em vigor" e o cartão abre o detalhe', async () => {
    const calls: InternalAxiosRequestConfig[] = [];
    restore = signInCustomer(me, (c) => {
      if (!is(c, 'GET', '/api/v1/me/policies')) return null;
      calls.push(c);
      return { status: 200, data: page([policySummaryFixture()]) };
    });
    renderRoute('/cliente/apolices');
    const card = await screen.findByTestId('policy-card', undefined, { timeout: 10_000 });
    expect(within(card).getByText('AAA-123-MC')).toBeVisible();
    expect(within(card).getByText('Toyota Corolla')).toBeVisible();
    expect(within(card).getByText('Seguradora Demo · RC Auto')).toBeVisible();
    expect(within(card).getByText('Em vigor')).toBeVisible();
    expect(within(card).getByText(/^Válida até 14\/01\/28/)).toBeVisible();
    expect(within(card).getByText('Apólice AP-2027-000123')).toBeVisible();
    expect(card.querySelector('a')).toHaveAttribute('href', '/cliente/apolices/pol-1');
    expect(calls[0]!.params).toMatchObject({ status: ['ACTIVE'], size: 20, sort: 'issuedAt,desc' });

    await userEvent.click(screen.getByRole('tab', { name: 'Todas' }));
    await waitFor(() => expect(calls.length).toBe(2));
    expect(calls[1]!.params.status).toBeUndefined();
  });

  it('erro mostra a mensagem e "Tentar de novo"', async () => {
    let fail = true;
    restore = signInCustomer(me, (c) => {
      if (!is(c, 'GET', '/api/v1/me/policies')) return null;
      return fail
        ? { status: 500, data: { code: 'INTERNAL_ERROR' } }
        : { status: 200, data: page([policySummaryFixture()]) };
    });
    renderRoute('/cliente/apolices');
    const retry = await screen.findByRole('button', { name: 'Tentar de novo' }, { timeout: 10_000 });
    fail = false;
    await userEvent.click(retry);
    expect(await screen.findByTestId('policy-card')).toBeVisible();
  });
});

describe('CertificateCard', () => {
  it('o QR tem o verificationUrl e o certificado pronto descarrega-se', () => {
    renderWithProviders(
      <CertificateCard policy={policyDetailFixture()} onShowFullscreen={() => {}} />,
    );
    expect(screen.getByTestId('qr')).toHaveAttribute('data-qr-value', verificationUrl);
    expect(screen.getByText('Certificado n.º CT-2027-000123')).toBeVisible();
    expect(screen.getByText('Válido de 15/01/27 a 14/01/28')).toBeVisible();
    expect(screen.getByRole('button', { name: 'Mostrar em ecrã inteiro' })).toBeVisible();
    expect(screen.getByRole('button', { name: 'Descarregar certificado' })).toBeVisible();
    expect(screen.queryByText('A preparar o certificado…')).not.toBeInTheDocument();
  });

  it('com o documento PENDING mostra "A preparar…" em vez de descarregar', () => {
    renderWithProviders(
      <CertificateCard
        policy={policyDetailFixture({ documents: documentsFixture('PENDING') })}
        onShowFullscreen={() => {}}
      />,
    );
    expect(screen.getByText('A preparar o certificado…')).toBeVisible();
    expect(screen.queryByRole('button', { name: 'Descarregar certificado' })).toBeNull();
  });

  it('um certificado REVOKED não mostra o QR', () => {
    const policy = policyDetailFixture();
    renderWithProviders(
      <CertificateCard
        policy={{ ...policy, certificate: { ...policy.certificate!, status: 'REVOKED' } }}
        onShowFullscreen={() => {}}
      />,
    );
    expect(screen.getByText('Este certificado foi substituído.')).toBeVisible();
    expect(screen.queryByTestId('qr')).toBeNull();
  });
});

describe('MyPolicyDetailPage', () => {
  function detailHandler(responses: PolicyDetailResponse[], counter: { calls: number }) {
    return (c: InternalAxiosRequestConfig) => {
      if (!is(c, 'GET', '/api/v1/me/policies/pol-1')) return null;
      const data = responses[Math.min(counter.calls, responses.length - 1)];
      counter.calls++;
      return { status: 200, data };
    };
  }

  it('volta a pedir a cada 5 s enquanto houver PENDING e pára quando ficam READY', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const pending = policyDetailFixture({ documents: documentsFixture('PENDING') });
    const counter = { calls: 0 };
    restore = signInCustomer(
      me,
      detailHandler([pending, pending, policyDetailFixture()], counter),
    );
    renderRoute('/cliente/apolices/pol-1');
    expect(
      await screen.findByText('A preparar o certificado…', undefined, { timeout: 10_000 }),
    ).toBeVisible();
    expect(counter.calls).toBe(1);
    await act(() => vi.advanceTimersByTimeAsync(5_100));
    await waitFor(() => expect(counter.calls).toBe(2));
    await act(() => vi.advanceTimersByTimeAsync(5_100));
    await waitFor(() => expect(counter.calls).toBe(3));
    expect(await screen.findByRole('button', { name: 'Descarregar certificado' })).toBeVisible();
    await act(() => vi.advanceTimersByTimeAsync(30_000));
    expect(counter.calls).toBe(3);
  });

  it('pára ao fim de 2 minutos e mostra o aviso', async () => {
    vi.useFakeTimers({ shouldAdvanceTime: true });
    const counter = { calls: 0 };
    restore = signInCustomer(
      me,
      detailHandler([policyDetailFixture({ documents: documentsFixture('PENDING') })], counter),
    );
    renderRoute('/cliente/apolices/pol-1');
    await screen.findByText('A preparar o certificado…', undefined, { timeout: 10_000 });
    for (let i = 0; i < 25; i++) await act(() => vi.advanceTimersByTimeAsync(5_000));
    expect(
      await screen.findByText(
        'Os documentos estão a demorar. Volte a abrir esta página dentro de alguns minutos.',
      ),
    ).toBeVisible();
    const calls = counter.calls;
    expect(calls).toBeLessThanOrEqual(26);
    await act(() => vi.advanceTimersByTimeAsync(30_000));
    expect(counter.calls).toBe(calls);
  });

  it('mostra os blocos e abre o certificado em ecrã inteiro', async () => {
    restore = signInCustomer(me, detailHandler([policyDetailFixture()], { calls: 0 }));
    renderRoute('/cliente/apolices/pol-1');
    expect(
      await screen.findByRole('heading', { name: 'AAA-123-MC' }, { timeout: 10_000 }),
    ).toBeVisible();
    expect(screen.getByTestId('policy-status')).toHaveTextContent('Activa');
    expect(screen.getByText('Anual renovável')).toBeVisible();
    expect(screen.getByText('Pagamento único')).toBeVisible();
    expect(screen.getByText('Responsabilidade civil obrigatória')).toBeVisible();
    expect(screen.getByTestId('document-SCHEDULE')).toHaveTextContent('Condições particulares');

    await userEvent.click(screen.getByRole('button', { name: 'Mostrar em ecrã inteiro' }));
    const dialog = await screen.findByRole('dialog');
    expect(within(dialog).getByTestId('qr')).toHaveAttribute('data-qr-value', verificationUrl);
    expect(within(dialog).getByText('Válido até 14/01/28')).toBeVisible();
    expect(
      within(dialog).getByText('O agente pode ler este código para confirmar o seguro.'),
    ).toBeVisible();
    await userEvent.keyboard('{Escape}');
    await waitFor(() => expect(screen.queryByRole('dialog')).toBeNull());
  });

  it('404 POLICY_NOT_FOUND mostra "Apólice não encontrada" com ligação para a lista', async () => {
    restore = signInCustomer(me, (c) =>
      is(c, 'GET', '/api/v1/me/policies/pol-x')
        ? { status: 404, data: { code: 'POLICY_NOT_FOUND' } }
        : null,
    );
    renderRoute('/cliente/apolices/pol-x');
    expect(
      await screen.findByRole('heading', { name: 'Apólice não encontrada' }, { timeout: 10_000 }),
    ).toBeVisible();
    expect(screen.getByRole('link', { name: 'Ver as minhas apólices' })).toHaveAttribute(
      'href',
      '/cliente/apolices',
    );
  });
});
