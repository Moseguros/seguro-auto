import { readdirSync, readFileSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { describe, expect, it } from 'vitest';

/** Pastas dos ecrãs de staff dos pagamentos (S5-12): não têm acções de conversão. */
const folders = ['insurer-payment-settings', 'insurer-payments', 'admin-payment-ops'];

describe('ecrãs de pagamentos da seguradora e da administração', () => {
  it('nenhum componente usa variant="cta" (laranja só para conversão)', () => {
    const offenders: string[] = [];
    for (const folder of folders) {
      const dir = resolve(__dirname, '..', folder);
      for (const file of readdirSync(dir)) {
        if (!file.endsWith('.tsx') || file.endsWith('.test.tsx')) continue;
        const source = readFileSync(join(dir, file), 'utf-8');
        if (/variant=["{']+cta/.test(source) || /variant:\s*'cta'/.test(source)) {
          offenders.push(`${folder}/${file}`);
        }
      }
    }
    expect(offenders).toEqual([]);
  });
});
