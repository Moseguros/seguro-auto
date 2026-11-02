import { isAxiosError } from 'axios';
import type { ProblemDetail } from './generated';

/**
 * Código de erro a traduzir com a chave i18n `errors.<código>`:
 * o `code` do ProblemDetail (RFC 7807), `NETWORK_ERROR` sem resposta do servidor,
 * ou `UNKNOWN` nos restantes casos.
 */
export function errorCode(error: unknown): string {
  if (isAxiosError<Partial<ProblemDetail>>(error)) {
    if (!error.response) return 'NETWORK_ERROR';
    const code = error.response.data?.code;
    if (typeof code === 'string' && code.length > 0) return code;
  }
  return 'UNKNOWN';
}
