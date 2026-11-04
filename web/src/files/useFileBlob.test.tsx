import { QueryClientProvider } from '@tanstack/react-query';
import { cleanup, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useState } from 'react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { mockApi } from '../test/mockApi';
import { testQueryClient } from '../test/render';
import { ProtectedImage } from './ProtectedImage';
import { useFileBlob } from './useFileBlob';

function Viewer({ id }: { id: string }) {
  const { url } = useFileBlob('f1');
  return <span data-testid={id}>{url ?? ''}</span>;
}

function Two() {
  const [first, setFirst] = useState(true);
  const [second, setSecond] = useState(true);
  return (
    <>
      {first && <Viewer id="a" />}
      {second && <Viewer id="b" />}
      <button onClick={() => setFirst(false)}>tirar a</button>
      <button onClick={() => setSecond(false)}>tirar b</button>
    </>
  );
}

describe('useFileBlob', () => {
  let restore: () => void = () => {};
  let requests = 0;

  beforeEach(() => {
    requests = 0;
    let n = 0;
    URL.createObjectURL = vi.fn(() => `blob:url-${++n}`);
    URL.revokeObjectURL = vi.fn();
  });
  afterEach(() => {
    cleanup();
    restore();
    Reflect.deleteProperty(URL, 'createObjectURL');
    Reflect.deleteProperty(URL, 'revokeObjectURL');
  });

  it('faz um só pedido para dois componentes e revoga o URL quando o último desmonta', async () => {
    restore = mockApi((config) => {
      if (config.url === '/api/v1/files/f1') {
        requests += 1;
        return { status: 200, data: new Blob(['x'], { type: 'image/jpeg' }) };
      }
      return { status: 404 };
    });
    render(
      <QueryClientProvider client={testQueryClient()}>
        <Two />
      </QueryClientProvider>,
    );
    await waitFor(() => expect(screen.getByTestId('a')).toHaveTextContent('blob:url-1'));
    expect(screen.getByTestId('b')).toHaveTextContent('blob:url-1');
    expect(requests).toBe(1);
    expect(URL.createObjectURL).toHaveBeenCalledTimes(1);

    await userEvent.click(screen.getByRole('button', { name: 'tirar a' }));
    await waitFor(() => expect(screen.queryByTestId('a')).not.toBeInTheDocument());
    expect(URL.revokeObjectURL).not.toHaveBeenCalled();

    await userEvent.click(screen.getByRole('button', { name: 'tirar b' }));
    await waitFor(() => expect(URL.revokeObjectURL).toHaveBeenCalledWith('blob:url-1'));
    expect(URL.revokeObjectURL).toHaveBeenCalledTimes(1);
  });

  it('ProtectedImage mostra a imagem e, no erro, o texto de erro', async () => {
    restore = mockApi((config) =>
      config.url === '/api/v1/files/ok'
        ? { status: 200, data: new Blob(['x'], { type: 'image/jpeg' }) }
        : { status: 404, data: { code: 'NOT_FOUND' } },
    );
    render(
      <QueryClientProvider client={testQueryClient()}>
        <ProtectedImage fileId="ok" alt="Frente" />
        <ProtectedImage fileId="nao" alt="Traseira" />
      </QueryClientProvider>,
    );
    expect(await screen.findByRole('img', { name: 'Frente' })).toHaveAttribute('src', 'blob:url-1');
    expect(await screen.findByText('Não foi possível abrir o ficheiro.')).toBeInTheDocument();
    expect(screen.getByRole('img', { name: 'Traseira' })).toBeInTheDocument();
  });
});
