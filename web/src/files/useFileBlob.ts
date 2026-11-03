import { useQuery } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { downloadFile } from '../api/generated';

export interface FileBlob {
  /** `blob:` URL do ficheiro (revogado ao desmontar). */
  url: string | undefined;
  contentType: string | undefined;
  isLoading: boolean;
  error: unknown;
}

/**
 * Lê um ficheiro da API (`downloadFile`) com o token da sessão (um `<a href>` não o levaria) e
 * devolve um URL local para o mostrar.
 */
export function useFileBlob(fileId: string | null | undefined): FileBlob {
  const query = useQuery({
    queryKey: ['file', fileId],
    queryFn: ({ signal }) => downloadFile(fileId as string, undefined, undefined, signal),
    enabled: !!fileId,
    staleTime: Infinity,
  });
  const blob = query.data;
  const [url, setUrl] = useState<string | undefined>(undefined);

  useEffect(() => {
    if (!blob) {
      setUrl(undefined);
      return;
    }
    const next = URL.createObjectURL(blob);
    setUrl(next);
    return () => URL.revokeObjectURL(next);
  }, [blob]);

  return {
    url,
    contentType: blob?.type || undefined,
    isLoading: query.isLoading,
    error: query.error,
  };
}
