import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import type { SignatureRequestResponse } from '../../api/generated';
import { mockApi } from '../../test/mockApi';
import { proposalFixture } from '../../test/proposals';
import { renderWithAuth } from '../../test/render';
import { SignDialog } from './SignDialog';

let restore: () => void = () => {};
afterEach(() => restore());

const signRequest = (
  overrides: Partial<SignatureRequestResponse> = {},
): SignatureRequestResponse => ({
  challengeId: 'ch1',
  channel: 'SMS',
  destinationMasked: '+25884****001',
  documentFileId: 'f-pdf',
  documentHash: 'abc',
  expiresAt: '2027-01-10T08:10:00Z',
  fallbackUsed: false,
  resendAfterSeconds: 0,
  ...overrides,
});

const otpOptions = {
  codeLength: 6,
  customerCanChoose: false,
  channels: { MSISDN: ['SMS'] },
  defaultChannels: { MSISDN: 'SMS' },
  identifierTypes: ['MSISDN'],
  resendAfterSeconds: 60,
};

function api(calls: InternalAxiosRequestConfig[]) {
  const signed = proposalFixture({
    version: 8,
    checklist: { ...proposalFixture().checklist, signature: 'DONE' },
  });
  const submitted = proposalFixture({ version: 9, status: 'UNDER_REVIEW' });
  return mockApi((config) => {
    calls.push(config);
    if (config.url === '/api/v1/auth/otp/options') return { status: 200, data: otpOptions };
    if (config.url === '/api/v1/proposals/p1/sign/confirm') return { status: 200, data: signed };
    if (config.url === '/api/v1/proposals/p1/submit') return { status: 200, data: submitted };
    if (config.url === '/api/v1/proposals/p1/sign/request') {
      return { status: 200, data: signRequest({ challengeId: 'ch2' }) };
    }
    return { status: 404, data: { code: 'NOT_FOUND' } };
  });
}

async function typeCode(user: ReturnType<typeof userEvent.setup>) {
  const boxes = await screen.findAllByRole('textbox');
  await user.click(boxes[0]!);
  await user.paste('123456');
}

describe('SignDialog', () => {
  it('chama confirmProposalSignature e depois submitProposal (com o version novo)', async () => {
    const user = userEvent.setup();
    const calls: InternalAxiosRequestConfig[] = [];
    restore = api(calls);
    const onSubmitted = vi.fn();
    renderWithAuth(
      <SignDialog
        open
        mode="customer"
        proposal={proposalFixture()}
        request={signRequest()}
        onClose={vi.fn()}
        onSubmitted={onSubmitted}
        onSubmitFailed={vi.fn()}
      />,
    );
    expect(screen.getByTestId('sign-sent-to')).toHaveTextContent(
      'Enviámos um código por SMS para +25884****001.',
    );
    await typeCode(user);
    await waitFor(() => expect(onSubmitted).toHaveBeenCalled());
    const writes = calls.filter((c) => c.method === 'post').map((c) => c.url);
    expect(writes).toEqual(['/api/v1/proposals/p1/sign/confirm', '/api/v1/proposals/p1/submit']);
    expect(JSON.parse(calls.find((c) => c.url?.endsWith('/sign/confirm'))!.data as string)).toEqual(
      {
        challengeId: 'ch1',
        code: '123456',
      },
    );
    expect(JSON.parse(calls.find((c) => c.url?.endsWith('/submit'))!.data as string)).toEqual({
      version: 8,
    });
    expect(onSubmitted.mock.calls[0]![0]).toMatchObject({ status: 'UNDER_REVIEW' });
  });

  it('o reenvio chama requestProposalSignature e o código seguinte usa o pedido novo', async () => {
    const user = userEvent.setup();
    const calls: InternalAxiosRequestConfig[] = [];
    restore = api(calls);
    renderWithAuth(
      <SignDialog
        open
        mode="customer"
        proposal={proposalFixture()}
        request={signRequest()}
        onClose={vi.fn()}
        onSubmitted={vi.fn()}
        onSubmitFailed={vi.fn()}
      />,
    );
    await user.click(await screen.findByRole('button', { name: 'Reenviar código' }));
    await screen.findByText('Enviámos um código novo.');
    const resend = calls.find((c) => c.url === '/api/v1/proposals/p1/sign/request');
    expect(JSON.parse(resend!.data as string)).toEqual({ documentsAcknowledged: true, version: 3 });
    await typeCode(user);
    await waitFor(() =>
      expect(calls.some((c) => c.url === '/api/v1/proposals/p1/sign/confirm')).toBe(true),
    );
    const confirm = calls.find((c) => c.url === '/api/v1/proposals/p1/sign/confirm');
    expect(JSON.parse(confirm!.data as string)).toMatchObject({ challengeId: 'ch2' });
  });

  it('o texto do mediador é diferente do do cliente', () => {
    restore = api([]);
    renderWithAuth(
      <SignDialog
        open
        mode="broker"
        proposal={proposalFixture()}
        request={signRequest()}
        onClose={vi.fn()}
        onSubmitted={vi.fn()}
        onSubmitFailed={vi.fn()}
      />,
    );
    expect(screen.getByTestId('sign-sent-to')).toHaveTextContent(
      'Enviámos um código para o telemóvel do cliente (+25884****001). Peça-lhe que o diga.',
    );
  });

  it('assinada mas com a submissão recusada avisa o pai', async () => {
    const user = userEvent.setup();
    const signed = proposalFixture({ version: 8 });
    restore = mockApi((config) => {
      if (config.url === '/api/v1/auth/otp/options') return { status: 200, data: otpOptions };
      if (config.url?.endsWith('/sign/confirm')) return { status: 200, data: signed };
      return { status: 409, data: { code: 'INSURER_NOT_ACTIVE' } };
    });
    const onSubmitFailed = vi.fn();
    renderWithAuth(
      <SignDialog
        open
        mode="customer"
        proposal={proposalFixture()}
        request={signRequest()}
        onClose={vi.fn()}
        onSubmitted={vi.fn()}
        onSubmitFailed={onSubmitFailed}
      />,
    );
    await typeCode(user);
    await waitFor(() => expect(onSubmitFailed).toHaveBeenCalled());
    expect(onSubmitFailed.mock.calls[0]![0]).toBe(signed);
    expect(onSubmitFailed.mock.calls[0]![1]).toMatchObject({ code: 'INSURER_NOT_ACTIVE' });
  });
});
