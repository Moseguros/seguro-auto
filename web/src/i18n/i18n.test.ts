import { describe, expect, it } from 'vitest';
import en from './locales/en.json';
import ptMZ from './locales/pt-MZ.json';
import { areas, menus } from '../layout/menus';

function keys(value: unknown, prefix = ''): string[] {
  if (value === null || typeof value !== 'object') return [prefix];
  return Object.entries(value).flatMap(([k, v]) => keys(v, prefix ? `${prefix}.${k}` : k));
}

describe('traduções', () => {
  it('pt-MZ e en têm as mesmas chaves', () => {
    expect(keys(en).sort()).toEqual(keys(ptMZ).sort());
  });

  it('todas as áreas e itens de menu têm texto', () => {
    const all = new Set(keys(ptMZ));
    for (const area of areas) {
      expect(all.has(`areas.${area}`)).toBe(true);
      for (const item of menus[area]) {
        expect(all.has(`menu.${area}.${item.key}`)).toBe(true);
      }
    }
  });

  it('tem a frase da página inicial', () => {
    expect(ptMZ.home.tagline).toBe('Compare, compre e gira o seu seguro automóvel num só lugar.');
    expect(en.home.tagline).toBe('Compare, buy and manage your car insurance in one place.');
  });
});
