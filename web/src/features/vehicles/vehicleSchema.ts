import type { TFunction } from 'i18next';
import { z } from 'zod';
import type {
  CreateVehicleRequest,
  Money,
  RegistryLookupResponse,
  VehicleResponse,
} from '../../api/generated';
import { currentYearInMaputo } from '../../format/dates';
import { provinces } from '../customer-profile/profileSchema';

/** As mesmas regras do backend (`VehicleRules`, S2-03 §2). Mensagens = códigos `validation.*`. */

export const vehicleCategories = [
  'LIGHT_PASSENGER',
  'LIGHT_MIXED',
  'HEAVY',
  'MOTORCYCLE',
  'TRAILER',
  'BUS',
] as const;
export type VehicleCategory = (typeof vehicleCategories)[number];

export const vehicleUsages = [
  'PRIVATE',
  'RENTAL',
  'PASSENGER_TRANSPORT',
  'GOODS',
  'TAXI',
  'DRIVING_SCHOOL',
] as const;

export { provinces };

export const plateMaxLength = 15;
export const minYear = 1900;
export const maxCommercialValue = 100_000_000_000;
const chassisPattern = /^[A-Z0-9]{5,30}$/;
const integerPattern = /^\d+$/;

/** Maiúsculas, sem espaços nem hífens (como o servidor compara matrículas e chassis). */
export function normalizePlate(value: string): string {
  return value.replace(/[\s-]+/g, '').toUpperCase();
}

export const normalizeChassis = normalizePlate;

/** Categoria sem cilindrada e com 0 lugares. */
export function isTrailer(category: string): boolean {
  return category === 'TRAILER';
}

/** O peso bruto é obrigatório nestas categorias. */
export function requiresWeight(category: string): boolean {
  return category === 'HEAVY' || category === 'TRAILER';
}

/** Limites de lugares por categoria. */
export function seatsRange(category: string): [number, number] {
  if (category === 'TRAILER') return [0, 0];
  if (category === 'MOTORCYCLE') return [1, 3];
  return [1, 100];
}

function toInteger(value: string): number | null {
  const text = value.trim();
  return integerPattern.test(text) ? Number(text) : null;
}

export interface VehicleSchemaOptions {
  /** Ano actual em `Africa/Maputo` (por defeito o de agora). */
  currentYear?: number;
}

export function vehicleSchema({ currentYear }: VehicleSchemaOptions = {}) {
  return z
    .object({
      plate: z.string(),
      chassisNumber: z.string(),
      make: z.string(),
      model: z.string(),
      year: z.string(),
      category: z.string(),
      engineCc: z.string(),
      seats: z.string(),
      grossWeightKg: z.string(),
      usage: z.string(),
      provinceOfCirculation: z.string(),
      commercialValue: z.custom<Money | null>(),
      hasTracker: z.boolean(),
    })
    .superRefine((values, ctx) => {
      const issue = (path: string, message: string) =>
        ctx.addIssue({ code: z.ZodIssueCode.custom, path: [path], message });

      const plate = normalizePlate(values.plate);
      if (!plate) issue('plate', 'REQUIRED');
      else if (plate.length > plateMaxLength) issue('plate', 'INVALID_PLATE');

      const chassis = normalizeChassis(values.chassisNumber);
      if (!chassis) issue('chassisNumber', 'REQUIRED');
      else if (!chassisPattern.test(chassis)) issue('chassisNumber', 'INVALID_CHASSIS');

      for (const field of ['make', 'model'] as const) {
        const text = values[field].trim();
        if (!text) issue(field, 'REQUIRED');
        else if (text.length > 60) issue(field, 'OUT_OF_RANGE');
      }

      const maxYear = (currentYear ?? currentYearInMaputo()) + 1;
      const year = toInteger(values.year);
      if (!values.year.trim()) issue('year', 'REQUIRED');
      else if (year === null || year < minYear || year > maxYear) issue('year', 'INVALID_YEAR');

      const category = values.category;
      if (!(vehicleCategories as readonly string[]).includes(category)) {
        issue('category', 'REQUIRED');
      }

      if (!isTrailer(category)) {
        const engineCc = toInteger(values.engineCc);
        if (!values.engineCc.trim()) issue('engineCc', 'REQUIRED');
        else if (engineCc === null || engineCc < 1 || engineCc > 30_000) {
          issue('engineCc', 'OUT_OF_RANGE');
        }
      }

      if (!isTrailer(category)) {
        const seats = toInteger(values.seats);
        const [min, max] = seatsRange(category);
        if (!values.seats.trim()) issue('seats', 'REQUIRED');
        else if (seats === null || seats < min || seats > max) issue('seats', 'OUT_OF_RANGE');
      }

      const weight = toInteger(values.grossWeightKg);
      if (!values.grossWeightKg.trim()) {
        if (requiresWeight(category)) issue('grossWeightKg', 'REQUIRED');
      } else if (weight === null || weight < 1 || weight > 100_000) {
        issue('grossWeightKg', 'OUT_OF_RANGE');
      }

      if (!(vehicleUsages as readonly string[]).includes(values.usage)) issue('usage', 'REQUIRED');
      if (!(provinces as readonly string[]).includes(values.provinceOfCirculation)) {
        issue('provinceOfCirculation', 'REQUIRED');
      }

      const value = values.commercialValue;
      if (value && (value.amount <= 0 || value.amount > maxCommercialValue)) {
        issue('commercialValue', 'OUT_OF_RANGE');
      }
    });
}

export type VehicleFormValues = z.infer<ReturnType<typeof vehicleSchema>>;

export const vehicleFields = [
  'plate',
  'chassisNumber',
  'make',
  'model',
  'year',
  'category',
  'engineCc',
  'seats',
  'grossWeightKg',
  'usage',
  'provinceOfCirculation',
  'commercialValue',
  'hasTracker',
] as const satisfies readonly (keyof VehicleFormValues)[];

export type VehicleField = (typeof vehicleFields)[number];

/** Campo do formulário de um `errors[].field` do servidor (`commercialValue.amount` → `commercialValue`). */
export function vehicleFieldOf(serverField: string): VehicleField | undefined {
  const root = serverField.split(/[.[]/)[0] ?? '';
  return (vehicleFields as readonly string[]).includes(root) ? (root as VehicleField) : undefined;
}

export const emptyVehicleValues: VehicleFormValues = {
  plate: '',
  chassisNumber: '',
  make: '',
  model: '',
  year: '',
  category: '',
  engineCc: '',
  seats: '',
  grossWeightKg: '',
  usage: '',
  provinceOfCirculation: '',
  commercialValue: null,
  hasTracker: false,
};

const numberText = (value: number | undefined) => (value === undefined ? '' : String(value));

export function vehicleValuesFrom(vehicle: VehicleResponse): VehicleFormValues {
  return {
    plate: vehicle.plate,
    chassisNumber: vehicle.chassisNumber,
    make: vehicle.make,
    model: vehicle.model,
    year: String(vehicle.year),
    category: vehicle.category,
    engineCc: numberText(vehicle.engineCc),
    seats: String(vehicle.seats),
    grossWeightKg: numberText(vehicle.grossWeightKg),
    usage: vehicle.usage,
    provinceOfCirculation: vehicle.provinceOfCirculation,
    commercialValue: vehicle.commercialValue ?? null,
    hasTracker: vehicle.hasTracker,
  };
}

/** Campos que a consulta ao registo preenche (substitui o que lá estiver). */
export function registryValues(data: RegistryLookupResponse): Partial<VehicleFormValues> {
  return {
    chassisNumber: data.chassisNumber,
    make: data.make,
    model: data.model,
    year: String(data.year),
    engineCc: isTrailer(data.category) ? '' : numberText(data.engineCc),
    category: data.category,
    seats: String(data.seats),
    grossWeightKg: numberText(data.grossWeightKg),
  };
}

/** Pedido para a API; os campos que não se aplicam vão ausentes. */
export function toVehicleRequest(values: VehicleFormValues): CreateVehicleRequest {
  const trailer = isTrailer(values.category);
  const weight = values.grossWeightKg.trim();
  return {
    plate: values.plate.trim().toUpperCase(),
    chassisNumber: normalizeChassis(values.chassisNumber),
    make: values.make.trim(),
    model: values.model.trim(),
    year: Number(values.year.trim()),
    category: values.category as CreateVehicleRequest['category'],
    ...(trailer ? {} : { engineCc: Number(values.engineCc.trim()) }),
    seats: trailer ? 0 : Number(values.seats.trim()),
    ...(weight ? { grossWeightKg: Number(weight) } : {}),
    usage: values.usage as CreateVehicleRequest['usage'],
    provinceOfCirculation:
      values.provinceOfCirculation as CreateVehicleRequest['provinceOfCirculation'],
    ...(values.commercialValue ? { commercialValue: values.commercialValue } : {}),
    hasTracker: values.hasTracker,
  };
}

/** Mensagem de um código de campo ou de topo (`validation.<código>`, senão `errors.<código>`). */
export function codeMessage(t: TFunction, code: string): string {
  for (const key of [`validation.${code}`, `errors.${code}`]) {
    if (t(key, { defaultValue: '' })) return t(key);
  }
  return t('validation.TYPE_MISMATCH');
}
