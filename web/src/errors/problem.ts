import { isAxiosError } from 'axios';
import type { TFunction } from 'i18next';
import type { FieldError } from '../api/generated';

/** Erro da API (`application/problem+json`, RFC 7807) já lido. */
export interface Problem {
  /** Estado HTTP; 0 sem resposta do servidor. */
  status: number;
  /** `code` do ProblemDetail, `NETWORK_ERROR` sem resposta ou `UNKNOWN`. */
  code: string;
  detail?: string;
  errors: FieldError[];
  retryAfterSeconds?: number;
  attemptsRemaining?: number;
}

function asNumber(value: unknown): number | undefined {
  if (typeof value === 'number' && Number.isFinite(value)) return value;
  if (typeof value === 'string' && value.trim() !== '' && Number.isFinite(Number(value))) {
    return Number(value);
  }
  return undefined;
}

export function getProblem(error: unknown): Problem {
  if (!isAxiosError(error)) return { status: 0, code: 'UNKNOWN', errors: [] };
  const response = error.response;
  if (!response) return { status: 0, code: 'NETWORK_ERROR', errors: [] };
  const data: Record<string, unknown> =
    response.data && typeof response.data === 'object'
      ? (response.data as Record<string, unknown>)
      : {};
  const code = typeof data.code === 'string' && data.code.length > 0 ? data.code : 'UNKNOWN';
  const errors = Array.isArray(data.errors)
    ? (data.errors as unknown[]).filter(
        (e): e is FieldError =>
          !!e &&
          typeof e === 'object' &&
          typeof (e as FieldError).field === 'string' &&
          typeof (e as FieldError).code === 'string',
      )
    : [];
  const headerRetry = asNumber(
    (response.headers as Record<string, unknown> | undefined)?.['retry-after'],
  );
  return {
    status: response.status,
    code,
    detail: typeof data.detail === 'string' ? data.detail : undefined,
    errors,
    retryAfterSeconds: asNumber(data.retryAfterSeconds) ?? headerRetry,
    attemptsRemaining: asNumber(data.attemptsRemaining),
  };
}

/** Mensagem para o utilizador: `errors.<code>`, ou `errors.UNKNOWN` se a chave não existir. */
export function problemMessage(
  t: TFunction,
  problem: Problem,
  values?: Record<string, unknown>,
): string {
  const key = `errors.${problem.code}`;
  return t(t(key, { defaultValue: '' }) ? key : 'errors.UNKNOWN', values);
}

/** Mensagem de um erro de campo: `validation.<code>`, ou uma genérica se a chave não existir. */
export function fieldMessage(t: TFunction, code: string): string {
  const key = `validation.${code}`;
  return t(t(key, { defaultValue: '' }) ? key : 'validation.TYPE_MISMATCH');
}

/**
 * Passa os `errors[]` para os campos do formulário (react-hook-form `setError`), com o código
 * como mensagem (o campo mostra-o com {@link fieldMessage}). Devolve `true` se algum campo
 * conhecido recebeu um erro.
 */
export function applyFieldErrors<F extends string>(
  problem: Problem,
  fields: readonly F[],
  setError: (field: F, error: { type: string; message: string }) => void,
): boolean {
  let applied = false;
  for (const error of problem.errors) {
    const field = error.field as F;
    if (fields.includes(field)) {
      setError(field, { type: 'server', message: error.code });
      applied = true;
    }
  }
  return applied;
}
