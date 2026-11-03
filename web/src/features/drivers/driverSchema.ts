import { z } from 'zod';
import { todayInMaputo } from '../../format/dates';
import { ageInYears, isValidName, normalizeName } from '../customer-profile/profileSchema';

/** As mesmas regras do backend (`DriverRules`, S2-03 §2). Mensagens = códigos `validation.*`. */

export const maxDriverAge = 100;
export const maxLicenceCategories = 12;
const licencePattern = /^[A-Z0-9/-]{5,20}$/;
const datePattern = /^(\d{4})-(\d{2})-(\d{2})$/;

/** Maiúsculas e sem espaços. */
export function normalizeLicenceNumber(value: string): string {
  return value.replace(/\s+/g, '').toUpperCase();
}

function parseDate(value: string): [number, number, number] | null {
  const match = datePattern.exec(value);
  if (!match) return null;
  const [y, m, d] = [Number(match[1]), Number(match[2]), Number(match[3])];
  const date = new Date(Date.UTC(y, m - 1, d));
  if (date.getUTCFullYear() !== y || date.getUTCMonth() !== m - 1 || date.getUTCDate() !== d) {
    return null;
  }
  return [y, m, d];
}

/** Código do erro da data de nascimento (no passado, idade até 100 anos), ou `null`. */
export function driverBirthDateError(
  value: string,
  today: string = todayInMaputo(),
): string | null {
  if (!value) return 'REQUIRED';
  const birth = parseDate(value);
  const now = parseDate(today);
  if (!birth || !now || value >= today || ageInYears(birth, now) > maxDriverAge) {
    return 'INVALID_DATE';
  }
  return null;
}

/** Código do erro da data de emissão da carta (entre o nascimento e hoje), ou `null`. */
export function licenceIssueDateError(
  value: string,
  birthDate: string,
  today: string = todayInMaputo(),
): string | null {
  if (!value) return 'REQUIRED';
  if (!parseDate(value) || value > today) return 'LICENCE_ISSUE_DATE_INVALID';
  if (parseDate(birthDate) && value < birthDate) return 'LICENCE_ISSUE_DATE_INVALID';
  return null;
}

export interface DriverSchemaOptions {
  /** O número da carta é obrigatório (registo novo, ou "Alterar" carregado). */
  licenceRequired: boolean;
  today?: string;
}

export function driverSchema({ licenceRequired, today }: DriverSchemaOptions) {
  return z
    .object({
      isOwner: z.boolean(),
      fullName: z.string(),
      birthDate: z.string(),
      licenceNumber: z.string(),
      licenceCategories: z.array(z.string()),
      licenceIssueDate: z.string(),
    })
    .superRefine((values, ctx) => {
      const issue = (path: string, message: string) =>
        ctx.addIssue({ code: z.ZodIssueCode.custom, path: [path], message });

      if (!normalizeName(values.fullName)) issue('fullName', 'REQUIRED');
      else if (!isValidName(values.fullName)) issue('fullName', 'INVALID_NAME');

      const birth = driverBirthDateError(values.birthDate, today);
      if (birth) issue('birthDate', birth);

      const licence = normalizeLicenceNumber(values.licenceNumber);
      if (!licence) {
        if (licenceRequired) issue('licenceNumber', 'REQUIRED');
      } else if (!licencePattern.test(licence)) {
        issue('licenceNumber', 'INVALID_LICENCE_NUMBER');
      }

      if (values.licenceCategories.length === 0) issue('licenceCategories', 'REQUIRED');
      else if (values.licenceCategories.length > maxLicenceCategories) {
        issue('licenceCategories', 'OUT_OF_RANGE');
      }

      const issued = licenceIssueDateError(
        values.licenceIssueDate,
        birth ? '' : values.birthDate,
        today,
      );
      if (issued) issue('licenceIssueDate', issued);
    });
}

export type DriverFormValues = z.infer<ReturnType<typeof driverSchema>>;

export const driverFields = [
  'isOwner',
  'fullName',
  'birthDate',
  'licenceNumber',
  'licenceCategories',
  'licenceIssueDate',
] as const satisfies readonly (keyof DriverFormValues)[];

export type DriverField = (typeof driverFields)[number];

/** Campo do formulário de um `errors[].field` do servidor (`licenceCategories[1]` → `licenceCategories`). */
export function driverFieldOf(serverField: string): DriverField | undefined {
  const root = serverField.split(/[.[]/)[0] ?? '';
  return (driverFields as readonly string[]).includes(root) ? (root as DriverField) : undefined;
}
