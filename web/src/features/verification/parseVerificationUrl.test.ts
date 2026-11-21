import { describe, expect, it } from 'vitest';
import { parseVerificationUrl } from './parseVerificationUrl';

const token = 'DEMOvalid0000000000001';

describe('parseVerificationUrl', () => {
  it('aceita qualquer domínio e o localhost', () => {
    expect(parseVerificationUrl(`https://qualquer.dominio/v/${token}`)).toBe(token);
    expect(parseVerificationUrl(`http://localhost:3000/v/${token}`)).toBe(token);
    expect(parseVerificationUrl(`https://verificar.moseguros.co.mz/v/Ab_-${'x'.repeat(18)}`)).toBe(
      `Ab_-${'x'.repeat(18)}`,
    );
  });

  it('rejeita outro caminho, 21 ou 23 caracteres e texto solto', () => {
    expect(parseVerificationUrl(`https://qualquer.dominio/x/${token}`)).toBeNull();
    expect(parseVerificationUrl(`https://qualquer.dominio/v/${token}/extra`)).toBeNull();
    expect(parseVerificationUrl(`https://qualquer.dominio/v/${token.slice(1)}`)).toBeNull();
    expect(parseVerificationUrl(`https://qualquer.dominio/v/${token}A`)).toBeNull();
    expect(parseVerificationUrl(`https://qualquer.dominio/v/${token.slice(1)}!`)).toBeNull();
    expect(parseVerificationUrl(token)).toBeNull();
    expect(parseVerificationUrl('olá mundo')).toBeNull();
    expect(parseVerificationUrl(`ftp://qualquer.dominio/v/${token}`)).toBeNull();
  });
});
