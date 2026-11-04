import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import en from './locales/en.json';
import ptMZ from './locales/pt-MZ.json';

/** Perguntas, respostas e avisos das propostas exportados pelo backend (S4-02). */
const questions = JSON.parse(
  readFileSync(resolve(__dirname, '../../../api/proposal-questions.json'), 'utf-8'),
) as {
  declarations: { code: string; followUp: string[] }[];
  suitability: { code: string; type: string; options: string[] }[];
  suitabilityReasons: string[];
};

function value(messages: unknown, key: string): unknown {
  return key
    .split('.')
    .reduce<unknown>(
      (node, part) =>
        node && typeof node === 'object' ? (node as Record<string, unknown>)[part] : undefined,
      messages,
    );
}

/** Todas as chaves que os ecrãs usam para as perguntas, as respostas e os avisos. */
function requiredKeys(): string[] {
  return [
    ...questions.declarations.flatMap((q) => [
      `declaration.${q.code}.question`,
      ...q.followUp.map((followUp) => `declaration.${q.code}.${followUp}`),
    ]),
    'declaration.truthfulness',
    ...questions.suitability.flatMap((q) => [
      `suitability.${q.code}.question`,
      ...q.options.map((option) => `suitability.${q.code}.option.${option}`),
    ]),
    ...questions.suitabilityReasons.map((reason) => `suitability.reason.${reason}`),
  ];
}

describe('api/proposal-questions.json', () => {
  it('não está vazio', () => {
    expect(questions.declarations.length).toBeGreaterThan(0);
    expect(questions.suitability.length).toBeGreaterThan(0);
    expect(questions.suitabilityReasons.length).toBeGreaterThan(0);
  });

  it.each([
    ['pt-MZ', ptMZ],
    ['en', en],
  ] as const)('todas as perguntas, respostas e avisos têm texto em %s', (_, messages) => {
    const missing = requiredKeys().filter((key) => {
      const text = value(messages, key);
      return typeof text !== 'string' || text.trim() === '';
    });
    expect(missing).toEqual([]);
  });

  it('usa os textos do S4-02, palavra por palavra', () => {
    expect(ptMZ.declaration.USAGE_DIFFERENT.question).toBe(
      'O veículo vai ser usado para outro fim além de «{{usage}}»? Por exemplo, transportar passageiros ou mercadorias a troco de pagamento.',
    );
    expect(en.suitability.reason.NEED_UNSURE).toBe(
      'You are not sure what cover you need. Read the cover summary and the pre-contract information before continuing.',
    );
  });
});
