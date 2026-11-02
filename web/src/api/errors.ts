import { getProblem } from '../errors/problem';

/**
 * Código de erro a traduzir com a chave i18n `errors.<código>`:
 * o `code` do ProblemDetail (RFC 7807), `NETWORK_ERROR` sem resposta do servidor,
 * ou `UNKNOWN` nos restantes casos. Ver {@link getProblem} para o erro completo.
 */
export function errorCode(error: unknown): string {
  return getProblem(error).code;
}
