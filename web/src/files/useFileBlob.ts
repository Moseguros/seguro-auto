import { useQuery } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { downloadFile } from '../api/generated';

export interface FileBlob {
  /** `blob:` URL do ficheiro (revogado quando o último componente que o usa desmonta). */
  url: string | undefined;
  contentType: string | undefined;
  isLoading: boolean;
  error: unknown;
}

/** Chave do ficheiro na cache do TanStack Query (partilhada por todos os componentes). */
export const fileQueryKey = (fileId: string | null | undefined) => ['file', fileId] as const;

interface SharedUrl {
  url: string;
  refs: number;
}

/** Um só `objectURL` por `Blob`, com o número de componentes que o mostram. */
const sharedUrls = new Map<Blob, SharedUrl>();

function acquireUrl(blob: Blob): string {
  let entry = sharedUrls.get(blob);
  if (!entry) {
    entry = { url: URL.createObjectURL(blob), refs: 0 };
    sharedUrls.set(blob, entry);
  }
  entry.refs += 1;
  return entry.url;
}

function releaseUrl(blob: Blob): void {
  const entry = sharedUrls.get(blob);
  if (!entry) return;
  entry.refs -= 1;
  if (entry.refs <= 0) {
    sharedUrls.delete(blob);
    URL.revokeObjectURL(entry.url);
  }
}

/**
 * Lê um ficheiro da API (`downloadFile`) com o token da sessão (um `<a href>` não o levaria) e
 * devolve um URL local para o mostrar. O ficheiro fica na cache (`['file', fileId]`, nunca
 * envelhece: um ficheiro não muda) e o URL é partilhado por todos os componentes com o mesmo
 * ficheiro; só é revogado quando o último desmonta.
 */
export function useFileBlob(fileId: string | null | undefined): FileBlob {
  const query = useQuery({
    queryKey: fileQueryKey(fileId),
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
    setUrl(acquireUrl(blob));
    return () => releaseUrl(blob);
  }, [blob]);

  return {
    url,
    contentType: blob?.type || undefined,
    isLoading: query.isLoading,
    error: query.error,
  };
}
