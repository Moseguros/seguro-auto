import Papa from 'papaparse';
import type { Condition, TariffRow, ValueKind, ValueRange } from './ratingTypes';
import { variables } from './variables';

/** CSV de uma tabela (base ou factor), S3-03 §6. */

export const maxCsvRows = 200;

/** Colunas pela ordem da exportação. */
export const csvColumns: string[] = [
  'label',
  ...variables.flatMap((v) =>
    v.kind === 'int' || v.kind === 'money' ? [`${v.name}_gte`, `${v.name}_lt`] : [v.name],
  ),
  'value',
];

export type CsvErrorCode =
  | 'EMPTY_FILE'
  | 'UNKNOWN_COLUMN'
  | 'MISSING_VALUE_COLUMN'
  | 'MISSING_VALUE'
  | 'INVALID_NUMBER'
  | 'INVALID_ENUM'
  | 'INVALID_BOOLEAN'
  | 'TOO_MANY_ROWS';

export interface CsvError {
  code: CsvErrorCode;
  /** Linha do ficheiro (a primeira linha de dados é a 2). */
  line?: number;
  column?: string;
  value?: string;
}

export type CsvImportResult = { ok: true; rows: TariffRow[] } | { ok: false; errors: CsvError[] };

const bom = '﻿';

/** Centavos → meticais com ponto e 2 casas (`450000` → `"4500.00"`). */
function centsToText(cents: number): string {
  const units = Math.trunc(cents / 100);
  const rest = String(cents % 100).padStart(2, '0');
  return `${units}.${rest}`;
}

const moneyPattern = /^(\d+)(?:[.,](\d{1,2}))?$/;
const intPattern = /^\d+$/;
const decimalPattern = /^\d+(?:[.,]\d{1,6})?$/;

/** Meticais escritos (`"4500"`, `"4500,5"`) → centavos, ou `null` se inválido. */
function textToCents(text: string): number | null {
  const match = moneyPattern.exec(text);
  if (!match) return null;
  const total = Number(match[1]) * 100 + Number((match[2] ?? '').padEnd(2, '0'));
  return Number.isSafeInteger(total) ? total : null;
}

function valueText(value: TariffRow['value'], kind: ValueKind): string {
  if (value === null || value === undefined) return '';
  if (kind === 'AMOUNT') return typeof value === 'number' ? centsToText(value) : String(value);
  return String(value);
}

/** Exporta as linhas de uma tabela: UTF-8 com BOM, `;` e `\r\n`. */
export function exportTableCsv(rows: readonly TariffRow[], kind: ValueKind): string {
  const data = rows.map((row) => {
    const cells: string[] = [row.label ?? ''];
    for (const v of variables) {
      const condition = row.when[v.name];
      if (v.kind === 'set') {
        cells.push(condition ? (condition as { in: string[] }).in.join('|') : '');
      } else if (v.kind === 'bool') {
        cells.push(condition === undefined ? '' : String(condition));
      } else {
        const range = condition as ValueRange | undefined;
        const write = (n: number | undefined) =>
          n === undefined ? '' : v.kind === 'money' ? centsToText(n) : String(n);
        cells.push(write(range?.gte), write(range?.lt));
      }
    }
    cells.push(valueText(row.value, kind));
    return cells;
  });
  return bom + Papa.unparse({ fields: csvColumns, data }, { delimiter: ';', newline: '\r\n' });
}

/** Lê o CSV de uma tabela. Qualquer erro cancela a importação toda. */
export function importTableCsv(text: string, kind: ValueKind): CsvImportResult {
  const content = text.startsWith(bom) ? text.slice(1) : text;
  const parsed = Papa.parse<string[]>(content, { delimiter: ';', skipEmptyLines: false });
  const lines = parsed.data;
  const blank = (cells: string[]) => cells.every((c) => c.trim() === '');
  const header = lines[0];
  if (!header || blank(header)) return { ok: false, errors: [{ code: 'EMPTY_FILE' }] };

  const errors: CsvError[] = [];
  const columns = header.map((c) => c.trim());
  for (const column of columns) {
    if (!csvColumns.includes(column)) errors.push({ code: 'UNKNOWN_COLUMN', column });
  }
  if (!columns.includes('value')) errors.push({ code: 'MISSING_VALUE_COLUMN' });
  if (errors.length > 0) return { ok: false, errors };

  const data = lines
    .map((cells, i) => ({ cells, line: i + 1 }))
    .slice(1)
    .filter(({ cells }) => !blank(cells));
  if (data.length === 0) return { ok: false, errors: [{ code: 'EMPTY_FILE' }] };
  if (data.length > maxCsvRows) return { ok: false, errors: [{ code: 'TOO_MANY_ROWS' }] };

  const rows: TariffRow[] = [];
  for (const { cells, line } of data) {
    const cell = (column: string) => {
      const i = columns.indexOf(column);
      return i < 0 ? '' : (cells[i] ?? '').trim();
    };
    const when: Condition = {};
    for (const v of variables) {
      if (v.kind === 'set') {
        const raw = cell(v.name);
        if (raw === '') continue;
        const codes = raw.split('|').map((c) => c.trim());
        const unknown = codes.find((c) => !v.options!.includes(c));
        if (unknown !== undefined) {
          errors.push({ code: 'INVALID_ENUM', line, column: v.name, value: unknown });
          continue;
        }
        (when as Record<string, unknown>)[v.name] = { in: codes };
      } else if (v.kind === 'bool') {
        const raw = cell(v.name);
        if (raw === '') continue;
        if (raw !== 'true' && raw !== 'false') {
          errors.push({ code: 'INVALID_BOOLEAN', line, column: v.name });
          continue;
        }
        when.hasTracker = raw === 'true';
      } else {
        const range: ValueRange = {};
        for (const bound of ['gte', 'lt'] as const) {
          const column = `${v.name}_${bound}`;
          const raw = cell(column);
          if (raw === '') continue;
          const n =
            v.kind === 'money' ? textToCents(raw) : intPattern.test(raw) ? Number(raw) : null;
          if (n === null || !Number.isSafeInteger(n)) {
            errors.push({ code: 'INVALID_NUMBER', line, column });
            continue;
          }
          range[bound] = n;
        }
        if (range.gte !== undefined || range.lt !== undefined) {
          (when as Record<string, unknown>)[v.name] = range;
        }
      }
    }

    const rawValue = cell('value');
    let value: TariffRow['value'] = null;
    if (rawValue === '') {
      errors.push({ code: 'MISSING_VALUE', line });
    } else if (kind === 'AMOUNT') {
      value = textToCents(rawValue);
      if (value === null) errors.push({ code: 'INVALID_NUMBER', line, column: 'value' });
    } else if (decimalPattern.test(rawValue)) {
      value = rawValue.replace(',', '.');
    } else {
      errors.push({ code: 'INVALID_NUMBER', line, column: 'value' });
    }

    const label = cell('label');
    rows.push(label ? { label, when, value } : { when, value });
  }
  return errors.length > 0 ? { ok: false, errors } : { ok: true, rows };
}
