import { useEffect, useState } from 'react';
import { downloadFile } from '../api/generated';

export interface FileBlob {
  /** `blob:` URL do ficheiro (revogado ao desmontar ou ao mudar de ficheiro). */
  url?: string;
  contentType?: string;
  isLoading: boolean;
  error?: unknown;
}

/**
 * Descarrega um ficheiro com `downloadFile` (o interceptor do cliente HTTP junta o token, o que
 * um `<a href>` não faria) e devolve um URL local. Sem `fileId` não faz nada.
 */
export function useFileBlob(fileId: string | null | undefined): FileBlob {
  const [state, setState] = useState<FileBlob>({ isLoading: Boolean(fileId) });

  useEffect(() => {
    if (!fileId) {
      setState({ isLoading: false });
      return;
    }
    const controller = new AbortController();
    let url: string | undefined;
    setState({ isLoading: true });
    downloadFile(fileId, undefined, undefined, controller.signal).then(
      (blob) => {
        if (controller.signal.aborted) return;
        url = URL.createObjectURL(blob);
        setState({ url, contentType: blob.type || undefined, isLoading: false });
      },
      (error: unknown) => {
        if (!controller.signal.aborted) setState({ isLoading: false, error });
      },
    );
    return () => {
      controller.abort();
      if (url) URL.revokeObjectURL(url);
    };
  }, [fileId]);

  return state;
}
