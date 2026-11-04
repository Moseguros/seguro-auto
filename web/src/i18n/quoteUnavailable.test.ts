import { describe, expect, it } from 'vitest';
import { UnavailableReason } from '../api/generated';
import en from './locales/en.json';
import ptMZ from './locales/pt-MZ.json';

/**
 * Motivos de uma cotação NOT_AVAILABLE (S3-02 §16): cada valor de UnavailableReason do contrato
 * tem o texto quoteUnavailable.<REASON> nas duas línguas (usado pelos ecrãs do S3-03 a S3-05).
 */
describe('quoteUnavailable', () => {
  const reasons = Object.values(UnavailableReason);

  it('o contrato tem os 13 motivos', () => {
    expect(reasons).toHaveLength(13);
  });

  it.each([
    ['pt-MZ', ptMZ],
    ['en', en],
  ] as const)('todos os motivos têm tradução em %s', (_, messages) => {
    const texts = messages.quoteUnavailable as Record<string, string>;
    expect(reasons.filter((reason) => !texts[reason])).toEqual([]);
  });
});
