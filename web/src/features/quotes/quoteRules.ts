import { z } from 'zod';
import type { Money } from '../../api/generated';
import { currentYearInMaputo, todayInMaputo } from '../../format/dates';
import { msisdnDigitsPattern } from '../customer-auth/otp';
import { provinces } from '../customer-profile/profileSchema';
import { driverBirthDateError, maxLicenceCategories } from '../drivers/driverSchema';
import {
  maxCommercialValue,
  minYear,
  normalizePlate,
  plateMaxLength,
  requiresWeight,
  seatsRange,
  vehicleCategories,
  vehicleUsages,
} from '../vehicles/vehicleSchema';
import { ONE_YEAR_DAYS, temporaryDurations } from './config';
import { startDateRange } from './period';

/**
 * As regras do pedido de cotação do S3-02 §3 (termos) e §6 (dados escritos pelo mediador), com
 * os mesmos códigos `validation.*`. Quem decide é o backend; aqui só se evita enviar o que ele
 * recusaria.
 */

/** Tipos que se podem cotar no S3 (`BORDER` e `FLEET` → `NOT_ALLOWED`). */
export const quoteProductTypes = ['MTPL_ONLY', 'COMPREHENSIVE', 'TEMPORARY'] as const;
export type QuoteProductType = (typeof quoteProductTypes)[number];

export const coverageCodes = [
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
] as const;
export type CoverageCode = (typeof coverageCodes)[number];

export const THEFT = 'OD_THEFT';
/** "Furto ou roubo" exige estas três. */
export const theftRequires = ['OD_COLLISION', 'OD_FIRE', 'OD_NATURE'] as const;

export function isOwnDamage(code: string): boolean {
  return code.startsWith('OD_');
}

export function isQuoteProductType(value: unknown): value is QuoteProductType {
  return typeof value === 'string' && (quoteProductTypes as readonly string[]).includes(value);
}

/** Durações aceites para o tipo (regra 2). */
export function allowedDurations(productType: string): readonly number[] {
  return productType === 'TEMPORARY' ? temporaryDurations : [ONE_YEAR_DAYS];
}

/** Cobertura pedida: código e, em `OPTIONS`/`PER_PERSON`, o capital escolhido. */
export interface CoverageChoice {
  code: string;
  capital?: Money | null;
}

const issueAdder = (ctx: z.RefinementCtx) => (path: (string | number)[], message: string) =>
  ctx.addIssue({ code: z.ZodIssueCode.custom, path, message });

export interface QuoteTermsOptions {
  /** Hoje em `Africa/Maputo` (por defeito o de agora). */
  today?: string;
}

/** Regras 1 a 5 do S3-02 §3 (tipo, duração, data de início e coberturas). */
export function quoteTermsSchema({ today }: QuoteTermsOptions = {}) {
  return z
    .object({
      productType: z.string(),
      durationDays: z.number().nullable(),
      startDate: z.string(),
      coverages: z.array(z.object({ code: z.string(), capital: z.custom<Money | null>() })),
    })
    .superRefine((values, ctx) => {
      const issue = issueAdder(ctx);
      const type = values.productType;
      const typeValid = isQuoteProductType(type);
      if (!type) issue(['productType'], 'REQUIRED');
      else if (!typeValid) issue(['productType'], 'NOT_ALLOWED');

      if (values.durationDays === null) issue(['durationDays'], 'REQUIRED');
      else if (typeValid && !allowedDurations(type).includes(values.durationDays)) {
        issue(['durationDays'], 'DURATION_NOT_ALLOWED');
      }

      const { min, max } = startDateRange(today ?? todayInMaputo());
      if (!values.startDate) issue(['startDate'], 'REQUIRED');
      else if (
        !/^\d{4}-\d{2}-\d{2}$/.test(values.startDate) ||
        values.startDate < min ||
        values.startDate > max
      ) {
        issue(['startDate'], 'START_DATE_OUT_OF_RANGE');
      }

      const seen = new Set<string>();
      values.coverages.forEach((coverage, index) => {
        if (!(coverageCodes as readonly string[]).includes(coverage.code)) {
          issue(['coverages', index, 'code'], 'INVALID_VALUE');
          return;
        }
        if (seen.has(coverage.code)) {
          issue(['coverages', index, 'code'], 'DUPLICATE_COVERAGE');
          return;
        }
        seen.add(coverage.code);
        if (typeValid && type !== 'COMPREHENSIVE' && isOwnDamage(coverage.code)) {
          issue(['coverages', index, 'code'], 'COVERAGE_NOT_ALLOWED_FOR_TYPE');
        }
        if (coverage.capital && coverage.capital.amount <= 0) {
          issue(['coverages', index, 'capital'], 'OUT_OF_RANGE');
        }
      });
      if (type === 'COMPREHENSIVE' && ![...seen].some(isOwnDamage)) {
        issue(['coverages'], 'OWN_DAMAGE_REQUIRED');
      }
      const theftIndex = values.coverages.findIndex((coverage) => coverage.code === THEFT);
      if (theftIndex >= 0 && !theftRequires.every((code) => seen.has(code))) {
        issue(['coverages', theftIndex, 'code'], 'COVERAGE_REQUIRES');
      }
    });
}

export type QuoteTermsValues = z.infer<ReturnType<typeof quoteTermsSchema>>;

/** Regra 6: danos próprios sem valor comercial do veículo → `VEHICLE_VALUE_REQUIRED`. */
export function vehicleValueMissing(
  coverages: readonly CoverageChoice[],
  commercialValue: Money | null | undefined,
): boolean {
  return coverages.some((coverage) => isOwnDamage(coverage.code)) && !commercialValue;
}

// --- Dados escritos pelo mediador (S3-02 §6) ---

/** Telemóvel do cliente: os 9 dígitos depois do `+258`. */
export const clientSchema = z.object({ clientMsisdn: z.string() }).superRefine((values, ctx) => {
  const issue = issueAdder(ctx);
  if (!values.clientMsisdn) issue(['clientMsisdn'], 'REQUIRED');
  else if (!msisdnDigitsPattern.test(values.clientMsisdn)) {
    issue(['clientMsisdn'], 'INVALID_MSISDN');
  }
});

export type ClientValues = z.infer<typeof clientSchema>;

const integerPattern = /^\d+$/;

function toInteger(value: string): number | null {
  const text = value.trim();
  return integerPattern.test(text) ? Number(text) : null;
}

export interface BrokerVehicleValues {
  plate: string;
  make: string;
  model: string;
  year: string;
  engineCc: string;
  category: string;
  usage: string;
  seats: string;
  grossWeightKg: string;
  provinceOfCirculation: string;
  commercialValue: Money | null;
  hasTracker: boolean;
}

export const emptyBrokerVehicle: BrokerVehicleValues = {
  plate: '',
  make: '',
  model: '',
  year: '',
  engineCc: '',
  category: '',
  usage: '',
  seats: '',
  grossWeightKg: '',
  provinceOfCirculation: '',
  commercialValue: null,
  hasTracker: false,
};

/** Veículo do mediador: matrícula, marca, modelo e cilindrada opcionais (S3-02 §6). */
export function brokerVehicleSchema({ currentYear }: { currentYear?: number } = {}) {
  return z
    .object({
      plate: z.string(),
      make: z.string(),
      model: z.string(),
      year: z.string(),
      engineCc: z.string(),
      category: z.string(),
      usage: z.string(),
      seats: z.string(),
      grossWeightKg: z.string(),
      provinceOfCirculation: z.string(),
      commercialValue: z.custom<Money | null>(),
      hasTracker: z.boolean(),
    })
    .superRefine((values, ctx) => {
      const issue = issueAdder(ctx);
      const plate = normalizePlate(values.plate);
      if (plate && plate.length > plateMaxLength) issue(['plate'], 'INVALID_PLATE');

      for (const field of ['make', 'model'] as const) {
        if (values[field].trim().length > 60) issue([field], 'OUT_OF_RANGE');
      }

      const maxYear = (currentYear ?? currentYearInMaputo()) + 1;
      const year = toInteger(values.year);
      if (!values.year.trim()) issue(['year'], 'REQUIRED');
      else if (year === null || year < minYear || year > maxYear) issue(['year'], 'INVALID_YEAR');

      const category = values.category;
      const categoryValid = (vehicleCategories as readonly string[]).includes(category);
      if (!categoryValid) issue(['category'], 'REQUIRED');
      if (!(vehicleUsages as readonly string[]).includes(values.usage)) {
        issue(['usage'], 'REQUIRED');
      }
      if (!(provinces as readonly string[]).includes(values.provinceOfCirculation)) {
        issue(['provinceOfCirculation'], 'REQUIRED');
      }

      if (values.engineCc.trim()) {
        const engineCc = toInteger(values.engineCc);
        if (category === 'TRAILER') issue(['engineCc'], 'NOT_ALLOWED');
        else if (engineCc === null || engineCc < 1 || engineCc > 30_000) {
          issue(['engineCc'], 'OUT_OF_RANGE');
        }
      }

      const seats = toInteger(values.seats);
      if (!values.seats.trim()) issue(['seats'], 'REQUIRED');
      else if (seats === null) issue(['seats'], 'OUT_OF_RANGE');
      else if (categoryValid) {
        const [min, max] = seatsRange(category);
        if (seats < min || seats > max) issue(['seats'], 'OUT_OF_RANGE');
      }

      const weight = toInteger(values.grossWeightKg);
      if (!values.grossWeightKg.trim()) {
        if (requiresWeight(category)) issue(['grossWeightKg'], 'REQUIRED');
      } else if (weight === null || weight < 1 || weight > 100_000) {
        issue(['grossWeightKg'], 'OUT_OF_RANGE');
      }

      const value = values.commercialValue;
      if (value && (value.amount <= 0 || value.amount > maxCommercialValue)) {
        issue(['commercialValue'], 'OUT_OF_RANGE');
      }
    });
}

export interface BrokerDriverValues {
  birthDate: string;
  licenceIssueDate: string;
  licenceCategories: string[];
}

export const emptyBrokerDriver: BrokerDriverValues = {
  birthDate: '',
  licenceIssueDate: '',
  licenceCategories: [],
};

/** Condutor do mediador: só a data de nascimento é obrigatória (S3-02 §6). */
export function brokerDriverSchema({ today }: { today?: string } = {}) {
  return z
    .object({
      birthDate: z.string(),
      licenceIssueDate: z.string(),
      licenceCategories: z.array(z.string()),
    })
    .superRefine((values, ctx) => {
      const issue = issueAdder(ctx);
      const now = today ?? todayInMaputo();
      const birth = driverBirthDateError(values.birthDate, now);
      if (birth) issue(['birthDate'], birth);

      const issued = values.licenceIssueDate;
      if (issued) {
        const validFormat = /^\d{4}-\d{2}-\d{2}$/.test(issued);
        if (!validFormat || issued > now || (!birth && issued < values.birthDate)) {
          issue(['licenceIssueDate'], 'LICENCE_ISSUE_DATE_INVALID');
        }
      }

      if (values.licenceCategories.length > maxLicenceCategories) {
        issue(['licenceCategories'], 'OUT_OF_RANGE');
      } else if (new Set(values.licenceCategories).size !== values.licenceCategories.length) {
        issue(['licenceCategories'], 'DUPLICATED');
      }
    });
}

/** Primeiro código de erro de cada campo (`{ year: 'INVALID_YEAR' }`), vazio se for válido. */
export function fieldErrors(
  result: z.SafeParseReturnType<unknown, unknown>,
): Record<string, string> {
  if (result.success) return {};
  const errors: Record<string, string> = {};
  for (const issue of result.error.issues) {
    const key = issue.path.join('.');
    if (!(key in errors)) errors[key] = issue.message;
  }
  return errors;
}
