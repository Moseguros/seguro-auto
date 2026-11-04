/**
 * Formato `rating-v1` da tarifa interna (S3-01 §6). O cliente gerado só o conhece como
 * `{ [key: string]: unknown }`; estes tipos descrevem o que o editor lê e escreve. Decimais
 * (taxas e factores) em texto, dinheiro em centavos.
 */

export type BaseType = 'AMOUNT' | 'RATE_OF_VEHICLE_VALUE' | 'RATE_OF_CAPITAL';

export const baseTypes: BaseType[] = ['AMOUNT', 'RATE_OF_VEHICLE_VALUE', 'RATE_OF_CAPITAL'];

export interface ValueRange {
  gte?: number;
  lt?: number;
}

export interface CodeSet {
  in: string[];
}

export interface Condition {
  vehicleCategory?: CodeSet;
  vehicleUsage?: CodeSet;
  province?: CodeSet;
  hasTracker?: boolean;
  vehicleAgeYears?: ValueRange;
  engineCc?: ValueRange;
  seats?: ValueRange;
  grossWeightKg?: ValueRange;
  driverAgeYears?: ValueRange;
  licenceYears?: ValueRange;
  durationDays?: ValueRange;
  capital?: ValueRange;
  vehicleValue?: ValueRange;
}

/**
 * Linha de uma tabela. `value`: centavos na base `AMOUNT`, taxa decimal em texto na base
 * `RATE_OF_*`, factor em texto nos factores; `null` enquanto não foi preenchido.
 */
export interface TariffRow {
  label?: string;
  when: Condition;
  value: number | string | null;
}

export interface BaseTable {
  type: BaseType;
  rows: TariffRow[];
}

export interface FactorTable {
  code: string;
  label?: string;
  rows: TariffRow[];
}

export interface CoverageTariff {
  coverageCode: string;
  base: BaseTable;
  factors?: FactorTable[];
}

export interface DurationFactor {
  days: number;
  factor: string;
}

export interface Rating {
  schemaVersion: 'rating-v1';
  durationFactors?: DurationFactor[];
  coverages: CoverageTariff[];
}

/** Que valor tem uma tabela: dinheiro, taxa ou factor. */
export type ValueKind = 'AMOUNT' | 'RATE' | 'FACTOR';

export function valueKindOf(baseType: BaseType | null): ValueKind {
  if (baseType === null) return 'FACTOR';
  return baseType === 'AMOUNT' ? 'AMOUNT' : 'RATE';
}

/** Erro de um campo da tarifa: ponteiro JSON dentro de `rating` e código `validation.*`. */
export interface TariffError {
  field: string;
  code: string;
}

/** Durações permitidas nos produtos temporários (S3-01 §6). */
export const allowedDurations = [7, 14, 30, 60, 365] as const;
