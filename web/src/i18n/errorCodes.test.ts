import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import en from './locales/en.json';
import ptMZ from './locales/pt-MZ.json';

/** Contrato dos códigos de erro exportado pelo backend (S1-02). */
const errorCodes = JSON.parse(
  readFileSync(resolve(__dirname, '../../../api/error-codes.json'), 'utf-8'),
) as { errors: { code: string }[]; validation: string[] };

describe('api/error-codes.json', () => {
  it('não está vazio', () => {
    expect(errorCodes.errors.length).toBeGreaterThan(0);
    expect(errorCodes.validation.length).toBeGreaterThan(0);
  });

  it.each([
    ['pt-MZ', ptMZ],
    ['en', en],
  ] as const)('todos os códigos têm tradução em %s', (_, messages) => {
    const errors = messages.errors as Record<string, string>;
    const validation = messages.validation as Record<string, string>;
    const missingErrors = errorCodes.errors.map((e) => e.code).filter((code) => !errors[code]);
    const missingValidation = errorCodes.validation.filter((code) => !validation[code]);
    expect(missingErrors).toEqual([]);
    expect(missingValidation).toEqual([]);
  });
});
