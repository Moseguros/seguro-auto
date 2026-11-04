import { describe, expect, it } from 'vitest';
import { csvColumns, exportTableCsv, importTableCsv } from './csv';
import tariffDp from './fixtures/tariff-dp.json';
import type { Rating } from './ratingTypes';

const tariff = tariffDp as unknown as Rating;
const mtpl = tariff.coverages[0]!;
const driverAge = mtpl.factors!.find((f) => f.code === 'DRIVER_AGE')!;
const header = csvColumns.join(';');
/** Marca de ordem dos bytes (U+FEFF). */
const bom = String.fromCharCode(0xfeff);

/** Linha CSV com só algumas colunas preenchidas. */
function line(values: Record<string, string>): string {
  return csvColumns.map((c) => values[c] ?? '').join(';');
}

describe('csv', () => {
  it('exporta o factor DRIVER_AGE como no exemplo da ficha', () => {
    const text = exportTableCsv(driverAge.rows, 'FACTOR');
    expect(text.startsWith(bom)).toBe(true);
    expect(text.slice(1).split('\r\n')).toEqual([
      header,
      ';;;;;;;;;;;;;;25;;;;;;;;;1.30',
      ';;;;;;;;;;;;;25;70;;;;;;;;;1.00',
      ';;;;;;;;;;;;;70;;;;;;;;;;1.15',
    ]);
  });

  it('exportar e importar o factor DRIVER_AGE devolve as mesmas linhas', () => {
    const result = importTableCsv(exportTableCsv(driverAge.rows, 'FACTOR'), 'FACTOR');
    expect(result).toEqual({ ok: true, rows: driverAge.rows });
  });

  it('exportar e importar a base da MTPL devolve as mesmas linhas', () => {
    const text = exportTableCsv(mtpl.base.rows, 'AMOUNT');
    expect(text).toContain('Ligeiro de passageiros, particular;LIGHT_PASSENGER;PRIVATE;');
    expect(text).toContain(';TAXI|RENTAL;');
    expect(text).toContain(';4500.00');
    expect(importTableCsv(text, 'AMOUNT')).toEqual({ ok: true, rows: mtpl.base.rows });
  });

  it('exportar e importar uma base com taxas e intervalos em MT', () => {
    const vtpl = tariff.coverages.find((c) => c.coverageCode === 'VTPL')!;
    const text = exportTableCsv(vtpl.base.rows, 'RATE');
    expect(text).toContain(';6000000.00;;;0.0004');
    expect(importTableCsv(text, 'RATE')).toEqual({ ok: true, rows: vtpl.base.rows });
  });

  it('aceita vírgula decimal, sem BOM, com \\n e colunas por outra ordem', () => {
    const text = [
      'value;driverAgeYears_lt;capital_gte;label',
      '1,30;25;5000,5;Jovem',
      '1;;;',
      '',
    ].join('\n');
    expect(importTableCsv(text, 'FACTOR')).toEqual({
      ok: true,
      rows: [
        {
          label: 'Jovem',
          when: { driverAgeYears: { lt: 25 }, capital: { gte: 500050 } },
          value: '1.30',
        },
        { when: {}, value: '1' },
      ],
    });
    expect(importTableCsv('value\r\n4500,5\r\n', 'AMOUNT')).toEqual({
      ok: true,
      rows: [{ when: {}, value: 450050 }],
    });
  });

  it('aceita o BOM', () => {
    const text = [bom + header, line({ value: '1.00' }), ''].join('\r\n');
    const result = importTableCsv(text, 'FACTOR');
    expect(result).toEqual({ ok: true, rows: [{ when: {}, value: '1.00' }] });
  });

  it('cada código de erro, com a linha certa', () => {
    expect(importTableCsv('', 'FACTOR')).toEqual({ ok: false, errors: [{ code: 'EMPTY_FILE' }] });
    expect(importTableCsv(`${header}\r\n`, 'FACTOR')).toEqual({
      ok: false,
      errors: [{ code: 'EMPTY_FILE' }],
    });
    expect(importTableCsv('value;colour\n1', 'FACTOR')).toEqual({
      ok: false,
      errors: [{ code: 'UNKNOWN_COLUMN', column: 'colour' }],
    });
    expect(importTableCsv('label\nx', 'FACTOR')).toEqual({
      ok: false,
      errors: [{ code: 'MISSING_VALUE_COLUMN' }],
    });
    const rows = [
      header,
      line({ value: '1.00' }),
      line({ label: 'sem valor' }),
      line({ seats_gte: '2a', value: '1' }),
      line({ vehicleCategory: 'LIGHT_PASSENGER|CAR', value: '1' }),
      line({ hasTracker: 'sim', value: '1' }),
      line({ value: '1,2,3' }),
    ].join('\r\n');
    expect(importTableCsv(rows, 'FACTOR')).toEqual({
      ok: false,
      errors: [
        { code: 'MISSING_VALUE', line: 3 },
        { code: 'INVALID_NUMBER', line: 4, column: 'seats_gte' },
        { code: 'INVALID_ENUM', line: 5, column: 'vehicleCategory', value: 'CAR' },
        { code: 'INVALID_BOOLEAN', line: 6, column: 'hasTracker' },
        { code: 'INVALID_NUMBER', line: 7, column: 'value' },
      ],
    });
  });

  it('recusa mais de 200 linhas', () => {
    const many = Array.from({ length: 201 }, () => line({ value: '1' }));
    expect(importTableCsv([header, ...many].join('\r\n'), 'FACTOR')).toEqual({
      ok: false,
      errors: [{ code: 'TOO_MANY_ROWS' }],
    });
    const enough = Array.from({ length: 200 }, () => line({ value: '1' }));
    expect(importTableCsv([header, ...enough].join('\r\n'), 'FACTOR').ok).toBe(true);
  });
});
