import { isAxiosError } from 'axios';

/**
 * Um pedido com `responseType: 'blob'` recebe também o ProblemDetail dos erros como `Blob`: lê-o
 * como JSON para o `getProblem` encontrar o `code` (ex.: `EXPORT_TOO_LARGE`).
 */
export async function readBlobProblem(error: unknown): Promise<unknown> {
  if (!isAxiosError(error) || !error.response) return error;
  const data: unknown = error.response.data;
  if (typeof Blob === 'undefined' || !(data instanceof Blob)) return error;
  try {
    error.response.data = JSON.parse(await data.text()) as unknown;
  } catch {
    // Não é JSON: fica como estava.
  }
  return error;
}
