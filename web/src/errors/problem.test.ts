import { AxiosError, AxiosHeaders, type AxiosResponse } from 'axios';
import { describe, expect, it } from 'vitest';
import i18n from '../i18n';
import { applyFieldErrors, fieldMessage, getProblem, problemMessage } from './problem';

function httpError(status: number, data: unknown, headers: Record<string, string> = {}) {
  const config = { headers: new AxiosHeaders() };
  const response = { status, data, statusText: '', headers, config } as unknown as AxiosResponse;
  return new AxiosError('failed', 'ERR_BAD_REQUEST', config, undefined, response);
}

const t = i18n.t.bind(i18n);

describe('getProblem', () => {
  it('lê o ProblemDetail com as propriedades extra', () => {
    const problem = getProblem(
      httpError(400, {
        status: 400,
        code: 'OTP_INVALID',
        detail: 'Wrong code',
        attemptsRemaining: 2,
        errors: [{ field: 'code', code: 'PATTERN' }, { nonsense: true }],
      }),
    );
    expect(problem).toEqual({
      status: 400,
      code: 'OTP_INVALID',
      detail: 'Wrong code',
      attemptsRemaining: 2,
      retryAfterSeconds: undefined,
      errors: [{ field: 'code', code: 'PATTERN' }],
    });
  });

  it('usa o cabeçalho Retry-After quando o corpo não traz retryAfterSeconds', () => {
    const problem = getProblem(
      httpError(429, { code: 'OTP_RATE_LIMITED' }, { 'retry-after': '120' }),
    );
    expect(problem.retryAfterSeconds).toBe(120);
  });

  it('sem resposta → NETWORK_ERROR; outro erro → UNKNOWN', () => {
    expect(getProblem(new AxiosError('Network Error', 'ERR_NETWORK')).code).toBe('NETWORK_ERROR');
    expect(getProblem(new Error('x')).code).toBe('UNKNOWN');
    expect(getProblem(httpError(500, 'boom')).code).toBe('UNKNOWN');
  });
});

describe('mensagens de erro por código', () => {
  it.each([
    ['OTP_EXPIRED', 'O código expirou. Peça um novo código.'],
    ['OTP_ATTEMPTS_EXCEEDED', 'Demasiadas tentativas erradas. Peça um novo código.'],
    ['ACCOUNT_DISABLED', 'Esta conta está desactivada. Contacte o apoio ao cliente.'],
    ['LAST_CONTACT', 'A conta tem de ficar com pelo menos um contacto confirmado.'],
    [
      'CONCURRENT_MODIFICATION',
      'Outra pessoa alterou estes dados entretanto. Actualize a página e tente de novo.',
    ],
  ])('%s', (code, message) => {
    expect(problemMessage(t, getProblem(httpError(400, { code })))).toBe(message);
  });

  it('código sem tradução → errors.UNKNOWN', () => {
    expect(problemMessage(t, getProblem(httpError(400, { code: 'SOMETHING_NEW' })))).toBe(
      'Ocorreu um erro inesperado.',
    );
  });

  it('em inglês', async () => {
    await i18n.changeLanguage('en');
    const message = problemMessage(
      i18n.t.bind(i18n),
      getProblem(httpError(400, { code: 'OTP_EXPIRED' })),
    );
    expect(message).toMatch(/expired/i);
  });

  it('erros de campo → validation.<code> nos campos conhecidos', () => {
    const problem = getProblem(
      httpError(400, {
        code: 'VALIDATION_ERROR',
        errors: [
          { field: 'nuit', code: 'INVALID_NUIT' },
          { field: 'other', code: 'REQUIRED' },
        ],
      }),
    );
    const set: Record<string, string> = {};
    const applied = applyFieldErrors(problem, ['nuit', 'fullName'] as const, (field, error) => {
      set[field] = error.message;
    });
    expect(applied).toBe(true);
    expect(set).toEqual({ nuit: 'INVALID_NUIT' });
    expect(fieldMessage(t, 'INVALID_NUIT')).toBe('O NUIT tem 9 dígitos.');
    expect(fieldMessage(t, 'NO_SUCH_CODE')).toBe('Valor inválido.');
  });
});
