import type { Condition } from './ratingTypes';

/** Variável de tarifação (S3-01 §8) e o controlo que a edita. */
export type VariableKind = 'set' | 'bool' | 'int' | 'money';

export interface VariableDef {
  name: keyof Condition;
  kind: VariableKind;
  /** Só nos conjuntos: códigos permitidos e o prefixo i18n dos textos. */
  options?: readonly string[];
  optionKey?: string;
}

export const vehicleCategories = [
  'LIGHT_PASSENGER',
  'LIGHT_MIXED',
  'HEAVY',
  'MOTORCYCLE',
  'TRAILER',
  'BUS',
] as const;

export const vehicleUsages = [
  'PRIVATE',
  'RENTAL',
  'PASSENGER_TRANSPORT',
  'GOODS',
  'TAXI',
  'DRIVING_SCHOOL',
] as const;

export const provinces = [
  'MAPUTO_CITY',
  'MAPUTO',
  'GAZA',
  'INHAMBANE',
  'SOFALA',
  'MANICA',
  'TETE',
  'ZAMBEZIA',
  'NAMPULA',
  'CABO_DELGADO',
  'NIASSA',
] as const;

/** Todas as variáveis, pela ordem do `RowDialog` e das colunas do CSV. */
export const variables: readonly VariableDef[] = [
  {
    name: 'vehicleCategory',
    kind: 'set',
    options: vehicleCategories,
    optionKey: 'vehicleCategory',
  },
  { name: 'vehicleUsage', kind: 'set', options: vehicleUsages, optionKey: 'vehicleUsage' },
  { name: 'province', kind: 'set', options: provinces, optionKey: 'province' },
  { name: 'hasTracker', kind: 'bool' },
  { name: 'vehicleAgeYears', kind: 'int' },
  { name: 'engineCc', kind: 'int' },
  { name: 'seats', kind: 'int' },
  { name: 'grossWeightKg', kind: 'int' },
  { name: 'driverAgeYears', kind: 'int' },
  { name: 'licenceYears', kind: 'int' },
  { name: 'durationDays', kind: 'int' },
  { name: 'capital', kind: 'money' },
  { name: 'vehicleValue', kind: 'money' },
];

export function variableDef(name: string): VariableDef | undefined {
  return variables.find((v) => v.name === name);
}

/** Limites dos intervalos inteiros (`rating-v1.json`). */
export const intLimits = { gteMin: 0, ltMin: 1, max: 1_000_000 } as const;
/** Limite dos valores em centavos (`rating-v1.json`). */
export const maxCentavos = 100_000_000_000;
