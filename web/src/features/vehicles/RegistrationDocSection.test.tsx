import { cleanup, fireEvent, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import type { RegistrationDocResponse, VehicleResponse } from '../../api/generated';
import { mockApi } from '../../test/mockApi';
import { renderWithProviders } from '../../test/render';
import { vehicleFixture } from '../../test/vehicles';
import { RegistrationDocSection } from './RegistrationDocSection';

const MB = 1024 * 1024;

function docOf(contentType: string): RegistrationDocResponse {
  return { fileId: 'f1', contentType, sizeBytes: 812345, uploadedAt: '2026-11-03T10:15:00Z' };
}

function file(name: string, type: string, size: number): File {
  const value = new File(['x'], name, { type });
  Object.defineProperty(value, 'size', { value: size });
  return value;
}

let restore: () => void = () => {};
let requests: InternalAxiosRequestConfig[] = [];

beforeEach(() => {
  requests = [];
  // O jsdom não tem URLs de blobs.
  URL.createObjectURL = vi.fn(() => 'blob:livrete');
  URL.revokeObjectURL = vi.fn();
  restore = mockApi((config) => {
    requests.push(config);
    if (config.method === 'get' && config.url === '/api/v1/files/f1') {
      return { status: 200, data: new Blob(['x'], { type: 'image/jpeg' }) };
    }
    if (config.method === 'post' && config.url === '/api/v1/me/vehicles/v1/registration-doc') {
      return {
        status: 200,
        data: { ...vehicleFixture, registrationDoc: docOf('image/jpeg'), version: 3 },
      };
    }
    return { status: 404, data: { code: 'NOT_FOUND' } };
  });
});

afterEach(() => {
  // Desmonta antes de tirar os URLs (o hook revoga-os ao desmontar).
  cleanup();
  restore();
  Reflect.deleteProperty(URL, 'createObjectURL');
  Reflect.deleteProperty(URL, 'revokeObjectURL');
});

function renderSection(vehicle: VehicleResponse = vehicleFixture) {
  const onUpdated = vi.fn();
  renderWithProviders(<RegistrationDocSection vehicle={vehicle} onUpdated={onUpdated} />);
  return { onUpdated };
}

const choose = (value: File) =>
  fireEvent.change(screen.getByTestId('registration-doc-file'), { target: { files: [value] } });

describe('RegistrationDocSection', () => {
  it('sem livrete mostra o texto e os botões de envio', () => {
    renderSection();
    expect(screen.getByText('Ainda não enviou o livrete.')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Tirar foto' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Escolher ficheiro' })).toBeInTheDocument();
    const camera = screen.getByTestId('registration-doc-camera');
    expect(camera).toHaveAttribute('accept', 'image/*');
    expect(camera).toHaveAttribute('capture', 'environment');
    expect(screen.getByTestId('registration-doc-file')).toHaveAttribute(
      'accept',
      'image/jpeg,image/png,image/heic,application/pdf',
    );
  });

  it('recusa um GIF sem chamar a API', async () => {
    renderSection();
    choose(file('livrete.gif', 'image/gif', 1000));
    expect(await screen.findByRole('alert')).toHaveTextContent(/tipo de ficheiro/i);
    expect(requests).toHaveLength(0);
  });

  it('recusa um GIF tirado com a câmara sem chamar a API', async () => {
    renderSection();
    fireEvent.change(screen.getByTestId('registration-doc-camera'), {
      target: { files: [file('foto.gif', 'image/gif', 1000)] },
    });
    expect(await screen.findByRole('alert')).toBeInTheDocument();
    expect(requests).toHaveLength(0);
  });

  it('recusa um PDF de 11 MB sem chamar a API', async () => {
    renderSection();
    choose(file('livrete.pdf', 'application/pdf', 11 * MB));
    expect(await screen.findByRole('alert')).toHaveTextContent(/10 MB/);
    expect(requests).toHaveLength(0);
  });

  it('envia um JPEG e avisa "Livrete guardado"', async () => {
    const { onUpdated } = renderSection();
    choose(file('livrete.jpg', 'image/jpeg', 2 * MB));
    await waitFor(() => expect(onUpdated).toHaveBeenCalled());
    const upload = requests.find((r) => r.method === 'post');
    expect(upload?.data).toBeInstanceOf(FormData);
    expect((upload?.data as FormData).get('file')).toBeInstanceOf(File);
    expect(await screen.findByText('Livrete guardado')).toBeInTheDocument();
  });

  it('erro FILE_* do servidor mostra a mensagem do código', async () => {
    restore();
    restore = mockApi(() => ({ status: 413, data: { status: 413, code: 'FILE_TOO_LARGE' } }));
    renderSection();
    choose(file('livrete.png', 'image/png', MB));
    expect(await screen.findByRole('alert')).toHaveTextContent(/10 MB/);
  });

  it('JPEG: miniatura e "Abrir"; os botões dizem "Substituir"', async () => {
    renderSection({ ...vehicleFixture, registrationDoc: docOf('image/jpeg') });
    const image = await screen.findByRole('img', { name: 'Foto do livrete' });
    expect(image).toHaveAttribute('src', 'blob:livrete');
    expect(screen.getByRole('button', { name: 'Abrir' })).toBeEnabled();
    expect(screen.getByRole('button', { name: 'Substituir com uma foto' })).toHaveTextContent(
      'Substituir',
    );
    expect(screen.getByRole('button', { name: 'Substituir com um ficheiro' })).toHaveTextContent(
      'Substituir',
    );
  });

  it.each(['application/pdf', 'image/heic'])('%s: sem miniatura, com "Abrir"', async (type) => {
    const open = vi.spyOn(window, 'open').mockReturnValue(null);
    renderSection({ ...vehicleFixture, registrationDoc: docOf(type) });
    const button = screen.getByRole('button', { name: 'Abrir' });
    await waitFor(() => expect(button).toBeEnabled());
    expect(screen.queryByRole('img', { name: 'Foto do livrete' })).not.toBeInTheDocument();
    expect(screen.getByTestId('registration-doc-icon')).toBeInTheDocument();
    expect(screen.getByText(/793 KB/)).toBeInTheDocument();
    await userEvent.click(button);
    expect(open).toHaveBeenCalledWith('blob:livrete', '_blank', 'noopener');
    open.mockRestore();
  });
});
