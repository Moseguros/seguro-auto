import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { useState } from 'react';
import { describe, expect, it } from 'vitest';
import en from '../../i18n/locales/en.json';
import ptMZ from '../../i18n/locales/pt-MZ.json';
import { methodSchema } from '../../test/insurerPayments';
import { renderWithProviders } from '../../test/render';
import { secretFieldsByMethod } from './methodRules';
import { SchemaForm } from './SchemaForm';
import {
  applyDefaults,
  cleanSettings,
  errorTarget,
  leafPaths,
  objectPaths,
  type JsonSchema,
  type Settings,
} from './schemaPaths';

function text(messages: unknown, key: string): unknown {
  return key
    .split('.')
    .reduce<unknown>(
      (node, part) =>
        node && typeof node === 'object' ? (node as Record<string, unknown>)[part] : undefined,
      messages,
    );
}

/** Schema com todos os tipos que o formulário desenha. */
const sample: JsonSchema = {
  type: 'object',
  required: ['code', 'mode'],
  properties: {
    code: { type: 'string', pattern: '^[0-9]{5}$', maxLength: 5 },
    mode: { type: 'string', enum: ['A', 'B'], default: 'B' },
    kind: { const: 'FIXED' },
    retries: { type: 'integer', minimum: 1, maximum: 9, default: 3 },
    active: { type: 'boolean', default: true },
    box: {
      type: 'object',
      properties: { dir: { $ref: '#/$defs/dir' } },
    },
  },
  $defs: { dir: { type: 'string', maxLength: 20 } },
};

function Harness({ schema, initial }: { schema: JsonSchema; initial: Settings }) {
  const [value, setValue] = useState(() => applyDefaults(schema, initial));
  return (
    <>
      <SchemaForm method="TEST" schema={schema} value={value} onChange={setValue} />
      <output data-testid="value">{JSON.stringify(value)}</output>
    </>
  );
}

describe('SchemaForm', () => {
  it('desenha string, enum, const, integer, boolean e object com os valores por defeito', async () => {
    renderWithProviders(<Harness schema={sample} initial={{}} />);
    const code = document.getElementById('TEST-code') as HTMLInputElement;
    expect(code).toHaveAttribute('pattern', '^[0-9]{5}$');
    expect(code).toHaveAttribute('maxlength', '5');
    expect(code).toBeRequired();
    // enum com o valor por defeito
    expect(screen.getByRole('combobox')).toHaveTextContent('B');
    // const só de leitura
    const kind = document.getElementById('TEST-kind') as HTMLInputElement;
    expect(kind.value).toBe('FIXED');
    expect(kind).toHaveAttribute('readonly');
    // integer com min/max e o valor por defeito
    const retries = document.getElementById('TEST-retries') as HTMLInputElement;
    expect(retries).toHaveAttribute('type', 'number');
    expect(retries).toHaveAttribute('min', '1');
    expect(retries).toHaveAttribute('max', '9');
    expect(retries.value).toBe('3');
    // boolean como interruptor ligado por defeito
    expect(document.getElementById('TEST-active')).toBeChecked();
    // object como fieldset, com o $ref resolvido
    expect(document.querySelector('fieldset')).not.toBeNull();
    expect(document.getElementById('TEST-box-dir')).toHaveAttribute('maxlength', '20');

    await userEvent.type(retries, '{backspace}7');
    await userEvent.click(document.getElementById('TEST-active') as HTMLElement);
    const value = JSON.parse(screen.getByTestId('value').textContent ?? '{}') as Settings;
    expect(value.retries).toBe(7);
    expect(value.active).toBe(false);
  });

  it('cleanSettings tira os vazios e os escondidos e converte os inteiros', () => {
    const cleaned = cleanSettings(
      sample,
      { code: '', mode: 'A', retries: '4', box: { dir: 'x' } },
      (path) => path === 'box.dir',
    );
    expect(cleaned).toEqual({ mode: 'A', retries: 4, box: {} });
  });

  it('converte os caminhos dos erros do servidor', () => {
    expect(errorTarget('settings.sftp.port')).toEqual({ scope: 'settings', path: 'sftp.port' });
    expect(errorTarget('secrets.apiKey')).toEqual({ scope: 'secrets', path: 'apiKey' });
    expect(errorTarget('minAmount')).toEqual({ scope: 'general', path: 'minAmount' });
  });

  it.each([
    ['MPESA', 'mpesa'],
    ['BANK_REFERENCE', 'bank_reference'],
  ] as const)('os campos do schema %s têm rótulo em pt e en', (method, file) => {
    const schema = methodSchema(file);
    const keys = [
      ...leafPaths(schema).map((path) => `paymentSettings.${method}.${path}`),
      ...(secretFieldsByMethod[method] ?? []).map((f) => `paymentSettings.${method}.${f}`),
      ...objectPaths(schema).map((path) => `paymentSettingsGroup.${method}.${path}`),
    ];
    expect(keys.length).toBeGreaterThan(5);
    for (const [lang, messages] of [
      ['pt-MZ', ptMZ],
      ['en', en],
    ] as const) {
      const missing = keys.filter((key) => typeof text(messages, key) !== 'string');
      expect(missing, lang).toEqual([]);
    }
  });
});
