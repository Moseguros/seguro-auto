import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { renderWithAuth } from '../../test/render';
import { KycForm } from './KycForm';

let restore: () => void = () => {};
afterEach(() => restore());

const photo = (name: string) => new File([new Uint8Array(10)], name, { type: 'image/jpeg' });

describe('KycForm', () => {
  it('esconde o verso no passaporte e mostra-o no BI', () => {
    const { unmount } = renderWithAuth(
      <KycForm insurerId="i1" idDocType="PASSPORT" onSubmitted={vi.fn()} />,
    );
    expect(screen.getByTestId('kyc-docFront')).toBeInTheDocument();
    expect(screen.queryByTestId('kyc-docBack')).not.toBeInTheDocument();
    unmount();

    renderWithAuth(<KycForm insurerId="i1" idDocType="BI" onSubmitted={vi.fn()} />);
    expect(screen.getByTestId('kyc-docBack')).toBeInTheDocument();
  });

  it('usa ficheiro (capture="user") quando getUserMedia falha e envia as partes', async () => {
    const user = userEvent.setup();
    const calls: InternalAxiosRequestConfig[] = [];
    restore = mockApi((config) => {
      calls.push(config);
      return {
        status: 201,
        data: {
          id: 'k1',
          status: 'PENDING',
          insurerId: 'i1',
          idDocType: 'PASSPORT',
          idDocNumberMasked: '****1234',
          submittedAt: '2027-01-10T08:00:00Z',
        },
      };
    });
    const getUserMedia = vi.fn(() => Promise.reject(new DOMException('denied', 'NotAllowedError')));
    const onSubmitted = vi.fn();
    renderWithAuth(
      <KycForm
        insurerId="i1"
        idDocType="PASSPORT"
        onSubmitted={onSubmitted}
        getUserMedia={getUserMedia}
      />,
    );

    expect(screen.queryByTestId('kyc-file-user')).not.toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Tirar selfie' }));
    await waitFor(() =>
      expect(getUserMedia).toHaveBeenCalledWith({
        video: { facingMode: 'user' },
        audio: false,
      }),
    );
    const selfieInput = await screen.findByTestId('kyc-file-user');
    expect(selfieInput).toHaveAttribute('capture', 'user');
    expect(
      screen.getByText('Não foi possível usar a câmara. Escolha uma fotografia sua.'),
    ).toBeInTheDocument();

    await user.upload(screen.getByTestId('kyc-file-environment'), photo('passaporte.jpg'));
    await user.upload(selfieInput, photo('eu.jpg'));
    // O diálogo da câmara fecha com transição; até lá o resto da página está aria-hidden.
    await user.click(await screen.findByRole('button', { name: 'Enviar' }, { timeout: 5000 }));

    await waitFor(() => expect(onSubmitted).toHaveBeenCalled());
    const body = calls[0]!.data as FormData;
    expect(calls[0]!.url).toBe('/api/v1/me/kyc');
    expect(body.get('insurerId')).toBe('i1');
    expect((body.get('docFront') as File).name).toBe('passaporte.jpg');
    expect((body.get('selfie') as File).name).toBe('eu.jpg');
    expect(body.has('docBack')).toBe(false);
  });

  it('exige o documento e a selfie', async () => {
    const user = userEvent.setup();
    const calls: InternalAxiosRequestConfig[] = [];
    restore = mockApi((config) => {
      calls.push(config);
      return { status: 201, data: {} };
    });
    renderWithAuth(<KycForm insurerId="i1" idDocType="BI" onSubmitted={vi.fn()} />);
    await user.click(screen.getByRole('button', { name: 'Enviar' }));
    expect(await screen.findAllByText('Campo obrigatório.')).toHaveLength(3);
    expect(calls).toHaveLength(0);
  });
});
