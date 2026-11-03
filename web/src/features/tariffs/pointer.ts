/** Sítio do editor a que um erro da tarifa se refere. */
export type TariffLocation =
  | { kind: 'top' }
  | { kind: 'duration'; index: number }
  | { kind: 'coverage'; coverage: number }
  | { kind: 'base'; coverage: number; row?: number }
  | { kind: 'factor'; coverage: number; factor: number; row?: number };

const index = (part: string | undefined): number | undefined =>
  part !== undefined && /^\d+$/.test(part) ? Number(part) : undefined;

/**
 * Ponteiro JSON do servidor (dentro de `rating`) → sítio no editor (S3-03 §7). Ponteiro que não
 * corresponde a nada → `null` (só aparece no painel, sem destaque).
 */
export function locate(pointer: string): TariffLocation | null {
  if (!pointer.startsWith('/')) return null;
  const parts = pointer.slice(1).split('/');
  const [head, first, second, third, fourth, fifth] = parts;

  if (head === 'durationFactors') {
    const i = index(first);
    return i === undefined ? null : { kind: 'duration', index: i };
  }
  if (head !== 'coverages') return null;
  if (first === undefined || first === '') return { kind: 'top' };
  const coverage = index(first);
  if (coverage === undefined) return null;
  if (second === undefined) return { kind: 'coverage', coverage };

  if (second === 'base') {
    if (third === 'rows') {
      const row = index(fourth);
      return row === undefined ? { kind: 'base', coverage } : { kind: 'base', coverage, row };
    }
    return { kind: 'base', coverage };
  }
  if (second === 'factors') {
    const factor = index(third);
    if (factor === undefined) return { kind: 'coverage', coverage };
    if (fourth === 'rows') {
      const row = index(fifth);
      return row === undefined
        ? { kind: 'factor', coverage, factor }
        : { kind: 'factor', coverage, factor, row };
    }
    return { kind: 'factor', coverage, factor };
  }
  return { kind: 'coverage', coverage };
}

/** Índice da cobertura de um sítio (para o ícone de erro na lista de coberturas). */
export function coverageOf(location: TariffLocation | null): number | undefined {
  if (!location) return undefined;
  if (location.kind === 'top' || location.kind === 'duration') return undefined;
  return location.coverage;
}

/** Identificador de uma tabela de uma cobertura: `base` ou `factor-<i>`. */
export type TableKey = 'base' | `factor-${number}`;

export function tableKeyOf(location: TariffLocation | null): TableKey | undefined {
  if (!location) return undefined;
  if (location.kind === 'base') return 'base';
  if (location.kind === 'factor') return `factor-${location.factor}`;
  return undefined;
}
