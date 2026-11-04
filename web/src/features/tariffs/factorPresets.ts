import type { Condition } from './ratingTypes';

/** Factores pré-definidos do menu "Acrescentar factor" (`factorPreset.<code>` dá o nome). */
export interface FactorPreset {
  code: string;
  /** Variável sugerida ao acrescentar a primeira linha. */
  variable: keyof Condition;
}

export const factorPresets: readonly FactorPreset[] = [
  { code: 'DRIVER_AGE', variable: 'driverAgeYears' },
  { code: 'LICENCE_YEARS', variable: 'licenceYears' },
  { code: 'PROVINCE', variable: 'province' },
  { code: 'VEHICLE_AGE', variable: 'vehicleAgeYears' },
  { code: 'TRACKER', variable: 'hasTracker' },
];

/** Código reservado ao bónus/malus (S7). */
export const reservedFactorCode = 'BONUS_MALUS';

export const factorCodePattern = /^[A-Z][A-Z0-9_]{1,29}$/;

/** Variável sugerida para um factor (pelos presets), se houver. */
export function suggestedVariable(code: string): keyof Condition | undefined {
  return factorPresets.find((p) => p.code === code)?.variable;
}

/** Erro do código de um factor personalizado, ou `null` se servir. */
export function customFactorCodeError(code: string, existing: readonly string[]): string | null {
  if (!factorCodePattern.test(code)) return 'INVALID_CODE';
  if (code === reservedFactorCode) return 'RESERVED_FACTOR_CODE';
  if (existing.includes(code)) return 'DUPLICATE_FACTOR';
  return null;
}
