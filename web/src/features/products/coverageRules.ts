import type {
  CoverageRequestCapitalType,
  CoverageRequestCode,
  CoverageRequestDeductibleType,
  ProductResponseType,
} from '../../api/generated';

export type CoverageCode = CoverageRequestCode;
export type CapitalType = CoverageRequestCapitalType;
export type DeductibleType = CoverageRequestDeductibleType;
export type ProductType = ProductResponseType;

export const productTypes: ProductType[] = [
  'MTPL_ONLY',
  'COMPREHENSIVE',
  'TEMPORARY',
  'BORDER',
  'FLEET',
];

/** Códigos de cobertura pela ordem em que aparecem no menu. */
export const coverageCodes: CoverageCode[] = [
  'MTPL',
  'VTPL',
  'OD_COLLISION',
  'OD_GLASS',
  'OD_FIRE',
  'OD_NATURE',
  'OD_THEFT',
  'OCCUPANTS',
  'ASSISTANCE',
  'LEGAL',
  'SRCC',
  'CROSS_BORDER',
];

export const capitalTypes: CapitalType[] = ['FIXED', 'VEHICLE_VALUE', 'OPTIONS', 'PER_PERSON'];
export const deductibleTypes: DeductibleType[] = ['NONE', 'FIXED', 'PERCENT'];

/** Coberturas de danos próprios. */
export function isOwnDamage(code: CoverageCode): boolean {
  return code.startsWith('OD_');
}

/** Coberturas que o tipo de produto admite (regras 4 e 5 da publicação, S2-04 §4). */
export function coveragesForType(type: ProductType): CoverageCode[] {
  switch (type) {
    case 'BORDER':
      return ['MTPL'];
    case 'MTPL_ONLY':
    case 'TEMPORARY':
      return coverageCodes.filter((code) => !isOwnDamage(code));
    default:
      return coverageCodes;
  }
}

/** Menu "Acrescentar cobertura": as que o tipo admite e ainda não estão na lista. */
export function addableCoverages(
  type: ProductType,
  present: readonly CoverageCode[],
): CoverageCode[] {
  return coveragesForType(type).filter((code) => !present.includes(code));
}

/** Tipos de capital que a cobertura admite (regras 3, 7 e 10). */
export function capitalTypesFor(code: CoverageCode): CapitalType[] {
  if (code === 'MTPL') return ['FIXED'];
  if (isOwnDamage(code)) return ['VEHICLE_VALUE'];
  if (code === 'VTPL') return ['FIXED', 'OPTIONS'];
  return capitalTypes;
}

/** Campos fixos pelas regras: o cartão mostra-os desactivados. */
export function lockedFields(code: CoverageCode): { mandatory: boolean; capitalType: boolean } {
  return {
    mandatory: code === 'MTPL',
    capitalType: capitalTypesFor(code).length === 1,
  };
}

/** Coberturas que `OD_THEFT` exige (regra 8). */
export const theftRequires: CoverageCode[] = ['OD_COLLISION', 'OD_FIRE', 'OD_NATURE'];

/** Valores iniciais de uma cobertura acrescentada. */
export function initialCoverage(code: CoverageCode): {
  mandatory: boolean;
  capitalType: CapitalType;
  requiresCodes: CoverageCode[];
} {
  return {
    mandatory: code === 'MTPL',
    capitalType: capitalTypesFor(code)[0] ?? 'FIXED',
    requiresCodes: code === 'OD_THEFT' ? [...theftRequires] : [],
  };
}

/** O capital tem valor (`capital`) neste tipo. */
export function hasCapitalAmount(type: CapitalType): boolean {
  return type === 'FIXED' || type === 'PER_PERSON';
}
