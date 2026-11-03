import type {
  CoverageRequest,
  CoverageResponse,
  Money,
  ProductVersionResponse,
  VehicleCategory,
  VehicleUsage,
} from '../../api/generated';
import {
  hasCapitalAmount,
  initialCoverage,
  type CapitalType,
  type CoverageCode,
  type DeductibleType,
} from './coverageRules';
import { homeCountry } from './countries';

export const vehicleCategories: VehicleCategory[] = [
  'LIGHT_PASSENGER',
  'LIGHT_MIXED',
  'HEAVY',
  'MOTORCYCLE',
  'TRAILER',
  'BUS',
];

export const vehicleUsages: VehicleUsage[] = [
  'PRIVATE',
  'RENTAL',
  'PASSENGER_TRANSPORT',
  'GOODS',
  'TAXI',
  'DRIVING_SCHOOL',
];

/** Dados gerais da versão no editor (passo 1). */
export interface GeneralDraft {
  vehicleCategories: VehicleCategory[];
  usages: VehicleUsage[];
  changeNotes: string;
}

/** Uma cobertura no editor (passos 2 e 3). */
export interface CoverageDraft {
  code: CoverageCode;
  mandatory: boolean;
  capitalType: CapitalType;
  capital: Money | null;
  capitalOptions: (Money | null)[];
  requiresCodes: CoverageCode[];
  territorialScope: string[];
  description: string;
  exclusions: string;
  deductibleType: DeductibleType;
  deductibleAmount: Money | null;
  /** Percentagem como escrita (vírgula decimal). */
  deductiblePercent: string;
  deductibleMinAmount: Money | null;
}

export function generalFrom(version: ProductVersionResponse): GeneralDraft {
  return {
    vehicleCategories: [...version.vehicleCategories],
    usages: [...version.usages],
    changeNotes: version.changeNotes ?? '',
  };
}

/** Percentagem para mostrar: `10` → `10`, `12.5` → `12,5`. */
export function percentText(value: number | undefined): string {
  return value === undefined ? '' : String(value).replace('.', ',');
}

const percentPattern = /^\d{1,3}(?:,\d{1,2})?$/;

/** Percentagem escrita → número (0,01–100, até 2 casas), `null` vazio ou `'invalid'`. */
export function parsePercent(text: string): number | null | 'invalid' {
  const value = text.trim();
  if (value === '') return null;
  if (!percentPattern.test(value)) return 'invalid';
  const number = Number(value.replace(',', '.'));
  return number >= 0.01 && number <= 100 ? number : 'invalid';
}

export function coverageFrom(coverage: CoverageResponse): CoverageDraft {
  return {
    code: coverage.code,
    mandatory: coverage.mandatory,
    capitalType: coverage.capitalType,
    capital: coverage.capital ?? null,
    capitalOptions: coverage.capitalOptions ? [...coverage.capitalOptions] : [],
    requiresCodes: [...(coverage.requiresCodes ?? [])],
    territorialScope: coverage.territorialScope?.length
      ? [...coverage.territorialScope]
      : [homeCountry],
    description: coverage.description ?? '',
    exclusions: coverage.exclusions ?? '',
    deductibleType: coverage.deductibleType,
    deductibleAmount: coverage.deductibleAmount ?? null,
    deductiblePercent: percentText(coverage.deductiblePercent),
    deductibleMinAmount: coverage.deductibleMinAmount ?? null,
  };
}

/** Cobertura acrescentada no passo 2, já com os valores que as regras impõem. */
export function newCoverage(code: CoverageCode): CoverageDraft {
  const initial = initialCoverage(code);
  return {
    code,
    mandatory: initial.mandatory,
    capitalType: initial.capitalType,
    capital: null,
    capitalOptions: initial.capitalType === 'OPTIONS' ? [null, null] : [],
    requiresCodes: initial.requiresCodes,
    territorialScope: [homeCountry],
    description: '',
    exclusions: '',
    deductibleType: 'NONE',
    deductibleAmount: null,
    deductiblePercent: '',
    deductibleMinAmount: null,
  };
}

function text(value: string): string | undefined {
  const trimmed = value.trim();
  return trimmed === '' ? undefined : trimmed;
}

/**
 * Cobertura para o `replaceProductVersionCoverages`: só os campos que o tipo de capital e o tipo
 * de franquia pedem (os outros são proibidos pelo servidor).
 */
export function coverageRequest(draft: CoverageDraft): CoverageRequest {
  const request: CoverageRequest = {
    code: draft.code,
    mandatory: draft.mandatory,
    capitalType: draft.capitalType,
    deductibleType: draft.deductibleType,
    requiresCodes: draft.requiresCodes,
    territorialScope: draft.territorialScope,
    description: text(draft.description),
    exclusions: text(draft.exclusions),
  };
  if (hasCapitalAmount(draft.capitalType) && draft.capital) request.capital = draft.capital;
  if (draft.capitalType === 'OPTIONS') {
    request.capitalOptions = draft.capitalOptions.filter((m): m is Money => m !== null);
  }
  if (draft.deductibleType === 'FIXED' && draft.deductibleAmount) {
    request.deductibleAmount = draft.deductibleAmount;
  }
  if (draft.deductibleType === 'PERCENT') {
    const percent = parsePercent(draft.deductiblePercent);
    if (typeof percent === 'number') request.deductiblePercent = percent;
    if (draft.deductibleMinAmount) request.deductibleMinAmount = draft.deductibleMinAmount;
  }
  return request;
}

/** Erros de campo locais e do servidor: `coverages[2].capital` → código. */
export type FieldErrors = Record<string, string>;

/** Primeiro erro cujo `field` é este caminho ou começa por ele (`capitalOptions[0]`). */
export function errorAt(errors: FieldErrors, path: string): string | undefined {
  if (errors[path]) return errors[path];
  const key = Object.keys(errors).find(
    (field) => field.startsWith(`${path}[`) || field.startsWith(`${path}.`),
  );
  return key ? errors[key] : undefined;
}
