import { httpClient } from '../api/http';

/** Nome do ficheiro no `Content-Disposition` (`filename*=UTF-8''…` ou `filename="…"`). */
export function fileNameFromDisposition(header: unknown): string | undefined {
  if (typeof header !== 'string') return undefined;
  const extended = /filename\*\s*=\s*(?:UTF-8|utf-8)''([^;]+)/.exec(header);
  if (extended?.[1]) {
    try {
      return decodeURIComponent(extended[1].trim().replace(/^"|"$/g, ''));
    } catch {
      // segue para o filename simples
    }
  }
  const plain = /filename\s*=\s*("([^"]*)"|[^;]+)/.exec(header);
  const name = (plain?.[2] ?? plain?.[1])?.trim();
  return name ? name : undefined;
}

/**
 * Descarrega `GET /api/v1/files/{fileId}` pelo `http.ts` (com o `Authorization` da sessão, nunca
 * com o token na URL) e abre o diálogo de gravação do browser com o nome do
 * `Content-Disposition` ou, sem ele, `fileName`. Partilhado com o S5-12, o S6-05 e o S6-06.
 */
export async function downloadFile(fileId: string, fileName: string): Promise<void> {
  const response = await httpClient.get<Blob>(`/api/v1/files/${fileId}`, {
    responseType: 'blob',
  });
  const headers = response.headers as Record<string, unknown> | undefined;
  const name = fileNameFromDisposition(headers?.['content-disposition']) ?? fileName;
  const url = URL.createObjectURL(response.data);
  const link = document.createElement('a');
  link.href = url;
  link.download = name;
  link.rel = 'noopener';
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  link.remove();
  // Alguns browsers só lêem o URL depois do clique: revoga-se mais tarde.
  setTimeout(() => URL.revokeObjectURL(url), 60_000);
}
